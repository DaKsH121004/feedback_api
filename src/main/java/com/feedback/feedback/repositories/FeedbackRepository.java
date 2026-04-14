package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Boolean existsByStudentRollNoAndFacultyIdAndCourseId(String   studentRollNo, Long  facultyId, Long  courseId);
    @Query("SELECT AVG(f.averageRating) FROM Faculty f")
    Double findAverageRatingFromFaculty();
}
