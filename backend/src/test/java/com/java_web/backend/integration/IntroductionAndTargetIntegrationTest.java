package com.java_web.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Entity.IntroductionAndTargetRequest;
import com.java_web.backend.Entity.IntroductionAndTargetResponse;
import com.java_web.backend.Service.LLMIntroductionAndTargetService;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IntroductionAndTargetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LLMIntroductionAndTargetService llmIntroductionAndTargetService;

    private TestResultWriter testResultWriter;

    @BeforeEach
    void setUp() {
        testResultWriter = new TestResultWriter();
    }

    @Test
    void testIntroductionAndTargetEndpoint_Success() throws Exception {
        // 准备测试数据
        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseId("1");
        request.setCourseTitle("高等数学");
        request.setRequest("请结合工程实际，突出应用能力培养");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("1");
        mockResponse.setCourseIntroduction("本课程是面向工程类专业本科生的高等数学课程");
        mockResponse.setTeachingTarget("通过本课程的学习，学生应掌握高等数学的基本理论");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        MvcResult result = mockMvc.perform(post("/api/llm/introduction_and_target")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.courseId").value("1"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists())
                .andReturn();

        // 解析响应
        String responseContent = result.getResponse().getContentAsString();
        IntroductionAndTargetResponse response = objectMapper.readValue(responseContent, IntroductionAndTargetResponse.class);

        // 验证响应内容
        assertNotNull(response);
        assertEquals("1", response.getCourseId());
        assertNotNull(response.getCourseIntroduction());
        assertNotNull(response.getTeachingTarget());
        assertFalse(response.getCourseIntroduction().isEmpty());
        assertFalse(response.getTeachingTarget().isEmpty());

        // 保存测试结果到文件
        testResultWriter.saveJsonResult("Integration_IntroductionAndTarget_Success", response);
        testResultWriter.saveTextResult("Integration_IntroductionAndTarget_Request", objectMapper.writeValueAsString(request));
        testResultWriter.saveRawResponse("Integration_IntroductionAndTarget_Success", responseContent);
    }

    @Test
    void testIntroductionAndTargetEndpoint_EmptyCourseTitle() throws Exception {
        // 准备测试数据 - 空课程标题
        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseId("2");
        request.setCourseTitle("");
        request.setRequest("测试请求");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("2");
        mockResponse.setCourseIntroduction("课程介绍");
        mockResponse.setTeachingTarget("教学目标");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        mockMvc.perform(post("/api/llm/introduction_and_target")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("2"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }

    @Test
    void testIntroductionAndTargetEndpoint_NullRequest() throws Exception {
        // 准备测试数据 - 空请求字段
        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseId("3");
        request.setCourseTitle("线性代数");
        request.setRequest(null);

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("3");
        mockResponse.setCourseIntroduction("课程介绍");
        mockResponse.setTeachingTarget("教学目标");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        mockMvc.perform(post("/api/llm/introduction_and_target")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("3"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }

    @Test
    void testIntroductionAndTargetEndpoint_InvalidJson() throws Exception {
        // 发送无效的JSON
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/llm/introduction_and_target")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testIntroductionAndTargetEndpoint_MissingRequiredFields() throws Exception {
        // 准备测试数据 - 缺少必要字段
        String incompleteJson = """
            {
                "courseId": "4"
            }
            """;

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("4");
        mockResponse.setCourseIntroduction("课程介绍");
        mockResponse.setTeachingTarget("教学目标");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/llm/introduction_and_target")
                .contentType(MediaType.APPLICATION_JSON)
                .content(incompleteJson))
                .andExpect(status().isOk()); // 由于字段可以为空，所以应该返回200
    }

    @Test
    void testIntroductionAndTargetEndpoint_DifferentCourseTypes() throws Exception {
        // 测试不同类型的课程
        String[] courseTitles = {"高等数学", "线性代数", "概率论与数理统计", "大学物理", "计算机程序设计"};
        
        for (int i = 0; i < courseTitles.length; i++) {
            IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
            request.setCourseId(String.valueOf(i + 1));
            request.setCourseTitle(courseTitles[i]);
            request.setRequest("请结合实际应用，注重理论与实践结合");

            // 模拟Service返回
            IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
            mockResponse.setCourseId(String.valueOf(i + 1));
            mockResponse.setCourseIntroduction("课程介绍");
            mockResponse.setTeachingTarget("教学目标");
            
            when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/api/llm/introduction_and_target")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.courseId").value(String.valueOf(i + 1)))
                    .andExpect(jsonPath("$.courseIntroduction").exists())
                    .andExpect(jsonPath("$.teachingTarget").exists());
        }
    }

    @Test
    void testIntroductionAndTargetEndpoint_LongRequest() throws Exception {
        // 测试长请求内容
        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseId("100");
        request.setCourseTitle("复杂系统建模");
        request.setRequest("请结合工程实际，突出应用能力培养，注重理论与实践的结合，强调数学建模在解决实际问题中的应用，培养学生的创新思维和解决复杂问题的能力，同时关注学科前沿发展动态");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("100");
        mockResponse.setCourseIntroduction("课程介绍");
        mockResponse.setTeachingTarget("教学目标");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/llm/introduction_and_target")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("100"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }

    @Test
    void testIntroductionAndTargetEndpoint_SpecialCharacters() throws Exception {
        // 测试包含特殊字符的请求
        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseId("101");
        request.setCourseTitle("C++程序设计");
        request.setRequest("请结合工程实际，突出应用能力培养，包含C++、Java、Python等编程语言");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("101");
        mockResponse.setCourseIntroduction("课程介绍");
        mockResponse.setTeachingTarget("教学目标");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/llm/introduction_and_target")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("101"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }
} 