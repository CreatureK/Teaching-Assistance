package com.java_web.backend.Admin.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java_web.backend.Common.Entity.Course;
import com.java_web.backend.Common.Mapper.CourseMapper;

@Service
public class AdminCourseService {
    @Autowired
    private CourseMapper courseMapper;
    
    public List<Course> getPendingCourses() {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "pending")
                   .eq("is_deleted", 0);
        return courseMapper.selectList(queryWrapper);
    }
    
    public List<Course> getAllCourses() {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        return courseMapper.selectList(queryWrapper);
    }
    
    public ResponseEntity<?> updateCourseStatus(Integer id, String status) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return ResponseEntity.badRequest().body("课程不存在");
        }
        
        course.setStatus(status);
        courseMapper.updateById(course);
        return ResponseEntity.ok("更新课程状态成功");
    }
}
