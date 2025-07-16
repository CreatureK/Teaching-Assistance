package com.java_web.backend.Utils;

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
} 