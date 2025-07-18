package com.java_web.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java_web.backend.Teacher.Controller.JsonToMarkdownController;
import com.java_web.backend.Common.DTO.JsonToMarkdownRequestDTO;
import com.java_web.backend.Common.Entity.JsonToMarkdownRequest;
import com.java_web.backend.Common.Service.LLMJsonToMarkdownService;
import com.java_web.backend.utils.TestResultWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(JsonToMarkdownController.class)
@Import({JsonToMarkdownControllerTest.TestCorsConfig.class, JsonToMarkdownController.class})
public class JsonToMarkdownControllerTest {

    // 测试专用的CORS配置
    @org.springframework.context.annotation.Configuration
    public static class TestCorsConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {
        @Override
        public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(false)  // 测试环境中设置为false避免CORS错误
                    .maxAge(168000);
        }
        
        // 禁用拦截器
        @Override
        public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
            // 不添加任何拦截器，禁用所有拦截器
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LLMJsonToMarkdownService jsonToMarkdownService;

    @MockBean
    private com.java_web.backend.Common.Service.JWTService jwtService;



    private TestResultWriter testResultWriter;

    @BeforeEach
    void setUp() {
        testResultWriter = new TestResultWriter();
    }

    @Test
    public void testConvertJsonToMarkdown_Success() throws Exception {
        // 准备测试数据
        JsonToMarkdownRequest request = new JsonToMarkdownRequest();
        request.setJsonContent("{\"name\": \"张三\", \"age\": 25}");
        request.setOutputFormat("markdown");
        request.setCustomStyle("使用表格格式展示");

        // 模拟Service返回
        String mockMarkdownContent = "# 个人信息\n\n| 项目 | 内容 |\n|------|------|\n| 姓名 | 张三 |\n| 年龄 | 25 |";
        when(jsonToMarkdownService.convertJsonToMarkdown(anyString(), anyString(), anyString()))
                .thenReturn(mockMarkdownContent);

        // 执行测试
        MvcResult result = mockMvc.perform(post("/api/llm/json_to_markdown")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.markdownContent").exists())
                .andReturn();

        // 保存测试结果
        String responseContent = result.getResponse().getContentAsString();
        testResultWriter.saveJsonResult("Controller_JsonToMarkdown_Success", responseContent);
        testResultWriter.saveTextResult("Controller_JsonToMarkdown_Request", objectMapper.writeValueAsString(request));
    }

    @Test
    public void testConvertJsonToMarkdown_InvalidJson() throws Exception {
        // 准备测试数据
        JsonToMarkdownRequest request = new JsonToMarkdownRequest();
        request.setJsonContent("{ invalid json }");
        request.setOutputFormat("markdown");

        // 模拟Service抛出异常 - 只有当输入是无效JSON时才抛出异常
        when(jsonToMarkdownService.convertJsonToMarkdown("{ invalid json }", "markdown", null))
                .thenThrow(new IllegalArgumentException("JSON格式不正确"));

        // 执行测试
        MvcResult result = mockMvc.perform(post("/api/llm/json_to_markdown")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").exists())
                .andReturn();

        // 保存测试结果
        String responseContent = result.getResponse().getContentAsString();
        testResultWriter.saveJsonResult("Controller_JsonToMarkdown_InvalidJson", responseContent);
    }

    @Test
    public void testBatchConvertJsonToMarkdown_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> request = new HashMap<>();
        request.put("jsonContents", Arrays.asList(
            "{\"name\": \"张三\", \"age\": 25}",
            "{\"name\": \"李四\", \"age\": 30}",
            "{\"name\": \"王五\", \"age\": 28}"
        ));
        request.put("outputFormat", "markdown");
        request.put("customStyle", "使用表格格式");

        // 模拟Service返回
        Map<String, Object> mockResults = new HashMap<>();
        
        Map<String, Object> item0 = new HashMap<>();
        item0.put("status", "success");
        item0.put("content", "# 个人信息\n\n- **姓名**: 张三\n- **年龄**: 25");
        mockResults.put("item_0", item0);
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("status", "success");
        item1.put("content", "# 个人信息\n\n- **姓名**: 李四\n- **年龄**: 30");
        mockResults.put("item_1", item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("status", "success");
        item2.put("content", "# 个人信息\n\n- **姓名**: 王五\n- **年龄**: 28");
        mockResults.put("item_2", item2);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", 3);
        summary.put("success", 3);
        summary.put("error", 0);
        summary.put("successRate", "100.0%");
        mockResults.put("summary", summary);
        
        when(jsonToMarkdownService.batchConvert(
            argThat(array -> array.length == 3 && 
                array[0].equals("{\"name\": \"张三\", \"age\": 25}") &&
                array[1].equals("{\"name\": \"李四\", \"age\": 30}") &&
                array[2].equals("{\"name\": \"王五\", \"age\": 28}")),
            eq("markdown"),
            eq("使用表格格式")
        )).thenReturn(mockResults);

        // 执行测试
        MvcResult result = mockMvc.perform(post("/api/llm/json_to_markdown/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").exists())
                .andReturn();

        // 保存测试结果
        String responseContent = result.getResponse().getContentAsString();
        testResultWriter.saveJsonResult("Controller_JsonToMarkdown_Batch_Success", responseContent);
        testResultWriter.saveTextResult("Controller_JsonToMarkdown_Batch_Request", objectMapper.writeValueAsString(request));
    }

    @Test
    public void testBatchConvertJsonToMarkdown_WithErrors() throws Exception {
        // 准备测试数据
        Map<String, Object> request = new HashMap<>();
        request.put("jsonContents", Arrays.asList(
            "{\"name\": \"张三\", \"age\": 25}",
            "{ invalid json }",
            "{\"name\": \"王五\", \"age\": 28}"
        ));
        request.put("outputFormat", "markdown");

        // 模拟Service返回（包含错误）
        Map<String, Object> mockResults = new HashMap<>();
        
        Map<String, Object> item0 = new HashMap<>();
        item0.put("status", "success");
        item0.put("content", "# 个人信息\n\n- **姓名**: 张三\n- **年龄**: 25");
        mockResults.put("item_0", item0);
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("status", "error");
        item1.put("error", "JSON格式不正确");
        mockResults.put("item_1", item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("status", "success");
        item2.put("content", "# 个人信息\n\n- **姓名**: 王五\n- **年龄**: 28");
        mockResults.put("item_2", item2);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", 3);
        summary.put("success", 2);
        summary.put("error", 1);
        summary.put("successRate", "66.7%");
        mockResults.put("summary", summary);
        
        when(jsonToMarkdownService.batchConvert(
            argThat(array -> array.length == 3 && 
                array[0].equals("{\"name\": \"张三\", \"age\": 25}") &&
                array[1].equals("{ invalid json }") &&
                array[2].equals("{\"name\": \"王五\", \"age\": 28}")),
            eq("markdown"),
            eq(null)
        )).thenReturn(mockResults);

        // 执行测试
        MvcResult result = mockMvc.perform(post("/api/llm/json_to_markdown/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").exists())
                .andReturn();

        // 保存测试结果
        String responseContent = result.getResponse().getContentAsString();
        testResultWriter.saveJsonResult("Controller_JsonToMarkdown_Batch_WithErrors", responseContent);
    }

    @Test
    public void testHealthCheck() throws Exception {
        // 执行健康检查测试
        mockMvc.perform(get("/api/llm/json_to_markdown/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("JSON to Markdown service is running"));
    }

    @Test
    public void testConvertJsonToMarkdown_DefaultFormat() throws Exception {
        // 准备测试数据（不指定输出格式）
        JsonToMarkdownRequestDTO request = new JsonToMarkdownRequestDTO();
        request.setJsonContent("{\"name\": \"张三\", \"age\": 25}");
        // 不设置outputFormat，应该使用默认值

        // 模拟Service返回
        String mockMarkdownContent = "# 个人信息\n\n- **姓名**: 张三\n- **年龄**: 25";
        when(jsonToMarkdownService.convertJsonToMarkdown(anyString(), anyString(), anyString()))
                .thenReturn(mockMarkdownContent);

        // 执行测试
        mockMvc.perform(post("/api/llm/json_to_markdown")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    public void testConvertJsonToMarkdown_EmptyCustomStyle() throws Exception {
        // 准备测试数据（空的自定义样式）
        JsonToMarkdownRequest request = new JsonToMarkdownRequest();
        request.setJsonContent("{\"name\": \"张三\", \"age\": 25}");
        request.setOutputFormat("markdown");
        request.setCustomStyle(""); // 空的自定义样式

        // 模拟Service返回
        String mockMarkdownContent = "# 个人信息\n\n- **姓名**: 张三\n- **年龄**: 25";
        when(jsonToMarkdownService.convertJsonToMarkdown(anyString(), anyString(), anyString()))
                .thenReturn(mockMarkdownContent);

        // 执行测试
        mockMvc.perform(post("/api/llm/json_to_markdown")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    public void testConvertJsonToMarkdown_InitialSyllabus() throws Exception {
        // 准备测试数据 - 使用完整的initial_syllabus.json内容
        String syllabusJson = """
            {
  "course_id": "13",
  "course_code": "1562156",
  "course_Chinese_name": "计算机网络",
  "course_English_name": "Computer Network",
  "teaching_language": "中文",
  "responsible_college": "计算机学院",
  "course_category": "理论课（含实践环节）",
  "principle": "赵钱",
  "verifier": "孙李",
  "credit": "2",
  "course_hour": "40",
  "whether_technical_course": "是",
  "assessment_type": "考查",
  "grade_recording": "百分制",
  "course_introduction": "本课程《计算机网络》旨在全面介绍计算机网络的基本原理、技术及应用。内容涵盖网络体系结构、协议栈（如TCP/IP）、数据传输、路由与交换、网络安全以及现代网络技术（如云计算、物联网等）。通过学习，学生将掌握计算机网络的核心概念，理解网络运行机制，并具备分析和解决实际网络问题的能力。",
  "course_target": "1. 理解计算机网络的基本概念、功能及其发展历史；2. 掌握OSI七层模型和TCP/IP协议栈的工作原理；3. 熟悉数据链路层、网络层、传输层的主要协议和技术；4. 学习路由选择、拥塞控制等关键技术的实现方法；5. 培养对网络安全威胁的认识及防护能力；6. 了解并跟踪现代网络技术（如SDN、5G、边缘计算）的发展趋势；7. 提升学生在网络环境下的问题分析和解决能力。",
  "detailed_course_target": {
    "detailed_course_target": [
      {
        "target_number": "1",
        "target": "掌握计算机网络的基本概念、体系结构及协议分层模型，能够描述OSI和TCP/IP参考模型的工作原理及其区别。",
        "support_graduation_requirement": "支撑毕业要求中的知识基础能力，使学生具备扎实的计算机网络理论知识。"
      },
      {
        "target_number": "2",
        "target": "理解并应用网络通信的关键技术，包括IP地址分配、路由选择算法、差错控制机制等，能够分析实际网络问题并提出解决方案。",
        "support_graduation_requirement": "支撑毕业要求中的问题分析与解决能力，培养学生解决复杂工程问题的能力。"
      },
      {
        "target_number": "3",
        "target": "熟悉常见网络设备（如交换机、路由器、防火墙）的功能与配置方法，能够完成小型局域网的设计与搭建。",
        "support_graduation_requirement": "支撑毕业要求中的实践动手能力，强化学生的实验与操作技能。"
      },
      {
        "target_number": "4",
        "target": "了解网络安全的基础知识与防护措施，能够评估网络系统的安全性，并设计合理的安全策略。",
        "support_graduation_requirement": "支撑毕业要求中的工程伦理与职业素养，提升学生对网络安全重要性的认识。"
      }
    ]
  },
  "evaluation_mode": {
    "class_performance": "0.2",
    "homework": "0.1",
    "midterm_exam": "0.2",
    "lab_work": "0.2",
    "closed-book_exam": "0.3",
    "open-book_exam": "0",
    "ppt_presentation": "0",
    "total": "1.0"
  },
  "teaching_content": {
    "teaching_content": [
      {
        "unit_number": "1",
        "content": "计算机网络概述：介绍计算机网络的基本概念、发展历程、分类和体系结构，重点讲解OSI七层模型与TCP/IP四层模型。",
        "ideological_and_political_integration": "通过讲解互联网的发展历程，引导学生理解技术创新对社会进步的重要性，培养学生的创新意识与责任感。",
        "time_allocation": "4",
        "detailed_time_allocation": {
          "lecture": "3",
          "experiment": "0",
          "computer_practice": "0",
          "practice": "0",
          "extracurricular": "1"
        }
      },
      {
        "unit_number": "2",
        "content": "物理层基础：数据通信基础、信道复用技术（频分复用、时分复用、码分复用）、传输介质及其特性。",
        "ideological_and_political_integration": "结合5G等现代通信技术的应用场景，培养学生关注科技前沿并思考其对国家发展的重要意义。",
        "time_allocation": "4",
        "detailed_time_allocation": {
          "lecture": "3",
          "experiment": "0",
          "computer_practice": "1",
          "practice": "0",
          "extracurricular": "0"
        }
      },
      {
        "unit_number": "3",
        "content": "数据链路层：差错检测与纠正、流量控制与可靠传输机制（如停止等待协议、滑动窗口协议），以太网及MAC地址。",
        "ideological_and_political_integration": "通过学习可靠的通信协议设计原理，引导学生树立严谨的科学态度和工程思维。",
        "time_allocation": "6",
        "detailed_time_allocation": {
          "lecture": "4",
          "experiment": "1",
          "computer_practice": "1",
          "practice": "0",
          "extracurricular": "0"
        }
      },
      {
        "unit_number": "4",
        "content": "网络层：IP协议基本原理、IPv4与IPv6地址分配、子网划分、路由选择算法（距离矢量与链路状态）。",
        "ideological_and_political_integration": "通过IPv6的推广案例，让学生认识到我国在国际标准制定中的贡献，增强民族自豪感。",
        "time_allocation": "8",
        "detailed_time_allocation": {
          "lecture": "5",
          "experiment": "1",
          "computer_practice": "1",
          "practice": "1",
          "extracurricular": "0"
        }
      },
      {
        "unit_number": "5",
        "content": "传输层：TCP与UDP协议的工作原理、拥塞控制与流量控制机制。",
        "ideological_and_political_integration": "通过分析网络拥塞现象，教育学生在网络资源使用中遵守规则，避免浪费公共资源。",
        "time_allocation": "6",
        "detailed_time_allocation": {
          "lecture": "4",
          "experiment": "1",
          "computer_practice": "1",
          "practice": "0",
          "extracurricular": "0"
        }
      },
      {
        "unit_number": "6",
        "content": "应用层：DNS系统工作原理、HTTP/HTTPS协议、电子邮件协议（SMTP、POP3、IMAP）、FTP协议。",
        "ideological_and_political_integration": "通过网络安全相关内容，引导学生重视信息安全，提升防范意识。",
        "time_allocation": "6",
        "detailed_time_allocation": {
          "lecture": "3",
          "experiment": "1",
          "computer_practice": "1",
          "practice": "0",
          "extracurricular": "1"
        }
      },
      {
        "unit_number": "7",
        "content": "网络安全基础：加密技术、认证机制、防火墙、常见攻击类型及其防御方法。",
        "ideological_and_political_integration": "通过网络安全知识的学习，培养学生的社会责任感，鼓励他们为构建安全的网络环境贡献力量。",
        "time_allocation": "6",
        "detailed_time_allocation": {
          "lecture": "3",
          "experiment": "1",
          "computer_practice": "1",
          "practice": "0",
          "extracurricular": "1"
        }
      }
    ]
  },
  "experimental_projects": {
    "experimental_projects": [
      {
        "unit_number": "1",
        "experiment_name": "网络协议分析实验",
        "content_and_requirements": "通过Wireshark等抓包工具，捕获并分析TCP/IP协议数据包，理解其结构和工作原理。要求学生能够解析数据包头部信息，并能识别常见协议（如HTTP、DNS等）的数据交互过程。",
        "experiment_hour": "4",
        "experiment_type": "验证性实验"
      },
      {
        "unit_number": "2",
        "experiment_name": "局域网组建与配置实验",
        "content_and_requirements": "使用交换机和路由器搭建一个小型局域网，配置IP地址、子网掩码和默认网关。要求学生能够实现不同子网之间的通信，并测试网络连通性。",
        "experiment_hour": "6",
        "experiment_type": "设计性实验"
      },
      {
        "unit_number": "3",
        "experiment_name": "Web服务器部署与性能测试实验",
        "content_and_requirements": "在Linux环境下安装并配置Apache或Nginx Web服务器，部署一个简单的静态网站，并使用工具（如ab命令）进行性能测试。要求学生记录并分析测试结果。",
        "experiment_hour": "6",
        "experiment_type": "综合性实验"
      }
    ]
  },
  "textbooks_and_reference_books": {
    "textbooks_and_reference_books": [
      {
        "book_name": "计算机网络：自顶向下方法（原书第8版）",
        "editor": "James F. Kurose, Keith W. Ross",
        "ISBN": "9787111624950",
        "publisher": "机械工业出版社",
        "publication_year": "2019"
      },
      {
        "book_name": "计算机网络（第7版）",
        "editor": "谢希仁",
        "ISBN": "9787115497304",
        "publisher": "人民邮电出版社",
        "publication_year": "2017"
      },
      {
        "book_name": "TCP/IP协议详解（卷1）: 协议",
        "editor": "W. Richard Stevens",
        "ISBN": "9787302112946",
        "publisher": "清华大学出版社",
        "publication_year": "2005"
      }
    ]
  }
}
            """;

        JsonToMarkdownRequest request = new JsonToMarkdownRequest();
        request.setJsonContent(syllabusJson);
        request.setOutputFormat("markdown");
        request.setCustomStyle("使用表格格式展示课程信息，使用列表展示详细内容");

        // 模拟Service返回
        String mockMarkdownContent = "# 计算机网络课程大纲\n\n" +
            "## 基本信息\n\n" +
            "| 项目 | 内容 |\n" +
            "|------|------|\n" +
            "| 课程ID | 13 |\n" +
            "| 课程代码 | 1562156 |\n" +
            "| 课程名称 | 计算机网络 |\n" +
            "| 英文名称 | Computer Network |\n" +
            "| 教学语言 | 中文 |\n" +
            "| 负责学院 | 计算机学院 |\n" +
            "| 课程类别 | 理论课（含实践环节） |\n" +
            "| 学分 | 2 |\n" +
            "| 学时 | 40 |\n\n" +
            "## 课程介绍\n\n" +
            "本课程《计算机网络》旨在全面介绍计算机网络的基本原理、技术及应用。内容涵盖网络体系结构、协议栈（如TCP/IP）、数据传输、路由与交换、网络安全以及现代网络技术（如云计算、物联网等）。通过学习，学生将掌握计算机网络的核心概念，理解网络运行机制，并具备分析和解决实际网络问题的能力。\n\n" +
            "## 课程目标\n\n" +
            "1. 理解计算机网络的基本概念、功能及其发展历史\n" +
            "2. 掌握OSI七层模型和TCP/IP协议栈的工作原理\n" +
            "3. 熟悉数据链路层、网络层、传输层的主要协议和技术\n" +
            "4. 学习路由选择、拥塞控制等关键技术的实现方法\n" +
            "5. 培养对网络安全威胁的认识及防护能力\n" +
            "6. 了解并跟踪现代网络技术（如SDN、5G、边缘计算）的发展趋势\n" +
            "7. 提升学生在网络环境下的问题分析和解决能力";

        when(jsonToMarkdownService.convertJsonToMarkdown(eq(syllabusJson), eq("markdown"), eq("使用表格格式展示课程信息，使用列表展示详细内容")))
                .thenReturn(mockMarkdownContent);

        // 执行测试
        MvcResult result = mockMvc.perform(post("/api/llm/json_to_markdown")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.markdownContent").exists())
                .andReturn();

        // 保存测试结果
        String responseContent = result.getResponse().getContentAsString();
        testResultWriter.saveJsonResult("Controller_JsonToMarkdown_InitialSyllabus", responseContent);
        testResultWriter.saveTextResult("Controller_JsonToMarkdown_InitialSyllabus_Request", objectMapper.writeValueAsString(request));
    }

    @Test
    public void testBatchConvertJsonToMarkdown_WithSyllabus() throws Exception {
        // 准备测试数据 - 包含syllabus的批量转换
        String syllabusJson = """
            {
  "course_id": "13",
  "course_code": "1562156",
  "course_Chinese_name": "计算机网络",
  "course_English_name": "Computer Network",
  "teaching_language": "中文",
  "responsible_college": "计算机学院",
  "course_category": "理论课（含实践环节）",
  "principle": "赵钱",
  "verifier": "孙李",
  "credit": "2",
  "course_hour": "40",
  "whether_technical_course": "是",
  "assessment_type": "考查",
  "grade_recording": "百分制",
  "course_introduction": "本课程《计算机网络》旨在全面介绍计算机网络的基本原理、技术及应用。内容涵盖网络体系结构、协议栈（如TCP/IP）、数据传输、路由与交换、网络安全以及现代网络技术（如云计算、物联网等）。通过学习，学生将掌握计算机网络的核心概念，理解网络运行机制，并具备分析和解决实际网络问题的能力。",
  "course_target": "1. 理解计算机网络的基本概念、功能及其发展历史；2. 掌握OSI七层模型和TCP/IP协议栈的工作原理；3. 熟悉数据链路层、网络层、传输层的主要协议和技术；4. 学习路由选择、拥塞控制等关键技术的实现方法；5. 培养对网络安全威胁的认识及防护能力；6. 了解并跟踪现代网络技术（如SDN、5G、边缘计算）的发展趋势；7. 提升学生在网络环境下的问题分析和解决能力。"
}
            """;

        Map<String, Object> request = new HashMap<>();
        request.put("jsonContents", Arrays.asList(
            "{\"name\": \"张三\", \"age\": 25}",
            syllabusJson,
            "{\"name\": \"王五\", \"age\": 28}"
        ));
        request.put("outputFormat", "markdown");
        request.put("customStyle", "使用表格格式");

        // 模拟Service返回
        Map<String, Object> mockResults = new HashMap<>();
        
        Map<String, Object> item0 = new HashMap<>();
        item0.put("status", "success");
        item0.put("content", "# 个人信息\n\n- **姓名**: 张三\n- **年龄**: 25");
        mockResults.put("item_0", item0);
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("status", "success");
        item1.put("content", "# 课程信息\n\n| 项目 | 内容 |\n|------|------|\n| 课程ID | 13 |\n| 课程名称 | 计算机网络 |\n| 英文名称 | Computer Network |\n| 学分 | 2 |\n| 学时 | 40 |");
        mockResults.put("item_1", item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("status", "success");
        item2.put("content", "# 个人信息\n\n- **姓名**: 王五\n- **年龄**: 28");
        mockResults.put("item_2", item2);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", 3);
        summary.put("success", 3);
        summary.put("error", 0);
        summary.put("successRate", "100.0%");
        mockResults.put("summary", summary);
        
        when(jsonToMarkdownService.batchConvert(
            argThat(array -> array.length == 3 && 
                array[0].equals("{\"name\": \"张三\", \"age\": 25}") &&
                array[1].equals(syllabusJson) &&
                array[2].equals("{\"name\": \"王五\", \"age\": 28}")),
            eq("markdown"),
            eq("使用表格格式")
        )).thenReturn(mockResults);

        // 执行测试
        MvcResult result = mockMvc.perform(post("/api/llm/json_to_markdown/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").exists())
                .andReturn();

        // 保存测试结果
        String responseContent = result.getResponse().getContentAsString();
        testResultWriter.saveJsonResult("Controller_JsonToMarkdown_Batch_WithSyllabus", responseContent);
        testResultWriter.saveTextResult("Controller_JsonToMarkdown_Batch_WithSyllabus_Request", objectMapper.writeValueAsString(request));
    }
} 