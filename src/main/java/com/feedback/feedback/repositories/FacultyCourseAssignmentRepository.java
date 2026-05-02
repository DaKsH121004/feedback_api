package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.FacultyCourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyCourseAssignmentRepository extends JpaRepository<FacultyCourseAssignment, Long> {
    boolean existsByFacultyIdAndDepartmentIdAndCourseId(
            Long facultyId,
            Long departmentId,
            Long courseId
    );

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM FacultyCourseAssignment f WHERE f.course.id = :courseId")
    void deleteByCourseId(@org.springframework.data.repository.query.Param("courseId") Long courseId);
}
