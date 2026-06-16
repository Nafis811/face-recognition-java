package com.example.facerecognitionsystem.repository;

import com.example.facerecognitionsystem.model.Person;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLitePersonRepository implements PersonRepository {

    private static final String DB_URL = "jdbc:sqlite:facerecognition.db";

    public SQLitePersonRepository() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS persons (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "enrolled_at TEXT NOT NULL)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    @Override
    public void save(Person person) {
        String sql = "INSERT OR REPLACE INTO persons(id, name, enrolled_at) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, person.getId());
            pstmt.setString(2, person.getName());
            pstmt.setString(3, person.getEnrolledAt().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving person: " + e.getMessage());
        }
    }

    @Override
    public Person findByName(String name) {
        String sql = "SELECT * FROM persons WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Person(
                        rs.getString("id"),
                        rs.getString("name"),
                        LocalDateTime.parse(rs.getString("enrolled_at"))
                );
            }
        } catch (SQLException e) {
            System.err.println("Error finding person: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Person> findAll() {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM persons";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                persons.add(new Person(
                        rs.getString("id"),
                        rs.getString("name"),
                        LocalDateTime.parse(rs.getString("enrolled_at"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all persons: " + e.getMessage());
        }
        return persons;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM persons WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting person: " + e.getMessage());
        }
    }
}