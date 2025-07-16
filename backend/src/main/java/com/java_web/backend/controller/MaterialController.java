package com.java_web.backend.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.java_web.backend.Entity.Material;

@RestController
public class MaterialController {
    @GetMapping("/material/{courseId}")
    public String getByCourseId(@PathVariable int courseId){
        System.out.println(courseId);
        return "根据课程ID获取讲义";
    }

    @PostMapping("/material")
    public String save(@RequestBody Material material){
        return "添加讲义";
    }

    @PutMapping("/material")
    public String update(@RequestBody Material material){
        return "更新讲义";
    }

    @DeleteMapping("/material/{courseId}")
    public String deleteByCourseId(@PathVariable int courseId){
        System.out.println(courseId);
        return "根据课程ID删除讲义";
    }
} 