package com.feedback.feedback.controllers;

import com.feedback.feedback.dto.AdminRegisterRequest;
import com.feedback.feedback.dto.LoginRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerAdminUser(@RequestBody AdminRegisterRequest adminRegisterRequest) {
        return new ResponseEntity<>(userService.registerAdmin(adminRegisterRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest){
        return new ResponseEntity<>(userService.loginUser(loginRequest), HttpStatus.OK);
    }
}
