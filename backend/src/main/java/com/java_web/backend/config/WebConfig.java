package com.java_web.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.java_web.backend.interceptor.LoginInterceptor;

/**
 * Web配置类，用于注册拦截器等Web相关配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 添加自定义的登录拦截器，对所有请求路径进行拦截
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册LoginInterceptor，拦截所有请求
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
    }
}
