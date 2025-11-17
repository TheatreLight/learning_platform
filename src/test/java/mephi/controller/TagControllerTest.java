package mephi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mephi.dto.TagDto;
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
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTag() throws Exception {
        TagDto tagDto = new TagDto();
        tagDto.setName("Java");

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    void testGetAllTags() throws Exception {
        TagDto tagDto = new TagDto();
        tagDto.setName("Spring");

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto)));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Spring"));
    }

    @Test
    void testGetTagById() throws Exception {
        TagDto tagDto = new TagDto();
        tagDto.setName("Hibernate");

        String response = mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto)))
                .andReturn().getResponse().getContentAsString();

        TagDto created = objectMapper.readValue(response, TagDto.class);

        mockMvc.perform(get("/api/tags/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hibernate"));
    }

    @Test
    void testGetTagByName() throws Exception {
        TagDto tagDto = new TagDto();
        tagDto.setName("PostgreSQL");

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto)));

        mockMvc.perform(get("/api/tags/name/PostgreSQL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PostgreSQL"));
    }

    @Test
    void testUpdateTag() throws Exception {
        TagDto tagDto = new TagDto();
        tagDto.setName("OldTag");

        String response = mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto)))
                .andReturn().getResponse().getContentAsString();

        TagDto created = objectMapper.readValue(response, TagDto.class);

        created.setName("NewTag");
        mockMvc.perform(put("/api/tags/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewTag"));
    }

    @Test
    void testDeleteTag() throws Exception {
        TagDto tagDto = new TagDto();
        tagDto.setName("ToDelete");

        String response = mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto)))
                .andReturn().getResponse().getContentAsString();

        TagDto created = objectMapper.readValue(response, TagDto.class);

        mockMvc.perform(delete("/api/tags/" + created.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tags/" + created.getId()))
                .andExpect(status().isNotFound());
    }
}
