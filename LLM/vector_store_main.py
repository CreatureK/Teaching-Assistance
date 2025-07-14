from core.vector_store import VectorStore
from processors.text_processor import TextProcessor
from processors.video_processor import VideoProcessor
import os

def main():
    # 初始化组件
    vector_store = VectorStore(persist_directory="vector_database_db")
    text_processor = TextProcessor()
    video_processor = VideoProcessor()

    # 处理PDF文件
    pdf_folder = "pdfs"
    if os.path.exists(pdf_folder):
        pdf_files = [os.path.join(pdf_folder, f) for f in os.listdir(pdf_folder) if f.endswith('.pdf')]
        pdf_metadata = [{"source": os.path.basename(f), "type": "pdf"} for f in pdf_files]
        
        pdf_texts = []
        for pdf_file in pdf_files:
            if not os.path.isfile(pdf_file):
                continue
            text = text_processor.read_pdf(pdf_file)
            if text.strip():
                pdf_texts.append(text)
                
        if pdf_texts:
            vector_store.add_documents(texts=pdf_texts, metadata=pdf_metadata)
    # 存入 md 文件请启用以下代码
     # 指定包含 .md 文件的文件夹路径
    md_folder = "knowledge-base/data-structure"
    
     # 获取文件夹中所有 .md 文件
    md_files = [os.path.join(md_folder, f) for f in os.listdir(md_folder) if f.endswith('.md')]
    metadata = [{"source": os.path.basename(f)} for f in md_files]

    print(f"[VectorStore] 准备读取 {len(md_files)} 个 .md 文件")

# 读取所有 .md 文件内容并添加到数据库
    texts = []
    for md_file in md_files:
        if not os.path.isfile(md_file):
            print(f"[VectorStore] 文件不存在: {md_file}")
            continue

        print(f"[VectorStore] 正在读取Markdown文件: {md_file}")
        text = text_processor.read_markdown(md_file)
        print(f"[VectorStore] 已完成读取Markdown文件: {md_file}，共读取 {len(text)} 个字符")

        if not text.strip():
            print(f"[VectorStore] 文本空白，跳过: {md_file}")
            continue

        texts.append(text)

    # 处理视频文件
    video_folder = "videos"
    if os.path.exists(video_folder):
        video_files = [os.path.join(video_folder, f) for f in os.listdir(video_folder) if f.endswith('.mp4')]
        video_metadata = [{"source": os.path.basename(f), "type": "video"} for f in video_files]
        
        video_texts = []
        for video_file in video_files:
            if not os.path.isfile(video_file):
                continue
            text = video_processor.process_video(video_file)
            if text.strip():
                video_texts.append(text)
                
        if video_texts:
            vector_store.add_documents(texts=video_texts, metadata=video_metadata)

if __name__ == "__main__":
    main()