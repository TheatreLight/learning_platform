package mephi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mephi.Role;
import mephi.dto.CategoryDto;
import mephi.dto.CourseDto;
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
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long categoryId;
    private Long teacherId;

    @BeforeEach
    void setup() throws Exception {
        CategoryDto category = new CategoryDto();
        category.setName("Programming");
        String catResponse = mockMvc.perform(post("/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andReturn().getResponse().getContentAsString();
        categoryId = objectMapper.readValue(catResponse, CategoryDto.class).getId();

        UserDto teacher = new UserDto();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setRole(Role.TEACHER);
        String teacherResponse = mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacher)))
                .andReturn().getResponse().getContentAsString();
        teacherId = objectMapper.readValue(teacherResponse, UserDto.class).getId();
    }

    @Test
    void testCreateCourse() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("Java Basics");
        courseDto.setDescription("Learn Java");
        courseDto.setDuration(30);
        courseDto.setCategoryId(categoryId);
        courseDto.setTeacherId(teacherId);

        mockMvc.perform(post("/courses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Java Basics"));
    }

    @Test
    void testGetAllCourses() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("Spring Boot");
        courseDto.setDescription("Learn Spring");
        courseDto.setDuration(20);
        courseDto.setCategoryId(categoryId);
        courseDto.setTeacherId(teacherId);

        mockMvc.perform(post("/courses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)));

        mockMvc.perform(get("/courses/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testUpdateCourse() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("Old Title");
        courseDto.setDescription("Old Desc");
        courseDto.setDuration(10);
        courseDto.setCategoryId(categoryId);
        courseDto.setTeacherId(teacherId);

        String response = mockMvc.perform(post("/courses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andReturn().getResponse().getContentAsString();

        CourseDto created = objectMapper.readValue(response, CourseDto.class);
        created.setTitle("New Title");

        mockMvc.perform(put("/courses/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void testDeleteCourse() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("To Delete");
        courseDto.setDescription("Delete me");
        courseDto.setDuration(5);
        courseDto.setCategoryId(categoryId);
        courseDto.setTeacherId(teacherId);

        String response = mockMvc.perform(post("/courses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andReturn().getResponse().getContentAsString();

        CourseDto created = objectMapper.readValue(response, CourseDto.class);

        mockMvc.perform(delete("/courses/" + created.getId()))
                .andExpect(status().isOk());
    }
}
