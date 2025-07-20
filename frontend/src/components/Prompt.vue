<template>
  <div class="prompt-overlay" v-if="isVisible" @click.self="handleClose">
    <div class="prompt-container">
      <div class="prompt-header">
        <h2>{{ title }}</h2>
        <button class="close-button" @click="handleClose">
          <span>×</span>
        </button>
      </div>
      
      <div class="prompt-body">
        <p class="prompt-description">{{ description }}</p>
        <div class="input-container">
          <textarea 
            v-model="inputContent" 
            :placeholder="placeholder"
            :maxlength="maxLength"
            rows="8"
            @input="handleInput"
          ></textarea>
          <span class="character-count">{{ inputContent.length }} / {{ maxLength }}</span>
        </div>
      </div>
      
      <div class="prompt-footer">
        <button class="cancel-button" @click="handleClose">取消</button>
        <button class="confirm-button" @click="handleConfirm">确认</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, watch } from 'vue';

const props = defineProps({
  title: {
    type: String,
    default: '生成教学大纲'
  },
  description: {
    type: String,
    default: '请输入课程名称或关键词，AI将为您生成教学大纲。'
  },
  placeholder: {
    type: String,
    default: ''
  },
  maxLength: {
    type: Number,
    default: 1000
  },
  isVisible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close', 'confirm', 'update:content']);

const inputContent = ref('');

// 监听isVisible属性变化，当显示时重置输入内容
watch(() => props.isVisible, (newValue) => {
  if (newValue === true) {
    inputContent.value = '';
  }
}, { immediate: false });

const handleInput = () => {
  emit('update:content', inputContent.value);
};

const handleClose = () => {
  emit('close');
};

const handleConfirm = () => {
  if (inputContent.value.trim()) {
    emit('confirm', inputContent.value);
  }
};

// 添加默认导出
defineExpose({
  handleClose,
  handleConfirm
});
</script>

<style scoped>
.prompt-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.prompt-container {
  width: 90%;
  max-width: 600px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.18);
  overflow: hidden;
  padding: 1.5rem;
}

.prompt-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.prompt-header h2 {
  font-size: 1.5rem;
  margin: 0;
  font-weight: 600;
  color: #333;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
  padding: 0;
  line-height: 1;
}

.prompt-body {
  margin-bottom: 1.5rem;
}

.prompt-description {
  margin: 0 0 1rem 0;
  color: #555;
}

.input-container {
  position: relative;
}

textarea {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  border-radius: 8px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.5);
  resize: none;
  outline: none;
  transition: border-color 0.2s ease;
}

textarea:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}

.character-count {
  position: absolute;
  bottom: 0.5rem;
  right: 0.75rem;
  font-size: 0.8rem;
  color: #999;
}

.prompt-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.cancel-button, .confirm-button {
  padding: 0.5rem 1.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.cancel-button {
  background: transparent;
  border: 1px solid rgba(0, 0, 0, 0.2);
  color: #555;
}

.cancel-button:hover {
  background: rgba(0, 0, 0, 0.05);
}

.confirm-button {
  background: #6366f1;
  color: white;
  border: none;
}

.confirm-button:hover {
  background: #4f46e5;
}
</style>
