package com.java_web.backend.Teacher.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java_web.backend.Common.Entity.Syllabus;
import com.java_web.backend.Teacher.Service.SyllabusService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/teacher/syllabus")
public class SyllabusController {
    @Autowired
    private SyllabusService syllabusService;

    // 获取课程教学大纲
    @GetMapping("/course/{courseId}/syllabus")
    public ResponseEntity<?> getSyllabus(@PathVariable int courseId, HttpServletRequest request) {
        Integer teacherId = (Integer) request.getAttribute("userId");
        try {
            Syllabus syllabus = syllabusService.getCourseSyllabus(courseId, teacherId);
            return ResponseEntity.ok(syllabus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

     /**
     * AI生成教学大纲
     */
    @PostMapping("/{courseId}/generate")
    public ResponseEntity<?> generateSyllabus(@PathVariable Integer courseId,
                                            @RequestBody Map<String, String> prompt,
                                            @RequestHeader("userId") Integer teacherId) {
        try {
            String userPrompt = prompt.get("prompt");
            String syllabus = syllabusService.generateSyllabusContent(courseId, userPrompt, teacherId);
            return ResponseEntity.ok(syllabus);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 保存教学大纲
     */
    @PostMapping("/{courseId}/save")
    public ResponseEntity<?> saveSyllabus(@PathVariable Integer courseId,
                                        @RequestBody Syllabus syllabus,
                                        @RequestHeader("userId") Integer teacherId) {
        try {
            // 确保courseId一致
            syllabus.setCourseId(courseId);
            syllabusService.saveSyllabus(syllabus, teacherId);
            return ResponseEntity.ok("保存成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 