<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
  <!DOCTYPE html>
  <html>

  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>错误页面</title>
    <style>
      body {
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
        height: 100vh;
        margin: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #333;
      }

      .error-container {
        background: rgba(255, 255, 255, 0.9);
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        padding: 30px;
        max-width: 500px;
        width: 90%;
        text-align: center;
      }

      h1 {
        color: #e74c3c;
        margin-bottom: 20px;
      }

      .error-code {
        font-size: 72px;
        font-weight: bold;
        color: #e74c3c;
        margin: 0;
      }

      .error-message {
        margin: 20px 0;
        font-size: 18px;
      }

      .back-button {
        display: inline-block;
        background: #3498db;
        color: white;
        padding: 10px 20px;
        border-radius: 4px;
        text-decoration: none;
        font-weight: 500;
        margin-top: 20px;
        transition: background 0.3s;
      }

      .back-button:hover {
        background: #2980b9;
      }
    </style>
  </head>

  <body>
    <div class="error-container">
      <h1>出错了</h1>
      <p class="error-code">
        <% Object statusCode=request.getAttribute("jakarta.servlet.error.status_code"); if (statusCode !=null) {
          out.print(statusCode); } else { out.print("Error"); } %>
      </p>
      <p class="error-message">
        <% Object message=request.getAttribute("jakarta.servlet.error.message"); if (message !=null &&
          !message.toString().isEmpty()) { out.print(message); } else if (exception !=null) {
          out.print(exception.getMessage()); } else { out.print("发生了未知错误，请稍后再试。"); } %>
      </p>
      <a href="${pageContext.request.contextPath}/" class="back-button">返回首页</a>
    </div>
  </body>

  </html>