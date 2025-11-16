package mephi.service;

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

    public List<UserDto> getAllUsersByCourse(Long courseId) {
        var users = enrollRepository.findUserByCourseId(courseId);
        List<UserDto> ressultList = new ArrayList<>();
        for (var item : users) {
            ressultList.add(userMapper.toDto(item));
        }
        return ressultList;
    }
}
