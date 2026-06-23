package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.AttendanceLog;
import com.example.facerecognitionsystem.model.RecognitionResult;
import com.example.facerecognitionsystem.model.UserSession;
import com.example.facerecognitionsystem.repository.SQLiteLogRepository;
import com.example.facerecognitionsystem.service.CameraService;
import com.example.facerecognitionsystem.service.RealCameraService;
import com.example.facerecognitionsystem.service.RealRecognitionClient;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

/** Controller halaman autentikasi wajah LMS. */
public class MainController {
    @FXML private ImageView cameraView;
    @FXML private Label nameLabel;
    @FXML private Label confidenceLabel;
    @FXML private Label serverStatusLabel;
    @FXML private Button startButton;
    @FXML private VBox cameraPlaceholder;
    @FXML private Circle serverStatusDot;

    private SQLiteLogRepository logRepository;
    private CameraService cameraService;
    private boolean loginInProgress;
    private static final double LOGIN_CONFIDENCE = 0.60;

    @FXML
    public void initialize() {
        UserSession.logout();
        logRepository = new SQLiteLogRepository();
        cameraService = new RealCameraService(new RealRecognitionClient());
        Rectangle cameraClip = new Rectangle(680, 360);
        cameraClip.setArcWidth(26);
        cameraClip.setArcHeight(26);
        cameraView.setClip(cameraClip);
        checkRecognitionServer();
    }

    private void checkRecognitionServer() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/docs"))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .whenComplete((response, error) -> Platform.runLater(() -> {
                    boolean online = error == null && response.statusCode() < 500;
                    serverStatusLabel.setText(online ? "Server siap" : "Server belum aktif");
                    serverStatusLabel.setStyle(online
                            ? "-fx-text-fill: #15803d;"
                            : "-fx-text-fill: #dc2626;");
                    serverStatusDot.setStyle(online ? "-fx-fill: #22c55e;" : "-fx-fill: #ef4444;");
                }));
    }

    private void updateUI(BufferedImage frame, RecognitionResult result) {
        Platform.runLater(() -> {
            if (frame != null) {
                WritableImage image = SwingFXUtils.toFXImage(frame, null);
                cameraView.setImage(image);
                applyCenteredCameraCrop(image);
                cameraPlaceholder.setVisible(false);
            }

            boolean validLogin = result.isRecognized()
                    && result.getPersonName() != null
                    && result.getConfidence() >= LOGIN_CONFIDENCE;

            if (validLogin) {
                nameLabel.setText("Wajah dikenali: " + result.getPersonName());
                nameLabel.setStyle("-fx-text-fill: #16a34a;");
                confidenceLabel.setText(String.format("Kecocokan %.0f%% - membuka dashboard...", result.getConfidence() * 100));
            } else {
                nameLabel.setText("Wajah belum dikenali");
                nameLabel.setStyle("-fx-text-fill: #dc2626;");
                confidenceLabel.setText("Pastikan wajah terlihat jelas dan menghadap kamera");
            }

            if (validLogin && !loginInProgress) {
                loginInProgress = true;
                logRepository.save(new AttendanceLog(UUID.randomUUID().toString(), result));
                cameraService.stopCapture();
                UserSession.login(result.getPersonName());
                HelloApplication.navigateTo("dashboard-view.fxml");
            }
        });
    }

    /** Memotong frame dari tengah agar memenuhi bidang 680x360 tanpa distorsi. */
    private void applyCenteredCameraCrop(WritableImage image) {
        double targetRatio = 680.0 / 360.0;
        double imageRatio = image.getWidth() / image.getHeight();
        double x = 0;
        double y = 0;
        double width = image.getWidth();
        double height = image.getHeight();

        if (imageRatio > targetRatio) {
            width = height * targetRatio;
            x = (image.getWidth() - width) / 2.0;
        } else {
            height = width / targetRatio;
            y = (image.getHeight() - height) / 2.0;
        }
        cameraView.setViewport(new Rectangle2D(x, y, width, height));
    }

    @FXML
    private void handleStartCamera() {
        if (!cameraService.isRunning()) {
            checkRecognitionServer();
            startButton.setText("Hentikan pemindaian");
            nameLabel.setText("Mencari wajah...");
            confidenceLabel.setText("Tetap menghadap kamera selama proses pemindaian");
            cameraService.startCapture(this::updateUI);
        } else {
            startButton.setText("Mulai pemindaian wajah");
            cameraService.stopCapture();
            cameraView.setImage(null);
            cameraView.setViewport(null);
            cameraPlaceholder.setVisible(true);
            nameLabel.setText("Pemindaian dihentikan");
            confidenceLabel.setText("Tekan tombol mulai untuk mencoba kembali");
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
}
