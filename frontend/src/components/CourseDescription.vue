<template>
  <div class="course-description-container">
    <div class="header">
      <button class="back-button" @click="$emit('back')">←</button>
      <button class="ai-btn">
        <span class="ai-icon">✓</span>
        AI生成
      </button>
    </div>

    <div class="section">
      <h3 class="section-title">课程介绍</h3>
      <Markdown 
        :initial-value="courseIntroduction" 
        height="200px" 
        preview-style="tab"
        :editable="false" 
      />
    </div>

    <div class="section">
      <h3 class="section-title">教学目标</h3>
      <Markdown 
        :initial-value="courseContent" 
        height="200px" 
        preview-style="tab"
        :editable="false" 
      />
    </div>

    <div class="actions">
      <button class="btn btn-secondary">暂存</button>
      <button class="btn btn-primary">保存</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue';
import Markdown from './markdown.vue';

defineEmits(['back']);

// 示例课程简介内容
const courseIntroduction = ref(`本课程是一门综合性学科课程，旨在帮助学习者掌握该领域的基础理论和实践技能。通过系统化的学习，学员将能够理解核心概念，并具备解决实际问题的能力。`);

// 示例课程主要内容
const courseContent = ref(`1. 基础理论篇：核心概念与原理
2. 实践应用篇：案例分析与项目实战
3. 进阶提升篇：前沿技术与发展趋势
4. 综合评估篇：项目实践与成果展示`);
</script>

<style scoped>
.course-description-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
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

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border-radius: 4px;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-secondary {
  background-color: #f5f5f5;
  color: #333;
}

.btn:hover {
  opacity: 0.9;
}
</style>
