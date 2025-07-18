package com.java_web.backend.Common.Service;

import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.DTO.IntroductionAndTargetRequestDTO;
import com.java_web.backend.Common.Entity.IntroductionAndTargetResponse;
import com.java_web.backend.Common.Utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

@Service
public class LLMIntroductionAndTargetService {
    @Autowired
    private OpenAIConfig openAIConfig;

    public IntroductionAndTargetResponse generateIntroductionAndTarget(IntroductionAndTargetRequestDTO req) {
        try {
            // 读取prompt和模板文件
            String prompt = PromptUtil.readPrompt("prompt/introduction_and_target/prompt_for_introduction_and_target.txt");
            String template = PromptUtil.readPrompt("templates/introduction_and_target/introduction_and_target.json");
            
            JSONObject body = new JSONObject();
            body.put("model", openAIConfig.getModelName());
            JSONArray messages = new JSONArray();
            
            // 添加完整的系统提示，与LLM模块保持一致
            messages.put(new JSONObject().put("role", "system").put("content", 
                "你是一位资深大学教授，尤其擅长" + req.getCourseTitle() + "学科的教学以及教学内容和教学目标的制定，你需要按照json模板进行教学内容及其对应目标的制作"));
            
            messages.put(new JSONObject().put("role", "system").put("content", 
                "在生成教学内容及其对应目标的时候，你需要参考该json模板示例进行生成:<template>" + template + "</template>"));
            
            messages.put(new JSONObject().put("role", "system").put("content", 
                "你所制作的教学内容需要参考用户指定的重点教学内容，但不应该只有用户指定的部分，而是应该在全部内容涵盖到的基础上，强调用户指定的重点教学内容"));
            
            messages.put(new JSONObject().put("role", "system").put("content", prompt));
            
            messages.put(new JSONObject().put("role", "user").put("content", 
                "你需要生成一份总体的教学内容及其教学目标，课程标题为<course_title>" + req.getCourseTitle() + "</course_title>, 请结合相关的知识库内容进行制作"));
            
            messages.put(new JSONObject().put("role", "user").put("content", 
                "在制作的过程中，用户的制作需求是:<request>" + req.getRequest() + "</request>"));
            
            body.put("messages", messages);
            
            Map<String, Object> headers = new java.util.HashMap<>();
            
            String response = HttpUtil.postJson(openAIConfig.getApiUrl(), body.toString(), headers);
            
            // 解析响应并返回结构化数据
            return parseResponse(req.getCourseId(), response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private IntroductionAndTargetResponse parseResponse(String courseId, String response) {
        try {
            JSONObject responseJson = new JSONObject(response);
            JSONArray choices = responseJson.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                String content = message.getString("content");
                
                // 解析LLM返回的JSON内容
                JSONObject contentJson = new JSONObject(content);
                String courseIntroduction = contentJson.getString("course_introduction");
                String teachingTarget = contentJson.getString("teaching_target");
                
                return new IntroductionAndTargetResponse(courseId, courseIntroduction, teachingTarget);
            }
        } catch (Exception e) {
            // 如果解析失败，返回错误信息
            return new IntroductionAndTargetResponse(courseId, "解析失败: " + e.getMessage(), "解析失败: " + e.getMessage());
        }
        
        return new IntroductionAndTargetResponse(courseId, "生成失败", "生成失败");
    }

    /**
     * 生成详细的教学内容和目标（兼容旧测试代码）
     */
    public IntroductionAndTargetResponse generateDetailedTeachingContentAndTarget(
            String courseId, String courseTitle, String credit, String courseHour, 
            String responsibleCollege, String courseCategory) {
        
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId(courseId);
        request.setCourseTitle(courseTitle);
        request.setRequest("生成详细的教学内容和目标");
        
        return generateIntroductionAndTarget(request);
    }

    /**
     * 调用大模型的公共方法，供测试使用
     */
    public String callLLM(String prompt) {
        try {
            JSONObject body = new JSONObject();
            body.put("model", openAIConfig.getModelName());
            JSONArray messages = new JSONArray();
            
            messages.put(new JSONObject().put("role", "system").put("content", 
                "你是一位资深大学教授，擅长教学内容和教学目标的制定。"));
            messages.put(new JSONObject().put("role", "user").put("content", prompt));
            
            body.put("messages", messages);
            
            Map<String, Object> headers = new java.util.HashMap<>();
            String response = HttpUtil.postJson(openAIConfig.getApiUrl(), body.toString(), headers);
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class PromptUtil {
        public static String readPrompt(String path) {
            try {
                ClassPathResource resource = new ClassPathResource(path);
                return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
} 