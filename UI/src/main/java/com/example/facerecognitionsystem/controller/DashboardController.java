package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.UserSession;
import com.example.facerecognitionsystem.repository.MySQLConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label profileNameLabel;
    @FXML private Label mataKuliahCountLabel;
    @FXML private Label tugasCountLabel;
    @FXML private Label progresLabel;

    @FXML
    public void initialize() {
        if (!UserSession.isLoggedIn()) {
            HelloApplication.navigateTo("main-view.fxml");
            return;
        }
        String name = UserSession.getInstance().getUserName();
        welcomeLabel.setText("Selamat datang, " + name + "!");
        profileNameLabel.setText(name);
        dateLabel.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id"))));
        loadStats();
    }

    private void loadStats() {
        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Hitung mata kuliah
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM mata_kuliah");
            if (rs1.next() && mataKuliahCountLabel != null)
                mataKuliahCountLabel.setText(rs1.getInt(1) + " Aktif");

            // Hitung tugas belum selesai
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM tugas WHERE status = 'belum'");
            if (rs2.next() && tugasCountLabel != null)
                tugasCountLabel.setText(rs2.getInt(1) + " Belum selesai");

            // Hitung progres (tugas selesai / total tugas)
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM tugas");
            if (rs3.next()) {
                int total = rs3.getInt(1);
                ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) FROM tugas WHERE status = 'selesai'");
                if (rs4.next() && progresLabel != null) {
                    int selesai = rs4.getInt(1);
                    int persen = total > 0 ? (selesai * 100 / total) : 0;
                    progresLabel.setText(persen + "%");
                }
            }

        } catch (Exception e) {
            System.out.println("Error load stats: " + e.getMessage());
        }
    }

    @FXML private void handleMataKuliah() { HelloApplication.navigateTo("matakuliah-view.fxml"); }
    @FXML private void handleTugas() { HelloApplication.navigateTo("tugas-view.fxml"); }
    @FXML private void handleJadwal() { HelloApplication.navigateTo("jadwal-view.fxml"); }
    @FXML private void handleLogout() { UserSession.logout(); HelloApplication.navigateTo("main-view.fxml"); }
}