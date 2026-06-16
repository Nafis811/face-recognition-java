package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.Person;
import com.example.facerecognitionsystem.repository.SQLitePersonRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class EnrollController {

    @FXML private ImageView enrollCameraView;
    @FXML private TextField nameField;
    @FXML private Label statusLabel;
    @FXML private Button captureButton;
    @FXML private Button enrollButton;

    private SQLitePersonRepository personRepository;

    private static final String STYLE_BLUE = "-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String STYLE_BLUE_HOVER = "-fx-background-color: #0773c5; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(9,132,227,0.4), 10, 0, 0, 3); -fx-translate-y: -2;";
    private static final String STYLE_GREEN = "-fx-background-color: #00b894; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-pref-width: 220;";
    private static final String STYLE_GREEN_HOVER = "-fx-background-color: #00a381; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-pref-width: 220; -fx-effect: dropshadow(gaussian, rgba(0,184,148,0.4), 10, 0, 0, 3); -fx-translate-y: -2;";

    @FXML
    public void initialize() {
        personRepository = new SQLitePersonRepository();
    }

    @FXML
    private void handleCapture() {
        statusLabel.setText("📸 Foto berhasil diambil!");
        statusLabel.setStyle("-fx-text-fill: #00b894;");
    }

    @FXML
    private void handleEnroll() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("⚠ Nama tidak boleh kosong!");
            statusLabel.setStyle("-fx-text-fill: #d63031;");
            return;
        }
        Person person = new Person(UUID.randomUUID().toString(), name, LocalDateTime.now());
        personRepository.save(person);
        statusLabel.setText("✅ " + name + " berhasil didaftarkan!");
        statusLabel.setStyle("-fx-text-fill: #00b894;");
        nameField.clear();
    }

    @FXML
    private void handleBack() {
        HelloApplication.navigateTo("main-view.fxml");
    }

    @FXML private void onCaptureHover(MouseEvent e) { ((Button) e.getSource()).setStyle(STYLE_BLUE_HOVER); }
    @FXML private void onCaptureExit(MouseEvent e) { ((Button) e.getSource()).setStyle(STYLE_BLUE); }
    @FXML private void onEnrollHover(MouseEvent e) { ((Button) e.getSource()).setStyle(STYLE_GREEN_HOVER); }
    @FXML private void onEnrollExit(MouseEvent e) { ((Button) e.getSource()).setStyle(STYLE_GREEN); }
}