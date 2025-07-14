# 程序流程:

## 一、课程预处理部分

### 用户输入:
- 课程标题 `input/pre_content/course_title.txt`
- 教学的重点内容 `input/pre_content/key_content.txt`
- 课程总学分 `input/pre_content/credit.txt`
- 课程总学时 `input/pre_content/credit_hours.txt`
- 考核方式占比 `input/pre_content/evaluation_mode.txt`

### 大模型输出:
- 无

## 二、教学内容及目标

### 1.生成课程内容

#### 用户输入:
- 教学内容及目标的制作要求 `input/content_and_target/request_for_content_and_target.txt`

#### 需要使用的部分:
- 课程标题 `input/pre_content/course_title.txt`
- 教学的重点内容 `input/pre_content/key_teaching_content.txt`
- 教学内容及其对应教学目标模版 `templates/content_and_target/teaching_content_and_target.json`
- 教学内容及目标的制作要求 `input/content_and_target/request_for_content_and_target.txt`
- 针对教学内容和教学目标的 prompt `prompt/content_and_target/prompt_for_teaching_content_and_target.txt`

#### 大模型输出:
- 教学内容及其对应的教学目标 `output/content_and_target/teaching_content_and_target.json`
- 教学内容 `output/content_and_target/teaching_content.json`

###### 注:教学内容与教学目标成对生成，即一个教学内容对应有一个教学目标

### 2.生成教学大纲

#### （1）初步生成

##### 用户输入:
- 教学大纲制作要求 `input/syllabus/request_for_syllabus.txt`

##### 需要使用的部分:
- 课程标题 `input/pre_content/course_title.txt`
- 教学内容 `input/pre_content/teaching_content.json`
- 教学的重点内容 `input/pre_content/key_teaching_content.txt`
- 课程总学时 `input/pre_content/credit_hours.txt`
- 教学大纲制作要求 `input/syllabus/request_for_syllabus.txt`
- 针对初版大纲生成的 prompt `promtp/syllabus/prompt_for_initial_generation.txt`

##### 大模型输出:
- 初版课程大纲 `output/syllabus/initial_syllabus.json`

###### 注:这里只生成一个初步大纲，用户可以根据自己的需求继续进行更改

#### （2）大纲定版:

##### 用户输入:
- 用户对教学大纲定版生成的要求 `input/syllabus/request_for_syllabus_regeneration.txt`

##### 需要使用的部分:
- 初版课程大纲 `initial_syllabus.json`
- 针对重新生成定版课程大纲的 prompt `prompt/syllabus/prompt_for_final_generatrion.txt`

##### 大模型生成:
- 定版课程大纲 `output/syllabus/final_syllabus.json`

###### 注：这里的课程大纲将作为后续所有内容生成的目录及指导，在后续部分中不可以再进行修改

### 讲义生成流程  

#### （1）生成对应章节的知识点  

##### 用户输入:  
- 无输入，内部处理  

##### 需要使用的部分:  
- 初步课程大纲 `output/syllabus/initial_syllabus.json`  

##### 调用:  
- `core/knowledge_extract.py` 提取知识点  

##### 大模型输出:  
- 对应章节的知识点 `output/knowledge_of_chapter/knowledge_point.json`  

###### 注: 需要先从课程大纲中提取章节的大标题，以指导知识点的生成。  

---  

#### （2）选择知识点  

##### 用户输入:  
- `output/knowledge_of_chapter/knowledge_point.json` 中罗列的知识点  

##### 需要使用的部分:  
- `output/knowledge_of_chapter/knowledge_point.json`  

##### 大模型输出:  
- 无需调用大模型  
- 用户筛选后的知识点存放至 `input/lecture/knowledge_candidate.json`  

---  

#### （3）根据知识点生成讲义  

##### 用户输入:  
- 讲义制作需求 `input/lecture/request_for_teaching_material.txt`  

##### 需要使用的部分:  
- `input/lecture/knowledge_candidate.json`（最终的知识点）  
- `input/lecture/request_for_teaching_material.txt`（用户额外要求）  
- `output/syllabus/final_syllabus.json`（最终课程大纲）  
- `templates/lecture/lecture_note.txt`（课程讲义模板）  
- `templates/lecture/tools.txt`（制作讲义可能用到的工具）  
- `prompt/lecture/prompt_for_lecture_generation.txt`（系统提供的 Prompt）  

##### 调用:  
- `core/lecture_generator.py` 生成讲义  

##### 大模型输出:  
- 生成的课程讲义存放至 `output/lectures` 文件夹。