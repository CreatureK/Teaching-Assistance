# edu_agent

## 核心内容生成部分

### 一、课程目录生成

###### 注：本部分不需要返回给后端，但是需要考虑能否存入知识库
###### 暂时考虑每次询问都独立生成，可控性较差

#### 需要的信息（可以从后端给出的消息队列中进行提取）

- 课程标题 `courseTitle`
- json 模版（本地）
- prompt（本地）

#### 功能

- 作为其他内容生成的指导

### 二、课程具体教学内容和教学目标的生成

#### 需要的信息（后端以消息队列格式发送消息）

```json
{
  "course_id": "1",
  "course_title": "课程标题",
  "key_teaching_content": "重点教学内容",
  "request": "用户的个性化制作需求"
}
```

### 三、课程初版教学大纲生成

###### 注：本部分分为两个内容：针对课程整体内容的教学内容及目标，针对课程具体内容的教学内容及目标

#### 需要的信息（后端以消息队列格式发送消息）

```json
{
  "course_id": "1",
  "course_title": "课程标题",
  "credit_hours": "学时",
  "key_teaching_content": "重点教学内容",
  "request": "用户的个性化制作需求"
}
```

#### 生成的内容

- 针对课程整体的教学内容及目标 `whole_teaching_content_and_target.json`
- 针对课程具体的教学内容及目标 `detailed_teaching_content_and_target.json`