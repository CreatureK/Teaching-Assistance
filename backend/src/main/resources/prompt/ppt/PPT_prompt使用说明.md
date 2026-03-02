# PPT生成Prompt使用说明

## 文件结构

### 1. 总体指导文件
- **`prompt_for_ppt_generation.txt`** - PPT生成的总体指导原则和流程说明

### 2. 专门页面生成文件
- **`prompt_for_ppt_overview.txt`** - 概览页面生成
- **`prompt_for_ppt_basic_content.txt`** - 基础内容页面生成
- **`prompt_for_ppt_essential_content.txt`** - 必备内容页面生成
- **`prompt_for_ppt_advanced_content.txt`** - 进阶内容页面生成
- **`prompt_for_ppt_examples.txt`** - 例题和习题页面生成
- **`prompt_for_ppt_summary.txt`** - 总结页面生成

## 使用方法

### 第一步：选择页面类型
根据讲义内容的不同部分，选择合适的prompt文件：
- 课程介绍 → `prompt_for_ppt_overview.txt`
- 基础概念 → `prompt_for_ppt_basic_content.txt`
- 核心技能 → `prompt_for_ppt_essential_content.txt`
- 高级理论 → `prompt_for_ppt_advanced_content.txt`
- 练习巩固 → `prompt_for_ppt_examples.txt`
- 知识总结 → `prompt_for_ppt_summary.txt`


### 第二步：整合内容
将所有生成的页面内容整合成完整的PPT演示文稿。

## PPT页面结构要求

每个PPT页面必须包含以下6个要素：

1. **主标题（Headline）**
   - 1行，10-12字以内
   - 回答"这一页到底想证明什么"

2. **副标题或过渡句（Sub-headline）**
   - 1行
   - 补充背景或因果关系，让学生知道"为什么这很重要"

3. **核心要点（Bullet Points）**
   - 用并列结构
   - 避免二级bullet，保证一眼读完
   - 每页不超过4个要点

4. **关键数字/证据（Evidence）**
   - 把最具冲击力的1-2个关键词单独拎出来
   - 加括号或破折号跟在要点后面

5. **讲解提示（Speaker Note）**
   - 用PPT备注栏写给讲者自己看的2-3句话
   - 确保台上不临场忘词

6. **衔接语（Transition）**
   - 每页最后一行小字
   - 提示"下一页我们将看到……"，保持PPT连贯

## 输出格式

所有prompt文件都输出JSON格式，包含以下字段：
```json
{
  "slide_type": "页面类型",
  "headline": "主标题",
  "sub_headline": "副标题",
  "bullet_points": ["要点1", "要点2", "要点3", "要点4"],
  "evidence": ["证据1", "证据2"],
  "speaker_note": "讲解提示",
  "transition": "衔接语"
}
```
