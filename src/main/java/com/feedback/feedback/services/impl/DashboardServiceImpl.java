package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.FacultyDto;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.entities.Faculty;
import com.feedback.feedback.repositories.FacultyRepository;
import com.feedback.feedback.repositories.FeedbackRepository;
import com.feedback.feedback.services.Dashboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements Dashboard {
    private final FeedbackRepository feedbackRepository;
    private final FacultyRepository facultyRepository;

    @Override
    public Response getDashboard() {

        return Response.builder()
                .status(200)
                .message("Dashboard fetched successfully")
                .totalFeedback(getTotalFeedback())
                .averageRating(getAverageFeedback())
                .totalFaculty(getTotalFacultyMember())
                .faculties(getTopThreeFacultyMember())
                .build();
    }

    private Long getTotalFeedback() {

        Long totalFeedback = feedbackRepository.count();

        return totalFeedback;
    }


    private Double getAverageFeedback() {

        Double avgRating = feedbackRepository.findAverageRatingFromFaculty();

        if (avgRating == null) {
            avgRating = 0.0;
        }

        return avgRating;
    }

    private Long getTotalFacultyMember() {

        Long totalFaculty = facultyRepository.count();

        return totalFaculty;
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

}
