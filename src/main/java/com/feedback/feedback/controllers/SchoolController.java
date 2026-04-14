package com.feedback.feedback.controllers;

import com.feedback.feedback.dto.Response;
import com.feedback.feedback.dto.SchoolRequest;
import com.feedback.feedback.services.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/school")
@RequiredArgsConstructor
public class SchoolController {
    private final SchoolService schoolService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> addSchool(@RequestBody SchoolRequest schoolRequest) {
        return new ResponseEntity<>(schoolService.createSchool(schoolRequest), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<Response> getAllSchools() {
        return new ResponseEntity<>(schoolService.getAllSchools(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> getSchoolById(@PathVariable Long id) {
        return new ResponseEntity<>(schoolService.getSchoolById(id), HttpStatus.OK);
    }
}
