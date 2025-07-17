package com.java_web.backend.Common.DTO;

/**
 * JSON转Markdown响应实体类
 */
public class JsonToMarkdownResponse {
    private String markdownContent;
    private String status;
    private String error;

    public JsonToMarkdownResponse() {
    }

    public JsonToMarkdownResponse(String markdownContent, String status) {
        this.markdownContent = markdownContent;
        this.status = status;
    }

    public JsonToMarkdownResponse(String status, String error, boolean isError) {
        this.status = status;
        this.error = error;
    }

    public String getMarkdownContent() {
        return markdownContent;
    }

    public void setMarkdownContent(String markdownContent) {
        this.markdownContent = markdownContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
} 