package com.java_web.backend.Teacher.Controller;

import com.java_web.backend.Common.DTO.JsonToMarkdownRequestDTO;
import com.java_web.backend.Common.DTO.JsonToMarkdownResponseDTO;
import com.java_web.backend.Common.Entity.JsonToMarkdownRequest;
import com.java_web.backend.Common.Entity.JsonToMarkdownResponse;
import com.java_web.backend.Common.Service.LLMJsonToMarkdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * JSON转Markdown控制器
 */
@RestController
@RequestMapping("/api/llm")
@CrossOrigin(origins = "*")
public class JsonToMarkdownController {

    @Autowired
    private LLMJsonToMarkdownService jsonToMarkdownService;

    /**
     * 单个JSON转Markdown
     */
    @PostMapping("/json_to_markdown")
    public ResponseEntity<JsonToMarkdownResponse> convertJsonToMarkdown(@RequestBody JsonToMarkdownRequest request) {
        try {
            String markdownContent = jsonToMarkdownService.convertJsonToMarkdown(
                request.getJsonContent(),
                request.getOutputFormat() != null ? request.getOutputFormat() : "markdown",
                request.getCustomStyle()
            );
            
            return ResponseEntity.ok(new JsonToMarkdownResponse(markdownContent, "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new JsonToMarkdownResponse("error", e.getMessage(), true));
        }
    }

    /**
     * 批量JSON转Markdown
     */
    @PostMapping("/json_to_markdown/batch")
    public ResponseEntity<Map<String, Object>> batchConvertJsonToMarkdown(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            String[] jsonContents = ((java.util.List<String>) request.get("jsonContents")).toArray(new String[0]);
            String outputFormat = (String) request.getOrDefault("outputFormat", "markdown");
            String customStyle = (String) request.get("customStyle");
            
            Map<String, Object> results = jsonToMarkdownService.batchConvert(jsonContents, outputFormat, customStyle);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/json_to_markdown/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("JSON to Markdown service is running");
    }
} 