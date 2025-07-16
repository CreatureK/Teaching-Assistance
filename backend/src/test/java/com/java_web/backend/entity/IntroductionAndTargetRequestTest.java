package com.java_web.backend.entity;

import com.java_web.backend.Entity.IntroductionAndTargetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntroductionAndTargetRequestTest {

    private IntroductionAndTargetRequest request;

    @BeforeEach
    void setUp() {
        request = new IntroductionAndTargetRequest();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(request);
        assertNull(request.getCourseId());
        assertNull(request.getCourseTitle());
        assertNull(request.getRequest());
    }

    @Test
    void testSetAndGetCourseId() {
        String courseId = "1";
        request.setCourseId(courseId);
        assertEquals(courseId, request.getCourseId());
    }

    @Test
    void testSetAndGetCourseTitle() {
        String courseTitle = "高等数学";
        request.setCourseTitle(courseTitle);
        assertEquals(courseTitle, request.getCourseTitle());
    }

    @Test
    void testSetAndGetRequest() {
        String requestText = "请结合工程实际，突出应用能力培养";
        request.setRequest(requestText);
        assertEquals(requestText, request.getRequest());
    }

    @Test
    void testSetAndGetAllFields() {
        String courseId = "1";
        String courseTitle = "高等数学";
        String requestText = "请结合工程实际，突出应用能力培养";

        request.setCourseId(courseId);
        request.setCourseTitle(courseTitle);
        request.setRequest(requestText);

        assertEquals(courseId, request.getCourseId());
        assertEquals(courseTitle, request.getCourseTitle());
        assertEquals(requestText, request.getRequest());
    }

    @Test
    void testSetNullValues() {
        request.setCourseId(null);
        request.setCourseTitle(null);
        request.setRequest(null);

        assertNull(request.getCourseId());
        assertNull(request.getCourseTitle());
        assertNull(request.getRequest());
    }

    @Test
    void testSetEmptyStrings() {
        request.setCourseId("");
        request.setCourseTitle("");
        request.setRequest("");

        assertEquals("", request.getCourseId());
        assertEquals("", request.getCourseTitle());
        assertEquals("", request.getRequest());
    }

    @Test
    void testSetSpecialCharacters() {
        String courseId = "C++_101";
        String courseTitle = "C++程序设计 & Java开发";
        String requestText = "请结合工程实际，包含C++、Java、Python等编程语言，注重理论与实践结合！";

        request.setCourseId(courseId);
        request.setCourseTitle(courseTitle);
        request.setRequest(requestText);

        assertEquals(courseId, request.getCourseId());
        assertEquals(courseTitle, request.getCourseTitle());
        assertEquals(requestText, request.getRequest());
    }

    @Test
    void testSetLongValues() {
        String longCourseId = "12345678901234567890";
        String longCourseTitle = "这是一个非常长的课程标题，用来测试边界情况";
        String longRequest = "这是一个非常长的请求内容，用来测试边界情况。".repeat(10);

        request.setCourseId(longCourseId);
        request.setCourseTitle(longCourseTitle);
        request.setRequest(longRequest);

        assertEquals(longCourseId, request.getCourseId());
        assertEquals(longCourseTitle, request.getCourseTitle());
        assertEquals(longRequest, request.getRequest());
    }

    @Test
    void testMultipleSetOperations() {
        // 第一次设置
        request.setCourseId("1");
        request.setCourseTitle("高等数学");
        request.setRequest("第一次请求");

        assertEquals("1", request.getCourseId());
        assertEquals("高等数学", request.getCourseTitle());
        assertEquals("第一次请求", request.getRequest());

        // 第二次设置
        request.setCourseId("2");
        request.setCourseTitle("线性代数");
        request.setRequest("第二次请求");

        assertEquals("2", request.getCourseId());
        assertEquals("线性代数", request.getCourseTitle());
        assertEquals("第二次请求", request.getRequest());
    }
} 