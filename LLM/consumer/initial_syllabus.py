import pika
import logging
from dotenv import load_dotenv
import os
import ssl
import json
from core.initial_syllabus_content_generator import InitialSyllabusContentGenerator
from core.contents_generator import ContentsGenerator

logging.basicConfig(filename = 'log_information/syllabus/initial_syllabus_logging.log', # 指定输出日志的路径
                    filemode = 'a', # 指定输出模式
                    level = logging.INFO, # 指定最低输出的日志级别
                    format = '%(asctime)s - %(levelname)s - %(message)s',  # 指定输出日志的格式
                    datefmt='%Y-%m-%d %H:%M:%S' # 格式化时间戳
                    )

load_dotenv(".env")

RabbitMQ_User = os.getenv("RABBITMQ_USER")
RabbitMQ_Password = os.getenv("RABBITMQ_PASSWORD")
RabbitMQ_Url = os.getenv("RABBITMQ_URL")
# 队列名称
QUEUE_NAME_TASK = 'initial_syllabus_task_queue'
QUEUE_NAME_RESPONSE = 'initial_syllabus_response_queue'

# 回调函数，用于处理接收到的任务消息
# 参数说明:
# ch: pika.channel.Channel 当前的通道
# method: pika.frame.Method 包含消息的元数据，例如 delivery_tag
# properties: pika.BasicProperties 包含消息的属性，例如 reply_to 和 correlation_id
# body: bytes，消息体，包含客户端发送的请求内容
def send_response(response:str):
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
                                           retry_delay=5,
                                           heartbeat=3600
                                           )
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()

    channel.queue_declare(queue=QUEUE_NAME_RESPONSE)

    # 发送任务到队列
    channel.basic_publish(
        exchange='',
        routing_key=QUEUE_NAME_RESPONSE,
        body=response
    )

    # 打印提示信息
    logging.info("[>] 发送生成的大纲")

    # 任务发送后立即关闭连接（异步）
    connection.close()


def callback(ch, method, properties, body):
    body_str = body.decode('utf-8')
    request_message = json.loads(body_str)
    logging.info(f"[x] 接收 producer 消息队列:\n{json.dumps(request_message, indent = 2,ensure_ascii=False)}")

    # 对于必传参数，直接获取
    course_id = request_message["course_id"]
    course_title = request_message['course_title']
    credit = request_message['credit']
    course_hour = request_message['course_hour']
    course_introduction = request_message['course_introduction']
    teaching_target =  request_message['teaching_target']
    evaluation_mode = request_message['evaluation_mode']

    # 对于可选参数，使用 get() 方法
    course_code = request_message.get('course_code',"0")
    teaching_language = request_message.get('teaching_language',"中文")
    responsible_college = request_message.get('responsible_college',"xx学院")
    course_category = request_message.get('course_category',"理论课")
    principle = request_message.get('principle',"张三")
    verifier = request_message.get('verifier',"李四")
    whether_technical_course = request_message.get('whether_technical_course',"是")
    assessment_type = request_message.get('assessment_type',"考查")
    grade_recording = request_message.get('grade_recording',"百分制")
    request = request_message.get('request',"无")

    # # 预生成课程目录
    # contents = ContentsGenerator(course_title)
    # contents.txt_contents_generator()
    # contents.json_contents_generator()

    parameter = {}
    parameter["course_id"] = course_id
    parameter["course_code"] = course_code
    parameter["course_title"] = course_title
    parameter["teaching_language"] = teaching_language
    parameter["responsible_college"] = responsible_college
    parameter["course_category"] = course_category
    parameter["principle"] = principle
    parameter["verifier"] = verifier
    parameter["credit"] = credit
    parameter["course_hour"] = course_hour
    parameter["evaluation_mode"] = evaluation_mode
    parameter["whether_technical_course"] = whether_technical_course
    parameter["assessment_type"] = assessment_type
    parameter["grade_recording"] = grade_recording
    parameter["request"] = request

    logging.info(f"传递给大模型的参数:\n{json.dumps(parameter, indent=2,ensure_ascii=False)}")

    # 传参
    initial_syllabus = InitialSyllabusContentGenerator(course_id,
                                                       course_code,
                                                       course_title,
                                                       teaching_language,
                                                       responsible_college,
                                                       course_category,
                                                       principle,
                                                       verifier,
                                                       credit,
                                                       course_hour,
                                                       course_introduction,
                                                       teaching_target,
                                                       evaluation_mode,
                                                       whether_technical_course,
                                                       assessment_type,
                                                       grade_recording,
                                                       request
                                                       )

    # 调用函数，指定大模型生成相关内容后返回
    response = json.dumps(initial_syllabus.syllabus_integration())
    send_response(response)

# 主函数
def main():
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
                                           retry_delay=5,
                                           heartbeat=3600
                                           )
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    # 确保队列存在
    channel.queue_declare(queue=QUEUE_NAME_TASK)

    # 设置队列消费者
    channel.basic_consume(queue=QUEUE_NAME_TASK,
                          on_message_callback=callback,
                          auto_ack=True)

    logging.info('[*] 等待接收任务...')
    channel.start_consuming()

if __name__ == '__main__':
    main()