<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <!-- CSRF 保护 -->
    <meta name="_csrf" content="${_csrf.token}" />
    <meta name="_csrf_header" content="${_csrf.headerName}" />
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>课程大纲 - 教学辅助系统</title>
    
    <!-- 引入jQuery -->
    <script src="${pageContext.request.contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    
    <!-- 引入Element UI -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/element-ui/theme-chalk/index.css">
    <script src="${pageContext.request.contextPath}/resources/vue/vue.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/element-ui/index.js"></script>
    
    <!-- 引入Editor.md -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/editor.md/css/editormd.min.css">
    
    <style>
        /* 保留原有样式 */
        body {
            font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f7fa;
            color: #333;
        }
        
        .container {
            max-width: 1500px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff;
            min-height: 100vh;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #ebeef5;
        }
        
        .header h2 {
            margin: 0;
            color: #303133;
            font-weight: 500;
        }
        
        .back-button {
            background-color: transparent;
            border: none;
            color: #409EFF;
            font-size: 20px;
            cursor: pointer;
            padding: 5px 10px;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        
        .back-button:hover {
            background-color: rgba(64, 158, 255, 0.1);
        }
        
        .header-actions {
            display: flex;
            gap: 10px;
        }
        
        .ai-btn {
            display: flex;
            align-items: center;
            background-color: #67C23A;
            color: white;
            border: none;
            border-radius: 4px;
            padding: 8px 16px;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s;
        }
        
        .ai-btn:hover {
            background-color: #5daf34;
            opacity: 0.9;
        }
        
        .ai-icon {
            margin-right: 5px;
        }
        
        .editor-container {
            margin-top: 20px;
            border: 1px solid #dcdfe6;
            border-radius: 4px;
            overflow: hidden;
        }
        
        .editor-toolbar {
            display: flex;
            justify-content: flex-end;
            padding: 10px;
            background-color: #f5f7fa;
            border-bottom: 1px solid #dcdfe6;
        }
        
        .btn {
            padding: 8px 16px;
            border-radius: 4px;
            border: 1px solid #dcdfe6;
            background-color: #fff;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s;
            margin-left: 10px;
        }
        
        .btn-primary {
            background-color: #409EFF;
            color: white;
            border-color: #409EFF;
        }
        
        .btn-primary:hover {
            background-color: #66b1ff;
            border-color: #66b1ff;
        }
        
        .btn-secondary {
            background-color: #f4f4f5;
            color: #606266;
        }
        
        .btn-secondary:hover {
            color: #409EFF;
            border-color: #c6e2ff;
            background-color: #ecf5ff;
        }
        
        .editormd {
            margin: 0;
        }
        
        .loading {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100px;
            color: #909399;
        }
        
        .content-container {
            display: flex;
            margin-top: 20px;
            gap: 20px;
            height: calc(100vh - 200px);
        }
        
        .chapter-sidebar {
            width: 300px;
            background: #fff;
            border-radius: 4px;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
            padding: 15px;
            overflow-y: auto;
            border: 1px solid #ebeef5;
        }
        
        .chapter-sidebar h3 {
            margin-top: 0;
            padding-bottom: 10px;
            border-bottom: 1px solid #ebeef5;
            color: #303133;
        }
        
        .chapter-item {
            margin-bottom: 10px;
            border: 1px solid #ebeef5;
            border-radius: 4px;
            overflow: hidden;
        }
        
        .chapter-header {
            background-color: #f5f7fa;
            padding: 10px 15px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: space-between;
            transition: background-color 0.3s;
        }
        
        .chapter-header:hover {
            background-color: #e6f7ff;
        }
        
        .chapter-title {
            font-weight: 500;
            flex: 1;
            color: #303133;
        }
        
        .chapter-duration {
            font-size: 12px;
            color: #fff;
            background-color: #409EFF;
            padding: 2px 8px;
            border-radius: 10px;
            margin-left: 10px;
            white-space: nowrap;
        }
        
        .chapter-content {
            padding: 10px 15px;
            border-top: 1px solid #ebeef5;
            background-color: #fff;
        }
        
        .learning-objectives {
            margin-top: 5px;
        }
        
        .objective-item {
            position: relative;
            padding: 4px 0 4px 15px;
            font-size: 13px;
            color: #606266;
            line-height: 1.5;
        }
        
        .objective-item:before {
            content: '•';
            position: absolute;
            left: 0;
            color: #409EFF;
            font-weight: bold;
        }
        
        .no-objectives {
            color: #909399;
            font-size: 13px;
            text-align: center;
            padding: 10px 0;
        }
        
        .empty-tip {
            color: #909399;
            text-align: center;
            padding: 20px 0;
        }
        
        .editor-container {
            flex: 1;
            display: flex;
            flex-direction: column;
            min-width: 0; /* 防止flex item溢出 */
        }
        
        #outlineEditor {
            flex: 1;
            display: flex;
            flex-direction: column;
        }
        
        .editormd {
            flex: 1;
            margin: 0;
            border: 1px solid #dcdfe6;
            border-radius: 4px;
            overflow: hidden;
        }
    </style>
</head>
<body>
    <div class="container" id="app">
        <div class="header">
            <button class="back-button" @click="goBack">← 返回</button>
            <h2>课程大纲 - <span id="courseTitle">${courseTitle}</span></h2>
            <div class="header-actions">
                <button class="ai-btn" @click="showAIDialog = true">
                    <span class="ai-icon">✨</span>
                    AI生成
                </button>
                <button class="btn btn-secondary" @click="saveDraft">暂存</button>
                <button class="btn btn-primary" @click="saveSyllabus">保存</button>
            </div>
        </div>
        
        <div class="content-container">
            <!-- 章节侧边栏 -->
            <div class="chapter-sidebar">
                <h3>课程大纲</h3>
                <div v-if="chapters.length === 0" class="empty-tip">
                    <p>暂无章节数据，请先编辑大纲内容</p>
                </div>
                <div v-else>
                    <div v-for="(chapter, index) in chapters" :key="index" class="chapter-item">
                        <div class="chapter-header" @click="toggleChapter(chapter)">
                            <span class="chapter-title">{{ chapter.title }}</span>
                            <span class="chapter-duration" v-if="chapter.duration">{{ chapter.duration }}</span>
                        </div>
                        <div class="chapter-content" v-if="chapter.expanded">
                            <div v-if="chapter.objectives && chapter.objectives.length > 0" class="learning-objectives">
                                <div class="objective-item" v-for="(obj, idx) in chapter.objectives" :key="idx">
                                    {{ obj }}
                                </div>
                            </div>
                            <div v-else class="no-objectives">
                                暂无学习目标
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- 编辑器区域 -->
            <div class="editor-container">
                <div id="outlineEditor">
                    <textarea style="display:none;" name="courseOutline">${syllabus.content}</textarea>
                </div>
            </div>
        </div>
        
        <!-- AI生成对话框 -->
        <el-dialog
            title="AI生成课程大纲"
            :visible.sync="showAIDialog"
            width="50%">
            <el-form :model="aiForm" label-width="100px">
                <el-form-item label="课程ID">
                    <el-input v-model="aiForm.courseId" disabled></el-input>
                </el-form-item>
                <el-form-item label="课程名称">
                    <el-input v-model="aiForm.courseTitle" placeholder="请输入课程名称"></el-input>
                </el-form-item>
                <el-form-item label="课程介绍">
                    <el-input
                        type="textarea"
                        :rows="4"
                        placeholder="请输入课程介绍"
                        v-model="aiForm.courseIntroduction">
                    </el-input>
                </el-form-item>
                <el-form-item label="教学要求">
                    <el-input
                        type="textarea"
                        :rows="4"
                        placeholder="请输入教学要求"
                        v-model="aiForm.teachingTarget">
                    </el-input>
                </el-form-item>
                <el-form-item label="其他要求">
                    <el-input
                        type="textarea"
                        :rows="4"
                        placeholder="请输入其他要求"
                        v-model="aiForm.request">
                    </el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="showAIDialog = false">取 消</el-button>
                <el-button type="primary" @click="generateWithAI" :loading="aiLoading">生 成</el-button>
            </span>
        </el-dialog>
    </div>
    
    <script>
    new Vue({
        el: '#app',
        data() {
            return {
                courseId: '${param.courseId}',
                courseTitle: document.getElementById('courseTitle') ? document.getElementById('courseTitle').textContent : '未命名课程',
                syllabus: {
                    content: '${not empty courseOutline ? fn:replace(fn:escapeXml(courseOutline), "'", "\\'") : ""}'
                },
                chapters: [],
                showAIDialog: false,
                aiLoading: false,
                aiForm: {
                    courseId: '${param.courseId}',
                    courseTitle: '',
                    courseIntroduction: '',
                    teachingTarget: '',
                    request: ''
                }
            }
        },
        methods: {
            // 返回上一页
            goBack() {
                window.history.back();
            },
            
            // 使用AI生成大纲
            generateWithAI() {
                this.aiLoading = true;
                
                // 准备请求数据
                const requestData = {
                    courseId: this.aiForm.courseId,
                    courseTitle: this.aiForm.courseTitle,
                    courseIntroduction: this.aiForm.courseIntroduction,
                    teachingTarget: this.aiForm.teachingTarget,
                    request: this.aiForm.request
                };
                
                // 调用后端API
                fetch('/api/initial-syllabus/generate', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    body: JSON.stringify(requestData)
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // 更新编辑器内容
                        if (window.editor_outlineEditor) {
                            window.editor_outlineEditor.setMarkdown(data.data.content || '');
                            this.$message.success('AI生成成功！');
                            this.showAIDialog = false;
                        }
                    } else {
                        this.$message.error('生成失败：' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    this.$message.error('生成过程中出错：' + error.message);
                })
                .finally(() => {
                    this.aiLoading = false;
                });
            },
            
            // 保存草稿
            saveDraft() {
                this.saveSyllabus(true);
            },
            
            // 切换章节展开状态
            toggleChapter(chapter) {
                this.$set(chapter, 'expanded', !chapter.expanded);
            },
            
            // 解析Markdown内容为章节结构
            parseChapters(content) {
                if (!content) return [];
                
                const lines = content.split('\n');
                const chapters = [];
                let currentChapter = null;
                
                lines.forEach(line => {
                    // 匹配章节标题 (## 或 ###)
                    const chapterMatch = line.match(/^#{2,3}\s+(.+?)(\s*\((.+?)\))?$/);
                    if (chapterMatch) {
                        const title = chapterMatch[1].trim();
                        const duration = chapterMatch[3] ? chapterMatch[3].trim() : '';
                        
                        // 如果是二级标题 (##)，创建新章节
                        if (line.startsWith('## ')) {
                            currentChapter = {
                                title: title,
                                duration: duration,
                                objectives: [],
                                expanded: true
                            };
                            chapters.push(currentChapter);
                        } 
                        // 如果是三级标题 (###)，添加到当前章节的目标中
                        else if (currentChapter) {
                            currentChapter.objectives.push(title);
                        }
                    }
                    // 匹配列表项作为学习目标
                    else if (currentChapter && line.trim().startsWith('- ')) {
                        const objective = line.replace(/^-\s*/, '').trim();
                        if (objective) {
                            currentChapter.objectives.push(objective);
                        }
                    }
                });
                
                return chapters;
            },
            
            // 保存大纲
            saveSyllabus(isDraft = false) {
                const content = window.editor_outlineEditor ? window.editor_outlineEditor.getMarkdown() : '';
                // 更新章节数据
                this.chapters = this.parseChapters(content);
                
                // 获取CSRF令牌
                const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
                
                // 准备请求头
                const headers = {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest',
                    'X-CSRF-TOKEN': token
                };
                
                // 添加用户ID请求头（如果存在）
                const userId = '${sessionScope.userId}';
                if (userId) {
                    headers['userId'] = userId;
                }
                
                fetch('/teacher/syllabus/' + this.courseId + '/save', {
                    method: 'POST',
                    headers: headers,
                    body: JSON.stringify({
                        courseId: this.courseId,
                        content: content,
                        status: isDraft ? 0 : 1 // 0表示草稿，1表示已发布
                    })
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('HTTP error, status = ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data && data.success) {
                        this.$message.success(isDraft ? '草稿保存成功！' : '课程大纲保存成功！');
                    } else {
                        const errorMsg = data && data.message ? data.message : '保存失败';
                        this.$message.error(errorMsg);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    this.$message.error('保存过程中出错：' + (error.message || '未知错误'));
                });
            }
        },
        watch: {
            'syllabus.content': {
                immediate: true,
                handler(newVal) {
                    if (newVal) {
                        this.chapters = this.parseChapters(newVal);
                    }
                }
            }
        },
        
        mounted() {
            // 解析初始内容
            this.chapters = this.parseChapters(this.syllabus.content);
            
            // 初始化编辑器
            window.editor_outlineEditor = editormd("outlineEditor", {
                width: "100%",
                height: "calc(100vh - 200px)",
                path: "${pageContext.request.contextPath}/resources/lib/editor.md/lib/",
                saveHTMLToTextarea: true,
                placeholder: "请输入课程大纲内容...",
                imageUpload: true,
                imageFormats: ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                imageUploadURL: "${pageContext.request.contextPath}/upload/image",
                toolbarIcons: function() {
                    return [
                        "undo", "redo", "|",
                        "bold", "del", "italic", "quote", "|",
                        "h1", "h2", "h3", "h4", "h5", "h6", "|",
                        "list-ul", "list-ol", "hr", "|",
                        "link", "image", "code", "preformatted-text", "code-block", "table", "|",
                        "watch", "preview", "fullscreen", "search"
                    ];
                }
            });
            
            // 加载课程大纲
            if (this.courseId) {
                fetch(`/teacher/syllabus/course/${this.courseId}/syllabus`, {
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                })
                .then(response => response.json())
                .then(data => {
                    if (data && data.content) {
                        this.syllabus = data;
                        if (window.editor_outlineEditor) {
                            window.editor_outlineEditor.setMarkdown(data.content);
                        }
                    }
                })
                .catch(error => {
                    console.error('Error loading syllabus:', error);
                });
            }
        }
    });
    </script>
</body>
</html>