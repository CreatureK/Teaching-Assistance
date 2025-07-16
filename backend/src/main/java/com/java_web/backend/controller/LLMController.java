package com.java_web.backend.controller;

import com.java_web.backend.entity.IntroductionAndTargetRequest;
import com.java_web.backend.service.IntroductionAndTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/llm")
public class LLMController {
    @Autowired
    private IntroductionAndTargetService introductionService;

    private static final Logger logger = LoggerFactory.getLogger(IntroductionAndTargetService.class);

    @PostMapping("/introduction_and_target")
    public ResponseEntity<String> generateIntroduction(@RequestBody IntroductionAndTargetRequest req) {
        try {
            String result = introductionService.generateIntroductionAndTarget(req);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("读取prompt失败", e);
            logger.error("大模型API调用失败");
            e.printStackTrace();
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }
} 