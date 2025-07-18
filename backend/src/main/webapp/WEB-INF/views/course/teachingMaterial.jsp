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
    <title>教学讲义 - 教学辅助系统</title>
    
    <!-- 引入jQuery -->
    <script src="${pageContext.request.contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    
    <!-- 引入Element UI -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/element-ui/theme-chalk/index.css">
    <script src="${pageContext.request.contextPath}/resources/vue/vue.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/element-ui/index.js"></script>
    
    <!-- 引入Editor.md -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/editor.md/css/editormd.min.css">
    
    <style>
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
        
        .btn {
            padding: 8px 16px;
            border-radius: 4px;
            border: 1px solid #dcdfe6;
            background-color: #fff;
            color: #606266;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s;
        }
        
        .btn:hover {
            opacity: 0.9;
        }
        
        .btn-primary {
            background-color: #409EFF;
            border-color: #409EFF;
            color: #fff;
        }
        
        .btn-secondary {
            background-color: #909399;
            border-color: #909399;
            color: #fff;
        }
        
        .content-container {
            display: flex;
            margin-top: 20px;
            gap: 20px;
            height: calc(100vh - 200px);
        }
        
        .sidebar {
            width: 300px;
            background: #fff;
            border-radius: 4px;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
            padding: 15px;
            overflow-y: auto;
            border: 1px solid #ebeef5;
        }
        
        .sidebar-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px solid #ebeef5;
        }
        
        .sidebar h3 {
            margin: 0;
            font-size: 16px;
            color: #303133;
        }
        
        .toggle-sidebar {
            background: none;
            border: none;
            font-size: 18px;
            cursor: pointer;
            color: #909399;
        }
        
        .toc {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        
        .toc-item {
            margin-bottom: 5px;
        }
        
        .toc-link {
            display: block;
            padding: 6px 10px;
            color: #606266;
            text-decoration: none;
            border-radius: 4px;
            transition: all 0.3s;
        }
        
        .toc-link:hover, .toc-link.active {
            background-color: #f0f7ff;
            color: #409EFF;
        }
        
        .toc-link.h2 {
            font-weight: 500;
            padding-left: 10px;
        }
        
        .toc-link.h3 {
            font-weight: normal;
            padding-left: 25px;
            font-size: 14px;
        }
        
        .editor-container {
            flex: 1;
            display: flex;
            flex-direction: column;
            min-width: 0;
        }
        
        .editormd {
            flex: 1;
            margin: 0;
            border: 1px solid #dcdfe6;
            border-radius: 4px;
            overflow: hidden;
        }
        
        .empty-tip {
            color: #909399;
            text-align: center;
            padding: 20px 0;
        }
        
        @media (max-width: 992px) {
            .content-container {
                flex-direction: column;
                height: auto;
            }
            
            .sidebar {
                width: 100%;
                margin-bottom: 20px;
                max-height: 300px;
            }
        }
    </style>
</head>
<body>
    <div class="container" id="app">
        <div class="header">
            <button class="back-button" @click="goBack">← 返回</button>
            <h2>教学讲义 - <span id="courseTitle">${courseName}</span></h2>
            <div class="header-actions">
                <button class="ai-btn" @click="showAIDialog = true">
                    <span class="ai-icon">✨</span>
                    AI生成
                </button>
                <button class="btn btn-secondary" @click="saveDraft">暂存</button>
                <button class="btn btn-primary" @click="saveMaterial">保存</button>
            </div>
        </div>
        
        <div class="content-container">
            <!-- 侧边栏目录 -->
            <div class="sidebar" :class="{'hidden': !showSidebar}">
                <div class="sidebar-header">
                    <h3>内容目录</h3>
                    <button class="toggle-sidebar" @click="toggleSidebar">
                        {{ showSidebar ? '◀' : '▶' }}
                    </button>
                </div>
                <div v-if="toc.length === 0" class="empty-tip">
                    <p>暂无目录</p>
                </div>
                <ul v-else class="toc">
                    <li v-for="(item, index) in toc" :key="index" class="toc-item">
                        <a :href="'#' + item.id" 
                           class="toc-link" 
                           :class="['h' + item.level, { 'active': currentHeading === item.id }]"
                           @click="scrollToHeading(item.id, $event)">
                            {{ item.text }}
                        </a>
                    </li>
                </ul>
            </div>
            
            <!-- 编辑器区域 -->
            <div class="editor-container">
                <div id="materialEditor">
                    <textarea style="display:none;" name="materialContent">${not empty material.content ? fn:replace(fn:escapeXml(material.content), "'", "\\'") : ''}</textarea>
                </div>
            </div>
        </div>
        
        <!-- AI生成对话框 -->
        <el-dialog
            title="AI生成教学讲义"
            :visible.sync="showAIDialog"
            width="50%">
            <el-form :model="aiForm" label-width="100px">
                <el-form-item label="课程ID">
                    <el-input v-model="aiForm.courseId" disabled></el-input>
                </el-form-item>
                <el-form-item label="课程名称">
                    <el-input v-model="aiForm.courseName" placeholder="请输入课程名称" :disabled="aiLoading"></el-input>
                </el-form-item>
                <el-form-item label="生成要求">
                    <el-input
                        type="textarea"
                        :rows="4"
                        placeholder="请输入对讲义内容的详细要求，例如：需要包含哪些章节、重点内容等"
                        v-model="aiForm.prompt"
                        :disabled="aiLoading">
                    </el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="showAIDialog = false" :disabled="aiLoading">取 消</el-button>
                <el-button type="primary" @click="generateWithAI" :loading="aiLoading">生 成</el-button>
            </span>
        </el-dialog>
    </div>
    
    <script src="${pageContext.request.contextPath}/resources/lib/editor.md/editormd.min.js"></script>
    <script>
    new Vue({
        el: '#app',
        data() {
            return {
                courseId: '${param.courseId}',
                courseName: '${courseName}',
                material: {
                    courseId: '${param.courseId}',
                    content: ''
                },
                showSidebar: true,
                showAIDialog: false,
                loading: false,
                aiLoading: false,
                aiForm: {
                    courseId: '${param.courseId}',
                    courseName: '${courseName}',
                    prompt: ''
                },
                toc: [],
                currentHeading: '',
                observer: null
            }
        },
        methods: {
            // 获取讲义数据
            fetchMaterial() {
                if (this.loading) return;
                
                this.loading = true;
                const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                
                return fetch(`/teacher/material/${this.courseId}`, {
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest',
                        'X-CSRF-TOKEN': token,
                        'userId': '${sessionScope.userId}'
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            throw new Error(text || '获取讲义失败');
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    if (data && data.content) {
                        this.material.content = data.content;
                        if (window.editor_materialEditor) {
                            window.editor_materialEditor.setMarkdown(data.content);
                        }
                    }
                })
                .catch(error => {
                    console.error('获取讲义失败:', error);
                    this.$message.error('获取讲义失败: ' + (error.message || '未知错误'));
                })
                .finally(() => {
                    this.loading = false;
                });
            },
            
            // 返回上一页
            goBack() {
                window.history.back();
            },
            
            // 切换侧边栏显示/隐藏
            toggleSidebar() {
                this.showSidebar = !this.showSidebar;
            },
            
            // 生成目录
            generateToc() {
                if (!window.editor_materialEditor) return;
                
                const $preview = $('.editormd-preview');
                if ($preview.length === 0) return;
                
                const headings = $preview.find('h1, h2, h3, h4, h5, h6');
                const toc = [];
                
                headings.each(function() {
                    const $heading = $(this);
                    const id = $heading.attr('id');
                    const level = parseInt(this.tagName.substring(1));
                    const text = $heading.text();
                    
                    if (id && text) {
                        toc.push({ id, level, text });
                    }
                });
                
                this.toc = toc;
            },
            
            // 滚动到指定标题
            scrollToHeading(id, event) {
                if (event) {
                    event.preventDefault();
                }
                
                const element = document.getElementById(id);
                if (element) {
                    element.scrollIntoView({ behavior: 'smooth' });
                    this.currentHeading = id;
                }
            },
            
            // 监听滚动，更新当前激活的标题
            setupScrollSpy() {
                if (this.observer) {
                    this.observer.disconnect();
                }
                
                const $preview = $('.editormd-preview');
                if ($preview.length === 0) return;
                
                const headings = $preview.find('h1, h2, h3, h4, h5, h6').toArray();
                
                this.observer = new IntersectionObserver(
                    (entries) => {
                        entries.forEach(entry => {
                            if (entry.isIntersecting) {
                                this.currentHeading = entry.target.id;
                            }
                        });
                    },
                    { 
                        root: $preview[0],
                        threshold: 0.5
                    }
                );
                
                headings.forEach(heading => {
                    this.observer.observe(heading);
                });
            },
            
            // 使用AI生成讲义内容
            generateWithAI() {
                if (!this.aiForm.prompt.trim()) {
                    this.$message.warning('请输入生成要求');
                    return;
                }
                
                this.aiLoading = true;
                const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                
                fetch(`/teacher/material/${this.courseId}/generate`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest',
                        'X-CSRF-TOKEN': token,
                        'userId': '${sessionScope.userId}'
                    },
                    body: JSON.stringify({
                        prompt: this.aiForm.prompt
                    })
                })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            try {
                                const errorData = JSON.parse(text);
                                throw new Error(errorData.message || '生成失败');
                            } catch (e) {
                                throw new Error(text || '生成失败');
                            }
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    if (data && data.content) {
                        if (window.editor_materialEditor) {
                            window.editor_materialEditor.setMarkdown(data.content);
                            this.$message.success('生成成功！');
                            this.showAIDialog = false;
                            this.aiForm.prompt = ''; // 清空输入框
                        }
                    } else {
                        throw new Error('生成失败：返回数据格式错误');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    this.$message.error(error.message || '生成过程中出错');
                })
                .finally(() => {
                    this.aiLoading = false;
                });
            },
            
            // 保存草稿
            saveDraft() {
                this.saveMaterial(true);
            },
            
            // 保存讲义
            saveMaterial(isDraft = false) {
                if (this.loading) return;
                
                this.loading = true;
                const content = window.editor_materialEditor ? window.editor_materialEditor.getMarkdown() : '';
                const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                
                fetch(`/teacher/material/${this.courseId}/save`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest',
                        'X-CSRF-TOKEN': token,
                        'userId': '${sessionScope.userId}'
                    },
                    body: JSON.stringify({
                        courseId: this.courseId,
                        content: content,
                        isDraft: isDraft
                    })
                })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            try {
                                const errorData = JSON.parse(text);
                                throw new Error(errorData.message || '保存失败');
                            } catch (e) {
                                throw new Error(text || '保存失败');
                            }
                        });
                    }
                    return response.text();
                })
                .then(message => {
                    this.$message.success(message || (isDraft ? '草稿保存成功！' : '教学讲义保存成功！'));
                })
                .catch(error => {
                    console.error('Error:', error);
                    this.$message.error('保存过程中出错：' + (error.message || '未知错误'));
                })
                .finally(() => {
                    this.loading = false;
                });
            }
        },
        mounted() {
            // 初始化编辑器
            this.initEditor();
            
            // 加载讲义数据
            this.fetchMaterial().then(() => {
                // 数据加载完成后初始化目录
                if (window.editor_materialEditor) {
                    this.$nextTick(() => {
                        this.generateToc();
                        this.setupScrollSpy();
                    });
                }
            });
        },
        
        methods: {
            // 初始化编辑器
            initEditor() {
                const _this = this;
                window.editor_materialEditor = editormd("materialEditor", {
                    width: "100%",
                    height: "100%",
                    path: "${pageContext.request.contextPath}/resources/lib/editor.md/lib/",
                    saveHTMLToTextarea: true,
                    placeholder: "请输入教学讲义内容...",
                    imageUpload: true,
                    imageFormats: ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                    imageUploadURL: "${pageContext.request.contextPath}/upload/image",
                    onload: function() {
                        // 监听内容变化，更新目录
                        this.cm.on("change", function() {
                            _this.$nextTick(() => {
                                _this.generateToc();
                            });
                        });
                        
                        // 初始生成目录
                        _this.$nextTick(() => {
                            _this.generateToc();
                            _this.setupScrollSpy();
                        });
                    },
                    onpreviewscroll: function() {
                        _this.setupScrollSpy();
                    },
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
                
                // 监听窗口大小变化，调整布局
                this.handleResize = () => {
                    if (window.editor_materialEditor) {
                        window.editor_materialEditor.resize();
                    }
                };
                
                window.addEventListener('resize', this.handleResize);
            },
            
            // ... 其他方法 ...
        },
        
        beforeDestroy() {
            if (this.observer) {
                this.observer.disconnect();
            }
            if (this.handleResize) {
                window.removeEventListener('resize', this.handleResize);
            }
        }
    });
    </script>
</body>
</html>
