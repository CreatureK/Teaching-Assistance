package com.java_web.backend.Teacher.Controller;

import com.java_web.backend.Teacher.Service.SyllabusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/teacher")
public class CourseOutlineController {

    @Autowired
    private SyllabusService syllabusService;

    /**
     * 显示课程大纲编辑页面
     */
    @GetMapping("/course/{courseId}/outline")
    public String showCourseOutline(@PathVariable Integer courseId, 
                                  HttpServletRequest request, 
                                  Model model) {
        // 获取当前登录用户ID（从请求属性中获取，由拦截器设置）
        Integer teacherId = (Integer) request.getAttribute("userId");
        
        try {
            // 验证教师是否有权限访问该课程
            syllabusService.getCourseSyllabus(courseId, teacherId);
            
            // 获取课程大纲内容
            String outlineContent = syllabusService.getCourseOutlineContent(courseId);
            
            // 如果没有大纲内容，设置默认值
            if (outlineContent == null || outlineContent.trim().isEmpty()) {
                outlineContent = "# 课程大纲\n\n" +
                        "## 第一章 课程介绍\n\n" +
                        "### 1.1 教学目标\n\n" +
                        "### 1.2 课程内容\n\n" +
                        "## 第二章 基础知识\n\n" +
                        "### 2.1 基本概念\n\n" +
                        "### 2.2 基础理论\n\n" +
                        "## 第三章 进阶内容\n\n" +
                        "### 3.1 高级应用\n\n" +
                        "### 3.2 实践案例\n\n" +
                        "## 第四章 考核评估\n\n" +
                        "### 4.1 考核方式\n\n" +
                        "### 4.2 评分标准";
            }
            
            // 添加模型数据
            model.addAttribute("courseId", courseId);
            model.addAttribute("courseOutline", outlineContent);
            
            // 返回JSP视图名称
            return "course/courseOutline";
            
        } catch (RuntimeException e) {
            // 处理权限验证失败或其他错误
            model.addAttribute("error", e.getMessage());
            return "error/403"; // 返回权限错误页面
        }
    }
}
