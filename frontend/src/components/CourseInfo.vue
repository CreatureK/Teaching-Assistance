<template>
  <div class="course-info-container">
    <div class="course-info-card">
      <div class="header-container">
        <button class="back-button" @click="goBack">←</button>
        <h1 class="course-title">课程信息</h1>
      </div>
      
      <form @submit.prevent="saveCourseInfo">
        <div class="form-content">
          <!-- 课程标题 -->
          <div class="form-group">
            <label class="form-label">课程标题</label>
            <input 
              v-model="courseInfo.title" 
              type="text" 
              class="form-input"
              placeholder="请输入课程标题"
            />
          </div>

          <!-- 教学重点内容 -->
          <div class="form-group">
            <label class="form-label">教学重点内容</label>
            <textarea
              v-model="courseInfo.keyPoints"
              class="form-textarea"
              placeholder="请描述本课程的教学重点内容，如核心知识点、教学目标等"
            ></textarea>
          </div>

          <!-- 学分和学时 -->
          <div class="credits-hours-container">
            <div class="form-group">
              <label class="form-label">学分</label>
              <input 
                v-model="courseInfo.credits" 
                type="text" 
                class="form-input"
                placeholder="请输入课程学分，如: 2"
              />
            </div>
            <div class="form-group">
              <label class="form-label">学时</label>
              <input 
                v-model="courseInfo.hours" 
                type="text" 
                class="form-input"
                placeholder="请输入课程学时，如: 40"
              />
            </div>
          </div>

          <!-- 考核方式 -->
          <div class="form-group">
            <label class="form-label">考核方式</label>
            <div class="table-container">
              <table class="assessment-table">
                <thead>
                  <tr>
                    <th class="table-header">考核方式</th>
                    <th class="table-header">课堂表现</th>
                    <th class="table-header">平时作业</th>
                    <th class="table-header">期中测试</th>
                    <th class="table-header">实验操作</th>
                    <th class="table-header">闭卷考试</th>
                    <th class="table-header">开卷考试</th>
                    <th class="table-header">PPT汇报</th>
                    <th class="table-header">合计</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td class="table-cell">成绩占比</td>
                    <td class="table-cell">
                      <input 
                        v-model="courseInfo.assessments.classPerformance" 
                        type="text" 
                        class="percentage-input"
                        @input="calculateTotal"
                      />%
                    </td>
                    <td class="table-cell">
                      <input 
                        v-model="courseInfo.assessments.homework" 
                        type="text" 
                        class="percentage-input"
                        @input="calculateTotal"
                      />%
                    </td>
                    <td class="table-cell">
                      <input 
                        v-model="courseInfo.assessments.midterm" 
                        type="text" 
                        class="percentage-input"
                        @input="calculateTotal"
                      />%
                    </td>
                    <td class="table-cell">
                      <input 
                        v-model="courseInfo.assessments.labWork" 
                        type="text" 
                        class="percentage-input"
                        @input="calculateTotal"
                      />%
                    </td>
                    <td class="table-cell">
                      <input 
                        v-model="courseInfo.assessments.closedExam" 
                        type="text" 
                        class="percentage-input"
                        @input="calculateTotal"
                      />%
                    </td>
                    <td class="table-cell">
                      <input 
                        v-model="courseInfo.assessments.openExam" 
                        type="text" 
                        class="percentage-input"
                        @input="calculateTotal"
                      />%
                    </td>
                    <td class="table-cell">
                      <input 
                        v-model="courseInfo.assessments.presentation" 
                        type="text" 
                        class="percentage-input"
                        @input="calculateTotal"
                      />%
                    </td>
                    <td class="table-cell total-cell">{{ totalPercentage }}%</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- 按钮 -->
        <div class="button-container">
          <button 
            type="submit"
            class="save-button"
          >
            保存
          </button>
          <button 
            type="button"
            class="cancel-button"
            @click="goBack"
          >
            取消
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'

// 定义emit以便发送返回事件
const emit = defineEmits(['back'])

interface CourseAssessments {
  classPerformance: string;
  homework: string;
  midterm: string;
  labWork: string;
  closedExam: string;
  openExam: string;
  presentation: string;
}

interface CourseInfoData {
  title: string;
  keyPoints: string;
  credits: string;
  hours: string;
  assessments: CourseAssessments;
}

const courseInfo = reactive<CourseInfoData>({
  title: '',
  keyPoints: '',
  credits: '',
  hours: '',
  assessments: {
    classPerformance: '0',
    homework: '0',
    midterm: '0',
    labWork: '0',
    closedExam: '0',
    openExam: '0',
    presentation: '0'
  }
})

const totalPercentage = computed(() => {
  const values = Object.values(courseInfo.assessments).map(val => parseFloat(val) || 0)
  return values.reduce((sum, val) => sum + val, 0)
})

const calculateTotal = () => {
  // 转换为数字并计算总和
  Object.keys(courseInfo.assessments).forEach(key => {
    const value = (courseInfo.assessments as any)[key]
    if (value === '') {
      (courseInfo.assessments as any)[key] = '0'
    } else if (isNaN(parseFloat(value))) {
      (courseInfo.assessments as any)[key] = '0'
    }
  })
}

const saveCourseInfo = () => {
  // 实现保存逻辑
  console.log('保存课程信息:', courseInfo)
  // 保存后返回
  goBack()
}

const goBack = () => {
  // 触发返回事件
  emit('back')
}
</script>

<style scoped>
.course-info-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 0;
  height: calc(100vh - 50px);
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
}

.course-info-card {
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 0.75rem;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  padding: 1rem;
  width: 100%;
  max-width: 64rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
  margin: 0;
  display: flex;
  flex-direction: column;
  max-height: 95%;
  position: relative;
  overflow: hidden;
}

/* 添加光效边框 */
.course-info-card::before {
  content: '';
  position: absolute;
  top: -1px;
  left: -1px;
  right: -1px;
  bottom: -1px;
  border-radius: 0.75rem;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.4), 
    rgba(255, 255, 255, 0.1), 
    rgba(52, 152, 219, 0.3), 
    rgba(255, 255, 255, 0.2));
  z-index: -1;
  pointer-events: none;
}

.header-container {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  position: relative;
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
  position: absolute;
  left: 0;
  transition: background-color 0.2s;
}

.back-button:hover {
  background-color: rgba(33, 150, 243, 0.1);
}

.course-title {
  font-size: 1.25rem;
  font-weight: bold;
  text-align: center;
  margin: 0 auto;
}

form {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
}

.form-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group {
  margin-bottom: 0.5rem;
}

.form-label {
  display: block;
  font-size: 0.75rem;
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.form-input {
  width: 100%;
  padding: 0.25rem 0.5rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 0.375rem;
  outline: none;
  height: 2rem;
  background-color: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

.form-input:focus {
  outline: none;
  ring: 2px;
  ring-color: #3b82f6;
}

.form-textarea {
  width: 100%;
  padding: 0.25rem 0.5rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 0.375rem;
  min-height: 60px;
  outline: none;
  resize: none;
  background-color: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

.form-textarea:focus {
  outline: none;
  ring: 2px;
  ring-color: #3b82f6;
}

.credits-hours-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

@media (min-width: 768px) {
  .credits-hours-container {
    grid-template-columns: 1fr 1fr;
  }
}

.table-container {
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 0.5rem;
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  padding: 0.25rem;
  overflow-x: auto;
  flex-shrink: 0;
  max-height: 120px;
}

.assessment-table {
  width: 100%;
  border-collapse: collapse;
}

.table-header {
  border: 1px solid #d1d5db;
  padding: 0.25rem;
  font-size: 0.75rem;
}

.table-cell {
  border: 1px solid #d1d5db;
  padding: 0.25rem;
  font-size: 0.75rem;
}

.percentage-input {
  width: 100%;
  text-align: center;
  border: none;
  outline: none;
  background-color: transparent;
  color: #3498db;
  font-weight: 500;
  transition: all 0.3s ease;
  padding: 2px;
  border-radius: 3px;
}

.percentage-input:focus {
  background-color: rgba(52, 152, 219, 0.1);
  box-shadow: 0 0 5px rgba(52, 152, 219, 0.3);
}

.total-cell {
  text-align: center;
}

.button-container {
  display: flex;
  justify-content: center;
  gap: 1rem;
  padding: 0.5rem 0;
  margin-top: auto;
}

.save-button {
  padding: 0.25rem 1rem;
  background-color: rgba(59, 130, 246, 0.7);
  color: white;
  border-radius: 0.375rem;
  border: none;
  cursor: pointer;
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  box-shadow: 0 4px 12px rgba(31, 38, 135, 0.1);
}

.save-button:hover {
  background-color: #2563eb;
}

.save-button:focus {
  outline: none;
  ring: 2px;
  ring-color: #3b82f6;
}

.cancel-button {
  padding: 0.25rem 1rem;
  background-color: rgba(229, 231, 235, 0.7);
  color: #374151;
  border-radius: 0.375rem;
  border: none;
  cursor: pointer;
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  box-shadow: 0 4px 12px rgba(31, 38, 135, 0.1);
}

.cancel-button:hover {
  background-color: #d1d5db;
}

.cancel-button:focus {
  outline: none;
  ring: 2px;
  ring-color: #9ca3af;
}

@media (min-height: 800px) {
  .form-group {
    margin-bottom: 0.75rem;
  }
  
  .form-textarea {
    min-height: 80px;
  }
  
  .table-container {
    max-height: 150px;
  }
}
</style>
