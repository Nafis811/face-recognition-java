package com.example.facerecognitionsystem.repository;

import com.example.facerecognitionsystem.model.Person;
import java.util.List;

public interface PersonRepository {
    void save(Person person);
    Person findByName(String name);
    List<Person> findAll();
    void delete(String id);
}