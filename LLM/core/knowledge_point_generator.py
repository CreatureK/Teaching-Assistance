import json
import logging
from openai import OpenAI, OpenAIError
from config.settings import ModelConfig
from core.text_read import TextReader
from pathlib import Path
from datetime import datetime

current_dir = Path(__file__).parent
project_root = current_dir.parent

# 设置日志目录
log_dir = Path(project_root / "log_information/knowledge_points")
log_dir.mkdir(parents=True, exist_ok=True)
log_filename = log_dir / f"knowledge_points_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log"
logging.basicConfig(filename=log_filename,
                    filemode='a',
                    level=logging.INFO,
                    format='%(asctime)s - %(levelname)s - %(message)s',
                    datefmt='%Y-%m-%d %H:%M:%S',
                    encoding='utf-8')


class KnowledgePointGenerator:
    def __init__(self, course_data: dict) -> None:
        try:
            self.config = ModelConfig()
            self.config_dict = self.config.get_qwen_plus()
            self.model = self.config_dict["model_name"]
            self.url = self.config_dict["model_url"]
            self.api_key = self.config_dict["model_api_key"]
            self.client = None
            self.completion = None

            self.course_id = course_data["course_id"]
            logging.info(f"课程序号：{self.course_id}")

            self.course_title = course_data["course_title"]
            logging.info(f"课程标题：{self.course_title}")

            self.teaching_language = course_data["teaching_language"]
            logging.info(f"授课语言：{self.teaching_language}")

            self.credits = course_data["credits"]
            logging.info(f"课程学分：{self.credits}")

            self.course_hour = course_data["course_hour"]
            logging.info(f"课程学时：{self.course_hour}")

            self.syllabus = course_data["syllabus"]
            logging.info("课程大纲导入成功")

            self.request = course_data["request"]
            logging.info("用户需求导入成功")

            self.knowledge_points_template_path = Path(project_root/"templates/lecture/know.txt")
            self.reader_knowledge = TextReader(self.knowledge_points_template_path)
            self.knowledge_point_template = self.reader_knowledge.read()
            logging.info("知识点模板导入成功")

            self.prompt_path = Path(project_root/"templates/lecture/prompt.txt")
            self.reader_prompt = TextReader(self.prompt_path)
            self.prompt = self.reader_prompt.read()
            logging.info("Prompt导入成功")

        except KeyError as e:
            logging.error(f"初始化失败，缺少关键字段：{e}")
            raise
        except Exception as e:
            logging.error(f"初始化过程中发生错误：{e}")
            raise

    def initial_knowledge_points_generator(self) -> str:
        try:
            logging.info("开始调用大模型生成知识点，请耐心等待...")

            self.client = OpenAI(
                api_key=self.api_key,
                base_url=self.url,
            )

            self.completion = self.client.chat.completions.create(
                model=self.model,
                messages=[
                    {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及学科知识点的提取，你需要按照模版进行知识点的制作'},
                    {'role': 'system', 'content': f'你必须生成json格式的内容'},
                    {'role': 'system', 'content': f'以下是一份json模版内容，在制作课程大纲时候，你需要根据该json模版进行生成:<template>{self.knowledge_point_template}</template>'},
                    {'role': 'system', 'content': f'{self.prompt}'},
                    {'role': 'user', 'content': f'你需要生成一份教学大纲，课程标题为:{self.course_title},请结合相关的知识库内容进行制作'},
                    {'role': 'user', 'content': f'该门课的总学时为{self.course_hour}'},
                    {'role': 'user', 'content': f'你需要根据这份课程大纲{self.syllabus}去生成知识点'},
                    {'role': 'user', 'content': f'在制作的过程中，用户的制作要求是:<request>{self.request}</request>'}
                ]
            )

            result = self.completion.choices[0].message.content
            logging.info("知识点生成完成，正在保存文件...")

            output_path = Path(project_root/"output/knowledge_of_chapter/knowledge_point.json")
            output_path.parent.mkdir(parents=True, exist_ok=True)

            with open(output_path, "w", encoding="utf-8") as f:
                f.write(result)

            logging.info(f"知识点已成功保存到: {output_path}")
            return result

        except OpenAIError as e:
            logging.error(f"调用OpenAI API失败：{e}")
            raise
        except IOError as e:
            logging.error(f"文件写入失败：{e}")
            raise
        except Exception as e:
            logging.error(f"生成知识点过程中发生未知错误：{e}")
            raise
