package com.feedback.feedback.services;

import com.feedback.feedback.dto.CourseRequest;
import com.feedback.feedback.dto.Response;

public interface CourseService {
    Response createCourse(CourseRequest courseRequest);
    Response getAllCourses(int page, int size, String search);
    Response getCourseById(Long id);
}
