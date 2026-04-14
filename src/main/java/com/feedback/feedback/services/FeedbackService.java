package com.feedback.feedback.services;

import com.feedback.feedback.dto.FeedbackRequest;
import com.feedback.feedback.dto.Response;
import org.springframework.web.multipart.MultipartFile;

public interface FeedbackService {
    Response uploadExcel(MultipartFile file);
    Response createFeedback(FeedbackRequest feedbackRequest);
    Response getAllFeedback();
}
