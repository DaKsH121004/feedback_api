package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByFacultyCode(String facultyCode);
    Optional<Faculty> findByFacultyPhoneAndFacultyEmail(String facultyPhone, String facultyEmail);
    Optional<Faculty> findByFacultyName(String name);
    List<Faculty> findTop3ByAverageRatingIsNotNullOrderByAverageRatingDesc();
}
