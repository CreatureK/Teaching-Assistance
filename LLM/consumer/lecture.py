import pika
from dotenv import load_dotenv
import os
import ssl
import json
import sys
from pathlib import Path
import logging
from datetime import datetime

current_dir = Path(__file__).parent
project_root = current_dir.parent
sys.path.append(str(project_root))
from core.lecture_generator import LectureMaterialGenerator

# 设置日志记录
log_dir = Path(project_root/"log_information/lecture")
log_dir.mkdir(parents=True, exist_ok=True)  # 确保日志目录存在
log_filename = log_dir / f"lecture_consumer_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log"
logging.basicConfig(filename=log_filename,
                    filemode='a',
                    level=logging.INFO,
                    format='%(asctime)s - %(levelname)s - %(message)s',
                    datefmt='%Y-%m-%d %H:%M:%S',
                    encoding='utf-8')

load_dotenv(".env")

RabbitMQ_User = os.getenv("RABBITMQ_USER")
RabbitMQ_Password = os.getenv("RABBITMQ_PASSWORD")
RabbitMQ_Url = os.getenv("RABBITMQ_URL")

# 队列名称
QUEUE_NAME_TASK = 'lecture_task_queue'
QUEUE_NAME_RESPONSE = 'lecture_response_queue'

def send_message(response:str):
    # 建立连接
    # SSL上下文配置（不检验证书）、
    context = ssl.create_default_context()
    context.check_hostname = False
    context.verify_mode = ssl.CERT_NONE

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

    channel.basic_publish(
        exchange='',
        routing_key=QUEUE_NAME_RESPONSE,
        body=response,
        # properties=pika.BasicProperties(
        #     delivery_mode=2
        # )
    )

    logging.info("[>] 发送生成的讲义")
    connection.close()

# 回调函数，用于处理接收到的任务消息
def callback(ch, method, properties, body):
    try:
        request_message = json.loads(body)
        logging.info(f"[x] 接收 producer 消息队列:\n{request_message}")

        # 初始化生成器并生成讲义
        generator = LectureMaterialGenerator(request_message)
        lecture_content = generator.generate_lecture_materials()  # 将生成的讲义内容赋给变量

        # 构建响应消息
        response = {
            "status": "success",
            "message": "讲义生成完成",
            "lecture_content": lecture_content  # 将生成的讲义内容包含在响应中
        }

        # 发送响应消息
        send_message(json.dumps(response, ensure_ascii=False))

        logging.info("讲义生成完成，已发送响应消息。")
        logging.info("\n")

    except Exception as e:
        ch.basic_ack(delivery_tag=method.delivery_tag)
        logging.error(f"Failed to process message: {e}")

# 主函数
def main():
    # 建立连接
    context = ssl.create_default_context()
    context.check_hostname = False
    context.verify_mode = ssl.CERT_NONE

    credentials = pika.PlainCredentials(RabbitMQ_User, RabbitMQ_Password)
    parameters = pika.ConnectionParameters(host=RabbitMQ_Url,
                                           port=5671,
                                           credentials=credentials,
                                           ssl_options=pika.SSLOptions(context),
                                           connection_attempts=3,
                                           retry_delay=5)
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()

    # 设置队列消费者
    channel.basic_consume(queue=QUEUE_NAME_TASK,
                          on_message_callback=callback,
                          auto_ack=True)

    logging.info('[*] 等待接收讲义生成任务...')
    channel.start_consuming()

if __name__ == '__main__':
    main()