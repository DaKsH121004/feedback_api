package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseNameIgnoreCase(String courseName);
    Page<Course> findByCourseNameContainingIgnoreCase(String courseName, Pageable pageable);
}
