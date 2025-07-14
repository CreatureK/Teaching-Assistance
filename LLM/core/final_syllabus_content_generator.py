import json
from openai import OpenAI
from config.settings import ModelConfig
from core.text_read import TextReader
from core.text_write import TextWriter

class FinalSyllabusContentGenerator:

    def __init__(self):
        # 大模型参数初始化
        self.config = ModelConfig()
        self.config_dict = self.config.get_qwen_plus()
        self.model = self.config_dict["model_name"]
        self.url = self.config_dict["model_url"]
        self.api_key = self.config_dict["model_api_key"]
        self.client = None
        self.completion = None

        # 课程标题导入
        self.course_title_file_path = "input/pre_content/course_title.txt"
        self.reader = TextReader(self.course_title_file_path)
        self.course_title = self.reader.read()

        # 用户给定的学时导入
        self.credit_hours_file_path = "input/pre_content/credit_hours.txt"
        self.reader = TextReader(self.credit_hours_file_path)
        self.credit_hours = self.reader.read()

        # 教学内容导入
        self.teaching_content_file_path = "output/content_and_target/teaching_content.json"
        self.reader = TextReader(self.teaching_content_file_path)
        self.teaching_content = self.reader.read()

        # 教学重点内容导入
        self.key_teaching_content_file_path = "input/pre_content/key_content.txt"
        self.reader = TextReader(self.key_teaching_content_file_path)
        self.key_teaching_content = self.reader.read()

        # 大纲模版导入
        self.syllabus_template_file_path = "templates/syllabus/syllabus_key_content.json"
        self.reader = TextReader(self.syllabus_template_file_path)
        self.syllabus_template = self.reader.read()

        # 大纲定版生成的 prompt 导入
        self.prompt_file_path = "prompt/syllabus/prompt_for_final_generation.txt"
        self.reader = TextReader(self.prompt_file_path)
        self.prompt = self.reader.read()

        # 用户提出的定版制作需求导入
        self.request_file_path = "input/syllabus/request_for_syllabus_regenerate.txt"
        self.reader = TextReader(self.request_file_path)
        self.request = self.reader.read()

        # 待生成结果初始化
        self.answer = None
        self.continue_content = None
        self.target_position = "output/syllabus/final_syllabus.json"
        self.writer = None


    def final_syllabus_content_generator(self):
        # 输出调用大模型提示
        print("正在调用大模型生成定版大纲，请耐心等待...")

        try:
            self.client = OpenAI(
                api_key=self.api_key,
                base_url=self.url,
            )

            self.completion = self.client.chat.completions.create(
                model=self.model,
                messages=[
                    {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及教学大纲的优化，你需要按照模版进行大纲的优化制作'},
                    # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
                    {'role': 'system', 'content': f'用户给出的待优化大纲可能不一定严格按照模版样式给出，但是在优化并生成时，你需要根据该json模版进行生成:<this_is_a_template_for_syllabus_generation>{self.syllabus_template}</this_is_a_template_for_syllabus_generation>'},
                    {'role': 'system', 'content': f'<this_is_the_prompt_for_initial_syllabus_generation>{self.prompt}</this_is_the_prompt_for_initial_syllabus_generation>'},
                    {'role': 'system', 'content': f'你的回答应当是json格式的内容，并且不要有任何多余的输出'},
                    {'role': 'user', 'content': f'我有一份初版的json教学大纲，课程标题为<this_is_the_course_title>{self.course_title}</this_is_the_course_title>,请结合相关的知识库内容对该版大纲进行优化和确定'},
                    {'role': 'user', 'content': f'该门课的总学时为<this_is_the_credit_hours>{self.credit_hours}</this_is_the_credit_hours>'},
                    {'role': 'user', 'content': f'本课程的教学内容是<this_is_the_teaching_content>{self.teaching_content}</this_is_the_teaching_content>'},
                    {'role': 'user', 'content': f'在以上教学内容中，作为教学重点的是:<this_is_the_key_teaching_content>{self.key_teaching_content}</this_is_the_key_teaching_content>,在大纲制作过程中,这些重点的教学内容应当占据更多的学时'},
                    {'role': 'user', 'content': f'在制作的过程中，我的制作要求是:<this_is_the_request_offered_by_the_user>{self.request}</this_is_the_request_offered_by_the_user>'}
                ],
            )
            self.answer = self.completion.choices[0].message.content
            print("大模型已经完成教学大纲的定版生成")

        except Exception as e:
            print(f"错误信息：{e}")

        # 输出初版大纲
        self.writer = TextWriter(self.answer,self.target_position)
        self.writer.write()

    def final_syllabus_partial_continuation_generator(self):
        # 输出调用大模型提示
        print("正在调用大模型进行定版大纲补充生成，请耐心等待...")

        try:
            self.client = OpenAI(
                api_key=self.api_key,
                base_url=self.url,
            )

            self.completion = self.client.chat.completions.create(
                model=self.model,
                messages=[
                    {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及教学大纲的优化，你需要按照模版进行大纲的优化制作'},
                    # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
                    {'role': 'system', 'content': f'用户给出的待优化大纲可能不一定严格按照模版样式给出，但是在优化并生成时，你需要根据该json模版进行生成:<this_is_a_template_for_syllabus_generation>{self.syllabus_template}</this_is_a_template_for_syllabus_generation>'},
                    {'role': 'system', 'content': f'<this_is_the_prompt_for_initial_syllabus_generation>{self.prompt}</this_is_the_prompt_for_initial_syllabus_generation>'},
                    {'role': 'user', 'content': f'我有一份初版的json教学大纲，课程标题为<this_is_the_course_title>{self.course_title}</this_is_the_course_title>,请结合相关的知识库内容对该版大纲进行优化和确定'},
                    {'role': 'user', 'content': f'该门课的总学时为<this_is_the_credit_hours>{self.credit_hours}</this_is_the_credit_hours>'},
                    {'role': 'user', 'content': f'本课程的教学内容是<this_is_the_teaching_content>{self.teaching_content}</this_is_the_teaching_content>'},
                    {'role': 'user', 'content': f'在以上教学内容中，作为教学重点的是:<this_is_the_key_teaching_content>{self.key_teaching_content}</this_is_the_key_teaching_content>,在大纲制作过程中,这些重点的教学内容应当占据更多的学时'},
                    {'role': 'user', 'content': f'在制作的过程中，我的制作要求是:<this_is_the_request_offered_by_the_user>{self.request}</this_is_the_request_offered_by_the_user>'},
                    {'role': 'user', 'content': f'请根据以上要求对{self.answer}进行续写，用以制成完整的大纲'},
                    {'role': 'assistant', 'content': f'{self.answer}',"partial":True }
                ],
            )
            self.continue_content = self.completion.choices[0].message.content
            print("大模型已经完成定版教学大纲的续写生成")

        except Exception as e:
            print(f"错误信息：{e}")

        self.answer = self.answer+self.continue_content

        # 输出定版大纲
        self.writer = TextWriter(self.answer, self.target_position)
        self.writer.write()

    def json_integrity_check(self)  -> bool :
        # 检测生成内容是否为完整的json格式内容
        print("正在检测生成内容是否为完整的json格式内容")

        try:
            json.loads(self.answer)  # 尝试解析 JSON 字符串
            print("大纲内容已经完整，无需进行续写生成")
            return True

        except json.JSONDecodeError:
            print("大纲内容不完整，需要进行续写生成")
            return False