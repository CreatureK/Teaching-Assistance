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
│   │   │               │   ├── DTO/                                     # 数据传输对象
│   │   │               │   │   ├── AdminLoginDTO.java                   # 管理员登录请求
│   │   │               │   │   ├── IntroductionAndTargetRequest.java    # 课程介绍和目标生成请求
│   │   │               │   │   ├── LectureRequest.java                  # 讲义生成请求
│   │   │               │   │   └── SyllabusRequest.java                 # 大纲生成请求
│   │   │               │   ├── Entity/                                  # 实体类
│   │   │               │   │   ├── Course.java                          # 课程实体
│   │   │               │   │   ├── CourseObjective.java                 # 课程目标实体
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
│   │   │               │   │   ├── LLMIntroductionAndTargetService.java # 课程介绍和目标AI生成服务
│   │   │               │   │   ├── LLMLectureService.java               # 讲义AI生成服务
│   │   │               │   │   └── LLMSyllabusService.java              # 大纲AI生成服务
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
│   └── test/                                                            # 测试目录
│       └── java/
│           └── com/
│               └── java_web/
│                   └── backend/
│                       ├── BackendApplicationTests.java                 # 应用程序测试
│                       └── controller/                                  # 控制器测试
│                           ├── LLMControllerTest.java                   # LLM控制器测试
│                           └── LLMLectureControllerTest.java            # LLM讲义控制器测试
```