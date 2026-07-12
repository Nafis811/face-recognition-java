package com.example.facerecognitionsystem.service;

import com.example.facerecognitionsystem.model.RecognitionResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Menjalankan preview dan face recognition pada thread terpisah.
 * Preview dibuat lancar, sedangkan request recognition dibatasi agar server
 * Python tidak menerima request yang bertumpuk.
 */
public class RealCameraService implements CameraService {

    private static final long PREVIEW_INTERVAL_MS = 40;      // sekitar 25 FPS
    private static final long RECOGNITION_INTERVAL_MS = 900; // sekitar 1x per detik

    private ScheduledExecutorService previewExecutor;
    private ScheduledExecutorService recognitionExecutor;
    private volatile boolean running;
    private VideoCapture camera;
    private final RecognitionClient recognitionClient;

    private final AtomicReference<BufferedImage> latestFrame = new AtomicReference<>();
    private final AtomicReference<RecognitionResult> latestResult = new AtomicReference<>(
            new RecognitionResult("unknown", "Unknown", 0.0));

    public RealCameraService(RecognitionClient recognitionClient) {
        nu.pattern.OpenCV.loadShared();
        this.recognitionClient = recognitionClient;
    }

    @Override
    public synchronized void startCapture(FrameListener listener) {
        if (running) return;

        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            camera.release();
            camera = null;
            return;
        }

        running = true;
        latestFrame.set(null);
        latestResult.set(new RecognitionResult("unknown", "Unknown", 0.0));

        previewExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "camera-preview");
            thread.setDaemon(true);
            return thread;
        });

        recognitionExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "face-recognition");
            thread.setDaemon(true);
            return thread;
        });

        previewExecutor.scheduleAtFixedRate(() -> capturePreview(listener),
                0, PREVIEW_INTERVAL_MS, TimeUnit.MILLISECONDS);

        recognitionExecutor.scheduleWithFixedDelay(this::recognizeLatestFrame,
                250, RECOGNITION_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void capturePreview(FrameListener listener) {
        if (!running || camera == null) return;

        Mat frame = new Mat();
        try {
            if (camera.read(frame) && !frame.empty()) {
                BufferedImage image = matToBufferedImage(frame);
                if (image != null) {
                    latestFrame.set(image);
                    listener.onFrame(image, latestResult.get());
                }
            }
        } finally {
            frame.release();
        }
    }

    private void recognizeLatestFrame() {
        if (!running) return;
        BufferedImage frame = latestFrame.get();
        if (frame != null) {
            RecognitionResult result = recognitionClient.recognize(frame);
            if (result != null) latestResult.set(result);
        }
    }

    @Override
    public synchronized void stopCapture() {
        running = false;

        if (previewExecutor != null) {
            previewExecutor.shutdownNow();
            previewExecutor = null;
        }
        if (recognitionExecutor != null) {
            recognitionExecutor.shutdownNow();
            recognitionExecutor = null;
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }

        latestFrame.set(null);
        latestResult.set(new RecognitionResult("unknown", "Unknown", 0.0));
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        try {
            MatOfByte buffer = new MatOfByte();
            try {
                Imgcodecs.imencode(".jpg", mat, buffer);
                return ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
            } finally {
                buffer.release();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
