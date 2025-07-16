<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!DOCTYPE html>
  <html>

  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>课程管理 - 智教未来</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/courseManage.css">
  </head>

  <body>
    <!-- 引入头部组件 -->
    <jsp:include page="../common/Header.jsp" />

    <div class="course-manage">
      <div class="course-header">
        <h2 class="course-title">您已有的课程 <span id="courseCount">0</span></h2>
        <div class="course-actions">
          <button id="deleteBtn" class="delete-button">删除</button>
          <button id="confirmDeleteBtn" class="confirm-delete-button" style="display: none;">确认删除</button>
        </div>
      </div>

      <div class="course-grid" id="courseGrid">
        <!-- 添加按钮卡片 -->
        <div class="course-card add-card" id="addCourseBtn">
          <div class="add-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
          </div>
        </div>

        <!-- 课程卡片将通过JavaScript动态生成 -->
      </div>
    </div>

    <script src="${pageContext.request.contextPath}/resources/js/courseManage.js"></script>
  </body>

  </html>