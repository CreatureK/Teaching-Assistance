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
QUEUE_NAME = 'introduction_and_target_task_queue'

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
        body=str(message),
    )

    # 任务发送后立即关闭连接（异步）
    connection.close()

if __name__ == '__main__':
    course_id = "2"
    course_title = "操作系统"
    request = "无"

    message = {
        "course_id": course_id,
        "course_title":course_title,
        "request":request
    }

    print(f" [>] 发送任务内容:\n{message}")

    message = json.dumps(message)

    send_task(message)
