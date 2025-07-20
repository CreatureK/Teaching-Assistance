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

import com.java_web.backend.Common.Entity.InitialSyllabusRequest;
import com.java_web.backend.Common.Entity.Material;
import com.java_web.backend.Teacher.Service.MaterialService;


@RestController
@RequestMapping("/teacher/material")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    /**
     * 获取课程讲义
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getMaterial(@PathVariable Integer courseId,
                                       @RequestHeader("userId") Integer teacherId) {
        try {
            Material material = materialService.getCourseMaterial(courseId, teacherId);
            return ResponseEntity.ok(material);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 生成完整课程讲义
     */
    @PostMapping("/{courseId}/generate")
    public ResponseEntity<String> generateMaterial(@PathVariable Integer courseId,
                                                   @RequestBody Map<String, String> request,
                                                   @RequestHeader("userId") Integer teacherId) {
        try {
            // 创建InitialSyllabusRequest对象，只设置必要参数
            InitialSyllabusRequest req = new InitialSyllabusRequest();
            req.setCourseId(courseId.toString());
            req.setCourseTitle(request.get("courseTitle"));
            req.setRequest(request.get("request"));
            
            // 其他字段使用默认值，courseIntroduction和teachingTarget将从数据库获取
            String result = materialService.generateMaterialContent(req, teacherId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 保存教学讲义
     */
    @PostMapping("/{courseId}/save")
    public ResponseEntity<?> saveMaterial(@PathVariable Integer courseId,
                                        @RequestBody Material material,
                                        @RequestHeader("userId") Integer teacherId) {
        try {
            // 确保courseId一致
            material.setCourseId(courseId);
            materialService.saveMaterial(material, teacherId);
            return ResponseEntity.ok("保存成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 