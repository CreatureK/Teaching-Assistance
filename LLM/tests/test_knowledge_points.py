import unittest
import os
import sys

from pathlib import Path
current_dir = Path(__file__).parent
project_root = current_dir.parent
sys.path.append(str(project_root))
from core.knowledge_point_generator import KnowledgePointGenerator

class TestKnowledgePointGenerator(unittest.TestCase):

    def setUp(self):
        # 模拟用户输入数据
        self.course_data = {
            "course_id": "CS101",
            "course_title": "离散数学",
            "teaching_language": "中文",
            "credits": "4",
            "course_hour": "64",
            "syllabus": "大纲内容暂时省略",
            "request": "请根据本大纲生成适用于本科生的教学知识点模板。"
        }

    def test_generate_knowledge_points(self):
        """测试知识点生成并保存json文件是否成功"""
        generator = KnowledgePointGenerator(self.course_data)
        result_file = generator.initial_knowledge_points_generator()

        # 断言输出文件是否存在
        self.assertTrue(Path(result_file).exists(), f"文件未找到: {result_file}")

        # 简单检查文件内容是否非空
        with open(result_file, 'r', encoding='utf-8') as f:
            content = f.read().strip()
            self.assertTrue(len(content) > 0, "生成的知识点文件内容为空")

        print(f"测试通过，文件已生成：{result_file}")

if __name__ == '__main__':
    unittest.main()
