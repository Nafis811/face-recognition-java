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

    // Tambahkan ini — pastikan fx:id="tugasMendatangContainer" ada di dashboard-view.fxml
    @FXML private VBox tugasMendatangContainer;

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
        loadTugasMendatang(); // ← tambahkan ini
    }

    private void loadStats() {
        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM mata_kuliah");
            if (rs1.next() && mataKuliahCountLabel != null)
                mataKuliahCountLabel.setText(rs1.getInt(1) + " Aktif");

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM tugas WHERE status = 'belum'");
            if (rs2.next() && tugasCountLabel != null)
                tugasCountLabel.setText(rs2.getInt(1) + " Belum selesai");

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

    // ↓ Method baru — ambil tugas dari DB, sama persis sumber datanya dengan TugasController
    private void loadTugasMendatang() {
        if (tugasMendatangContainer == null) return;
        tugasMendatangContainer.getChildren().clear();

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT t.judul, t.deadline, mk.nama_mk
                FROM tugas t
                JOIN mata_kuliah mk ON t.kode_mk = mk.kode_mk
                WHERE t.status = 'belum'
                ORDER BY t.deadline ASC
             """)) {

            boolean adaTugas = false;

            while (rs.next()) {
                adaTugas = true;
                String judul = rs.getString("judul");
                String deadline = rs.getString("deadline");
                String namaMk = rs.getString("nama_mk");

                HBox row = new HBox(12);
                row.getStyleClass().add("feed-item");
                row.setStyle("-fx-alignment: CENTER_LEFT;");

                VBox info = new VBox(2);
                HBox.setHgrow(info, Priority.ALWAYS);

                Label judulLabel = new Label(judul);
                judulLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

                Label mkLabel = new Label(namaMk);
                mkLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

                info.getChildren().addAll(judulLabel, mkLabel);

                Label deadlineLabel = new Label(deadline);
                deadlineLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 12px;");

                row.getChildren().addAll(info, deadlineLabel);
                tugasMendatangContainer.getChildren().add(row);
            }

            // Kalau semua tugas sudah selesai
            if (!adaTugas) {
                Label kosong = new Label("🎉 Semua tugas sudah selesai!");
                kosong.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 13px; -fx-padding: 12;");
                tugasMendatangContainer.getChildren().add(kosong);
            }

        } catch (Exception e) {
            System.out.println("Error load tugas mendatang: " + e.getMessage());
        }
    }

    @FXML private void handleMataKuliah() { HelloApplication.navigateTo("matakuliah-view.fxml"); }
    @FXML private void handleTugas() { HelloApplication.navigateTo("tugas-view.fxml"); }
    @FXML private void handleJadwal() { HelloApplication.navigateTo("jadwal-view.fxml"); }
    @FXML private void handleLogout() { UserSession.logout(); HelloApplication.navigateTo("main-view.fxml"); }
}