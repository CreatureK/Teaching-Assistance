package com.java_web.backend.service;

import com.java_web.backend.Entity.IntroductionAndTargetRequest;
import com.java_web.backend.Entity.IntroductionAndTargetResponse;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试结果输出功能演示类
 * 用于展示如何将大模型返回的内容保存到test_result_txt目录
 */
@ExtendWith(MockitoExtension.class)
public class TestResultOutputDemoTest {

    private TestResultWriter testResultWriter;

    @BeforeEach
    void setUp() {
        testResultWriter = new TestResultWriter();
    }

    @Test
    void testSaveTextResult() {
        // 模拟大模型返回的文本内容
        String llmResponse = """
            课程介绍：
            本课程是面向工程类专业本科生的高等数学课程，内容涵盖函数、极限与连续、导数与微分、积分学、多元函数微积分、常微分方程、级数等基础理论知识。
            
            教学目标：
            通过本课程的学习，学生应掌握高等数学的基本理论和运算方法，具备运用数学工具分析和解决工程实际问题的能力。
            """;

        // 保存文本结果
        testResultWriter.saveTextResult("Demo_TextResult", llmResponse);
    }

    @Test
    void testSaveJsonResult() {
        // 模拟大模型返回的JSON响应对象
        IntroductionAndTargetResponse response = new IntroductionAndTargetResponse();
        response.setCourseId("1");
        response.setCourseIntroduction("本课程是面向工程类专业本科生的高等数学课程，内容涵盖函数、极限与连续、导数与微分、积分学、多元函数微积分、常微分方程、级数等基础理论知识。");
        response.setTeachingTarget("通过本课程的学习，学生应掌握高等数学的基本理论和运算方法，具备运用数学工具分析和解决工程实际问题的能力。");

        // 保存JSON结果
        testResultWriter.saveJsonResult("Demo_JsonResult", response);
    }

    @Test
    void testSaveMarkdownResult() {
        // 模拟大模型返回的Markdown内容
        String markdownContent = """
            # 高等数学课程介绍与教学目标
            
            ## 课程介绍
            
            本课程是面向工程类专业本科生的高等数学课程，内容涵盖：
            
            - 函数
            - 极限与连续
            - 导数与微分
            - 积分学
            - 多元函数微积分
            - 常微分方程
            - 级数等基础理论知识
            
            ## 教学目标
            
            通过本课程的学习，学生应：
            
            1. 掌握高等数学的基本理论
            2. 掌握运算方法
            3. 具备运用数学工具分析和解决工程实际问题的能力
            
            ## 课程特色
            
            - 理论与实践相结合
            - 注重应用能力培养
            - 突出工程实际应用
            """;

        // 保存Markdown结果
        testResultWriter.saveMarkdownResult("Demo_MarkdownResult", markdownContent);
    }

    @Test
    void testSaveRawResponse() {
        // 模拟大模型返回的原始响应
        String rawResponse = """
            {
                "choices": [
                    {
                        "message": {
                            "content": "{\\"course_introduction\\": \\"本课程是面向工程类专业本科生的高等数学课程，内容涵盖函数、极限与连续、导数与微分、积分学、多元函数微积分、常微分方程、级数等基础理论知识。\\", \\"teaching_target\\": \\"通过本课程的学习，学生应掌握高等数学的基本理论和运算方法，具备运用数学工具分析和解决工程实际问题的能力。\\"}"
                        }
                    }
                ],
                "usage": {
                    "prompt_tokens": 150,
                    "completion_tokens": 200,
                    "total_tokens": 350
                }
            }
            """;

        // 保存原始响应
        testResultWriter.saveRawResponse("Demo_RawResponse", rawResponse);
    }

    @Test
    void testSaveComplexData() {
        // 模拟复杂的测试数据
        Map<String, Object> complexData = new HashMap<>();
        complexData.put("testName", "复杂数据结构测试");
        complexData.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("courseId", "1");
        requestData.put("courseTitle", "高等数学");
        requestData.put("request", "请结合工程实际，突出应用能力培养");
        complexData.put("request", requestData);
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("courseId", "1");
        responseData.put("courseIntroduction", "课程介绍内容");
        responseData.put("teachingTarget", "教学目标内容");
        complexData.put("response", responseData);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("processingTime", "1.5秒");
        metadata.put("modelUsed", "qwen-plus");
        metadata.put("status", "success");
        complexData.put("metadata", metadata);

        // 保存复杂数据结构
        testResultWriter.saveJsonResult("Demo_ComplexData", complexData);
    }

    @Test
    void testSaveMultipleFormats() {
        // 模拟同一个测试结果的不同格式
        String courseTitle = "高等数学";
        String courseIntroduction = "本课程是面向工程类专业本科生的高等数学课程，内容涵盖函数、极限与连续、导数与微分、积分学、多元函数微积分、常微分方程、级数等基础理论知识。";
        String teachingTarget = "通过本课程的学习，学生应掌握高等数学的基本理论和运算方法，具备运用数学工具分析和解决工程实际问题的能力。";

        // 保存为JSON格式
        IntroductionAndTargetResponse jsonResponse = new IntroductionAndTargetResponse();
        jsonResponse.setCourseId("1");
        jsonResponse.setCourseIntroduction(courseIntroduction);
        jsonResponse.setTeachingTarget(teachingTarget);
        testResultWriter.saveJsonResult("Demo_MultiFormat", jsonResponse);

        // 保存为Markdown格式
        String markdownContent = String.format("""
            # %s
            
            ## 课程介绍
            
            %s
            
            ## 教学目标
            
            %s
            """, courseTitle, courseIntroduction, teachingTarget);
        testResultWriter.saveMarkdownResult("Demo_MultiFormat", markdownContent);

        // 保存为纯文本格式
        String textContent = String.format("""
            课程名称：%s
            
            课程介绍：
            %s
            
            教学目标：
            %s
            """, courseTitle, courseIntroduction, teachingTarget);
        testResultWriter.saveTextResult("Demo_MultiFormat", textContent);
    }
} 