package com.java_web.backend.Common.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.DTO.LectureRequestDTO;
import com.java_web.backend.Common.DTO.LectureRequestDTO.LectureSection;
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

    public String generateLecture(LectureRequestDTO req) {
        // 读取lecture模板
        String templateContent = LLMIntroductionAndTargetService.PromptUtil.readPrompt("prompt/lecture/prompt_for_lecture_generation.txt");
        parseLectureTemplate(templateContent);
        // 读取tools
        String tools = LLMIntroductionAndTargetService.PromptUtil.readPrompt("prompt/lecture/lecture_generate_tools.txt");

        List<LectureSection> sections = req.getSections();
        if (sections == null || sections.isEmpty()) {
            return "未提供章节内容";
        }

        StringBuilder allContent = new StringBuilder();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        try {
            for (int i = 0; i < sections.size(); i++) {
                LectureSection section = sections.get(i);
                String topic = section.getTopic();
                allContent.append("# ").append(i + 1).append(". ").append(topic).append("\n\n");

                // 并发生成各部分内容
                Future<String> overviewFuture = executor.submit(() -> callLLM("概述", templateSections.getOrDefault("overview", "").toString(), req, tools));
                Future<String> coreBasicFuture = executor.submit(() -> callLLM("核心内容（基础）", templateSections.getOrDefault("core_content_basic", "").toString(), req, tools));
                Future<String> coreEssentialFuture = executor.submit(() -> callLLM("核心内容（必备）", templateSections.getOrDefault("core_content_essential", "").toString(), req, tools));
                Future<String> coreAdvancedFuture = executor.submit(() -> callLLM("核心内容（进阶）", templateSections.getOrDefault("core_content_advanced", "").toString(), req, tools));
                Future<String> exBasicFuture = executor.submit(() -> callLLM("例题选讲（基础）", templateSections.getOrDefault("example_exercises_basic", "").toString(), req, tools));
                Future<String> exEssentialFuture = executor.submit(() -> callLLM("例题选讲（必备）", templateSections.getOrDefault("example_exercises_essential", "").toString(), req, tools));
                Future<String> exAdvancedFuture = executor.submit(() -> callLLM("例题选讲（进阶）", templateSections.getOrDefault("example_exercises_advanced", "").toString(), req, tools));

                String overview = getOrDefault(overviewFuture);
                String coreBasic = getOrDefault(coreBasicFuture);
                String coreEssential = getOrDefault(coreEssentialFuture);
                String coreAdvanced = getOrDefault(coreAdvancedFuture);
                String exBasic = getOrDefault(exBasicFuture);
                String exEssential = getOrDefault(exEssentialFuture);
                String exAdvanced = getOrDefault(exAdvancedFuture);

                allContent.append("## 概述\n").append(overview).append("\n\n");
                allContent.append("## 核心内容（基础）\n").append(coreBasic).append("\n\n");
                allContent.append("## 核心内容（必备）\n").append(coreEssential).append("\n\n");
                allContent.append("## 核心内容（进阶）\n").append(coreAdvanced).append("\n\n");
                allContent.append("## 例题选讲（基础）\n").append(exBasic).append("\n\n");
                allContent.append("## 例题选讲（必备）\n").append(exEssential).append("\n\n");
                allContent.append("## 例题选讲（进阶）\n").append(exAdvanced).append("\n\n");
            }
        } finally {
            executor.shutdown();
        }
        return allContent.toString();
    }

    private String callLLM(String sectionName, String promptContent, LectureRequestDTO req, String tools) {
        if (promptContent == null || promptContent.isEmpty() || "无".equals(promptContent) || "未提供".equals(promptContent)) {
            return "无数据";
        }
        JSONObject body = new JSONObject();
        body.put("model", openAIConfig.getModelName());
        JSONArray messages = new JSONArray();
        String systemContent = "你是一名课程设计专家，下面是你要设计的课程内容: " + sectionName;
        if (req.getRequest() != null && !req.getRequest().isEmpty()) {
            systemContent += "\n\n<用户特别要求>\n" + req.getRequest();
        }
        messages.put(new JSONObject().put("role", "system").put("content", systemContent));
        messages.put(new JSONObject().put("role", "user").put("content", "<设计要求> " + promptContent));
        messages.put(new JSONObject().put("role", "system").put("content", "<工具> " + tools));
        if (commandInstructions != null && !commandInstructions.isEmpty()) {
            messages.put(new JSONObject().put("role", "system").put("content", "<command>" + commandInstructions + "</command>"));
        }
        body.put("messages", messages);
        Map<String, Object> headers = new java.util.HashMap<>();
        try {
            headers.put("Authorization", "Bearer " + openAIConfig.getApiKey());
            headers.put("Content-Type", "application/json");
            String response = HttpUtil.postJson(openAIConfig.getApiUrl(), body.toString(), headers);
            System.out.println("【" + sectionName + "】大模型原始输出：\n" + response); // 打印大模型输出
            return response != null && !response.isEmpty() ? response : "内容生成失败";
        } catch (Exception e) {
            return "内容生成失败: " + e.getMessage();
        }
    }

    private String getOrDefault(Future<String> future) {
        try {
            return future.get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            return "内容生成失败";
        }
    }
}
