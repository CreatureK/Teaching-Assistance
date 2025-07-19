
package com.java_web.backend.service;

import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.DTO.IntroductionAndTargetRequestDTO;
import com.java_web.backend.Common.Entity.IntroductionAndTargetResponse;
import com.java_web.backend.Common.Service.LLMIntroductionAndTargetService;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 真实LLM调用测试类
 * 不使用Mock，直接调用真实的LLM API
 * 用于验证实际的LLM调用结果
 */
public class RealLLMIntroductionAndTargetServiceTest {

    private LLMIntroductionAndTargetService llmService;
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
        llmService = new LLMIntroductionAndTargetService();
        
        // 使用反射设置OpenAIConfig
        Field openAIConfigField = LLMIntroductionAndTargetService.class.getDeclaredField("openAIConfig");
        openAIConfigField.setAccessible(true);
        openAIConfigField.set(llmService, openAIConfig);
        
        testResultWriter = new TestResultWriter();
    }

    @Test
    void testRealLLMCallForIntroductionAndTarget() {
        // 准备测试数据
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId("CS101");
        request.setCourseTitle("计算机科学导论");
        request.setRequest("请结合工程实际，突出应用能力培养，注重理论与实践结合");

        System.out.println("开始真实LLM调用测试...");
        System.out.println("课程ID: " + request.getCourseId());
        System.out.println("课程标题: " + request.getCourseTitle());
        System.out.println("用户需求: " + request.getRequest());
        System.out.println("API配置: " + openAIConfig.getApiUrl());
        System.out.println("模型: " + openAIConfig.getModelName());

        try {
            // 执行真实的LLM调用
            long startTime = System.currentTimeMillis();
            IntroductionAndTargetResponse result = llmService.generateIntroductionAndTarget(request);
            long endTime = System.currentTimeMillis();

            System.out.println("LLM调用完成，耗时: " + (endTime - startTime) + "ms");

            // 验证结果
            assertNotNull(result, "LLM调用结果不应为空");
            assertNotNull(result.getCourseId(), "课程ID不应为空");
            assertNotNull(result.getCourseIntroduction(), "课程介绍不应为空");
            assertNotNull(result.getTeachingTarget(), "教学目标不应为空");

            // 输出结果到控制台
            System.out.println("=== LLM调用结果 ===");
            System.out.println("课程ID: " + result.getCourseId());
            System.out.println("课程介绍: " + result.getCourseIntroduction());
            System.out.println("教学目标: " + result.getTeachingTarget());
            System.out.println("==================");

            // 保存结果到文件
            testResultWriter.saveJsonResult("RealLLM_IntroductionAndTarget", result);
            testResultWriter.saveTextResult("RealLLM_IntroductionAndTarget_Request", 
                "课程ID: " + request.getCourseId() + "\n" +
                "课程标题: " + request.getCourseTitle() + "\n" +
                "用户需求: " + request.getRequest() + "\n" +
                "调用耗时: " + (endTime - startTime) + "ms");

            // 验证内容质量
            assertTrue(result.getCourseIntroduction().length() > 50, "课程介绍应该包含足够的内容");
            assertTrue(result.getTeachingTarget().length() > 50, "教学目标应该包含足够的内容");
            assertTrue(result.getCourseIntroduction().contains("计算机") || 
                      result.getCourseIntroduction().contains("编程") || 
                      result.getCourseIntroduction().contains("技术"), 
                      "课程介绍应该包含相关专业内容");

        } catch (Exception e) {
            System.err.println("LLM调用失败: " + e.getMessage());
            e.printStackTrace();
            
            // 保存错误信息
            testResultWriter.saveTextResult("RealLLM_IntroductionAndTarget_Error", 
                "错误时间: " + System.currentTimeMillis() + "\n" +
                "错误信息: " + e.getMessage() + "\n" +
                "错误堆栈: " + e.getStackTrace());
            
            fail("LLM调用应该成功，但发生了异常: " + e.getMessage());
        }
    }

    @Test
    void testRealLLMCallForDetailedTeachingContent() {
        // 准备测试数据
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId("MATH101");
        request.setCourseTitle("高等数学");
        request.setRequest("请结合工程应用，注重数学建模能力培养，包含微积分、线性代数等核心内容");

        System.out.println("开始详细教学内容LLM调用测试...");

        try {
            // 执行真实的LLM调用
            long startTime = System.currentTimeMillis();
            IntroductionAndTargetResponse result = llmService.generateDetailedTeachingContentAndTarget(
                request.getCourseId(), 
                request.getCourseTitle(), 
                "4", // 学分
                "64", // 学时
                "数学学院", 
                "专业必修"
            );
            long endTime = System.currentTimeMillis();

            System.out.println("详细教学内容LLM调用完成，耗时: " + (endTime - startTime) + "ms");

            // 验证结果
            assertNotNull(result, "详细教学内容LLM调用结果不应为空");
            assertNotNull(result.getCourseIntroduction(), "详细课程介绍不应为空");
            assertNotNull(result.getTeachingTarget(), "详细教学目标不应为空");

            // 输出结果到控制台
            System.out.println("=== 详细教学内容LLM调用结果 ===");
            System.out.println("课程ID: " + result.getCourseId());
            System.out.println("课程介绍: " + result.getCourseIntroduction());
            System.out.println("教学目标: " + result.getTeachingTarget());
            System.out.println("==================================");

            // 保存结果到文件
            testResultWriter.saveJsonResult("RealLLM_DetailedTeachingContent", result);
            testResultWriter.saveTextResult("RealLLM_DetailedTeachingContent_Request", 
                "课程ID: " + request.getCourseId() + "\n" +
                "课程标题: " + request.getCourseTitle() + "\n" +
                "用户需求: " + request.getRequest() + "\n" +
                "学分: 4\n" +
                "学时: 64\n" +
                "学院: 数学学院\n" +
                "类别: 专业必修\n" +
                "调用耗时: " + (endTime - startTime) + "ms");

        } catch (Exception e) {
            System.err.println("详细教学内容LLM调用失败: " + e.getMessage());
            e.printStackTrace();
            
            // 保存错误信息
            testResultWriter.saveTextResult("RealLLM_DetailedTeachingContent_Error", 
                "错误时间: " + System.currentTimeMillis() + "\n" +
                "错误信息: " + e.getMessage() + "\n" +
                "错误堆栈: " + e.getStackTrace());
            
            fail("详细教学内容LLM调用应该成功，但发生了异常: " + e.getMessage());
        }
    }

    @Test
    void testRealLLMCallWithDifferentCourseTypes() {
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
            
            IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
            request.setCourseId("TEST_" + courseTitle.hashCode());
            request.setCourseTitle(courseTitle);
            request.setRequest("请结合当前技术发展趋势，注重实践能力培养");

            try {
                long startTime = System.currentTimeMillis();
                IntroductionAndTargetResponse result = llmService.generateIntroductionAndTarget(request);
                long endTime = System.currentTimeMillis();

                System.out.println("课程 '" + courseTitle + "' LLM调用完成，耗时: " + (endTime - startTime) + "ms");

                // 验证结果
                assertNotNull(result, "课程 '" + courseTitle + "' 的LLM调用结果不应为空");
                assertNotNull(result.getCourseIntroduction(), "课程介绍不应为空");
                assertNotNull(result.getTeachingTarget(), "教学目标不应为空");

                // 保存结果到文件
                String safeFileName = courseTitle.replaceAll("[^a-zA-Z0-9]", "_");
                testResultWriter.saveJsonResult("RealLLM_" + safeFileName, result);
                testResultWriter.saveTextResult("RealLLM_" + safeFileName + "_Request", 
                    "课程标题: " + courseTitle + "\n" +
                    "用户需求: " + request.getRequest() + "\n" +
                    "调用耗时: " + (endTime - startTime) + "ms");

            } catch (Exception e) {
                System.err.println("课程 '" + courseTitle + "' LLM调用失败: " + e.getMessage());
                
                // 保存错误信息
                String safeFileName = courseTitle.replaceAll("[^a-zA-Z0-9]", "_");
                testResultWriter.saveTextResult("RealLLM_" + safeFileName + "_Error", 
                    "课程标题: " + courseTitle + "\n" +
                    "错误信息: " + e.getMessage());
            }
        }
    }
} 