package com.feedback.feedback.controllers;

import com.feedback.feedback.dto.AssignmentRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.services.FacultyCourseAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assignment")
@RequiredArgsConstructor
public class AssignmentController {

    private final FacultyCourseAssignmentService assignmentService;

    @GetMapping
    public Response getAll() {
        return assignmentService.getAllAssignments();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public Response create(@RequestBody AssignmentRequest request) {
        return assignmentService.createAssignment(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public Response update(@PathVariable Long id,
                           @RequestBody AssignmentRequest request) {
        return assignmentService.updateAssignment(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public Response delete(@PathVariable Long id) {
        return assignmentService.deleteAssignment(id);
    }
}
