import json
import pika
import logging
from openai import OpenAI
from config.settings import ModelConfig
from core.text_read import TextReader
from core.text_write import TextWriter

logging.basicConfig(filename = 'log_information/introduction_and_target/introduction_and_target_logging.log', # 指定输出日志的路径
                    filemode = 'a', # 指定输出模式
                    level = logging.INFO, # 指定最低输出的日志级别
                    format = '%(asctime)s - %(levelname)s - %(message)s',  # 指定输出日志的格式
                    datefmt='%Y-%m-%d %H:%M:%S' # 格式化时间戳
                    )

class IntroductionAndTargetGenerator:

    # 接受传参
    def __init__(self, course_id:str, course_title:str,request:str):
        # 大模型参数初始化
        self.config = ModelConfig()
        self.config_dict = self.config.get_qwen_plus()
        self.model = self.config_dict["model_name"]
        self.url = self.config_dict["model_url"]
        self.api_key = self.config_dict["model_api_key"]
        self.client = None
        self.completion = None

        # 课程序号导入
        self.course_id = course_id
        logging.info(f"课程序号:{self.course_id}")

        # 课程标题导入
        self.course_title = course_title
        logging.info(f"课程标题:{self.course_title}")

        # 用户个性化制作需求导入
        self.request = request
        logging.info(f"用户个性化制作需求:{request}")

        # # 课程目录读入
        # self.contents_file_path = "output/contents/contents.json"
        # self.reader = TextReader(self.contents_file_path)
        # self.contents = self.reader.read()
        # self.contents = json.loads(self.contents)
        # self.Contents = {}
        # for chapter in self.contents["chapters"]:
        #     self.unitName = chapter["unitName"]
        #     self.subNames = [sub["subName"] for sub in chapter["subChapters"]]
        #     self.sub_names_str = ", ".join(self.subNames)  # 将 subNames 用逗号隔开
        #     self.Contents[self.unitName] = self.sub_names_str
        # logging.info(f"课程目录导入")

        # # 具体教学内容及其对应教学目标生成的 prompt 导入
        # self.detailed_prompt_file_path = "prompt/content_and_target/prompt_for_detailed_teaching_content_and_target.txt"
        # self.reader = TextReader(self.detailed_prompt_file_path)
        # self.detailed_prompt = self.reader.read()
        # logging.info(f"课程具体教学内容及其对应教学目标 prompt 导入")

        # 教学内容及其对应教学目标生成的 prompt 导入
        self.whole_prompt_file_path = "prompt/introduction_and_target/prompt_for_introduction_and_target.txt"
        self.reader = TextReader(self.whole_prompt_file_path)
        self.prompt = self.reader.read()
        logging.info(f"课程整体教学内容及其对应教学目标 prompt 导入")

        # 总体教学内容及其对应教学目标模版的导入
        self.content_and_target_template_file_path = "templates/introduction_and_target/introduction_and_target.json"
        self.reader = TextReader(self.content_and_target_template_file_path)
        self.content_and_target_template = self.reader.read()
        logging.info(f"课程整体教学内容及其对应教学目标模版导入")

        # # 具体教学内容及其教学目标模版的导入
        # self.detailed_teaching_content_and_target_template_file_path = "templates/content_and_target/detailed_teaching_content_and_target_part.json"
        # self.reader = TextReader(self.detailed_teaching_content_and_target_template_file_path)
        # self.detailed_teaching_content_and_target_template = self.reader.read()
        # logging.info(f"课程具体教学内容及其对应教学目标模版导入")

        # # 待生成结果初始化
        self.answer = ""
        self.response = {}
        # self.text = ('{'
        #              f'"course_id": "{self.course_id}",'
        #              '"chu":[{'
        #              f'"course_title": "{self.course_title}",'
        #              '"chapter": ['
        #              '')
        self.target_position = "output/introduction_and_target/introduction_and_target.json"
        self.writer = None
        self.count = 1

    def introduction_and_target_generator(self) -> str:
        # 输出调用大模型提示
        logging.info("正在调用大模型生成课程的总体教学内容及其教学目标...")

        try:
            self.client = OpenAI(
                api_key=self.api_key,
                base_url=self.url,
            )

            self.completion = self.client.chat.completions.create(
                model=self.model,
                messages=[
                    {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及教学内容和教学目标的制定，你需要按照json模版进行教学内容及其对应目标的制作'},
                    # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
                    {'role': 'system', 'content': f'在生成教学内容及其对应目标的时候，你需要参考该json模版示例进行生成:<template>{self.content_and_target_template}</template>'},
                    {'role': 'system', 'content': f'你所制作的教学内容需要参考用户指定的重点教学内容，但不应该只有用户指定的部分，而是应该在全部内容涵盖到的基础上，强调用户指定的重点教学内容'},
                    {'role': 'system', 'content': f'{self.prompt}'},
                    {'role': 'user', 'content': f'你需要生成一份总体的教学内容及其教学目标，课程标题为<course_title>{self.course_title}</course_title>, 请结合相关的知识库内容进行制作'},
                    {'role': 'user', 'content': f'在制作的过程中，用户的制作需求是:<request>{self.request}</request>'}
                ],
            )
            self.answer = self.completion.choices[0].message.content
            logging.info(f"大模型已经完成课程总体教学内容及其对应教学目标的生成")

        except Exception as e:
            logging.error(f"{e}")

        return self.answer

    # def detailed_content_and_target_generator(self):
    #     # 输出调用大模型提示
    #     logging.info("正在调用大模型生成具体教学内容及其对应目标，请耐心等待...")
    #
    #     # 输出结果
    #     for unitName, subName in self.Contents.items():
    #         try:
    #             self.client = OpenAI(
    #                 api_key=self.api_key,
    #                 base_url=self.url,
    #             )
    #
    #             self.completion = self.client.chat.completions.create(
    #                 model=self.model,
    #                 messages=[
    #                     {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及教学内容和教学目标的制定，你需要按照json模版进行教学内容及其对应目标的制作'},
    #                     # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
    #                     {'role': 'system', 'content': f'在生成教学内容及其对应目标的时候，你需要参考该json模版示例进行生成:<template>{self.detailed_teaching_content_and_target_template}</template>'},
    #                     {'role': 'system', 'content': f'{self.detailed_prompt}'},
    #                     {'role': 'user', 'content': f'你需要生成一份教学内容及其教学目标，课程标题为<course_title>{self.course_title}</course_title>, 请结合相关的知识库内容进行制作'},
    #                     {'role': 'user', 'content': f'章节大标题是{unitName},其下的小标题为{subName}，当前的unitNumber值为{self.count}'},
    #                     {'role': 'user', 'content': f'在制作的过程中，我的制作要求是:<request>{self.request}</request>'}
    #                 ],
    #             )
    #             self.answer = self.completion.choices[0].message.content
    #             logging.info(f"大模型已经完成具体教学内容及其对应教学目标的生成 -> part{self.count}")
    #
    #         except Exception as e:
    #             logging.error(f"{e}")
    #
    #         if self.count >1 :
    #             self.text = self.text + ',' + self.answer
    #         else:
    #             self.text = self.text + self.answer
    #
    #         self.count += 1
    #
    #     self.text += (']}]}')
    #
    #     # 输出教学内容及其对应教学目标
    #     self.writer = TextWriter(self.text, self.target_position2)
    #     self.writer.write()
    #
    #     return self.text

    def introduction_and_target_integration(self) -> dict :
        answer = json.loads(self.introduction_and_target_generator())
        logging.info("正在处理 content_and_target,即将返回最终结果...")
        self.response["course_id"] = self.course_id
        self.response["course_introduction"] = answer["course_introduction"]
        self.response["teaching_target"] = answer["teaching_target"]

        logging.info(f"处理完成，最终结果为:")
        logging.info(json.dumps(self.response, indent=4, ensure_ascii=False))

        self.writer = TextWriter(json.dumps(self.response,indent =2,ensure_ascii=False),self.target_position)
        self.writer.write()

        return self.response