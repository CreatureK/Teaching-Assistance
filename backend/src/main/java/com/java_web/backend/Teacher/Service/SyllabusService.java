package com.java_web.backend.Teacher.Service;

import com.java_web.backend.Common.DTO.SyllabusRequestDTO;
import com.java_web.backend.Common.Entity.Course;
import com.java_web.backend.Common.Entity.CourseObjective;
import com.java_web.backend.Common.Entity.Syllabus;
import com.java_web.backend.Common.Mapper.CourseMapper;
import com.java_web.backend.Common.Mapper.CourseObjectMapper;
import com.java_web.backend.Common.Mapper.SyllabusMapper;
import com.java_web.backend.Common.Service.LLMSyllabusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SyllabusService {
    @Autowired
    private SyllabusMapper syllabusMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private CourseObjectMapper courseObjectMapper;
    
    @Autowired
    private LLMSyllabusService llmService;
    
    /**
     * 获取课程教学大纲
     */
    public Syllabus getCourseSyllabus(Integer courseId, Integer teacherId) {
        // 权限验证
        verifyTeacherCourseAccess(courseId, teacherId);

        // 获取数据
        return syllabusMapper.selectById(courseId);
    }

    /**
     * 生成课程教学大纲内容
     */
    public String generateSyllabusContent(Integer courseId, String prompt, Integer teacherId) {
        // 权限验证
        Course course = verifyTeacherCourseAccess(courseId, teacherId);
        
        // 检查是否已完成课程目标阶段
        CourseObjective objective = courseObjectMapper.selectById(courseId);
        if (objective == null || objective.getTeachingTarget() == null ||
        objective.getCourseContent() != null) {
            throw new RuntimeException("请先完成课程介绍和教学目标的生成");
        }
        
        // 创建请求对象
        SyllabusRequestDTO request = new SyllabusRequestDTO();
        request.setCourseTitle(course.getName());
        request.setRequest(prompt);
        
        // 调用LLM服务生成大纲
        return llmService.generateInitialSyllabus(request);
    }
    
    /**
     * 保存教学大纲
     */
    public void saveSyllabus(Syllabus syllabus, Integer teacherId) {
        // 1. 权限验证
        Course course = courseMapper.selectById(syllabus.getCourseId());
        if (course == null || course.getIsDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }

        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权操作此课程");
        }

        // 2. 检查是新增还是更新
        Syllabus existingSyllabus = syllabusMapper.selectById(syllabus.getCourseId());
        if (existingSyllabus == null) {
            // 新增
            syllabus.setCreatedAt(new Date());
            syllabusMapper.insert(syllabus);
        } else {
            // 更新
            existingSyllabus.setContent(syllabus.getContent());
            existingSyllabus.setUpdatedAt(new Date());
            syllabusMapper.updateById(existingSyllabus);
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
    
    /**
     * 获取课程大纲的Markdown内容
     * @param courseId 课程ID
     * @return 大纲的Markdown内容，如果不存在则返回null
     */
    public String getCourseOutlineContent(Integer courseId) {
        Syllabus syllabus = syllabusMapper.selectById(courseId);
        return syllabus != null ? syllabus.getContent() : null;
    }
}
