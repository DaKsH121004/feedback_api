package com.feedback.feedback.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackRequest {

    @JsonProperty("studentName")
    private String studentName;
    @JsonProperty("studentRollNo")
    private String studentRollNo;
    @JsonProperty("studentEmail")
    private String studentEmail;

    @NotNull(message = "School ID is required")
    @JsonProperty("schoolId")
    private Long schoolId;

    @NotNull(message = "Department ID is required")
    @JsonProperty("departmentId")
    private Long departmentId;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 10, message = "Semester must be at most 10")

    @JsonProperty("semester")
    private Integer semester;

    @NotBlank(message = "Class section is required")
    @JsonProperty("classSection")
    private String classSection;

    @NotNull(message = "Faculty ID is required")
    @JsonProperty("facultyId")
    private Long facultyId;

    @NotNull(message = "Course ID is required")
    @JsonProperty("courseId")
    private Long courseId;

    @NotNull(message = "Q1 rating is required")
    @Min(1) @Max(5)
    @JsonProperty("q1")
    private Integer q1;

    @NotNull(message = "Q2 rating is required")
    @Min(1) @Max(5)
    @JsonProperty("q2")
    private Integer q2;

    @NotNull(message = "Q3 rating is required")
    @Min(1) @Max(5)
    @JsonProperty("q3")
    private Integer q3;

    @NotNull(message = "Q4 rating is required")
    @Min(1) @Max(5)
    @JsonProperty("q4")
    private Integer q4;

    @NotNull(message = "Q5 rating is required")
    @Min(1) @Max(5)
    @JsonProperty("q5")
    private Integer q5;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    @JsonProperty("remarks")
    private String remarks;
}