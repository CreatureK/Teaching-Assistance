import pika
import json
import os

# 从文件读取 teachingContent.json 和 teachingTarget.json
def send_teaching_files():
    # 发送 teachingContent.json
    teaching_content_file_path = 'teachingContent.json'
    if os.path.exists(teaching_content_file_path):
        with open(teaching_content_file_path, 'r', encoding='utf-8') as file:
            teaching_content_data = file.read()
        
        # 将内容发送到 teaching_content_queue
        connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        channel = connection.channel()
        channel.queue_declare(queue='teaching_content_queue')
        channel.basic_publish(exchange='', routing_key='teaching_content_queue', body=teaching_content_data)
        print(f"[x] sent teaching content from '{teaching_content_file_path}'")
    else:
        print(f"文件 {teaching_content_file_path} 不存在。")

    # 发送 teachingTarget.json
    teaching_target_file_path = 'teachingTarget.json'
    if os.path.exists(teaching_target_file_path):
        with open(teaching_target_file_path, 'r', encoding='utf-8') as file:
            teaching_target_data = file.read()
        
        # 将内容发送到 teaching_target_queue
        channel.queue_declare(queue='teaching_target_queue')
        channel.basic_publish(exchange='', routing_key='teaching_target_queue', body=teaching_target_data)
        print(f"[x] sent teaching target from '{teaching_target_file_path}'")
    else:
        print(f"文件 {teaching_target_file_path} 不存在。")

# 发送教学内容和目标文件
send_teaching_files() 