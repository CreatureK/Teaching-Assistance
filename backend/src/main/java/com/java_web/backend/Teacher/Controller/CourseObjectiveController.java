package com.java_web.backend.Teacher.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.java_web.backend.Common.Entity.CourseObjective;
import com.java_web.backend.Teacher.Service.CourseObjectiveService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/teacher/objective")
public class CourseObjectiveController {
    @Autowired
    private CourseObjectiveService objectiveService;

    // 获取课程教学目标
    @GetMapping("/course/{courseId}/objective")
    public ResponseEntity<?> getObjective(@PathVariable int courseId, HttpServletRequest request) {
        Integer teacherId = (Integer) request.getAttribute("userId");
        try {
            CourseObjective objective = objectiveService.getCourseObjective(courseId, teacherId);
            return ResponseEntity.ok(objective);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * AI生成课程介绍和教学目标
     */
    @PostMapping("/{courseId}/generate")
    public ResponseEntity<?> generateObjective(@PathVariable Integer courseId,
                                             @RequestBody Map<String, String> prompt,
                                             @RequestHeader("userId") Integer teacherId) {
        try {
            String userPrompt = prompt.get("prompt");
            String objective = objectiveService.generateObjectiveContent(courseId, userPrompt, teacherId);
            return ResponseEntity.ok(objective);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 保存课程目标
     */
    @PostMapping("/{courseId}/save")
    public ResponseEntity<?> saveObjective(@PathVariable Integer courseId,
                                         @RequestBody CourseObjective objective,
                                         @RequestHeader("userId") Integer teacherId) {
        try {
            // 确保courseId一致
            objective.setCourseId(courseId);
            objectiveService.saveObjective(objective, teacherId);
            return ResponseEntity.ok("保存成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 