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
    
    <%-- 引入外部CSS文件 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/course-outline.css">
    
    <!-- 引入jQuery -->
    <script src="${pageContext.request.contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    
    <!-- 引入Editor.md -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/editor.md/css/editormd.min.css">
</head>
<body>
    <!-- 加载中遮罩 -->
    <div id="loadingOverlay" class="loading-overlay">
        <div class="loading-spinner"></div>
        <div class="loading-text">处理中，请稍候...</div>
    </div>
    
    <div class="container">
        <div class="header">
            <button class="back-button" onclick="goBack()">← 返回</button>
            <h2>课程大纲 - <span id="courseTitle">${courseTitle}</span></h2>
            <div class="header-actions">
                <button class="ai-btn" onclick="showAIDialog()">
                    <span class="ai-icon">✨</span>
                    AI生成
                </button>
                <button class="btn btn-secondary" onclick="saveDraft()">暂存</button>
                <button class="btn btn-primary" onclick="saveSyllabus()">保存</button>
            </div>
        </div>
        
        <div class="content-container">
            <!-- 章节侧边栏 -->
            <div class="chapter-sidebar">
                <h3>课程大纲</h3>
                <div id="chapterList">
                    <div class="empty-tip">
                        <p>暂无章节数据，请先编辑大纲内容</p>
                    </div>
                </div>
            </div>
            
            <!-- 编辑器区域 -->
            <div class="editor-container">
                <div id="outlineEditor">
                    <textarea style="display:none;" name="courseOutline">${not empty courseOutline ? fn:escapeXml(courseOutline) : ''}</textarea>
                </div>
            </div>
        </div>
        
        <!-- AI生成对话框 -->
        <div id="aiDialog" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>AI生成课程大纲</h3>
                    <span class="close" onclick="hideAIDialog()">&times;</span>
                </div>
                <div class="modal-body">
                    <form id="aiForm" onsubmit="generateWithAI(event)">
                        <div class="form-group">
                            <label for="courseId">课程ID</label>
                            <input type="text" id="courseId" name="courseId" value="${param.courseId}" disabled>
                        </div>
                        <div class="form-group">
                            <label for="courseTitle">课程名称</label>
                            <input type="text" id="aiCourseTitle" name="courseTitle" placeholder="请输入课程名称">
                        </div>
                        <div class="form-group">
                            <label for="courseIntroduction">课程介绍</label>
                            <textarea id="courseIntroduction" name="courseIntroduction" rows="4" placeholder="请输入课程介绍"></textarea>
                        </div>
                        <div class="form-group">
                            <label for="teachingTarget">教学要求</label>
                            <textarea id="teachingTarget" name="teachingTarget" rows="4" placeholder="请输入教学要求"></textarea>
                        </div>
                        <div class="form-group">
                            <label for="request">其他要求</label>
                            <textarea id="request" name="request" rows="4" placeholder="请输入其他要求"></textarea>
                        </div>
                        <div class="form-actions">
                            <button type="button" class="btn btn-secondary" onclick="hideAIDialog()">取 消</button>
                            <button type="submit" class="btn btn-primary" id="generateBtn">生 成</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <%-- 引入jQuery --%>
    <script src="${pageContext.request.contextPath}/resources/lib/jquery/jquery-3.6.0.min.js"></script>
    <%-- 引入Editor.md --%>
    <script src="${pageContext.request.contextPath}/resources/lib/editor.md/editormd.min.js"></script>
    <%-- 引入自定义JS --%>
    <script src="${pageContext.request.contextPath}/resources/js/course-outline.js"></script>
    
    <script>
    // 初始化编辑器
    document.addEventListener('DOMContentLoaded', function() {
        // 设置CSRF令牌和用户ID
        const csrfHeaderName = '${_csrf.headerName}';
        const csrfToken = '${_csrf.token}';
        const userId = '${sessionScope.userId}';
        const courseId = '${param.courseId}';
        
        // 设置全局变量
        window.csrfHeaderName = csrfHeaderName;
        window.csrfToken = csrfToken;
        window.userId = userId;
        window.courseId = courseId;
        
        // 初始化编辑器
        if (typeof editormd !== 'undefined') {
            editor_outlineEditor = editormd('outlineEditor', {
                width: '100%',
                height: '100%',
                path: '${pageContext.request.contextPath}/resources/lib/editor.md/lib/',
                theme: 'default',
                previewTheme: 'default',
                editorTheme: 'base16-light',
                markdown: '${not empty courseOutline ? fn:escapeXml(courseOutline) : ""}',
                codeFold: true,
                saveHTMLToTextarea: true,
                searchReplace: true,
                htmlDecode: 'style,script,iframe|on*',
                toolbarIcons: 'full',
                emoji: true,
                taskList: true,
                tocm: true,
                tex: true,
                flowChart: true,
                sequenceDiagram: true,
                imageUpload: true,
                imageFormats: ['jpg', 'jpeg', 'gif', 'png', 'bmp', 'webp'],
                imageUploadURL: '${pageContext.request.contextPath}/upload/image'
            });
            
            // 设置编辑器的onload和onchange事件
            if (editor_outlineEditor) {
                editor_outlineEditor.on('load', function() {
                    console.log('Editor.md is loaded!');
                    // 初始解析章节
                    const content = editor_outlineEditor.getMarkdown();
                    chapters = parseChapters(content);
                    updateChapterList();
                });
                
                editor_outlineEditor.on('change', function() {
                    const content = editor_outlineEditor.getMarkdown();
                    chapters = parseChapters(content);
                    updateChapterList();
                });
            }
        }
        
        // 设置课程名称到AI对话框
        const courseTitle = document.getElementById('courseTitle');
        const aiCourseTitle = document.getElementById('aiCourseTitle');
        if (courseTitle && aiCourseTitle) {
            aiCourseTitle.value = courseTitle.textContent || '';
        }
        
        // 绑定保存按钮事件
        document.getElementById('saveDraftBtn')?.addEventListener('click', function() {
            saveSyllabus(true);
        });
        
        document.getElementById('saveSyllabusBtn')?.addEventListener('click', function() {
            saveSyllabus(false);
        });
        
        // 绑定AI生成相关事件
        document.getElementById('aiGenerateBtn')?.addEventListener('click', showAIDialog);
        document.getElementById('aiDialogClose')?.addEventListener('click', hideAIDialog);
        document.getElementById('aiForm')?.addEventListener('submit', generateWithAI);
        
        // 点击模态框外部关闭
        window.onclick = function(event) {
            const modal = document.getElementById('aiDialog');
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        };
    });
    </script>
</body>
</html>