package com.example.facerecognitionsystem.model;

import java.time.LocalDateTime;

public class AttendanceLog {
    private String logId;
    private RecognitionResult result;
    private LocalDateTime loggedAt;

    public AttendanceLog(String logId, RecognitionResult result) {
        this.logId = logId;
        this.result = result;
        this.loggedAt = LocalDateTime.now();
    }

    public String getLogId() { return logId; }
    public RecognitionResult getResult() { return result; }
    public LocalDateTime getLoggedAt() { return loggedAt; }

    public void setLogId(String logId) { this.logId = logId; }
    public void setResult(RecognitionResult result) { this.result = result; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }

    @Override
    public String toString() {
        return "AttendanceLog{logId='" + logId + "', loggedAt=" + loggedAt + "}";
    }
}