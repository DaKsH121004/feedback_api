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
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;

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
        if(courseRepository.findByCourseNameIgnoreCase(courseRequest.getCourseName()).isPresent()){
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

    @Override
    public Response processBulkUpload(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            int courseNameIdx = -1;

            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                if (header.equalsIgnoreCase("Course Name")) courseNameIdx = cell.getColumnIndex();
            }

            if (courseNameIdx == -1) {
                return Response.builder().status(400).message("Invalid Excel template. Required column: Course Name").build();
            }

            int successCount = 0;
            java.util.List<String> skippedCourses = new java.util.ArrayList<>();
            java.util.List<String> addedCourses = new java.util.ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String courseName = getCellValue(row.getCell(courseNameIdx));

                if (courseName.isEmpty()) continue;

                if (courseRepository.findByCourseNameIgnoreCase(courseName).isPresent()) {
                    skippedCourses.add(courseName);
                    continue;
                }

                Course course = Course.builder()
                        .courseName(courseName)
                        .createdAt(LocalDateTime.now())
                        .build();

                courseRepository.save(course);
                addedCourses.add(courseName);
                successCount++;
            }

            StringBuilder finalMessage = new StringBuilder();
            finalMessage.append("Bulk upload processed: ")
                    .append(successCount).append(" created, ")
                    .append(skippedCourses.size()).append(" already existed. ");

            if (!skippedCourses.isEmpty()) {
                finalMessage.append("Skipped (already exist): [").append(String.join(", ", skippedCourses)).append("]. ");
            }
            if (!addedCourses.isEmpty()) {
                finalMessage.append("Added courses: [").append(String.join(", ", addedCourses)).append("].");
            }

            return Response.builder()
                    .status(200)
                    .message(finalMessage.toString())
                    .build();
        } catch (Exception e) {
            log.error("Error processing bulk upload", e);
            return Response.builder().status(500).message("Error processing file: " + e.getMessage()).build();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((int) cell.getNumericCellValue());
        return "";
    }
}