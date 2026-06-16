package com.example.facerecognitionsystem.repository;

import com.example.facerecognitionsystem.model.AttendanceLog;
import java.time.LocalDate;
import java.util.List;

public interface LogRepository {
    void save(AttendanceLog log);
    List<AttendanceLog> findAll();
    List<AttendanceLog> findByDate(LocalDate date);
}