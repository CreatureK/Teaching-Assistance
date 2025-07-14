package com.java_web.backend.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.java_web.backend.Entity.CourseObjective;

@RestController
public class CourseObjectiveController {
    @GetMapping("/courseObjective/{courseId}")
    public String getByCourseId(@PathVariable int courseId){
        System.out.println(courseId);
        return "根据课程ID获取教学目标";
    }

    @PostMapping("/courseObjective")
    public String save(@RequestBody CourseObjective obj){
        return "添加教学目标";
    }

    @PutMapping("/courseObjective")
    public String update(@RequestBody CourseObjective obj){
        return "更新教学目标";
    }

    @DeleteMapping("/courseObjective/{courseId}")
    public String deleteByCourseId(@PathVariable int courseId){
        System.out.println(courseId);
        return "根据课程ID删除教学目标";
    }
} 