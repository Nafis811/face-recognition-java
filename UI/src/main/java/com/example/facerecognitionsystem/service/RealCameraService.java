package com.example.facerecognitionsystem.service;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import com.example.facerecognitionsystem.model.RecognitionResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealCameraService implements CameraService {

    private ScheduledExecutorService executor;
    private boolean running = false;
    private VideoCapture camera;
    private final RecognitionClient recognitionClient;

    public RealCameraService(RecognitionClient recognitionClient) {
        System.load("C:\\Users\\muham\\.m2\\repository\\org\\openpnp\\opencv\\4.9.0-0\\nu\\pattern\\opencv\\windows\\x86_64\\opencv_java490.dll");
        this.recognitionClient = recognitionClient;
    }

    @Override
    public void startCapture(FrameListener listener) {
        if (running) return;

        System.load("C:\\Users\\muham\\.m2\\repository\\org\\openpnp\\opencv\\4.9.0-0\\nu\\pattern\\opencv\\windows\\x86_64\\opencv_java490.dll");

        camera = new VideoCapture(0);

        running = true;
        executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            Mat frame = new Mat();
            camera.read(frame);
            if (!frame.empty()) {
                BufferedImage image = matToBufferedImage(frame);
                if (image != null) {
                    RecognitionResult result = recognitionClient.recognize(image);
                    listener.onFrame(image, result);
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopCapture() {
        running = false;
        if (executor != null) executor.shutdown();
        if (camera != null) camera.release();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        try {
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, buffer);
            return ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
        } catch (Exception e) {
            return null;
        }
    }
}