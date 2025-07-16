package com.java_web.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Common.DTO.LectureRequest;
import com.java_web.backend.Common.DTO.LectureRequest.LectureSection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LLMLectureControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGenerateLecture() throws Exception {
        LectureRequest req = new LectureRequest();
        req.setCourseId("C001");
        req.setCourseTitle("线性代数");
        req.setUnitTitle("矩阵与线性方程组");
        req.setKnowledgeHour("4");
        req.setKnowledgePoint("矩阵的秩");
        req.setRequest("请严格按照模板和时间分配要求生成讲义内容");

        List<LectureSection> sections = new ArrayList<>();
        LectureSection section = new LectureSection();
        section.setTopic("矩阵的秩");
        section.setOverview("矩阵秩的定义、实际应用、与其他知识点的关系");
        section.setCoreContentBasic("秩的基本概念、相关定理、基础算法");
        section.setCoreContentEssential("秩的计算技巧、过程模拟、复杂度分析");
        section.setCoreContentAdvanced("秩的进阶理论、交叉学科应用、竞赛级优化");
        section.setExampleExercisesBasic("基础例题1、基础例题2、基础例题3");
        section.setExampleExercisesEssential("必备例题1、必备例题2、必备例题3");
        section.setExampleExercisesAdvanced("进阶例题1、进阶例题2");
        sections.add(section);
        req.setSections(sections);

        mockMvc.perform(post("/api/llm/lecture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
} 