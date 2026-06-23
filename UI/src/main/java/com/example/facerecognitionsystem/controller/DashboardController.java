package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label profileNameLabel;

    @FXML
    public void initialize() {
        if (!UserSession.isLoggedIn()) {
            HelloApplication.navigateTo("main-view.fxml");
            return;
        }
        String name = UserSession.getInstance().getUserName();
        welcomeLabel.setText("Selamat datang, " + name + "!");
        profileNameLabel.setText(name);
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
    }

    @FXML
    private void handleLogout() {
        UserSession.logout();
        HelloApplication.navigateTo("main-view.fxml");
    }
}
