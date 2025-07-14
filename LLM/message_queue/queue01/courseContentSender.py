import pika
import json
import os

# 从文件读取 courseContent.json
file_path = 'courseContent.json'
if os.path.exists(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        course_content_data = file.read()
    
    # 将内容发送到 course_content_queue
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='course_content_queue')
    channel.basic_publish(exchange='', routing_key='course_content_queue', body=course_content_data)
    print(f"[x] sent course content from '{file_path}'")
else:
    print(f"文件 {file_path} 不存在。") 