package com.java_web.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.Service.LLMInitialSyllabusService;
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

@ExtendWith(MockitoExtension.class)
public class LLMInitialSyllabusServiceTest {

    @Mock
    private OpenAIConfig openAIConfig;

    @InjectMocks
    private LLMInitialSyllabusService initialSyllabusService;

    private LLMInitialSyllabusService spyService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(openAIConfig.getModelName()).thenReturn("qwen-plus");
        Mockito.lenient().when(openAIConfig.getApiUrl()).thenReturn("http://localhost:8080/v1/chat/completions");
        Mockito.lenient().when(openAIConfig.getApiKey()).thenReturn("test-api-key");
        spyService = Mockito.spy(initialSyllabusService);
    }

    @Test
    void testGenerateInitialSyllabus() throws IOException {
        // 分别mock每次callLLM的返回内容
        doReturn("Computer Science")
                .doReturn("{\"detailed_course_target\":[{\"target_number\":\"1\",\"target\":\"掌握计算机科学基础理论\",\"support_graduation_requirement\":\"支撑毕业要求1\"}]}")
                .doReturn("{\"teaching_content\":[{\"unit_number\":\"1\",\"content\":\"计算机基础\",\"ideological_and_political_integration\":\"爱国主义教育\",\"time_allocation\":\"16\",\"detailed_time_allocation\":{\"lecture\":\"12\",\"experiment\":\"4\",\"computer_practice\":\"0\",\"practice\":\"0\",\"extracurricular\":\"0\"}}]}")
                .doReturn("{\"experimental_projects\":[{\"unit_number\":\"1\",\"experiment_name\":\"基础实验\",\"content_and_requirements\":\"掌握基本操作\",\"experiment_hour\":\"4\",\"experiment_type\":\"验证性\"}]}")
                .doReturn("{\"textbooks_and_reference_books\":[{\"book_name\":\"计算机科学导论\",\"editor\":\"张三\",\"ISBN\":\"978-7-111-12345-6\",\"publisher\":\"机械工业出版社\",\"publication_year\":\"2023\"}]}")
                .when(spyService).callLLM(anyString());

        Map<String, Object> result = spyService.generateInitialSyllabus(
                "CS001", "CS101", "计算机科学导论", "中文", "计算机学院",
                "专业必修", "李老师", "王老师", "3", "48",
                "计算机科学基础课程", "掌握计算机科学基础知识", "考试",
                "否", "理论", "百分制", "希望课程内容生动有趣"
        );

        assertNotNull(result);
        assertEquals("CS001", result.get("course_id"));
        assertEquals("CS101", result.get("course_code"));
        assertEquals("计算机科学导论", result.get("course_Chinese_name"));
        assertEquals("Computer Science", result.get("course_English_name"));
        assertEquals("中文", result.get("teaching_language"));
        assertEquals("计算机学院", result.get("responsible_college"));
        assertEquals("专业必修", result.get("course_category"));
        assertEquals("李老师", result.get("principle"));
        assertEquals("王老师", result.get("verifier"));
        assertEquals("3", result.get("credit"));
        assertEquals("48", result.get("course_hour"));
        assertEquals("否", result.get("whether_technical_course"));
        assertEquals("理论", result.get("assessment_type"));
        assertEquals("百分制", result.get("grade_recording"));
        assertEquals("计算机科学基础课程", result.get("course_introduction"));
        assertEquals("掌握计算机科学基础知识", result.get("course_target"));
        assertEquals("考试", result.get("evaluation_mode"));
        assertNotNull(result.get("detailed_course_target"));
        assertNotNull(result.get("teaching_content"));
        assertNotNull(result.get("experimental_projects"));
        assertNotNull(result.get("textbooks_and_reference_books"));
    }

    @Test
    void testGenerateInitialSyllabusWithError() throws JsonProcessingException {
        doReturn("Computer Science")
                .doThrow(new RuntimeException("大模型异常"))
                .when(spyService).callLLM(anyString());
        assertThrows(Exception.class, () -> {
            spyService.generateInitialSyllabus(
                    "CS001", "CS101", "计算机科学导论", "中文", "计算机学院",
                    "专业必修", "李老师", "王老师", "3", "48",
                    "计算机科学基础课程", "掌握计算机科学基础知识", "考试",
                    "否", "理论", "百分制", "希望课程内容生动有趣"
            );
        });
    }

    @Test
    void testGenerateInitialSyllabusWithInvalidResponse() throws JsonProcessingException {
        doReturn("Computer Science")
                .doReturn("not a json string")
                .doReturn("not a json string")
                .doReturn("not a json string")
                .doReturn("not a json string")
                .when(spyService).callLLM(anyString());
        assertThrows(Exception.class, () -> {
            spyService.generateInitialSyllabus(
                    "CS001", "CS101", "计算机科学导论", "中文", "计算机学院",
                    "专业必修", "李老师", "王老师", "3", "48",
                    "计算机科学基础课程", "掌握计算机科学基础知识", "考试",
                    "否", "理论", "百分制", "希望课程内容生动有趣"
            );
        });
    }
} 