package com.feedback.feedback.services;

import com.feedback.feedback.dto.CourseRequest;
import com.feedback.feedback.dto.Response;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {
    Response processBulkUpload(MultipartFile file);
    Response createCourse(CourseRequest courseRequest);
    Response getAllCourses(int page, int size, String search);
    Response getCourseById(Long id);
    Response updateCourse(Long id, CourseRequest courseRequest);
    Response deleteCourse(Long id);
    Response deleteAllCourses();
}
