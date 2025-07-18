package com.java_web.backend.Teacher.Controller;

import com.java_web.backend.Common.DTO.InitialSyllabusRequestDTO;
import com.java_web.backend.Common.Entity.InitialSyllabusRequest;
import com.java_web.backend.Common.Service.LLMInitialSyllabusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始教学大纲生成控制器
 */
@RestController
@RequestMapping("/api/initial-syllabus")
@CrossOrigin(originPatterns = "*")
public class InitialSyllabusController {

    @Autowired
    private LLMInitialSyllabusService initialSyllabusService;

    /**
     * 生成初始教学大纲
     *
     * @param request 教学大纲生成请求
     * @return 生成的教学大纲
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateInitialSyllabus(@RequestBody InitialSyllabusRequest request) {
        try {
            Map<String, Object> result = initialSyllabusService.generateInitialSyllabus(
                    request.getCourseId(),
                    request.getCourseCode(),
                    request.getCourseTitle(),
                    request.getTeachingLanguage(),
                    request.getResponsibleCollege(),
                    request.getCourseCategory(),
                    request.getPrinciple(),
                    request.getVerifier(),
                    request.getCredit(),
                    request.getCourseHour(),
                    request.getCourseIntroduction(),
                    request.getTeachingTarget(),
                    request.getEvaluationMode(),
                    request.getWhetherTechnicalCourse(),
                    request.getAssessmentType(),
                    request.getGradeRecording(),
                    request.getRequest()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "初始教学大纲生成成功");
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "生成教学大纲时发生错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "系统错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 健康检查接口
     *
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "初始教学大纲服务运行正常");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
} 