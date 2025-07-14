import unittest
import sys
# from core.lecture_generator import LectureMaterialGenerator

from pathlib import Path
current_dir = Path(__file__).parent
project_root = current_dir.parent
sys.path.append(str(project_root))
from core.lecture_generator import LectureMaterialGenerator

class TestLectureMaterialGenerator(unittest.TestCase):

    def setUp(self):
        # 准备测试数据
        self.test_data = {
            "course_id": 0,
            "course_title": "数字电路",
            "teaching_language": "中文",
            "knowledge_hour": "1",
            "unit_title": "时序逻辑",
            "knowledge_point": "触发器",
            "request": "无"
        }

        self.generator = LectureMaterialGenerator(self.test_data)

    def test_generate_lecture_materials(self):
        # 生成讲义
        self.generator.knowledge_hierarchy = self.generator._parse_knowledge_hierarchy(self.test_data)
        lecture_content = self.generator.generate_lecture_materials()

        # 检查生成的内容是否包含预期的知识点
        self.assertIn("触发器", lecture_content)
        # self.assertIn("测试知识点2", lecture_content)

        # 将生成的讲义内容保存到文件
        output_dir = Path(project_root/"output/lectures")
        output_dir.mkdir(parents=True, exist_ok=True)  # 确保输出目录存在
        output_file = output_dir / "test_lecture_material.md"

        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(lecture_content)

        # 检查文件是否成功创建
        self.assertTrue(output_file.exists())

if __name__ == '__main__':
    unittest.main()

