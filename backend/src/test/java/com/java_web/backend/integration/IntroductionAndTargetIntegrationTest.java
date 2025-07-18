package com.java_web.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Common.DTO.IntroductionAndTargetRequestDTO;
import com.java_web.backend.Common.DTO.IntroductionAndTargetResponseDTO;
import com.java_web.backend.Common.Entity.IntroductionAndTargetResponse;
import com.java_web.backend.Common.Service.LLMIntroductionAndTargetService;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.java_web.backend.BackendApplication.class)
@AutoConfigureMockMvc
@Import(IntroductionAndTargetIntegrationTest.TestConfig.class)
public class IntroductionAndTargetIntegrationTest {
    
    // 测试配置类，配置消息转换器
    @org.springframework.context.annotation.Configuration
    public static class TestConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {
        @Override
        public void extendMessageConverters(java.util.List<org.springframework.http.converter.HttpMessageConverter<?>> converters) {
            // 确保JSON消息转换器存在
            boolean hasJsonConverter = converters.stream()
                .anyMatch(converter -> converter instanceof org.springframework.http.converter.json.MappingJackson2HttpMessageConverter);
            
            if (!hasJsonConverter) {
                converters.add(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter());
            }
        }
        
        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            return Jackson2ObjectMapperBuilder.json().build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LLMIntroductionAndTargetService llmIntroductionAndTargetService;

    @MockBean
    private com.java_web.backend.Common.Service.JWTService jwtService;

    @MockBean
    private com.java_web.backend.Common.Service.LLMInitialSyllabusService llmInitialSyllabusService;

    @MockBean
    private com.java_web.backend.Common.Service.LLMJsonToMarkdownService llmJsonToMarkdownService;

    @MockBean
    private com.java_web.backend.Common.Service.LLMSyllabusService llmSyllabusService;

    @MockBean
    private com.java_web.backend.Common.Service.LLMLectureService llmLectureService;

    private TestResultWriter testResultWriter;

    @BeforeEach
    void setUp() {
        testResultWriter = new TestResultWriter();
        
        // 配置JWT服务模拟，匹配任意token
        when(jwtService.parseToken(org.mockito.ArgumentMatchers.anyString())).thenReturn(createMockClaims());
    }
    
    private io.jsonwebtoken.Claims createMockClaims() {
        return new io.jsonwebtoken.Claims() {
            @Override
            public Object get(Object key) {
                if ("id".equals(key)) return 1;
                if ("role".equals(key)) return "teacher";
                return null;
            }
            @Override
            public <T> T get(String key, Class<T> requiredType) {
                Object value = get(key);
                if (requiredType.isInstance(value)) {
                    return requiredType.cast(value);
                }
                return null;
            }
            @Override
            public String getSubject() {
                return "testuser";
            }
            @Override public int size() { return 0; }
            @Override public boolean isEmpty() { return false; }
            @Override public boolean containsKey(Object key) { return false; }
            @Override public boolean containsValue(Object value) { return false; }
            @Override public Object put(String key, Object value) { return null; }
            @Override public Object remove(Object key) { return null; }
            @Override public void putAll(java.util.Map<? extends String, ?> m) {}
            @Override public void clear() {}
            @Override public java.util.Set<String> keySet() { return null; }
            @Override public java.util.Collection<Object> values() { return null; }
            @Override public java.util.Set<java.util.Map.Entry<String, Object>> entrySet() { return null; }
            @Override public Object getOrDefault(Object key, Object defaultValue) { return null; }
            @Override public void forEach(java.util.function.BiConsumer<? super String, ? super Object> action) {}
            @Override public void replaceAll(java.util.function.BiFunction<? super String, ? super Object, ? extends Object> function) {}
            @Override public Object putIfAbsent(String key, Object value) { return null; }
            @Override public boolean remove(Object key, Object value) { return false; }
            @Override public boolean replace(String key, Object oldValue, Object newValue) { return false; }
            @Override public Object replace(String key, Object value) { return null; }
            @Override public Object computeIfAbsent(String key, java.util.function.Function<? super String, ? extends Object> mappingFunction) { return null; }
            @Override public Object computeIfPresent(String key, java.util.function.BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) { return null; }
            @Override public Object compute(String key, java.util.function.BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) { return null; }
            @Override public Object merge(String key, Object value, java.util.function.BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) { return null; }
            @Override public java.util.Date getExpiration() { return null; }
            @Override public java.util.Date getNotBefore() { return null; }
            @Override public java.util.Date getIssuedAt() { return null; }
            @Override public String getIssuer() { return null; }
            @Override public java.util.Set<String> getAudience() { return null; }
            @Override public String getId() { return null; }
        };
    }

    @Test
    void testIntroductionAndTargetEndpoint_Success() throws Exception {
        // 准备测试数据
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId("1");
        request.setCourseTitle("高等数学");
        request.setRequest("请结合工程实际，突出应用能力培养");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("1");
        mockResponse.setCourseIntroduction("本课程是面向工程类专业本科生的高等数学课程");
        mockResponse.setTeachingTarget("通过本课程的学习，学生应掌握高等数学的基本理论");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequestDTO.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        MvcResult result = mockMvc.perform(post("/api/llm/introduction_and_target")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.courseId").value("1"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists())
                .andReturn();

        // 解析响应
        String responseContent = result.getResponse().getContentAsString();
        IntroductionAndTargetResponse response = objectMapper.readValue(responseContent, IntroductionAndTargetResponse.class);

        // 验证响应内容
        assertNotNull(response);
        assertEquals("1", response.getCourseId());
        assertNotNull(response.getCourseIntroduction());
        assertNotNull(response.getTeachingTarget());
        assertFalse(response.getCourseIntroduction().isEmpty());
        assertFalse(response.getTeachingTarget().isEmpty());

        // 保存测试结果到文件
        testResultWriter.saveJsonResult("Integration_IntroductionAndTarget_Success", response);
        testResultWriter.saveTextResult("Integration_IntroductionAndTarget_Request", objectMapper.writeValueAsString(request));
        testResultWriter.saveRawResponse("Integration_IntroductionAndTarget_Success", responseContent);
    }

    @Test
    void testIntroductionAndTargetEndpoint_EmptyCourseTitle() throws Exception {
        // 准备测试数据 - 空课程标题
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId("2");
        request.setCourseTitle("");
        request.setRequest("测试请求");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("2");
        mockResponse.setCourseIntroduction("课程介绍");
        mockResponse.setTeachingTarget("教学目标");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequestDTO.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        mockMvc.perform(post("/api/llm/introduction_and_target")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("2"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }

    @Test
    void testIntroductionAndTargetEndpoint_NullRequest() throws Exception {
        // 准备测试数据 - 空请求字段
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId("3");
        request.setCourseTitle("线性代数");
        request.setRequest(null);

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("3");
        mockResponse.setCourseIntroduction("课程介绍");
        mockResponse.setTeachingTarget("教学目标");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequestDTO.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        mockMvc.perform(post("/api/llm/introduction_and_target")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("3"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }

    @Test
    void testIntroductionAndTargetEndpoint_InvalidJson() throws Exception {
        // 发送无效的JSON
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/llm/introduction_and_target")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testIntroductionAndTargetEndpoint_MissingRequiredFields() throws Exception {
        // 准备测试数据 - 缺少必要字段
        String incompleteJson = """
            {
                \"courseId\": \"4\"
            }
            """;

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("4");
        mockResponse.setCourseIntroduction("课程介绍");
        mockResponse.setTeachingTarget("教学目标");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequestDTO.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        mockMvc.perform(post("/api/llm/introduction_and_target")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(incompleteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("4"));
    }

    @Test
    void testIntroductionAndTargetEndpoint_DifferentCourseTypes() throws Exception {
        // 准备测试数据 - 不同类型的课程
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId("5");
        request.setCourseTitle("计算机程序设计");
        request.setRequest("请结合编程实践，注重动手能力培养");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("5");
        mockResponse.setCourseIntroduction("本课程是计算机科学与技术专业的核心课程");
        mockResponse.setTeachingTarget("通过本课程的学习，学生应掌握程序设计的基本方法");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequestDTO.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        mockMvc.perform(post("/api/llm/introduction_and_target")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("5"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }

    @Test
    void testIntroductionAndTargetEndpoint_LongRequest() throws Exception {
        // 准备测试数据 - 长请求内容
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId("6");
        request.setCourseTitle("数据结构与算法");
        request.setRequest("请详细说明课程的教学目标，包括理论知识的掌握、实践技能的培养、创新思维的训练，以及对学生未来职业发展的指导意义。要求内容全面、具体、可操作，能够为教学实施提供明确的指导方向。");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("6");
        mockResponse.setCourseIntroduction("本课程是计算机专业的核心基础课程");
        mockResponse.setTeachingTarget("通过本课程的学习，学生应掌握数据结构与算法的基本理论");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequestDTO.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        mockMvc.perform(post("/api/llm/introduction_and_target")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("6"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }

    @Test
    void testIntroductionAndTargetEndpoint_SpecialCharacters() throws Exception {
        // 准备测试数据 - 包含特殊字符
        IntroductionAndTargetRequestDTO request = new IntroductionAndTargetRequestDTO();
        request.setCourseId("7");
        request.setCourseTitle("C++程序设计");
        request.setRequest("请包含C++、STL、模板等专业术语，以及数学符号如∑、∫等");

        // 模拟Service返回
        IntroductionAndTargetResponse mockResponse = new IntroductionAndTargetResponse();
        mockResponse.setCourseId("7");
        mockResponse.setCourseIntroduction("本课程是C++程序设计的专业课程");
        mockResponse.setTeachingTarget("通过本课程的学习，学生应掌握C++、STL、模板等");
        
        when(llmIntroductionAndTargetService.generateIntroductionAndTarget(any(IntroductionAndTargetRequestDTO.class)))
                .thenReturn(mockResponse);

        // 执行API调用
        mockMvc.perform(post("/api/llm/introduction_and_target")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("7"))
                .andExpect(jsonPath("$.courseIntroduction").exists())
                .andExpect(jsonPath("$.teachingTarget").exists());
    }
} 