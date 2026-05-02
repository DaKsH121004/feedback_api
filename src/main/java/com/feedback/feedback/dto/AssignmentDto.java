package com.feedback.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDto {
    private Long id;
    private Long facultyId;
    private String facultyName;

    private Long departmentId;
    private String departmentName;

    private Long courseId;
    private String courseName;

    private Integer semester;
    private String classSection;
}