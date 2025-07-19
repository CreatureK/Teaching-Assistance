package com.java_web.backend.Common.Utils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

public class HttpUtil {
    public static String postJson(String url, String json, Map<String, Object> headers) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach((k, v) -> httpHeaders.add(k, v.toString()));
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }

    public static String postJsonWithApiKey(String url, String apiKey, Map<String, Object> requestBody) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("Authorization", "Bearer " + apiKey);
            
            // 将Map转换为JSON字符串
            String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(requestBody);
            
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, httpHeaders);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("HTTP请求失败", e);
        }
    }
} 