package com.java_web.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Entity.InitialSyllabusRequest;
import com.java_web.backend.Service.LLMInitialSyllabusService;
import com.java_web.backend.Controller.InitialSyllabusController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InitialSyllabusController.class)
public class InitialSyllabusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private LLMInitialSyllabusService initialSyllabusService;

    @Autowired
    private ObjectMapper objectMapper;

    private InitialSyllabusRequest testRequest;
    private Map<String, Object> testResponse;

    @BeforeEach
    void setUp() {
        // 创建测试请求
        testRequest = new InitialSyllabusRequest(
                "CS001", "CS101", "计算机科学导论", "中文", "计算机学院",
                "专业必修", "李老师", "王老师", "3", "48",
                "计算机科学基础课程", "掌握计算机科学基础知识", "考试",
                "否", "理论", "百分制", "希望课程内容生动有趣"
        );

        // 创建测试响应
        testResponse = new HashMap<>();
        testResponse.put("course_id", "CS001");
        testResponse.put("course_code", "CS101");
        testResponse.put("course_Chinese_name", "计算机科学导论");
        testResponse.put("course_English_name", "Computer Science");
        testResponse.put("teaching_language", "中文");
        testResponse.put("responsible_college", "计算机学院");
        testResponse.put("course_category", "专业必修");
        testResponse.put("principle", "李老师");
        testResponse.put("verifier", "王老师");
        testResponse.put("credit", "3");
        testResponse.put("course_hour", "48");
        testResponse.put("whether_technical_course", "否");
        testResponse.put("assessment_type", "理论");
        testResponse.put("grade_recording", "百分制");
        testResponse.put("course_introduction", "计算机科学基础课程");
        testResponse.put("course_target", "掌握计算机科学基础知识");
        testResponse.put("evaluation_mode", "考试");

        // 添加详细课程目标
        Map<String, Object> detailedTarget = new HashMap<>();
        detailedTarget.put("detailed_course_target", new Object[]{
                Map.of("target_number", "1", "target", "掌握计算机科学基础理论", "support_graduation_requirement", "支撑毕业要求1")
        });
        testResponse.put("detailed_course_target", detailedTarget);

        // 添加教学内容
        Map<String, Object> teachingContent = new HashMap<>();
        teachingContent.put("teaching_content", new Object[]{
                Map.of("unit_number", "1", "content", "计算机基础", "ideological_and_political_integration", "爱国主义教育", 
                      "time_allocation", "16", "detailed_time_allocation", 
                      Map.of("lecture", "12", "experiment", "4", "computer_practice", "0", "practice", "0", "extracurricular", "0"))
        });
        testResponse.put("teaching_content", teachingContent);

        // 添加实验项目
        Map<String, Object> experimentalProjects = new HashMap<>();
        experimentalProjects.put("experimental_projects", new Object[]{
                Map.of("unit_number", "1", "experiment_name", "基础实验", "content_and_requirements", "掌握基本操作", 
                      "experiment_hour", "4", "experiment_type", "验证性")
        });
        testResponse.put("experimental_projects", experimentalProjects);

        // 添加教材和参考书
        Map<String, Object> textbooks = new HashMap<>();
        textbooks.put("textbooks_and_reference_books", new Object[]{
                Map.of("book_name", "计算机科学导论", "editor", "张三", "ISBN", "978-7-111-12345-6", 
                      "publisher", "机械工业出版社", "publication_year", "2023")
        });
        testResponse.put("textbooks_and_reference_books", textbooks);
    }

    @Test
    void testGenerateInitialSyllabus() throws Exception {
        when(initialSyllabusService.generateInitialSyllabus(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/initial-syllabus/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("初始教学大纲生成成功"))
                .andExpect(jsonPath("$.data.course_id").value("CS001"))
                .andExpect(jsonPath("$.data.course_code").value("CS101"))
                .andExpect(jsonPath("$.data.course_Chinese_name").value("计算机科学导论"))
                .andExpect(jsonPath("$.data.course_English_name").value("Computer Science"))
                .andExpect(jsonPath("$.data.teaching_language").value("中文"))
                .andExpect(jsonPath("$.data.responsible_college").value("计算机学院"))
                .andExpect(jsonPath("$.data.course_category").value("专业必修"))
                .andExpect(jsonPath("$.data.principle").value("李老师"))
                .andExpect(jsonPath("$.data.verifier").value("王老师"))
                .andExpect(jsonPath("$.data.credit").value("3"))
                .andExpect(jsonPath("$.data.course_hour").value("48"))
                .andExpect(jsonPath("$.data.whether_technical_course").value("否"))
                .andExpect(jsonPath("$.data.assessment_type").value("理论"))
                .andExpect(jsonPath("$.data.grade_recording").value("百分制"))
                .andExpect(jsonPath("$.data.course_introduction").value("计算机科学基础课程"))
                .andExpect(jsonPath("$.data.course_target").value("掌握计算机科学基础知识"))
                .andExpect(jsonPath("$.data.evaluation_mode").value("考试"));
    }

    @Test
    void testGenerateInitialSyllabusWithError() throws Exception {
        when(initialSyllabusService.generateInitialSyllabus(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new IOException("生成教学大纲时发生错误"));

        mockMvc.perform(post("/api/initial-syllabus/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("生成教学大纲时发生错误: 生成教学大纲时发生错误"));
    }

    @Test
    void testGenerateInitialSyllabusWithSystemError() throws Exception {
        when(initialSyllabusService.generateInitialSyllabus(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("系统错误"));

        mockMvc.perform(post("/api/initial-syllabus/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("系统错误: 系统错误"));
    }

    @Test
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/initial-syllabus/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("初始教学大纲服务运行正常"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGenerateInitialSyllabusWithInvalidRequest() throws Exception {
        // 测试缺少必要字段的请求
        InitialSyllabusRequest invalidRequest = new InitialSyllabusRequest();
        invalidRequest.setCourseId("CS001");
        // 其他字段为null

        mockMvc.perform(post("/api/initial-syllabus/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk()); // 由于我们使用了@RequestBody，Spring会自动处理验证
    }
} 