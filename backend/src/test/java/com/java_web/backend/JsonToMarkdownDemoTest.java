package com.java_web.backend;

import com.java_web.backend.Entity.JsonToMarkdownRequest;
import com.java_web.backend.Service.LLMJsonToMarkdownService;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * JSON转Markdown功能演示测试类
 */
@SpringBootTest
public class JsonToMarkdownDemoTest {

    @Autowired
    private LLMJsonToMarkdownService jsonToMarkdownService;

    private TestResultWriter testResultWriter = new TestResultWriter();

    @Test
    void testCourseInfoJsonToMarkdown() {
        // 课程信息的JSON转Markdown演示
        String courseJson = """
            {
                "course_info": {
                    "title": "高等数学",
                    "credits": 4,
                    "duration": "16周",
                    "instructor": "张教授",
                    "semester": "2024春季"
                },
                "chapters": [
                    {
                        "title": "函数与极限",
                        "topics": ["函数概念", "极限定义", "连续性"],
                        "hours": 8,
                        "difficulty": "基础"
                    },
                    {
                        "title": "导数与微分",
                        "topics": ["导数概念", "求导法则", "微分应用"],
                        "hours": 10,
                        "difficulty": "中等"
                    },
                    {
                        "title": "积分学",
                        "topics": ["不定积分", "定积分", "积分应用"],
                        "hours": 12,
                        "difficulty": "中等"
                    }
                ],
                "objectives": [
                    "掌握高等数学的基本概念和理论",
                    "培养数学思维和逻辑推理能力",
                    "提高运用数学工具解决实际问题的能力"
                ],
                "assessment": {
                    "midterm": 30,
                    "final": 50,
                    "homework": 20
                }
            }
            """;

        try {
            String markdownContent = jsonToMarkdownService.convertJsonToMarkdown(
                courseJson, "markdown", "使用表格展示课程信息，使用列表展示章节内容"
            );
            
            testResultWriter.saveTextResult("Demo_CourseInfo_JsonToMarkdown", markdownContent);
            testResultWriter.saveJsonResult("Demo_CourseInfo_OriginalJson", courseJson);
            
            System.out.println("课程信息JSON转Markdown完成");
        } catch (Exception e) {
            System.err.println("转换失败: " + e.getMessage());
        }
    }

    @Test
    void testStudentInfoJsonToMarkdown() {
        // 学生信息的JSON转Markdown演示
        String studentJson = """
            {
                "student_info": {
                    "name": "李小明",
                    "student_id": "2024001",
                    "major": "计算机科学与技术",
                    "grade": "大二",
                    "gpa": 3.8
                },
                "courses": [
                    {
                        "name": "高等数学",
                        "score": 85,
                        "credits": 4,
                        "semester": "2024春季"
                    },
                    {
                        "name": "线性代数",
                        "score": 92,
                        "credits": 3,
                        "semester": "2024春季"
                    },
                    {
                        "name": "程序设计基础",
                        "score": 88,
                        "credits": 4,
                        "semester": "2024春季"
                    }
                ],
                "skills": ["Java", "Python", "C++", "数据结构", "算法设计"],
                "activities": [
                    {
                        "name": "编程竞赛",
                        "role": "参赛者",
                        "achievement": "校级二等奖"
                    },
                    {
                        "name": "学生会",
                        "role": "技术部干事",
                        "achievement": "优秀干事"
                    }
                ]
            }
            """;

        try {
            String markdownContent = jsonToMarkdownService.convertJsonToMarkdown(
                studentJson, "markdown", "使用表格展示学生基本信息和课程成绩，使用列表展示技能和活动"
            );
            
            testResultWriter.saveTextResult("Demo_StudentInfo_JsonToMarkdown", markdownContent);
            testResultWriter.saveJsonResult("Demo_StudentInfo_OriginalJson", studentJson);
            
            System.out.println("学生信息JSON转Markdown完成");
        } catch (Exception e) {
            System.err.println("转换失败: " + e.getMessage());
        }
    }

    @Test
    void testProjectInfoJsonToMarkdown() {
        // 项目信息的JSON转Markdown演示
        String projectJson = """
            {
                "project_info": {
                    "name": "智能教学辅助系统",
                    "type": "Web应用",
                    "status": "开发中",
                    "start_date": "2024-01-15",
                    "expected_end": "2024-06-30",
                    "budget": 50000
                },
                "team_members": [
                    {
                        "name": "张三",
                        "role": "项目经理",
                        "skills": ["项目管理", "需求分析", "团队协作"],
                        "experience": "5年"
                    },
                    {
                        "name": "李四",
                        "role": "前端开发",
                        "skills": ["Vue.js", "React", "TypeScript"],
                        "experience": "3年"
                    },
                    {
                        "name": "王五",
                        "role": "后端开发",
                        "skills": ["Java", "Spring Boot", "MySQL"],
                        "experience": "4年"
                    }
                ],
                "technologies": {
                    "frontend": ["Vue.js", "Element UI", "Axios"],
                    "backend": ["Spring Boot", "MyBatis", "MySQL"],
                    "deployment": ["Docker", "Nginx", "Linux"]
                },
                "milestones": [
                    {
                        "name": "需求分析完成",
                        "date": "2024-02-15",
                        "status": "已完成"
                    },
                    {
                        "name": "系统设计完成",
                        "date": "2024-03-15",
                        "status": "已完成"
                    },
                    {
                        "name": "开发完成",
                        "date": "2024-05-30",
                        "status": "进行中"
                    },
                    {
                        "name": "测试完成",
                        "date": "2024-06-15",
                        "status": "待开始"
                    }
                ]
            }
            """;

        try {
            String markdownContent = jsonToMarkdownService.convertJsonToMarkdown(
                projectJson, "markdown", "使用表格展示项目信息和里程碑，使用列表展示团队成员和技术栈"
            );
            
            testResultWriter.saveTextResult("Demo_ProjectInfo_JsonToMarkdown", markdownContent);
            testResultWriter.saveJsonResult("Demo_ProjectInfo_OriginalJson", projectJson);
            
            System.out.println("项目信息JSON转Markdown完成");
        } catch (Exception e) {
            System.err.println("转换失败: " + e.getMessage());
        }
    }

    @Test
    void testBatchConvertDemo() {
        // 批量转换演示
        String[] jsonContents = {
            // 简单的个人信息
            "{\"name\": \"张三\", \"age\": 25, \"city\": \"北京\"}",
            
            // 课程信息
            "{\"course\": \"Java程序设计\", \"instructor\": \"李教授\", \"students\": 45}",
            
            // 产品信息
            "{\"product\": \"智能手机\", \"brand\": \"华为\", \"price\": 3999, \"features\": [\"5G\", \"AI摄影\", \"快充\"]}"
        };

        try {
            Map<String, Object> results = jsonToMarkdownService.batchConvert(
                jsonContents, "markdown", "使用简洁的格式展示信息"
            );
            
            testResultWriter.saveJsonResult("Demo_BatchConvert_Results", results);
            
            System.out.println("批量转换完成");
            System.out.println("结果摘要: " + results.get("summary"));
        } catch (Exception e) {
            System.err.println("批量转换失败: " + e.getMessage());
        }
    }
} 