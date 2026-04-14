package com.feedback.feedback.dto;

import com.feedback.feedback.entities.School;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentDto {
    private Long id;
    private String departmentName;
    private SchoolDto school;
}
