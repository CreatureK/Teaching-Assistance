package com.java_web.backend.Service;

import com.java_web.backend.Config.OpenAIConfig;
import com.java_web.backend.Entity.SyllabusRequest;
import com.java_web.backend.Utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class LLMSyllabusService {
    @Autowired
    private OpenAIConfig openAIConfig;

    public String generateInitialSyllabus(SyllabusRequest req) {
        String prompt = "你是一位资深大学教授，擅长课程大纲制定。请根据以下信息生成初版教学大纲。";
        JSONObject body = new JSONObject();
        body.put("model", openAIConfig.getModelName());
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", prompt));
        messages.put(new JSONObject().put("role", "user").put("content",
                "课程标题: " + req.getCourseTitle() +
                "，学时: " + req.getCreditHours() +
                "，重点内容: " + req.getKeyTeachingContent() +
                "，需求: " + req.getRequest()));
        body.put("messages", messages);
        Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("Authorization", "Bearer " + openAIConfig.getApiKey());
        headers.put("Content-Type", "application/json");
        String response = HttpUtil.postJson(openAIConfig.getApiUrl(), body.toString(), headers);
        return response;
    }
} 