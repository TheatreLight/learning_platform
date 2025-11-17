package mephi.repository;

import mephi.Role;
import mephi.entity.Assignment;
import mephi.entity.Category;
import mephi.entity.Course;
import mephi.entity.Enrollment;
import mephi.entity.Lesson;
import mephi.entity.Module;
import mephi.entity.Quiz;
import mephi.entity.QuizSubmission;
import mephi.entity.Submission;
import mephi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for data integrity and relationship verification
 * Testing requirement: Verify data integrity and correct relationships between entities
 */
@DataJpaTest
@ActiveProfiles("test")
class DataIntegrityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Test
    void testCourseIntegrity_VerifyModulesCount() {
        // Create course with specific number of modules
        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        Course course = new Course();
        course.setTitle("Java Course");
        course.setDescription("Description");
        course.setDuration(30);
        course.setCategory(category);
        course.setTeacher(teacher);

        // Add exactly 3 modules
        for (int i = 1; i <= 3; i++) {
            Module module = new Module();
            module.setTitle("Module " + i);
            module.setOrderIndex(i);
            module.setCourse(course);
            course.getModules().add(module);
        }

        Course savedCourse = courseRepository.save(course);
        entityManager.flush();
        entityManager.clear();

        // Verify course has exactly 3 modules
        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();
        assertThat(foundCourse.getModules()).hasSize(3);
        assertThat(foundCourse.getModules()).extracting(Module::getOrderIndex)
                .containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    void testEnrollmentIntegrity_VerifyCorrectUserAndCourse() {
        // Create student and course
        User student = new User();
        student.setName("Student");
        student.setEmail("student@test.com");
        student.setRole(Role.STUDENT);
        entityManager.persist(student);

        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        Course course = new Course();
        course.setTitle("Java Course");
        course.setDescription("Description");
        course.setDuration(30);
        course.setCategory(category);
        course.setTeacher(teacher);
        entityManager.persist(course);

        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(student);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDateTime.now().toLocalDate());
        enrollmentRepository.save(enrollment);
        entityManager.flush();
        entityManager.clear();

        // Verify enrollment via User - check enrolled courses
        var enrolledCourses = enrollmentRepository.findCourseByUserId(student.getId());
        assertThat(enrolledCourses).hasSize(1);
        assertThat(enrolledCourses.get(0).getId()).isEqualTo(course.getId());
        assertThat(enrolledCourses.get(0).getTitle()).isEqualTo("Java Course");

        // Verify enrollment via Course - check enrolled users
        var enrolledUsers = enrollmentRepository.findUserByCourseId(course.getId());
        assertThat(enrolledUsers).hasSize(1);
        assertThat(enrolledUsers.get(0).getId()).isEqualTo(student.getId());
        assertThat(enrolledUsers.get(0).getName()).isEqualTo("Student");
    }

    @Test
    void testSubmissionIntegrity_VerifyStudentAndAssignment() {
        // Create student
        User student = new User();
        student.setName("Student");
        student.setEmail("student@test.com");
        student.setRole(Role.STUDENT);
        entityManager.persist(student);

        // Create assignment
        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        Course course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Description");
        course.setDuration(10);
        course.setCategory(category);
        course.setTeacher(teacher);
        entityManager.persist(course);

        Module module = new Module();
        module.setTitle("Module 1");
        module.setOrderIndex(1);
        module.setCourse(course);
        entityManager.persist(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Lesson 1");
        lesson.setContent("Content");
        lesson.setModule(module);
        entityManager.persist(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("Assignment 1");
        assignment.setDescription("Do this");
        assignment.setMaxScore(100);
        assignment.setLesson(lesson);
        entityManager.persist(assignment);

        // Create submission
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent("My solution");
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setScore(85);
        Submission savedSubmission = submissionRepository.save(submission);
        entityManager.flush();
        entityManager.clear();

        // Verify submission references correct student and assignment
        Submission foundSubmission = submissionRepository.findById(savedSubmission.getId()).orElseThrow();
        assertThat(foundSubmission.getStudent().getId()).isEqualTo(student.getId());
        assertThat(foundSubmission.getStudent().getName()).isEqualTo("Student");
        assertThat(foundSubmission.getAssignment().getId()).isEqualTo(assignment.getId());
        assertThat(foundSubmission.getAssignment().getTitle()).isEqualTo("Assignment 1");
        assertThat(foundSubmission.getScore()).isEqualTo(85);
    }

    @Test
    void testModuleHierarchy_VerifyParentCourseReference() {
        // Create course with module
        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        Course course = new Course();
        course.setTitle("Java Course");
        course.setDescription("Description");
        course.setDuration(30);
        course.setCategory(category);
        course.setTeacher(teacher);

        Module module = new Module();
        module.setTitle("Introduction");
        module.setOrderIndex(1);
        module.setCourse(course);
        course.getModules().add(module);

        Course savedCourse = courseRepository.save(course);
        entityManager.flush();
        entityManager.clear();

        // Verify module correctly references parent course
        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();
        Module foundModule = foundCourse.getModules().get(0);
        assertThat(foundModule.getCourse().getId()).isEqualTo(savedCourse.getId());
        assertThat(foundModule.getCourse().getTitle()).isEqualTo("Java Course");
    }

    @Test
    void testQuizSubmissionIntegrity_VerifyQuizAndStudent() {
        // Create student
        User student = new User();
        student.setName("Student");
        student.setEmail("student@test.com");
        student.setRole(Role.STUDENT);
        entityManager.persist(student);

        // Create quiz
        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        Course course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Description");
        course.setDuration(10);
        course.setCategory(category);
        course.setTeacher(teacher);
        entityManager.persist(course);

        Module module = new Module();
        module.setTitle("Module 1");
        module.setOrderIndex(1);
        module.setCourse(course);
        entityManager.persist(module);

        Quiz quiz = new Quiz();
        quiz.setTitle("Java Quiz");
        quiz.setTimeLimit(30);
        quiz.setModule(module);
        entityManager.persist(quiz);

        // Create quiz submission
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setQuiz(quiz);
        quizSubmission.setStudent(student);
        quizSubmission.setScore(92.5);
        quizSubmission.setTakenAt(LocalDateTime.now());
        QuizSubmission savedSubmission = quizSubmissionRepository.save(quizSubmission);
        entityManager.flush();
        entityManager.clear();

        // Verify quiz submission references correct quiz and student
        QuizSubmission foundSubmission = quizSubmissionRepository.findById(savedSubmission.getId()).orElseThrow();
        assertThat(foundSubmission.getQuiz().getId()).isEqualTo(quiz.getId());
        assertThat(foundSubmission.getQuiz().getTitle()).isEqualTo("Java Quiz");
        assertThat(foundSubmission.getStudent().getId()).isEqualTo(student.getId());
        assertThat(foundSubmission.getStudent().getName()).isEqualTo("Student");
        assertThat(foundSubmission.getScore()).isEqualTo(92.5);
    }

    @Test
    void testCategoryToCoursesRelationship() {
        // Create category with multiple courses
        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        Course course1 = new Course();
        course1.setTitle("Java Course");
        course1.setDescription("Description");
        course1.setDuration(30);
        course1.setCategory(category);
        course1.setTeacher(teacher);
        entityManager.persist(course1);

        Course course2 = new Course();
        course2.setTitle("Python Course");
        course2.setDescription("Description");
        course2.setDuration(25);
        course2.setCategory(category);
        course2.setTeacher(teacher);
        entityManager.persist(course2);

        entityManager.flush();
        entityManager.clear();

        // Verify courses belong to correct category
        var courses = courseRepository.findByCategoryId(category.getId());
        assertThat(courses).hasSize(2);
        assertThat(courses).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Java Course", "Python Course");
        assertThat(courses).allMatch(c -> c.getCategory().getId().equals(category.getId()));
    }

    @Test
    void testTeacherToCoursesRelationship() {
        // Create teacher with multiple courses
        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        Course course1 = new Course();
        course1.setTitle("Java Course");
        course1.setDescription("Description");
        course1.setDuration(30);
        course1.setCategory(category);
        course1.setTeacher(teacher);
        entityManager.persist(course1);

        Course course2 = new Course();
        course2.setTitle("Spring Course");
        course2.setDescription("Description");
        course2.setDuration(20);
        course2.setCategory(category);
        course2.setTeacher(teacher);
        entityManager.persist(course2);

        entityManager.flush();
        entityManager.clear();

        // Verify courses belong to correct teacher
        var courses = courseRepository.findByTeacherId(teacher.getId());
        assertThat(courses).hasSize(2);
        assertThat(courses).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Java Course", "Spring Course");
        assertThat(courses).allMatch(c -> c.getTeacher().getId().equals(teacher.getId()));
    }

    @Test
    void testUpdateOperation_CourseDescription() {
        // Create course
        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        Course course = new Course();
        course.setTitle("Java Course");
        course.setDescription("Old Description");
        course.setDuration(30);
        course.setCategory(category);
        course.setTeacher(teacher);
        Course savedCourse = courseRepository.save(course);
        entityManager.flush();
        entityManager.clear();

        // Update course description
        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();
        foundCourse.setDescription("New Description");
        courseRepository.save(foundCourse);
        entityManager.flush();
        entityManager.clear();

        // Verify update persisted
        Course updatedCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();
        assertThat(updatedCourse.getDescription()).isEqualTo("New Description");
        assertThat(updatedCourse.getTitle()).isEqualTo("Java Course"); // Other fields unchanged
    }

    @Test
    void testUpdateOperation_SubmissionScore() {
        // Create submission
        User student = new User();
        student.setName("Student");
        student.setEmail("student@test.com");
        student.setRole(Role.STUDENT);
        entityManager.persist(student);

        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        Course course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Description");
        course.setDuration(10);
        course.setCategory(category);
        course.setTeacher(teacher);
        entityManager.persist(course);

        Module module = new Module();
        module.setTitle("Module 1");
        module.setOrderIndex(1);
        module.setCourse(course);
        entityManager.persist(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Lesson 1");
        lesson.setContent("Content");
        lesson.setModule(module);
        entityManager.persist(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("Assignment 1");
        assignment.setDescription("Do this");
        assignment.setMaxScore(100);
        assignment.setLesson(lesson);
        entityManager.persist(assignment);

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent("My solution");
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setScore(null); // Initially ungraded
        Submission savedSubmission = submissionRepository.save(submission);
        entityManager.flush();
        entityManager.clear();

        // Update submission with score and feedback
        Submission foundSubmission = submissionRepository.findById(savedSubmission.getId()).orElseThrow();
        foundSubmission.setScore(90);
        foundSubmission.setFeedback("Great work!");
        submissionRepository.save(foundSubmission);
        entityManager.flush();
        entityManager.clear();

        // Verify update persisted
        Submission gradedSubmission = submissionRepository.findById(savedSubmission.getId()).orElseThrow();
        assertThat(gradedSubmission.getScore()).isEqualTo(90);
        assertThat(gradedSubmission.getFeedback()).isEqualTo("Great work!");
        assertThat(gradedSubmission.getContent()).isEqualTo("My solution"); // Other fields unchanged
    }
}
