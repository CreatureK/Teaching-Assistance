# Teaching - AI 辅助教学平台

面向教师的教学辅助系统，集成大模型能力，支持课程介绍、教学目标、教学大纲、讲义的 AI 生成与编辑。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3、TypeScript、Vite、Element Plus、Toast UI Editor、Axios、Vue Router、Katex、MathJax |
| 后端 | Spring Boot 3.5.3、Java 17、MyBatis-Plus、MySQL、Druid、JWT |
| AI | 阿里云 DashScope（通义千问 qwen-plus） |

## 功能介绍

### 教师端

- **课程管理**：创建课程、编辑课程名称、删除课程；课程以卡片形式展示，支持批量删除
- **课程信息**：填写课程标题、教学重点、学分学时；考核方式占比表（课堂表现、平时作业、期中测试、实验操作、闭卷/开卷考试、PPT 汇报），合计自动校验
- **课程介绍与目标**：课程介绍、教学目标两栏 Markdown 编辑；支持输入自定义 prompt 由 AI 生成，可多次生成并覆盖
- **课程大纲**：Markdown 编辑；AI 根据课程信息生成初版大纲，支持 prompt 微调
- **教学讲义**：Markdown 编辑，支持左侧目录导航、公式渲染（Katex/MathJax）；AI 按模块生成讲义内容，可指定章节或主题
- **模块进度**：课程介绍、课程大纲、教学讲义、教学课件四个模块有完成状态指示（空/进行中/已完成）

### 管理员端

- **数据统计**：总用户数、总课程数、教师数、管理员数；待审核/已审核课程数
- **用户管理**：用户列表、添加用户、删除用户、编辑用户名、编辑邮箱；支持批量删除
- **课程审核**：查看待审核课程列表，审批通过
- **功能限制**：按用户限制指定功能使用（后端提供 API）

### 认证与权限

- **登录**：教师与管理员分别登录，管理员登录后自动跳转管理后台
- **注册**：支持教师注册，用户名与邮箱唯一性校验
- **鉴权**：JWT 令牌，路由守卫区分 teacher/admin

### 其他

- **Markdown 编辑器**：Toast UI Editor，支持代码高亮、UML 图、公式
- **Prompt 模板**：`backend/src/main/resources/prompt/` 下可配置 AI 提示词，含 PPT 生成相关模板

## 项目结构

```
Teaching/
├── frontend/          # Vue 3 前端
│   ├── src/
│   │   ├── api/       # API 封装
│   │   ├── components/# 课程管理、大纲、讲义、Markdown 等组件
│   │   ├── views/    # 首页、登录、管理后台
│   │   └── router/   # 路由与鉴权
│   └── package.json
├── backend/           # Spring Boot 后端
│   ├── src/main/java/com/java_web/backend/
│   │   ├── Admin/    # 管理员：用户、课程审核、统计、功能限制
│   │   ├── Teacher/  # 教师：课程、目标、大纲、讲义、LLM 调用
│   │   └── Common/   # 注册、实体、配置、拦截器
│   └── src/main/resources/
│       ├── application.properties
│       └── prompt/   # AI 提示词模板（含 PPT 相关）
└── docs/             # 开发环境说明
```

## 快速启动

### 环境要求

- Node.js LTS、Yarn
- JDK 17、Maven 3.8+
- MySQL 8.0+

### 数据库

创建数据库 `backend`，执行建表脚本（见 `docs/开发环境问题.md` 中 SQL）。

### 后端

```bash
cd backend
# 修改 application.properties 中的数据库连接与 openai.api-key
mvn spring-boot:run
```

默认端口：8080

### 前端

```bash
cd frontend
yarn install
yarn dev
```

默认端口：5173，API 请求指向 `http://localhost:8080`

### 默认账号

- 管理员：admin / 123456
- 教师：teacher / 123456

## 配置说明

| 配置项 | 说明 |
|--------|------|
| `spring.datasource.*` | MySQL 连接 |
| `openai.api-url` | DashScope 兼容接口 |
| `openai.model-name` | 模型名（如 qwen-plus） |
| `openai.api-key` | API 密钥 |

## API 概览

| 模块 | 路径 | 说明 |
|------|------|------|
| 注册 | `/api/signup/register` | 用户注册 |
| 教师 | `/teacher/courses` | 课程列表 |
| 教师 | `/teacher/objective/{id}` | 课程目标 |
| 教师 | `/teacher/syllabus/{id}` | 教学大纲 |
| 教师 | `/teacher/material/{id}` | 讲义 |
| LLM | `/api/llm/introduction_and_target` | 生成介绍与目标 |
| LLM | `/api/llm/syllabus` | 生成大纲 |
| LLM | `/api/llm/lecture` | 生成讲义 |
| 管理 | `/admin/*` | 用户、课程审核、统计、限制 |

Swagger UI：`http://localhost:8080/swagger-ui/index.html`

## 文档

- 前后端开发环境与常见问题：`docs/开发环境问题.md`
- Prompt 使用说明：`backend/src/main/resources/prompt使用说明.md`
