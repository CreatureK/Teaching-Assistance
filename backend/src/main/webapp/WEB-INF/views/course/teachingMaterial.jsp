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
    
    <!-- 引入Editor.md -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/editor.md/css/editormd.min.css">
    
    <!-- 引入自定义CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/teaching-material.css">
    
    <!-- 隐藏的CSRF Token和用户信息 -->
    <input type="hidden" id="csrfToken" value="${_csrf.token}" />
    <input type="hidden" id="csrfHeaderName" value="${_csrf.headerName}" />
    <input type="hidden" id="userId" value="${sessionScope.userId}" />
    <input type="hidden" id="courseId" value="${param.courseId}" />
</head>
<body>
    <div class="container">
        <div class="header">
            <button class="back-button" id="backButton">← 返回</button>
            <h2>教学讲义 - <span id="courseTitle">${courseName}</span></h2>
            <div class="header-actions">
                <button class="ai-btn" id="aiGenerateBtn">
                    <span class="ai-icon">✨</span>
                    AI生成
                </button>
                <button class="btn btn-primary" id="saveMaterialBtn">保存讲义</button>
            </div>
        </div>
        
        <div class="content-container">
            <!-- 侧边栏目录 -->
            <div class="sidebar" id="sidebar">
                <div class="sidebar-header">
                    <h3>内容目录</h3>
                    <button class="toggle-sidebar" id="toggleSidebar">◀</button>
                </div>
                <div id="tocContainer">
                    <div class="empty-tip">
                        <p>暂无目录</p>
                    </div>
                    <ul class="toc" style="display: none;"></ul>
                </div>
            </div>
            
            <!-- 编辑器区域 -->
            <div class="editor-container">
                <div id="materialEditor">
                    <textarea style="display:none;" name="materialContent">${not empty material.content ? fn:replace(fn:escapeXml(material.content), "'", "\\'") : ''}</textarea>
                </div>
            </div>
        </div>
        
        <!-- AI生成对话框 -->
        <div class="modal-overlay" id="aiDialog" style="display: none;">
            <div class="modal-dialog">
                <div class="modal-header">
                    <h3 class="modal-title">AI生成教学讲义</h3>
                    <button class="modal-close" id="aiDialogClose">&times;</button>
                </div>
                <div class="modal-body">
                    <form id="aiForm">
                        <div class="form-group">
                            <label for="aiCourseId">课程ID</label>
                            <input type="text" id="aiCourseId" class="form-control" value="${param.courseId}" disabled>
                        </div>
                        <div class="form-group">
                            <label for="aiCourseName">课程名称</label>
                            <input type="text" id="aiCourseName" class="form-control" value="${courseName}" disabled>
                        </div>
                        <div class="form-group">
                            <label for="aiPrompt">生成要求</label>
                            <textarea id="aiPrompt" class="form-control" rows="6" 
                                placeholder="请输入对讲义内容的详细要求，例如：需要包含哪些章节、重点内容等"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="cancelAiBtn">取 消</button>
                    <button type="button" class="btn btn-primary" id="generateBtn">生 成</button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 加载中遮罩 -->
    <div class="loading-overlay" id="loadingOverlay">
        <div class="loading-spinner"></div>
        <div class="loading-text">加载中，请稍候...</div>
    </div>
    
    <!-- 消息容器 -->
    <div id="messageContainer" class="message-container"></div>
    
    <!-- 引入Editor.md -->
    <script src="${pageContext.request.contextPath}/resources/lib/editor.md/editormd.min.js"></script>
    
    <!-- 引入自定义JS -->
    <script src="${pageContext.request.contextPath}/resources/js/teaching-material.js"></script>
</body>
</html>
