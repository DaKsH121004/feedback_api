package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentNameAndSchoolId(String departmentName, Long schoolId);
    Optional<Department> findByDepartmentName(String departmentName);
}
