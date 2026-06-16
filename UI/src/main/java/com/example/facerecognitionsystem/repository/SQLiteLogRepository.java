package com.example.facerecognitionsystem.repository;

import com.example.facerecognitionsystem.model.AttendanceLog;
import com.example.facerecognitionsystem.model.RecognitionResult;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLiteLogRepository implements LogRepository {

    private static final String DB_URL = "jdbc:sqlite:facerecognition.db";

    public SQLiteLogRepository() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS attendance_logs (" +
                "log_id TEXT PRIMARY KEY, " +
                "status TEXT NOT NULL, " +
                "person_name TEXT, " +
                "confidence REAL, " +
                "logged_at TEXT NOT NULL)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    @Override
    public void save(AttendanceLog log) {
        String sql = "INSERT INTO attendance_logs(log_id, status, person_name, confidence, logged_at) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, log.getLogId());
            pstmt.setString(2, log.getResult().getStatus());
            pstmt.setString(3, log.getResult().getPersonName());
            pstmt.setDouble(4, log.getResult().getConfidence());
            pstmt.setString(5, log.getLoggedAt().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving log: " + e.getMessage());
        }
    }

    @Override
    public List<AttendanceLog> findAll() {
        List<AttendanceLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM attendance_logs ORDER BY logged_at DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                RecognitionResult result = new RecognitionResult(
                        rs.getString("status"),
                        rs.getString("person_name"),
                        rs.getDouble("confidence")
                );
                AttendanceLog log = new AttendanceLog(rs.getString("log_id"), result);
                log.setLoggedAt(LocalDateTime.parse(rs.getString("logged_at")));
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all logs: " + e.getMessage());
        }
        return logs;
    }

    @Override
    public List<AttendanceLog> findByDate(LocalDate date) {
        List<AttendanceLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM attendance_logs WHERE logged_at LIKE ? ORDER BY logged_at DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date.toString() + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                RecognitionResult result = new RecognitionResult(
                        rs.getString("status"),
                        rs.getString("person_name"),
                        rs.getDouble("confidence")
                );
                AttendanceLog log = new AttendanceLog(rs.getString("log_id"), result);
                log.setLoggedAt(LocalDateTime.parse(rs.getString("logged_at")));
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error finding logs by date: " + e.getMessage());
        }
        return logs;
    }
}