import pika
import json
import os

# 从文件读取 knowledgeCandidate.json
file_path = 'knowledgeCandidate.json'
if os.path.exists(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        knowledge_candidate_data = file.read()
    
    # 将内容发送到 knowledge_candidate_queue
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='knowledge_candidate_queue')
    channel.basic_publish(exchange='', routing_key='knowledge_candidate_queue', body=knowledge_candidate_data)
    print(f"[x] sent knowledge candidate from '{file_path}'")
else:
    print(f"文件 {file_path} 不存在。")
