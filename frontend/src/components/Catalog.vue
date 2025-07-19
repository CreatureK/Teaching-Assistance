<template>
  <div class="catalog-container" :class="{ 'collapsed': !expanded }">
    <div class="catalog-header">
      <h2>目录</h2>
    </div>
    <div class="catalog-body">
      <div v-if="expanded" class="catalog-items">
        <div 
          v-for="(item, index) in catalogItems" 
          :key="index"
          class="catalog-item"
          :class="{ 
            'level-1': item.level === 1, 
            'level-2': item.level === 2, 
            'level-3': item.level === 3,
            'active': item.id === activeItemId
          }"
          @click="handleItemClick(item)"
        >
          {{ item.text }}
        </div>
      </div>
      <div class="toggle-btn" @click="toggleCatalog">
        <span class="toggle-icon">{{ expanded ? '◀' : '▶' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

interface CatalogItem {
  id: string
  text: string
  level: number
  anchor: string
}

const props = defineProps<{
  content: string
  activeHeading?: string
}>()

const emit = defineEmits(['navigate', 'edit', 'toggle'])

const expanded = ref(true)
const activeItemId = ref('')

const toggleCatalog = () => {
  expanded.value = !expanded.value
  emit('toggle', expanded.value)
}

// 解析markdown内容，提取标题生成目录
const catalogItems = computed<CatalogItem[]>(() => {
  if (!props.content) return []
  
  const headingRegex = /^(#{1,6})\s+(.+)$/gm
  const items: CatalogItem[] = []
  let match
  
  while ((match = headingRegex.exec(props.content)) !== null) {
    const level = match[1].length
    const text = match[2]
    const anchor = text.toLowerCase().replace(/\s+/g, '-')
    const id = `heading-${items.length}`
    
    if (level <= 3) { // 只显示前三级标题
      items.push({
        id,
        text,
        level,
        anchor
      })
    }
  }
  
  return items
})

// 格式化标题文本，添加章节编号
const formattedCatalogItems = computed(() => {
  const items = [...catalogItems.value]
  const counters = [0, 0, 0]
  
  return items.map(item => {
    const level = item.level
    
    // 重置低级别计数器
    for (let i = level; i < counters.length; i++) {
      counters[i] = 0
    }
    
    // 增加当前级别计数器
    counters[level - 1]++
    
    // 格式化标题文本
    let prefix = ''
    for (let i = 0; i < level; i++) {
      if (i > 0) prefix += '.'
      prefix += counters[i]
    }
    
    return {
      ...item,
      text: `${prefix} ${item.text}`
    }
  })
})

// 处理目录项点击
const handleItemClick = (item: CatalogItem) => {
  activeItemId.value = item.id
  emit('navigate', item.anchor)
}

// 监听activeHeading变化，更新高亮项
watch(() => props.activeHeading, (newValue) => {
  if (newValue) {
    const item = catalogItems.value.find(item => item.anchor === newValue)
    if (item) {
      activeItemId.value = item.id
    }
  }
})

// 添加默认导出
defineExpose({
  toggleCatalog,
  handleItemClick,
  catalogItems
});
</script>

<style scoped>
.catalog-container {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  background-color: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  position: relative;
  height: 100%;
}

.catalog-container.collapsed {
  width: 40px;
  background-color: transparent;
  box-shadow: none;
  border: none;
}

.collapsed .catalog-header {
  display: none;
}

.collapsed .catalog-body {
  background-color: transparent;
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
  height: 100%;
}

.catalog-container:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.catalog-container.collapsed:hover {
  box-shadow: none;
}

.catalog-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 5px;
  background: linear-gradient(135deg, rgba(41, 128, 185, 0.5), rgba(52, 152, 219, 0.3), rgba(85, 175, 237, 0.4));
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  color: white;
  border-bottom: 1px solid rgba(255, 255, 255, 0.25);
  position: relative;
}

/* 添加发光边缘效果，类似于Header组件 */
.catalog-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0),
    rgba(255, 255, 255, 0.5),
    rgba(52, 152, 219, 0.3),
    rgba(255, 255, 255, 0.5),
    rgba(255, 255, 255, 0)
  );
  z-index: 1;
}

.catalog-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: bold;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.catalog-body {
  background-color: rgba(240, 240, 240, 0.2);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  position: relative;
  height: calc(100% - 40px);
}

.toggle-btn {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background-color: rgba(52, 152, 219, 0.2);
  border-radius: 4px 0 0 4px;
  z-index: 10;
  transition: all 0.2s ease;
}

.toggle-btn:hover {
  background-color: rgba(52, 152, 219, 0.4);
}

.toggle-icon {
  color: rgba(12, 77, 162, 0.9);
  font-size: 14px;
}

.catalog-items {
  padding: 8px 0;
  max-height: 100%;
  overflow-y: auto;
}

/* 添加滚动条样式 */
.catalog-items::-webkit-scrollbar {
  width: 6px;
}

.catalog-items::-webkit-scrollbar-track {
  background: rgba(240, 240, 240, 0.1);
  border-radius: 3px;
}

.catalog-items::-webkit-scrollbar-thumb {
  background: rgba(12, 77, 162, 0.2);
  border-radius: 3px;
}

.catalog-items::-webkit-scrollbar-thumb:hover {
  background: rgba(12, 77, 162, 0.4);
}

.catalog-item {
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.catalog-item:hover {
  background-color: rgba(224, 224, 224, 0.4);
  transform: translateX(2px);
}

/* 移除active样式 */
/* .catalog-item.active {
  background-color: rgba(204, 229, 255, 0.3);
  border-left: 3px solid rgba(33, 150, 243, 0.8);
  color: rgba(12, 77, 162, 0.9);
  font-weight: 500;
} */

.level-1 {
  font-size: 17px;
  font-weight: bold;
}

.level-2 {
  padding-left: 32px;
  font-size: 15px;
}

.level-3 {
  padding-left: 48px;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.7);
}

.collapsed .toggle-btn {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 60px;
  border-radius: 0 4px 4px 0;
  background-color: rgba(52, 152, 219, 0.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.collapsed .toggle-icon {
  color: rgba(12, 77, 162, 0.9);
}
</style>
