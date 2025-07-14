import pika
import json

def callback(ch, method, properties, body):
    data = json.loads(body.decode())
    
    # 保存到 initialSyllabusKeyRe.json
    with open('initialSyllabusKeyRe.json', 'w', encoding='utf-8') as file:
        json.dump(data, file, ensure_ascii=False)
    
    print(f"[x] received and saved initial syllabus key to 'initialSyllabusKeyRe.json'")

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.queue_declare(queue='initial_syllabus_key_queue')

# 消费消息
channel.basic_consume(queue='initial_syllabus_key_queue', on_message_callback=callback, auto_ack=True)

print('[*] waiting for messages in initial_syllabus_key_queue, to exit express CTRL+C')
channel.start_consuming()
