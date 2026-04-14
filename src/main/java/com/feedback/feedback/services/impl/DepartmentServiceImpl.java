package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.DepartmentDto;
import com.feedback.feedback.dto.DepartmentRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.entities.Department;
import com.feedback.feedback.entities.School;
import com.feedback.feedback.exceptions.AlreadyExistException;
import com.feedback.feedback.exceptions.NotFoundException;
import com.feedback.feedback.repositories.DepartmentRepository;
import com.feedback.feedback.repositories.SchoolRepository;
import com.feedback.feedback.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final SchoolRepository schoolRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response createDepartment(DepartmentRequest departmentRequest) {
        if(departmentRepository.findByDepartmentNameAndSchoolId(departmentRequest.getDepartmentName(),departmentRequest.getSchoolId()).isPresent()){
            throw new AlreadyExistException("Department with name already exists");
        }

        School school = schoolRepository.findById(departmentRequest.getSchoolId()).orElseThrow(
                () -> new NotFoundException("School not found")
        );

        Department department = Department.builder()
                .departmentName(departmentRequest.getDepartmentName())
                .school(school)
                .createdAt(LocalDateTime.now())
                .build();

        departmentRepository.save(department);
        return Response.builder()
                .status(201)
                .message("Department created successfully")
                .build();
    }

    @Override
    public Response getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentDto> departmentDtos = modelMapper.map(departments, new TypeToken<List<DepartmentDto>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .departments(departmentDtos)
                .build();
    }

    @Override
    public Response getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Department not found")
        );

        DepartmentDto departmentDto = modelMapper.map(department, DepartmentDto.class);
        return Response.builder()
                .status(200)
                .message("success")
                .department(departmentDto)
                .build();
    }
}
