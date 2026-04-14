package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findBySchoolName(String schoolName);
}
