package mephi.repository;

import mephi.entity.Course;
import mephi.entity.Enrollment;
import mephi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Query("select e.course from Enrollment e where e.user.id = :userId")
    List<Course> findCourseByUserId(Long userId);

    @Query("select e.user from Enrollment e where e.course.id = :courseId")
    List<User> findUserByCourseId(Long courseId);
}
