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
import com.feedback.feedback.repositories.FacultyCourseAssignmentRepository;
import com.feedback.feedback.repositories.FeedbackRepository;
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
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DateUtil;
import java.io.InputStream;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final com.feedback.feedback.repositories.DepartmentRepository departmentRepository;
    private final FacultyCourseAssignmentRepository facultyCourseAssignmentRepository;
    private final FeedbackRepository feedbackRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response createCourse(CourseRequest courseRequest) {
        if(courseRepository.findByCourseNameIgnoreCase(courseRequest.getCourseName()).isPresent()){
            throw new AlreadyExistException("Course Already Exist");
        }

        Course.CourseBuilder courseBuilder = Course.builder()
                .courseName(courseRequest.getCourseName())
                .createdAt(LocalDateTime.now());

        if (courseRequest.getDepartmentId() != null) {
            com.feedback.feedback.entities.Department department = departmentRepository.findById(courseRequest.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department Not Found"));
            courseBuilder.department(department);
        }

        courseRepository.save(courseBuilder.build());

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
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                return Response.builder().status(400).message("The uploaded file is empty.").build();
            }

            int courseNameIdx = -1;
            int departmentIdx = -1;

            for (Cell cell : headerRow) {
                if (cell.getCellType() == CellType.STRING) {
                    String header = cell.getStringCellValue().trim().toLowerCase().replaceAll("\\s", "");
                    if (header.equals("coursename") || header.equals("course") || header.equals("coursetitle")) {
                        courseNameIdx = cell.getColumnIndex();
                    }
                    if (header.contains("department") || header.equals("dept")) {
                        departmentIdx = cell.getColumnIndex();
                    }
                }
            }

            if (courseNameIdx == -1) {
                return Response.builder().status(400).message("Invalid Excel template. Required column: 'Course Name' or 'Course'").build();
            }

            // Fetch departments for mapping if department column exists
            java.util.Map<String, com.feedback.feedback.entities.Department> deptMap = new java.util.TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            if (departmentIdx != -1) {
                departmentRepository.findAll().forEach(d -> deptMap.put(d.getDepartmentName().trim(), d));
            }

            int successCount = 0;
            java.util.List<String> skippedCourses = new java.util.ArrayList<>();
            java.util.List<String> addedCourses = new java.util.ArrayList<>();

            // Iterate through all rows using the row iterator for better reliability
            for (Row row : sheet) {
                // Skip header row
                if (row.getRowNum() == 0) continue;

                String courseName = getCellValue(row.getCell(courseNameIdx));

                if (courseName == null || courseName.trim().isEmpty()) continue;

                courseName = courseName.trim();

                if (courseRepository.findByCourseNameIgnoreCase(courseName).isPresent()) {
                    skippedCourses.add(courseName);
                    continue;
                }

                Course.CourseBuilder courseBuilder = Course.builder()
                        .courseName(courseName)
                        .createdAt(LocalDateTime.now());

                if (departmentIdx != -1) {
                    String deptName = getCellValue(row.getCell(departmentIdx)).trim();
                    if (!deptName.isEmpty()) {
                        com.feedback.feedback.entities.Department department = deptMap.get(deptName);
                        if (department != null) {
                            courseBuilder.department(department);
                        }
                    }
                }

                courseRepository.save(courseBuilder.build());
                addedCourses.add(courseName);
                successCount++;
            }

            StringBuilder finalMessage = new StringBuilder();
            finalMessage.append("Bulk upload processed: ")
                    .append(successCount).append(" created, ")
                    .append(skippedCourses.size()).append(" already existed.");

            if (!skippedCourses.isEmpty()) {
                finalMessage.append(" Skipped (already exist): [").append(String.join(", ", skippedCourses)).append("].");
            }
            if (!addedCourses.isEmpty()) {
                finalMessage.append(" Added courses: [").append(String.join(", ", addedCourses)).append("].");
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

    @Override
    public Response updateCourse(Long id, CourseRequest courseRequest) {
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Course Not Found")
        );
        course.setCourseName(courseRequest.getCourseName());
        
        if (courseRequest.getDepartmentId() != null) {
            com.feedback.feedback.entities.Department department = departmentRepository.findById(courseRequest.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department Not Found"));
            course.setDepartment(department);
        }
        
        courseRepository.save(course);
        return Response.builder().status(200).message("Course Updated successfully").build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Response deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NotFoundException("Course Not Found");
        }
        facultyCourseAssignmentRepository.deleteByCourseId(id);
        feedbackRepository.deleteByCourseId(id);
        courseRepository.deleteById(id);
        return Response.builder().status(200).message("Course Deleted successfully").build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Response deleteAllCourses() {
        facultyCourseAssignmentRepository.deleteAll();
        feedbackRepository.deleteAll();
        courseRepository.deleteAll();
        return Response.builder().status(200).message("All Courses Deleted successfully").build();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}