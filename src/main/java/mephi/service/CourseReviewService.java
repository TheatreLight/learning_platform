package mephi.service;

import lombok.AllArgsConstructor;
import mephi.dto.CourseReviewDto;
import mephi.entity.Course;
import mephi.entity.CourseReview;
import mephi.entity.User;
import mephi.mapper.CourseReviewMapper;
import mephi.repository.CourseRepository;
import mephi.repository.CourseReviewRepository;
import mephi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CourseReviewService {
    private CourseReviewRepository crRep;
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private CourseReviewMapper crMapper;

    public CourseReviewDto createReview(CourseReviewDto crDto) {
        CourseReview courseReview = crMapper.toEntity(crDto);
        Course course = courseRepository.findById(crDto.getCourseId())
                .orElseThrow();
        User user = userRepository.findById(crDto.getUserId())
                .orElseThrow();
        courseReview.setCourse(course);
        courseReview.setUser(user);
        CourseReview saved = crRep.save(courseReview);
        return crMapper.toDto(saved);
    }

    public List<CourseReviewDto> getAllReviewByUser(Long userId) {
        var reviews = crRep.findAllByUserId(userId);
        List<CourseReviewDto> resultList = new ArrayList<>();
        for (var item : reviews) {
            resultList.add(crMapper.toDto(item));
        }
        return resultList;
    }

    public List<CourseReviewDto> getAllReviewByCourse(Long courseId) {
        var reviews = crRep.findAllByCourseId(courseId);
        List<CourseReviewDto> resultList = new ArrayList<>();
        for (var item : reviews) {
            resultList.add(crMapper.toDto(item));
        }
        return resultList;
    }
}
