package mephi.repository;

import mephi.Role;
import mephi.entity.Assignment;
import mephi.entity.Category;
import mephi.entity.Course;
import mephi.entity.Lesson;
import mephi.entity.Module;
import mephi.entity.Question;
import mephi.entity.Quiz;
import mephi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for CASCADE operations (persist, merge, remove)
 * Testing requirement: Verify that cascade operations work correctly for entity relationships
 */
@DataJpaTest
@ActiveProfiles("test")
class CascadeOperationsTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void testCascadePersist_CourseWithModules() {
        // Create category and teacher
        Category category = new Category();
        category.setName("Programming");
        entityManager.persist(category);

        User teacher = new User();
        teacher.setName("John Doe");
        teacher.setEmail("john@example.com");
        teacher.setRole(Role.TEACHER);
        entityManager.persist(teacher);

        // Create course with modules (cascade persist)
        Course course = new Course();
        course.setTitle("Java Fundamentals");
        course.setDescription("Learn Java basics");
        course.setDuration(30);
        course.setCategory(category);
        course.setTeacher(teacher);

        Module module1 = new Module();
        module1.setTitle("Introduction");
        module1.setOrderIndex(1);
        module1.setCourse(course);
        course.getModules().add(module1);

        Module module2 = new Module();
        module2.setTitle("Advanced Topics");
        module2.setOrderIndex(2);
        module2.setCourse(course);
        course.getModules().add(module2);

        // Save course (should cascade to modules)
        Course savedCourse = courseRepository.save(course);
        entityManager.flush();
        entityManager.clear();

        // Verify cascade persist worked
        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();
        assertThat(foundCourse.getModules()).hasSize(2);
        assertThat(foundCourse.getModules().get(0).getTitle()).isIn("Introduction", "Advanced Topics");
        assertThat(foundCourse.getModules().get(1).getTitle()).isIn("Introduction", "Advanced Topics");
    }

    @Test
    void testCascadePersist_ModuleWithLessons() {
        // Create necessary parent entities
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

        // Create module with lessons
        Module module = new Module();
        module.setTitle("Module 1");
        module.setOrderIndex(1);
        module.setCourse(course);

        Lesson lesson1 = new Lesson();
        lesson1.setTitle("Lesson 1");
        lesson1.setContent("Content 1");
        lesson1.setOrderIndex(1);
        lesson1.setModule(module);
        module.getLessons().add(lesson1);

        Lesson lesson2 = new Lesson();
        lesson2.setTitle("Lesson 2");
        lesson2.setContent("Content 2");
        lesson2.setOrderIndex(2);
        lesson2.setModule(module);
        module.getLessons().add(lesson2);

        // Save module (should cascade to lessons)
        Module savedModule = moduleRepository.save(module);
        entityManager.flush();
        entityManager.clear();

        // Verify cascade persist worked
        Module foundModule = moduleRepository.findById(savedModule.getId()).orElseThrow();
        assertThat(foundModule.getLessons()).hasSize(2);
        assertThat(foundModule.getLessons()).extracting(Lesson::getTitle)
                .containsExactlyInAnyOrder("Lesson 1", "Lesson 2");
    }

    @Test
    void testCascadePersist_LessonWithAssignments() {
        // Create necessary parent entities
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

        // Create lesson with assignments
        Lesson lesson = new Lesson();
        lesson.setTitle("Java Basics");
        lesson.setContent("Learn variables");
        lesson.setModule(module);

        Assignment assignment1 = new Assignment();
        assignment1.setTitle("Assignment 1");
        assignment1.setDescription("Do exercise 1");
        assignment1.setMaxScore(100);
        assignment1.setLesson(lesson);
        lesson.getAssignments().add(assignment1);

        Assignment assignment2 = new Assignment();
        assignment2.setTitle("Assignment 2");
        assignment2.setDescription("Do exercise 2");
        assignment2.setMaxScore(50);
        assignment2.setLesson(lesson);
        lesson.getAssignments().add(assignment2);

        // Save lesson (should cascade to assignments)
        Lesson savedLesson = lessonRepository.save(lesson);
        entityManager.flush();
        entityManager.clear();

        // Verify cascade persist worked
        Lesson foundLesson = lessonRepository.findById(savedLesson.getId()).orElseThrow();
        assertThat(foundLesson.getAssignments()).hasSize(2);
        assertThat(foundLesson.getAssignments()).extracting(Assignment::getTitle)
                .containsExactlyInAnyOrder("Assignment 1", "Assignment 2");
    }

    @Test
    void testCascadePersist_QuizWithQuestions() {
        // Create necessary parent entities
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

        // Create quiz with questions
        Quiz quiz = new Quiz();
        quiz.setTitle("Java Quiz");
        quiz.setTimeLimit(30);
        quiz.setModule(module);

        Question question1 = new Question();
        question1.setText("What is Java?");
        question1.setType(Question.QuestionType.SINGLE_CHOICE);
        question1.setQuiz(quiz);
        quiz.getQuestions().add(question1);

        Question question2 = new Question();
        question2.setText("What is OOP?");
        question2.setType(Question.QuestionType.MULTIPLE_CHOICE);
        question2.setQuiz(quiz);
        quiz.getQuestions().add(question2);

        // Save quiz (should cascade to questions)
        Quiz savedQuiz = quizRepository.save(quiz);
        entityManager.flush();
        entityManager.clear();

        // Verify cascade persist worked
        Quiz foundQuiz = quizRepository.findById(savedQuiz.getId()).orElseThrow();
        assertThat(foundQuiz.getQuestions()).hasSize(2);
        assertThat(foundQuiz.getQuestions()).extracting(Question::getText)
                .containsExactlyInAnyOrder("What is Java?", "What is OOP?");
    }

    @Test
    void testCascadeDelete_CourseWithModules() {
        // Create course with modules
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
        course.setDuration(10);
        course.setCategory(category);
        course.setTeacher(teacher);

        Module module1 = new Module();
        module1.setTitle("Module 1");
        module1.setOrderIndex(1);
        module1.setCourse(course);
        course.getModules().add(module1);

        Module module2 = new Module();
        module2.setTitle("Module 2");
        module2.setOrderIndex(2);
        module2.setCourse(course);
        course.getModules().add(module2);

        Course savedCourse = courseRepository.save(course);
        Long module1Id = savedCourse.getModules().get(0).getId();
        Long module2Id = savedCourse.getModules().get(1).getId();
        entityManager.flush();
        entityManager.clear();

        // Delete course (should cascade to modules)
        courseRepository.deleteById(savedCourse.getId());
        entityManager.flush();
        entityManager.clear();

        // Verify modules were deleted
        assertThat(moduleRepository.findById(module1Id)).isEmpty();
        assertThat(moduleRepository.findById(module2Id)).isEmpty();
    }

    @Test
    void testCascadeDelete_ModuleWithLessons() {
        // Create module with lessons
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

        Lesson lesson1 = new Lesson();
        lesson1.setTitle("Lesson 1");
        lesson1.setContent("Content");
        lesson1.setModule(module);
        module.getLessons().add(lesson1);

        Lesson lesson2 = new Lesson();
        lesson2.setTitle("Lesson 2");
        lesson2.setContent("Content");
        lesson2.setModule(module);
        module.getLessons().add(lesson2);

        Module savedModule = moduleRepository.save(module);
        Long lesson1Id = savedModule.getLessons().get(0).getId();
        Long lesson2Id = savedModule.getLessons().get(1).getId();
        entityManager.flush();
        entityManager.clear();

        // Delete module (should cascade to lessons)
        moduleRepository.deleteById(savedModule.getId());
        entityManager.flush();
        entityManager.clear();

        // Verify lessons were deleted
        assertThat(lessonRepository.findById(lesson1Id)).isEmpty();
        assertThat(lessonRepository.findById(lesson2Id)).isEmpty();
    }

    @Test
    void testOrphanRemoval_ModuleRemovesLesson() {
        // Create module with lessons
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

        Lesson lesson1 = new Lesson();
        lesson1.setTitle("Lesson 1");
        lesson1.setContent("Content");
        lesson1.setModule(module);
        module.getLessons().add(lesson1);

        Lesson lesson2 = new Lesson();
        lesson2.setTitle("Lesson 2");
        lesson2.setContent("Content");
        lesson2.setModule(module);
        module.getLessons().add(lesson2);

        Module savedModule = moduleRepository.save(module);
        Long lesson1Id = savedModule.getLessons().get(0).getId();
        entityManager.flush();
        entityManager.clear();

        // Remove lesson from collection (should trigger orphan removal)
        Module foundModule = moduleRepository.findById(savedModule.getId()).orElseThrow();
        foundModule.getLessons().removeIf(l -> l.getId().equals(lesson1Id));
        moduleRepository.save(foundModule);
        entityManager.flush();
        entityManager.clear();

        // Verify orphaned lesson was deleted
        assertThat(lessonRepository.findById(lesson1Id)).isEmpty();

        // Verify other lesson still exists
        Module verifyModule = moduleRepository.findById(savedModule.getId()).orElseThrow();
        assertThat(verifyModule.getLessons()).hasSize(1);
        assertThat(verifyModule.getLessons().get(0).getTitle()).isEqualTo("Lesson 2");
    }
}
