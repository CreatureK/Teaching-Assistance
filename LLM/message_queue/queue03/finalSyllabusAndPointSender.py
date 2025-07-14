import pika
import json
import os

def send_files():
    # 发送 finalSyllabus.json
    final_syllabus_file_path = 'finalSyllabus.json'
    if os.path.exists(final_syllabus_file_path):
        with open(final_syllabus_file_path, 'r', encoding='utf-8') as file:
            final_syllabus_data = file.read()
        
        # 将内容发送到 final_syllabus_and_point_queue
        connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        channel = connection.channel()
        channel.queue_declare(queue='final_syllabus_and_point_queue')
        channel.basic_publish(exchange='', routing_key='final_syllabus_and_point_queue', body=final_syllabus_data)
        print(f"[x] sent final syllabus from '{final_syllabus_file_path}'")
    else:
        print(f"文件 {final_syllabus_file_path} 不存在。")

    # 发送 knowledgePoint.json
    knowledge_point_file_path = 'knowledgePoint.json'
    if os.path.exists(knowledge_point_file_path):
        with open(knowledge_point_file_path, 'r', encoding='utf-8') as file:
            knowledge_point_data = file.read()
        
        # 将内容发送到 final_syllabus_and_point_queue
        channel.basic_publish(exchange='', routing_key='final_syllabus_and_point_queue', body=knowledge_point_data)
        print(f"[x] sent knowledge point from '{knowledge_point_file_path}'")
    else:
        print(f"文件 {knowledge_point_file_path} 不存在。")

send_files()
