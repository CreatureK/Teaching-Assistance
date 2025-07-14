import pika
import json

def callback(ch, method, properties, body):
    data = json.loads(body.decode())
    
    # 保存到 courseContentRe.json
    with open('courseContentRe.json', 'w', encoding='utf-8') as file:
        json.dump(data, file, ensure_ascii=False)
    
    print(f"[x] received and saved course content to 'courseContentRe.json'")

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.queue_declare(queue='course_content_queue')

# 消费消息
channel.basic_consume(queue='course_content_queue', on_message_callback=callback, auto_ack=True)

print('[*] waiting for messages in course_content_queue, to exit express CTRL+C')
channel.start_consuming() 