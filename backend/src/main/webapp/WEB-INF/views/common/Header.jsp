<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

  <header class="header-container">
    <div class="logo-container">
      <img src="https://res.cloudinary.com/dm3rouwgn/image/upload/t_media_lib_thumb/rfm1y1en2sqea4rd9ggy" alt="智教未来"
        class="logo" />
      <span class="logo-text">智教未来</span>
    </div>

    <div class="search-container">
      <div class="search-input-wrapper">
        <input type="text" id="searchInput" class="search-input" placeholder="请输入内容" />
        <span class="search-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16">
            <path
              d="M23.78 22.61l-6-6a10 10 0 1 0-1.18 1.18l6 6a.83.83 0 0 0 1.18-1.18zM10 18a8 8 0 1 1 8-8 8 8 0 0 1-8 8z" />
          </svg>
        </span>
        <span class="clear-icon" id="clearSearch" style="display: none;">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="14" height="14">
            <path
              d="M13.41 12l6.3-6.29a1 1 0 1 0-1.42-1.42L12 10.59l-6.29-6.3a1 1 0 0 0-1.42 1.42l6.3 6.29-6.3 6.29a1 1 0 0 0 0 1.42 1 1 0 0 0 1.42 0l6.29-6.3 6.29 6.3a1 1 0 0 0 1.42 0 1 1 0 0 0 0-1.42z">
            </path>
          </svg>
        </span>
      </div>
    </div>

    <div class="user-actions">
      <div class="user-avatar">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="18" height="18">
          <path
            d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5zm0-8a3 3 0 1 1-3 3 3 3 0 0 1 3-3zm9 17v-2a7 7 0 0 0-7-7H10a7 7 0 0 0-7 7v2a1 1 0 0 0 2 0v-2a5 5 0 0 1 5-5h4a5 5 0 0 1 5 5v2a1 1 0 0 0 2 0z" />
        </svg>
      </div>
      <span class="username">
        <%=session.getAttribute("username") !=null ? session.getAttribute("username") : "用户" %>
      </span>
      <div class="divider"></div>
      <a href="javascript:void(0)" class="logout-btn" onclick="logout()">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16">
          <path
            d="M7 6a1 1 0 0 0 0-2H5a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h2a1 1 0 0 0 0-2H6V6h1zm13.82 5.42l-2.82-4a1 1 0 0 0-1.39-.24 1 1 0 0 0-.24 1.4L18.09 11H10a1 1 0 0 0 0 2h8l-1.8 2.4a1 1 0 0 0 .2 1.4 1 1 0 0 0 .6.2 1 1 0 0 0 .8-.4l3-4a1 1 0 0 0 .02-1.18z" />
        </svg>
        <span>退出</span>
      </a>
      <div class="divider"></div>
      <a href="javascript:void(0)" class="about-btn">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16">
          <path
            d="M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm0 18a8 8 0 1 1 8-8 8 8 0 0 1-8 8zm0-8.5a1 1 0 0 0-1 1v3a1 1 0 0 0 2 0v-3a1 1 0 0 0-1-1zm0-4a1.25 1.25 0 1 0 1.25 1.25A1.25 1.25 0 0 0 12 7.5z" />
        </svg>
        <span>关于</span>
      </a>
    </div>
  </header>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/header.css">
  <script src="${pageContext.request.contextPath}/resources/js/header.js"></script>