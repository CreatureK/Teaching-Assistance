package com.java_web.backend.service;

import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.Service.LLMInitialSyllabusService;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 真实初始教学大纲LLM调用测试类
 * 不使用Mock，直接调用真实的LLM API
 * 用于验证实际的初始教学大纲生成功能
 */
public class RealInitialSyllabusServiceTest {

    private LLMInitialSyllabusService llmService;
    private OpenAIConfig openAIConfig;
    private TestResultWriter testResultWriter;

    @BeforeEach
    void setUp() throws Exception {
        // 手动创建配置
        openAIConfig = new OpenAIConfig();
        openAIConfig.setApiUrl("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
        openAIConfig.setModelName("qwen-plus");
        openAIConfig.setApiKey("sk-728496f8845c495fa7b5c0229a63b2a3");
        
        // 手动创建服务实例
        llmService = new LLMInitialSyllabusService();
        
        // 使用反射设置OpenAIConfig
        Field openAIConfigField = LLMInitialSyllabusService.class.getDeclaredField("openAIConfig");
        openAIConfigField.setAccessible(true);
        openAIConfigField.set(llmService, openAIConfig);
        
        testResultWriter = new TestResultWriter();
    }

    @Test
    void testRealLLMCallForInitialSyllabus() {
        System.out.println("开始真实初始教学大纲LLM调用测试...");
        System.out.println("API配置: " + openAIConfig.getApiUrl());
        System.out.println("模型: " + openAIConfig.getModelName());

        try {
            // 执行真实的LLM调用
            long startTime = System.currentTimeMillis();
            Map<String, Object> result = llmService.generateInitialSyllabus(
                "CS001",           // courseId
                "CS101",           // courseCode
                "计算机科学导论",    // courseTitle
                "中文",            // teachingLanguage
                "计算机学院",       // responsibleCollege
                "专业必修",         // courseCategory
                "李老师",          // principle
                "王老师",          // verifier
                "3",              // credit
                "48",             // courseHour
                "计算机科学基础课程，介绍计算机科学的基本概念和原理", // courseIntroduction
                "掌握计算机科学基础知识，培养计算思维能力", // teachingTarget
                "考试",           // evaluationMode
                "否",            // whetherTechnicalCourse
                "理论",          // assessmentType
                "百分制",        // gradeRecording
                "希望课程内容生动有趣，注重理论与实践结合" // request
            );
            long endTime = System.currentTimeMillis();

            System.out.println("初始教学大纲LLM调用完成，耗时: " + (endTime - startTime) + "ms");

            // 验证结果
            assertNotNull(result, "LLM调用结果不应为空");
            assertNotNull(result.get("course_id"), "课程ID不应为空");
            assertNotNull(result.get("course_code"), "课程代码不应为空");
            assertNotNull(result.get("course_Chinese_name"), "课程中文名不应为空");
            assertNotNull(result.get("course_English_name"), "课程英文名不应为空");
            assertNotNull(result.get("detailed_course_target"), "详细课程目标不应为空");
            assertNotNull(result.get("teaching_content"), "教学内容不应为空");
            assertNotNull(result.get("experimental_projects"), "实验项目不应为空");
            assertNotNull(result.get("textbooks_and_reference_books"), "教材和参考书不应为空");

            // 输出结果到控制台
            System.out.println("=== 初始教学大纲LLM调用结果 ===");
            System.out.println("课程ID: " + result.get("course_id"));
            System.out.println("课程代码: " + result.get("course_code"));
            System.out.println("课程中文名: " + result.get("course_Chinese_name"));
            System.out.println("课程英文名: " + result.get("course_English_name"));
            System.out.println("学分: " + result.get("credit"));
            System.out.println("学时: " + result.get("course_hour"));
            System.out.println("负责学院: " + result.get("responsible_college"));
            System.out.println("课程类别: " + result.get("course_category"));
            System.out.println("课程负责人: " + result.get("principle"));
            System.out.println("审核人: " + result.get("verifier"));
            System.out.println("教学语言: " + result.get("teaching_language"));
            System.out.println("是否技术课程: " + result.get("whether_technical_course"));
            System.out.println("考核类型: " + result.get("assessment_type"));
            System.out.println("成绩记载: " + result.get("grade_recording"));
            System.out.println("课程介绍: " + result.get("course_introduction"));
            System.out.println("教学目标: " + result.get("course_target"));
            System.out.println("评价模式: " + result.get("evaluation_mode"));
            System.out.println("详细课程目标: " + result.get("detailed_course_target"));
            System.out.println("教学内容: " + result.get("teaching_content"));
            System.out.println("实验项目: " + result.get("experimental_projects"));
            System.out.println("教材和参考书: " + result.get("textbooks_and_reference_books"));
            System.out.println("==================================");

            // 保存结果到文件
            testResultWriter.saveJsonResult("RealLLM_InitialSyllabus", result);
            testResultWriter.saveTextResult("RealLLM_InitialSyllabus_Request", 
                "课程ID: CS001\n" +
                "课程代码: CS101\n" +
                "课程标题: 计算机科学导论\n" +
                "教学语言: 中文\n" +
                "负责学院: 计算机学院\n" +
                "课程类别: 专业必修\n" +
                "课程负责人: 李老师\n" +
                "审核人: 王老师\n" +
                "学分: 3\n" +
                "学时: 48\n" +
                "课程介绍: 计算机科学基础课程，介绍计算机科学的基本概念和原理\n" +
                "教学目标: 掌握计算机科学基础知识，培养计算思维能力\n" +
                "评价模式: 考试\n" +
                "是否技术课程: 否\n" +
                "考核类型: 理论\n" +
                "成绩记载: 百分制\n" +
                "用户需求: 希望课程内容生动有趣，注重理论与实践结合\n" +
                "调用耗时: " + (endTime - startTime) + "ms");

            // 验证内容质量
            assertTrue(result.get("course_English_name").toString().length() > 5, "课程英文名应该包含足够的内容");
            assertTrue(result.get("detailed_course_target") instanceof Map, "详细课程目标应该是Map类型");
            assertTrue(result.get("teaching_content") instanceof Map, "教学内容应该是Map类型");
            assertTrue(result.get("experimental_projects") instanceof Map, "实验项目应该是Map类型");
            assertTrue(result.get("textbooks_and_reference_books") instanceof Map, "教材和参考书应该是Map类型");

        } catch (Exception e) {
            System.err.println("初始教学大纲LLM调用失败: " + e.getMessage());
            e.printStackTrace();
            
            // 保存错误信息
            testResultWriter.saveTextResult("RealLLM_InitialSyllabus_Error", 
                "错误时间: " + System.currentTimeMillis() + "\n" +
                "错误信息: " + e.getMessage() + "\n" +
                "错误堆栈: " + e.getStackTrace());
            
            fail("初始教学大纲LLM调用应该成功，但发生了异常: " + e.getMessage());
        }
    }

    @Test
    void testRealLLMCallForDifferentCourseTypes() {
        // 测试不同类型的课程
        String[] courseTitles = {
            "数据结构与算法",
            "计算机网络",
            "软件工程",
            "人工智能导论",
            "数据库系统原理"
        };

        for (String courseTitle : courseTitles) {
            System.out.println("测试课程: " + courseTitle);
            
            try {
                long startTime = System.currentTimeMillis();
                Map<String, Object> result = llmService.generateInitialSyllabus(
                    "TEST_" + courseTitle.hashCode(), // courseId
                    "TEST" + courseTitle.hashCode(),  // courseCode
                    courseTitle,                      // courseTitle
                    "中文",                           // teachingLanguage
                    "计算机学院",                      // responsibleCollege
                    "专业必修",                        // courseCategory
                    "张老师",                         // principle
                    "李老师",                         // verifier
                    "4",                             // credit
                    "64",                            // courseHour
                    courseTitle + "是计算机科学的重要基础课程", // courseIntroduction
                    "掌握" + courseTitle + "的基本理论和实践技能", // teachingTarget
                    "考试",                          // evaluationMode
                    "是",                           // whetherTechnicalCourse
                    "理论+实践",                     // assessmentType
                    "百分制",                        // gradeRecording
                    "请结合当前技术发展趋势，注重实践能力培养" // request
                );
                long endTime = System.currentTimeMillis();

                System.out.println("课程 '" + courseTitle + "' 初始教学大纲LLM调用完成，耗时: " + (endTime - startTime) + "ms");

                // 验证结果
                assertNotNull(result, "课程 '" + courseTitle + "' 的LLM调用结果不应为空");
                assertNotNull(result.get("course_English_name"), "课程英文名不应为空");
                assertNotNull(result.get("detailed_course_target"), "详细课程目标不应为空");
                assertNotNull(result.get("teaching_content"), "教学内容不应为空");
                assertNotNull(result.get("experimental_projects"), "实验项目不应为空");
                assertNotNull(result.get("textbooks_and_reference_books"), "教材和参考书不应为空");

                // 保存结果到文件
                String safeFileName = courseTitle.replaceAll("[^a-zA-Z0-9]", "_");
                testResultWriter.saveJsonResult("RealLLM_InitialSyllabus_" + safeFileName, result);
                testResultWriter.saveTextResult("RealLLM_InitialSyllabus_" + safeFileName + "_Request", 
                    "课程标题: " + courseTitle + "\n" +
                    "课程代码: TEST" + courseTitle.hashCode() + "\n" +
                    "学分: 4\n" +
                    "学时: 64\n" +
                    "用户需求: 请结合当前技术发展趋势，注重实践能力培养\n" +
                    "调用耗时: " + (endTime - startTime) + "ms");

            } catch (Exception e) {
                System.err.println("课程 '" + courseTitle + "' 初始教学大纲LLM调用失败: " + e.getMessage());
                
                // 保存错误信息
                String safeFileName = courseTitle.replaceAll("[^a-zA-Z0-9]", "_");
                testResultWriter.saveTextResult("RealLLM_InitialSyllabus_" + safeFileName + "_Error", 
                    "课程标题: " + courseTitle + "\n" +
                    "错误信息: " + e.getMessage());
            }
        }
    }

    @Test
    void testRealLLMCallForEnglishNameGeneration() {
        System.out.println("开始测试课程英文名生成功能...");

        String[] courseTitles = {
            "高等数学",
            "线性代数",
            "概率论与数理统计",
            "离散数学",
            "数值分析"
        };

        for (String courseTitle : courseTitles) {
            try {
                long startTime = System.currentTimeMillis();
                String englishName = llmService.callLLM(
                    "你是一位资深大学教授，你知道各个学科对应的中文名和英文名分别是什么。\n" +
                    "用户会给你一个课程的中文名，你需要将其翻译成英文，并且返回给用户。注意，你返回的内容应该有且仅有课程的英文名，并且是一个字符串，而不要有其他任何多余的内容\n" +
                    "课程的中文名是" + courseTitle + "，请给出它的英文名。"
                );
                long endTime = System.currentTimeMillis();

                System.out.println("课程 '" + courseTitle + "' 英文名生成完成，耗时: " + (endTime - startTime) + "ms");
                System.out.println("中文名: " + courseTitle + " -> 英文名: " + englishName.trim());

                // 验证结果
                assertNotNull(englishName, "英文名不应为空");
                assertTrue(englishName.trim().length() > 0, "英文名应该包含内容");

                // 保存结果到文件
                String safeFileName = courseTitle.replaceAll("[^a-zA-Z0-9]", "_");
                testResultWriter.saveTextResult("RealLLM_EnglishName_" + safeFileName, 
                    "课程中文名: " + courseTitle + "\n" +
                    "课程英文名: " + englishName.trim() + "\n" +
                    "调用耗时: " + (endTime - startTime) + "ms");

            } catch (Exception e) {
                System.err.println("课程 '" + courseTitle + "' 英文名生成失败: " + e.getMessage());
                
                // 保存错误信息
                String safeFileName = courseTitle.replaceAll("[^a-zA-Z0-9]", "_");
                testResultWriter.saveTextResult("RealLLM_EnglishName_" + safeFileName + "_Error", 
                    "课程中文名: " + courseTitle + "\n" +
                    "错误信息: " + e.getMessage());
            }
        }
    }
} 