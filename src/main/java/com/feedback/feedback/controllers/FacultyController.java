package com.feedback.feedback.controllers;

import com.feedback.feedback.dto.FacultyRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.services.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/faculty")
@RequiredArgsConstructor
public class FacultyController {
    private final FacultyService facultyService;

    @PostMapping("add")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> addFaculty(@RequestBody FacultyRequest facultyRequest) {
        return new ResponseEntity<>(facultyService.createFaculty(facultyRequest), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<Response> getAllFaculties() {
        return new ResponseEntity<>(facultyService.getAllFaculties(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> getFacultyById(@PathVariable Long id) {
        return new ResponseEntity<>(facultyService.getFacultyById(id), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateFacultyById(
            @PathVariable Long id,
            @RequestBody FacultyRequest facultyRequest
    ) {
        return new ResponseEntity<>(facultyService.updateFacultyById(id, facultyRequest), HttpStatus.CREATED);
    }

    // Delete Faculty
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteFacultyById(@PathVariable Long id) {
        return new ResponseEntity<>(facultyService.deleteFacultyById(id), HttpStatus.OK);
    }
}
