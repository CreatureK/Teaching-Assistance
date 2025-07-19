<template>
  <div class="course-description-container">
    <div class="header">
      <button class="back-button" @click="$emit('back')">←</button>
      <div class="header-right">
        <button class="ai-btn" @click="showPrompt = true">
          <span class="ai-icon">✨</span>
          AI生成
        </button>
        <button class="btn btn-secondary">暂存</button>
        <button class="btn btn-primary">保存</button>
      </div>
    </div>

    <div class="section">
      <h2  class="section-title">课程介绍</h2>
      <Markdown 
        :initial-value="courseIntroduction" 
        height="200px" 
        preview-style="tab"
        :editable="false" 
      />
    </div>

    <div class="section">
      <h2  class="section-title">教学目标</h2>
      <Markdown 
        :initial-value="courseContent" 
        height="200px" 
        preview-style="tab"
        :editable="false" 
      />
    </div>

    <Prompt
      :is-visible="showPrompt"
      title="AI生成"
      description="请输入您想要AI生成的内容描述或指令"
      placeholder="例如：生成一个关于人工智能基础的课程介绍"
      @close="showPrompt = false"
      @confirm="handlePromptConfirm"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Markdown from './markdown.vue';
import Prompt from './Prompt.vue';

defineEmits(['back']);

// 控制Prompt组件显示
const showPrompt = ref(false);

// 示例课程简介内容
const courseIntroduction = ref(`本课程是一门综合性学科课程，旨在帮助学习者掌握该领域的基础理论和实践技能。通过系统化的学习，学员将能够理解核心概念，并具备解决实际问题的能力。`);

// 示例课程主要内容
const courseContent = ref(`1. 基础理论篇：核心概念与原理
2. 实践应用篇：案例分析与项目实战
3. 进阶提升篇：前沿技术与发展趋势
4. 综合评估篇：项目实践与成果展示`);

// 处理Prompt提交事件
const handlePromptConfirm = (content: string) => {
  console.log('用户提交的内容:', content);
  // 这里可以添加处理AI生成的逻辑，比如发送请求到后端
  showPrompt.value = false;
};
</script>

<style scoped>
.course-description-container {
  max-width: 1500px;
  margin: 0 auto;
  padding: 20px;
  overflow: hidden; /* 隐藏滚动条 */
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-button {
  background-color: transparent;
  border: none;
  color: #2196f3;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.back-button:hover {
  background-color: rgba(33, 150, 243, 0.1);
}



.ai-btn {
  display: flex;
  align-items: center;
  background-color: rgba(76, 175, 80, 0.7);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.ai-btn:hover {
  background-color: rgba(76, 175, 80, 0.85);
}

.ai-icon {
  margin-right: 8px;
}

.section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  margin-bottom: 10px;
}



.btn {
  padding: 10px 20px;
  border-radius: 4px;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.btn-primary {
  background-color: rgba(76, 175, 80, 0.7);
  color: white;
}

.btn-primary:hover {
  background-color: rgba(76, 175, 80, 0.85);
}

.btn-secondary {
  background-color: rgba(245, 245, 245, 0.7);
  color: #333;
}

.btn-secondary:hover {
  background-color: rgba(245, 245, 245, 0.85);
}

.btn:hover {
  opacity: 0.9;
}
</style>

<style>
/* 隐藏全局滚动条 */
body {
  overflow: hidden;
}

::-webkit-scrollbar {
  display: none;
}

* {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}
</style>
