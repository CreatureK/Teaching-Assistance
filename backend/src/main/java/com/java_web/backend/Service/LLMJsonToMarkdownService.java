package com.java_web.backend.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Config.OpenAIConfig;
import com.java_web.backend.Utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
    public String convertJsonToMarkdown(String jsonContent, String outputFormat, String customStyle) throws IOException {
        // 验证JSON格式
        if (!isValidJson(jsonContent)) {
            throw new IllegalArgumentException("JSON格式不正确");
        }

        // 解析JSON结构
        JsonNode jsonNode = objectMapper.readTree(jsonContent);
        Map<String, Object> structureInfo = parseJsonStructure(jsonNode);

        // 构建prompt
        String prompt = buildPrompt(jsonContent, structureInfo, outputFormat, customStyle);

        // 调用大模型
        String response = callLLM(prompt);

        return response;
    }

    /**
     * 带自定义样式的JSON转换（兼容旧测试代码）
     */
    public String convertJsonToMarkdownWithCustomStyle(String jsonContent, String customStyle) throws IOException {
        return convertJsonToMarkdown(jsonContent, "markdown", customStyle);
    }

    /**
     * 批量转换JSON文件（兼容旧测试代码）
     */
    public Map<String, String> batchConvertJsonToMarkdown(Map<String, String> jsonContents) throws IOException {
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
    private Map<String, Object> parseJsonStructure(JsonNode jsonNode) {
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
    private String buildPrompt(String jsonContent, Map<String, Object> structureInfo, String outputFormat, String customStyle) throws IOException {
        StringBuilder prompt = new StringBuilder();
        
        // 基础prompt
        String basePrompt = loadPromptFromFile("prompt/json_to_markdown/prompt_for_json_to_markdown.txt");
        if (basePrompt == null) {
            basePrompt = getDefaultPrompt();
        }
        
        prompt.append("请将以下JSON内容转换为").append(outputFormat.toUpperCase()).append("格式的文档：\n\n");
        prompt.append("JSON内容：\n```json\n").append(jsonContent).append("\n```\n\n");
        
        // 结构信息
        prompt.append("JSON结构信息：\n");
        prompt.append("- 数据类型: ").append(structureInfo.get("type")).append("\n");
        if (structureInfo.get("keys") != null) {
            @SuppressWarnings("unchecked")
            java.util.List<String> keys = (java.util.List<String>) structureInfo.get("keys");
            prompt.append("- 主要键: ").append(String.join(", ", keys)).append("\n");
        }
        prompt.append("- 数据长度: ").append(structureInfo.get("length")).append("\n");
        prompt.append("- 包含嵌套结构: ").append(structureInfo.get("hasNested")).append("\n\n");
        
        prompt.append("输出格式要求: ").append(outputFormat.toUpperCase()).append("\n");
        
        // 自定义样式
        if (customStyle != null && !customStyle.trim().isEmpty()) {
            prompt.append("\n自定义样式要求:\n").append(customStyle).append("\n");
        }
        
        // 样式定制指导
        String stylePrompt = loadPromptFromFile("prompt/json_to_markdown/prompt_for_custom_style.txt");
        if (stylePrompt != null) {
            prompt.append("\n样式定制指导:\n").append(stylePrompt).append("\n");
        }
        
        prompt.append("\n请确保：\n");
        prompt.append("1. 保持数据的完整性和准确性\n");
        prompt.append("2. 使用合适的文档结构\n");
        prompt.append("3. 保持良好的可读性和格式\n");
        prompt.append("4. 根据内容类型选择合适的展示方式\n");
        
        return prompt.toString();
    }

    /**
     * 调用大模型
     */
    public String callLLM(String prompt) throws IOException {
        // 构建请求参数
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", openAIConfig.getModelName());

        // 修正：使用List<Map<String, Object>>
        java.util.List<Map<String, Object>> messages = new java.util.ArrayList<>();

        Map<String, Object> systemMsg1 = new HashMap<>();
        systemMsg1.put("role", "system");
        systemMsg1.put("content", "你是一个专业的文档转换专家，擅长将JSON格式的内容转换为结构化的Markdown文档。");
        messages.add(systemMsg1);

        Map<String, Object> systemMsg2 = new HashMap<>();
        systemMsg2.put("role", "system");
        systemMsg2.put("content", getDefaultPrompt());
        messages.add(systemMsg2);

        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 8000);

        // 发送请求
        String response = HttpUtil.postJson(
            openAIConfig.getApiUrl(),
            openAIConfig.getApiKey(),
            requestBody
        );

        // 解析响应
        JsonNode responseNode = objectMapper.readTree(response);
        if (responseNode.has("choices") && responseNode.get("choices").isArray() && responseNode.get("choices").size() > 0) {
            JsonNode choice = responseNode.get("choices").get(0);
            if (choice.has("message") && choice.get("message").has("content")) {
                return choice.get("message").get("content").asText();
            }
        }

        throw new IOException("大模型响应格式错误");
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