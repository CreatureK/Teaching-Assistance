package com.java_web.backend.Teacher.Service;

import com.java_web.backend.Common.DTO.IntroductionAndTargetRequest;
import com.java_web.backend.Common.Entity.Course;
import com.java_web.backend.Common.Entity.CourseObjective;
import com.java_web.backend.Common.Mapper.CourseMapper;
import com.java_web.backend.Common.Mapper.CourseObjectMapper;
import com.java_web.backend.Common.Service.LLMIntroductionAndTargetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class CourseObjectiveService {
    @Autowired
    private CourseObjectMapper courseObjectMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private LLMIntroductionAndTargetService llmService;
    
    /**
     * 获取课程教学目标
     */
    public CourseObjective getCourseObjective(Integer courseId, Integer teacherId) {
        // 权限验证
        verifyTeacherCourseAccess(courseId, teacherId);

        // 获取数据
        return courseObjectMapper.selectById(courseId);
    }
    
    /**
     * 生成课程教学目标内容
     */
    public String generateObjectiveContent(Integer courseId, String prompt, Integer teacherId) throws IOException {
        // 权限验证
        Course course = verifyTeacherCourseAccess(courseId, teacherId);

        // 创建请求对象
        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseTitle(course.getName());
        request.setRequest(prompt);

        // 调用LLM服务生成内容
        return llmService.generateIntroductionAndTarget(request);
    }
    
    /**
     * 保存课程目标
     */
    public void saveObjective(CourseObjective objective, Integer teacherId) {
        // 1. 权限验证
        Course course = courseMapper.selectById(objective.getCourseId());
        if (course == null || course.getIsDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }

        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权操作此课程");
        }

        // 2. 检查是新增还是更新
        CourseObjective existingObj = courseObjectMapper.selectById(objective.getCourseId());
        if (existingObj == null) {
            // 新增
            objective.setCreatedAt(new Date());
            courseObjectMapper.insert(objective);
        } else {
            // 更新
            existingObj.setContent(objective.getContent());
            existingObj.setUpdatedAt(new Date());
            courseObjectMapper.updateById(existingObj);
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
