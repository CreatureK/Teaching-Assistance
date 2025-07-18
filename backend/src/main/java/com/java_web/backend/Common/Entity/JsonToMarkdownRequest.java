package com.java_web.backend.Common.Entity;

/**
 * JSON转Markdown请求实体类
 */
public class JsonToMarkdownRequest {
    private String jsonContent;
    private String outputFormat;
    private String customStyle;

    public JsonToMarkdownRequest() {
    }

    public JsonToMarkdownRequest(String jsonContent, String outputFormat, String customStyle) {
        this.jsonContent = jsonContent;
        this.outputFormat = outputFormat;
        this.customStyle = customStyle;
    }

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getCustomStyle() {
        return customStyle;
    }

    public void setCustomStyle(String customStyle) {
        this.customStyle = customStyle;
    }
} 