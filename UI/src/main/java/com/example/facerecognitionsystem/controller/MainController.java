package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.model.AttendanceLog;
import com.example.facerecognitionsystem.model.RecognitionResult;
import com.example.facerecognitionsystem.repository.SQLiteLogRepository;
import com.example.facerecognitionsystem.service.CameraService;
import com.example.facerecognitionsystem.service.MockCameraService;
import com.example.facerecognitionsystem.service.MockRecognitionClient;
import com.example.facerecognitionsystem.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MainController {

    @FXML private ImageView cameraView;
    @FXML private Label nameLabel;
    @FXML private Label confidenceLabel;
    @FXML private Label timeLabel;
    @FXML private Button startButton;

    private SQLiteLogRepository logRepository;
    private CameraService cameraService;

    private static final String STYLE_BLUE = "-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String STYLE_BLUE_HOVER = "-fx-background-color: #0773c5; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(9,132,227,0.4), 10, 0, 0, 3); -fx-translate-y: -2;";
    private static final String STYLE_GRAY = "-fx-background-color: #636e72; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String STYLE_GRAY_HOVER = "-fx-background-color: #4d5659; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3); -fx-translate-y: -2;";

    @FXML
    public void initialize() {
        logRepository = new SQLiteLogRepository();
        cameraService = new MockCameraService(new MockRecognitionClient());
    }

    public void updateUI(RecognitionResult result) {
        Platform.runLater(() -> {
            if (result.isRecognized()) {
                nameLabel.setText("Terdeteksi: " + result.getPersonName());
                nameLabel.setStyle("-fx-text-fill: #00b894; -fx-font-size: 18px; -fx-font-weight: bold;");
                confidenceLabel.setText(String.format("Confidence: %.0f%%", result.getConfidence() * 100));
            } else {
                nameLabel.setText("Terdeteksi: UNKNOWN");
                nameLabel.setStyle("-fx-text-fill: #d63031; -fx-font-size: 18px; -fx-font-weight: bold;");
                confidenceLabel.setText("Confidence: -");
            }
            timeLabel.setText("Waktu: " + result.getTimestamp()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")));

            AttendanceLog log = new AttendanceLog(UUID.randomUUID().toString(), result);
            logRepository.save(log);
        });
    }

    @FXML
    private void handleStartCamera() {
        if (!cameraService.isRunning()) {
            startButton.setText("⏹ Stop Kamera");
            cameraService.startCapture((frame, result) -> updateUI(result));
        } else {
            startButton.setText("Mulai Kamera");
            cameraService.stopCapture();
        }
    }

    @FXML
    private void handleEnroll() {
        cameraService.stopCapture();
        HelloApplication.navigateTo("enroll-view.fxml");
    }

    @FXML
    private void handleHistory() {
        cameraService.stopCapture();
        HelloApplication.navigateTo("history-view.fxml");
    }

    @FXML
    private void onStartHover(MouseEvent e) {
        ((Button) e.getSource()).setStyle(STYLE_BLUE_HOVER);
    }

    @FXML
    private void onStartExit(MouseEvent e) {
        ((Button) e.getSource()).setStyle(STYLE_BLUE);
    }

    @FXML
    private void onGrayHover(MouseEvent e) {
        ((Button) e.getSource()).setStyle(STYLE_GRAY_HOVER);
    }

    @FXML
    private void onGrayExit(MouseEvent e) {
        ((Button) e.getSource()).setStyle(STYLE_GRAY);
    }
}