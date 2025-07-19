package com.java_web.backend.Teacher.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Common.DTO.LectureRequestDTO;
import com.java_web.backend.Common.Entity.Course;
import com.java_web.backend.Common.Entity.Material;
import com.java_web.backend.Common.Entity.Syllabus;
import com.java_web.backend.Common.Mapper.CourseMapper;
import com.java_web.backend.Common.Mapper.MaterialMapper;
import com.java_web.backend.Common.Mapper.SyllabusMapper;
import com.java_web.backend.Common.Service.LLMLectureService;
import com.java_web.backend.Common.Entity.InitialSyllabusRequest;
import com.java_web.backend.Common.Service.LLMInitialSyllabusService;

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
    
    @Autowired
    private LLMInitialSyllabusService llmInitialSyllabusService;
    
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
    public String generateMaterialContent(InitialSyllabusRequest req, Integer teacherId) {
        // 权限验证
        Course course = verifyTeacherCourseAccess(Integer.valueOf(req.getCourseId()), teacherId);

        // 1. 生成大纲
        Map<String, Object> syllabusMap = llmInitialSyllabusService.generateInitialSyllabus(
            req.getCourseId(), req.getCourseCode(), req.getCourseTitle(), req.getTeachingLanguage(),
            req.getResponsibleCollege(), req.getCourseCategory(), req.getPrinciple(), req.getVerifier(),
            req.getCredit(), req.getCourseHour(), req.getCourseIntroduction(), req.getTeachingTarget(),
            req.getEvaluationMode(), req.getWhetherTechnicalCourse(), req.getAssessmentType(),
            req.getGradeRecording(), req.getRequest()
        );

        // 2. 转为JsonNode并取data节点
        ObjectMapper mapper = new ObjectMapper();
        JsonNode syllabusJson = mapper.valueToTree(syllabusMap);
        // 直接传 syllabusJson
        return llmService.generateLecture(syllabusJson);
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
