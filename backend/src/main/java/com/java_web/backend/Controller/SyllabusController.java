package com.java_web.backend.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.java_web.backend.Entity.Syllabus;

@RestController
public class SyllabusController {
    @GetMapping("/syllabus/{courseId}")
    public String getByCourseId(@PathVariable int courseId){
        System.out.println(courseId);
        return "根据课程ID获取教学大纲";
    }

    @PostMapping("/syllabus")
    public String save(@RequestBody Syllabus syllabus){
        return "添加教学大纲";
    }

    @PutMapping("/syllabus")
    public String update(@RequestBody Syllabus syllabus){
        return "更新教学大纲";
    }

    @DeleteMapping("/syllabus/{courseId}")
    public String deleteByCourseId(@PathVariable int courseId){
        System.out.println(courseId);
        return "根据课程ID删除教学大纲";
    }
} 