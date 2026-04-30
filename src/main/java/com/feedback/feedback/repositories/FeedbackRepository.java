package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Boolean existsByStudentRollNoAndFacultyIdAndCourseId(String   studentRollNo, Long  facultyId, Long  courseId);
    @Query("SELECT AVG(f.averageRating) FROM Faculty f")
    Double findAverageRatingFromFaculty();

    @Query("SELECT new com.feedback.feedback.dto.ChartDataDto(d.departmentName, AVG((f.q1 + f.q2 + f.q3 + f.q4 + f.q5) / 5.0)) " +
           "FROM Feedback f JOIN f.department d GROUP BY d.id, d.departmentName")
    List<com.feedback.feedback.dto.ChartDataDto> findDepartmentPerformance();

    @Query("SELECT new com.feedback.feedback.dto.ChartDataDto(d.departmentName, CAST(COUNT(f) AS double)) " +
           "FROM Feedback f JOIN f.department d GROUP BY d.id, d.departmentName")
    List<com.feedback.feedback.dto.ChartDataDto> findFeedbackVolumeByDepartment();

    @Query("SELECT new com.feedback.feedback.dto.ChartDataDto(FUNCTION('DATE_FORMAT', f.createdAt, '%b %Y'), AVG((f.q1 + f.q2 + f.q3 + f.q4 + f.q5) / 5.0)) " +
           "FROM Feedback f GROUP BY FUNCTION('DATE_FORMAT', f.createdAt, '%b %Y') ORDER BY MIN(f.createdAt)")
    List<com.feedback.feedback.dto.ChartDataDto> findMonthlyRatingTrend();
}
