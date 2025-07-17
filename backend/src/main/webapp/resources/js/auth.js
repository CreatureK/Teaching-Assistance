/**
 * 登录注册页面JavaScript功能
 * 从Vue3组件转换而来
 */

// 全局变量
let isRegister = false;
let isLoading = false;

// DOM元素缓存
const elements = {
  authForm: null,
  authCard: null,
  authTitle: null,
  authSubtitle: null,
  usernameInput: null,
  passwordInput: null,
  emailGroup: null,
  emailInput: null,
  submitButton: null,
  submitButtonText: null,
  togglePassword: null,
  passwordVisibleIcon: null,
  passwordHiddenIcon: null,
  modeToggleText: null,
  modeToggleLink: null,
  errorMessage: null,
  errorMessageText: null,
  successMessage: null,
  successMessageText: null
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
  // 缓存DOM元素
  cacheElements();

  // 添加事件监听器
  addEventListeners();

  // 检查用户是否已登录
  checkLoggedInStatus();

  // 初始化光影效果
  initializeGlowEffect();
});

// 缓存页面中的DOM元素
function cacheElements() {
  elements.authForm = document.getElementById('authForm');
  elements.authCard = document.getElementById('authCard');
  elements.authTitle = document.getElementById('authTitle');
  elements.authSubtitle = document.getElementById('authSubtitle');
  elements.usernameInput = document.getElementById('username');
  elements.passwordInput = document.getElementById('password');
  elements.emailGroup = document.getElementById('emailGroup');
  elements.emailInput = document.getElementById('email');
  elements.submitButton = document.getElementById('submitButton');
  elements.submitButtonText = document.getElementById('submitButtonText');
  elements.togglePassword = document.getElementById('togglePassword');
  elements.passwordVisibleIcon = document.getElementById('passwordVisibleIcon');
  elements.passwordHiddenIcon = document.getElementById('passwordHiddenIcon');
  elements.modeToggleText = document.getElementById('modeToggleText');
  elements.modeToggleLink = document.getElementById('modeToggleLink');
  elements.errorMessage = document.getElementById('errorMessage');
  elements.errorMessageText = document.getElementById('errorMessageText');
  elements.successMessage = document.getElementById('successMessage');
  elements.successMessageText = document.getElementById('successMessageText');
}

// 添加所有事件监听器
function addEventListeners() {
  // 表单提交
  elements.authForm.addEventListener('submit', handleSubmit);

  // 切换注册/登录模式
  elements.modeToggleLink.addEventListener('click', toggleMode);

  // 密码显示/隐藏
  elements.togglePassword.addEventListener('click', togglePasswordVisibility);
}

// 检查用户是否已登录
function checkLoggedInStatus() {
  // 先检查sessionStorage，再检查localStorage
  if (sessionStorage.getItem('access_token') || localStorage.getItem('access_token')) {
    // 用户已登录，跳转到首页
    window.location.href = '/';
  }
}

// 初始化光影效果
function initializeGlowEffect() {
  // 添加鼠标移动事件监听器
  window.addEventListener('mousemove', handleMouseMove);
}

// 处理鼠标移动效果
function handleMouseMove(event) {
  // 获取容器的位置和尺寸
  const container = document.querySelector('.auth-container');
  if (container) {
    const rect = container.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;

    // 计算鼠标位置相对于容器中心的偏移
    const offsetX = (event.clientX - centerX) / rect.width;
    const offsetY = (event.clientY - centerY) / rect.height;

    // 根据鼠标位置动态更新卡片阴影
    const intensity = 20; // 光影强度

    // 使用浅色模式的光影效果
    const glowColor = 'rgba(255, 255, 255, 0.6)'; // 浅色模式：白色光晕
    const shadowColor = 'rgba(31, 38, 135, 0.2)'; // 浅色模式：原阴影
    const borderGlow = `rgba(255, 255, 255, ${0.4 + offsetY * 0.2})`; // 浅色模式：白色边框光晕

    elements.authCard.style.boxShadow = `
            ${-offsetX * intensity}px ${-offsetY * intensity}px 20px ${glowColor},
            ${offsetX * intensity}px ${offsetY * intensity}px 25px ${shadowColor},
            0 4px 8px rgba(0, 0, 0, 0.05),
            inset 0 0 0 1px ${borderGlow}
        `;
  }
}

// 切换注册/登录模式
function toggleMode(event) {
  event.preventDefault();

  isRegister = !isRegister;

  // 更新UI元素
  if (isRegister) {
    elements.authTitle.textContent = '创建账号';
    elements.authSubtitle.textContent = '请填写以下信息完成注册';
    elements.emailGroup.style.display = 'block';
    elements.submitButtonText.textContent = '注册';
    elements.modeToggleText.innerHTML = '已有账号？<a href="#" id="modeToggleLink">返回登录</a>';
  } else {
    elements.authTitle.textContent = '欢迎登录';
    elements.authSubtitle.textContent = '请输入您的账号信息';
    elements.emailGroup.style.display = 'none';
    elements.submitButtonText.textContent = '登录';
    elements.modeToggleText.innerHTML = '没有账号？<a href="#" id="modeToggleLink">立即注册</a>';
  }

  // 重新绑定模式切换事件，因为我们更改了DOM
  elements.modeToggleLink = document.getElementById('modeToggleLink');
  elements.modeToggleLink.addEventListener('click', toggleMode);

  // 清除错误和成功消息
  hideMessages();
}

// 切换密码显示/隐藏
function togglePasswordVisibility() {
  const isPasswordVisible = elements.passwordInput.type === 'text';

  // 切换密码输入字段类型
  elements.passwordInput.type = isPasswordVisible ? 'password' : 'text';

  // 切换图标显示
  elements.passwordVisibleIcon.style.display = isPasswordVisible ? 'none' : 'block';
  elements.passwordHiddenIcon.style.display = isPasswordVisible ? 'block' : 'none';
}

// 表单提交处理
async function handleSubmit(event) {
  event.preventDefault();

  if (isLoading) return;

  // 隐藏任何现有消息
  hideMessages();

  // 设置加载状态
  setLoading(true);

  try {
    const username = elements.usernameInput.value;
    const password = elements.passwordInput.value;

    if (isRegister) {
      // 注册
      const email = elements.emailInput.value;
      console.log('开始注册，数据:', { username, password, email });

      await registerUser(username, password, email);
    } else {
      // 登录
      console.log('开始登录，数据:', { username, password });

      await loginUser(username, password);
    }
  } catch (error) {
    console.error('操作出错:', error);
    showError(error.message || '操作失败');
  } finally {
    setLoading(false);
  }
}

// 注册用户
async function registerUser(username, password, email) {
  try {
    const response = await fetch('/api/auth/signin', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username,
        password,
        email
      })
    });

    const data = await response.json();
    console.log('注册响应:', data);

    if (data.code === 200) {
      showSuccess('注册成功');

      // 清空表单
      elements.usernameInput.value = '';
      elements.passwordInput.value = '';
      elements.emailInput.value = '';

      // 注册成功后，延迟1.5秒后切换到登录模式
      setTimeout(() => {
        isRegister = true;  // 当前是注册模式
        toggleMode({ preventDefault: () => { } });  // 切换到登录模式
        hideMessages();
      }, 1500);
    } else {
      showError('注册失败，请重试');
    }
  } catch (error) {
    throw new Error('注册请求失败: ' + error.message);
  }
}

// 用户登录
async function loginUser(username, password) {
  try {
    const response = await fetch('/api/auth/token', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username,
        password
      })
    });

    const data = await response.json();
    console.log('登录响应:', data);

    if (data.code === 200 && data.data?.data) {
      // 存储令牌到本地存储
      const token = data.data.data;
      sessionStorage.setItem('access_token', token);

      // 如果用户选择了"记住我"，也存储到localStorage
      const rememberMe = document.getElementById('rememberMe');
      if (rememberMe && rememberMe.checked) {
        localStorage.setItem('access_token', token);
      }

      showSuccess('登录成功');

      // 触发登录状态变更事件
      window.dispatchEvent(new Event('login-state-changed'));

      // 短暂延迟后跳转
      setTimeout(() => {
        window.location.href = '/';
      }, 500);
    } else {
      showError('登录失败，请检查用户名和密码');
    }
  } catch (error) {
    throw new Error('登录请求失败: ' + error.message);
  }
}

// 设置加载状态
function setLoading(loading) {
  isLoading = loading;

  if (loading) {
    elements.submitButton.classList.add('loading');
    elements.submitButtonText.textContent = '处理中...';
    elements.submitButton.disabled = true;
    elements.usernameInput.disabled = true;
    elements.passwordInput.disabled = true;
    if (elements.emailInput) {
      elements.emailInput.disabled = true;
    }
  } else {
    elements.submitButton.classList.remove('loading');
    elements.submitButtonText.textContent = isRegister ? '注册' : '登录';
    elements.submitButton.disabled = false;
    elements.usernameInput.disabled = false;
    elements.passwordInput.disabled = false;
    if (elements.emailInput) {
      elements.emailInput.disabled = false;
    }
  }
}

// 显示错误消息
function showError(message) {
  elements.errorMessage.style.display = 'flex';
  elements.errorMessageText.textContent = message;
  elements.successMessage.style.display = 'none';
}

// 显示成功消息
function showSuccess(message) {
  elements.successMessage.style.display = 'flex';
  elements.successMessageText.textContent = message;
  elements.errorMessage.style.display = 'none';
}

// 隐藏所有消息
function hideMessages() {
  elements.errorMessage.style.display = 'none';
  elements.successMessage.style.display = 'none';
} 