<% String accessToken=(String) session.getAttribute("access_token"); if (accessToken==null) {
    response.sendRedirect("login.jsp"); } else { response.sendRedirect("home.jsp"); } %>