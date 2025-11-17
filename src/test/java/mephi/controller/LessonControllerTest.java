package mephi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mephi.Role;
import mephi.dto.*;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LessonControllerTest {

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
    void testCreateLesson() throws Exception {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setTitle("Lesson 1");
        lessonDto.setContent("Content");
        lessonDto.setModuleId(moduleId);

        mockMvc.perform(post("/api/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Lesson 1"));
    }

    @Test
    void testGetLessonsByModule() throws Exception {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setTitle("Lesson 1");
        lessonDto.setContent("Content");
        lessonDto.setModuleId(moduleId);

        mockMvc.perform(post("/api/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lessonDto)));

        mockMvc.perform(get("/api/lessons/module/" + moduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Lesson 1"));
    }

    @Test
    void testCreateAssignment() throws Exception {
        // Create lesson first
        LessonDto lessonDto = new LessonDto();
        lessonDto.setTitle("Lesson 1");
        lessonDto.setModuleId(moduleId);
        String lessonResponse = mockMvc.perform(post("/api/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonDto)))
                .andReturn().getResponse().getContentAsString();
        Long lessonId = objectMapper.readValue(lessonResponse, LessonDto.class).getId();

        AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setTitle("Assignment 1");
        assignmentDto.setDescription("Do this");
        assignmentDto.setMaxScore(100);
        assignmentDto.setLessonId(lessonId);

        mockMvc.perform(post("/api/lessons/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Assignment 1"));
    }

    @Test
    void testCreateSubmission() throws Exception {
        // Create lesson and assignment
        LessonDto lessonDto = new LessonDto();
        lessonDto.setTitle("Lesson 1");
        lessonDto.setModuleId(moduleId);
        String lessonResponse = mockMvc.perform(post("/api/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonDto)))
                .andReturn().getResponse().getContentAsString();
        Long lessonId = objectMapper.readValue(lessonResponse, LessonDto.class).getId();

        AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setTitle("Assignment 1");
        assignmentDto.setMaxScore(100);
        assignmentDto.setLessonId(lessonId);
        String assignmentResponse = mockMvc.perform(post("/api/lessons/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentDto)))
                .andReturn().getResponse().getContentAsString();
        Long assignmentId = objectMapper.readValue(assignmentResponse, AssignmentDto.class).getId();

        SubmissionDto submissionDto = new SubmissionDto();
        submissionDto.setAssignmentId(assignmentId);
        submissionDto.setStudentId(studentId);
        submissionDto.setContent("My submission");

        mockMvc.perform(post("/api/lessons/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submissionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("My submission"));
    }
}
