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
QUEUE_NAME = 'contents_response_queue'

# 接收消费者“任务完成”响应的回调函数
def on_response(ch, method, properties, body):
    response_message = json.loads(body)
    print(f" [x] Received message:")
    print(json.dumps(response_message, indent=4, ensure_ascii=False))


# 单独线程中运行，监听来自消费者的“任务完成”响应
def listen_for_responses():
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
    # 确保队列存在
    channel.queue_declare(queue=QUEUE_NAME)

    # 监听队列中的所有消息
    channel.basic_consume(queue=QUEUE_NAME,
                          on_message_callback=on_response,
                          auto_ack=True)

    print("[*] 正在监听任务完成通知...")
    channel.start_consuming()

if __name__ == '__main__':
    listen_for_responses()