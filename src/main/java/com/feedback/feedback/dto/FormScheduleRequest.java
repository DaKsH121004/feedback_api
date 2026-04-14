package com.feedback.feedback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormScheduleRequest {

    @NotNull(message = "End time is required")
    private OffsetDateTime endTime;
}
