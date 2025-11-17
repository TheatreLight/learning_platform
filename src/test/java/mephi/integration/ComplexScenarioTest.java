package mephi.integration;

import jakarta.persistence.EntityManager;
import mephi.Role;
import mephi.entity.AnswerOption;
import mephi.entity.Assignment;
import mephi.entity.Category;
import mephi.entity.Course;
import mephi.entity.CourseReview;
import mephi.entity.Enrollment;
import mephi.entity.Lesson;
import mephi.entity.Module;
import mephi.entity.Question;
import mephi.entity.Quiz;
import mephi.entity.QuizSubmission;
import mephi.entity.Submission;
import mephi.entity.User;
import mephi.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Complex end-to-end scenario tests
 * Testing requirement: Simulate real-world workflows like creating courses, enrolling students,
 * submitting assignments, and taking quizzes
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ComplexScenarioTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseReviewRepository courseReviewRepository;

    /**
     * Complete scenario:
     * 1. Create teacher and students
     * 2. Create course "Hibernate Basics" with 2 modules
     * 3. Add 1-2 lessons per module
     * 4. Create assignments in lessons
     * 5. Enroll student in course
     * 6. Submit assignment solution
     * 7. Take and submit quiz
     */
    @Test
    void testCompleteCoursePlatformScenario() {
        // Step 1: Create users
        User teacher = new User();
        teacher.setName("Dr. Smith");
        teacher.setEmail("smith@university.com");
        teacher.setRole(Role.TEACHER);
        teacher = userRepository.save(teacher);

        User student1 = new User();
        student1.setName("Alice Johnson");
        student1.setEmail("alice@student.com");
        student1.setRole(Role.STUDENT);
        student1 = userRepository.save(student1);

        User student2 = new User();
        student2.setName("Bob Williams");
        student2.setEmail("bob@student.com");
        student2.setRole(Role.STUDENT);
        student2 = userRepository.save(student2);

        // Step 2: Create category and course
        Category category = new Category();
        category.setName("Database Technologies");
        category = categoryRepository.save(category);

        Course course = new Course();
        course.setTitle("Hibernate Basics");
        course.setDescription("Learn Hibernate ORM framework");
        course.setDuration(40);
        course.setCategory(category);
        course.setTeacher(teacher);

        // Step 3: Create 2 modules with lessons
        Module module1 = new Module();
        module1.setTitle("Introduction to ORM");
        module1.setDescription("Understanding Object-Relational Mapping");
        module1.setOrderIndex(1);
        module1.setCourse(course);

        Lesson lesson1_1 = new Lesson();
        lesson1_1.setTitle("What is ORM?");
        lesson1_1.setContent("ORM stands for Object-Relational Mapping...");
        lesson1_1.setVideoUrl("https://example.com/videos/orm-intro");
        lesson1_1.setOrderIndex(1);
        lesson1_1.setModule(module1);
        module1.getLessons().add(lesson1_1);

        Lesson lesson1_2 = new Lesson();
        lesson1_2.setTitle("Introduction to Hibernate");
        lesson1_2.setContent("Hibernate is a powerful ORM framework...");
        lesson1_2.setOrderIndex(2);
        lesson1_2.setModule(module1);
        module1.getLessons().add(lesson1_2);

        course.getModules().add(module1);

        Module module2 = new Module();
        module2.setTitle("Entity Mapping");
        module2.setDescription("Mapping entities and relationships");
        module2.setOrderIndex(2);
        module2.setCourse(course);

        Lesson lesson2_1 = new Lesson();
        lesson2_1.setTitle("Basic Entity Mapping");
        lesson2_1.setContent("Learn how to map entities using @Entity...");
        lesson2_1.setOrderIndex(1);
        lesson2_1.setModule(module2);
        module2.getLessons().add(lesson2_1);

        course.getModules().add(module2);

        // Save course (cascade saves modules and lessons)
        course = courseRepository.save(course);
        entityManager.flush();

        // Step 4: Create assignments
        Lesson savedLesson1_2 = course.getModules().get(0).getLessons().get(1);

        Assignment assignment1 = new Assignment();
        assignment1.setTitle("Setup Hibernate Project");
        assignment1.setDescription("Create a Maven project with Hibernate dependencies");
        assignment1.setDueDate(LocalDateTime.now().plusDays(7));
        assignment1.setMaxScore(100);
        assignment1.setLesson(savedLesson1_2);
        savedLesson1_2.getAssignments().add(assignment1);

        Assignment assignment2 = new Assignment();
        assignment2.setTitle("Create Entity Classes");
        assignment2.setDescription("Map 3 entity classes with relationships");
        assignment2.setDueDate(LocalDateTime.now().plusDays(14));
        assignment2.setMaxScore(150);
        assignment2.setLesson(savedLesson1_2);
        savedLesson1_2.getAssignments().add(assignment2);

        courseRepository.save(course);
        entityManager.flush();

        // Step 5: Enroll students in course
        Enrollment enrollment1 = new Enrollment();
        enrollment1.setUser(student1);
        enrollment1.setCourse(course);
        enrollment1.setEnrollDate(LocalDateTime.now().toLocalDate());
        enrollmentRepository.save(enrollment1);

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setUser(student2);
        enrollment2.setCourse(course);
        enrollment2.setEnrollDate(LocalDateTime.now().toLocalDate());
        enrollmentRepository.save(enrollment2);

        entityManager.flush();

        // Step 6: Student submits assignment solution
        Assignment savedAssignment1 = savedLesson1_2.getAssignments().get(0);

        Submission submission1 = new Submission();
        submission1.setAssignment(savedAssignment1);
        submission1.setStudent(student1);
        submission1.setContent("I have created the Maven project with the following dependencies: hibernate-core, postgresql...");
        submission1.setSubmittedAt(LocalDateTime.now());
        submissionRepository.save(submission1);

        entityManager.flush();

        // Teacher grades the submission
        submission1.setScore(95);
        submission1.setFeedback("Excellent work! Good dependency management.");
        submissionRepository.save(submission1);

        // Step 7: Create quiz for module
        Module savedModule1 = course.getModules().get(0);

        Quiz quiz = new Quiz();
        quiz.setTitle("ORM Fundamentals Quiz");
        quiz.setTimeLimit(30);
        quiz.setModule(savedModule1);

        Question question1 = new Question();
        question1.setText("What does ORM stand for?");
        question1.setType(Question.QuestionType.SINGLE_CHOICE);
        question1.setQuiz(quiz);

        AnswerOption option1_1 = new AnswerOption();
        option1_1.setText("Object-Relational Mapping");
        option1_1.setIsCorrect(true);
        option1_1.setQuestion(question1);
        question1.getOptions().add(option1_1);

        AnswerOption option1_2 = new AnswerOption();
        option1_2.setText("Object-Resource Management");
        option1_2.setIsCorrect(false);
        option1_2.setQuestion(question1);
        question1.getOptions().add(option1_2);

        quiz.getQuestions().add(question1);

        Question question2 = new Question();
        question2.setText("Which annotations are used in Hibernate? (select all that apply)");
        question2.setType(Question.QuestionType.MULTIPLE_CHOICE);
        question2.setQuiz(quiz);

        AnswerOption option2_1 = new AnswerOption();
        option2_1.setText("@Entity");
        option2_1.setIsCorrect(true);
        option2_1.setQuestion(question2);
        question2.getOptions().add(option2_1);

        AnswerOption option2_2 = new AnswerOption();
        option2_2.setText("@Table");
        option2_2.setIsCorrect(true);
        option2_2.setQuestion(question2);
        question2.getOptions().add(option2_2);

        AnswerOption option2_3 = new AnswerOption();
        option2_3.setText("@Component");
        option2_3.setIsCorrect(false);
        option2_3.setQuestion(question2);
        question2.getOptions().add(option2_3);

        quiz.getQuestions().add(question2);

        quiz = quizRepository.save(quiz);
        entityManager.flush();

        // Step 8: Student takes and submits quiz
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setQuiz(quiz);
        quizSubmission.setStudent(student1);
        quizSubmission.setScore(90.0);
        quizSubmission.setTakenAt(LocalDateTime.now());
        quizSubmissionRepository.save(quizSubmission);

        // Step 9: Student writes course review
        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setUser(student1);
        review.setRating(5);
        review.setReview("Excellent course! Very well structured and easy to follow.");
        courseReviewRepository.save(review);

        entityManager.flush();
        entityManager.clear();

        // VERIFICATION: Verify the entire scenario
        Course verifiedCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(verifiedCourse.getTitle()).isEqualTo("Hibernate Basics");
        assertThat(verifiedCourse.getTeacher().getName()).isEqualTo("Dr. Smith");
        assertThat(verifiedCourse.getModules()).hasSize(2);

        // Verify modules and lessons
        assertThat(verifiedCourse.getModules().get(0).getLessons()).hasSize(2);
        assertThat(verifiedCourse.getModules().get(1).getLessons()).hasSize(1);

        // Verify assignments
        var assignments = assignmentRepository.findByLessonId(savedLesson1_2.getId());
        assertThat(assignments).hasSize(2);

        // Verify enrollments via Course
        var enrolledUsers = enrollmentRepository.findUserByCourseId(course.getId());
        assertThat(enrolledUsers).hasSize(2);

        // Verify submissions
        Submission verifiedSubmission = submissionRepository.findById(submission1.getId()).orElseThrow();
        assertThat(verifiedSubmission.getScore()).isEqualTo(95);
        assertThat(verifiedSubmission.getFeedback()).isEqualTo("Excellent work! Good dependency management.");

        // Verify quiz
        Quiz verifiedQuiz = quizRepository.findById(quiz.getId()).orElseThrow();
        assertThat(verifiedQuiz.getQuestions()).hasSize(2);
        assertThat(verifiedQuiz.getQuestions().get(0).getOptions()).hasSize(2);
        assertThat(verifiedQuiz.getQuestions().get(1).getOptions()).hasSize(3);

        // Verify quiz submission
        var quizSubmissions = quizSubmissionRepository.findByStudentId(student1.getId());
        assertThat(quizSubmissions).hasSize(1);
        assertThat(quizSubmissions.get(0).getScore()).isEqualTo(90.0);

        // Verify course review
        var reviews = courseReviewRepository.findAllByCourseId(course.getId());
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getRating()).isEqualTo(5);
    }

    @Test
    void testMultipleStudentsEnrollmentAndProgress() {
        // Create course structure
        Category category = new Category();
        category.setName("Programming");
        categoryRepository.save(category);

        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        userRepository.save(teacher);

        Course course = new Course();
        course.setTitle("Java Programming");
        course.setDescription("Learn Java");
        course.setDuration(30);
        course.setCategory(category);
        course.setTeacher(teacher);
        courseRepository.save(course);

        // Create 3 students
        User student1 = new User();
        student1.setName("Student 1");
        student1.setEmail("student1@test.com");
        student1.setRole(Role.STUDENT);
        userRepository.save(student1);

        User student2 = new User();
        student2.setName("Student 2");
        student2.setEmail("student2@test.com");
        student2.setRole(Role.STUDENT);
        userRepository.save(student2);

        User student3 = new User();
        student3.setName("Student 3");
        student3.setEmail("student3@test.com");
        student3.setRole(Role.STUDENT);
        userRepository.save(student3);

        // Enroll all students
        Enrollment e1 = new Enrollment();
        e1.setUser(student1);
        e1.setCourse(course);
        e1.setEnrollDate(LocalDateTime.now().minusDays(10).toLocalDate());
        enrollmentRepository.save(e1);

        Enrollment e2 = new Enrollment();
        e2.setUser(student2);
        e2.setCourse(course);
        e2.setEnrollDate(LocalDateTime.now().minusDays(5).toLocalDate());
        enrollmentRepository.save(e2);

        Enrollment e3 = new Enrollment();
        e3.setUser(student3);
        e3.setCourse(course);
        e3.setEnrollDate(LocalDateTime.now().minusDays(2).toLocalDate());
        enrollmentRepository.save(e3);

        entityManager.flush();

        // Verify all students enrolled
        var enrolledUsers = enrollmentRepository.findUserByCourseId(course.getId());
        assertThat(enrolledUsers).hasSize(3);
        assertThat(enrolledUsers).extracting(User::getName)
                .containsExactlyInAnyOrder("Student 1", "Student 2", "Student 3");
    }

    @Test
    void testTeacherManagesMultipleCourses() {
        // Create teacher
        User teacher = new User();
        teacher.setName("Prof. Johnson");
        teacher.setEmail("johnson@university.com");
        teacher.setRole(Role.TEACHER);
        userRepository.save(teacher);

        Category category1 = new Category();
        category1.setName("Programming");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Databases");
        categoryRepository.save(category2);

        // Teacher creates 3 courses
        Course course1 = new Course();
        course1.setTitle("Java Basics");
        course1.setDescription("Introduction to Java");
        course1.setDuration(20);
        course1.setCategory(category1);
        course1.setTeacher(teacher);
        courseRepository.save(course1);

        Course course2 = new Course();
        course2.setTitle("Advanced Java");
        course2.setDescription("Advanced Java topics");
        course2.setDuration(30);
        course2.setCategory(category1);
        course2.setTeacher(teacher);
        courseRepository.save(course2);

        Course course3 = new Course();
        course3.setTitle("Database Design");
        course3.setDescription("Learn database design");
        course3.setDuration(25);
        course3.setCategory(category2);
        course3.setTeacher(teacher);
        courseRepository.save(course3);

        entityManager.flush();

        // Verify teacher teaches all 3 courses
        var teacherCourses = courseRepository.findByTeacherId(teacher.getId());
        assertThat(teacherCourses).hasSize(3);
        assertThat(teacherCourses).extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Java Basics", "Advanced Java", "Database Design");
    }
}
