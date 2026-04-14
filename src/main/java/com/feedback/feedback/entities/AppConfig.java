package com.feedback.feedback.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "app_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppConfig {

    @Id
    private Long id;

    private Boolean feedbackEnabled;

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String formToken;
}
