import pika
from dotenv import load_dotenv
import os
import ssl
import json
from core.contents_generator import ContentsGenerator

load_dotenv(".env")

RabbitMQ_User = os.getenv("RABBITMQ_USER")
RabbitMQ_Password = os.getenv("RABBITMQ_PASSWORD")
RabbitMQ_Url = os.getenv("RABBITMQ_URL")

# 声明队列名称
QUEUE_NAME_TASK = 'contents_task_queue'
QUEUE_NAME_RESPONSE = 'contents_response_queue'

# 回调函数，用于处理接收到的任务消息
# 参数说明:
# ch: pika.channel.Channel 当前的通道
# method: pika.frame.Method 包含消息的元数据，例如 delivery_tag
# properties: pika.BasicProperties 包含消息的属性，例如 reply_to 和 correlation_id
# body: bytes，消息体，包含客户端发送的请求内容
def callback(ch, method, properties, body):
    request_message = json.loads(body)
    course_title = request_message['course_title']

    print(f"[x] 接收 producer 消息队列:\n{request_message}")

    # 传参
    contents = ContentsGenerator(course_title)

    # 调用函数，指定大模型生成相关内容后返回
    contents.txt_contents_generator()
    response = contents.json_contents_generator()

    ch.queue_declare(queue = QUEUE_NAME_RESPONSE)

    # 使用相同队列发送完成任务的内容
    ch.basic_publish(exchange='',
                     routing_key=QUEUE_NAME_RESPONSE,
                     body=response)

    print("\n")

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
                                           retry_delay=5
                                           )
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    # 确保队列存在
    channel.queue_declare(queue=QUEUE_NAME_TASK)

    # 设置队列消费者
    channel.basic_consume(queue=QUEUE_NAME_TASK,
                          on_message_callback=callback,
                          auto_ack=True)

    print('[*] 等待接收任务...')
    channel.start_consuming()

if __name__ == '__main__':
    main()