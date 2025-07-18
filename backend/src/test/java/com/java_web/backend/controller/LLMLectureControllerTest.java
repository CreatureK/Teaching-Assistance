package com.java_web.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Common.DTO.LectureRequestDTO;
import com.java_web.backend.Common.DTO.LectureRequestDTO.LectureSection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootTest(classes = com.java_web.backend.BackendApplication.class)
@AutoConfigureMockMvc
@Import(LLMLectureControllerTest.TestConfig.class)
public class LLMLectureControllerTest {
    
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
    private com.java_web.backend.Common.Service.JWTService jwtService;

    @MockBean
    private com.java_web.backend.Common.Service.LLMInitialSyllabusService llmInitialSyllabusService;

    @MockBean
    private com.java_web.backend.Common.Service.LLMIntroductionAndTargetService llmIntroductionAndTargetService;

    @MockBean
    private com.java_web.backend.Common.Service.LLMJsonToMarkdownService llmJsonToMarkdownService;

    @MockBean
    private com.java_web.backend.Common.Service.LLMSyllabusService llmSyllabusService;

    @MockBean
    private com.java_web.backend.Common.Service.LLMLectureService llmLectureService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // 配置JWT服务模拟，匹配任意token
        org.mockito.Mockito.when(jwtService.parseToken(org.mockito.ArgumentMatchers.anyString()))
            .thenReturn(createMockClaims());
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
    public void testGenerateLecture() throws Exception {
        LectureRequestDTO req = new LectureRequestDTO();
        req.setCourseId("C001");
        req.setCourseTitle("线性代数");
        req.setUnitTitle("矩阵与线性方程组");
        req.setKnowledgeHour("4");
        req.setKnowledgePoint("矩阵的秩");
        req.setRequest("请严格按照模板和时间分配要求生成讲义内容");

        List<LectureSection> sections = new ArrayList<>();
        LectureSection section = new LectureSection();
        section.setTopic("矩阵的秩");
        section.setOverview("矩阵秩的定义、实际应用、与其他知识点的关系");
        section.setCoreContentBasic("秩的基本概念、相关定理、基础算法");
        section.setCoreContentEssential("秩的计算技巧、过程模拟、复杂度分析");
        section.setCoreContentAdvanced("秩的进阶理论、交叉学科应用、竞赛级优化");
        section.setExampleExercisesBasic("基础例题1、基础例题2、基础例题3");
        section.setExampleExercisesEssential("必备例题1、必备例题2、必备例题3");
        section.setExampleExercisesAdvanced("进阶例题1、进阶例题2");
        sections.add(section);
        req.setSections(sections);

        mockMvc.perform(post("/api/llm/lecture")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
} 