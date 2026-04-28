package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.CourseDto;
import com.feedback.feedback.dto.CourseRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.entities.Course;
import com.feedback.feedback.entities.Faculty;
import com.feedback.feedback.exceptions.AlreadyExistException;
import com.feedback.feedback.exceptions.NotFoundException;
import com.feedback.feedback.repositories.CourseRepository;
import com.feedback.feedback.repositories.FacultyRepository;
import com.feedback.feedback.services.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response createCourse(CourseRequest courseRequest) {
        if(courseRepository.findByCourseName(courseRequest.getCourseName()).isPresent()){
            throw new AlreadyExistException("Course Already Exist");
        }

        Course course = Course.builder()
                .courseName(courseRequest.getCourseName())
                .createdAt(LocalDateTime.now())
                .build();

        courseRepository.save(course);

        return Response.builder()
                .status(200)
                .message("Course Created successfully")
                .build();
    }

    @Override
    public Response getAllCourses(int page, int size, String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Course> coursePage;

        if (search != null && !search.isEmpty()) {
            coursePage = courseRepository
                    .findByCourseNameContainingIgnoreCase(search, pageable);
        } else {
            coursePage = courseRepository.findAll(pageable);
        }

        List<Course> courses = courseRepository.findAll();
        List<CourseDto> courseDtos = modelMapper.map(courses, new TypeToken<List<CourseDto>>() {}.getType());
        return Response.builder()
                .status(200)
                .message("success")
                .courses(courseDtos)
                .currentPage(coursePage.getNumber())
                .totalPages(coursePage.getTotalPages())
                .totalElements(coursePage.getTotalElements())
                .build();
    }

    @Override
    public Response getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Course Not Found")
        );

        CourseDto courseDto = modelMapper.map(course, CourseDto.class);

        return Response.builder()
                .status(200)
                .message("success")
                .course(courseDto)
                .build();
    }
}