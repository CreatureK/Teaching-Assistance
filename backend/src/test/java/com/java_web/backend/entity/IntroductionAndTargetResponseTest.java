package com.java_web.backend.entity;

import com.java_web.backend.Common.DTO.IntroductionAndTargetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntroductionAndTargetResponseTest {

    private IntroductionAndTargetResponse response;

    @BeforeEach
    void setUp() {
        response = new IntroductionAndTargetResponse();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(response);
        assertNull(response.getCourseId());
        assertNull(response.getCourseIntroduction());
        assertNull(response.getTeachingTarget());
    }

    @Test
    void testParameterizedConstructor() {
        String courseId = "1";
        String courseIntroduction = "本课程是面向工程类专业本科生的高等数学课程";
        String teachingTarget = "通过本课程的学习，学生应掌握高等数学的基本理论";

        IntroductionAndTargetResponse response = new IntroductionAndTargetResponse(courseId, courseIntroduction, teachingTarget);

        assertEquals(courseId, response.getCourseId());
        assertEquals(courseIntroduction, response.getCourseIntroduction());
        assertEquals(teachingTarget, response.getTeachingTarget());
    }

    @Test
    void testSetAndGetCourseId() {
        String courseId = "1";
        response.setCourseId(courseId);
        assertEquals(courseId, response.getCourseId());
    }

    @Test
    void testSetAndGetCourseIntroduction() {
        String courseIntroduction = "本课程是面向工程类专业本科生的高等数学课程，内容涵盖函数、极限与连续、导数与微分、积分学、多元函数微积分、常微分方程、级数等基础理论知识。";
        response.setCourseIntroduction(courseIntroduction);
        assertEquals(courseIntroduction, response.getCourseIntroduction());
    }

    @Test
    void testSetAndGetTeachingTarget() {
        String teachingTarget = "通过本课程的学习，学生应掌握高等数学的基本理论和运算方法，具备运用数学工具分析和解决工程实际问题的能力；能够理解并运用数学模型描述和求解现实问题；提升逻辑思维能力和抽象思维能力，为后续专业课程的学习及工程实践奠定坚实的数学基础。";
        response.setTeachingTarget(teachingTarget);
        assertEquals(teachingTarget, response.getTeachingTarget());
    }

    @Test
    void testSetAndGetAllFields() {
        String courseId = "1";
        String courseIntroduction = "本课程是面向工程类专业本科生的高等数学课程";
        String teachingTarget = "通过本课程的学习，学生应掌握高等数学的基本理论";

        response.setCourseId(courseId);
        response.setCourseIntroduction(courseIntroduction);
        response.setTeachingTarget(teachingTarget);

        assertEquals(courseId, response.getCourseId());
        assertEquals(courseIntroduction, response.getCourseIntroduction());
        assertEquals(teachingTarget, response.getTeachingTarget());
    }

    @Test
    void testSetNullValues() {
        response.setCourseId(null);
        response.setCourseIntroduction(null);
        response.setTeachingTarget(null);

        assertNull(response.getCourseId());
        assertNull(response.getCourseIntroduction());
        assertNull(response.getTeachingTarget());
    }

    @Test
    void testSetEmptyStrings() {
        response.setCourseId("");
        response.setCourseIntroduction("");
        response.setTeachingTarget("");

        assertEquals("", response.getCourseId());
        assertEquals("", response.getCourseIntroduction());
        assertEquals("", response.getTeachingTarget());
    }

    @Test
    void testSetSpecialCharacters() {
        String courseId = "C++_101";
        String courseIntroduction = "本课程是C++程序设计课程，包含面向对象编程、STL库使用等核心内容。";
        String teachingTarget = "通过本课程学习，学生应掌握C++编程技能，能够独立开发应用程序！";

        response.setCourseId(courseId);
        response.setCourseIntroduction(courseIntroduction);
        response.setTeachingTarget(teachingTarget);

        assertEquals(courseId, response.getCourseId());
        assertEquals(courseIntroduction, response.getCourseIntroduction());
        assertEquals(teachingTarget, response.getTeachingTarget());
    }

    @Test
    void testSetLongValues() {
        String longCourseId = "12345678901234567890";
        String longCourseIntroduction = "这是一个非常长的课程介绍，用来测试边界情况。".repeat(10);
        String longTeachingTarget = "这是一个非常长的教学目标，用来测试边界情况。".repeat(10);

        response.setCourseId(longCourseId);
        response.setCourseIntroduction(longCourseIntroduction);
        response.setTeachingTarget(longTeachingTarget);

        assertEquals(longCourseId, response.getCourseId());
        assertEquals(longCourseIntroduction, response.getCourseIntroduction());
        assertEquals(longTeachingTarget, response.getTeachingTarget());
    }

    @Test
    void testMultipleSetOperations() {
        // 第一次设置
        response.setCourseId("1");
        response.setCourseIntroduction("第一次课程介绍");
        response.setTeachingTarget("第一次教学目标");

        assertEquals("1", response.getCourseId());
        assertEquals("第一次课程介绍", response.getCourseIntroduction());
        assertEquals("第一次教学目标", response.getTeachingTarget());

        // 第二次设置
        response.setCourseId("2");
        response.setCourseIntroduction("第二次课程介绍");
        response.setTeachingTarget("第二次教学目标");

        assertEquals("2", response.getCourseId());
        assertEquals("第二次课程介绍", response.getCourseIntroduction());
        assertEquals("第二次教学目标", response.getTeachingTarget());
    }

    @Test
    void testCreateResponseWithErrorMessages() {
        String courseId = "1";
        String errorMessage = "解析失败: JSON格式错误";

        IntroductionAndTargetResponse errorResponse = new IntroductionAndTargetResponse(courseId, errorMessage, errorMessage);

        assertEquals(courseId, errorResponse.getCourseId());
        assertEquals(errorMessage, errorResponse.getCourseIntroduction());
        assertEquals(errorMessage, errorResponse.getTeachingTarget());
    }

    @Test
    void testCreateResponseWithFailureMessages() {
        String courseId = "1";
        String failureMessage = "生成失败";

        IntroductionAndTargetResponse failureResponse = new IntroductionAndTargetResponse(courseId, failureMessage, failureMessage);

        assertEquals(courseId, failureResponse.getCourseId());
        assertEquals(failureMessage, failureResponse.getCourseIntroduction());
        assertEquals(failureMessage, failureResponse.getTeachingTarget());
    }
} 