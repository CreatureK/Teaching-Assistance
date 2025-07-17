package com.java_web.backend.service;

import com.java_web.backend.Common.Config.OpenAIConfig;
import com.java_web.backend.Common.DTO.IntroductionAndTargetRequest;
import com.java_web.backend.Common.DTO.IntroductionAndTargetResponse;
import com.java_web.backend.Common.Service.LLMIntroductionAndTargetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LLMIntroductionAndTargetServiceTest {

    @Mock
    private OpenAIConfig openAIConfig;

    @Mock
    private LLMIntroductionAndTargetService service;

    @Test
    void testGenerateIntroductionAndTarget() {
        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseId("CS101");
        request.setCourseTitle("计算机科学导论");
        request.setRequest("用户需求测试");
        
        IntroductionAndTargetResponse expectedResponse = new IntroductionAndTargetResponse("CS101", "intro", "target");
        when(service.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class))).thenReturn(expectedResponse);
        
        IntroductionAndTargetResponse result = service.generateIntroductionAndTarget(request);
        
        assertNotNull(result);
        assertEquals("CS101", result.getCourseId());
        assertEquals("intro", result.getCourseIntroduction());
        assertEquals("target", result.getTeachingTarget());
    }

    @Test
    void testGenerateDetailedTeachingContentAndTarget() {
        IntroductionAndTargetResponse expectedResponse = new IntroductionAndTargetResponse("CS101", "detailed intro", "detailed target");
        when(service.generateDetailedTeachingContentAndTarget(any(), any(), any(), any(), any(), any())).thenReturn(expectedResponse);
        
        IntroductionAndTargetResponse result = service.generateDetailedTeachingContentAndTarget(
                "CS101", "计算机科学导论", "3", "48", "计算机学院", "专业必修"
        );
        
        assertNotNull(result);
        assertEquals("CS101", result.getCourseId());
        assertEquals("detailed intro", result.getCourseIntroduction());
        assertEquals("detailed target", result.getTeachingTarget());
    }

    @Test
    void testGenerateIntroductionAndTargetWithError() {
        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseId("CS101");
        request.setCourseTitle("计算机科学导论");
        request.setRequest("用户需求测试");
        
        when(service.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                .thenThrow(new RuntimeException("大模型异常"));
        
        assertThrows(RuntimeException.class, () -> service.generateIntroductionAndTarget(request));
    }

    @Test
    void testGenerateDetailedTeachingContentAndTargetWithError() {
        when(service.generateDetailedTeachingContentAndTarget(any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("大模型异常"));
        
        assertThrows(RuntimeException.class, () ->
            service.generateDetailedTeachingContentAndTarget(
                "CS101", "计算机科学导论", "3", "48", "计算机学院", "专业必修"
            )
        );
    }

    @Test
    void testGenerateIntroductionAndTargetWithInvalidResponse() {
        IntroductionAndTargetRequest request = new IntroductionAndTargetRequest();
        request.setCourseId("CS101");
        request.setCourseTitle("计算机科学导论");
        request.setRequest("用户需求测试");
        
        when(service.generateIntroductionAndTarget(any(IntroductionAndTargetRequest.class)))
                .thenThrow(new RuntimeException("JSON解析失败"));
        
        assertThrows(RuntimeException.class, () -> service.generateIntroductionAndTarget(request));
    }

    @Test
    void testGenerateDetailedTeachingContentAndTargetWithInvalidResponse() {
        when(service.generateDetailedTeachingContentAndTarget(any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("JSON解析失败"));
        
        assertThrows(RuntimeException.class, () ->
            service.generateDetailedTeachingContentAndTarget(
                "CS101", "计算机科学导论", "3", "48", "计算机学院", "专业必修"
            )
        );
    }
}