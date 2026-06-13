package com.facerecog;

import com.facerecog.impl.CameraServiceImpl;
import com.facerecog.impl.RecognitionClientImpl;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Face Recognition Java - Demo ===");

        var client = new RecognitionClientImpl(true); // mock = true
        var camera = new CameraServiceImpl(client);

        camera.startCapture((frame, result) -> {
            System.out.println("Frame diterima → " + result);
        });

        // Jalankan 10 detik
        Thread.sleep(10000);
        camera.stopCapture();
    }
}