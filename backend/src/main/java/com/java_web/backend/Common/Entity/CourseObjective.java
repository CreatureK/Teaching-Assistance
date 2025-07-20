package com.java_web.backend.Common.Entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

public class CourseObjective {
    @TableId
    private Integer courseId;      // 课程ID
    private String courseContent;  // 课程内容
    private String teachingTarget; // 教学目标
    private Date createdAt;        // 创建时间
    private Date updatedAt;        // 更新时间

    public CourseObjective() {}

    public CourseObjective(Integer courseId, String courseContent, String teachingTarget, Date createdAt, Date updatedAt) {
        this.courseId = courseId;
        this.courseContent = courseContent;
        this.teachingTarget = teachingTarget;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseContent() {
        return courseContent;
    }

    public void setCourseContent(String courseContent) {
        this.courseContent = courseContent;
    }

    public String getTeachingTarget() {
        return teachingTarget;
    }

    public void setTeachingTarget(String teachingTarget) {
        this.teachingTarget = teachingTarget;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "CourseObjective{" +
                "courseId=" + courseId +
                ", courseContent='" + courseContent + '\'' +
                ", teachingTarget='" + teachingTarget + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
