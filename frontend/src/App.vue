<script setup lang="ts">
// 导入路由视图组件
import { RouterView } from 'vue-router'
// import { StagewiseToolbar } from '@stagewise/toolbar-vue'

// const isDev = import.meta.env.DEV
// const stagewise = {
//   plugins: []
// }
</script>

<template>
  <div class="app-container">
    <RouterView />
  </div>
  <!-- <StagewiseToolbar v-if="isDev" :config="stagewise" /> -->
</template>

<style>
:root {
  --primary-color: #3498db;
  --primary-light: #5dade2;
  --primary-dark: #2980b9;
  --secondary-color: #2ecc71;
  --secondary-light: #58d68d;
  --secondary-dark: #27ae60;
  --text-color: rgba(0, 0, 0, 0.8);
  --text-light: rgba(0, 0, 0, 0.6);
  --background-color: #f8f9fa;
  --card-bg: rgba(255, 255, 255, 0.8);
  --border-color: rgba(0, 0, 0, 0.1);
  --shadow-color: rgba(0, 0, 0, 0.05);
  --notification-error-bg: rgba(244, 67, 54, 0.1);
  --notification-error-color: #e74c3c;
  --notification-info-bg: rgba(33, 150, 243, 0.1);
  --notification-info-color: #3498db;
}

/* 全局样式 */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html, body {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

body {
  color: var(--text-color);
  background: linear-gradient(-45deg, #a6d4fa, #48d1cc, #a6d4fa, #48d1cc);
  background-size: 400% 400%;
  overflow: hidden;
  animation: gradientBG 15s ease-in-out infinite;
  line-height: 1.6;
}

/* 添加动态光斑效果 */
body::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(255, 0, 38, 0.6) 0%, rgba(255, 169, 182, 0) 20%),
    radial-gradient(circle at 75% 40%, rgba(255, 153, 0, 0.8) 0%, rgba(255, 222, 173, 0) 25%),
    radial-gradient(circle at 40% 80%, rgba(0, 191, 255, 0.5) 0%, rgba(173, 216, 230, 0) 30%),
    radial-gradient(circle at 85% 15%, rgba(0, 255, 0, 0.5) 0%, rgba(240, 255, 240, 0) 20%),
    radial-gradient(circle at 60% 60%, rgba(255, 239, 90, 0.4) 0%, rgba(255, 250, 205, 0) 25%);
  background-size: 200% 200%;
  mix-blend-mode: overlay;
  z-index: 0;
  animation: movingLights 20s ease-in-out infinite;
  pointer-events: none;
}

/* 添加模糊背景效果 */
body::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  z-index: 0;
  pointer-events: none;
}

/* 动画效果 */
@keyframes gradientBG {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes movingLights {
  0% {
    background-position: 0% 0%;
  }
  25% {
    background-position: 50% 25%;
  }
  50% {
    background-position: 100% 50%;
  }
  75% {
    background-position: 50% 75%;
  }
  100% {
    background-position: 0% 0%;
  }
}

.app-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  overflow-y: auto;
  position: relative;
  z-index: 1;
}

/* 响应式字体大小 */
html {
  font-size: 16px;
}

@media (max-width: 768px) {
  html {
    font-size: 14px;
  }
}
</style>
