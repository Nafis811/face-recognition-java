package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.UserSession;
import com.example.facerecognitionsystem.repository.MySQLConnection;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TugasController {

    @FXML private VBox tugasContainer;

    @FXML
    public void initialize() {
        if (!UserSession.isLoggedIn()) {
            HelloApplication.navigateTo("main-view.fxml");
            return;
        }
        loadData();
    }

    private void loadData() {
        tugasContainer.getChildren().clear();
        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT t.id, t.judul, t.deadline, t.status, t.deskripsi, mk.nama_mk
                FROM tugas t
                JOIN mata_kuliah mk ON t.kode_mk = mk.kode_mk
                ORDER BY t.deadline ASC
             """)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String judul = rs.getString("judul");
                String deadline = rs.getString("deadline");
                String status = rs.getString("status");
                String namaMk = rs.getString("nama_mk");
                String deskripsi = rs.getString("deskripsi");
                boolean selesai = "selesai".equals(status);

                // === CARD WRAPPER ===
                VBox cardWrapper = new VBox(0);
                cardWrapper.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

                // === HEADER ROW ===
                HBox card = new HBox(16);
                card.setStyle("-fx-padding: 18 20; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");

                Label statusDot = new Label(selesai ? "✓" : "!");
                statusDot.setStyle(selesai
                        ? "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; -fx-padding: 6 10; -fx-background-radius: 20; -fx-font-weight: bold;"
                        : "-fx-background-color: #fef9c3; -fx-text-fill: #ca8a04; -fx-padding: 6 10; -fx-background-radius: 20; -fx-font-weight: bold;");

                VBox info = new VBox(4);
                HBox.setHgrow(info, Priority.ALWAYS);

                Label judulLabel = new Label(judul);
                judulLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

                Label mkLabel = new Label(namaMk);
                mkLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

                info.getChildren().addAll(judulLabel, mkLabel);

                VBox rightInfo = new VBox(6);
                rightInfo.setStyle("-fx-alignment: CENTER_RIGHT;");

                Label deadlineLabel = new Label("📅 " + deadline);
                deadlineLabel.setStyle(selesai ? "-fx-text-fill: #64748b;" : "-fx-text-fill: #dc2626; -fx-font-weight: bold;");

                Label statusLabel = new Label(selesai ? "Selesai" : "Belum selesai");
                statusLabel.setStyle(selesai
                        ? "-fx-text-fill: #16a34a; -fx-font-size: 12px;"
                        : "-fx-text-fill: #dc2626; -fx-font-size: 12px;");

                Label arrowLabel = new Label("▼");
                arrowLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

                rightInfo.getChildren().addAll(deadlineLabel, statusLabel);

                if (!selesai) {
                    Button tandaiBtn = new Button("✓ Tandai Selesai");
                    tandaiBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand;");
                    tandaiBtn.setOnAction(e -> tandaiSelesai(id));
                    rightInfo.getChildren().add(tandaiBtn);
                }

                card.getChildren().addAll(statusDot, info, rightInfo, arrowLabel);

                // === DESKRIPSI PANEL (hidden by default) ===
                VBox deskripsiPanel = new VBox(8);
                deskripsiPanel.setStyle("-fx-background-color: #f8fafc; -fx-padding: 14 20 18 20; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");
                deskripsiPanel.setVisible(false);
                deskripsiPanel.setManaged(false);

                Label deskripsiTitle = new Label("📋 Instruksi Tugas");
                deskripsiTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155; -fx-font-size: 13px;");

                Label deskripsiText = new Label(
                        (deskripsi != null && !deskripsi.isEmpty()) ? deskripsi : "Tidak ada instruksi tersedia."
                );
                deskripsiText.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px; -fx-wrap-text: true;");
                deskripsiText.setWrapText(true);

                deskripsiPanel.getChildren().addAll(deskripsiTitle, deskripsiText);

                // === TOGGLE EXPAND on click ===
                card.setOnMouseClicked(e -> {
                    boolean expanded = deskripsiPanel.isVisible();
                    deskripsiPanel.setVisible(!expanded);
                    deskripsiPanel.setManaged(!expanded);
                    arrowLabel.setText(expanded ? "▼" : "▲");

                    FadeTransition ft = new FadeTransition(Duration.millis(150), deskripsiPanel);
                    ft.setFromValue(expanded ? 1 : 0);
                    ft.setToValue(expanded ? 0 : 1);
                    ft.play();
                });

                cardWrapper.getChildren().addAll(card, deskripsiPanel);
                tugasContainer.getChildren().add(cardWrapper);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void tandaiSelesai(int id) {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE tugas SET status = 'selesai' WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadData();
        } catch (Exception e) {
            System.out.println("Error update tugas: " + e.getMessage());
        }
    }

    @FXML private void handleDashboard() { HelloApplication.navigateTo("dashboard-view.fxml"); }
    @FXML private void handleMataKuliah() { HelloApplication.navigateTo("matakuliah-view.fxml"); }
    @FXML private void handleJadwal() { HelloApplication.navigateTo("jadwal-view.fxml"); }
    @FXML private void handleLogout() { UserSession.logout(); HelloApplication.navigateTo("main-view.fxml"); }
}