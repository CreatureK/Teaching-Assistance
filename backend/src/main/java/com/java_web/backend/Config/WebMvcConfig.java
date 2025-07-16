package com.java_web.backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/");
        
        // 配置上传文件的访问路径
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:./upload/");
        
        // 配置 Editor.md 的静态资源
        registry.addResourceHandler("/resources/lib/editor.md/**")
                .addResourceLocations("classpath:/static/lib/editor.md/");
    }
}
