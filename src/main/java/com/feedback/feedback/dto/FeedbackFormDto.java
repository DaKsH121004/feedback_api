package com.feedback.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackFormDto {
    private Long id;
    private Boolean feedbackEnabled;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
}
