package mephi.service;

import lombok.AllArgsConstructor;
import mephi.EnrollStatus;
import mephi.dto.CourseDto;
import mephi.dto.EnrollmentDto;
import mephi.entity.Course;
import mephi.entity.Enrollment;
import mephi.entity.User;
import mephi.mapper.CourseMapper;
import mephi.mapper.EnrollmentMapper;
import mephi.repository.CourseRepository;
import mephi.repository.EnrollmentRepository;
import mephi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class EnrollmentService {
    private EnrollmentRepository enrollmentRepository;
    private UserRepository userRepository;
    private CourseRepository courseRepository;
    private EnrollmentMapper enrollMapper;
    private CourseMapper courseMapper;

    public EnrollmentDto createEnrollment(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        Course course = courseRepository.findById(courseId)
                .orElseThrow();
        Enrollment enroll = new Enrollment();

        enroll.setUser(user);
        enroll.setCourse(course);

        enroll.setEnrollDate(LocalDate.now());
        enroll.setStatus(EnrollStatus.Active);

        Enrollment enrollSaved = enrollmentRepository.save(enroll);
        return enrollMapper.toDto(enrollSaved);
    }

    public List<CourseDto> getCoursesByUser(Long userId) {
        var courses = enrollmentRepository.findCourseByUserId(userId);
        List<CourseDto> resultList = new ArrayList<>();
        for (var item : courses) {
            resultList.add(courseMapper.toDto(item));
        }
        return resultList;
    }

//    public EnrollmentDto updateEnrollStatus(Long id, EnrollStatus status) {
//
//    }
}
