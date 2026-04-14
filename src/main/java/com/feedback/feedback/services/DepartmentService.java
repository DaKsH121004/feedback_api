package com.feedback.feedback.services;

import com.feedback.feedback.dto.DepartmentRequest;
import com.feedback.feedback.dto.Response;
import org.springframework.web.bind.annotation.PathVariable;

public interface DepartmentService {
    Response createDepartment(DepartmentRequest departmentRequest);
    Response getAllDepartments();
    Response getDepartmentById(Long id);
}
