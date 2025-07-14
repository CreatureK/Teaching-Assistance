import os
import json
from core.initial_syllabus_content_generator import InitialSyllabusContentGenerator
from core.final_syllabus_content_generator import FinalSyllabusContentGenerator
from core.contents_generator import ContentsGenerator


##################################
# 主程序入口
##################################
if __name__=="__main__":
    try:
        print("程序开始运行")

        # 课程介绍和教学目标生成

        # 教学大纲生成

        print("程序正常结束")

    except Exception as e:
        print("程序在运行时发生错误")
        print(f"错误信息:{e}")