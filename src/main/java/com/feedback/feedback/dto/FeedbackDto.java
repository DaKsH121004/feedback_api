package com.feedback.feedback.dto;

import com.feedback.feedback.entities.Course;
import com.feedback.feedback.entities.Department;
import com.feedback.feedback.entities.Faculty;
import com.feedback.feedback.entities.School;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackDto {
    private Long id;
    private String studentName;
    private String studentRollNo;
    private String studentEmail;
    private School school;
    private Department department;

    private Integer semester;
    private String classSection;

    private Faculty faculty;

    private Course course;

    private Integer q1;
    private Integer q2;
    private Integer q3;
    private Integer q4;
    private Integer q5;

    private String remarks;

    private LocalDateTime createdAt;
}
