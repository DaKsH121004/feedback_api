package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.FacultyCourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyCourseAssignmentRepository extends JpaRepository<FacultyCourseAssignment, Long> {
    boolean existsByFacultyIdAndDepartmentIdAndCourseId(
            Long facultyId,
            Long departmentId,
            Long courseId
    );
}
