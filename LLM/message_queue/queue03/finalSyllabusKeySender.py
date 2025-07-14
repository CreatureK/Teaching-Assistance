import pika
import json
import os

# 从文件读取 finalSyllabusKey.json
file_path = 'finalSyllabusKey.json'
if os.path.exists(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        final_syllabus_key_data = file.read()
    
    # 将内容发送到 final_syllabus_key_queue
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='final_syllabus_key_queue')
    channel.basic_publish(exchange='', routing_key='final_syllabus_key_queue', body=final_syllabus_key_data)
    print(f"[x] sent final syllabus key from '{file_path}'")
else:
    print(f"文件 {file_path} 不存在。")
