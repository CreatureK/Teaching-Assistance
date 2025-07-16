# 测试结果输出功能说明

## 概述

`TestResultWriter` 是一个用于将大模型返回的内容保存到 `test_result_txt` 目录的工具类。它支持多种格式的输出，包括文本、JSON、Markdown等。

## 功能特性

- **自动创建目录**: 自动创建 `test_result_txt` 目录（如果不存在）
- **时间戳命名**: 文件名自动包含时间戳，避免覆盖
- **多种格式支持**: 支持文本、JSON、Markdown、原始响应等多种格式
- **结构化输出**: 包含测试名称、生成时间等元信息

## 使用方法

### 1. 基本使用

```java
@ExtendWith(MockitoExtension.class)
public class YourTestClass {
    
    private TestResultWriter testResultWriter;
    
    @BeforeEach
    void setUp() {
        testResultWriter = new TestResultWriter();
    }
    
    @Test
    void testYourMethod() {
        // 你的测试逻辑
        String llmResponse = "大模型返回的内容";
        
        // 保存结果
        testResultWriter.saveTextResult("TestName", llmResponse);
    }
}
```

### 2. 保存JSON结果

```java
@Test
void testSaveJsonResult() {
    // 创建响应对象
    IntroductionAndTargetResponse response = new IntroductionAndTargetResponse();
    response.setCourseId("1");
    response.setCourseIntroduction("课程介绍内容");
    response.setTeachingTarget("教学目标内容");
    
    // 保存JSON结果
    testResultWriter.saveJsonResult("TestName", response);
}
```

### 3. 保存Markdown结果

```java
@Test
void testSaveMarkdownResult() {
    String markdownContent = """
        # 标题
        
        ## 子标题
        
        内容...
        """;
    
    testResultWriter.saveMarkdownResult("TestName", markdownContent);
}
```

### 4. 保存原始响应

```java
@Test
void testSaveRawResponse() {
    String rawResponse = """
        {
            "choices": [
                {
                    "message": {
                        "content": "..."
                    }
                }
            ]
        }
        """;
    
    testResultWriter.saveRawResponse("TestName", rawResponse);
}
```

## 输出文件格式

### 文本文件 (.txt)
```
测试名称: TestName
生成时间: 2024-01-01 12:00:00
==================================================
实际内容...
```

### JSON文件 (.json)
```json
// 测试名称: TestName
// 生成时间: 2024-01-01 12:00:00
{
  "courseId": "1",
  "courseIntroduction": "...",
  "teachingTarget": "..."
}
```

### Markdown文件 (.md)
```markdown
# TestName

**生成时间:** 2024-01-01 12:00:00

---

# 标题

内容...
```

### 原始响应文件 (_raw.txt)
```
测试名称: TestName_raw
生成时间: 2024-01-01 12:00:00
原始响应内容:
==================================================
{
  "choices": [...]
}
```

## 文件命名规则

文件名格式：`{测试名称}_{时间戳}.{扩展名}`

示例：
- `IntroductionAndTarget_Success_20240101_120000.json`
- `Demo_TextResult_20240101_120000.txt`
- `Controller_Test_20240101_120000.md`

## 在现有测试中使用

### Service层测试

```java
@Test
void testGenerateIntroductionAndTarget_Success() throws IOException {
    // 模拟LLM响应
    String mockResponse = "...";
    
    try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
        httpUtilMock.when(() -> HttpUtil.postJson(anyString(), anyString(), anyMap()))
                .thenReturn(mockResponse);

        // 执行测试
        IntroductionAndTargetResponse response = service.generateIntroductionAndTarget(request);

        // 验证结果
        assertNotNull(response);
        
        // 保存测试结果
        testResultWriter.saveJsonResult("IntroductionAndTarget_Success", response);
        testResultWriter.saveRawResponse("IntroductionAndTarget_Success", mockResponse);
    }
}
```

### Controller层测试

```java
@Test
public void testGenerateIntroductionAndTarget() throws Exception {
    // 准备测试数据
    IntroductionAndTargetRequest req = new IntroductionAndTargetRequest();
    // ... 设置请求数据

    // 执行测试
    MvcResult result = mockMvc.perform(post("/api/llm/introduction_and_target")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andReturn();

    // 保存测试结果
    String responseContent = result.getResponse().getContentAsString();
    testResultWriter.saveJsonResult("Controller_IntroductionAndTarget", responseContent);
    testResultWriter.saveTextResult("Controller_IntroductionAndTarget_Request", 
            objectMapper.writeValueAsString(req));
}
```

### 集成测试

```java
@Test
void testIntroductionAndTargetEndpoint_Success() throws Exception {
    // 准备测试数据
    IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
    // ... 设置请求数据

    // 执行API调用
    MvcResult result = mockMvc.perform(post("/api/llm/introduction_and_target")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

    // 解析响应
    String responseContent = result.getResponse().getContentAsString();
    IntroductionAndTargetResponse response = objectMapper.readValue(responseContent, 
            IntroductionAndTargetResponse.class);

    // 验证响应内容
    assertNotNull(response);
    
    // 保存测试结果
    testResultWriter.saveJsonResult("Integration_IntroductionAndTarget_Success", response);
    testResultWriter.saveTextResult("Integration_IntroductionAndTarget_Request", 
            objectMapper.writeValueAsString(request));
    testResultWriter.saveRawResponse("Integration_IntroductionAndTarget_Success", responseContent);
}
```

## 注意事项

1. **目录位置**: 文件会保存在项目根目录下的 `test_result_txt` 文件夹中
2. **文件覆盖**: 由于使用时间戳命名，不会覆盖之前的文件
3. **异常处理**: 如果保存失败，会在控制台输出错误信息，但不会中断测试
4. **依赖**: 需要 Jackson 库来处理 JSON 序列化

## 运行示例

运行演示测试类来查看输出效果：

```bash
# 运行演示测试
mvn test -Dtest=TestResultOutputDemoTest

# 运行所有测试（包含结果输出）
mvn test
```

运行后，在项目根目录下会创建 `test_result_txt` 文件夹，包含所有测试结果文件。 