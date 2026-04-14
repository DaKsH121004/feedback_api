package com.feedback.feedback.services;

import com.feedback.feedback.dto.Response;
import com.feedback.feedback.dto.SchoolRequest;

public interface SchoolService {
    Response createSchool(SchoolRequest schoolRequest);
    Response getAllSchools();
    Response getSchoolById(Long id);
}
