from openai import OpenAI
from config.settings import ModelConfig
from core.text_read import TextReader
from pathlib import Path
import re
from concurrent.futures import ThreadPoolExecutor

# 获取项目根目录
current_dir = Path(__file__).parent
project_root = current_dir.parent

class LectureMaterialGenerator:
    def __init__(self, knowledge_hierarchy_data):
        # 初始化配置参数
        self.config = ModelConfig()
        self.config_dict = self.config.get_qwen_plus()
        self.model = self.config_dict["model_name"]
        self.url = self.config_dict["model_url"]
        self.api_key = self.config_dict["model_api_key"]
        self.client = None
        self.completion = None

        # 读取教学大纲
        self.syllabus_path = project_root / "output/syllabus/final_syllabus.json"
        self.syllabus = TextReader(self.syllabus_path).read()

        # 使用传入的知识点数据
        self.knowledge_hierarchy = self._parse_knowledge_hierarchy(knowledge_hierarchy_data)

        # 模板加载
        self.lecture_template = self._load_template(project_root / "templates/lecture/lecture_note.txt")
        self.tools_content = self._load_template(project_root / "templates/lecture/tools.txt")

        # 模板解析
        self.template_sections = self._parse_lecture_template()

        # 存储讲义
        self.lecture_content = ""

    def _parse_knowledge_hierarchy(self, data):
        hierarchy = {
            "course_title": data.get('course_title'),
            "unit_title": data.get('unit_title'),
            "knowledge_hour": data.get('knowledge_hour'),
            "knowledge_point": data.get('knowledge_point'),
            "request": data.get('request')
        }
        return hierarchy

    def _load_template(self, path):
        try:
            with open(str(path), 'r', encoding='utf-8') as f:
                return f.read()
        except FileNotFoundError:
            print(f"警告：模板文件 {path} 未找到")
            return ""

    def _parse_lecture_template(self):
        sections = [
            ('overview', 'overview'),
            ('core_content_basic', 'core_content_basic'),
            ('core_content_essential', 'core_content_essential'),
            ('core_content_advanced', 'core_content_advanced'),
            ('example_exercises_basic', 'example_exercises_basic'),
            ('example_exercises_essential', 'example_exercises_essential'),
            ('example_exercises_advanced', 'example_exercises_advanced')
        ]

        command_pattern = r"<command>(.*?)</command>"
        command_match = re.search(command_pattern, self.lecture_template, re.DOTALL)
        if command_match:
            self.command_instructions = command_match.group(1).strip()
            print(f"成功提取命令指令，长度：{len(self.command_instructions)}字符")
        else:
            self.command_instructions = ""
            print("警告：未找到命令指令部分")

        parsed = {}
        for start_tag, end_tag in sections:
            pattern = rf"<{start_tag}>(.*?)</{end_tag}>"
            match = re.search(pattern, self.lecture_template, re.DOTALL)
            if match:
                content = match.group(1).strip()
                parsed[start_tag] = '\n'.join(line.strip() for line in content.split('\n'))
            else:
                parsed[start_tag] = ""
                print(f"警告：未找到 {start_tag} 部分")

        return parsed

    def generate_lecture_materials(self):
        print("正在生成课程讲义，请稍候...")

        try:
            self.client = OpenAI(
                api_key=self.api_key,
                base_url=self.url,
            )

            self.lecture_content = ""
            knowledge_point = self.knowledge_hierarchy["knowledge_point"]
            print(f"正在生成知识点: {knowledge_point}")

            section_results = {}
            with ThreadPoolExecutor(max_workers=7) as executor:
                future_to_section = {
                    section_name: executor.submit(
                        self._generate_section_content,
                        section_name,
                        self.template_sections[section_name],
                        knowledge_point
                    )
                    for section_name in self.template_sections.keys()
                }

                for section_name, future in future_to_section.items():
                    try:
                        section_results[section_name] = future.result()
                    except Exception as e:
                        print(f"生成 {section_name} 部分失败: {str(e)}")
                        section_results[section_name] = f"# {section_name} 生成失败\n\n错误: {str(e)}"

            formatted_content = self._format_content(knowledge_point, section_results)
            self.lecture_content += formatted_content + "\n\n"

            print("讲义生成完成！")
            return self.lecture_content

        except Exception as e:
            print(f"严重错误导致进程终止：{str(e)}")
            raise

    def _format_content(self, knowledge_point, section_results):
        formatted = f"# {knowledge_point} 讲义\n\n"
        for section_name, content in section_results.items():
            formatted += f"## {section_name}\n\n{content}\n\n"
        return formatted

    def _generate_section_content(self, section_name, section_template, knowledge_point):
        try:
            prompt = f"""请根据以下要求生成讲义的 {section_name} 部分：

            {section_template}

            课程大纲：<syllabus>{self.syllabus}</syllabus>
            用户要求：<user_request>{self.user_request}</user_request>
            系统提示：<system_prompt>{self.prompt_template}</system_prompt>
            课程标题: <course_title>{self.knowledge_hierarchy['course_title']}</course_title>
            知识点学时: <knowledge_hour>{self.knowledge_hierarchy['knowledge_hour']}</knowledge_hour>
            单元上下文: <unit_context>{self.knowledge_hierarchy['unit_title']}</unit_context>
            知识点: <knowledge_point>{knowledge_point}</knowledge_point>
            其他用户请求: <request>{self.knowledge_hierarchy['request']}</request>

            重要要求：<command>
            {self.command_instructions}
            </command>

            请仅生成 {section_name} 部分的内容，不要包含其他部分。
            确保严格遵循上述指令中的时间分配、字数要求和格式规范。
            """

            response = self.client.chat.completions.create(
                model=self.model,
                messages=[
                    {"role": "system", "content": f"你是一名资深讲师，现在需要生成'{knowledge_point}'的{section_name}部分，请严格遵循指令要求"},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.5,
                max_tokens=8000
            )
            return response.choices[0].message.content
        except Exception as e:
            print(f"生成 {section_name} 部分时出错: {str(e)}")
            raise

    def _save_lecture(self):
        print(f"生成的讲义内容:\n{self.lecture_content}")

    def _generate_filename(self, knowledge_point):
        safe_name = knowledge_point.replace(' ', '_').replace('/', '-')
        return f"{safe_name}.md"
