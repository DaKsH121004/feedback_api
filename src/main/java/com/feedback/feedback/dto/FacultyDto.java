package com.feedback.feedback.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacultyDto {
    private Long id;
    private String facultyName;

    private String facultyCode;

    private String facultyEmail;

    private String facultyPhone;

    private Double averageRating;
    private Integer totalResponses;
    private List<DepartmentDto> departments;
}
