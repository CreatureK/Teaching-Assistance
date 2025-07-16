package com.java_web.backend.Common.Entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

public class Syllabus {
    @TableId
    private Integer courseId;      // 课程ID
    private String content;        // 教学大纲内容
    private Date createdAt;        // 创建时间
    private Date updatedAt;        // 更新时间

    public Syllabus() {}

    public Syllabus(Integer courseId, String content, Date createdAt, Date updatedAt) {
        this.courseId = courseId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        return "Syllabus{" +
                "courseId=" + courseId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
