import pika
import json

def callback(ch, method, properties, body):
    # 判断消息内容类型并保存到相应文件
    try:
        data = json.loads(body.decode())
        if 'finalSyllabus' in data:
            with open('finalSyllabusRe.json', 'w', encoding='utf-8') as file:
                json.dump(data, file, ensure_ascii=False)
            print(f"[x] received and saved final syllabus to 'finalSyllabusRe.json'")
        elif 'knowledgePoint' in data:
            with open('knowledgePointRe.json', 'w', encoding='utf-8') as file:
                json.dump(data, file, ensure_ascii=False)
            print(f"[x] received and saved knowledge point to 'knowledgePointRe.json'")
    except json.JSONDecodeError:
        print("[x] received invalid JSON data.")

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.queue_declare(queue='final_syllabus_and_point_queue')

# 消费消息
channel.basic_consume(queue='final_syllabus_and_point_queue', on_message_callback=callback, auto_ack=True)

print('[*] waiting for messages in final_syllabus_and_point_queue, to exit express CTRL+C')
channel.start_consuming() 