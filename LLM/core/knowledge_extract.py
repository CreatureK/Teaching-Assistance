import json
from copy import deepcopy

class SyllabusProcessor:
    
    def __init__(self, input_data):
        """
        初始化处理器
        :param input_data: 输入的原始数据（字典格式）
        """
        self.raw_data = input_data
        self.processed_data = None

    def process(self):
        """执行完整处理流程"""
        self.process_data()
        return self.processed_data  # 返回处理后的数据

    def process_data(self):
        """处理数据核心逻辑"""
        def process_unit(unit):
            unit.pop('unit_knowledge_point', None)
            unit['subsections'] = [process_subsection(sub) for sub in unit.get('subsections', [])]
            return unit

        def process_subsection(subsection):
            """处理单个小节，保留小节层级的content"""
            if 'knowledge_point' in subsection:
                # 获取当前小节的分配时间
                sub_time = self.parse_time(subsection.get('time_allocation', '0学时'))
                
                # 拆分知识点，支持顿号和分号
                kp_list = [kp.strip() for kp in subsection['knowledge_point'].replace('；', '、').split('、') if kp.strip()]
                kp_count = len(kp_list) or 1  # 防止除零错误
                
                # 计算每个知识点的时间
                per_time = sub_time / kp_count
                time_str = self.format_time(per_time)
                
                # 构建knowledge_points（移除content字段）
                subsection['knowledge_points'] = [
                    {
                        'knowledge_point': kp,
                        'time_allocation': time_str
                    }
                    for kp in kp_list
                ]
                
                # 移除冗余字段（保留小节层级的content）
                for key in ['knowledge_point', 'subUnitNumber']:
                    subsection.pop(key, None)
                
            return subsection

        self.processed_data = {
            'teachingContentAndSchedule': [
                process_unit(deepcopy(unit)) for unit in self.raw_data['teachingContentAndSchedule']
            ]
        }

    @staticmethod
    def parse_time(time_str):
        """将时间字符串转换为分钟数"""
        if '学时' in time_str:
            return float(time_str.replace('学时', '')) * 45  # 1学时=45分钟
        return 0

    @staticmethod
    def format_time(minutes):
        """将分钟数转换为学时字符串"""
        hours = minutes / 45
        # 保留两位小数并去除末尾的零
        return f"{hours:.2f}".rstrip('0').rstrip('.') + '学时'

if __name__ == "__main__":
    # 读取初始数据
    with open('output/syllabus/initial_syllabus.json', 'r', encoding='utf-8') as f:
        input_data = json.load(f)

    processor = SyllabusProcessor(input_data)
    processed_result = processor.process()  # 处理后的结果赋给变量
    # print(processed_result)
    processed_result = json.dumps(processed_result, indent=2, ensure_ascii=False)  # 打印处理结果
    # print(processed_result)