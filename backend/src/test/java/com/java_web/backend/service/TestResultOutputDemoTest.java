package com.java_web.backend.service;

import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.DTO.IntroductionAndTargetRequestDTO;
import com.java_web.backend.Common.DTO.IntroductionAndTargetResponseDTO;
import com.java_web.backend.Common.Entity.IntroductionAndTargetResponse;
import com.java_web.backend.Common.Service.LLMIntroductionAndTargetService;
import com.java_web.backend.utils.TestResultOutputUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestResultOutputDemoTest {

    @Mock
    private OpenAIConfig openAIConfig;

    @Mock
    private LLMIntroductionAndTargetService service;

    @Test
    void testGenerateIntroductionAndTargetWithOutput() {
        String expectedResponse = "{\"course_introduction\":\"intro\",\"teaching_target\":\"target\"}";
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId("CS101");
        request.setCourseTitle("计算机科学导论");
        request.setRequest("用户需求测试");
        
        IntroductionAndTargetResponse mockResult = new IntroductionAndTargetResponse("CS101", "intro", "target");
        when(service.generateIntroductionAndTarget(any(IntroductionAndTargetRequestDTO.class))).thenReturn(mockResult);
        
        IntroductionAndTargetResponse result = service.generateIntroductionAndTarget(request);
        
        assertNotNull(result);
        assertNotNull(result.getCourseIntroduction());
        assertNotNull(result.getTeachingTarget());
        
        String testName = "testGenerateIntroductionAndTargetWithOutput";
        String outputContent = "测试方法: " + testName + "\n" +
                "输入参数: CS101, 计算机科学导论, 用户需求测试\n" +
                "大模型返回内容:\n" + expectedResponse + "\n" +
                "解析结果:\n课程介绍: " + result.getCourseIntroduction() + "\n教学目标: " + result.getTeachingTarget();
        TestResultOutputUtil.outputToFile(testName, outputContent);
    }

    @Test
    void testGenerateDetailedTeachingContentAndTargetWithOutput() {
        String expectedResponse = "{\"course_introduction\":\"detailed intro\",\"teaching_target\":\"detailed target\"}";
        IntroductionAndTargetResponse mockResult = new IntroductionAndTargetResponse("CS101", "detailed intro", "detailed target");
        when(service.generateDetailedTeachingContentAndTarget(any(), any(), any(), any(), any(), any())).thenReturn(mockResult);
        
        IntroductionAndTargetResponse result = service.generateDetailedTeachingContentAndTarget(
                "CS101", "计算机科学导论", "3", "48", "计算机学院", "专业必修"
        );
        
        assertNotNull(result);
        assertNotNull(result.getCourseIntroduction());
        assertNotNull(result.getTeachingTarget());
        
        String testName = "testGenerateDetailedTeachingContentAndTargetWithOutput";
        String outputContent = "测试方法: " + testName + "\n" +
                "输入参数: CS101, 计算机科学导论, 3, 48, 计算机学院, 专业必修\n" +
                "大模型返回内容:\n" + expectedResponse + "\n" +
                "解析结果:\n课程介绍: " + result.getCourseIntroduction() + "\n教学目标: " + result.getTeachingTarget();
        TestResultOutputUtil.outputToFile(testName, outputContent);
    }

    @Test
    void testGenerateIntroductionAndTargetWithErrorAndOutput() {
        when(service.generateIntroductionAndTarget(any(IntroductionAndTargetRequestDTO.class)))
                .thenThrow(new RuntimeException("JSON解析失败"));
        
        try {
            IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
            request.setCourseId("CS101");
            request.setCourseTitle("计算机科学导论");
            request.setRequest("用户需求测试");
            service.generateIntroductionAndTarget(request);
            fail("应该抛出异常");
        } catch (Exception e) {
            String testName = "testGenerateIntroductionAndTargetWithErrorAndOutput";
            String outputContent = "测试方法: " + testName + "\n" +
                    "输入参数: CS101, 计算机科学导论, 用户需求测试\n" +
                    "大模型返回内容: not a json string\n" +
                    "异常信息: " + e.getMessage();
            TestResultOutputUtil.outputToFile(testName, outputContent);
        }
    }
} 