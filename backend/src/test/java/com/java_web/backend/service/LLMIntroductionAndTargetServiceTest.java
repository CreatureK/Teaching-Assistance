package com.java_web.backend.service;

import com.java_web.backend.Config.OpenAIConfig;
import com.java_web.backend.Entity.IntroductionAndTargetRequest;
import com.java_web.backend.Entity.IntroductionAndTargetResponse;
import com.java_web.backend.Service.LLMIntroductionAndTargetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class LLMIntroductionAndTargetServiceTest {

    @Mock
    private OpenAIConfig openAIConfig;

    @InjectMocks
    private LLMIntroductionAndTargetService introductionAndTargetService;

    private LLMIntroductionAndTargetService spyService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(openAIConfig.getModelName()).thenReturn("qwen-plus");
        Mockito.lenient().when(openAIConfig.getApiUrl()).thenReturn("http://localhost:8080/v1/chat/completions");
        Mockito.lenient().when(openAIConfig.getApiKey()).thenReturn("test-api-key");
        spyService = Mockito.spy(introductionAndTargetService);
    }

    @Test
    void testGenerateIntroductionAndTarget() throws IOException {
        String expectedResponse = """
            {
                "course_introduction": "计算机科学导论是一门介绍计算机科学基础理论和基本概念的课程。本课程旨在帮助学生建立对计算机科学的整体认识，了解计算机科学的发展历程、基本概念、核心技术和应用领域。通过本课程的学习，学生将掌握计算机科学的基本知识体系，为后续专业课程的学习奠定坚实基础。",
                "course_target": "通过本课程的学习，学生应该能够：1. 理解计算机科学的基本概念和核心理论；2. 掌握计算机系统的基本组成和工作原理；3. 了解计算机科学的主要分支和应用领域；4. 培养计算思维和问题解决能力；5. 为后续专业课程学习做好准备。"
            }
            """;

        doReturn(expectedResponse).when(spyService).callLLM(anyString());

        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseId("CS101");
        request.setCourseTitle("计算机科学导论");
        request.setRequest("用户需求测试");
        
        IntroductionAndTargetResponse result = spyService.generateIntroductionAndTarget(request);

        assertNotNull(result);
        assertNotNull(result.getCourseIntroduction());
        assertNotNull(result.getTeachingTarget());
        assertTrue(result.getCourseIntroduction().contains("计算机科学导论"));
        assertTrue(result.getTeachingTarget().contains("计算机科学"));
    }

    @Test
    void testGenerateDetailedTeachingContentAndTarget() throws IOException {
        String expectedResponse = """
            {
                "detailed_teaching_content": [
                    {
                        "unit_number": "1",
                        "content": "计算机科学概述",
                        "teaching_objective": "了解计算机科学的基本概念和发展历程",
                        "key_points": ["计算机科学的定义", "计算机科学的发展历史", "计算机科学的主要分支"],
                        "difficulty_level": "基础",
                        "estimated_hours": "8"
                    },
                    {
                        "unit_number": "2",
                        "content": "计算机系统基础",
                        "teaching_objective": "掌握计算机系统的基本组成和工作原理",
                        "key_points": ["计算机硬件系统", "计算机软件系统", "计算机工作原理"],
                        "difficulty_level": "基础",
                        "estimated_hours": "12"
                    }
                ],
                "detailed_course_target": [
                    {
                        "target_number": "1",
                        "target": "理解计算机科学的基本概念和核心理论",
                        "support_graduation_requirement": "支撑毕业要求1：掌握本专业基础理论",
                        "assessment_method": "课堂讨论、作业、考试"
                    },
                    {
                        "target_number": "2",
                        "target": "掌握计算机系统的基本组成和工作原理",
                        "support_graduation_requirement": "支撑毕业要求2：具备专业实践能力",
                        "assessment_method": "实验报告、项目设计、考试"
                    }
                ]
            }
            """;

        doReturn(expectedResponse).when(spyService).callLLM(anyString());

        IntroductionAndTargetResponse result = spyService.generateDetailedTeachingContentAndTarget(
                "CS101", "计算机科学导论", "3", "48", "计算机学院", "专业必修"
        );

        assertNotNull(result);
        assertNotNull(result.getCourseIntroduction());
        assertNotNull(result.getTeachingTarget());
        
        // 验证详细教学内容
        assertNotNull(result.getCourseIntroduction());
        
        // 验证详细课程目标
        assertNotNull(result.getTeachingTarget());
    }

    @Test
    void testGenerateIntroductionAndTargetWithError() throws IOException {
        doThrow(new RuntimeException("大模型异常"))
                .when(spyService).callLLM(anyString());

        assertThrows(IOException.class, () -> {
            IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
            request.setCourseId("CS101");
            request.setCourseTitle("计算机科学导论");
            request.setRequest("用户需求测试");
            spyService.generateIntroductionAndTarget(request);
        });
    }

    @Test
    void testGenerateDetailedTeachingContentAndTargetWithError() throws IOException {
        doThrow(new RuntimeException("大模型异常"))
                .when(spyService).callLLM(anyString());

        assertThrows(IOException.class, () -> {
            spyService.generateDetailedTeachingContentAndTarget(
                    "CS101", "计算机科学导论", "3", "48", "计算机学院", "专业必修"
            );
        });
    }

    @Test
    void testGenerateIntroductionAndTargetWithInvalidResponse() throws IOException {
        doReturn("not a json string").when(spyService).callLLM(anyString());

        assertThrows(IOException.class, () -> {
            IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
            request.setCourseId("CS101");
            request.setCourseTitle("计算机科学导论");
            request.setRequest("用户需求测试");
            spyService.generateIntroductionAndTarget(request);
        });
    }

    @Test
    void testGenerateDetailedTeachingContentAndTargetWithInvalidResponse() throws IOException {
        doReturn("not a json string").when(spyService).callLLM(anyString());

        assertThrows(IOException.class, () -> {
            spyService.generateDetailedTeachingContentAndTarget(
                    "CS101", "计算机科学导论", "3", "48", "计算机学院", "专业必修"
            );
        });
    }
} 