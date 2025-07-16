package com.java_web.backend.service;

import com.java_web.backend.Config.OpenAIConfig;
import com.java_web.backend.Service.LLMJsonToMarkdownService;
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
public class LLMJsonToMarkdownServiceTest {

    @Mock
    private OpenAIConfig openAIConfig;

    @InjectMocks
    private LLMJsonToMarkdownService jsonToMarkdownService;

    private LLMJsonToMarkdownService spyService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(openAIConfig.getModelName()).thenReturn("qwen-plus");
        Mockito.lenient().when(openAIConfig.getApiUrl()).thenReturn("http://localhost:8080/v1/chat/completions");
        Mockito.lenient().when(openAIConfig.getApiKey()).thenReturn("test-api-key");
        spyService = Mockito.spy(jsonToMarkdownService);
    }

    @Test
    void testConvertJsonToMarkdown() throws IOException {
        String testJson = """
            {
                "course_id": "CS001",
                "course_code": "CS101",
                "course_Chinese_name": "计算机科学导论",
                "course_English_name": "Introduction to Computer Science",
                "teaching_language": "中文",
                "responsible_college": "计算机学院",
                "course_category": "专业必修",
                "principle": "李老师",
                "verifier": "王老师",
                "credit": "3",
                "course_hour": "48",
                "course_introduction": "计算机科学基础课程",
                "course_target": "掌握计算机科学基础知识",
                "evaluation_mode": "考试",
                "whether_technical_course": "否",
                "assessment_type": "理论",
                "grade_recording": "百分制",
                "detailed_course_target": [
                    {
                        "target_number": "1",
                        "target": "掌握计算机科学基础理论",
                        "support_graduation_requirement": "支撑毕业要求1"
                    }
                ],
                "teaching_content": [
                    {
                        "unit_number": "1",
                        "content": "计算机基础",
                        "ideological_and_political_integration": "爱国主义教育",
                        "time_allocation": "16",
                        "detailed_time_allocation": {
                            "lecture": "12",
                            "experiment": "4",
                            "computer_practice": "0",
                            "practice": "0",
                            "extracurricular": "0"
                        }
                    }
                ],
                "experimental_projects": [
                    {
                        "unit_number": "1",
                        "experiment_name": "基础实验",
                        "content_and_requirements": "掌握基本操作",
                        "experiment_hour": "4",
                        "experiment_type": "验证性"
                    }
                ],
                "textbooks_and_reference_books": [
                    {
                        "book_name": "计算机科学导论",
                        "editor": "张三",
                        "ISBN": "978-7-111-12345-6",
                        "publisher": "机械工业出版社",
                        "publication_year": "2023"
                    }
                ]
            }
            """;

        String expectedMarkdown = """
            # 计算机科学导论 (CS101)
            
            ## 课程基本信息
            - **课程编号**: CS001
            - **课程代码**: CS101
            - **课程名称**: 计算机科学导论
            - **英文名称**: Introduction to Computer Science
            - **教学语言**: 中文
            - **负责学院**: 计算机学院
            - **课程类别**: 专业必修
            - **负责人**: 李老师
            - **审核人**: 王老师
            - **学分**: 3
            - **学时**: 48
            
            ## 课程介绍
            计算机科学基础课程
            
            ## 课程目标
            掌握计算机科学基础知识
            
            ## 考核方式
            - **考核模式**: 考试
            - **是否技术课程**: 否
            - **考核类型**: 理论
            - **成绩记录**: 百分制
            
            ## 详细课程目标
            1. **目标1**: 掌握计算机科学基础理论
               - 支撑毕业要求: 支撑毕业要求1
            
            ## 教学内容
            ### 第一单元: 计算机基础
            - **思政融入**: 爱国主义教育
            - **学时分配**: 16学时
            - **详细时间分配**:
                - 讲授: 12学时
                - 实验: 4学时
                - 上机: 0学时
                - 实践: 0学时
                - 课外: 0学时
            
            ## 实验项目
            ### 第一单元实验: 基础实验
            - **实验内容与要求**: 掌握基本操作
            - **实验学时**: 4学时
            - **实验类型**: 验证性
            
            ## 教材与参考书
            - **教材名称**: 计算机科学导论
            - **编者**: 张三
            - **ISBN**: 978-7-111-12345-6
            - **出版社**: 机械工业出版社
            - **出版年份**: 2023
            """;

        doReturn(expectedMarkdown).when(spyService).callLLM(anyString());

        String result = spyService.convertJsonToMarkdown(testJson, "markdown", "默认样式");

        assertNotNull(result);
        assertTrue(result.contains("# 计算机科学导论 (CS101)"));
        assertTrue(result.contains("课程编号: CS001"));
        assertTrue(result.contains("课程代码: CS101"));
        assertTrue(result.contains("课程名称: 计算机科学导论"));
    }

    @Test
    void testConvertJsonToMarkdownWithCustomStyle() throws IOException {
        String testJson = """
            {
                "course_id": "CS001",
                "course_code": "CS101",
                "course_Chinese_name": "计算机科学导论"
            }
            """;

        String customStyle = "使用表格格式展示课程信息";

        String expectedMarkdown = """
            | 字段 | 值 |
            |------|----|
            | 课程编号 | CS001 |
            | 课程代码 | CS101 |
            | 课程名称 | 计算机科学导论 |
            """;

        doReturn(expectedMarkdown).when(spyService).callLLM(anyString());

        String result = spyService.convertJsonToMarkdownWithCustomStyle(testJson, customStyle);

        assertNotNull(result);
        assertTrue(result.contains("| 字段 | 值 |"));
        assertTrue(result.contains("| 课程编号 | CS001 |"));
    }

    @Test
    void testBatchConvertJsonToMarkdown() throws IOException {
        String testJson1 = """
            {
                "course_id": "CS001",
                "course_code": "CS101",
                "course_Chinese_name": "计算机科学导论"
            }
            """;

        String testJson2 = """
            {
                "course_id": "CS002",
                "course_code": "CS102",
                "course_Chinese_name": "数据结构"
            }
            """;

        String expectedMarkdown1 = "# 计算机科学导论 (CS101)\n\n课程编号: CS001";
        String expectedMarkdown2 = "# 数据结构 (CS102)\n\n课程编号: CS002";

        doReturn(expectedMarkdown1)
                .doReturn(expectedMarkdown2)
                .when(spyService).callLLM(anyString());

        Map<String, String> result = spyService.batchConvertJsonToMarkdown(
                Map.of("course1.json", testJson1, "course2.json", testJson2)
        );

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get("course1.json").contains("计算机科学导论"));
        assertTrue(result.get("course2.json").contains("数据结构"));
    }

    @Test
    void testConvertJsonToMarkdownWithError() throws IOException {
        doReturn("").doThrow(new RuntimeException("大模型异常"))
                .when(spyService).callLLM(anyString());

        assertThrows(IOException.class, () -> {
            spyService.convertJsonToMarkdown("invalid json", "markdown", "默认样式");
        });
    }

    @Test
    void testConvertJsonToMarkdownWithInvalidJson() throws IOException {
        assertThrows(IOException.class, () -> {
            spyService.convertJsonToMarkdown("not a json string", "markdown", "默认样式");
        });
    }
} 