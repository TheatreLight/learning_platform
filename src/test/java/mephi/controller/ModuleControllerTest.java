package mephi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mephi.Role;
import mephi.dto.CategoryDto;
import mephi.dto.CourseDto;
import mephi.dto.ModuleDto;
import mephi.dto.UserDto;
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
class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long courseId;

    @BeforeEach
    void setup() throws Exception {
        // Create category
        CategoryDto category = new CategoryDto();
        category.setName("Test Category");
        String catResponse = mockMvc.perform(post("/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andReturn().getResponse().getContentAsString();
        CategoryDto createdCategory = objectMapper.readValue(catResponse, CategoryDto.class);

        // Create teacher
        UserDto teacher = new UserDto();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        String userResponse = mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacher)))
                .andReturn().getResponse().getContentAsString();
        UserDto createdTeacher = objectMapper.readValue(userResponse, UserDto.class);

        // Create course
        CourseDto course = new CourseDto();
        course.setTitle("Test Course");
        course.setDescription("Description");
        course.setDuration(10);
        course.setCategoryId(createdCategory.getId());
        course.setTeacherId(createdTeacher.getId());
        String courseResponse = mockMvc.perform(post("/courses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andReturn().getResponse().getContentAsString();
        CourseDto createdCourse = objectMapper.readValue(courseResponse, CourseDto.class);
        courseId = createdCourse.getId();
    }

    @Test
    void testCreateModule() throws Exception {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setTitle("Module 1");
        moduleDto.setDescription("Module Description");
        moduleDto.setOrderIndex(1);
        moduleDto.setCourseId(courseId);

        mockMvc.perform(post("/modules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moduleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Module 1"));
    }

    @Test
    void testGetModulesByCourse() throws Exception {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setTitle("Module 1");
        moduleDto.setOrderIndex(1);
        moduleDto.setCourseId(courseId);

        mockMvc.perform(post("/modules/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moduleDto)));

        mockMvc.perform(get("/modules/get-list").param("course_id", courseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Module 1"));
    }

    @Test
    void testGetModuleById() throws Exception {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setTitle("Module Test");
        moduleDto.setOrderIndex(1);
        moduleDto.setCourseId(courseId);

        String response = mockMvc.perform(post("/modules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moduleDto)))
                .andReturn().getResponse().getContentAsString();

        ModuleDto created = objectMapper.readValue(response, ModuleDto.class);

        mockMvc.perform(get("/modules/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Module Test"));
    }

    @Test
    void testUpdateModule() throws Exception {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setTitle("Old Title");
        moduleDto.setOrderIndex(1);
        moduleDto.setCourseId(courseId);

        String response = mockMvc.perform(post("/modules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moduleDto)))
                .andReturn().getResponse().getContentAsString();

        ModuleDto created = objectMapper.readValue(response, ModuleDto.class);
        created.setTitle("New Title");

        mockMvc.perform(put("/modules/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void testDeleteModule() throws Exception {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setTitle("To Delete");
        moduleDto.setOrderIndex(1);
        moduleDto.setCourseId(courseId);

        String response = mockMvc.perform(post("/modules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moduleDto)))
                .andReturn().getResponse().getContentAsString();

        ModuleDto created = objectMapper.readValue(response, ModuleDto.class);

        mockMvc.perform(delete("/modules/" + created.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/modules/" + created.getId()))
                .andExpect(status().isNotFound());
    }
}
