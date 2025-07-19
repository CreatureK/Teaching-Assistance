package com.java_web.backend.Teacher.Controller;

import com.java_web.backend.Common.DTO.IntroductionAndTargetRequestDTO;
import com.java_web.backend.Common.DTO.LectureRequestDTO;
import com.java_web.backend.Common.DTO.SyllabusRequestDTO;
import com.java_web.backend.Common.Service.LLMIntroductionAndTargetService;
import com.java_web.backend.Common.Service.LLMLectureService;
import com.java_web.backend.Common.Service.LLMSyllabusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * LLMController 控制器
 * 提供与大模型内容生成相关的接口，包括课程介绍与目标、初版教学大纲的生成。
 */
@RestController
@RequestMapping("/api/llm")
public class LLMController {
    @Autowired
    private LLMIntroductionAndTargetService introductionService;
    @Autowired
    private LLMSyllabusService syllabusService;
    @Autowired
    private LLMLectureService lectureService;

    /**
     * 生成课程介绍和教学目标
     * @param req IntroductionRequest 请求体，包含课程ID、课程名称、用户需求等
     * @return 由大模型生成的课程介绍和教学目标内容
     */
    @PostMapping("/introduction_and_target")
    public ResponseEntity<?> generateIntroductionAndTarget(@RequestBody IntroductionAndTargetRequestDTO req) {
        var result = introductionService.generateIntroductionAndTarget(req);
        return ResponseEntity.ok(result);
    }

    /**
     * 生成初版教学大纲
     * @param req SyllabusRequest 请求体，包含课程ID、课程名称、学时、重点内容、用户需求等
     * @return 由大模型生成的初版教学大纲内容
     */
    @PostMapping("/syllabus")
    public ResponseEntity<String> generateSyllabus(@RequestBody SyllabusRequestDTO req) {
        String result = syllabusService.generateInitialSyllabus(req);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/lecture")
    public ResponseEntity<String> generateLecture(@RequestBody JsonNode syllabusData) {
        String result = lectureService.generateLecture(syllabusData);
        return ResponseEntity.ok(result);
    }
} 