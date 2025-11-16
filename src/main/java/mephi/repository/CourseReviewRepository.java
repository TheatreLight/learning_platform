package mephi.repository;

import mephi.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    List<CourseReview> findAllByUserId(Long userId);
    List<CourseReview> findAllByCourseId(Long courseId);
}
