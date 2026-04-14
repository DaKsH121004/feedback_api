package com.feedback.feedback.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private String studentRollNo;
    private String studentEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schoolId")
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId")
    private Department department;

    private Integer semester;
    private String classSection;

    @ManyToOne
    @JoinColumn(name = "facultyId")
    private Faculty faculty;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private Integer q1;
    private Integer q2;
    private Integer q3;
    private Integer q4;
    private Integer q5;

    private String remarks;

    private LocalDateTime createdAt;
}
