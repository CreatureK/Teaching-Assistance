package com.java_web.backend.Common.Entity;

public class IntroductionAndTargetResponse {
    private String courseId;
    private String courseIntroduction;
    private String teachingTarget;

    public IntroductionAndTargetResponse() {
    }

    public IntroductionAndTargetResponse(String courseId, String courseIntroduction, String teachingTarget) {
        this.courseId = courseId;
        this.courseIntroduction = courseIntroduction;
        this.teachingTarget = teachingTarget;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseIntroduction() {
        return courseIntroduction;
    }

    public void setCourseIntroduction(String courseIntroduction) {
        this.courseIntroduction = courseIntroduction;
    }

    public String getTeachingTarget() {
        return teachingTarget;
    }

    public void setTeachingTarget(String teachingTarget) {
        this.teachingTarget = teachingTarget;
    }
} 