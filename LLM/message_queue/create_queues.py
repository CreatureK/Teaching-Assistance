import pika

# 连接到 RabbitMQ 服务器
connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

# 声明所需的队列
queues = [
    'course_content_queue',
    'teaching_content_queue',
    'teaching_target_queue',
    'initial_syllabus_queue',
    'generated_initial_syllabus_queue',
    'final_syllabus_queue',
    'generated_final_syllabus_queue',
    'knowledge_extraction_queue',
    'knowledge_candidate_queue',
    'lecture_markdown_queue'
]

for queue in queues:
    channel.queue_declare(queue=queue)
    print(f"队列 '{queue}' 已创建。")

# 关闭连接
connection.close() 