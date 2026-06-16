package com.example.facerecognitionsystem.service;

public interface CameraService {
    void startCapture(FrameListener listener);
    void stopCapture();
    boolean isRunning();
}