package com.feedback.feedback.controllers;

import com.feedback.feedback.dto.DepartmentRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> createDepartment(@RequestBody DepartmentRequest departmentRequest) {
        return new ResponseEntity<>(departmentService.createDepartment(departmentRequest), HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> getAllDepartments(){
        return new ResponseEntity<>(departmentService.getAllDepartments(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> getDepartmentById(@PathVariable Long id){
        return new ResponseEntity<>(departmentService.getDepartmentById(id), HttpStatus.OK);
    }
}
