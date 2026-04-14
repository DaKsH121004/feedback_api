package com.feedback.feedback.controllers;

import com.feedback.feedback.dto.FeedbackRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.services.AppConfigService;
import com.feedback.feedback.services.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;
    private final AppConfigService appConfigService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> uploadExcel(@RequestParam("file") MultipartFile file){
        return new ResponseEntity<>(feedbackService.uploadExcel(file), HttpStatus.CREATED);
    }

    @PostMapping("/add")
    public ResponseEntity<Response> addFeedback(@RequestBody FeedbackRequest feedbackRequest){
        if (!appConfigService.isFormActive()) {
            return ResponseEntity.status(403)
                    .body(Response.builder()
                            .status(403)
                            .message("Feedback form is closed")
                            .build());
        }
        return new ResponseEntity<>(feedbackService.createFeedback(feedbackRequest), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Response> getAllFeedbacks(){
        return new ResponseEntity<>(feedbackService.getAllFeedback(), HttpStatus.OK);
    }
}
