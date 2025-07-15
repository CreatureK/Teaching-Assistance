package com.java_web.backend.entity;

public class SyllabusRequest {
    private String courseId;
    private String courseTitle;
    private String creditHours;
    private String keyTeachingContent;
    private String request;
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public String getCreditHours() { return creditHours; }
    public void setCreditHours(String creditHours) { this.creditHours = creditHours; }
    public String getKeyTeachingContent() { return keyTeachingContent; }
    public void setKeyTeachingContent(String keyTeachingContent) { this.keyTeachingContent = keyTeachingContent; }
    public String getRequest() { return request; }
    public void setRequest(String request) { this.request = request; }
} 