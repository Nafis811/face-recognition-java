package com.example.facerecognitionsystem.service;

import com.example.facerecognitionsystem.model.RecognitionResult;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MockRecognitionClient implements RecognitionClient {

    private final List<String> mockNames = Arrays.asList(
            "Budi Santoso",
            "Ani Wijaya",
            "Citra Dewi",
            "Doni Pratama"
    );

    private final Random random = new Random();
    private int callCount = 0;

    @Override
    public RecognitionResult recognize(BufferedImage frame) {
        callCount++;

        // Setiap 10 call sekali, return unknown
        if (callCount % 10 == 0) {
            return new RecognitionResult("unknown", null, 0.21);
        }

        // Random pilih nama dari daftar
        String name = mockNames.get(random.nextInt(mockNames.size()));
        double confidence = 0.75 + (random.nextDouble() * 0.20);

        return new RecognitionResult("recognized", name, confidence);
    }
}