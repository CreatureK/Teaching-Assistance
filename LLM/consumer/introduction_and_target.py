import pika
import logging
from dotenv import load_dotenv
import os
import ssl
import json
from core.introduction_and_target_generator import IntroductionAndTargetGenerator

logging.basicConfig(filename = 'log_information/introduction_and_target/introduction_and_target_logging.log', # 指定输出日志的路径
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
QUEUE_NAME_TASK = 'introduction_and_target_task_queue'
QUEUE_NAME_RESPONSE = 'introduction_and_target_response_queue'

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
        body=response,
        # properties=pika.BasicProperties(
        #     delivery_mode=2, # 设置消息持久化
        # )
    )

    # 打印提示信息
    logging.info("[>] 发送生成的大纲")

    # 任务发送后立即关闭连接（异步）
    connection.close()

def callback(ch, method, properties, body):
    body_str = body.decode('utf-8')
    request_message = json.loads(body_str)
    logging.info(f"接收到消息:\n{request_message}")

    # 对于必传参数，直接访问
    course_id = request_message['course_id']
    course_title = request_message['course_title']

    # 对于可选参数，使用 get() 方法
    request = request_message.get("request","无")

    logging.info(f"参数详情:{course_id},{course_title},{request}")

    # # 预生成课程目录
    # contents = ContentsGenerator(course_title)
    # contents.txt_contents_generator()
    # contents.json_contents_generator()

    # 传参
    introduction_and_target = IntroductionAndTargetGenerator(course_id, course_title, request)

    # 调用函数，指定大模型生成相关内容后返回并发送消息
    response = introduction_and_target.introduction_and_target_integration()
    send_response(json.dumps(response))

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

    # 设置队列消费者
    channel.basic_consume(queue=QUEUE_NAME_TASK,
                          on_message_callback=callback,
                          auto_ack=True)

    logging.info('[*] 等待接收任务...')
    channel.start_consuming()

if __name__ == '__main__':
    main()