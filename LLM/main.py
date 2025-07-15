import os
import json
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
from core.initial_syllabus_content_generator import InitialSyllabusContentGenerator
from core.final_syllabus_content_generator import FinalSyllabusContentGenerator
from core.contents_generator import ContentsGenerator
from core.introduction_and_target_generator import IntroductionAndTargetGenerator


##################################
# 主程序入口
##################################
def test_introduction_and_target():
    # 示例参数
    course_id = "1"
    course_title = "高等数学"
    request = "请结合工程实际，突出应用能力培养"

    generator = IntroductionAndTargetGenerator(course_id, course_title, request)
    result = generator.introduction_and_target_integration()
    print("生成结果：")
    print(result)

if __name__ == "__main__":
    try:
        print("程序开始运行")

        # 课程介绍和教学目标生成
        test_introduction_and_target()

        # 教学大纲生成

        print("程序正常结束")

    except Exception as e:
        print("程序在运行时发生错误")
        print(f"错误信息:{e}")