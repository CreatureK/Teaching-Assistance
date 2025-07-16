package com.java_web.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * OpenAPI（Swagger）配置类，用于生成接口文档
 */
@Configuration
public class OpenApiConfig {
    /**
     * 配置OpenAPI的基本信息，包括标题、版本、描述和联系人
     * @return OpenAPI对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SpringBoot 项目 API 文档")  // 标题
                        .version("1.0")                  // 版本
                        .description("这是一个示例项目的 API 文档") // 描述
                        .contact(new Contact()
                                .name("开发者")           // 联系人姓名
                                .email("dev@example.com"))); // 联系人邮箱
    }
}
