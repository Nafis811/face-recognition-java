package com.example.facerecognitionsystem.repository;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {

    public static void initialize() {
        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement()) {


            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mahasiswa (
                    nim VARCHAR(10) PRIMARY KEY,
                    nama VARCHAR(50),
                    jurusan VARCHAR(50)
                )
            """);


            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mata_kuliah (
                    kode_mk VARCHAR(10) PRIMARY KEY,
                    nama_mk VARCHAR(100),
                    sks INT,
                    dosen VARCHAR(50)
                )
            """);


            stmt.execute("""
                CREATE TABLE IF NOT EXISTS krs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nim VARCHAR(10),
                    kode_mk VARCHAR(10),
                    FOREIGN KEY (nim) REFERENCES mahasiswa(nim),
                    FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode_mk)
                )
            """);

            // Tabel tugas
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tugas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    kode_mk VARCHAR(10),
                    judul VARCHAR(100),
                    deadline DATE,
                    status VARCHAR(20) DEFAULT 'belum',
                    FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode_mk)
                )
            """);

            // Insert data awal mata kuliah kalau belum ada
            stmt.execute("""
                INSERT IGNORE INTO mata_kuliah VALUES
                ('MK001', 'Pemrograman Berorientasi Objek', 3, 'M. Bayu Wibisono, S.Kom.,MM'),
                ('MK002', 'Sistem Basis Data', 3, 'Iin Ernawati S.Kom., M.Si'),
                ('MK003', 'Struktur Data', 3, 'Rifka Dwi Amalia, S.Pd., M.Kom.'),
                ('MK004', 'Jaringan Komputer', 2, 'Dr. Ifik L. Arifin, Dipl.Inf. Arifin'),
                ('MK005', 'UI/UX', 3, 'Ika Nurlaili Isnainiyah, S.Kom.,M.Sc')
            """);

            // Insert data awal tugas
            stmt.execute("""
                INSERT IGNORE INTO tugas VALUES
                (1, 'MK001', 'UAS - Aplikasi OOP', '2026-06-28', 'belum'),
                (2, 'MK002', 'Sistem Basis Data', '2026-06-30', 'belum'),
                (3, 'MK003', 'Struktur Data', '2026-07-05', 'selesai')
            """);

            // Tabel jadwal
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS jadwal (
                id INT AUTO_INCREMENT PRIMARY KEY,
                kode_mk VARCHAR(10),
                hari VARCHAR(15),
                jam_mulai VARCHAR(10),
                jam_selesai VARCHAR(10),
                ruangan VARCHAR(20),
                FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode_mk)
            )
        """);

            // Insert data jadwal
            stmt.execute("""
            INSERT IGNORE INTO jadwal VALUES
            (1, 'MK001', 'Senin', '08:00', '10:30', 'Lab 301'),
            (2, 'MK002', 'Selasa', '10:00', '12:30', 'Ruang 202'),
            (3, 'MK003', 'Rabu', '13:00', '15:30', 'Lab 302'),
            (4, 'MK004', 'Kamis', '08:00', '09:40', 'Ruang 101'),
            (5, 'MK005', 'Jumat', '10:00', '12:30', 'Lab 303')
        """);
            System.out.println("Database initialized!");

        } catch (Exception e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}