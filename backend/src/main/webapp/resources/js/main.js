/**
 * 教学辅助系统首页脚本
 */
document.addEventListener('DOMContentLoaded', function() {
    // 初始化页面
    initPage();
    
    // 设置登出功能
    setupLogout();
});

/**
 * 初始化页面
 */
function initPage() {
    // 获取当前时间
    const now = new Date();
    const hours = now.getHours();
    
    // 根据时间显示不同的问候语
    let greeting = '';
    if (hours < 12) {
        greeting = '早上好';
    } else if (hours < 18) {
        greeting = '下午好';
    } else {
        greeting = '晚上好';
    }
    
    // 获取用户名称元素
    const userElement = document.querySelector('header p');
    if (userElement) {
        const username = userElement.textContent.replace('欢迎，', '');
        userElement.textContent = `${greeting}，${username}`;
    }
    
    // 检查是否有通知消息
    checkNotifications();
}

/**
 * 设置登出功能
 */
function setupLogout() {
    // 在这里添加登出按钮的功能，如果页面上有登出按钮的话
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            // 发送登出请求
            fetch('/logout', {
                method: 'POST',
                credentials: 'include'
            })
            .then(response => {
                if (response.ok) {
                    // 登出成功，重定向到登录页面
                    window.location.href = 'login.jsp';
                } else {
                    console.error('登出失败');
                }
            })
            .catch(error => {
                console.error('登出请求出错:', error);
            });
        });
    }
}

/**
 * 检查通知
 */
function checkNotifications() {
    // 这里可以添加检查通知的逻辑，比如请求API获取未读通知
    console.log('检查通知...');
    
    // 示例：如果想要在页面上添加通知显示
    // const notificationContainer = document.createElement('div');
    // notificationContainer.className = 'notification';
    // notificationContainer.textContent = '您有新的通知';
    // document.querySelector('.container').appendChild(notificationContainer);
} 