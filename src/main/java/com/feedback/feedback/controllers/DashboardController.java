package com.feedback.feedback.controllers;

import com.feedback.feedback.dto.Response;
import com.feedback.feedback.services.Dashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final Dashboard dashboard;

    @GetMapping()
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> getAllCourses(){
        return new ResponseEntity<>(dashboard.getDashboard(), HttpStatus.CREATED);
    }
}
