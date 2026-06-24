package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.UserSession;
import com.example.facerecognitionsystem.repository.MySQLConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MataKuliahController {

    @FXML private VBox mataKuliahContainer;

    @FXML
    public void initialize() {
        if (!UserSession.isLoggedIn()) {
            HelloApplication.navigateTo("main-view.fxml");
            return;
        }
        loadData();
    }

    private void loadData() {
        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM mata_kuliah")) {

            while (rs.next()) {
                String kodeMk = rs.getString("kode_mk");
                String namaMk = rs.getString("nama_mk");
                int sks = rs.getInt("sks");
                String dosen = rs.getString("dosen");

                VBox card = new VBox(10);
                card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

                HBox header = new HBox();
                Label namaLabel = new Label(namaMk);
                namaLabel.setStyle("-fx-text-fill: #1e3a8a; -fx-font-size: 16px; -fx-font-weight: bold;");
                HBox.setHgrow(namaLabel, Priority.ALWAYS);

                Label sksLabel = new Label(sks + " SKS");
                sksLabel.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8; -fx-padding: 4 10; -fx-background-radius: 20; -fx-font-size: 12px;");

                header.getChildren().addAll(namaLabel, sksLabel);

                Label kodeLabel = new Label("Kode: " + kodeMk);
                kodeLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

                Label dosenLabel = new Label("👨‍🏫 " + dosen);
                dosenLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

                ProgressBar progressBar = new ProgressBar(Math.random() * 0.4 + 0.6);
                progressBar.setMaxWidth(Double.MAX_VALUE);
                progressBar.setStyle("-fx-accent: #2563eb;");

                card.getChildren().addAll(header, kodeLabel, dosenLabel, progressBar);
                mataKuliahContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML private void handleDashboard() { HelloApplication.navigateTo("dashboard-view.fxml"); }
    @FXML private void handleTugas() { HelloApplication.navigateTo("tugas-view.fxml"); }
    @FXML private void handleJadwal() { HelloApplication.navigateTo("jadwal-view.fxml"); }
    @FXML private void handleLogout() { UserSession.logout(); HelloApplication.navigateTo("main-view.fxml"); }
}