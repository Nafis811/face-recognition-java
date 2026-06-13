package com.facerecog.service;

public interface CameraService {
    void startCapture(FrameListener listener);
    void stopCapture();
}