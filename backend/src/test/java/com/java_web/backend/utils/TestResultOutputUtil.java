package com.java_web.backend.utils;

import org.springframework.stereotype.Component;

/**
 * 测试结果输出工具类 (TestResultWriter的别名)
 * 用于向后兼容旧的测试代码
 */
@Component
public class TestResultOutputUtil {
    
    private static final TestResultWriter writer = new TestResultWriter();
    
    /**
     * 写入测试结果到文件
     * @param testName 测试名称
     * @param content 内容
     * @param fileType 文件类型 (json, txt, md)
     */
    public static void writeTestResult(String testName, String content, String fileType) {
        switch (fileType.toLowerCase()) {
            case "json":
                writer.saveJsonResult(testName, content);
                break;
            case "md":
            case "markdown":
                writer.saveMarkdownResult(testName, content);
                break;
            default:
                writer.saveTextResult(testName, content);
                break;
        }
    }
    
    /**
     * 写入JSON格式的测试结果
     * @param testName 测试名称
     * @param jsonObject JSON对象
     */
    public static void writeJsonResult(String testName, Object jsonObject) {
        writer.saveJsonResult(testName, jsonObject);
    }
    
    /**
     * 写入文本格式的测试结果
     * @param testName 测试名称
     * @param content 文本内容
     */
    public static void writeTextResult(String testName, String content) {
        writer.saveTextResult(testName, content);
    }
    
    /**
     * 写入Markdown格式的测试结果
     * @param testName 测试名称
     * @param content Markdown内容
     */
    public static void writeMarkdownResult(String testName, String content) {
        writer.saveMarkdownResult(testName, content);
    }
    
    /**
     * 输出到文件（兼容旧代码）
     * @param testName 测试名称
     * @param content 内容
     */
    public static void outputToFile(String testName, String content) {
        writer.saveTextResult(testName, content);
    }
} 