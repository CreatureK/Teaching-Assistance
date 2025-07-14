import whisper
import os
import subprocess

class AudioProcessor:
    def __init__(self):
        self.whisper_model = whisper.load_model("base")

    def extract_audio_from_video(self, video_path: str, audio_path: str):
        """从视频中提取音频"""
        command = [
            "ffmpeg",
            "-i", video_path,
            "-vn",
            "-acodec", "pcm_s16le",
            "-ar", "16000",
            "-ac", "1",
            audio_path
        ]
        subprocess.run(command, check=True)

    def transcribe_audio(self, audio_path: str) -> str:
        """音频转写为文本"""
        print("[AudioProcessor] 开始音频转写")
        result = self.whisper_model.transcribe(audio_path, language="zh")
        text = result.get("text", "")
        print(f"[AudioProcessor] 音频转写完成，获得文本长度: {len(text)}")
        return text
