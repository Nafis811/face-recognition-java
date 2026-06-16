package com.example.facerecognitionsystem.model;

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

    public String getStatus() { return status; }
    public String getPersonName() { return personName; }
    public double getConfidence() { return confidence; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setStatus(String status) { this.status = status; }
    public void setPersonName(String personName) { this.personName = personName; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isRecognized() {
        return "recognized".equals(this.status);
    }

    @Override
    public String toString() {
        return "RecognitionResult{status='" + status + "', name='" + personName + "', confidence=" + confidence + "}";
    }
}