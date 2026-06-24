package com.example.facerecognitionsystem.controller;

import com.example.facerecognitionsystem.HelloApplication;
import com.example.facerecognitionsystem.model.UserSession;
import com.example.facerecognitionsystem.repository.MySQLConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class JadwalController {

    @FXML private TableView<JadwalRow> jadwalTable;
    @FXML private TableColumn<JadwalRow, String> colHari;
    @FXML private TableColumn<JadwalRow, String> colMk;
    @FXML private TableColumn<JadwalRow, String> colJam;
    @FXML private TableColumn<JadwalRow, String> colRuang;
    @FXML private TableColumn<JadwalRow, String> colDosen;

    @FXML
    public void initialize() {
        if (!UserSession.isLoggedIn()) {
            HelloApplication.navigateTo("main-view.fxml");
            return;
        }
        setupTable();
        loadData();
    }

    private void setupTable() {
        colHari.setCellValueFactory(new PropertyValueFactory<>("hari"));
        colMk.setCellValueFactory(new PropertyValueFactory<>("namaMk"));
        colJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
        colRuang.setCellValueFactory(new PropertyValueFactory<>("ruangan"));
        colDosen.setCellValueFactory(new PropertyValueFactory<>("dosen"));
    }

    private void loadData() {
        ObservableList<JadwalRow> data = FXCollections.observableArrayList();
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
                String jam = rs.getString("jam_mulai") + " - " + rs.getString("jam_selesai");
                data.add(new JadwalRow(
                        rs.getString("hari"),
                        rs.getString("nama_mk"),
                        jam,
                        rs.getString("ruangan"),
                        rs.getString("dosen")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error load jadwal: " + e.getMessage());
        }
        jadwalTable.setItems(data);
    }

    @FXML private void handleDashboard() { HelloApplication.navigateTo("dashboard-view.fxml"); }
    @FXML private void handleMataKuliah() { HelloApplication.navigateTo("matakuliah-view.fxml"); }
    @FXML private void handleTugas() { HelloApplication.navigateTo("tugas-view.fxml"); }
    @FXML private void handleLogout() { UserSession.logout(); HelloApplication.navigateTo("main-view.fxml"); }

    public static class JadwalRow {
        private final String hari;
        private final String namaMk;
        private final String jam;
        private final String ruangan;
        private final String dosen;

        public JadwalRow(String hari, String namaMk, String jam, String ruangan, String dosen) {
            this.hari = hari;
            this.namaMk = namaMk;
            this.jam = jam;
            this.ruangan = ruangan;
            this.dosen = dosen;
        }

        public String getHari() { return hari; }
        public String getNamaMk() { return namaMk; }
        public String getJam() { return jam; }
        public String getRuangan() { return ruangan; }
        public String getDosen() { return dosen; }
    }
}