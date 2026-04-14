package com.feedback.feedback.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "faculties")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is required")
    private String facultyName;
    @NotBlank(message = "Faculty Code is required")
    @Column(unique = true)
    private String facultyCode;
    @NotBlank(message = "Email is required")
    @Email
    @Column(unique = true)
    private String facultyEmail;
    @NotBlank(message = "Phone number is required")
    @Column(unique = true)
    private String facultyPhone;

    private Double averageRating;
    private Integer totalResponses;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "faculty_departments",
            joinColumns = @JoinColumn(name = "faculty_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private List<Department> departments;

    private LocalDateTime createAt;
}
