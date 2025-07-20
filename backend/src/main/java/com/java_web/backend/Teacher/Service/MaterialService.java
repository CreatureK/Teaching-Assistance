package com.java_web.backend.Teacher.Service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Common.Entity.Course;
import com.java_web.backend.Common.Entity.CourseObjective;
import com.java_web.backend.Common.Entity.InitialSyllabusRequest;
import com.java_web.backend.Common.Entity.Material;
import com.java_web.backend.Common.Mapper.CourseMapper;
import com.java_web.backend.Common.Mapper.CourseObjectMapper;
import com.java_web.backend.Common.Mapper.MaterialMapper;
import com.java_web.backend.Common.Mapper.SyllabusMapper;
import com.java_web.backend.Common.Service.LLMInitialSyllabusService;
import com.java_web.backend.Common.Service.LLMLectureService;

@Service
public class MaterialService {
    @Autowired
    private MaterialMapper materialMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private CourseObjectMapper courseObjectMapper;
    
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
    public String generateMaterialContent(InitialSyllabusRequest req, Integer teacherId) throws JsonProcessingException {
        // 权限验证
        Course course = verifyTeacherCourseAccess(Integer.valueOf(req.getCourseId()), teacherId);

        System.out.println("111");

        CourseObjective courseObjective = new CourseObjective();
        String courseIntroduction = courseObjective.getCourseContent();
        String teachingTarget = courseObjective.getTeachingTarget();

        // 1. 强化生成大纲，失败自动重试
        Map<String, Object> syllabusMap = null;
        int retryCount = 0;
        while (syllabusMap == null) {
            syllabusMap = llmInitialSyllabusService.generateInitialSyllabus(
                req.getCourseId(), req.getCourseCode(), req.getCourseTitle(), req.getTeachingLanguage(),
                req.getResponsibleCollege(), req.getCourseCategory(), req.getPrinciple(), req.getVerifier(),
                req.getCredit(), req.getCourseHour(), courseIntroduction, teachingTarget,
                req.getEvaluationMode(), req.getWhetherTechnicalCourse(), req.getAssessmentType(),
                req.getGradeRecording(), req.getRequest()
            );
        }

        System.out.println("222");

        // 2. 转为JsonNode并取data节点
        ObjectMapper mapper = new ObjectMapper();
//        JsonNode syllabusJson = mapper.valueToTree(syllabusMap);
        // 直接传 syllabusJson
        // 1. 转为 JSON 字符串
        String rawJson = mapper.writeValueAsString(syllabusMap);

        // 2. 替换掉 markdown 包裹内容（你也可以更精细地处理字段）
        String cleanedJson = rawJson.replaceAll("```json\\n?", "")
                .replaceAll("\\n?```", "");

        // 3. 再解析回 JsonNode
        JsonNode syllabusJson = mapper.readTree(cleanedJson);
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

        // 2. 处理内容中的转义字符（可选）
        String processedContent = material.getContent();
        if (processedContent != null) {
            // 将 \\n 转换为 \n，\\t 转换为 \t 等
            processedContent = processedContent.replace("\\\\n", "\n")
                                             .replace("\\\\t", "\t")
                                             .replace("\\\\r", "\r");
            material.setContent(processedContent);
        }

        // 3. 检查是新增还是更新
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
