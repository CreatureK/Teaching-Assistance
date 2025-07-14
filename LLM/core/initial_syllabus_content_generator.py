import json
import logging
from openai import OpenAI
from config.settings import ModelConfig
from core.text_read import TextReader
from core.text_write import TextWriter
from concurrent.futures import ThreadPoolExecutor,wait

logging.basicConfig(filename = 'log_information/syllabus/initial_syllabus_logging.log', # 指定输出日志的路径
                    filemode = 'a', # 指定输出模式
                    level = logging.INFO, # 指定最低输出的日志级别
                    format = '%(asctime)s - %(levelname)s - %(message)s',  # 指定输出日志的格式
                    datefmt='%Y-%m-%d %H:%M:%S' # 格式化时间戳
                    )

class InitialSyllabusContentGenerator:

    def __init__(self, course_id: str,course_code: str,course_title: str,teaching_language: str,responsible_college: str,course_category: str,principle: str,verifier: str,credit: str,course_hour: str,course_introduction: str,teaching_target: str,evaluation_mode: str,whether_technical_course: str,assessment_type: str,grade_recording: str,request: str):
        # 大模型参数初始化
        self.config = ModelConfig()
        self.config_dict = self.config.get_qwen_plus()
        self.model = self.config_dict["model_name"]
        self.url = self.config_dict["model_url"]
        self.api_key = self.config_dict["model_api_key"]
        self.client = None
        self.completion = None

        # 课程序号导入
        self.course_id: str = course_id
        logging.info(f"课程序号: {course_id}")

        # 课程代码导入
        self.course_code: str = course_code
        logging.info(f"课程代码: {course_code}")

        # 课程标题导入
        self.course_title: str = course_title
        logging.info(f"课程标题: {course_title}")

        # 授课语言导入
        self.teaching_language: str = teaching_language
        logging.info(f"授课语言: {teaching_language}")

        # 开课学院导入
        self.responsible_college: str = responsible_college
        logging.info(f"开课学院: {responsible_college}")

        # 课程类别导入
        self.course_category: str = course_category
        logging.info(f"课程类别: {course_category}")

        # 课程负责人导入
        self.principle: str = principle
        logging.info(f"课程负责人: {principle}")

        # 课程审核人导入
        self.verifier: str = verifier
        logging.info(f"课程审核人: {verifier}")

        # 学分导入
        self.credit: str = credit
        logging.info(f"学分: {credit}")

        # 课程学时导入
        self.course_hour: str = course_hour
        logging.info(f"课程学时: {course_hour}")

        # 课程介绍导入
        self.course_introduction: str = course_introduction
        logging.info(f"课程介绍: {course_introduction}")

        # 教学目标导入
        self.teaching_target: str = teaching_target
        logging.info(f"教学目标: {teaching_target}")

        # 考核方式导入
        self.evaluation_mode: str = evaluation_mode
        logging.info(f"考核方式: {evaluation_mode}")

        # 是否术科课导入
        self.whether_technical_course: str = whether_technical_course
        logging.info(f"是否术科课: {whether_technical_course}")

        # 考核类型导入
        self.assessment_type: str = assessment_type
        logging.info(f"考核类型: {assessment_type}")

        # 成绩记载导入
        self.grade_recording: str = grade_recording
        logging.info(f"成绩记载: {grade_recording}")

        # 用户个性化需求的导入
        self.request: str = request
        logging.info(f"用户的个性化需求: {request}")

        # 大纲模版导入
        self.syllabus_template_file_path = "templates/syllabus/syllabus.json"
        self.reader = TextReader(self.syllabus_template_file_path)
        self.syllabus_template = self.reader.read()
        logging.info("教学大纲模版导入")

        # 大纲初版生成的 prompt 导入
        self.prompt_file_path = "prompt/syllabus/prompt_for_initial_generation.txt"
        self.reader = TextReader(self.prompt_file_path)
        self.prompt = self.reader.read()
        logging.info("教学大纲 prompt 导入")

        # 并行生成时需要用的内容
        self.course_English_name = ""

        self.reader = TextReader("prompt/syllabus/prompt_for_detailed_course_target.txt")
        self.prompt_for_detailed_course_target = self.reader.read()
        self.detailed_course_target_part = None

        self.reader = TextReader("prompt/syllabus/prompt_for_teaching_content.txt")
        self.prompt_for_teaching_content = self.reader.read()
        self.teaching_content = None

        self.reader = TextReader("prompt/syllabus/prompt_for_experimental_projects.txt")
        self.prompt_for_experimental_projects = self.reader.read()
        self.experimental_projects = None

        self.reader = TextReader("prompt/syllabus/prompt_for_textbooks_and_reference_books.txt")
        self.prompt_for_textbooks_and_reference_books = self.reader.read()
        self.textbooks_and_reference_books = None


        # # 课程目录读入
        # self.contents_file_path = "output/contents/contents.json"
        # self.reader = TextReader(self.contents_file_path)
        # self.contents = self.reader.read()
        # self.contents = json.loads(self.contents)
        # self.Contents = {}
        # for chapter in self.contents["chapters"]:
        #     self.unitName = chapter["unitName"]
        #     self.subNames = [sub["subName"] for sub in chapter["subChapters"]]
        #     self.sub_names_str = ", ".join(self.subNames)
        #     self.Contents[self.unitName] = self.sub_names_str
        # # logging.info(f"{self.contents}")

        # 待生成结果初始化
        self.answer = ""
        self.response = {}
        self.text = ('{'
                     f'"course_id" : "{self.course_id}",'
                     '"chu":['
                     '{"course_title": "'
                     f'{self.course_title}'
                     f'","initial_syllabus": [')
        self.target_position = "output/syllabus/initial_syllabus.json"
        self.writer = None
        self.count = 1

    # def initial_syllabus_content_generator(self):
    #     # 输出调用大模型提示
    #     logging.info("正在调用大模型生成初版大纲，请耐心等待...")
    #
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
    #                     {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及大纲制作，你需要按照模版进行大纲的制作'},
    #                     # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
    #                     {'role': 'system', 'content': f'以下是一份json模版内容，在制作课程大纲时候，你需要根据该json模版进行生成:<template>{self.syllabus_template}</template>'},
    #                     {'role': 'system', 'content': f'每次，你只需要生成一个大章节及其下的内容即可，用户会告诉你当前前的大章节是什么以及小标题都有什么'},
    #                     {'role': 'system', 'content': f'{self.prompt}'},
    #                     {'role': 'user', 'content': f'你需要生成一份教学大纲，课程标题为:{self.course_title},请结合相关的知识库内容进行制作'},
    #                     {'role': 'user', 'content': f'该门课的总学时为{self.credit_hours}'},
    #                     {'role': 'user', 'content': f'章节大标题是{unitName},其下的小标题为{subName}，当前的unitNumber值为{self.count}'},
    #                     {'role': 'user', 'content': f'在制作的过程中，我的制作要求是:<request>{self.request}</request>'}
    #                 ]
    #             )
    #             self.answer = self.completion.choices[0].message.content
    #             logging.info(f"大模型已经完成教学大纲 part{self.count} 的初步生成")
    #
    #         except Exception as e:
    #             logging.error(f"错误信息：{e}")
    #
    #         if self.count >1 :
    #             self.text = self.text + ',' + self.answer
    #         else:
    #             self.text = self.text + self.answer
    #
    #         self.count += 1
    #
    #     self.text += ']}]}'
    #     # 输出初版大纲
    #     self.writer = TextWriter(self.text,self.target_position)
    #     self.writer.write()
    #
    #     return self.text

    def get_English_name(self):
        # 调用大模型生成课程的英文名
        logging.info(f"正在调用大模型翻译课程名称，请耐心等待...")

        client = OpenAI(
            api_key=self.api_key,
            base_url=self.url,
        )

        completion = client.chat.completions.create(
            model=self.model,
            messages=[
                {'role': 'system', 'content': f'你是一位资深大学教授，你知道各个学科对应的中文名和英文名分别是什么。'},
                {'role': 'system', 'content': f'用户会给你一个课程的中文名，你需要将其翻译成英文，并且返回给用户。注意，你返回的内容应该有且仅有课程的英文名，并且是一个字符串，而不要有其他任何多余的内容'},
                {'role': 'user', 'content': f'课程的中文名是{self.course_title}，请给出它的英文名。'}
            ]
        )
        self.course_English_name = completion.choices[0].message.content

        logging.info(f"课程名称翻译完成:{self.course_English_name}")

    def detailed_course_target_generator(self):
        # 调用大模型生成教学目标的部分内容
        logging.info(f"正在调用大模型进行初版大纲中的 detailed_course_target 部分生成，请耐心等待...")

        client = OpenAI(
            api_key=self.api_key,
            base_url=self.url,
        )

        completion = client.chat.completions.create(
            model=self.model,
            messages=[
                {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及大纲制作，你需要按照模版进行大纲的制作'},
                # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
                {'role': 'system', 'content': f'在本次生成中，你需要生成的大纲部分是 detailed_course_target，其json模版内容后续会指定。'},
                {'role': 'system', 'content': f'你需要从下面信息中挑选和本部分生成相关的内容有关联的有效信息，而不是全部纳入考虑范围。'},
                {'role': 'system', 'content': f'{self.prompt_for_detailed_course_target}'},
                {'role': 'user', 'content': f'你需要生成一份教学大纲的部分内容，课程标题为:{self.course_title},请结合相关的知识库内容进行对应大纲部分内容的制作'},
                {'role': 'user', 'content': f'后续是用户的额外需求，但是首先，你需要判断用户的需求和本部分生成是否相关，再决定是否执行。在制作的过程中，用户的制作要求是:<request>{self.request}</request>'}
            ]
        )
        answer = completion.choices[0].message.content
        answer_dict = json.loads(answer)
        self.detailed_course_target_part = answer_dict

        logging.info(f"detailed_course_target 部分内容生成完毕")
        # logging.info(f"\n{json.dumps(answer_dict, indent=2, ensure_ascii=False)}")

    def teaching_content_generator(self):
        # 调用大模型生成教学内容部分的内容
        logging.info(f"正在调用大模型进行初版大纲中的 teaching_content 部分生成，请耐心等待...")

        client = OpenAI(
            api_key=self.api_key,
            base_url=self.url,
        )

        completion = client.chat.completions.create(
            model=self.model,
            messages=[
                {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及大纲制作，你需要按照模版进行大纲的制作'},
                # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
                {'role': 'system', 'content': f'在本次生成中，你需要生成的大纲部分是 teaching_content，其json模版内容后续会指定。'},
                {'role': 'system', 'content': f'你需要从下面信息中挑选和本部分生成相关的内容有关联的有效信息，而不是全部纳入考虑范围。'},
                {'role': 'system', 'content': f'该门课的总学时为{self.course_hour}，你需要合理分配这些学时'},
                {'role': 'system', 'content': f'{self.prompt_for_teaching_content}'},
                {'role': 'user', 'content': f'你需要生成一份教学大纲的部分内容，课程标题为:{self.course_title},请结合相关的知识库内容进行对应大纲部分内容的制作'},
                {'role': 'user', 'content': f'后续是用户的额外需求，但是首先，你需要判断用户的需求和本部分生成是否相关，再决定是否执行。在制作的过程中，用户的制作要求是:<request>{self.request}</request>'}
            ]
        )
        answer = completion.choices[0].message.content
        answer_dict = json.loads(answer)
        self.teaching_content = answer_dict

        logging.info(f"teaching_content 部分内容生成完毕")
        # logging.info(f"\n{json.dumps(answer_dict, indent=2, ensure_ascii=False)}")

    def experimental_projects_generator(self):
        # 调用大模型生成实验部分的内容
        logging.info(f"正在调用大模型进行初版大纲中的 experimental_projects 部分生成，请耐心等待...")

        client = OpenAI(
            api_key=self.api_key,
            base_url=self.url,
        )

        completion = client.chat.completions.create(
            model=self.model,
            messages=[
                {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及大纲制作，你需要按照模版进行大纲的制作'},
                # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
                {'role': 'system', 'content': f'在本次生成中，你需要生成的大纲部分是 experimental_projects，其json模版内容后续会指定。'},
                {'role': 'system', 'content': f'你需要从下面信息中挑选和本部分生成相关的内容有关联的有效信息，而不是全部纳入考虑范围。'},
                {'role': 'system', 'content': f'{self.prompt_for_experimental_projects}'},
                {'role': 'user', 'content': f'你需要生成一份教学大纲的部分内容，课程标题为:{self.course_title},请结合相关的知识库内容进行对应大纲部分内容的制作'},
                {'role': 'user', 'content': f'后续是用户的额外需求，但是首先，你需要判断用户的需求和本部分生成是否相关，再决定是否执行。在制作的过程中，用户的制作要求是:<request>{self.request}</request>'}
            ]
        )
        answer = completion.choices[0].message.content
        answer_dict = json.loads(answer)
        self.experimental_projects = answer_dict

        logging.info(f"experimental_projects 部分内容生成完毕")
        # logging.info(f"\n{json.dumps(answer_dict, indent=2, ensure_ascii=False)}")

    def textbooks_and_reference_books_generator(self):
        # 调用大模型生成参考教材和书籍部分的内容
        logging.info(f"正在调用大模型进行初版大纲中的 textbooks_and_reference_books 部分生成，请耐心等待...")

        client = OpenAI(
            api_key=self.api_key,
            base_url=self.url,
        )

        completion = client.chat.completions.create(
            model=self.model,
            messages=[
                {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及大纲制作，你需要按照模版进行大纲的制作'},
                # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
                {'role': 'system', 'content': f'在本次生成中，你需要生成的大纲部分是 textbooks_and_reference_books，其json模版内容后续会指定。'},
                {'role': 'system', 'content': f'你需要从下面信息中挑选和本部分生成相关的内容有关联的有效信息，而不是全部纳入考虑范围。'},
                {'role': 'system', 'content': f'该门课的总学时为{self.course_hour}，你需要合理分配这些学时'},
                {'role': 'system', 'content': f'{self.prompt_for_textbooks_and_reference_books}'},
                {'role': 'user', 'content': f'你需要生成一份教学大纲的部分内容，课程标题为:{self.course_title},请结合相关的知识库内容进行对应大纲部分内容的制作'},
                {'role': 'user', 'content': f'后续是用户的额外需求，但是首先，你需要判断用户的需求和本部分生成是否相关，再决定是否执行。在制作的过程中，用户的制作要求是:<request>{self.request}</request>'}
            ]
        )
        answer = completion.choices[0].message.content
        answer_dict = json.loads(answer)
        self.textbooks_and_reference_books = answer_dict

        logging.info(f"textbooks_and_reference_books 部分内容生成完毕")
        # logging.info(f"\n{json.dumps(answer_dict, indent=2, ensure_ascii=False)}")

    def multithreading_processor(self):
        # 输出调用大模型提示
        logging.info("正在多线程调用大模型生成初版大纲，请耐心等待...")

        # 将大纲中部分内容拆分出来，并行调用，优化生活时间
        # 根据具体并行调用个数确定线程数
        max_workers = 4

        generation_part_list = [self.get_English_name,self.detailed_course_target_generator,self.teaching_content_generator,self.experimental_projects_generator,self.textbooks_and_reference_books_generator]

        # 创建线程池
        with ThreadPoolExecutor(max_workers = max_workers) as executor:
            # 提交任务到线程池
            futures = [executor.submit(generation_part) for generation_part in generation_part_list]
            done, not_done = wait(futures)

        logging.info("并行调用结束，即将返回最终结果")

    def initial_syllabus_generator(self) -> str:
        # 输出大模型调用提示信息
        logging.info("正在调用大模型生成初版大纲，请耐心等待...")
        self.client = OpenAI(
            api_key=self.api_key,
            base_url=self.url,
        )

        self.completion = self.client.chat.completions.create(
            model=self.model,
            messages=[
                {'role': 'system', 'content': f'你是一位资深大学教授，尤其擅长{self.course_title}学科的教学以及大纲制作，你需要按照模版进行大纲的制作'},
                # {'role': 'system', 'content': f'在开始制作课程大纲之前，你需要先阅读理解以下内容，这将对你制作课程大纲的部分内容提供指导<this_is_a_knowledge_content_for_syllabus_generation>{knowledge}</this_is_a_knowledge_content_for_syllabus_generation>'},
                {'role': 'system', 'content': f'以下是一份json模版内容，在制作课程大纲时候，你需要根据该json模版进行生成:<template>{self.syllabus_template}</template>'},
                {'role': 'system', 'content': f'{self.prompt}'},
                {'role': 'user', 'content': f'你需要生成一份教学大纲，课程标题为:{self.course_title},请结合相关的知识库内容进行制作'},
                {'role': 'user', 'content': f'该门课的总学时为{self.course_hour}'},
                {'role': 'user', 'content': f'在制作的过程中，用户的制作要求是:<request>{self.request}</request>'}
            ]
        )
        self.answer = self.completion.choices[0].message.content

        return self.answer


    def syllabus_integration(self) -> dict:
        self.multithreading_processor()

        logging.info("正在处理 initial_syllabus,请耐心等待处理结果...")

        self.response["course_id"] = self.course_id
        self.response["course_code"] = self.course_code
        self.response["course_Chinese_name"] = self.course_title
        self.response["course_English_name"] = self.course_English_name
        self.response["teaching_language"] = self.teaching_language
        self.response["responsible_college"] = self.responsible_college
        self.response["course_category"] = self.course_category
        self.response["principle"] = self.principle
        self.response["verifier"] = self.verifier
        self.response["credit"] = self.credit
        self.response["course_hour"] = self.course_hour
        self.response["whether_technical_course"] = self.whether_technical_course
        self.response["assessment_type"] = self.assessment_type
        self.response["grade_recording"] = self.grade_recording
        self.response["course_introduction"] = self.course_introduction
        self.response["course_target"] = self.teaching_target
        self.response["detailed_course_target"] = self.detailed_course_target_part
        self.response["evaluation_mode"] = self.evaluation_mode
        self.response["teaching_content"] = self.teaching_content
        self.response["experimental_projects"] = self.experimental_projects
        self.response["textbooks_and_reference_books"] = self.textbooks_and_reference_books

        # answer = json.loads(self.initial_syllabus_generator())
        # logging.info("正在处理 initial_syllabus,即将返回最终结果...")
        #
        # self.response["course_id"] = self.course_id
        # self.response["course_code"] = self.course_code
        # self.response["course_Chinese_name"] = answer["course_Chinese_name"]
        # self.response["course_English_name"] = answer["course_English_name"]
        # self.response["teaching_language"] = self.teaching_language
        # self.response["responsible_college"] = self.responsible_college
        # self.response["course_category"] = self.course_category
        # self.response["principle"] = self.principle
        # self.response["verifier"] = self.verifier
        # self.response["credit"] = self.credit
        # self.response["course_hour"] = self.course_hour
        # self.response["whether_technical_course"] = self.whether_technical_course
        # self.response["assessment_type"] = self.assessment_type
        # self.response["grade_recording"] = self.grade_recording
        # self.response["course_introduction"] = self.course_introduction
        # self.response["course_target"] = self.teaching_target
        # self.response["detailed_course_target"] = answer["detailed_course_target"]
        # self.response["evaluation_mode"] = self.evaluation_mode
        # self.response["teaching_content"] = answer["teaching_content"]
        # self.response["experimental_projects"] = answer["experimental_projects"]
        # self.response["textbooks_and_reference_books"] = answer["textbooks_and_reference_books"]

        logging.info(f"所有生成和处理已经完成，即将返回最终的 initial_syllabus 返回")
        # logging.info(json.dumps(self.response, indent=4, ensure_ascii=False))

        self.writer = TextWriter(json.dumps(self.response,indent =2,ensure_ascii=False),self.target_position)
        self.writer.write()

        return self.response