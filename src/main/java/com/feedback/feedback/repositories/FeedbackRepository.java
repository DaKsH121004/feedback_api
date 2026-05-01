package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Boolean existsByStudentRollNoAndFacultyIdAndCourseId(String   studentRollNo, Long  facultyId, Long  courseId);
    
    @org.springframework.transaction.annotation.Transactional
    void deleteByCourseId(Long courseId);

    @Query("SELECT AVG(f.averageRating) FROM Faculty f")
    Double findAverageRatingFromFaculty();

    @Query("SELECT d.departmentName as label, AVG((f.q1 + f.q2 + f.q3 + f.q4 + f.q5) / 5.0) as value " +
           "FROM Feedback f JOIN f.department d GROUP BY d.id, d.departmentName")
    List<com.feedback.feedback.dto.ChartDataProjection> findDepartmentPerformance();

    @Query("SELECT d.departmentName as label, COUNT(f) as value " +
           "FROM Feedback f JOIN f.department d GROUP BY d.id, d.departmentName")
    List<com.feedback.feedback.dto.ChartDataProjection> findFeedbackVolumeByDepartment();

    @Query("SELECT FUNCTION('DATE_FORMAT', f.createdAt, '%b %Y') as label, AVG((f.q1 + f.q2 + f.q3 + f.q4 + f.q5) / 5.0) as value " +
           "FROM Feedback f GROUP BY FUNCTION('DATE_FORMAT', f.createdAt, '%b %Y') ORDER BY MIN(f.createdAt)")
    List<com.feedback.feedback.dto.ChartDataProjection> findMonthlyRatingTrend();
}
