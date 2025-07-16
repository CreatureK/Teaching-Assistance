package com.java_web.backend.Teacher.Service;

import com.java_web.backend.Common.DTO.LectureRequest;
import com.java_web.backend.Common.Entity.Course;
import com.java_web.backend.Common.Entity.Material;
import com.java_web.backend.Common.Entity.Syllabus;
import com.java_web.backend.Common.Mapper.CourseMapper;
import com.java_web.backend.Common.Mapper.MaterialMapper;
import com.java_web.backend.Common.Mapper.SyllabusMapper;
import com.java_web.backend.Common.Service.LLMLectureService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class MaterialService {
    @Autowired
    private MaterialMapper materialMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private SyllabusMapper syllabusMapper;
    
    @Autowired
    private LLMLectureService llmService;
    
    /**
     * 获取课程讲义
     */
    public Material getCourseMaterial(Integer courseId, Integer teacherId) {
        // 权限验证
        verifyTeacherCourseAccess(courseId, teacherId);
        
        // 获取数据
        return materialMapper.selectById(courseId);
    }
    
    /**
     * 生成课程讲义内容
     */
    public String generateMaterialContent(Integer courseId, String prompt, Integer teacherId) throws IOException {
        // 权限验证
        Course course = verifyTeacherCourseAccess(courseId, teacherId);
        
        // 检查是否已完成教学大纲阶段
        Syllabus syllabus = syllabusMapper.selectById(courseId);
        if (syllabus == null || syllabus.getContent() == null) {
            throw new RuntimeException("请先完成教学大纲的生成");
        }
        
        // 创建请求对象
        LectureRequest request = new LectureRequest();
        request.setCourseTitle(course.getName());
        request.setRequest(prompt);
        
        // 调用LLM服务生成讲义内容
        return llmService.generateLecture(request);
    }
    
    /**
     * 保存教学讲义
     */
    public void saveMaterial(Material material, Integer teacherId) {
        // 1. 权限验证
        Course course = courseMapper.selectById(material.getCourseId());
        if (course == null || course.getIsDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }

        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权操作此课程");
        }

        // 2. 检查是新增还是更新
        Material existingMaterial = materialMapper.selectById(material.getCourseId());
        if (existingMaterial == null) {
            // 新增
            material.setCreatedAt(new Date());
            materialMapper.insert(material);
        } else {
            // 更新
            existingMaterial.setContent(material.getContent());
            existingMaterial.setUpdatedAt(new Date());
            materialMapper.updateById(existingMaterial);
        }
    }
    
    /**
     * 验证教师对课程的访问权限
     */
    private Course verifyTeacherCourseAccess(Integer courseId, Integer teacherId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getIsDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }
        
        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权操作此课程");
        }
        
        return course;
    }
}
