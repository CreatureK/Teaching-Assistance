package com.java_web.backend.Common.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.Utils.HttpUtil;

/**
 * 初始教学大纲生成服务类
 * 用于生成完整的课程教学大纲
 */
@Service
public class LLMInitialSyllabusService {

    @Autowired
    private OpenAIConfig openAIConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成初始教学大纲
     *
     * @param courseId 课程ID
     * @param courseCode 课程代码
     * @param courseTitle 课程标题
     * @param teachingLanguage 教学语言
     * @param responsibleCollege 负责学院
     * @param courseCategory 课程类别
     * @param principle 课程负责人
     * @param verifier 审核人
     * @param credit 学分
     * @param courseHour 课程学时
     * @param courseIntroduction 课程介绍
     * @param teachingTarget 教学目标
     * @param evaluationMode 评价模式
     * @param whetherTechnicalCourse 是否技术课程
     * @param assessmentType 考核类型
     * @param gradeRecording 成绩记载
     * @param request 用户请求
     * @return 生成的教学大纲
     */
    public Map<String, Object> generateInitialSyllabus(
            String courseId, String courseCode, String courseTitle, String teachingLanguage,
            String responsibleCollege, String courseCategory, String principle, String verifier,
            String credit, String courseHour, String courseIntroduction, String teachingTarget,
            String evaluationMode, String whetherTechnicalCourse, String assessmentType,
            String gradeRecording, String request) {

        // 并行生成各个部分
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        CompletableFuture<String> englishNameFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return generateEnglishName(courseTitle);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);

        CompletableFuture<Map<String, Object>> detailedTargetFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return generateDetailedCourseTarget(courseTitle, request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);

        CompletableFuture<Map<String, Object>> teachingContentFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return generateTeachingContent(courseTitle, courseHour, request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);

        CompletableFuture<Map<String, Object>> experimentalProjectsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return generateExperimentalProjects(courseTitle, request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);

        CompletableFuture<Map<String, Object>> textbooksFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return generateTextbooksAndReferenceBooks(courseTitle, courseHour, request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);

        // 等待所有任务完成
        CompletableFuture.allOf(englishNameFuture, detailedTargetFuture, teachingContentFuture, 
                               experimentalProjectsFuture, textbooksFuture).join();

        // 整合结果
        Map<String, Object> response = new HashMap<>();
        response.put("course_id", courseId);
        response.put("course_code", courseCode);
        response.put("course_Chinese_name", courseTitle);
        try {
            response.put("course_English_name", englishNameFuture.get());
            response.put("detailed_course_target", detailedTargetFuture.get());
            response.put("teaching_content", teachingContentFuture.get());
            response.put("experimental_projects", experimentalProjectsFuture.get());
            response.put("textbooks_and_reference_books", textbooksFuture.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("任务被中断", e);
        } catch (java.util.concurrent.ExecutionException e) {
            throw new RuntimeException("任务执行异常: " + e.getMessage(), e);
        }
        response.put("teaching_language", teachingLanguage);
        response.put("responsible_college", responsibleCollege);
        response.put("course_category", courseCategory);
        response.put("principle", principle);
        response.put("verifier", verifier);
        response.put("credit", credit);
        response.put("course_hour", courseHour);
        response.put("whether_technical_course", whetherTechnicalCourse);
        response.put("assessment_type", assessmentType);
        response.put("grade_recording", gradeRecording);
        response.put("course_introduction", courseIntroduction);
        response.put("course_target", teachingTarget);
        response.put("evaluation_mode", evaluationMode);

        executor.shutdown();
        return response;
    }

    /**
     * 生成课程英文名
     */
    private String generateEnglishName(String courseTitle) throws JsonProcessingException {
        String prompt = "你是一位资深大学教授，你知道各个学科对应的中文名和英文名分别是什么。\n" +
                       "用户会给你一个课程的中文名，你需要将其翻译成英文，并且返回给用户。注意，你返回的内容应该有且仅有课程的英文名，并且是一个字符串，而不要有其他任何多余的内容\n" +
                       "课程的中文名是" + courseTitle + "，请给出它的英文名。";

        String response = callLLM(prompt);
        return response.trim();
    }

    /**
     * 生成详细课程目标
     */
    private Map<String, Object> generateDetailedCourseTarget(String courseTitle, String request) throws JsonProcessingException {
        String promptTemplate = loadPromptFromFile("prompt/syllabus/prompt_for_detailed_course_target.txt");
        String prompt = "你是一位资深大学教授，尤其擅长" + courseTitle + "学科的教学以及大纲制作，你需要按照模版进行大纲的制作\n" +
                       "在本次生成中，你需要生成的大纲部分是 detailed_course_target，其json模版内容后续会指定。\n" +
                       "你需要从下面信息中挑选和本部分生成相关的内容有关联的有效信息，而不是全部纳入考虑范围。\n" +
                       promptTemplate + "\n" +
                       "你需要生成一份教学大纲的部分内容，课程标题为:" + courseTitle + ",请结合相关的知识库内容进行对应大纲部分内容的制作\n" +
                       "后续是用户的额外需求，但是首先，你需要判断用户的需求和本部分生成是否相关，再决定是否执行。在制作的过程中，用户的制作要求是:<request>" + request + "</request>";

        String response = callLLM(prompt);
        try {
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成教学内容
     */
    private Map<String, Object> generateTeachingContent(String courseTitle, String courseHour, String request) throws JsonProcessingException {
        String promptTemplate = loadPromptFromFile("prompt/syllabus/prompt_for_teaching_content.txt");
        String prompt = "你是一位资深大学教授，尤其擅长" + courseTitle + "学科的教学以及大纲制作，你需要按照模版进行大纲的制作\n" +
                       "在本次生成中，你需要生成的大纲部分是 teaching_content，其json模版内容后续会指定。\n" +
                       "你需要从下面信息中挑选和本部分生成相关的内容有关联的有效信息，而不是全部纳入考虑范围。\n" +
                       "该门课的总学时为" + courseHour + "，你需要合理分配这些学时\n" +
                       promptTemplate + "\n" +
                       "你需要生成一份教学大纲的部分内容，课程标题为:" + courseTitle + ",请结合相关的知识库内容进行对应大纲部分内容的制作\n" +
                       "后续是用户的额外需求，但是首先，你需要判断用户的需求和本部分生成是否相关，再决定是否执行。在制作的过程中，用户的制作要求是:<request>" + request + "</request>";

        String response = callLLM(prompt);
        try {
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成实验项目
     */
    private Map<String, Object> generateExperimentalProjects(String courseTitle, String request) throws JsonProcessingException {
        String promptTemplate = loadPromptFromFile("prompt/syllabus/prompt_for_experimental_projects.txt");
        String prompt = "你是一位资深大学教授，尤其擅长" + courseTitle + "学科的教学以及大纲制作，你需要按照模版进行大纲的制作\n" +
                       "在本次生成中，你需要生成的大纲部分是 experimental_projects，其json模版内容后续会指定。\n" +
                       "你需要从下面信息中挑选和本部分生成相关的内容有关联的有效信息，而不是全部纳入考虑范围。\n" +
                       promptTemplate + "\n" +
                       "你需要生成一份教学大纲的部分内容，课程标题为:" + courseTitle + ",请结合相关的知识库内容进行对应大纲部分内容的制作\n" +
                       "后续是用户的额外需求，但是首先，你需要判断用户的需求和本部分生成是否相关，再决定是否执行。在制作的过程中，用户的制作要求是:<request>" + request + "</request>";

        String response = callLLM(prompt);
        try {
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成教材和参考书
     */
    private Map<String, Object> generateTextbooksAndReferenceBooks(String courseTitle, String courseHour, String request) throws JsonProcessingException {
        String promptTemplate = loadPromptFromFile("prompt/syllabus/prompt_for_textbooks_and_reference_books.txt");
        String prompt = "你是一位资深大学教授，尤其擅长" + courseTitle + "学科的教学以及大纲制作，你需要按照模版进行大纲的制作\n" +
                       "在本次生成中，你需要生成的大纲部分是 textbooks_and_reference_books，其json模版内容后续会指定。\n" +
                       "你需要从下面信息中挑选和本部分生成相关的内容有关联的有效信息，而不是全部纳入考虑范围。\n" +
                       "该门课的总学时为" + courseHour + "，你需要合理分配这些学时\n" +
                       promptTemplate + "\n" +
                       "你需要生成一份教学大纲的部分内容，课程标题为:" + courseTitle + ",请结合相关的知识库内容进行对应大纲部分内容的制作\n" +
                       "后续是用户的额外需求，但是首先，你需要判断用户的需求和本部分生成是否相关，再决定是否执行。在制作的过程中，用户的制作要求是:<request>" + request + "</request>";

        String response = callLLM(prompt);
        try {
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用大模型
     */
    public String callLLM(String prompt) throws JsonProcessingException {
        // 构建请求参数
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", openAIConfig.getModelName());

        java.util.List<Map<String, Object>> messages = new java.util.ArrayList<>();

        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一个专业的教学大纲生成专家，擅长生成结构化的课程教学大纲。");
        messages.add(systemMsg);

        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 8000);

        // 发送请求
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + openAIConfig.getApiKey());

        String jsonBody = new ObjectMapper().writeValueAsString(requestBody); // 确保是 JSON 字符串

        String response = HttpUtil.postJson(
            openAIConfig.getApiUrl(),
            jsonBody,
            headers
        );

        // 解析响应
        try {
            JsonNode responseNode = objectMapper.readTree(response);
            if (responseNode.has("choices") && responseNode.get("choices").isArray() && responseNode.get("choices").size() > 0) {
                JsonNode choice = responseNode.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    return choice.get("message").get("content").asText();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("大模型响应格式错误");
    }

    /**
     * 从文件加载prompt
     */
    private String loadPromptFromFile(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
} 