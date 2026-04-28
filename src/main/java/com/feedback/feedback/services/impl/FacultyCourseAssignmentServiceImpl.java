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
}
