package com.feedback.feedback.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackRequest {

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100, message = "Student name must be between 2-100 characters")
    private String studentName;

    @NotBlank(message = "Roll number is required")
    private String studentRollNo;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String studentEmail;

    @NotNull(message = "School ID is required")
    private Long schoolId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 8, message = "Semester must be at most 8")
    private Integer semester;

    @NotBlank(message = "Class section is required")
    private String classSection;

    @NotNull(message = "Faculty ID is required")
    private Long facultyId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Q1 rating is required")
    @Min(1) @Max(5)
    private Integer q1;

    @NotNull(message = "Q2 rating is required")
    @Min(1) @Max(5)
    private Integer q2;

    @NotNull(message = "Q3 rating is required")
    @Min(1) @Max(5)
    private Integer q3;

    @NotNull(message = "Q4 rating is required")
    @Min(1) @Max(5)
    private Integer q4;

    @NotNull(message = "Q5 rating is required")
    @Min(1) @Max(5)
    private Integer q5;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
}