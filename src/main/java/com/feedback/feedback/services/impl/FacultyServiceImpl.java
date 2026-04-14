package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.FacultyDto;
import com.feedback.feedback.dto.FacultyRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.entities.Department;
import com.feedback.feedback.entities.Faculty;
import com.feedback.feedback.exceptions.AlreadyExistException;
import com.feedback.feedback.exceptions.NotFoundException;
import com.feedback.feedback.repositories.DepartmentRepository;
import com.feedback.feedback.repositories.FacultyRepository;
import com.feedback.feedback.services.FacultyService;
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
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;
    private final ModelMapper modelMapper;
    private final DepartmentRepository departmentRepository;

    @Override
    public Response createFaculty(FacultyRequest facultyRequest) {
        if(facultyRepository.findByFacultyCode(facultyRequest.getFacultyCode()).isPresent()){
            throw new AlreadyExistException("Faculty already exist");
        }

        if (facultyRepository.findByFacultyPhoneAndFacultyEmail(facultyRequest.getFacultyPhone(), facultyRequest.getFacultyEmail()).isPresent()){
            throw new AlreadyExistException("Faculty already exist");
        }

        List<Department> departments = departmentRepository
                .findAllById(facultyRequest.getDepartmentId());

        if (departments.isEmpty()) {
            throw new NotFoundException("Department not found");
        }

        Faculty faculty = Faculty.builder()
                .facultyName(facultyRequest.getFacultyName())
                .facultyCode(facultyRequest.getFacultyCode())
                .facultyPhone(facultyRequest.getFacultyPhone())
                .facultyEmail(facultyRequest.getFacultyEmail())
                .departments(departments)
                .createAt(LocalDateTime.now())
                .build();

        facultyRepository.save(faculty);
        return Response.builder()
                .status(201)
                .message("Faculty created successfully")
                .build();
    }

    @Override
    public Response getAllFaculties() {
        List<Faculty> faculties = facultyRepository.findAll();
        List<FacultyDto> facultyDtos = modelMapper.map(faculties, new TypeToken<List<FacultyDto>>() {}.getType());
        return Response.builder()
                .status(200)
                .message("success")
                .faculties(facultyDtos)
                .build();
    }

    @Override
    public Response getFacultyById(Long id) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Faculty Not Found")
        );

        FacultyDto facultyDto = modelMapper.map(faculty, FacultyDto.class);
        return Response.builder()
                .status(200)
                .message("success")
                .faculty(facultyDto)
                .build();
    }
}
