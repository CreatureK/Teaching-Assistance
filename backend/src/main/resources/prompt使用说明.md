# 后端Prompt和Templates使用说明

## 1. Prompt和Templates文件夹的使用场景说明

### **第一阶段：生成课程教学内容和目标**
- **使用的Prompt文件**：`prompt/introduction_and_target/prompt_for_introduction_and_target.txt`
- **使用的Template文件**：`templates/introduction_and_target/introduction_and_target.json`
- **生成内容**：课程介绍和教学目标
- **输出格式**：JSON格式，包含`course_introduction`和`teaching_target`两个字段

### **第二阶段：生成课程教学大纲内容**
- **使用的Prompt文件**：`prompt/syllabus/`下的多个专门prompt文件
- **使用的Template文件**：`templates/syllabus/syllabus.json`
- **生成内容**：完整的课程教学大纲，包括：
  - 课程基本信息（中英文名称、学分、学时等）
  - 详细课程目标
  - 教学内容（按单元划分）
  - 实验项目
  - 教材和参考书目

### **第三阶段：生成课程讲义内容**
- **使用的Prompt文件**：`prompt/lecture/prompt_for_lecture_generation.txt`
- **使用的Template文件**：`templates/lecture/`下的多个模板文件
- **生成内容**：每个教学单元的详细讲义内容

### **第四阶段：JSON转Markdown**
- **使用的Prompt文件**：`prompt/json_to_markdown/json_to_markdown_prompt.txt`
- **功能**：将前面生成的JSON内容转换为Markdown格式发送给前端

## 2. 具体使用流程

### **第一步：生成课程教学内容和目标**
```java
// 使用 LLMIntroductionAndTargetService
String prompt = PromptUtil.readPrompt("prompt/introduction_and_target/prompt_for_introduction_and_target.txt");
String template = PromptUtil.readPrompt("templates/introduction_and_target/introduction_and_target.json");
```
**生成内容**：课程介绍和教学目标的JSON数据

### **第二步：生成课程教学大纲内容**
```java
// 使用 LLMInitialSyllabusService，并行调用多个专门的prompt
String promptTemplate = loadPromptFromFile("prompt/syllabus/prompt_for_detailed_course_target.txt");
String promptTemplate = loadPromptFromFile("prompt/syllabus/prompt_for_teaching_content.txt");
String promptTemplate = loadPromptFromFile("prompt/syllabus/prompt_for_experimental_projects.txt");
String promptTemplate = loadPromptFromFile("prompt/syllabus/prompt_for_textbooks_and_reference_books.txt");
```
**生成内容**：完整的教学大纲JSON数据，包含所有必要部分

### **第三步：生成课程讲义内容**
```java
// 使用 LLMLectureService
// 基于第二步生成的教学内容，为每个单元生成详细讲义
```
**生成内容**：每个教学单元的详细讲义JSON数据

### **第四步：JSON转Markdown**
```java
// 使用 LLMJsonToMarkdownService
String promptTemplate = loadPromptFromFile("prompt/json_to_markdown/json_to_markdown_prompt.txt");
```
**生成内容**：将前面所有JSON内容转换为Markdown格式

## 3. 详细的Prompt文件

### **Syllabus模块的多个Prompt文件**：
1. **`prompt_for_initial_generation.txt`**：初始大纲生成的总体指导
2. **`prompt_for_detailed_course_target.txt`**：生成详细课程目标
3. **`prompt_for_teaching_content.txt`**：生成教学内容（按单元划分）
4. **`prompt_for_experimental_projects.txt`**：生成实验项目
5. **`prompt_for_textbooks_and_reference_books.txt`**：生成教材和参考书目

### **Introduction_and_target模块**：
1. **`prompt_for_introduction_and_target.txt`**：生成课程介绍和教学目标


### **Lecture模块**：
1. **`prompt_for_lecture_generation.txt`**：生成讲义内容的主要指导
2. **`lecture_generate_tools.txt`**：讲义生成的工具和格式说明

### **JSON转Markdown模块**：
1. **`json_to_markdown_prompt.txt`**：将JSON转换为Markdown的指导

