<% String accessToken=(String) session.getAttribute("access_token"); if (accessToken==null) {
    response.sendRedirect("login.jsp"); } %>
    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>教学辅助系统</title>
            <!-- 引入样式文件 -->
            <link rel="stylesheet" href="resources/css/main.css">
        </head>

        <body>
            <div class="container">
                <header>
                    <h1>教学辅助系统</h1>
                    <div class="user-info">
                        <p>欢迎，<%= session.getAttribute("username") !=null ? session.getAttribute("username") : "用户" %>
                        </p>
                        <button id="logoutBtn" class="logout-btn">登出</button>
                    </div>
                </header>

                <main>
                    <div class="content">
                        <!-- 主要内容区域 -->
                        <p>这里是系统首页内容</p>
                    </div>
                </main>

                <footer>
                    <p>&copy; 2023 教学辅助系统</p>
                </footer>
            </div>

            <!-- 引入脚本文件 -->
            <script src="resources/js/main.js"></script>
        </body>

        </html>