package com.java_web.backend.service;

import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.Service.LLMJsonToMarkdownService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LLMJsonToMarkdownServiceTest {

    @Mock
    private OpenAIConfig openAIConfig;

    @Mock
    private LLMJsonToMarkdownService service;

    @Test
    void testConvertJsonToMarkdown() {
        String testJson = "{\"course_id\":\"CS001\",\"course_code\":\"CS101\",\"course_Chinese_name\":\"计算机科学导论\"}";
        String expectedMarkdown = "# 计算机科学导论 (CS101)\n\n## 课程基本信息\n- **课程编号**: CS001\n- **课程代码**: CS101";
        
        when(service.convertJsonToMarkdown(anyString(), anyString(), anyString())).thenReturn(expectedMarkdown);
        
        String result = service.convertJsonToMarkdown(testJson, "markdown", "默认样式");
        
        assertNotNull(result);
        assertTrue(result.contains("# 计算机科学导论 (CS101)"));
        assertTrue(result.contains("**课程编号**: CS001"));
        assertTrue(result.contains("**课程代码**: CS101"));
    }

    @Test
    void testConvertJsonToMarkdownWithCustomStyle() {
        String testJson = "{\"course_id\":\"CS001\",\"course_code\":\"CS101\",\"course_Chinese_name\":\"计算机科学导论\"}";
        String customStyle = "使用表格格式展示课程信息";
        String expectedMarkdown = "| 字段 | 值 |\n|------|----|\n| 课程编号 | CS001 |";
        
        when(service.convertJsonToMarkdownWithCustomStyle(anyString(), anyString())).thenReturn(expectedMarkdown);
        
        String result = service.convertJsonToMarkdownWithCustomStyle(testJson, customStyle);
        
        assertNotNull(result);
        assertTrue(result.contains("| 字段 | 值 |"));
        assertTrue(result.contains("| 课程编号 | CS001 |"));
    }

    @Test
    void testBatchConvertJsonToMarkdown() {
        Map<String, String> jsonContents = Map.of(
            "test1", "{\"course_id\":\"CS001\"}",
            "test2", "{\"course_id\":\"CS002\"}"
        );
        
        Map<String, String> expectedResults = Map.of(
            "test1", "# 课程1\n- 课程编号: CS001",
            "test2", "# 课程2\n- 课程编号: CS002"
        );
        
        when(service.batchConvertJsonToMarkdown(any())).thenReturn(expectedResults);
        
        Map<String, String> result = service.batchConvertJsonToMarkdown(jsonContents);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("test1"));
        assertTrue(result.containsKey("test2"));
        assertTrue(result.get("test1").contains("CS001"));
        assertTrue(result.get("test2").contains("CS002"));
    }

    @Test
    void testConvertJsonToMarkdownWithError() {
        String testJson = "{\"course_id\":\"CS001\"}";
        
        when(service.convertJsonToMarkdown(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("大模型异常"));
        
        assertThrows(RuntimeException.class, () -> 
            service.convertJsonToMarkdown(testJson, "markdown", "默认样式")
        );
    }

    @Test
    void testConvertJsonToMarkdownWithInvalidJson() {
        String invalidJson = "invalid json";
        
        when(service.convertJsonToMarkdown(anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("JSON格式不正确"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            service.convertJsonToMarkdown(invalidJson, "markdown", "默认样式")
        );
    }
} 