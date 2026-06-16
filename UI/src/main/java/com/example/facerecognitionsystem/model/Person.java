package com.example.facerecognitionsystem.model;

import java.time.LocalDateTime;

public class Person {
    private String id;
    private String name;
    private LocalDateTime enrolledAt;

    public Person(String id, String name, LocalDateTime enrolledAt) {
        this.id = id;
        this.name = name;
        this.enrolledAt = enrolledAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    @Override
    public String toString() {
        return "Person{id='" + id + "', name='" + name + "'}";
    }
}