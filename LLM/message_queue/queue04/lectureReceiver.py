import pika

def callback(ch, method, properties, body):
    # 保存到 lectureRe.md
    with open('lectureRe.md', 'w', encoding='utf-8') as file:
        file.write(body.decode())
    
    print(f"[x] received and saved lecture to 'lectureRe.md'")

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.queue_declare(queue='lecture_queue')

# 消费消息
channel.basic_consume(queue='lecture_queue', on_message_callback=callback, auto_ack=True)

print('[*] waiting for messages in lecture_queue, to exit express CTRL+C')
channel.start_consuming()
