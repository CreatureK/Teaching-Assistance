package com.java_web.backend.Common.DTO;

import java.util.Objects;

public class JsonToMarkdownResponseDTO {
    private String markdownContent;

    public JsonToMarkdownResponseDTO() {}

    public JsonToMarkdownResponseDTO(String markdownContent) {
        this.markdownContent = markdownContent;
    }

    public String getMarkdownContent() { return markdownContent; }
    public void setMarkdownContent(String markdownContent) { this.markdownContent = markdownContent; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonToMarkdownResponseDTO that = (JsonToMarkdownResponseDTO) o;
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