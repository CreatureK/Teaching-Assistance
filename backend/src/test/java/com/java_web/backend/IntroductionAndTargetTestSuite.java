package com.java_web.backend;

import com.java_web.backend.controller.LLMControllerTest;
import com.java_web.backend.entity.IntroductionAndTargetRequestTest;
import com.java_web.backend.entity.IntroductionAndTargetResponseTest;
import com.java_web.backend.integration.IntroductionAndTargetIntegrationTest;
import com.java_web.backend.service.LLMIntroductionAndTargetServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Introduction_and_target 功能测试套件
 * 包含所有相关的单元测试和集成测试
 */
@Suite
@SuiteDisplayName("Introduction_and_target 测试套件")
@SelectClasses({
    // 实体类测试
    IntroductionAndTargetRequestTest.class,
    IntroductionAndTargetResponseTest.class,
    // Service层测试
    LLMIntroductionAndTargetServiceTest.class,
    // Controller层测试
    LLMControllerTest.class,
    // 集成测试
    IntroductionAndTargetIntegrationTest.class
})
public class IntroductionAndTargetTestSuite {
    // 测试套件配置
    // 所有测试类将通过@SelectClasses注解自动包含
} 