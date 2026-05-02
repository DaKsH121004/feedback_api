package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.FacultyCourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyCourseAssignmentRepository extends JpaRepository<FacultyCourseAssignment, Long> {
    boolean existsByFacultyIdAndDepartmentIdAndCourseIdAndSemesterAndClassSection(
            Long facultyId,
            Long departmentId,
            Long courseId,
            Integer semester,
            String classSection
    );

    java.util.List<FacultyCourseAssignment> findByFacultyIdAndDepartmentIdAndSemesterAndClassSection(
            Long facultyId,
            Long departmentId,
            Integer semester,
            String classSection
    );

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM FacultyCourseAssignment f WHERE f.course.id = :courseId")
    void deleteByCourseId(@org.springframework.data.repository.query.Param("courseId") Long courseId);
}
