package com.example.facerecognitionsystem.service;

import com.example.facerecognitionsystem.model.RecognitionResult;
import java.awt.image.BufferedImage;

public interface FrameListener {
    void onFrame(BufferedImage frame, RecognitionResult result);
}