import pika
import os

# 从文件读取 lecture.md
file_path = 'lecture.md'
if os.path.exists(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        lecture_data = file.read()
    
    # 将内容发送到 lecture_queue
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='lecture_queue')
    channel.basic_publish(exchange='', routing_key='lecture_queue', body=lecture_data)
    print(f"[x] sent lecture from '{file_path}'")
else:
    print(f"文件 {file_path} 不存在。")
