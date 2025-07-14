import chromadb
import hashlib
import time
import os
from typing import List, Dict

class VectorStore:
    def __init__(self, persist_directory: str = "../vector_database_db"):
        self.persist_directory = os.path.abspath(persist_directory)
        os.makedirs(self.persist_directory, exist_ok=True)
        print(f"[VectorStore] 初始化向量数据库: {self.persist_directory}")

        self.client = chromadb.PersistentClient(path=self.persist_directory)
        self.collection = self.client.get_or_create_collection(
            name="course_materials",
            metadata={"hnsw:space": "cosine"}
        )

    def add_documents(self, texts: List[str], metadata: List[Dict]) -> None:
        start_time = time.time()
        added_count = 0

        for text, meta in zip(texts, metadata):
            if not text.strip():
                continue

            chunks = self._chunk_document(text)
            for i, chunk in enumerate(chunks):
                chunk_id = hashlib.md5(f"{chunk}{i}".encode()).hexdigest()
                chunk_meta = {**meta, "chunk_id": i, "total_chunks": len(chunks)}

                try:
                    existing = self.collection.get(ids=[chunk_id])
                    if existing['ids']:
                        print(f"[VectorStore] 跳过已存在的文档块: {chunk_id}")
                        continue

                    self.collection.add(
                        documents=[chunk],
                        metadatas=[chunk_meta],
                        ids=[chunk_id]
                    )
                    added_count += 1
                    print(f"[VectorStore] 添加文档块 {i + 1}/{len(chunks)}")

                except Exception as e:
                    print(f"[VectorStore] 添加文档出错: {str(e)}")

        print(f"[VectorStore] 完成添加 {added_count} 个文档块，用时 {time.time() - start_time:.2f}s")
        self._verify_persistence()

    def _chunk_document(self, text: str, chunk_size: int = 1000) -> List[str]:
        """将文档分块"""
        chunks = []
        sentences = text.split('。')
        current_chunk = []
        current_size = 0

        for sentence in sentences:
            if current_size + len(sentence) > chunk_size and current_chunk:
                chunks.append('。'.join(current_chunk))
                current_chunk = []
                current_size = 0
            current_chunk.append(sentence)
            current_size += len(sentence)

        if current_chunk:
            chunks.append('。'.join(current_chunk))
        return chunks

    def _verify_persistence(self):
        """验证数据持久化状态"""
        try:
            count = self.collection.count()
            if count > 0:
                print(f"[VectorStore] 验证成功: 数据库包含 {count} 个文档")
            else:
                print("[VectorStore] 警告: 数据库为空")
        except Exception as e:
            print(f"[VectorStore] 验证失败: {str(e)}")

    def search_relevant(self, query: str, n_results: int = 5) -> List[str]:
        """检索相似文档"""
        try:
            results = self.collection.query(
                query_texts=[query],
                n_results=min(n_results, self.collection.count())
            )
            return results.get('documents', [[]])[0]
        except Exception as e:
            print(f"[VectorStore] 搜索出错: {str(e)}")
            return []