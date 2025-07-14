import pika
from dotenv import load_dotenv
import os
import ssl
import json

load_dotenv(".env")

RabbitMQ_User = os.getenv("RABBITMQ_USER")
RabbitMQ_Password = os.getenv("RABBITMQ_PASSWORD")
RabbitMQ_Url = os.getenv("RABBITMQ_URL")

# 队列名
QUEUE_NAME = 'initial_syllabus_task_queue'

# 发送任务消息
def send_task(message:str):
    # 建立连接
    # SSL 上下文配置（不验证证书）
    context = ssl.create_default_context()
    context.check_hostname = False
    context.verify_mode = ssl.CERT_NONE

    # 连接参数
    credentials = pika.PlainCredentials(RabbitMQ_User, RabbitMQ_Password)
    parameters = pika.ConnectionParameters(host=RabbitMQ_Url,
                                           port=5671,
                                           credentials=credentials,
                                           ssl_options=pika.SSLOptions(context),
                                           connection_attempts=3,
                                           retry_delay=5
                                           )
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    channel.queue_declare(queue=QUEUE_NAME)

    # 发送任务到队列
    channel.basic_publish(
        exchange='',
        routing_key=QUEUE_NAME,
        body=str(message)
    )

    # 任务发送后立即关闭连接（异步）
    connection.close()

if __name__ == '__main__':

    message1 = {
        "course_id":"13",
        "course_code":"1562156",
        "course_title":"计算机网络",
        "teaching_language":"中文",
        "responsible_college": "计算机学院",
        "course_category": "理论课（含实践环节）",
        "principle": "赵钱",
        "verifier":"孙李",
        "credit":"2",
        "course_hour":"40",
        "course_introduction": "本课程《计算机网络》旨在全面介绍计算机网络的基本原理、技术及应用。内容涵盖网络体系结构、协议栈（如TCP/IP）、数据传输、路由与交换、网络安全以及现代网络技术（如云计算、物联网等）。通过学习，学生将掌握计算机网络的核心概念，理解网络运行机制，并具备分析和解决实际网络问题的能力。",
        "teaching_target": "1. 理解计算机网络的基本概念、功能及其发展历史；2. 掌握OSI七层模型和TCP/IP协议栈的工作原理；3. 熟悉数据链路层、网络层、传输层的主要协议和技术；4. 学习路由选择、拥塞控制等关键技术的实现方法；5. 培养对网络安全威胁的认识及防护能力；6. 了解并跟踪现代网络技术（如SDN、5G、边缘计算）的发展趋势；7. 提升学生在网络环境下的问题分析和解决能力。",
        "evaluation_mode":
        {
            "class_performance": "0.2",
            "homework": "0.1",
            "midterm_exam": "0.2",
            "lab_work": "0.2",
            "closed-book_exam": "0.3",
            "open-book_exam": "0",
            "ppt_presentation": "0",
            "total": "1.0"
        },
        "whether_technical_course": "是",
        "assessment_type": "考查",
        "grade_recording": "百分制",
        "request": "生成的时候需要具体一点"
    }

    message2 = {
        "course_id":"13",
        "course_title":"操作系统",
        "credit":"2",
        "course_hour":"40",
        "course_introduction": "本课程《操作系统》旨在全面介绍操作系统的原理、结构与功能。内容涵盖操作系统的概述、进程管理、内存管理、文件系统、输入输出管理以及并发控制等核心主题。同时，结合现代操作系统的特点，强调其实用性和发展动态，帮助学生理解操作系统在计算机系统中的核心地位及作用。",
        "teaching_target": "1. 掌握操作系统的定义、功能和基本组成；2. 理解进程的概念及其调度机制；3. 学习内存管理技术，包括分页、分段和虚拟内存；4. 掌握文件系统的基本原理及其组织方式；5. 了解设备管理的机制及输入输出子系统；6. 能够分析和解决与操作系统相关的实际问题；7. 培养学生的系统思维能力，为后续学习打下坚实基础。",
        "evaluation_mode":
        {
            "class_performance": "0.2",
            "homework": "0.1",
            "midterm_exam": "0",
            "lab_work": "0.2",
            "closed-book_exam": "0.5",
            "open-book_exam": "0",
            "ppt_presentation": "0",
            "total": "1.0"
        }
    }

    print(f" [>] 发送任务内容:\n{json.dumps(message1,indent = 4,ensure_ascii=False)}")

    message1 = json.dumps(message1)
    message2 = json.dumps(message2)

    send_task(message1)
