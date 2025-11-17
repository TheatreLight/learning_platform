package mephi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mephi.dto.CategoryDto;
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
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Programming");

        mockMvc.perform(post("/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Programming"));
    }

    @Test
    void testGetAllCategories() throws Exception {
        // Create a category first
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Design");

        mockMvc.perform(post("/categories/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        // Get all categories
        mockMvc.perform(get("/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Design"));
    }

    @Test
    void testGetCategoryById() throws Exception {
        // Create a category
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Marketing");

        String response = mockMvc.perform(post("/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andReturn().getResponse().getContentAsString();

        CategoryDto created = objectMapper.readValue(response, CategoryDto.class);

        // Get by ID
        mockMvc.perform(get("/categories/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marketing"));
    }

    @Test
    void testUpdateCategory() throws Exception {
        // Create a category
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Old Name");

        String response = mockMvc.perform(post("/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andReturn().getResponse().getContentAsString();

        CategoryDto created = objectMapper.readValue(response, CategoryDto.class);

        // Update
        created.setName("New Name");
        mockMvc.perform(put("/categories/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        // Create a category
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("To Delete");

        String response = mockMvc.perform(post("/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andReturn().getResponse().getContentAsString();

        CategoryDto created = objectMapper.readValue(response, CategoryDto.class);

        // Delete
        mockMvc.perform(delete("/categories/" + created.getId()))
                .andExpect(status().isOk());

        // Verify deleted
        mockMvc.perform(get("/categories/" + created.getId()))
                .andExpect(status().isNotFound());
    }
}
