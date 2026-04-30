package com.feedback.feedback.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int status;
    private String message;

    private int currentPage;
    private int totalPages;
    private long totalElements;

    private String token;
    private Date expiryDate;

    private UserDto user;

    private List<SchoolDto> schools;
    private SchoolDto school;

    private List<DepartmentDto> departments;
    private DepartmentDto department;

    private List<FacultyDto> faculties;
    private FacultyDto faculty;

    private List<CourseDto> courses;
    private CourseDto course;

    private FeedbackFormDto data;

    private List<FeedbackDto> feedbacks;

    // Dashboard
    private Long totalFeedback;
    private Double averageRating;
    private Long totalFaculty;
    private Long totalCourses;
    private Long totalDepartments;

    private List<ChartDataDto> departmentPerformance;
    private List<ChartDataDto> ratingTrend;
    private List<ChartDataDto> feedbackVolume;

    private String url;

    private List<AssignmentDto> assignments;

}
