package com.feedback.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentChampionDto {
    private String departmentName;
    private String facultyName;
    private Double averageRating;
}
