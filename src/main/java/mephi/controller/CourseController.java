package mephi.controller;

import lombok.AllArgsConstructor;
import mephi.dto.CourseDto;
import mephi.dto.CourseReviewDto;
import mephi.dto.UserDto;
import mephi.entity.CourseReview;
import mephi.service.CourseReviewService;
import mephi.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final CourseReviewService courseReviewService;

    @GetMapping("/courses/all")
    public List<CourseDto> getAllCourses() {
        return courseService.getList(-1L);
    }

    @GetMapping("/courses/list_by_category")
    public List<CourseDto> getAllByCategory(@RequestParam("category_id") Long id) {
        return courseService.getList(id);
    }

    @GetMapping("/courses/users-for-course")
    public List<UserDto> getCourseUsers(@RequestParam("courseId") Long courseId) {
        return courseService.getAllUsersByCourse(courseId);
    }

    @GetMapping("/courses/reviews-for-course")
    public List<CourseReviewDto> getReviews(@RequestParam("courseId") Long courseId) {
        return courseReviewService.getAllReviewByCourse(courseId);
    }

    @PostMapping("/courses/create-review")
    public CourseReviewDto createReview(@RequestBody CourseReviewDto crDto) {
        return courseReviewService.createReview(crDto);
    }

    @PostMapping("/courses/create")
    public CourseDto createCourse(@RequestBody CourseDto courseDto) {
        return courseService.createCourse(courseDto);
    }

    @DeleteMapping("/courses/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    @PutMapping("/courses/{id}")
    public CourseDto updateCourse(@PathVariable Long id, @RequestBody CourseDto courseDto) {
        return courseService.updateCourse(id, courseDto);
    }
}
