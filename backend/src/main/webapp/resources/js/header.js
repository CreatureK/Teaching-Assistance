/**
 * 头部组件JavaScript功能
 */
document.addEventListener('DOMContentLoaded', function () {
  // 获取DOM元素
  const searchInput = document.getElementById('searchInput');
  const clearSearchBtn = document.getElementById('clearSearch');

  // 搜索输入事件处理
  if (searchInput) {
    // 输入变化时显示/隐藏清除按钮
    searchInput.addEventListener('input', function () {
      if (this.value.trim() !== '') {
        clearSearchBtn.style.display = 'block';
      } else {
        clearSearchBtn.style.display = 'none';
      }
    });

    // 按下回车键执行搜索
    searchInput.addEventListener('keydown', function (e) {
      if (e.key === 'Enter') {
        performSearch(this.value);
      }
    });
  }

  // 清除搜索内容
  if (clearSearchBtn) {
    clearSearchBtn.addEventListener('click', function () {
      searchInput.value = '';
      this.style.display = 'none';
      searchInput.focus();
    });
  }
});

/**
 * 执行搜索操作
 * @param {string} query - 搜索关键词
 */
function performSearch(query) {
  if (query.trim() === '') return;

  // 这里可以实现搜索逻辑，例如跳转到搜索结果页或在当前页面显示搜索结果
  console.log('执行搜索：', query);

  // 示例：跳转到搜索结果页
  // window.location.href = 'search.jsp?query=' + encodeURIComponent(query);
}

/**
 * 退出登录
 */
function logout() {
  // 发送登出请求到服务器
  fetch('/logout', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'same-origin'
  })
    .then(response => {
      if (response.ok) {
        // 成功登出后，清除会话数据
        console.log('退出登录成功');

        // 清除本地存储
        localStorage.removeItem('showFunctionSelect');
        localStorage.removeItem('showCourseInfo');
        localStorage.removeItem('selectedCourseTitle');

        // 跳转到登录页面
        window.location.href = 'login.jsp';
      } else {
        console.error('退出登录失败');
        alert('退出登录失败，请稍后重试');
      }
    })
    .catch(error => {
      console.error('退出登录错误:', error);
      alert('网络错误，请检查连接后重试');
    });
} 