import cv2
import numpy as np
import pytesseract
import os
from .audio_processor import AudioProcessor

class VideoProcessor:
    def __init__(self):
        self.audio_processor = AudioProcessor()
        # 设置Tesseract
        pytesseract.pytesseract.tesseract_cmd = '/opt/homebrew/bin/tesseract'
        os.environ['TESSDATA_PREFIX'] = '/opt/homebrew/share/tessdata/'

    def process_video(self, video_path: str) -> str:
        """处理视频内容"""
        print(f"\n[VideoProcessor] ===== 开始处理视频: {video_path} =====")
        
        # 1. 处理音频
        audio_text = self._process_audio(video_path)
        
        # 2. 处理视频帧
        frame_texts = self._process_frames(video_path)
        
        # 3. 合并结果
        combined_text = f"""
视频音频内容：
{audio_text}

视频画面内容：
{frame_texts}
        """.strip()
        
        print(f"[VideoProcessor] ===== 视频处理完成 =====\n")
        return combined_text

    def _process_audio(self, video_path: str) -> str:
        """处理视频音频"""
        audio_path = "temp_audio.wav"
        self.audio_processor.extract_audio_from_video(video_path, audio_path)
        text = self.audio_processor.transcribe_audio(audio_path)
        
        if os.path.exists(audio_path):
            os.remove(audio_path)
        
        return text

    def _process_frames(self, video_path: str) -> str:
        """处理视频帧"""
        keyframes = self._extract_keyframes(video_path)
        frame_texts = []
        
        for frame_id, frame in keyframes:
            frame_text = self._recognize_frame(frame)
            if frame_text.strip():
                frame_texts.append(f"第{frame_id}帧文本：{frame_text}")
                
        return "\n".join(frame_texts)

    def _extract_keyframes(self, video_path: str, threshold: float = 30.0) -> list:
        """提取视频关键帧"""
        cap = cv2.VideoCapture(video_path)
        if not cap.isOpened():
            print(f"[VideoProcessor] 无法打开视频文件: {video_path}")
            return []
        
        keyframes = []
        prev_gray = None
        frame_id = 0
        
        while True:
            ret, frame = cap.read()
            if not ret:
                break

            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            if prev_gray is None:
                keyframes.append((frame_id, frame))
                prev_gray = gray
            else:
                diff = cv2.absdiff(gray, prev_gray)
                mean_diff = np.mean(diff)
                if mean_diff > threshold:
                    keyframes.append((frame_id, frame))
                    prev_gray = gray
            frame_id += 1

        cap.release()
        return keyframes

    def _recognize_frame(self, frame) -> str:
        """识别帧内文字"""
        try:
            text = pytesseract.image_to_string(frame, lang="chi_sim")
            return text.strip()
        except Exception as e:
            print(f"[VideoProcessor] OCR识别错误: {str(e)}")
            return ""
