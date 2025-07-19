package com.java_web.backend.Teacher.Controller;

import java.util.HashMap;
import java.util.Map;

import com.java_web.backend.Common.Entity.InitialSyllabusRequest;
import com.java_web.backend.Teacher.Service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.java_web.backend.Common.Entity.Material;


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
    public ResponseEntity<String> generateMaterial(@RequestBody InitialSyllabusRequest req,
                                                   @RequestParam Integer teacherId) {
        String result = materialService.generateMaterialContent(req, teacherId);
        return ResponseEntity.ok(result);
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