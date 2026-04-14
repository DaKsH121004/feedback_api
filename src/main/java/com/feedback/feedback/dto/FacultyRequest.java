package com.feedback.feedback.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacultyRequest {
    @NotBlank(message = "Name is required")
    private String facultyName;
    @NotBlank(message = "Faculty Code is required")
    private String facultyCode;
    @NotBlank(message = "Email is required")
    @Email
    private String facultyEmail;
    @NotBlank(message = "Phone number is required")
    private String facultyPhone;
    @NotNull(message = "Please select department")
    private List<Long> departmentId;
}
