package com.example.facerecognitionsystem.service;

import com.example.facerecognitionsystem.model.RecognitionResult;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MockCameraService implements CameraService {

    private ScheduledExecutorService executor;
    private boolean running = false;
    private final RecognitionClient recognitionClient;

    public MockCameraService(RecognitionClient recognitionClient) {
        this.recognitionClient = recognitionClient;
    }

    @Override
    public void startCapture(FrameListener listener) {
        if (running) return;

        running = true;
        executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            BufferedImage mockFrame = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
            RecognitionResult result = recognitionClient.recognize(mockFrame);
            listener.onFrame(mockFrame, result);
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopCapture() {
        running = false;
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}