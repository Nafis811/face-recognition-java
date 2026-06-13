package com.facerecog.service;

import com.facerecog.model.RecognitionResult;
import java.awt.image.BufferedImage;

public interface FrameListener {
    void onFrame(BufferedImage frame, RecognitionResult result);
}