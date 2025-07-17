package com.java_web.backend.common.dto;

import java.util.Objects;

public class JsonToMarkdownRequest {
    private String jsonContent;

    public JsonToMarkdownRequest() {}

    public JsonToMarkdownRequest(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public String getJsonContent() { return jsonContent; }
    public void setJsonContent(String jsonContent) { this.jsonContent = jsonContent; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonToMarkdownRequest that = (JsonToMarkdownRequest) o;
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