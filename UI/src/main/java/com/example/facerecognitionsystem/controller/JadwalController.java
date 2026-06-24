package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.UserSession;
import com.example.facerecognitionsystem.repository.MySQLConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class JadwalController {

    @FXML private VBox jadwalContainer;

    // Warna per hari
    private String getBadgeColor(String hari) {
        return switch (hari) {
            case "Senin"  -> "#dbeafe;-fx-text-fill:#1d4ed8";
            case "Selasa" -> "#dcfce7;-fx-text-fill:#15803d";
            case "Rabu"   -> "#fef9c3;-fx-text-fill:#a16207";
            case "Kamis"  -> "#fce7f3;-fx-text-fill:#be185d";
            case "Jumat"  -> "#ede9fe;-fx-text-fill:#6d28d9";
            default       -> "#f1f5f9;-fx-text-fill:#475569";
        };
    }

    @FXML
    public void initialize() {
        if (!UserSession.isLoggedIn()) {
            HelloApplication.navigateTo("main-view.fxml");
            return;
        }
        loadData();
    }

    private void loadData() {
        jadwalContainer.getChildren().clear();

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT j.hari, j.jam_mulai, j.jam_selesai, j.ruangan,
                       mk.nama_mk, mk.dosen
                FROM jadwal j
                JOIN mata_kuliah mk ON j.kode_mk = mk.kode_mk
                ORDER BY FIELD(j.hari, 'Senin','Selasa','Rabu','Kamis','Jumat')
             """)) {

            while (rs.next()) {
                String hari    = rs.getString("hari");
                String namaMk  = rs.getString("nama_mk");
                String jam     = rs.getString("jam_mulai") + " - " + rs.getString("jam_selesai");
                String ruangan = rs.getString("ruangan");
                String dosen   = rs.getString("dosen");

                // Card utama
                HBox card = new HBox(16);
                card.setStyle("-fx-background-color: white; -fx-padding: 18 20; -fx-background-radius: 12; -fx-alignment: CENTER_LEFT; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

                // Badge hari
                String[] colorParts = getBadgeColor(hari).split(";-fx-text-fill:");
                Label hariBadge = new Label(hari);
                hariBadge.setMinWidth(70);
                hariBadge.setStyle("-fx-background-color: #" + colorParts[0].replace("#","") +
                        "; -fx-text-fill: #" + colorParts[1].replace("#","") +
                        "; -fx-padding: 6 12; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 12px;");

                // Info tengah
                VBox info = new VBox(4);
                HBox.setHgrow(info, Priority.ALWAYS);

                Label mkLabel = new Label(namaMk);
                mkLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

                Label dosenLabel = new Label("👤 " + dosen);
                dosenLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

                info.getChildren().addAll(mkLabel, dosenLabel);

                // Info kanan
                VBox rightInfo = new VBox(4);
                rightInfo.setStyle("-fx-alignment: CENTER_RIGHT;");

                Label jamLabel = new Label("🕐 " + jam);
                jamLabel.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold; -fx-font-size: 13px;");

                Label ruangLabel = new Label("📍 " + ruangan);
                ruangLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

                rightInfo.getChildren().addAll(jamLabel, ruangLabel);

                card.getChildren().addAll(hariBadge, info, rightInfo);
                jadwalContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            System.out.println("Error load jadwal: " + e.getMessage());
        }
    }

    @FXML private void handleDashboard()  { HelloApplication.navigateTo("dashboard-view.fxml"); }
    @FXML private void handleMataKuliah() { HelloApplication.navigateTo("matakuliah-view.fxml"); }
    @FXML private void handleTugas()      { HelloApplication.navigateTo("tugas-view.fxml"); }
    @FXML private void handleLogout()     { UserSession.logout(); HelloApplication.navigateTo("main-view.fxml"); }
}