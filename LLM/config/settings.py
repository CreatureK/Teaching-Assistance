import os
from dotenv import load_dotenv

load_dotenv()

class ModelConfig:

    def __init__(self):
        self.qwen_plus_model = {
            "model_name": "qwen-plus",
            "url": "https://dashscope.aliyuncs.com/compatible-mode/v1",
            "model_api_key": os.getenv("QWEN_API_KEY")
        }

    def get_qwen_plus(self):
        return {
            "model_name": self.qwen_plus_model["model_name"],
            "model_url": self.qwen_plus_model["url"],
            "model_api_key": self.qwen_plus_model["model_api_key"]
        }