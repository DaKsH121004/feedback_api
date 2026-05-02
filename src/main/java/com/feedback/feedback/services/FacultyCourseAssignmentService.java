package com.feedback.feedback.services;

import com.feedback.feedback.dto.AssignmentRequest;
import com.feedback.feedback.dto.Response;

public interface FacultyCourseAssignmentService {
    Response createAssignment(AssignmentRequest request);
    Response updateAssignment(Long id, AssignmentRequest request);
    Response deleteAssignment(Long id);
    Response getAllAssignments();
    Response processBulkUpload(org.springframework.web.multipart.MultipartFile file);
    Response getAssignedCourses(Long facultyId, Long departmentId, Integer semester, String section);
}
