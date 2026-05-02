package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.AdminRegisterRequest;
import com.feedback.feedback.dto.LoginRequest;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.dto.UserDto;
import com.feedback.feedback.entities.User;
import com.feedback.feedback.enums.UserRole;
import com.feedback.feedback.exceptions.AlreadyExistException;
import com.feedback.feedback.exceptions.NotFoundException;
import com.feedback.feedback.repositories.UserRepository;
import com.feedback.feedback.security.JwtUtils;
import com.feedback.feedback.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ModelMapper modelMapper;

    @Override
    public Response registerAdmin(AdminRegisterRequest adminRegisterRequest) {
        if(userRepository.findByEmail(adminRegisterRequest.getEmail()).isPresent()){
            throw new AlreadyExistException("Email Already Exist");
        }

        User user = User.builder()
                .fullName(adminRegisterRequest.getFullName())
                .email(adminRegisterRequest.getEmail())
                .password(passwordEncoder.encode(adminRegisterRequest.getPassword()))
                .role(UserRole.SUPERADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return Response.builder()
                .status(201)
                .message("Admin Registered Successfully")
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        User adminUser = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new NotFoundException("Invalid Credentials")
        );

        if (!passwordEncoder.matches(loginRequest.getPassword(), adminUser.getPassword())) {
            throw new NotFoundException("Invalid Credentials");
        }

        String token = jwtUtils.generateJwtToken(loginRequest.getEmail(), adminUser.getRole().name());

        UserDto adminUserDtos = modelMapper.map(adminUser, UserDto.class);
        return Response.builder()
                .status(200)
                .message("Logged in successfully")
                .user(adminUserDtos)
                .token(token)
                .expiryDate(jwtUtils.getExpirationDateFromToken(token))
                .build();
    }
}
