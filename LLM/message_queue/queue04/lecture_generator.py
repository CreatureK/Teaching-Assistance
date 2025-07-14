import pika
import json

def generate_lecture(knowledge_candidate):
    # 这里是生成知识点讲义的逻辑
    return f"# 课程讲义\n\n知识点: {knowledge_candidate['knowledgeCandidate'][0]['knowledgePoint']}"

def callback(ch, method, properties, body):
    data = json.loads(body.decode())
    knowledge_candidate = data['knowledgeCandidate'][0]
    
    lecture_output = generate_lecture(knowledge_candidate)
    
    # 发送到 lecture_markdown_queue
    channel.basic_publish(exchange='', routing_key='lecture_markdown_queue', body=lecture_output)

    print(f"[x] processed knowledge candidate and sent lecture markdown.")

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.queue_declare(queue='knowledge_candidate_queue')
channel.basic_consume(queue='knowledge_candidate_queue', on_message_callback=callback, auto_ack=True)

print('[*] waiting for messages in knowledge_candidate_queue, to exit express CTRL+C')
channel.start_consuming() 