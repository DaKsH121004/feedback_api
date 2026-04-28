package com.feedback.feedback.services.impl;

import com.feedback.feedback.entities.AppConfig;
import com.feedback.feedback.repositories.AppConfigRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class FormScheduler {

    private final AppConfigRepository appConfigRepository;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void closeExpiredForms() {

        AppConfig config = appConfigRepository.findById(1L).orElse(null);

        if (config == null) return;

        OffsetDateTime now = OffsetDateTime.now();

        if (Boolean.TRUE.equals(config.getFeedbackEnabled())
                && config.getEndTime() != null
                && now.isAfter(config.getEndTime())) {

            config.setFeedbackEnabled(false);
            appConfigRepository.save(config);

            System.out.println("Form automatically closed ✅");
        }
    }
}
