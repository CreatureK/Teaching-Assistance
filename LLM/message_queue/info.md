# 消息队列传递
### 整体信息传递过程：
#### 1. 第一次信息传递（队列名称：course_content_queue → teaching_content_queue & teaching_target_queue）
- 输入（发送到 course_content_queue）：
```json
{
    "courseContent": [
        {
            "courseTitle": "<课程标题>",
            "keyTeachingContent": "<教学重点内容>",
            "credit": "<学分>",
            "creditHour": "<学时>",
            "request": "<制作要求>"
        }
    ]
}

```
- 处理： 大模型识别到 `courseContent`
- 1.调用 `contents_generator.py` 生成教学目录，用于指导后续内容的生成
- 2.调用 `content_and_target_generator.py` 生成教学内容和教学目标
- 输出（发送到 teaching_content_queue & teaching_target_queue）：
- 教学内容（teaching_content_queue）：
```json
{
    "teachingContent": [
        "<教学内容>"
    ]
}

```
- 教学目标（teaching_target_queue）：
```json
{
    "teachingTarget": [
        "<教学目标>"
    ]
}

```
---
#### 2. 第二次信息传递（队列名称：initial_syllabus_queue → generated_initial_syllabus_queue）
- 输入（发送到 initial_syllabus_queue）：
```json
{
    "initialSyllabus": [
        {
            "courseTitle": "<课程标题>",
            "teachingContent": "<教学内容>",
            "keyTeachingContent": "<教学重点内容>",
            "creditHours": "<学时>",
            "request": "<用户修改要求>"
        }
    ]
}

```
- 处理： 大模型识别到 "initialSyllabus"，调用 initial_syllabus_content_generator.py 生成 初版教学大纲。
- 输出（发送到 generated_initial_syllabus_queue）：
```json
{
    "initialSyllabus": [
        "<初版教学大纲>"
    ]
}

```
---
#### 3. 第三次信息传递（队列名称：final_syllabus_queue → generated_final_syllabus_queue & knowledge_extraction_queue）
- 输入（发送到 final_syllabus_queue）：
```json
{
    "finalSyllabus": [
        {
            "initialSyllabus": "<初版教学大纲>",
            "request": "<用户修改要求>"
        }
    ]
}

```
- 处理：
1. 大模型识别到 "finalSyllabus"，调用 final_syllabus_content_generator.py 生成 终版教学大纲。
2. 大模型根据 终版课程大纲 提取 知识点 并发送到消息队列。
- 输出（发送到 generated_final_syllabus_queue & knowledge_extraction_queue）：
- 终版教学大纲（generated_final_syllabus_queue）：
```json
{
    "finalSyllabus": [
        "<终版教学大纲>"
    ]
}

```
- 知识点（knowledge_extraction_queue）：
```json
{
    "knowledgePoint": [
        "<提取出的知识点>"
    ]
}

```
---
#### 4. 第四次信息传递（队列名称：knowledge_candidate_queue → lecture_markdown_queue）
- 输入（发送到 knowledge_candidate_queue）：
```json
{
    "knowledgeCandidate": [
        {
            "unitNumber": "<单元编号>",
            "content": "<单元内容>",
            "unitTimeAllocation": "<单元学时>",
            "subsections": [
                {
                    "content": "<子章节内容>",
                    "timeAllocation": "<子章节学时>",
                    "knowledgePoints": [
                        {
                            "knowledgePoint": "<知识点>",
                            "timeAllocation": "<知识点学时>"
                        }
                    ]
                }
            ]
        }
    ]
}

```
- 处理： 大模型识别到 "knowledgeCandidate"，调用 lecture_generator.py 生成 知识点讲义 并转换成 Markdown 格式。
- 输出（发送到 lecture_markdown_queue）：
```plaintext
# 课程讲义
……

```