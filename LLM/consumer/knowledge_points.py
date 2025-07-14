import pika
from dotenv import load_dotenv
import os
import ssl
import json
import sys
from pathlib import Path

current_dir = Path(__file__).parent
project_root = current_dir.parent
sys.path.append(str(project_root))

from core.knowledge_point_generator import KnowledgePointGenerator

load_dotenv(".env")

RabbitMQ_User = os.getenv("RABBITMQ_USER")
RabbitMQ_Password = os.getenv("RABBITMQ_PASSWORD")
RabbitMQ_Url = os.getenv("RABBITMQ_URL")

QUEUE_NAME_TASK = 'knowledge_points_task_queue'
QUEUE_NAME_RESPONSE = 'knowledge_points_response_queue'


def callback(ch, method, properties, body):
    try:
        request_message = json.loads(body)
        print(f"[x] 接收 producer 消息队列:\n{request_message}")

        # 初始化处理器并处理
        processor = KnowledgePointGenerator(request_message)
        processed_result = processor.initial_knowledge_points_generator()  # 调用正确的方法

        # 构建响应消息
        response = {
            "status": "success",
            "message": "知识点处理完成",
            "data": processed_result
        }

    except Exception as e:
        response = {
            "status": "error",
            "message": f"任务处理失败：{e}",
            "data": None
        }
        print(f"[!] 任务处理异常：{e}")

    # 发送响应消息
    ch.queue_declare(queue=QUEUE_NAME_RESPONSE)
    ch.basic_publish(
        exchange='',
        routing_key=QUEUE_NAME_RESPONSE,
        body=json.dumps(response, ensure_ascii=False)
    )

    print("\n")

# 启动消费者
def main():
    # SSL配置，跳过证书校验
    context = ssl.create_default_context()
    context.check_hostname = False
    context.verify_mode = ssl.CERT_NONE

    credentials = pika.PlainCredentials(RabbitMQ_User, RabbitMQ_Password)
    parameters = pika.ConnectionParameters(
        host=RabbitMQ_Url,
        port=5671,
        credentials=credentials,
        ssl_options=pika.SSLOptions(context),
        connection_attempts=3,
        retry_delay=5
    )

    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()

    # 声明任务队列，防止第一次没有队列时报错
    channel.queue_declare(queue=QUEUE_NAME_TASK)

    # 不自动确认，确保成功处理后才ack
    channel.basic_consume(
        queue=QUEUE_NAME_TASK,
        on_message_callback=callback,
        auto_ack=False
    )

    print('[*] 等待接收知识点任务。')
    channel.start_consuming()


if __name__ == '__main__':
    main()
