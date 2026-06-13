package com.facerecog.model;

import java.time.LocalDateTime;

public class RecognitionResult {
    private String status;
    private String personName;
    private double confidence;
    private LocalDateTime timestamp;

    public RecognitionResult(String status, String personName, double confidence) {
        this.status = status;
        this.personName = personName;
        this.confidence = confidence;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus()           { return status; }
    public String getPersonName()       { return personName; }
    public double getConfidence()       { return confidence; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s (confidence: %.0f%%) @ %s",
                status, personName, confidence * 100, timestamp);
    }
}