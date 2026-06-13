package com.facerecog.service;

import com.facerecog.model.RecognitionResult;
import java.awt.image.BufferedImage;

public interface RecognitionClient {
    RecognitionResult recognize(BufferedImage frame);
}