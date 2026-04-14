package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.Response;
import com.feedback.feedback.dto.SchoolDto;
import com.feedback.feedback.dto.SchoolRequest;
import com.feedback.feedback.entities.School;
import com.feedback.feedback.exceptions.AlreadyExistException;
import com.feedback.feedback.exceptions.NotFoundException;
import com.feedback.feedback.repositories.SchoolRepository;
import com.feedback.feedback.services.SchoolService;
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
public class SchoolServiceImpl implements SchoolService {
    private final SchoolRepository schoolRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response createSchool(SchoolRequest schoolRequest) {
        if(schoolRepository.findBySchoolName(schoolRequest.getSchoolName()).isPresent()){
            throw new AlreadyExistException("School already exists");
        }

        School school = School.builder()
                .schoolName(schoolRequest.getSchoolName())
                .createdAt(LocalDateTime.now())
                .build();

        schoolRepository.save(school);

        return Response.builder()
                .status(201)
                .message("School created successfully")
                .build();
    }

    @Override
    public Response getAllSchools() {
        List<School> schools = schoolRepository.findAll();

        List<SchoolDto> schoolDtos = modelMapper.map(schools, new TypeToken<List<SchoolDto>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .schools(schoolDtos)
                .build();
    }

    @Override
    public Response getSchoolById(Long id) {
        School school = schoolRepository.findById(id).orElseThrow(
                () -> new NotFoundException("School Not Found")
        );

        SchoolDto schoolDto = modelMapper.map(school, SchoolDto.class);
        return Response.builder()
                .status(200)
                .message("success")
                .school(schoolDto)
                .build();
    }
}
