class TextReader:
    def __init__(self,file_path:str):
        self.file_path = file_path

    def read(self) -> str:
        try:
            with open(self.file_path,"r",encoding = "utf-8") as file:
                content = file.read()

            # print(f"已读取{self.file_path}的内容")

            return content

        except Exception as e:
            print(f"读取{self.file_path}的内容时发生错误")
            print(f"错误信息:{e}")