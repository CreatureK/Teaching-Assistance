package com.java_web.backend.Common.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.Utils.HttpUtil;

@Service
public class LLMLectureService {
    @Autowired
    private OpenAIConfig openAIConfig;

    private String commandInstructions = "";
    private final String[] sectionTags = {
            "overview",
            "core_content_basic",
            "core_content_essential",
            "core_content_advanced",
            "example_exercises_basic",
            "example_exercises_essential",
            "example_exercises_advanced"
    };
    private Map<String, Object> templateSections;

    private void parseLectureTemplate(String templateContent) {
        // 提取<command>部分
        Pattern commandPattern = Pattern.compile("<command>(.*?)</command>", Pattern.DOTALL);
        Matcher commandMatcher = commandPattern.matcher(templateContent);
        if (commandMatcher.find()) {
            commandInstructions = commandMatcher.group(1).trim();
        } else {
            commandInstructions = "";
        }
        // 提取各部分内容
        templateSections = new ConcurrentHashMap<>();
        for (String tag : sectionTags) {
            Pattern sectionPattern = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">", Pattern.DOTALL);
            Matcher sectionMatcher = sectionPattern.matcher(templateContent);
            if (sectionMatcher.find()) {
                String content = sectionMatcher.group(1).trim();
                // 去除每行前导空格
                StringBuilder sb = new StringBuilder();
                for (String line : content.split("\n")) {
                    sb.append(line.strip()).append("\n");
                }
                templateSections.put(tag, sb.toString().trim());
            } else {
                templateSections.put(tag, "");
            }
        }
    }

    /**
     * 根据大纲JSON生成详细讲义内容（多线程并发调用大模型）
     */
    public String generateLecture(JsonNode syllabusData) {
        JsonNode teachingContentNode = syllabusData.get("teaching_content").get("teaching_content");
        if (teachingContentNode == null || !teachingContentNode.isArray()) {
            return "未找到教学内容";
        }
        List<String> unitNumbers = new ArrayList<>();
        List<String> contents = new ArrayList<>();
        List<String> ideologicals = new ArrayList<>();
        List<String> timeAllocations = new ArrayList<>();
        for (JsonNode unit : teachingContentNode) {
            unitNumbers.add(unit.get("unit_number").asText());
            contents.add(unit.get("content").asText());
            ideologicals.add(unit.get("ideological_and_political_integration").asText());
            timeAllocations.add(unit.get("time_allocation").asText());
        }
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < unitNumbers.size(); i++) {
            final int idx = i;
            futures.add(executor.submit(() -> {
                String prompt = String.format(
                    "单元编号: %s\n单元内容: %s\n思政目标: %s\n学时分配: %s\n请为该单元生成详细讲义内容，分为概述、基础内容、必备内容、进阶内容、基础习题、必备习题、进阶习题。",
                    unitNumbers.get(idx), contents.get(idx), ideologicals.get(idx), timeAllocations.get(idx)
                );
                return callLLM(prompt);
            }));
        }
        
        // 构建返回的JSON结构
        org.json.JSONObject result = new org.json.JSONObject();
        result.put("status", "success");
        result.put("message", "讲义生成成功");
        
        org.json.JSONArray units = new org.json.JSONArray();
        try {
            for (int i = 0; i < futures.size(); i++) {
                org.json.JSONObject unit = new org.json.JSONObject();
                unit.put("unit_number", unitNumbers.get(i));
                unit.put("unit_title", contents.get(i));
                unit.put("ideological_target", ideologicals.get(i));
                unit.put("time_allocation", timeAllocations.get(i));
                
                // 直接使用JSON响应，保持原始格式
                String llmResponse = futures.get(i).get();

                llmResponse = cleanLLMResponse(llmResponse);

                // 将JSON字符串作为原始字符串存储，避免转义
                unit.put("lecture_content", llmResponse);
                
                units.put(unit);
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "生成讲义内容时发生错误: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
        
        result.put("units", units);
        
        // 返回JSON字符串
        return result.toString();
    }

    private String cleanLLMResponse(String response) {
        if (response == null) return "";

        response = response.trim();
        if (response.startsWith("```")) {
            response = response.replaceFirst("```[a-zA-Z]*\\n?", ""); // 去除 ```json 或 ```
        }
        if (response.endsWith("```")) {
            response = response.substring(0, response.lastIndexOf("```")).trim(); // 去尾部 ```
        }
        return response;
    }

    // 修改callLLM函数，只接收prompt参数
    private String callLLM(String prompt) {
        // 构建请求参数
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", openAIConfig.getModelName());

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "你是一名课程讲义内容生成专家..."));
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        Map<String, Object> headers = Map.of("Authorization", "Bearer " + openAIConfig.getApiKey());

        String jsonBody;
        try {
            jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(requestBody);
        } catch (Exception e) {
            return "请求体序列化失败: " + e.getMessage();
        }

        String response = HttpUtil.postJson(openAIConfig.getApiUrl(), jsonBody, headers);

        try {
            JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response);
            return root.get("choices").get(0).get("message").get("content").asText();
        } catch (Exception e) {
            return "响应解析失败: " + e.getMessage();
        }
    }
//    private String callLLM(String prompt) {
//        // 构建请求参数
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", openAIConfig.getModelName());
//        List<Map<String, Object>> messages = new ArrayList<>();
//        Map<String, Object> systemMsg = new HashMap<>();
//        systemMsg.put("role", "system");
//        systemMsg.put("content", "你是一名课程讲义内容生成专家。请根据用户提供的单元信息和要求，生成结构化、详细的讲义内容。");
//        messages.add(systemMsg);
//        Map<String, Object> userMsg = new HashMap<>();
//        userMsg.put("role", "user");
//        userMsg.put("content", prompt);
//        messages.add(userMsg);
//        requestBody.put("messages", messages);
//        Map<String, Object> headers = new HashMap<>();
//        headers.put("Authorization", "Bearer " + openAIConfig.getApiKey());
//        String jsonBody;
//        try {
//            jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(requestBody);
//        } catch (Exception e) {
//            return "请求体序列化失败: " + e.getMessage();
//        }
//        String response = HttpUtil.postJson(
//            openAIConfig.getApiUrl(),
//            jsonBody,
//            headers
//        );
//        // 直接返回JSON响应，让调用方解析
//        return response;
//    }



    private String getOrDefault(Future<String> future) {
        try {
            return future.get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            return "内容生成失败";
        }
    }
}
