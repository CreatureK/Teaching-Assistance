package com.java_web.backend.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/course")
public class MarkdownController {

    // 显示Markdown编辑器演示页面
    @GetMapping("/markdown/editor")
    public String showMarkdownEditor() {
        return "course/MarkdownDemo";
    }
    
    // 保存Markdown内容
    @PostMapping("/saveMarkdown")
    @ResponseBody
    public Map<String, Object> saveMarkdown(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpServletRequest request) {
        
        // 这里可以添加保存到数据库的逻辑
        System.out.println("标题: " + title);
        System.out.println("内容: " + content);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "保存成功");
        result.put("title", title);
        // 返回内容的HTML预览
        result.put("content", content);
        
        return result;
    }
    
    // 处理图片上传
    @PostMapping("/upload/image")
    @ResponseBody
    public Map<String, Object> uploadImage(@RequestParam(value = "editormd-image-file", required = false) MultipartFile file,
                                         HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        if (file == null || file.isEmpty()) {
            result.put("success", 0);
            result.put("message", "请选择要上传的图片");
            return result;
        }
        
        try {
            // 获取上传目录（相对于webapp）
            String uploadDir = "/upload/images/";
            // 获取实际物理路径
            String realPath = request.getServletContext().getRealPath(uploadDir);
            
            // 如果目录不存在则创建
            File dir = new File(realPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 生成新的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExt;
            
            // 保存文件
            Path filePath = Paths.get(realPath, newFilename);
            file.transferTo(filePath.toFile());
            
            // 构造访问URL
            String contextPath = request.getContextPath();
            String imageUrl = contextPath + uploadDir + newFilename;
            
            // 返回结果（Editor.md期望的格式）
            result.put("success", 1);
            result.put("message", "上传成功");
            result.put("url", imageUrl);
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", 0);
            result.put("message", "上传失败: " + e.getMessage());
        }
        
        return result;
    }
    
    // 获取Markdown内容的HTML预览
    @PostMapping("/preview")
    @ResponseBody
    public Map<String, Object> previewMarkdown(@RequestParam("content") String content) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("html", content);
        return result;
    }
}
