package com.feedback.feedback.services;

import com.feedback.feedback.dto.FacultyRequest;
import com.feedback.feedback.dto.Response;

public interface FacultyService {
    Response createFaculty(FacultyRequest facultyRequest);
    Response getAllFaculties();
    Response getFacultyById(Long id);
    Response updateFacultyById(Long id, FacultyRequest facultyRequest);
    Response deleteFacultyById(Long id);
}
