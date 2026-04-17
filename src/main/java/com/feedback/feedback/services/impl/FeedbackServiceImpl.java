package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.FeedbackDto;
import com.feedback.feedback.dto.FeedbackRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.entities.*;
import com.feedback.feedback.exceptions.AlreadyExistException;
import com.feedback.feedback.exceptions.NotFoundException;
import com.feedback.feedback.repositories.*;
import com.feedback.feedback.services.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final SchoolRepository schoolRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final ModelMapper modelMapper;


    @Override
    public Response uploadExcel(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row row = rows.next();

                String facultyName = row.getCell(4).getStringCellValue();

                Faculty faculty = facultyRepository.findByFacultyName(facultyName)
                        .orElseThrow(() -> new RuntimeException("Faculty not found: " + facultyName));

                int q1 = (int) row.getCell(6).getNumericCellValue();
                int q2 = (int) row.getCell(7).getNumericCellValue();
                int q3 = (int) row.getCell(8).getNumericCellValue();
                int q4 = (int) row.getCell(9).getNumericCellValue();
                int q5 = (int) row.getCell(10).getNumericCellValue();

                double feedbackAvg = (q1 + q2 + q3 + q4 + q5) / 5.0;

                // Save feedback
                Feedback feedback = Feedback.builder()
                        .faculty(faculty)
                        .q1(q1)
                        .q2(q2)
                        .q3(q3)
                        .q4(q4)
                        .q5(q5)
                        .remarks(row.getCell(11).getStringCellValue())
                        .createdAt(LocalDateTime.now())
                        .build();

                feedbackRepository.save(feedback);

                // Update faculty stats
                double currentAvg = faculty.getAverageRating() == null ? 0 : faculty.getAverageRating();
                int total = faculty.getTotalResponses() == null ? 0 : faculty.getTotalResponses();

                double newAvg = ((currentAvg * total) + feedbackAvg) / (total + 1);

                faculty.setAverageRating(newAvg);
                faculty.setTotalResponses(total + 1);

                facultyRepository.save(faculty);
            }

        } catch (Exception e) {
            log.error("Error processing file", e);
            throw new RuntimeException("Failed to process Excel");
        }

        return Response.builder()
                .status(201)
                .message("Successfully uploaded feedback")
                .build();
    }

    @Override
    public Response createFeedback(FeedbackRequest feedbackRequest) {

        School school = schoolRepository.findById(feedbackRequest.getSchoolId())
                .orElseThrow(() -> new NotFoundException("School not found"));

        Department department = departmentRepository.findById(feedbackRequest.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found"));

        Course course = courseRepository.findById(feedbackRequest.getCourseId())
                .orElseThrow(() -> new NotFoundException("Course not found"));

        Faculty faculty = facultyRepository.findById(feedbackRequest.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Faculty not found"));

        if (!department.getSchool().getId().equals(school.getId())) {
            throw new RuntimeException("Department does not belong to selected school");
        }

        if (course.getFaculties() == null ||
                course.getFaculties().stream().noneMatch(f -> f.getId().equals(faculty.getId()))) {
            throw new RuntimeException("Faculty does not teach this course");
        }

        // boolean alreadySubmitted = feedbackRepository
        //         .existsByStudentRollNoAndFacultyIdAndCourseId(
        //                 feedbackRequest.getStudentRollNo(),
        //                 faculty.getId(),
        //                 course.getId()
        //         );

        // if (alreadySubmitted) {
        //     throw new AlreadyExistException("You have already submitted feedback for this faculty and course");
        // }

        Feedback feedback = Feedback.builder()
                // .studentName(feedbackRequest.getStudentName())
                // .studentEmail(feedbackRequest.getStudentEmail())
                // .studentRollNo(feedbackRequest.getStudentRollNo())
                .school(school)
                .department(department)
                .semester(feedbackRequest.getSemester())
                .classSection(feedbackRequest.getClassSection())
                .course(course)
                .faculty(faculty)
                .q1(feedbackRequest.getQ1())
                .q2(feedbackRequest.getQ2())
                .q3(feedbackRequest.getQ3())
                .q4(feedbackRequest.getQ4())
                .q5(feedbackRequest.getQ5())
                .remarks(feedbackRequest.getRemarks())
                .createdAt(LocalDateTime.now())
                .build();

        feedbackRepository.save(feedback);

        updateFacultyRating(faculty, feedbackRequest);
        facultyRepository.save(faculty);

        return Response.builder()
                .status(201)
                .message("Successfully submitted feedback")
                .build();
    }

    @Override
    public Response getAllFeedback() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        List<FeedbackDto> feedbackDtos = modelMapper.map(feedbacks, new TypeToken<List<FeedbackDto>>() {}.getType());
        return Response.builder()
                .status(200)
                .message("Successfully submitted feedback")
                .feedbacks(feedbackDtos)
                .build();
    }

    private void updateFacultyRating(Faculty faculty, FeedbackRequest feedbackRequest) {

        double feedbackAvg = (
                feedbackRequest.getQ1() +
                        feedbackRequest.getQ2() +
                        feedbackRequest.getQ3() +
                        feedbackRequest.getQ4() +
                        feedbackRequest.getQ5()
        ) / 5.0;

        double currentAvg = faculty.getAverageRating() == null ? 0 : faculty.getAverageRating();
        int total = faculty.getTotalResponses() == null ? 0 : faculty.getTotalResponses();

        double newAvg = ((currentAvg * total) + feedbackAvg) / (total + 1);

        faculty.setAverageRating(newAvg);
        faculty.setTotalResponses(total + 1);
    }
}
