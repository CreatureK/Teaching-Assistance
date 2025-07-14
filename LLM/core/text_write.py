import logging

class TextWriter:
    def __init__(self,content:str,file_path:str):
        self.content = content
        self.file_path = file_path


    def write(self) :
        try:
            with open(self.file_path,'w',encoding='utf-8') as file:
                file.write(self.content)

            logging.info(f"已将指定内容写入{self.file_path}")

        except Exception as e:
            print(f"将指定内容写入{self.file_path}时出错")
            print(f"错误信息{e}")
