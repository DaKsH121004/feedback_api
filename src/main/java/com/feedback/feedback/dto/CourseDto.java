package com.feedback.feedback.dto;

import com.feedback.feedback.entities.Faculty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDto {
    private Long id;
    private String courseName;
    private List<Faculty> faculties;

    private int currentPage;
    private int totalPages;
    private long totalElements;
}
