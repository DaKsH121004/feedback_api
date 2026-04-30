package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.AssignmentDto;
import com.feedback.feedback.dto.AssignmentRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.entities.Course;
import com.feedback.feedback.entities.Department;
import com.feedback.feedback.entities.Faculty;
import com.feedback.feedback.entities.FacultyCourseAssignment;
import com.feedback.feedback.exceptions.AlreadyExistException;
import com.feedback.feedback.exceptions.NotFoundException;
import com.feedback.feedback.repositories.CourseRepository;
import com.feedback.feedback.repositories.DepartmentRepository;
import com.feedback.feedback.repositories.FacultyCourseAssignmentRepository;
import com.feedback.feedback.repositories.FacultyRepository;
import com.feedback.feedback.services.FacultyCourseAssignmentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacultyCourseAssignmentServiceImpl implements FacultyCourseAssignmentService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final FacultyCourseAssignmentRepository assignmentRepository;

    @Override
    public Response createAssignment(AssignmentRequest request) {


        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Faculty not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Course not found"));

        boolean belongs = faculty.getDepartments()
                .stream()
                .anyMatch(d -> d.getId().equals(request.getDepartmentId()));

        if (!belongs) {
            throw new NotFoundException("Faculty does not belong to this department");
        }

        boolean alreadyAssigned = assignmentRepository
                .existsByFacultyIdAndDepartmentIdAndCourseId(
                        request.getFacultyId(),
                        request.getDepartmentId(),
                        request.getCourseId()
                );

        if (alreadyAssigned) {
            throw new AlreadyExistException("Already Assigned");
        }


        FacultyCourseAssignment assignment = FacultyCourseAssignment.builder()
                .faculty(faculty)
                .department(department)
                .course(course)
                .build();

        assignmentRepository.save(assignment);

        return Response.builder()
                .status(201)
                .message("Assignment created successfully")
                .build();
    }

    @Override
    public Response updateAssignment(Long id, AssignmentRequest request) {

        FacultyCourseAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        boolean alreadyAssigned = assignmentRepository
                .existsByFacultyIdAndDepartmentIdAndCourseId(
                        request.getFacultyId(),
                        request.getDepartmentId(),
                        request.getCourseId()
                );

        if (alreadyAssigned) {
            throw new AlreadyExistException("Already Assigned");
        }

        Faculty faculty = facultyRepository.findById(request.getFacultyId()).orElseThrow();
        Department department = departmentRepository.findById(request.getDepartmentId()).orElseThrow();
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow();

        boolean belongs = faculty.getDepartments()
                .stream()
                .anyMatch(d -> d.getId().equals(request.getDepartmentId()));

        if (!belongs) {
            throw new NotFoundException("Faculty does not belong to this department");
        }

        assignment.setFaculty(faculty);
        assignment.setDepartment(department);
        assignment.setCourse(course);

        assignmentRepository.save(assignment);

        return Response.builder()
                .status(200)
                .message("Assignment updated successfully")
                .build();
    }

    @Override
    public Response deleteAssignment(Long id) {

        if (!assignmentRepository.existsById(id)) {
            throw new NotFoundException("Assignment not found");
        }

        assignmentRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Assignment deleted successfully")
                .build();
    }

    @Override
    public Response getAllAssignments() {

        List<FacultyCourseAssignment> list = assignmentRepository.findAll();

        List<AssignmentDto> dtoList = list.stream().map(a ->
                AssignmentDto.builder()
                        .id(a.getId())
                        .facultyId(a.getFaculty().getId())
                        .facultyName(a.getFaculty().getFacultyName())
                        .departmentId(a.getDepartment().getId())
                        .departmentName(a.getDepartment().getDepartmentName())
                        .courseId(a.getCourse().getId())
                        .courseName(a.getCourse().getCourseName())
                        .build()
        ).toList();

        return Response.builder()
                .status(200)
                .message("Assignments fetched successfully")
                .assignments(dtoList)
                .build();
    }

    @Override
    public Response processBulkUpload(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            int sectionIdx = -1;
            int courseNameIdx = -1;
            int facultyNameIdx = -1;

            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                if (header.equalsIgnoreCase("Section")) sectionIdx = cell.getColumnIndex();
                if (header.equalsIgnoreCase("Course Name")) courseNameIdx = cell.getColumnIndex();
                if (header.equalsIgnoreCase("Faculty Name")) facultyNameIdx = cell.getColumnIndex();
            }

            if (sectionIdx == -1 || courseNameIdx == -1 || facultyNameIdx == -1) {
                return Response.builder().status(400).message("Invalid Excel template. Required columns: Section, Course Name, Faculty Name").build();
            }

            int successCount = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String section = getCellValue(row.getCell(sectionIdx));
                String courseName = getCellValue(row.getCell(courseNameIdx));
                String facultyName = getCellValue(row.getCell(facultyNameIdx));

                if (section.isEmpty() || courseName.isEmpty() || facultyName.isEmpty()) continue;

                Faculty faculty = facultyRepository.findByFacultyName(facultyName).orElse(null);
                Department department = departmentRepository.findByDepartmentName(section).orElse(null);
                Course course = courseRepository.findByCourseName(courseName).orElse(null);

                if (faculty != null && department != null && course != null) {
                    boolean alreadyAssigned = assignmentRepository.existsByFacultyIdAndDepartmentIdAndCourseId(
                            faculty.getId(), department.getId(), course.getId());
                    
                    if (!alreadyAssigned) {
                        FacultyCourseAssignment assignment = FacultyCourseAssignment.builder()
                                .faculty(faculty)
                                .department(department)
                                .course(course)
                                .build();
                        assignmentRepository.save(assignment);
                        successCount++;
                    }
                }
            }

            return Response.builder()
                    .status(200)
                    .message("Bulk upload processed successfully. " + successCount + " assignments created.")
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
