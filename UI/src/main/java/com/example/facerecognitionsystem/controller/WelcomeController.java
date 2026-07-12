package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.UserSession;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class WelcomeController {

    @FXML private StackPane avatarContainer;
    @FXML private Label nameLabel;
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label loadingLabel;

    @FXML
    public void initialize() {
        // Set nama user dari sesi aktif
        if (UserSession.isLoggedIn()) {
            nameLabel.setText(UserSession.getInstance().getUserName());
        } else {
            nameLabel.setText("Mahasiswa NUSA");
        }

        // Jalankan seluruh rangkaian animasi transisi
        playTransitions();
    }

    private void playTransitions() {
        // 1. Animasi Avatar (Scale + Fade In)
        avatarContainer.setScaleX(0.4);
        avatarContainer.setScaleY(0.4);
        avatarContainer.setOpacity(0.0);

        ScaleTransition avatarScale = new ScaleTransition(Duration.millis(800), avatarContainer);
        avatarScale.setToX(1.0);
        avatarScale.setToY(1.0);
        avatarScale.setInterpolator(Interpolator.SPLINE(0.1, 0.8, 0.3, 1.0)); // Custom cubic bezier easing

        FadeTransition avatarFade = new FadeTransition(Duration.millis(600), avatarContainer);
        avatarFade.setToValue(1.0);

        // ParallelTransition untuk Avatar
        ParallelTransition avatarAnimation = new ParallelTransition(avatarContainer, avatarScale, avatarFade);

        // 2. Animasi Welcome Text (Translate Up + Fade In)
        nameLabel.setOpacity(0.0);
        nameLabel.setTranslateY(15);
        statusLabel.setOpacity(0.0);
        statusLabel.setTranslateY(10);

        FadeTransition nameFade = new FadeTransition(Duration.millis(600), nameLabel);
        nameFade.setToValue(1.0);
        TranslateTransition nameTranslate = new TranslateTransition(Duration.millis(700), nameLabel);
        nameTranslate.setToY(0);
        nameTranslate.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition statusFade = new FadeTransition(Duration.millis(500), statusLabel);
        statusFade.setToValue(1.0);
        TranslateTransition statusTranslate = new TranslateTransition(Duration.millis(600), statusLabel);
        statusTranslate.setToY(0);
        statusTranslate.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition textAnimation = new ParallelTransition(nameFade, nameTranslate, statusFade, statusTranslate);
        textAnimation.setDelay(Duration.millis(200)); // Delay sedikit agar avatar muncul duluan

        // 3. Animasi Progress Bar pengisian loading
        Timeline progressTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0.0)),
                new KeyFrame(Duration.seconds(2.0), new KeyValue(progressBar.progressProperty(), 1.0))
        );
        progressTimeline.setDelay(Duration.millis(400));

        // Transisi teks loading bertahap agar estetik
        Timeline textLoadingTimeline = new Timeline(
                new KeyFrame(Duration.millis(700), e -> loadingLabel.setText("Menyiapkan dashboard akademik Anda...")),
                new KeyFrame(Duration.millis(1400), e -> loadingLabel.setText("Memuat jadwal dan daftar tugas...")),
                new KeyFrame(Duration.seconds(2.0), e -> loadingLabel.setText("Membuka ruang kelas..."))
        );
        textLoadingTimeline.setDelay(Duration.millis(400));

        // 4. Pengalihan halaman ke dashboard setelah loading selesai
        Timeline redirectTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2.5), e -> {
                    // Pindah ke dashboard-view
                    Platform.runLater(() -> HelloApplication.navigateTo("dashboard-view.fxml"));
                })
        );

        // Jalankan semua animasi
        avatarAnimation.play();
        textAnimation.play();
        progressTimeline.play();
        textLoadingTimeline.play();
        redirectTimeline.play();
    }
}
