package com.java_web.backend.Interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登录拦截器，用于拦截未登录的请求
 */
public class LoginInterceptor implements HandlerInterceptor {
    /**
     * 预处理方法，在请求到达Controller前执行
     * @return true表示放行，false表示拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 这里简单演示，实际应根据登录状态判断
        if(true){
            System.out.println("pass"); // 放行
            return true;
        }else{
            System.out.println("fail"); // 拦截
            return false;
        }
    }
}
