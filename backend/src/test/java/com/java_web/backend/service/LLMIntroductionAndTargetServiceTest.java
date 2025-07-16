package com.java_web.backend.service;

import com.java_web.backend.Config.OpenAIConfig;
import com.java_web.backend.Entity.IntroductionAndTargetRequest;
import com.java_web.backend.Entity.IntroductionAndTargetResponse;
import com.java_web.backend.Service.LLMIntroductionAndTargetService;
import com.java_web.backend.Utils.HttpUtil;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LLMIntroductionAndTargetServiceTest {

    @Mock
    private OpenAIConfig openAIConfig;

    @InjectMocks
    private LLMIntroductionAndTargetService service;

    private TestResultWriter testResultWriter;
    private IntroductionAndTargetRequest request;

    @BeforeEach
    void setUp() {
        testResultWriter = new TestResultWriter();
        request = new IntroductionAndTargetRequest();
        request.setCourseId("1");
        request.setCourseTitle("高等数学");
        request.setRequest("请结合工程实际，突出应用能力培养");

        // 设置OpenAI配置的模拟值 - 使用lenient避免不必要的stubbing警告
        lenient().when(openAIConfig.getModelName()).thenReturn("qwen-plus");
        lenient().when(openAIConfig.getApiKey()).thenReturn("test-api-key");
        lenient().when(openAIConfig.getApiUrl()).thenReturn("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
    }

    @Test
    void testGenerateIntroductionAndTarget_Success() throws IOException {
        // 模拟LLM返回的成功响应
        String mockResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "content": "{\\"course_introduction\\": \\"本课程是面向工程类专业本科生的高等数学课程，内容涵盖函数、极限与连续、导数与微分、积分学、多元函数微积分、常微分方程、级数等基础理论知识。\\", \\"teaching_target\\": \\"通过本课程的学习，学生应掌握高等数学的基本理论和运算方法，具备运用数学工具分析和解决工程实际问题的能力。\\"}"
                        }
                    }
                ]
            }
            """;

        try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
            httpUtilMock.when(() -> HttpUtil.postJson(anyString(), anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // 执行测试
            IntroductionAndTargetResponse response = service.generateIntroductionAndTarget(request);

            // 验证结果
            assertNotNull(response);
            assertEquals("1", response.getCourseId());
            assertEquals("本课程是面向工程类专业本科生的高等数学课程，内容涵盖函数、极限与连续、导数与微分、积分学、多元函数微积分、常微分方程、级数等基础理论知识。", response.getCourseIntroduction());
            assertEquals("通过本课程的学习，学生应掌握高等数学的基本理论和运算方法，具备运用数学工具分析和解决工程实际问题的能力。", response.getTeachingTarget());

            // 保存测试结果到文件
            testResultWriter.saveJsonResult("IntroductionAndTarget_Success", response);
            testResultWriter.saveRawResponse("IntroductionAndTarget_Success", mockResponse);

            // 验证HTTP调用
            httpUtilMock.verify(() -> HttpUtil.postJson(anyString(), anyString(), anyMap()), times(1));
        }
    }

    @Test
    void testGenerateIntroductionAndTarget_EmptyChoices() throws IOException {
        // 模拟LLM返回空choices的响应
        String mockResponse = """
            {
                "choices": []
            }
            """;

        try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
            httpUtilMock.when(() -> HttpUtil.postJson(anyString(), anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // 执行测试
            IntroductionAndTargetResponse response = service.generateIntroductionAndTarget(request);

            // 验证结果
            assertNotNull(response);
            assertEquals("1", response.getCourseId());
            assertEquals("生成失败", response.getCourseIntroduction());
            assertEquals("生成失败", response.getTeachingTarget());
        }
    }

    @Test
    void testGenerateIntroductionAndTarget_InvalidJson() throws IOException {
        // 模拟LLM返回无效JSON的响应
        String mockResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "content": "invalid json content"
                        }
                    }
                ]
            }
            """;

        try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
            httpUtilMock.when(() -> HttpUtil.postJson(anyString(), anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // 执行测试
            IntroductionAndTargetResponse response = service.generateIntroductionAndTarget(request);

            // 验证结果
            assertNotNull(response);
            assertEquals("1", response.getCourseId());
            assertTrue(response.getCourseIntroduction().startsWith("解析失败:"));
            assertTrue(response.getTeachingTarget().startsWith("解析失败:"));
        }
    }

    @Test
    void testGenerateIntroductionAndTarget_MissingFields() throws IOException {
        // 模拟LLM返回缺少字段的响应
        String mockResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "content": "{\\"course_introduction\\": \\"课程介绍\\"}"
                        }
                    }
                ]
            }
            """;

        try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
            httpUtilMock.when(() -> HttpUtil.postJson(anyString(), anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // 执行测试
            IntroductionAndTargetResponse response = service.generateIntroductionAndTarget(request);

            // 验证结果
            assertNotNull(response);
            assertEquals("1", response.getCourseId());
            assertTrue(response.getCourseIntroduction().startsWith("解析失败:"));
            assertTrue(response.getTeachingTarget().startsWith("解析失败:"));
        }
    }

    @Test
    void testGenerateIntroductionAndTarget_HttpException() throws IOException {
        // 模拟HTTP调用异常 - 使用RuntimeException而不是IOException
        try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
            httpUtilMock.when(() -> HttpUtil.postJson(anyString(), anyString(), anyMap()))
                    .thenThrow(new RuntimeException("Network error"));

            // 执行测试并验证异常
            assertThrows(RuntimeException.class, () -> {
                service.generateIntroductionAndTarget(request);
            });
        }
    }

    @Test
    void testGenerateIntroductionAndTarget_WithNullRequest() {
        // 测试空请求
        assertThrows(NullPointerException.class, () -> {
            service.generateIntroductionAndTarget(null);
        });
    }

    @Test
    void testGenerateIntroductionAndTarget_WithEmptyCourseTitle() throws IOException {
        // 设置空的课程标题
        request.setCourseTitle("");
        
        // 模拟LLM返回的成功响应
        String mockResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "content": "{\\"course_introduction\\": \\"课程介绍\\", \\"teaching_target\\": \\"教学目标\\"}"
                        }
                    }
                ]
            }
            """;

        try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
            httpUtilMock.when(() -> HttpUtil.postJson(anyString(), anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // 执行测试
            IntroductionAndTargetResponse response = service.generateIntroductionAndTarget(request);

            // 验证结果
            assertNotNull(response);
            assertEquals("1", response.getCourseId());
            assertEquals("课程介绍", response.getCourseIntroduction());
            assertEquals("教学目标", response.getTeachingTarget());
        }
    }

    @Test
    void testGenerateIntroductionAndTarget_WithNullRequestField() throws IOException {
        // 设置空的请求字段
        request.setRequest(null);
        
        // 模拟LLM返回的成功响应
        String mockResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "content": "{\\"course_introduction\\": \\"课程介绍\\", \\"teaching_target\\": \\"教学目标\\"}"
                        }
                    }
                ]
            }
            """;

        try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
            httpUtilMock.when(() -> HttpUtil.postJson(anyString(), anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // 执行测试
            IntroductionAndTargetResponse response = service.generateIntroductionAndTarget(request);

            // 验证结果
            assertNotNull(response);
            assertEquals("1", response.getCourseId());
            assertEquals("课程介绍", response.getCourseIntroduction());
            assertEquals("教学目标", response.getTeachingTarget());
        }
    }
} 