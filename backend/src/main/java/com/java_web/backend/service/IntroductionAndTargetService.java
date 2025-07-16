package com.java_web.backend.service;

import com.java_web.backend.entity.IntroductionAndTargetRequest;
import com.java_web.backend.utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;

@Service
public class IntroductionAndTargetService {

    @Value("${openai.api-url}")
    private String apiUrl;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model-name}")
    private String modelName;

    public String generateIntroductionAndTarget(IntroductionAndTargetRequest req) {
        String prompt;
        try {
            prompt = PromptUtil.readPrompt("prompt/introduction_and_target/prompt_for_introduction_and_target.txt");
        } catch (IOException e) {
            return "读取提示词文件失败";
        }

        JSONObject body = new JSONObject();
        body.put("model", modelName);
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", prompt));
        messages.put(new JSONObject().put("role", "user")
                .put("content", "课程标题: " + req.getCourseTitle() + "，需求: " + req.getRequest()));
        body.put("messages", messages);

        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + apiKey,
                "Content-Type", "application/json"
        );

        String response = HttpUtil.postJson(apiUrl, body.toString(), headers);

        if (response == null) {
            return "大模型服务调用失败";
        }

        return response;
    }
}

