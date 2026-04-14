package com.feedback.feedback.controllers;

import com.feedback.feedback.dto.FormScheduleRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.services.AppConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/form")
@RequiredArgsConstructor
public class AppConfigController {

    private final AppConfigService appConfigService;

    @PutMapping("/schedule")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> scheduleForm(
            @RequestBody FormScheduleRequest request
    ) {
        return new ResponseEntity<>(appConfigService.scheduleForm(request.getEndTime()), HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<Response> getStatus() {
        return new ResponseEntity<>(appConfigService.getFormStatus(),HttpStatus.OK);
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<?> validateForm(@PathVariable String token) {

        boolean isValid = appConfigService.isValidToken(token);

        if (!isValid) {
            return ResponseEntity.status(403).body("Form is expired or invalid");
        }

        return ResponseEntity.ok("Valid form");
    }
}
