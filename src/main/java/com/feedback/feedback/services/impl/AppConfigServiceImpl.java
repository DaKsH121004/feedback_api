package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.FeedbackFormDto;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.entities.AppConfig;
import com.feedback.feedback.repositories.AppConfigRepository;
import com.feedback.feedback.services.AppConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppConfigServiceImpl implements AppConfigService {
    private final AppConfigRepository appConfigRepository;
    private final ModelMapper modelMapper;


    @Override
    public Boolean isFormActive() {
        AppConfig config = appConfigRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Config not found"));

        OffsetDateTime now = OffsetDateTime.now();

        return Boolean.TRUE.equals(config.getFeedbackEnabled()) &&
                config.getStartTime() != null &&
                config.getEndTime() != null &&
                now.isAfter(config.getStartTime()) &&
                now.isBefore(config.getEndTime());
    }

    @Override
    public Response scheduleForm(OffsetDateTime end) {

        if (end.isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("End time must be in the future");
        }

        AppConfig config = appConfigRepository.findById(1L)
                .orElse(AppConfig.builder().id(1L).build());

        String token = UUID.randomUUID().toString();

        config.setFeedbackEnabled(true);
        config.setStartTime(OffsetDateTime.now());
        config.setEndTime(end);
        config.setFormToken(token);

        appConfigRepository.save(config);

        String formUrl = "https://feedback-api-gcbr.onrender.com/create-form/" + token;

        return Response.builder()
                .status(201)
                .message("Successfully scheduled feedback form")
                .url(formUrl) // ✅ return URL
                .build();
    }



    @Override
    public Response getFormStatus() {

        AppConfig config = appConfigRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Config not found"));

        FeedbackFormDto dto = modelMapper.map(config, FeedbackFormDto.class);

        String formUrl = null;

        if (isFormActive()) {
            formUrl = "http://localhost:3000/create-form/" + config.getFormToken();
        }

        return Response.builder()
                .status(200)
                .message("Form status fetched successfully")
                .data(dto)
                .url(formUrl) // ✅ ADD THIS FIELD
                .build();
    }

    @Override
    public boolean isValidToken(String token) {

        AppConfig config = appConfigRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Config not found"));

        return token != null &&
                token.equals(config.getFormToken()) &&
                isFormActive();
    }
}