import pika
import json

def callback_teaching_content(ch, method, properties, body):
    data = json.loads(body.decode())
    teaching_content = data['teachingContent'][0]
    
    # 保存到 teachingContent.json
    with open('teachingContentRe.json', 'w', encoding='utf-8') as file:
        json.dump({"teachingContent": [teaching_content]}, file, ensure_ascii=False)
    
    print(f"[x] received and saved teaching content to 'queue01/teachingContent.json'")

def callback_teaching_target(ch, method, properties, body):
    data = json.loads(body.decode())
    teaching_target = data['teachingTarget'][0]
    
    # 保存到 teachingTarget.json
    with open('teachingTargetRe.json', 'w', encoding='utf-8') as file:
        json.dump({"teachingTarget": [teaching_target]}, file, ensure_ascii=False)
    
    print(f"[x] received and saved teaching target to 'queue01/teachingTarget.json'")

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.queue_declare(queue='teaching_content_queue')
channel.queue_declare(queue='teaching_target_queue')

# 消费消息
channel.basic_consume(queue='teaching_content_queue', on_message_callback=callback_teaching_content, auto_ack=True)
channel.basic_consume(queue='teaching_target_queue', on_message_callback=callback_teaching_target, auto_ack=True)

print('[*] waiting for messages in teaching_content_queue and teaching_target_queue, to exit express CTRL+C')
channel.start_consuming() 