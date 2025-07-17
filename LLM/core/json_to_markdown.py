import json
import pika
import logging
from openai import OpenAI
from config.settings import ModelConfig
from core.text_read import TextReader
from core.text_write import TextWriter
from pathlib import Path

# 配置日志
logging.basicConfig(
    filename='log_information/json_to_markdown/json_to_markdown_logging.log',
    filemode='a',
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
    encoding='gb2312'
)

class JsonToMarkdownGenerator:

    # 接收传参
    def __init__(self, json_content: str):
        # 大模型参数初始化
        self.config = ModelConfig()
        self.config_dict = self.config.get_qwen_plus()
        self.model = self.config_dict["model_name"]
        self.url = self.config_dict["model_url"]
        self.api_key = self.config_dict["model_api_key"]
        self.client = None
        self.completion = None

        self.json_content = json_content

        # json 转 markdown 内容的 Prompt 导入
        self.prompt_file_path = "prompt/json_to_markdown/json_to_markdown_prompt.txt"
        self.reader = TextReader(self.prompt_file_path)
        self.prompt = self.reader.read()
        logging.info(f"json 转 markdown 的 prompt 读取")

        self.answer = ""
        self.target_position = "output/json_to_markdown/json_to_markdown.md"
        self.writer = None

    def json_to_markdown(self) -> str:
        # 输出调用大模型提示
        logging.info("正在调用大模型将json内容转为markdown内容")

        try:
            self.client = OpenAI(
                api_key=self.api_key,
                base_url=self.url,
            )

            self.completion = self.client.chat.completions.create(
                model=self.model,
                messages=[
                    {'role': 'system', 'content': f'你是一位文本处理专家，擅长将 json 格式的文本内容转为 markdown 格式'},
                    {'role': 'system', 'content': f'你在转换的时候需要注意 markdown 的层级关系以及什么样的 json 结构适合转换为纯文本，什么样的 json 结构适合转换为表格'},
                    {'role': 'system', 'content': f'你只需要输出转换后的内容，不需要输出多余的提示词'},
                    {'role': 'system', 'content': f'{self.prompt}'},
                    {'role': 'user', 'content': f'请把以下json内容转为markdown内容:<json_content>{self.json_content}</json_content>'}
                ],
            )
            self.answer = self.completion.choices[0].message.content
            logging.info(f"大模型已经将json内容转为markdown")

        except Exception as e:
            logging.error(f"{e}")

        self.writer = TextWriter(self.answer,self.target_position)
        self.writer.write()

        return self.answer