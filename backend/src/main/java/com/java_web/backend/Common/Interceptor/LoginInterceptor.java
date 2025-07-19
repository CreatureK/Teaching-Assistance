package com.java_web.backend.Common.Interceptor;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.java_web.backend.Common.Service.JWTService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JWTService jwtService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // 放行Swagger相关接口
        if (path.startsWith("/swagger-ui") ||
            path.equals("/swagger-ui.html") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars")) {
            return true;
        }

        // 排除登录接口
        if (path.equals("/login") || path.equals("/admin/login")) {
            return true;
        }

        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token是否存在
        if (token == null || token.isEmpty()) {
            handleUnauthorized(response, "未登录");
            return false;
        }

        try {
            // 解析token
            Claims claims = jwtService.parseToken(token);

            // 获取用户角色
            String role = claims.get("role", String.class);

            // 管理员API路径检查
            if (path.startsWith("/admin/") && !"admin".equals(role)) {
                handleUnauthorized(response, "无权限访问");
                return false;
            }

            // 将用户信息存入请求属性
            Integer userId = ((Number) claims.get("id")).intValue();
            request.setAttribute("userId", userId);
            request.setAttribute("username", claims.getSubject());
            request.setAttribute("userRole", role);

            return true;
        } catch (Exception e) {
            handleUnauthorized(response, "登录已过期");
            return false;
        }
    }
    
    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
}
