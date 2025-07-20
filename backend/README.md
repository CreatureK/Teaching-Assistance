# 后端目录结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── java_web/
│   │   │           └── backend/
│   │   │               ├── BackendApplication.java                      # 应用程序入口类
│   │   │               ├── Admin/                                       # 管理员模块
│   │   │               │   ├── Controller/                              # 管理员控制器
│   │   │               │   │   ├── AdminCourseController.java           # 课程审核管理接口
│   │   │               │   │   ├── AdminRestrictionController.java      # 用户功能限制接口
│   │   │               │   │   ├── AdminStatisticsController.java       # 数据统计接口
│   │   │               │   │   └── AdminUserController.java             # 用户管理接口
│   │   │               │   └── Service/                                 # 管理员服务层
│   │   │               │       ├── AdminCourseService.java              # 课程审核管理服务
│   │   │               │       ├── AdminRestrictionService.java         # 用户功能限制服务
│   │   │               │       ├── AdminStatisticsService.java          # 数据统计服务
│   │   │               │       └── AdminUserService.java                # 用户管理服务
│   │   │               ├── Common/                                      # 通用模块
│   │   │               │   ├── Config/                                  # 配置类
│   │   │               │   │   ├── CorsConfig.java                      # 跨域配置
│   │   │               │   │   ├── MyBatisPlusConfig.java               # MyBatisPlus配置
│   │   │               │   │   ├── OpenAIConfig.java                    # OpenAI接口配置
│   │   │               │   │   ├── OpenApiConfig.java                   # API文档配置
│   │   │               │   │   └── WebConfig.java                       # Web应用配置
│   │   │               │   ├── Controller/                              # 通用控制器
│   │   │               │   │   └── SignupController.java                # 用户注册控制器
│   │   │               │   ├── DTO/                                     # 数据传输对象
│   │   │               │   │   ├── AdminLoginDTO.java                   # 管理员登录请求
│   │   │               │   │   ├── InitialSyllabusRequestDTO.java       # 初始大纲生成请求
│   │   │               │   │   ├── IntroductionAndTargetRequestDTO.java # 课程介绍和目标生成请求
│   │   │               │   │   ├── IntroductionAndTargetResponseDTO.java# 课程介绍和目标生成响应
│   │   │               │   │   ├── JsonToMarkdownRequestDTO.java        # JSON转Markdown请求
│   │   │               │   │   ├── JsonToMarkdownResponseDTO.java       # JSON转Markdown响应
│   │   │               │   │   ├── LectureRequestDTO.java               # 讲义生成请求
│   │   │               │   │   ├── LoginDTO.java                        # 用户登录请求
│   │   │               │   │   ├── SyllabusRequestDTO.java              # 大纲生成请求
│   │   │               │   │   └── UpdateUserDTO.java                   # 用户信息更新请求
│   │   │               │   ├── Entity/                                  # 实体类
│   │   │               │   │   ├── Course.java                          # 课程实体
│   │   │               │   │   ├── CourseObjective.java                 # 课程目标实体
│   │   │               │   │   ├── InitialSyllabusRequest.java          # 初始大纲请求实体
│   │   │               │   │   ├── IntroductionAndTargetResponse.java   # 课程介绍和目标响应实体
│   │   │               │   │   ├── JsonToMarkdownRequest.java           # JSON转Markdown请求实体
│   │   │               │   │   ├── JsonToMarkdownResponse.java          # JSON转Markdown响应实体
│   │   │               │   │   ├── Material.java                        # 讲义实体
│   │   │               │   │   ├── Restriction.java                     # 功能限制实体
│   │   │               │   │   ├── Syllabus.java                        # 大纲实体
│   │   │               │   │   └── User.java                            # 用户实体
│   │   │               │   ├── Interceptor/                             # 拦截器
│   │   │               │   │   └── LoginInterceptor.java                # 登录拦截器
│   │   │               │   ├── Mapper/                                  # 数据访问层
│   │   │               │   │   ├── CourseMapper.java                    # 课程数据访问接口
│   │   │               │   │   ├── CourseObjectMapper.java              # 课程目标数据访问接口
│   │   │               │   │   ├── MaterialMapper.java                  # 讲义数据访问接口
│   │   │               │   │   ├── RestrictionMapper.java               # 功能限制数据访问接口
│   │   │               │   │   ├── SyllabusMapper.java                  # 大纲数据访问接口
│   │   │               │   │   └── UserMapper.java                      # 用户数据访问接口
│   │   │               │   ├── Service/                                 # 共用服务
│   │   │               │   │   ├── JWTService.java                      # JWT令牌服务
│   │   │               │   │   ├── LLMInitialSyllabusService.java       # 初始大纲AI生成服务
│   │   │               │   │   ├── LLMIntroductionAndTargetService.java # 课程介绍和目标AI生成服务
│   │   │               │   │   ├── LLMJsonToMarkdownService.java        # JSON转Markdown服务
│   │   │               │   │   ├── LLMLectureService.java               # 讲义AI生成服务
│   │   │               │   │   ├── LLMSyllabusService.java              # 大纲AI生成服务
│   │   │               │   │   └── SignupService.java                   # 用户注册服务
│   │   │               │   └── Utils/                                   # 工具类
│   │   │               │       └── HttpUtil.java                        # HTTP请求工具
│   │   │               └── Teacher/                                     # 教师模块
│   │   │                   ├── Controller/                              # 教师控制器
│   │   │                   │   ├── CourseController.java                # 课程管理接口
│   │   │                   │   ├── CourseObjectiveController.java       # 课程目标管理接口
│   │   │                   │   ├── MaterialController.java              # 讲义管理接口
│   │   │                   │   ├── SyllabusController.java              # 大纲管理接口
│   │   │                   │   └── UserController.java                  # 教师用户接口
│   │   │                   └── Service/                                 # 教师服务层
│   │   │                       ├── CourseObjectiveService.java          # 课程目标管理服务
│   │   │                       ├── CourseService.java                   # 课程管理服务
│   │   │                       ├── MaterialService.java                 # 讲义管理服务
│   │   │                       ├── SyllabusService.java                 # 大纲管理服务
│   │   │                       └── UserService.java                     # 教师用户服务
│   │   └── resources/
│   │       ├── application.properties                                   # 应用配置文件
│   │       ├── prompt/                                                  # AI提示词模板
│   │       │   ├── contents/                                            # 内容相关提示词
│   │       │   │   └── prompt_for_contents.txt                          # 内容生成提示词
│   │       │   ├── introduction_and_target/                             # 课程介绍和目标相关提示词
│   │       │   │   ├── prompt_for_detailed_teaching_content_and_target.txt  # 详细教学内容和目标提示词
│   │       │   │   └── prompt_for_introduction_and_target.txt           # 介绍和目标生成提示词
│   │       │   ├── json_to_markdown/                                    # JSON转Markdown相关提示词
│   │       │   │   └── json_to_markdown_prompt.txt                      # JSON转Markdown提示词
│   │       │   ├── lecture/                                             # 讲义相关提示词
│   │       │   │   ├── lecture_generate_tools.txt                       # 讲义生成工具提示词
│   │       │   │   └── prompt_for_lecture_generation.txt                # 讲义生成提示词
│   │       │   └── syllabus/                                            # 大纲相关提示词
│   │       │       ├── prompt_for_detailed_course_target.txt            # 详细课程目标提示词
│   │       │       ├── prompt_for_experimental_projects.txt             # 实验项目提示词
│   │       │       ├── prompt_for_final_generation.txt                  # 最终生成提示词
│   │       │       ├── prompt_for_initial_generation.txt                # 初始生成提示词
│   │       │       ├── prompt_for_teaching_content.txt                  # 教学内容提示词
│   │       │       └── prompt_for_textbooks_and_reference_books.txt     # 教材参考书籍提示词
│   │       ├── static/                                                  # 静态资源目录
│   │       └── templates/                                               # 模板目录
│   │           ├── contents/                                            # 内容模板
│   │           │   └── contents.json                                    # 内容JSON模板
│   │           ├── introduction_and_target/                             # 课程介绍和目标模板
│   │           │   ├── detailed_teaching_content_and_target_part.json   # 详细教学内容和目标部分模板
│   │           │   ├── introduction_and_target.json                     # 介绍和目标模板
│   │           │   └── teaching_content_and_target.json                 # 教学内容和目标模板
│   │           ├── lecture/                                             # 讲义模板
│   │           │   ├── know.txt                                         # 知识点模板
│   │           │   ├── knowledge_extract.json                           # 知识提取模板
│   │           │   ├── lecture_note.txt                                 # 讲义笔记模板
│   │           │   ├── prompt.txt                                       # 讲义提示模板
│   │           │   └── tools.txt                                        # 工具描述模板
│   │           └── syllabus/                                            # 大纲模板
│   │               ├── syllabus_key_content_part.json                   # 大纲关键内容部分模板
│   │               ├── syllabus_key_content.json                        # 大纲关键内容模板
│   │               └── syllabus.json                                    # 大纲模板
│   └── test/                                                            # 测试目录
│       └── java/
│           └── com/
│               └── java_web/
│                   └── backend/
│                       ├── BackendApplicationTests.java                 # 应用程序测试
│                       ├── IntroductionAndTargetTestSuite.java          # 课程介绍和目标测试套件
│                       ├── JsonToMarkdownDemoTest.java                  # JSON转Markdown演示测试
│                       ├── controller/                                  # 控制器测试
│                       │   ├── InitialSyllabusControllerTest.java       # 初始大纲控制器测试
│                       │   ├── JsonToMarkdownControllerTest.java        # JSON转Markdown控制器测试
│                       │   ├── LLMControllerTest.java                   # LLM控制器测试
│                       │   └── LLMLectureControllerTest.java            # LLM讲义控制器测试
│                       ├── entity/                                      # 实体测试
│                       │   ├── InitialSyllabusRequestTest.java          # 初始大纲请求实体测试
│                       │   ├── IntroductionAndTargetRequestTest.java    # 课程介绍和目标请求实体测试
│                       │   └── IntroductionAndTargetResponseTest.java   # 课程介绍和目标响应实体测试
│                       ├── integration/                                 # 集成测试
│                       │   ├── InitialSyllabusIntegrationTest.java      # 初始大纲集成测试
│                       │   └── IntroductionAndTargetIntegrationTest.java# 课程介绍和目标集成测试
│                       ├── service/                                     # 服务测试
│                       │   ├── LLMInitialSyllabusServiceTest.java       # 初始大纲服务测试
│                       │   ├── LLMIntroductionAndTargetServiceTest.java # 课程介绍和目标服务测试
│                       │   └── LLMJsonToMarkdownServiceTest.java        # JSON转Markdown服务测试
│                       └── utils/                                       # 工具测试
│                           ├── README_TestResultOutput.md               # 测试结果输出说明
│                           ├── TestResultOutputUtil.java                # 测试结果输出工具
│                           └── TestResultWriter.java                    # 测试结果写入器
```