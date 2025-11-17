package mephi.controller;

import lombok.AllArgsConstructor;
import mephi.dto.*;
import mephi.entity.User;
import mephi.service.CourseReviewService;
import mephi.service.EnrollmentService;
import mephi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final EnrollmentService enrollService;
    private final CourseReviewService courseReviewService;

    @RequestMapping("/user")
    public UserDto getUser(@RequestParam("id") Long id) {
        return userService.getUser(id);
    }

    @RequestMapping("/users")
    public List<UserDto> getAll() {
        return userService.getAllUsers();
    }

    @RequestMapping("/user/create")
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @RequestMapping("/user/courses-list")
    public List<CourseDto> getAllCourses(@RequestParam("userId") Long userId) {
        return enrollService.getCoursesByUser(userId);
    }

    @GetMapping("/user/reviews-for-user")
    public List<CourseReviewDto> getReviews(@RequestParam("userId") Long userId) {
        return courseReviewService.getAllReviewByUser(userId);
    }

    @RequestMapping("/user/create-enrollment")
    public EnrollmentDto createEnrollment(@RequestParam("userId") Long userId, @RequestParam("courseId") Long courseId) {
        return enrollService.createEnrollment(userId, courseId);
    }

    @PutMapping("/user/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
