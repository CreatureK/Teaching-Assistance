package com.java_web.backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 测试结果输出工具类
 * 用于将大模型返回的内容保存到test_result_txt目录
 */
@Component
public class TestResultWriter {
    
    private static final String TEST_RESULT_DIR = "test_result_txt";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * 确保测试结果目录存在
     */
    private void ensureDirectoryExists() {
        try {
            Path dirPath = Paths.get(TEST_RESULT_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            System.err.println("创建测试结果目录失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成带时间戳的文件名
     */
    private String generateFileName(String testName, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_%s.%s", testName, timestamp, extension);
    }
    
    /**
     * 保存文本内容到文件
     */
    public void saveTextResult(String testName, String content) {
        ensureDirectoryExists();
        String fileName = generateFileName(testName, "txt");
        String filePath = TEST_RESULT_DIR + File.separator + fileName;
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("测试名称: " + testName + "\n");
            writer.write("生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("=".repeat(50) + "\n");
            writer.write(content);
            System.out.println("测试结果已保存到: " + filePath);
        } catch (IOException e) {
            System.err.println("保存测试结果失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存JSON内容到文件
     */
    public void saveJsonResult(String testName, Object content) {
        ensureDirectoryExists();
        String fileName = generateFileName(testName, "json");
        String filePath = TEST_RESULT_DIR + File.separator + fileName;
        
        try {
            String jsonContent = objectMapper.writeValueAsString(content);
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("// 测试名称: " + testName + "\n");
                writer.write("// 生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
                writer.write(jsonContent);
            }
            System.out.println("测试结果已保存到: " + filePath);
        } catch (IOException e) {
            System.err.println("保存JSON测试结果失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存Markdown内容到文件
     */
    public void saveMarkdownResult(String testName, String content) {
        ensureDirectoryExists();
        String fileName = generateFileName(testName, "md");
        String filePath = TEST_RESULT_DIR + File.separator + fileName;
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("# " + testName + "\n\n");
            writer.write("**生成时间:** " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
            writer.write("---\n\n");
            writer.write(content);
            System.out.println("测试结果已保存到: " + filePath);
        } catch (IOException e) {
            System.err.println("保存Markdown测试结果失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存原始响应内容到文件
     */
    public void saveRawResponse(String testName, String response) {
        ensureDirectoryExists();
        String fileName = generateFileName(testName + "_raw", "txt");
        String filePath = TEST_RESULT_DIR + File.separator + fileName;
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("测试名称: " + testName + "\n");
            writer.write("生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("原始响应内容:\n");
            writer.write("=".repeat(50) + "\n");
            writer.write(response);
            System.out.println("原始响应已保存到: " + filePath);
        } catch (IOException e) {
            System.err.println("保存原始响应失败: " + e.getMessage());
        }
    }
} 