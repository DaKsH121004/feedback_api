package com.feedback.feedback.services;

import com.feedback.feedback.dto.AdminRegisterRequest;
import com.feedback.feedback.dto.LoginRequest;
import com.feedback.feedback.dto.Response;

public interface UserService {
    Response registerAdmin(AdminRegisterRequest adminRegisterRequest);
    Response loginUser(LoginRequest loginRequest);
}
