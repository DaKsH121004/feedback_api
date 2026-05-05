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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "faculties")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String facultyName;
    @Column(unique = true)
    private String facultyCode;
    @Email
    @Column(unique = true)
    private String facultyEmail;
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
