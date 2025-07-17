package com.java_web.backend.common.dto;

import java.util.Objects;

public class JsonToMarkdownResponse {
    private String markdownContent;

    public JsonToMarkdownResponse() {}

    public JsonToMarkdownResponse(String markdownContent) {
        this.markdownContent = markdownContent;
    }

    public String getMarkdownContent() { return markdownContent; }
    public void setMarkdownContent(String markdownContent) { this.markdownContent = markdownContent; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonToMarkdownResponse that = (JsonToMarkdownResponse) o;
        return Objects.equals(markdownContent, that.markdownContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(markdownContent);
    }

    @Override
    public String toString() {
        return "JsonToMarkdownResponse{" +
                "markdownContent='" + markdownContent + '\'' +
                '}';
    }
} 