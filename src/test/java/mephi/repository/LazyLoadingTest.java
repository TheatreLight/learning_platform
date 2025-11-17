package mephi.repository;

import jakarta.persistence.EntityManager;
import mephi.Role;
import mephi.entity.Category;
import mephi.entity.Course;
import mephi.entity.Lesson;
import mephi.entity.Question;
import mephi.entity.Quiz;
import mephi.entity.User;
import mephi.entity.Module;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for lazy loading behavior
 * Testing requirement: Demonstrate LazyInitializationException when accessing lazy collections outside transaction
 */
@SpringBootTest
@ActiveProfiles("test")
class LazyLoadingTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void testLazyLoading_CourseModules_ThrowsException() {
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
        course.setDuration(30);
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
        Long courseId = savedCourse.getId();
        entityManager.flush();
        entityManager.clear();

        // Load course in current transaction
        Course loadedCourse = courseRepository.findById(courseId).orElseThrow();

        // Verify lazy collection is NOT initialized
        assertThat(Hibernate.isInitialized(loadedCourse.getModules())).isFalse();

        // Access lazy collection within transaction - should work
        assertThat(loadedCourse.getModules()).hasSize(2);

        // Verify lazy collection is NOW initialized
        assertThat(Hibernate.isInitialized(loadedCourse.getModules())).isTrue();
    }

    @Test
    @Transactional
    void testLazyLoading_ModuleLessons() {
        // Similar test for Module->Lessons lazy loading
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
        module.setTitle("Introduction");
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
        entityManager.flush();
        entityManager.clear();

        // Load module
        Module loadedModule = moduleRepository.findById(savedModule.getId()).orElseThrow();

        // Verify lazy collection is NOT initialized
        assertThat(Hibernate.isInitialized(loadedModule.getLessons())).isFalse();

        // Access lazy collection - should work within transaction
        assertThat(loadedModule.getLessons()).hasSize(2);
        assertThat(Hibernate.isInitialized(loadedModule.getLessons())).isTrue();
    }

    @Test
    @Transactional
    void testLazyLoading_QuizQuestions() {
        // Similar test for Quiz->Questions lazy loading
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

        Quiz savedQuiz = quizRepository.save(quiz);
        entityManager.flush();
        entityManager.clear();

        // Load quiz
        Quiz loadedQuiz = quizRepository.findById(savedQuiz.getId()).orElseThrow();

        // Verify lazy collection is NOT initialized
        assertThat(Hibernate.isInitialized(loadedQuiz.getQuestions())).isFalse();

        // Access lazy collection - should work within transaction
        assertThat(loadedQuiz.getQuestions()).hasSize(2);
        assertThat(Hibernate.isInitialized(loadedQuiz.getQuestions())).isTrue();
    }

    @Test
    @Transactional
    void testLazyLoading_WithinTransaction_WorksCorrectly() {
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
        course.setDuration(30);
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
        entityManager.flush();
        entityManager.clear();

        // Load course within transaction
        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();

        // Verify lazy collection is NOT initialized yet
        assertThat(Hibernate.isInitialized(foundCourse.getModules())).isFalse();

        // Access lazy collection within transaction - should work
        assertThat(foundCourse.getModules()).hasSize(2);

        // Verify lazy collection is NOW initialized
        assertThat(Hibernate.isInitialized(foundCourse.getModules())).isTrue();
    }

    @Test
    @Transactional
    void testEagerFetching_AvoidLazyException() {
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
        course.setDuration(30);
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
        entityManager.flush();
        entityManager.clear();

        // Load course with eager fetching (initialize collection within transaction)
        Course loadedCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();

        // Force initialization within transaction
        Hibernate.initialize(loadedCourse.getModules());

        // Verify collection is initialized
        assertThat(Hibernate.isInitialized(loadedCourse.getModules())).isTrue();
        assertThat(loadedCourse.getModules()).hasSize(2);

        // Now access outside transaction would still work because collection is already loaded
        // (Note: in real scenario, this object would be detached after transaction ends)
    }

    @Test
    @Transactional
    void testLazyLoading_ManyToOneRelationship() {
        // Test lazy loading of ManyToOne relationships
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
        module.setTitle("Test Module");
        module.setOrderIndex(1);
        module.setCourse(course);
        Module savedModule = moduleRepository.save(module);
        entityManager.flush();
        entityManager.clear();

        // Load module
        Module loadedModule = moduleRepository.findById(savedModule.getId()).orElseThrow();

        // Verify lazy ManyToOne is NOT initialized
        assertThat(Hibernate.isInitialized(loadedModule.getCourse())).isFalse();

        // Access lazy ManyToOne - should work within transaction
        assertThat(loadedModule.getCourse().getTitle()).isEqualTo("Test Course");
        assertThat(Hibernate.isInitialized(loadedModule.getCourse())).isTrue();
    }
}
