package com.java_web.backend.Common.Service;

import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.DTO.IntroductionAndTargetRequest;
import com.java_web.backend.Common.Utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

@Service
public class LLMIntroductionAndTargetService {
    @Autowired
    private OpenAIConfig openAIConfig;

    public String generateIntroductionAndTarget(IntroductionAndTargetRequest req) throws IOException {
        String prompt = PromptUtil.readPrompt("prompt/introduction_and_target/prompt_for_introduction_and_target.txt");
        JSONObject body = new JSONObject();
        body.put("model", openAIConfig.getModelName());
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", prompt));
        messages.put(new JSONObject().put("role", "user").put("content", "课程标题: " + req.getCourseTitle() + "，需求: " + req.getRequest()));
        body.put("messages", messages);
        Map<String, String> headers = Map.of(
            "Authorization", "Bearer " + openAIConfig.getApiKey(),
            "Content-Type", "application/json"
        );
        String response = HttpUtil.postJson(openAIConfig.getApiUrl(), body.toString(), headers);
        return response;
    }

    public static class PromptUtil {
        public static String readPrompt(String path) throws IOException {
            ClassPathResource resource = new ClassPathResource(path);
            return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
        }
    }
} 