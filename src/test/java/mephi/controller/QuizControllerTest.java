package mephi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mephi.Role;
import mephi.dto.*;
import mephi.entity.Question.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long moduleId;
    private Long studentId;

    @BeforeEach
    void setup() throws Exception {
        // Create category, teacher, course, and module
        CategoryDto category = new CategoryDto();
        category.setName("Test Category");
        String catResponse = mockMvc.perform(post("/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andReturn().getResponse().getContentAsString();

        UserDto teacher = new UserDto();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        String teacherResponse = mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacher)))
                .andReturn().getResponse().getContentAsString();

        UserDto student = new UserDto();
        student.setName("Student");
        student.setEmail("student@test.com");
        student.setRole(Role.STUDENT);
        String studentResponse = mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andReturn().getResponse().getContentAsString();
        studentId = objectMapper.readValue(studentResponse, UserDto.class).getId();

        CourseDto course = new CourseDto();
        course.setTitle("Test Course");
        course.setDescription("Desc");
        course.setDuration(10);
        course.setCategoryId(objectMapper.readValue(catResponse, CategoryDto.class).getId());
        course.setTeacherId(objectMapper.readValue(teacherResponse, UserDto.class).getId());
        String courseResponse = mockMvc.perform(post("/courses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andReturn().getResponse().getContentAsString();

        ModuleDto module = new ModuleDto();
        module.setTitle("Test Module");
        module.setOrderIndex(1);
        module.setCourseId(objectMapper.readValue(courseResponse, CourseDto.class).getId());
        String moduleResponse = mockMvc.perform(post("/modules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(module)))
                .andReturn().getResponse().getContentAsString();
        moduleId = objectMapper.readValue(moduleResponse, ModuleDto.class).getId();
    }

    @Test
    void testCreateQuiz() throws Exception {
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("Quiz 1");
        quizDto.setTimeLimit(30);
        quizDto.setModuleId(moduleId);

        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Quiz 1"))
                .andExpect(jsonPath("$.timeLimit").value(30));
    }

    @Test
    void testGetQuizById() throws Exception {
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("Quiz 1");
        quizDto.setTimeLimit(20);
        quizDto.setModuleId(moduleId);

        String response = mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizDto)))
                .andReturn().getResponse().getContentAsString();
        Long quizId = objectMapper.readValue(response, QuizDto.class).getId();

        mockMvc.perform(get("/api/quizzes/" + quizId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Quiz 1"));
    }

    @Test
    void testCreateQuestion() throws Exception {
        // Create quiz first
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("Quiz 1");
        quizDto.setModuleId(moduleId);
        String quizResponse = mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizDto)))
                .andReturn().getResponse().getContentAsString();
        Long quizId = objectMapper.readValue(quizResponse, QuizDto.class).getId();

        QuestionDto questionDto = new QuestionDto();
        questionDto.setText("What is Java?");
        questionDto.setType(QuestionType.SINGLE_CHOICE);
        questionDto.setQuizId(quizId);

        mockMvc.perform(post("/api/quizzes/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("What is Java?"));
    }

    @Test
    void testGetQuestionsByQuiz() throws Exception {
        // Create quiz first
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("Quiz 1");
        quizDto.setModuleId(moduleId);
        String quizResponse = mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizDto)))
                .andReturn().getResponse().getContentAsString();
        Long quizId = objectMapper.readValue(quizResponse, QuizDto.class).getId();

        // Create question
        QuestionDto questionDto = new QuestionDto();
        questionDto.setText("What is Spring?");
        questionDto.setType(QuestionType.MULTIPLE_CHOICE);
        questionDto.setQuizId(quizId);

        mockMvc.perform(post("/api/quizzes/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionDto)));

        mockMvc.perform(get("/api/quizzes/" + quizId + "/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].text").value("What is Spring?"));
    }

    @Test
    void testCreateAnswerOption() throws Exception {
        // Create quiz and question first
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("Quiz 1");
        quizDto.setModuleId(moduleId);
        String quizResponse = mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizDto)))
                .andReturn().getResponse().getContentAsString();
        Long quizId = objectMapper.readValue(quizResponse, QuizDto.class).getId();

        QuestionDto questionDto = new QuestionDto();
        questionDto.setText("What is Java?");
        questionDto.setType(QuestionType.SINGLE_CHOICE);
        questionDto.setQuizId(quizId);
        String questionResponse = mockMvc.perform(post("/api/quizzes/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDto)))
                .andReturn().getResponse().getContentAsString();
        Long questionId = objectMapper.readValue(questionResponse, QuestionDto.class).getId();

        AnswerOptionDto answerDto = new AnswerOptionDto();
        answerDto.setText("A programming language");
        answerDto.setIsCorrect(true);
        answerDto.setQuestionId(questionId);

        mockMvc.perform(post("/api/quizzes/options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("A programming language"))
                .andExpect(jsonPath("$.isCorrect").value(true));
    }

    @Test
    void testCreateQuizSubmission() throws Exception {
        // Create quiz first
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("Quiz 1");
        quizDto.setModuleId(moduleId);
        String quizResponse = mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizDto)))
                .andReturn().getResponse().getContentAsString();
        Long quizId = objectMapper.readValue(quizResponse, QuizDto.class).getId();

        QuizSubmissionDto submissionDto = new QuizSubmissionDto();
        submissionDto.setQuizId(quizId);
        submissionDto.setStudentId(studentId);
        submissionDto.setScore(85.0);

        mockMvc.perform(post("/api/quizzes/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submissionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(85.0));
    }

    @Test
    void testGetQuizSubmissionsByStudent() throws Exception {
        // Create quiz first
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("Quiz 1");
        quizDto.setModuleId(moduleId);
        String quizResponse = mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizDto)))
                .andReturn().getResponse().getContentAsString();
        Long quizId = objectMapper.readValue(quizResponse, QuizDto.class).getId();

        // Create submission
        QuizSubmissionDto submissionDto = new QuizSubmissionDto();
        submissionDto.setQuizId(quizId);
        submissionDto.setStudentId(studentId);
        submissionDto.setScore(90.0);

        mockMvc.perform(post("/api/quizzes/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submissionDto)));

        mockMvc.perform(get("/api/quizzes/submissions/student/" + studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].score").value(90.0));
    }
}
