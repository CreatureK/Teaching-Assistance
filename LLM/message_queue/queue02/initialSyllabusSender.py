import pika
import json
import os

# 从文件读取 initialSyllabus.json
file_path = 'initialSyllabus.json'
if os.path.exists(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        initial_syllabus_data = file.read()
    
    # 将内容发送到 initial_syllabus_queue
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='initial_syllabus_queue')
    channel.basic_publish(exchange='', routing_key='initial_syllabus_queue', body=initial_syllabus_data)
    print(f"[x] sent initial syllabus from '{file_path}'")
else:
    print(f"文件 {file_path} 不存在。")
