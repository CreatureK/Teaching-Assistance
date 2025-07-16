package com.java_web.backend.Teacher.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.java_web.backend.Common.Entity.Course;
import com.java_web.backend.Teacher.Service.CourseService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/teacher/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    // 获取教师的所有课程
    @GetMapping("/courses")
    public ResponseEntity<?> getCoursesByTeacher(HttpServletRequest request) {
        Integer teacherId = (Integer) request.getAttribute("userId");
        List<Course> courses = courseService.getTeacherCourses(teacherId);
        return ResponseEntity.ok(courses);
    }

    // 获取课程详情（包括教学目标、大纲和讲义）
    @GetMapping("/course/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable int id, HttpServletRequest request) {
        Integer teacherId = (Integer) request.getAttribute("userId");
        try {
            Map<String, Object> courseDetail = courseService.getCourseDetail(id, teacherId);
            return ResponseEntity.ok(courseDetail);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 创建新课程
     */
    @PostMapping("/create")
    public ResponseEntity<?> createCourse(@RequestBody Map<String, String> payload, 
                                        @RequestHeader("userId") Integer teacherId) {
        try {
            String courseName = payload.get("courseName");
            Course course = courseService.createCourse(courseName, teacherId);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 删除课程（逻辑删除）
    @DeleteMapping("/course/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable int id, HttpServletRequest request) {
        Integer teacherId = (Integer) request.getAttribute("userId");
        try {
            courseService.deleteCourse(id, teacherId);
            return ResponseEntity.ok("课程已删除");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 