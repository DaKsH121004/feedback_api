package com.feedback.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentRequest {
    @NotBlank(message = "Department Name is required")
    private String departmentName;
    @NotNull(message = "Please select school")
    private Long schoolId;
}
