package com.java_web.backend.Common.DTO;

import java.util.Objects;

public class JsonToMarkdownRequestDTO {
    private String jsonContent;

    public JsonToMarkdownRequestDTO() {}

    public JsonToMarkdownRequestDTO(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public String getJsonContent() { return jsonContent; }
    public void setJsonContent(String jsonContent) { this.jsonContent = jsonContent; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonToMarkdownRequestDTO that = (JsonToMarkdownRequestDTO) o;
        return Objects.equals(jsonContent, that.jsonContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonContent);
    }

    @Override
    public String toString() {
        return "JsonToMarkdownRequest{" +
                "jsonContent='" + jsonContent + '\'' +
                '}';
    }
} 