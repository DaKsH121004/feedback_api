package com.feedback.feedback.services;

import com.feedback.feedback.dto.Response;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface AppConfigService {
    Boolean isFormActive();
    Response scheduleForm(OffsetDateTime end);
    Response getFormStatus();
    boolean isValidToken(String token);
}
