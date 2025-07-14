from PyPDF2 import PdfReader
import os

class TextProcessor:
    @staticmethod
    def read_pdf(pdf_file_path: str) -> str:
        """读取PDF文件内容"""
        print(f"[TextProcessor] 开始读取PDF文件: {pdf_file_path}")
        text = ""
        with open(pdf_file_path, "rb") as f:
            reader = PdfReader(f)
            for page in reader.pages:
                text += page.extract_text() or ""
        print(f"[TextProcessor] PDF文件读取完成，共获取 {len(text)} 个字符")
        return text

    @staticmethod
    def read_markdown(md_file_path: str) -> str:
        """读取Markdown文件内容"""
        print(f"[TextProcessor] 开始读取Markdown文件: {md_file_path}")
        with open(md_file_path, "r", encoding="utf-8") as f:
            text = f.read()
        print(f"[TextProcessor] Markdown文件读取完成，共获取 {len(text)} 个字符")
        return text

    @staticmethod
    def remove_surrogate_chars(text: str) -> str:
        """移除无法编码的特殊字符"""
        return text.encode("utf-8", "ignore").decode("utf-8", "ignore")
