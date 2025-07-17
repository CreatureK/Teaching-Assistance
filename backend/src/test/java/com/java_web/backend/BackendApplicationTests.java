package com.java_web.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.java_web.backend.Common.Service.JWTService;
import com.java_web.backend.Common.Service.LLMInitialSyllabusService;
import com.java_web.backend.Common.Service.LLMIntroductionAndTargetService;
import com.java_web.backend.Common.Service.LLMJsonToMarkdownService;

@SpringBootTest
@Import(com.java_web.backend.Common.Config.CorsConfig.class)
class BackendApplicationTests {

    @MockBean
    private JWTService jwtService;
    
    @MockBean
    private LLMInitialSyllabusService llmInitialSyllabusService;
    
    @MockBean
    private LLMIntroductionAndTargetService llmIntroductionAndTargetService;
    
    @MockBean
    private LLMJsonToMarkdownService llmJsonToMarkdownService;

    @Test
    void contextLoads() {
    }

}
