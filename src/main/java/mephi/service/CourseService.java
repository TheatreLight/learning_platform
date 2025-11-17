package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mephi.dto.CourseDto;
import mephi.dto.UserDto;
import mephi.entity.Category;
import mephi.entity.Course;
import mephi.entity.User;
import mephi.mapper.CourseMapper;
import mephi.mapper.UserMapper;
import mephi.repository.CategoryRepository;
import mephi.repository.CourseRepository;
import mephi.repository.EnrollmentRepository;
import mephi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CourseService {
    private CourseRepository courseRepository;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private EnrollmentRepository enrollRepository;
    private CourseMapper courseMapper;
    private UserMapper userMapper;

    private List<Course> findByCategoryId(Long id) {
        return courseRepository.findByCategoryId(id);
    }

    public List<CourseDto> getList(Long id) {
        List<Course> findedCourses;
        if (id < 0) {
            findedCourses = courseRepository.findAll();
        } else {
            findedCourses = findByCategoryId(id);
        }

        List<CourseDto> coursesDto = new ArrayList<CourseDto>();
        for (var course : findedCourses) {
            coursesDto.add(courseMapper.toDto(course));
        }
        return coursesDto;
    }

    public CourseDto createCourse(CourseDto courseDto) {
        Course course = courseMapper.toEntity(courseDto);

        Category category = categoryRepository.findById(courseDto.getCategoryId())
                .orElseThrow();
        User teacher = userRepository.findById(courseDto.getTeacherId())
                .orElseThrow();
        course.setCategory(category);
        course.setTeacher(teacher);

        Course savedCourse = courseRepository.save(course);
        return courseMapper.toDto(savedCourse);
    }

    public CourseDto updateCourse(Long id, CourseDto courseDto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));

        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setDuration(courseDto.getDuration());

        if (courseDto.getCategoryId() != null && !courseDto.getCategoryId().equals(course.getCategory().getId())) {
            Category category = categoryRepository.findById(courseDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + courseDto.getCategoryId()));
            course.setCategory(category);
        }

        if (courseDto.getTeacherId() != null && !courseDto.getTeacherId().equals(course.getTeacher().getId())) {
            User teacher = userRepository.findById(courseDto.getTeacherId())
                    .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + courseDto.getTeacherId()));
            course.setTeacher(teacher);
        }

        Course updated = courseRepository.save(course);
        return courseMapper.toDto(updated);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new EntityNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    public List<UserDto> getAllUsersByCourse(Long courseId) {
        var users = enrollRepository.findUserByCourseId(courseId);
        List<UserDto> ressultList = new ArrayList<>();
        for (var item : users) {
            ressultList.add(userMapper.toDto(item));
        }
        return ressultList;
    }
}
