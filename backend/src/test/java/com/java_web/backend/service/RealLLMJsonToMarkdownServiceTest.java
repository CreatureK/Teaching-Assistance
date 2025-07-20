package com.java_web.backend.service;

import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.Service.LLMJsonToMarkdownService;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 真实JSON转Markdown LLM调用测试类
 * 不使用Mock，直接调用真实的LLM API
 * 用于验证实际的JSON转Markdown功能
 */
public class RealLLMJsonToMarkdownServiceTest {

    private LLMJsonToMarkdownService llmService;
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
        llmService = new LLMJsonToMarkdownService();
        
        // 使用反射设置OpenAIConfig
        Field openAIConfigField = LLMJsonToMarkdownService.class.getDeclaredField("openAIConfig");
        openAIConfigField.setAccessible(true);
        openAIConfigField.set(llmService, openAIConfig);
        
        testResultWriter = new TestResultWriter();
    }

    @Test
    void testRealLLMCallForSyllabusJsonToMarkdown() {
        System.out.println("开始真实JSON转Markdown LLM调用测试...");
        System.out.println("API配置: " + openAIConfig.getApiUrl());
        System.out.println("模型: " + openAIConfig.getModelName());

        // 准备测试用的Syllabus JSON数据
        String syllabusJson = """
            {
              "course_id": "CS001",
              "course_code": "CS101",
              "course_Chinese_name": "计算机科学导论",
              "course_English_name": "Introduction to Computer Science",
              "credit": "3",
              "course_hour": "48",
              "responsible_college": "计算机学院",
              "course_category": "专业必修",
              "principle": "李老师",
              "verifier": "王老师",
              "teaching_language": "中文",
              "whether_technical_course": "否",
              "assessment_type": "理论",
              "grade_recording": "百分制",
              "course_introduction": "计算机科学基础课程，介绍计算机科学的基本概念和原理",
              "course_target": "掌握计算机科学基础知识，培养计算思维能力",
              "evaluation_mode": "考试",
              "detailed_course_target": [
                {
                  "target_number": "1",
                  "target": "理解计算机科学的基本概念和原理",
                  "support_graduation_requirement": "支撑毕业要求1：具备扎实的计算机科学理论基础"
                },
                {
                  "target_number": "2",
                  "target": "掌握基本的编程思维和算法思想",
                  "support_graduation_requirement": "支撑毕业要求2：具备良好的编程能力和算法设计能力"
                },
                {
                  "target_number": "3",
                  "target": "培养计算思维和问题解决能力",
                  "support_graduation_requirement": "支撑毕业要求3：具备分析和解决复杂问题的能力"
                },
                {
                  "target_number": "4",
                  "target": "了解计算机科学的发展趋势和应用领域",
                  "support_graduation_requirement": "支撑毕业要求4：具备终身学习和适应技术发展的能力"
                }
              ],
              "teaching_content": [
                {
                  "unit_number": "1",
                  "content": "计算机科学概述：计算机的发展历史、基本组成和工作原理",
                  "ideological_and_political_integration": "结合中国计算机发展历程，培养学生的爱国情怀和科技自信",
                  "time_allocation": "8",
                  "detailed_time_allocation": {
                    "lecture": "6",
                    "experiment": "0",
                    "computer_practice": "2",
                    "practice": "0",
                    "extracurricular": "0"
                  }
                },
                {
                  "unit_number": "2",
                  "content": "算法与数据结构基础：基本算法思想、线性数据结构",
                  "ideological_and_political_integration": "通过算法优化案例，培养学生的工匠精神和创新意识",
                  "time_allocation": "12",
                  "detailed_time_allocation": {
                    "lecture": "8",
                    "experiment": "0",
                    "computer_practice": "4",
                    "practice": "0",
                    "extracurricular": "0"
                  }
                },
                {
                  "unit_number": "3",
                  "content": "程序设计基础：编程语言概述、基本语法和程序结构",
                  "ideological_and_political_integration": "强调编程规范的重要性，培养学生的职业素养和责任感",
                  "time_allocation": "16",
                  "detailed_time_allocation": {
                    "lecture": "10",
                    "experiment": "0",
                    "computer_practice": "6",
                    "practice": "0",
                    "extracurricular": "0"
                  }
                },
                {
                  "unit_number": "4",
                  "content": "软件工程基础：软件开发流程、软件测试和维护",
                  "ideological_and_political_integration": "通过团队协作项目，培养学生的团队精神和沟通能力",
                  "time_allocation": "12",
                  "detailed_time_allocation": {
                    "lecture": "8",
                    "experiment": "0",
                    "computer_practice": "4",
                    "practice": "0",
                    "extracurricular": "0"
                  }
                }
              ],
              "experimental_projects": [
                {
                  "unit_number": "1",
                  "experiment_name": "计算机硬件认知实验",
                  "content_and_requirements": "了解计算机硬件组成，完成硬件拆装和组装",
                  "experiment_hour": "2",
                  "experiment_type": "认知性实验"
                },
                {
                  "unit_number": "2",
                  "experiment_name": "算法实现实验",
                  "content_and_requirements": "使用编程语言实现基本算法，如排序、查找等",
                  "experiment_hour": "4",
                  "experiment_type": "验证性实验"
                },
                {
                  "unit_number": "3",
                  "experiment_name": "简单程序开发实验",
                  "content_and_requirements": "开发简单的控制台应用程序，如计算器、学生管理系统等",
                  "experiment_hour": "6",
                  "experiment_type": "综合性实验"
                }
              ],
              "textbooks_and_reference_books": [
                {
                  "book_name": "计算机科学导论",
                  "editor": "张三",
                  "ISBN": "978-7-111-12345-6",
                  "publisher": "机械工业出版社",
                  "publication_year": "2023"
                },
                {
                  "book_name": "算法导论",
                  "editor": "Thomas H. Cormen",
                  "ISBN": "978-7-111-23456-7",
                  "publisher": "机械工业出版社",
                  "publication_year": "2022"
                },
                {
                  "book_name": "程序设计基础",
                  "editor": "李四",
                  "ISBN": "978-7-111-34567-8",
                  "publisher": "清华大学出版社",
                  "publication_year": "2023"
                }
              ]
            }
            """;

        try {
            // 执行真实的LLM调用
            long startTime = System.currentTimeMillis();
            String result = llmService.convertJsonToMarkdown(syllabusJson, "markdown", "学术文档样式");
            long endTime = System.currentTimeMillis();

            System.out.println("JSON转Markdown LLM调用完成，耗时: " + (endTime - startTime) + "ms");

            // 验证结果
            assertNotNull(result, "LLM调用结果不应为空");
            assertTrue(result.length() > 100, "转换结果应该包含足够的内容");

            // 输出结果到控制台
            System.out.println("=== JSON转Markdown LLM调用结果 ===");
            System.out.println("转换结果长度: " + result.length() + " 字符");
            System.out.println("转换结果预览（前500字符）:");
            System.out.println(result.substring(0, Math.min(500, result.length())));
            if (result.length() > 500) {
                System.out.println("...（内容过长，已截断）");
            }
            System.out.println("==================================");

            // 保存结果到文件
            testResultWriter.saveMarkdownResult("RealLLM_JsonToMarkdown_Syllabus", result);
            testResultWriter.saveTextResult("RealLLM_JsonToMarkdown_Syllabus_Request", 
                "JSON内容长度: " + syllabusJson.length() + " 字符\n" +
                "输出格式: markdown\n" +
                "自定义样式: 学术文档样式\n" +
                "调用耗时: " + (endTime - startTime) + "ms\n\n" +
                "原始JSON内容:\n" + syllabusJson);

            // 验证内容质量
            assertTrue(result.contains("#"), "转换结果应该包含Markdown标题");
            assertTrue(result.contains("计算机科学导论") || result.contains("CS101"), "转换结果应该包含课程信息");
            assertTrue(result.contains("教学目标") || result.contains("课程目标"), "转换结果应该包含课程目标相关内容");

        } catch (Exception e) {
            System.err.println("JSON转Markdown LLM调用失败: " + e.getMessage());
            e.printStackTrace();
            
            // 保存错误信息
            testResultWriter.saveTextResult("RealLLM_JsonToMarkdown_Syllabus_Error", 
                "错误时间: " + System.currentTimeMillis() + "\n" +
                "错误信息: " + e.getMessage() + "\n" +
                "错误堆栈: " + e.getStackTrace());
            
            fail("JSON转Markdown LLM调用应该成功，但发生了异常: " + e.getMessage());
        }
    }

    @Test
    void testRealLLMCallForDifferentJsonTypes() {
        // 测试不同类型的JSON内容
        String[] jsonContents = {
            // 简单对象JSON
            """
            {
              "course_id": "MATH101",
              "course_name": "高等数学",
              "credit": "4",
              "instructor": "张教授",
              "description": "微积分基础课程"
            }
            """,
            
            // 数组JSON
            """
            [
              {"name": "第一章", "content": "函数与极限", "hours": 8},
              {"name": "第二章", "content": "导数与微分", "hours": 10},
              {"name": "第三章", "content": "积分学", "hours": 12}
            ]
            """,
            
            // 嵌套对象JSON
            """
            {
              "student": {
                "id": "2023001",
                "name": "李明",
                "major": "计算机科学",
                "courses": [
                  {"code": "CS101", "name": "程序设计", "grade": 85},
                  {"code": "MATH101", "name": "高等数学", "grade": 92}
                ]
              }
            }
            """,
            
            // 复杂教学大纲JSON（简化版）
            """
            {
              "course_info": {
                "name": "数据结构与算法",
                "code": "CS201",
                "credits": 4
              },
              "objectives": [
                "掌握基本数据结构",
                "理解算法设计思想",
                "提高编程实践能力"
              ],
              "schedule": {
                "week1": "线性表",
                "week2": "栈和队列",
                "week3": "树结构"
              }
            }
            """
        };

        for (int i = 0; i < jsonContents.length; i++) {
            String jsonContent = jsonContents[i];
            System.out.println("测试JSON类型 " + (i + 1) + "...");
            
            try {
                long startTime = System.currentTimeMillis();
                String result = llmService.convertJsonToMarkdown(jsonContent, "markdown", "通用样式");
                long endTime = System.currentTimeMillis();

                System.out.println("JSON类型 " + (i + 1) + " 转换完成，耗时: " + (endTime - startTime) + "ms");

                // 验证结果
                assertNotNull(result, "转换结果不应为空");
                assertTrue(result.length() > 10, "转换结果应该包含内容");

                // 保存结果到文件
                testResultWriter.saveMarkdownResult("RealLLM_JsonToMarkdown_Type" + (i + 1), result);
                testResultWriter.saveTextResult("RealLLM_JsonToMarkdown_Type" + (i + 1) + "_Request", 
                    "JSON类型: " + (i + 1) + "\n" +
                    "JSON内容长度: " + jsonContent.length() + " 字符\n" +
                    "调用耗时: " + (endTime - startTime) + "ms\n\n" +
                    "原始JSON内容:\n" + jsonContent);

            } catch (Exception e) {
                System.err.println("JSON类型 " + (i + 1) + " 转换失败: " + e.getMessage());
                
                // 保存错误信息
                testResultWriter.saveTextResult("RealLLM_JsonToMarkdown_Type" + (i + 1) + "_Error", 
                    "JSON类型: " + (i + 1) + "\n" +
                    "错误信息: " + e.getMessage());
            }
        }
    }

    @Test
    void testRealLLMCallForBatchConversion() {
        System.out.println("开始批量JSON转Markdown测试...");

        // 准备多个JSON内容
        String[] jsonContents = {
            // 课程信息
            """
            {
              "course": {
                "name": "软件工程",
                "code": "CS301",
                "credits": 3,
                "description": "软件开发生命周期管理"
              }
            }
            """,
            
            // 学生信息
            """
            {
              "students": [
                {"id": "001", "name": "张三", "grade": "A"},
                {"id": "002", "name": "李四", "grade": "B+"},
                {"id": "003", "name": "王五", "grade": "A-"}
              ]
            }
            """,
            
            // 实验项目
            """
            {
              "experiments": [
                {
                  "name": "需求分析实验",
                  "duration": "2小时",
                  "tools": ["Visio", "Axure"]
                },
                {
                  "name": "系统设计实验", 
                  "duration": "3小时",
                  "tools": ["UML", "Rational Rose"]
                }
              ]
            }
            """
        };

        try {
            long startTime = System.currentTimeMillis();
            Map<String, Object> results = llmService.batchConvert(jsonContents, "markdown", "表格样式");
            long endTime = System.currentTimeMillis();

            System.out.println("批量转换完成，耗时: " + (endTime - startTime) + "ms");

            // 验证结果
            assertNotNull(results, "批量转换结果不应为空");
            assertTrue(results.containsKey("summary"), "应该包含汇总信息");

            // 输出汇总信息
            Map<String, Object> summary = (Map<String, Object>) results.get("summary");
            System.out.println("=== 批量转换汇总 ===");
            System.out.println("总数: " + summary.get("total"));
            System.out.println("成功: " + summary.get("success"));
            System.out.println("失败: " + summary.get("error"));
            System.out.println("成功率: " + summary.get("successRate"));
            System.out.println("==================");

            // 保存结果到文件
            testResultWriter.saveJsonResult("RealLLM_JsonToMarkdown_Batch", results);
            testResultWriter.saveTextResult("RealLLM_JsonToMarkdown_Batch_Request", 
                "批量转换测试\n" +
                "JSON文件数量: " + jsonContents.length + "\n" +
                "输出格式: markdown\n" +
                "自定义样式: 表格样式\n" +
                "调用耗时: " + (endTime - startTime) + "ms\n\n" +
                "转换汇总:\n" +
                "总数: " + summary.get("total") + "\n" +
                "成功: " + summary.get("success") + "\n" +
                "失败: " + summary.get("error") + "\n" +
                "成功率: " + summary.get("successRate"));

        } catch (Exception e) {
            System.err.println("批量转换失败: " + e.getMessage());
            e.printStackTrace();
            
            // 保存错误信息
            testResultWriter.saveTextResult("RealLLM_JsonToMarkdown_Batch_Error", 
                "错误时间: " + System.currentTimeMillis() + "\n" +
                "错误信息: " + e.getMessage() + "\n" +
                "错误堆栈: " + e.getStackTrace());
            
            fail("批量转换应该成功，但发生了异常: " + e.getMessage());
        }
    }

    @Test
    void testRealLLMCallForCustomStyles() {
        System.out.println("开始测试不同自定义样式的转换效果...");

        String testJson = """
            {
              "title": "课程大纲",
              "sections": [
                {
                  "name": "第一章",
                  "content": "基础知识",
                  "duration": "2周"
                },
                {
                  "name": "第二章", 
                  "content": "进阶内容",
                  "duration": "3周"
                }
              ]
            }
            """;

        String[] customStyles = {
            "学术文档样式",
            "简洁列表样式", 
            "表格样式",
            "卡片样式",
            "默认样式"
        };

        for (String style : customStyles) {
            System.out.println("测试样式: " + style);
            
            try {
                long startTime = System.currentTimeMillis();
                String result = llmService.convertJsonToMarkdown(testJson, "markdown", style);
                long endTime = System.currentTimeMillis();

                System.out.println("样式 '" + style + "' 转换完成，耗时: " + (endTime - startTime) + "ms");

                // 验证结果
                assertNotNull(result, "转换结果不应为空");
                assertTrue(result.length() > 10, "转换结果应该包含内容");

                // 保存结果到文件
                String safeStyleName = style.replaceAll("[^a-zA-Z0-9]", "_");
                testResultWriter.saveMarkdownResult("RealLLM_JsonToMarkdown_Style_" + safeStyleName, result);
                testResultWriter.saveTextResult("RealLLM_JsonToMarkdown_Style_" + safeStyleName + "_Request", 
                    "自定义样式: " + style + "\n" +
                    "JSON内容长度: " + testJson.length() + " 字符\n" +
                    "调用耗时: " + (endTime - startTime) + "ms\n\n" +
                    "原始JSON内容:\n" + testJson);

            } catch (Exception e) {
                System.err.println("样式 '" + style + "' 转换失败: " + e.getMessage());
                
                // 保存错误信息
                String safeStyleName = style.replaceAll("[^a-zA-Z0-9]", "_");
                testResultWriter.saveTextResult("RealLLM_JsonToMarkdown_Style_" + safeStyleName + "_Error", 
                    "自定义样式: " + style + "\n" +
                    "错误信息: " + e.getMessage());
            }
        }
    }
} 