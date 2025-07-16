package com.java_web.backend.Common.Entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class Restriction {
    @TableId(type=IdType.AUTO)
    private Integer id;                // 限制ID
    private Integer userId;            // 用户ID
    private String functionName;       // 功能名称
    private Date createdAt;            // 创建时间

    public Restriction() {}

    public Restriction(Integer id, Integer userId, String functionName, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.functionName = functionName;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Restriction{" +
                "id=" + id +
                ", userId=" + userId +
                ", functionName='" + functionName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
