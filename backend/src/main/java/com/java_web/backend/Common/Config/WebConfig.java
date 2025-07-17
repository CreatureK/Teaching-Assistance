package com.java_web.backend.Common.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.java_web.backend.Common.Interceptor.LoginInterceptor;

/**
 * Web配置类，用于注册拦截器等Web相关配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册LoginInterceptor，拦截所有请求
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}
