package com.java_web.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Entity.IntroductionAndTargetRequest;
import com.java_web.backend.Entity.IntroductionAndTargetResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class LLMControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGenerateIntroductionAndTarget() throws Exception {
        IntroductionAndTargetRequest req = new IntroductionAndTargetRequest();
        req.setCourseId("1");
        req.setCourseTitle("高等数学");
        req.setRequest("请结合工程实际，突出应用能力培养");

        mockMvc.perform(post("/api/llm/introduction_and_target")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("1"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }
} 