package com.java_web.backend.Teacher.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java_web.backend.Common.Entity.Course;
import com.java_web.backend.Common.Entity.CourseObjective;
import com.java_web.backend.Common.Entity.Material;
import com.java_web.backend.Common.Entity.Syllabus;
import com.java_web.backend.Common.Mapper.CourseMapper;
import com.java_web.backend.Common.Mapper.CourseObjectMapper;
import com.java_web.backend.Common.Mapper.MaterialMapper;
import com.java_web.backend.Common.Mapper.SyllabusMapper;

@Service
public class CourseService {
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private CourseObjectMapper objectiveMapper;
    
    @Autowired
    private SyllabusMapper syllabusMapper;
    
    @Autowired
    private MaterialMapper materialMapper;
    
    /**
     * 获取教师创建的课程列表
     */
    public List<Course> getTeacherCourses(Integer teacherId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId)
                   .eq("is_deleted", 0);
        return courseMapper.selectList(queryWrapper);
    }
    
    /**
     * 获取课程详情（包括教学目标、大纲和讲义）
     */
    public Map<String, Object> getCourseDetail(Integer courseId, Integer teacherId) {
        // 查询课程基本信息
        Course course = courseMapper.getCourseWithTeacher(courseId);
        
        // 检查课程是否存在
        if (course == null || course.getIsDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }
        
        // 检查当前用户是否为该课程的创建者
        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("您没有权限查看此课程");
        }
        
        // 查询关联的教学目标、大纲和讲义
        CourseObjective objective = objectiveMapper.getObjectiveWithCourse(courseId);
        Syllabus syllabus = syllabusMapper.getSyllabusByCourseId(courseId);
        Material material = materialMapper.selectByCourseId(courseId);

        System.out.println("Objective: " + (objective != null));
        System.out.println("Syllabus: " + (syllabus != null));
        System.out.println("Material: " + (material != null));

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("course", course);
        result.put("objective", objective);
        result.put("syllabus", syllabus);
        result.put("material", material);
        
        return result;
    }
    
    /**
     * 创建新课程
     */
    public Course createCourse(String courseName, Integer teacherId) {
        // 验证课程名称
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new RuntimeException("课程名称不能为空");
        }
        
        // 创建课程对象
        Course course = new Course();
        course.setName(courseName);
        course.setTeacherId(teacherId);
        course.setStatus("draft"); // 设置为草稿状态
        course.setIsDeleted(0);
        course.setCreatedAt(new Date());
        
        // 保存课程
        courseMapper.insert(course);
        
        return course;
    }
    
    /**
     * 删除课程（逻辑删除）
     */
    public void deleteCourse(Integer courseId, Integer teacherId) {
        // 查询课程
        Course course = courseMapper.selectById(courseId);
        
        // 检查课程是否存在
        if (course == null || course.getIsDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }
        
        // 检查当前用户是否为该课程的创建者
        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("您没有权限删除此课程");
        }
        
        // 执行逻辑删除
        course.setIsDeleted(1);
        course.setUpdatedAt(new Date());
        courseMapper.updateById(course);
    }
    
    /**
     * 验证课程存在且属于指定教师
     */
    public Course validateCourseOwnership(Integer courseId, Integer teacherId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getIsDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }
        
        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("您没有权限操作此课程");
        }
        
        return course;
    }
}
