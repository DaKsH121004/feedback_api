package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.FacultyDto;
import com.feedback.feedback.dto.DepartmentChampionDto;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.entities.Faculty;
import com.feedback.feedback.repositories.FacultyRepository;
import com.feedback.feedback.repositories.FeedbackRepository;
import com.feedback.feedback.repositories.CourseRepository;
import com.feedback.feedback.repositories.DepartmentRepository;
import com.feedback.feedback.services.Dashboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements Dashboard {
    private final FeedbackRepository feedbackRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional(readOnly = true)
    public Response getDashboard() {

        return Response.builder()
                .status(200)
                .message("Dashboard fetched successfully")
                .totalFeedback(feedbackRepository.count())
                .averageRating(getAverageFeedback())
                .totalFaculty(facultyRepository.count())
                .totalCourses(courseRepository.count())
                .totalDepartments(departmentRepository.count())
                .faculties(getTopThreeFacultyMember())
                .departmentPerformance(feedbackRepository.findDepartmentPerformance().stream()
                        .map(p -> new com.feedback.feedback.dto.ChartDataDto(p.getLabel(), p.getValue()))
                        .toList())
                .feedbackVolume(feedbackRepository.findFeedbackVolumeByDepartment().stream()
                        .map(p -> new com.feedback.feedback.dto.ChartDataDto(p.getLabel(), p.getValue()))
                        .toList())
                .ratingTrend(feedbackRepository.findMonthlyRatingTrend().stream()
                        .map(p -> new com.feedback.feedback.dto.ChartDataDto(p.getLabel(), p.getValue()))
                        .toList())
                .departmentChampions(getDepartmentChampions())
                .build();
    }

    private Double getAverageFeedback() {
        Double avgRating = feedbackRepository.findAverageRatingFromFaculty();
        return avgRating != null ? avgRating : 0.0;
    }

    private List<FacultyDto> getTopThreeFacultyMember() {

        List<Faculty> topFaculties = facultyRepository
                .findTop10ByAverageRatingIsNotNullOrderByAverageRatingDesc();

        List<FacultyDto> facultyDtos = topFaculties.stream().map(faculty ->
                FacultyDto.builder()
                        .id(faculty.getId())
                        .facultyName(faculty.getFacultyName())
                        .facultyCode(faculty.getFacultyCode())
                        .facultyEmail(faculty.getFacultyEmail())
                        .facultyPhone(faculty.getFacultyPhone())
                        .averageRating(faculty.getAverageRating())
                        .totalResponses(faculty.getTotalResponses())
                        .build()
        ).toList();

        return facultyDtos;
    }

    private List<DepartmentChampionDto> getDepartmentChampions() {
        List<DepartmentChampionDto> champions = new ArrayList<>();
        List<com.feedback.feedback.entities.Department> departments = departmentRepository.findAll();
        List<Faculty> faculties = facultyRepository.findAll();

        for (com.feedback.feedback.entities.Department dept : departments) {
            Faculty bestFaculty = null;
            for (Faculty f : faculties) {
                if (f.getDepartments() != null && f.getDepartments().stream().anyMatch(d -> d.getId().equals(dept.getId()))) {
                    if (bestFaculty == null || (f.getAverageRating() != null && f.getAverageRating() > (bestFaculty.getAverageRating() != null ? bestFaculty.getAverageRating() : 0))) {
                        bestFaculty = f;
                    }
                }
            }
            if (bestFaculty != null) {
                champions.add(DepartmentChampionDto.builder()
                        .departmentName(dept.getDepartmentName())
                        .facultyName(bestFaculty.getFacultyName())
                        .averageRating(bestFaculty.getAverageRating())
                        .build());
            }
        }
        return champions;
    }

}
