package com.facerecog.impl;

import com.facerecog.service.CameraService;
import com.facerecog.service.FrameListener;
import com.facerecog.service.RecognitionClient;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class CameraServiceImpl implements CameraService {

    private VideoCapture camera;
    private boolean running = false;
    private final RecognitionClient recognitionClient;

    public CameraServiceImpl(RecognitionClient recognitionClient) {
        nu.pattern.OpenCV.loadShared();
        this.recognitionClient = recognitionClient;
    }

    @Override
    public void startCapture(FrameListener listener) {
        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("ERROR: Kamera tidak ditemukan!");
            return;
        }

        running = true;
        System.out.println("Kamera aktif. Tekan Ctrl+C untuk berhenti.");

        new Thread(() -> {
            Mat frame = new Mat();
            while (running) {
                camera.read(frame);
                if (!frame.empty()) {
                    BufferedImage image = matToBufferedImage(frame);
                    if (image != null) {
                        var result = recognitionClient.recognize(image);
                        listener.onFrame(image, result);
                    }
                }
                try { Thread.sleep(500); } catch (InterruptedException e) { break; }
            }
        }).start();
    }

    @Override
    public void stopCapture() {
        running = false;
        if (camera != null) camera.release();
        System.out.println("Kamera dimatikan.");
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        try {
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, buffer);
            return ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
        } catch (Exception e) {
            System.out.println("ERROR konversi frame: " + e.getMessage());
            return null;
        }
    }
}