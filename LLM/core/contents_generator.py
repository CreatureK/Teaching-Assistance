import json
import logging
from openai import OpenAI
from config.settings import ModelConfig
from core.text_read import TextReader
from core.text_write import TextWriter

# logging.basicConfig(filename = 'log_information/content_and_target/content_and_target_logging.log', # 指定输出日志的路径
#                     filemode = 'a', # 指定输出模式
#                     level = logging.INFO, # 指定最低输出的日志级别
#                     format = '%(asctime)s - %(levelname)s - %(message)s',  # 指定输出日志的格式
#                     datefmt='%Y-%m-%d %H:%M:%S' # 格式化时间戳
#                     )

class ContentsGenerator:

    def __init__(self,course_title:str):
        # 大模型参数初始化
        self.config = ModelConfig()
        self.config_dict = self.config.get_qwen_plus()
        self.model = self.config_dict["model_name"]
        self.url = self.config_dict["model_url"]
        self.api_key = self.config_dict["model_api_key"]
        self.client = None
        self.completion = None

        # 课程标题导入
        # print("读取课程标题...")
        # self.course_title_file_path = "input/pre_content/course_title.txt"
        # self.reader = TextReader(self.course_title_file_path)
        self.course_title = course_title
        logging.info(f"课程标题: {self.course_title}")

        # 课程目录模版读入
        self.contents_template_file_path = "templates/contents/contents.json"
        self.reader = TextReader(self.contents_template_file_path)
        self.contents_template = self.reader.read()
        logging.info(f"课程目录模版导入")

        # 课程目录检索
        self.contents_search = ""
        self.txt_contents_file_path = "output/contents/contents.txt"

        # 教学目录 prompt 读入
        self.prompt_file_path = "prompt/contents/prompt_for_contents.txt"
        self.reader = TextReader(self.prompt_file_path)
        self.prompt = self.reader.read()
        logging.info(f"课程目录 prompt 导入")

        # 待生成结果初始化
        self.answer = ""
        self.continue_answer = ""
        self.target_position = "output/contents/contents.json"
        self.writer = None


    def txt_contents_generator(self):
        # 输出调用大模型信息
        logging.info("正在调用大模型检索课程目录，请耐心等待...")

        try:
            self.client = OpenAI(
                api_key=self.api_key,
                base_url=self.url,
            )

            self.completion = self.client.chat.completions.create(
                model=self.model,
                messages=[
                    {'role': 'system', 'content': f'你是一位资深大学教授，尤其熟悉{self.course_title}学科的教学内容'},
                    {'role': 'system', 'content': f'你需要生成一份该学科的教学目录'},
                    {'role': 'user', 'content': f'请生成一份课程目录，内容要全面，课程名称:{self.course_title}'}
                ],
            )
            self.contents_search = self.completion.choices[0].message.content
            logging.info("大模型已经完成目录检索")

        except Exception as e:
            logging.error(f"{e}")

        self.writer = TextWriter(self.contents_search, self.txt_contents_file_path)
        self.writer.write()


    def json_contents_generator(self):
        # 输出调用大模型信息
        logging.info("正在调用大模型生成课程目录，请耐心等待...")

        try:
            self.client = OpenAI(
                api_key=self.api_key,
                base_url=self.url,
            )

            self.completion = self.client.chat.completions.create(
                model=self.model,
                messages=[
                    {'role': 'system', 'content': f'你是一位资深大学教授，尤其熟悉{self.course_title}学科的教学内容'},
                    {'role': 'system', 'content': f'你需要根据以下json模版，生成一份关于该门课程的目录内容:<template>{self.contents_template}</template>'},
                    {'role': 'system', 'content': f'{self.prompt}'},
                    {'role': 'user', 'content': f'课程名称:{self.course_title},课程目录内容如下:<txt_contents>{self.contents_search}</txt_contents>'},
                    {'role': 'user', 'content': f'请按照模版格式生成一份{self.course_title}课程的目录'}
                ],
            )
            self.answer = self.completion.choices[0].message.content
            logging.info("大模型已经完成课程目录的生成")

        except Exception as e:
            logging.error(f"{e}")

        # 输出目录内容
        self.writer = TextWriter(self.answer,self.target_position)
        self.writer.write()

        return self.answer