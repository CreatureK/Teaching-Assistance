package com.java_web.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Controller.LLMController;
import com.java_web.backend.Entity.IntroductionAndTargetRequest;
import com.java_web.backend.Entity.IntroductionAndTargetResponse;
import com.java_web.backend.Service.LLMIntroductionAndTargetService;
import com.java_web.backend.Service.LLMSyllabusService;
import com.java_web.backend.Service.LLMLectureService;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(LLMController.class)
public class LLMControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LLMIntroductionAndTargetService llmIntroductionAndTargetService;

    @MockBean
    private LLMSyllabusService llmSyllabusService;

    @MockBean
    private LLMLectureService llmLectureService;

    private TestResultWriter testResultWriter;

    @BeforeEach
    void setUp() {
        testResultWriter = new TestResultWriter();
    }

    @Test
    public void testGenerateIntroductionAndTarget() throws Exception {
        // 准备测试数据
        IntroductionAndTargetRequest req = new IntroductionAndTargetRequest();
        req.setCourseId("1");
        req.setCourseTitle("高等数学");
        req.setRequest("请结合工程实际，突出应用能力培养");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("1");
        mockResponse.setCourseIntroduction("本课程是面向工程类专业本科生的高等数学课程");
        mockResponse.setTeachingTarget("通过本课程的学习，学生应掌握高等数学的基本理论");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                .thenReturn(mockResponse);

        // 执行测试
        MvcResult result = mockMvc.perform(post("/api/llm/introduction_and_target")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("1"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists())
                .andReturn();

        // 保存测试结果到文件
        String responseContent = result.getResponse().getContentAsString();
        testResultWriter.saveJsonResult("Controller_IntroductionAndTarget", responseContent);
        testResultWriter.saveTextResult("Controller_IntroductionAndTarget_Request", objectMapper.writeValueAsString(req));
    }
} 