package com.java_web.backend.Common.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.Utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON转Markdown服务类
 * 用于将JSON格式的内容转换为结构化的Markdown文档
 */
@Service
public class LLMJsonToMarkdownService {

    @Autowired
    private OpenAIConfig openAIConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将JSON内容转换为Markdown格式
     *
     * @param jsonContent JSON内容字符串
     * @param outputFormat 输出格式 (markdown, html, docx)
     * @param customStyle 自定义样式要求
     * @return 转换后的Markdown内容
     */
    public String convertJsonToMarkdown(String jsonContent, String outputFormat, String customStyle) {
        try {
            // 验证JSON格式
            if (!isValidJson(jsonContent)) {
                throw new IllegalArgumentException("JSON格式不正确");
            }
            
            // 解析JSON结构信息
            Map<String, Object> structureInfo = parseJsonStructure(jsonContent);
            
            // 构建prompt
            String prompt = buildPrompt(jsonContent, structureInfo, outputFormat, customStyle);
            
            // 调用大模型
            String response = callLLM(prompt);
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 带自定义样式的JSON转换（兼容旧测试代码）
     */
    public String convertJsonToMarkdownWithCustomStyle(String jsonContent, String customStyle) {
        try {
            return convertJsonToMarkdown(jsonContent, "markdown", customStyle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量转换JSON文件（兼容旧测试代码）
     */
    public Map<String, String> batchConvertJsonToMarkdown(Map<String, String> jsonContents) {
        try {
            Map<String, String> results = new HashMap<>();
            for (Map.Entry<String, String> entry : jsonContents.entrySet()) {
                try {
                    String result = convertJsonToMarkdown(entry.getValue(), "markdown", "默认样式");
                    results.put(entry.getKey(), result);
                } catch (Exception e) {
                    results.put(entry.getKey(), "转换失败: " + e.getMessage());
                }
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证JSON格式
     */
    private boolean isValidJson(String jsonContent) {
        try {
            objectMapper.readTree(jsonContent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析JSON结构
     */
    private Map<String, Object> parseJsonStructure(String jsonContent) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            Map<String, Object> structureInfo = new HashMap<>();
            
            if (jsonNode.isObject()) {
                structureInfo.put("type", "object");
                java.util.Iterator<String> it = jsonNode.fieldNames();
                java.util.List<String> keys = new java.util.ArrayList<>();
                while (it.hasNext()) {
                    keys.add(it.next());
                }
                structureInfo.put("keys", keys);
                structureInfo.put("length", jsonNode.size());
                structureInfo.put("hasNested", hasNestedStructure(jsonNode, 0, 3));
            } else if (jsonNode.isArray()) {
                structureInfo.put("type", "array");
                structureInfo.put("length", jsonNode.size());
                structureInfo.put("hasNested", hasNestedStructure(jsonNode, 0, 3));
            } else {
                structureInfo.put("type", "primitive");
                structureInfo.put("length", 1);
                structureInfo.put("hasNested", false);
            }
            
            return structureInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查是否有嵌套结构
     */
    private boolean hasNestedStructure(JsonNode node, int currentDepth, int maxDepth) {
        if (currentDepth >= maxDepth) {
            return false;
        }

        if (node.isObject()) {
            for (JsonNode child : node) {
                if (hasNestedStructure(child, currentDepth + 1, maxDepth)) {
                    return true;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                if (hasNestedStructure(child, currentDepth + 1, maxDepth)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 构建prompt
     */
    private String buildPrompt(String jsonContent, Map<String, Object> structureInfo, String outputFormat, String customStyle) {
        try {
            String promptTemplate = loadPromptFromFile("prompt/json_to_markdown/json_to_markdown_prompt.txt");
            
            // 如果prompt模板文件不存在，使用默认prompt
            if (promptTemplate == null) {
                return getDefaultPrompt() + "\n\nJSON内容：\n" + jsonContent + "\n\n输出格式：" + outputFormat + "\n自定义样式：" + customStyle;
            }
            
            // 简化prompt构建，避免String.format的问题
            return promptTemplate + "\n\nJSON内容：\n" + jsonContent + "\n\n输出格式：" + outputFormat + "\n自定义样式：" + customStyle;
        } catch (Exception e) {
            // 如果出现异常，使用默认prompt
            return getDefaultPrompt() + "\n\nJSON内容：\n" + jsonContent + "\n\n输出格式：" + outputFormat + "\n自定义样式：" + customStyle;
        }
    }

    /**
     * 调用大模型
     */
    public String callLLM(String prompt) {
        try {
            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", openAIConfig.getModelName());

            List<Map<String, Object>> messages = new ArrayList<>();

            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一个专业的JSON转Markdown专家，擅长将JSON数据转换为结构化的Markdown文档。");
            messages.add(systemMsg);

            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 8000);

            // 发送请求
            String response = HttpUtil.postJsonWithApiKey(
                openAIConfig.getApiUrl(),
                openAIConfig.getApiKey(),
                requestBody
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从文件加载prompt
     */
    private String loadPromptFromFile(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取默认prompt
     */
    private String getDefaultPrompt() {
        return "你是一个专业的文档转换专家，擅长将JSON格式的内容转换为结构化的Markdown文档。\n\n" +
               "转换要求：\n" +
               "1. 保持JSON数据的完整性和准确性\n" +
               "2. 使用合适的Markdown语法结构\n" +
               "3. 根据内容类型选择合适的标题层级\n" +
               "4. 对于列表数据，使用适当的列表格式\n" +
               "5. 对于表格数据，使用Markdown表格格式\n" +
               "6. 保持内容的逻辑层次和可读性\n" +
               "7. 添加适当的空行和分隔符以提高可读性\n\n" +
               "请将提供的JSON内容转换为格式良好的Markdown文档。";
    }

    /**
     * 批量转换JSON文件
     *
     * @param jsonContents JSON内容列表
     * @param outputFormat 输出格式
     * @param customStyle 自定义样式
     * @return 转换结果列表
     */
    public Map<String, Object> batchConvert(String[] jsonContents, String outputFormat, String customStyle) {
        Map<String, Object> results = new HashMap<>();
        int success = 0;
        int error = 0;

        for (int i = 0; i < jsonContents.length; i++) {
            try {
                String markdownContent = convertJsonToMarkdown(jsonContents[i], outputFormat, customStyle);
                Map<String, Object> itemResult = new HashMap<>();
                itemResult.put("status", "success");
                itemResult.put("content", markdownContent);
                results.put("item_" + i, itemResult);
                success++;
            } catch (Exception e) {
                Map<String, Object> itemResult = new HashMap<>();
                itemResult.put("status", "error");
                itemResult.put("error", e.getMessage());
                results.put("item_" + i, itemResult);
                error++;
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("total", jsonContents.length);
        summary.put("success", success);
        summary.put("error", error);
        summary.put("successRate", String.format("%.1f%%", (double) success / jsonContents.length * 100));
        results.put("summary", summary);

        return results;
    }
} 