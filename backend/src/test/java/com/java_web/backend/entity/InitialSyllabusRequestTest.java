package com.java_web.backend.entity;

import com.java_web.backend.Common.DTO.InitialSyllabusRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InitialSyllabusRequestTest {

    @Test
    void testDefaultConstructor() {
        InitialSyllabusRequest request = new InitialSyllabusRequest();
        
        assertNotNull(request);
        assertNull(request.getCourseId());
        assertNull(request.getCourseCode());
        assertNull(request.getCourseTitle());
        assertNull(request.getTeachingLanguage());
        assertNull(request.getResponsibleCollege());
        assertNull(request.getCourseCategory());
        assertNull(request.getPrinciple());
        assertNull(request.getVerifier());
        assertNull(request.getCredit());
        assertNull(request.getCourseHour());
        assertNull(request.getCourseIntroduction());
        assertNull(request.getTeachingTarget());
        assertNull(request.getEvaluationMode());
        assertNull(request.getWhetherTechnicalCourse());
        assertNull(request.getAssessmentType());
        assertNull(request.getGradeRecording());
        assertNull(request.getRequest());
    }

    @Test
    void testParameterizedConstructor() {
        InitialSyllabusRequest request = new InitialSyllabusRequest(
                "CS001", "CS101", "计算机科学导论", "中文", "计算机学院",
                "专业必修", "李老师", "王老师", "3", "48",
                "计算机科学基础课程", "掌握计算机科学基础知识", "考试",
                "否", "理论", "百分制", "希望课程内容生动有趣"
        );

        assertEquals("CS001", request.getCourseId());
        assertEquals("CS101", request.getCourseCode());
        assertEquals("计算机科学导论", request.getCourseTitle());
        assertEquals("中文", request.getTeachingLanguage());
        assertEquals("计算机学院", request.getResponsibleCollege());
        assertEquals("专业必修", request.getCourseCategory());
        assertEquals("李老师", request.getPrinciple());
        assertEquals("王老师", request.getVerifier());
        assertEquals("3", request.getCredit());
        assertEquals("48", request.getCourseHour());
        assertEquals("计算机科学基础课程", request.getCourseIntroduction());
        assertEquals("掌握计算机科学基础知识", request.getTeachingTarget());
        assertEquals("考试", request.getEvaluationMode());
        assertEquals("否", request.getWhetherTechnicalCourse());
        assertEquals("理论", request.getAssessmentType());
        assertEquals("百分制", request.getGradeRecording());
        assertEquals("希望课程内容生动有趣", request.getRequest());
    }

    @Test
    void testSettersAndGetters() {
        InitialSyllabusRequest request = new InitialSyllabusRequest();

        // 测试所有setter和getter
        request.setCourseId("CS001");
        assertEquals("CS001", request.getCourseId());

        request.setCourseCode("CS101");
        assertEquals("CS101", request.getCourseCode());

        request.setCourseTitle("计算机科学导论");
        assertEquals("计算机科学导论", request.getCourseTitle());

        request.setTeachingLanguage("中文");
        assertEquals("中文", request.getTeachingLanguage());

        request.setResponsibleCollege("计算机学院");
        assertEquals("计算机学院", request.getResponsibleCollege());

        request.setCourseCategory("专业必修");
        assertEquals("专业必修", request.getCourseCategory());

        request.setPrinciple("李老师");
        assertEquals("李老师", request.getPrinciple());

        request.setVerifier("王老师");
        assertEquals("王老师", request.getVerifier());

        request.setCredit("3");
        assertEquals("3", request.getCredit());

        request.setCourseHour("48");
        assertEquals("48", request.getCourseHour());

        request.setCourseIntroduction("计算机科学基础课程");
        assertEquals("计算机科学基础课程", request.getCourseIntroduction());

        request.setTeachingTarget("掌握计算机科学基础知识");
        assertEquals("掌握计算机科学基础知识", request.getTeachingTarget());

        request.setEvaluationMode("考试");
        assertEquals("考试", request.getEvaluationMode());

        request.setWhetherTechnicalCourse("否");
        assertEquals("否", request.getWhetherTechnicalCourse());

        request.setAssessmentType("理论");
        assertEquals("理论", request.getAssessmentType());

        request.setGradeRecording("百分制");
        assertEquals("百分制", request.getGradeRecording());

        request.setRequest("希望课程内容生动有趣");
        assertEquals("希望课程内容生动有趣", request.getRequest());
    }

    @Test
    void testToString() {
        InitialSyllabusRequest request = new InitialSyllabusRequest(
                "CS001", "CS101", "计算机科学导论", "中文", "计算机学院",
                "专业必修", "李老师", "王老师", "3", "48",
                "计算机科学基础课程", "掌握计算机科学基础知识", "考试",
                "否", "理论", "百分制", "希望课程内容生动有趣"
        );

        String toString = request.toString();
        
        assertTrue(toString.contains("CS001"));
        assertTrue(toString.contains("CS101"));
        assertTrue(toString.contains("计算机科学导论"));
        assertTrue(toString.contains("中文"));
        assertTrue(toString.contains("计算机学院"));
        assertTrue(toString.contains("专业必修"));
        assertTrue(toString.contains("李老师"));
        assertTrue(toString.contains("王老师"));
        assertTrue(toString.contains("3"));
        assertTrue(toString.contains("48"));
        assertTrue(toString.contains("计算机科学基础课程"));
        assertTrue(toString.contains("掌握计算机科学基础知识"));
        assertTrue(toString.contains("考试"));
        assertTrue(toString.contains("否"));
        assertTrue(toString.contains("理论"));
        assertTrue(toString.contains("百分制"));
        assertTrue(toString.contains("希望课程内容生动有趣"));
    }

    @Test
    void testEqualsAndHashCode() {
        InitialSyllabusRequest request1 = new InitialSyllabusRequest(
                "CS001", "CS101", "计算机科学导论", "中文", "计算机学院",
                "专业必修", "李老师", "王老师", "3", "48",
                "计算机科学基础课程", "掌握计算机科学基础知识", "考试",
                "否", "理论", "百分制", "希望课程内容生动有趣"
        );

        InitialSyllabusRequest request2 = new InitialSyllabusRequest(
                "CS001", "CS101", "计算机科学导论", "中文", "计算机学院",
                "专业必修", "李老师", "王老师", "3", "48",
                "计算机科学基础课程", "掌握计算机科学基础知识", "考试",
                "否", "理论", "百分制", "希望课程内容生动有趣"
        );

        InitialSyllabusRequest request3 = new InitialSyllabusRequest(
                "CS002", "CS102", "数据结构", "中文", "计算机学院",
                "专业必修", "张老师", "李老师", "4", "64",
                "数据结构基础课程", "掌握数据结构基础知识", "考试",
                "否", "理论", "百分制", "希望课程内容生动有趣"
        );

        // 测试equals
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertNotEquals(request1, null);
        assertEquals(request1, request1);

        // 测试hashCode
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }
} 