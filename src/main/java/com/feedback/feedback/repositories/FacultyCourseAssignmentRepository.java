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

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(f) > 0 FROM FacultyCourseAssignment f WHERE f.faculty.id = :facultyId AND f.department.id = :departmentId AND f.course.id = :courseId AND (f.semester IS NULL OR f.classSection IS NULL OR f.classSection = '')")
    boolean existsByFacultyIdAndDepartmentIdAndCourseIdAndSemesterIsNull(@org.springframework.data.repository.query.Param("facultyId") Long facultyId, @org.springframework.data.repository.query.Param("departmentId") Long departmentId, @org.springframework.data.repository.query.Param("courseId") Long courseId);

    java.util.List<FacultyCourseAssignment> findByFacultyIdAndDepartmentIdAndSemesterAndClassSection(
            Long facultyId,
            Long departmentId,
            Integer semester,
            String classSection
    );

    @org.springframework.data.jpa.repository.Query("SELECT f FROM FacultyCourseAssignment f WHERE f.faculty.id = :facultyId AND f.department.id = :departmentId AND (f.semester IS NULL OR f.classSection IS NULL OR f.classSection = '')")
    java.util.List<FacultyCourseAssignment> findByFacultyIdAndDepartmentIdAndSemesterIsNull(@org.springframework.data.repository.query.Param("facultyId") Long facultyId, @org.springframework.data.repository.query.Param("departmentId") Long departmentId);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM FacultyCourseAssignment f WHERE f.course.id = :courseId")
    void deleteByCourseId(@org.springframework.data.repository.query.Param("courseId") Long courseId);
}
