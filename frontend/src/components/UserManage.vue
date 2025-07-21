<script setup lang="ts">
import { ref, defineEmits, onMounted } from 'vue';
import { adminApi, type User as BaseUser } from '../api/admin';

// 扩展用户接口，添加UI所需的属性
interface User extends BaseUser {
  isEditing?: boolean;
  isEmailEditing?: boolean; // 添加邮箱编辑状态
  isSelected?: boolean;
  isFlipped?: boolean;
  detailedInfo?: BaseUser;
  originalUsername?: string; // 保存编辑前的原始用户名
  originalEmail?: string;    // 保存编辑前的原始邮箱
}

// 定义事件
const emit = defineEmits(['user-selected']);

// 用户数据
const users = ref<User[]>([]);

// 是否处于删除模式
const isDeleteMode = ref(false);

// 加载状态
const loading = ref(false);

// 错误信息
const errorMessage = ref('');

// 成功信息和显示状态
const successMessage = ref('');
const showSuccess = ref(false);

// 显示成功消息
const showSuccessMessage = (message: string) => {
  successMessage.value = message;
  showSuccess.value = true;
  
  // 3秒后自动隐藏
  setTimeout(() => {
    showSuccess.value = false;
  }, 3000);
};

// 初始化用户列表
const initializeUsers = async () => {
  loading.value = true;
  errorMessage.value = '';
  
  try {
    const userList = await adminApi.getUserList();
    users.value = userList.map(user => ({
      ...user,
      isEditing: false,
      isEmailEditing: false,
      isSelected: false,
      isFlipped: false
    })).sort((a, b) => (b.id || 0) - (a.id || 0));
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '获取用户列表失败';
    console.error('获取用户列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 组件挂载时初始化用户列表
onMounted(initializeUsers);

// 添加新用户
const addNewUser = async () => {
  const newUser: Omit<BaseUser, 'id'> = {
    username: '新用户',
    email: `user${Date.now()}@example.com`,
    role: 'teacher'
  };
  
  loading.value = true;
  errorMessage.value = '';
  
  try {
    // 添加用户
    await adminApi.addUser(newUser as BaseUser);
    
    // 添加用户成功后直接重新获取用户列表
    try {
      const userList = await adminApi.getUserList();
      users.value = userList.map(user => ({
        ...user,
        isEditing: false,
        isEmailEditing: false,
        isSelected: false,
        isFlipped: false
      })).sort((a, b) => (b.id || 0) - (a.id || 0));
    } catch (error) {
      console.error('重新获取用户列表失败:', error);
    }
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '添加用户失败';
    console.error('添加用户失败:', error);
  } finally {
    loading.value = false;
  }
};

// 切换删除模式
const toggleDeleteMode = () => {
  isDeleteMode.value = !isDeleteMode.value;
  
  // 退出删除模式时，取消所有选择
  if (!isDeleteMode.value) {
    users.value.forEach(user => {
      user.isSelected = false;
    });
  }
};

// 切换用户选择状态
const toggleUserSelection = (user: User) => {
  if (isDeleteMode.value) {
    user.isSelected = !user.isSelected;
  }
};

// 删除选中的用户
const deleteSelectedUsers = async () => {
  const selectedUsers = users.value.filter(user => user.isSelected);
  
  if (selectedUsers.length === 0) {
    errorMessage.value = '请选择要删除的用户';
    return;
  }
  
  loading.value = true;
  errorMessage.value = '';
  
  try {
    // 并行删除所有选中的用户
    const deletePromises = selectedUsers.map(user => 
      user.id ? adminApi.deleteUser(user.id) : Promise.resolve()
    );
    
    await Promise.all(deletePromises);
    
    // 直接从本地列表中移除被删除的用户，而不是重新加载整个列表
    const selectedUserIds = selectedUsers.map(user => user.id);
    users.value = users.value.filter(user => !selectedUserIds.includes(user.id));
    
    isDeleteMode.value = false;
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '删除用户失败';
    console.error('删除用户失败:', error);
  } finally {
    loading.value = false;
  }
};

// 点击用户卡片
const handleUserClick = async (user: User) => {
  if (isDeleteMode.value) {
    toggleUserSelection(user);
  } else if (user.isEditing || user.isEmailEditing) {
    // 如果正在编辑，则不执行翻转
    return;
  } else {
    // 翻转卡片
    user.isFlipped = !user.isFlipped;
    
    // 如果是翻转到背面且没有详细信息，则获取用户详情
    if (user.isFlipped && !user.detailedInfo && user.id) {
      try {
        const userInfo = await adminApi.getUserInfo(user.id);
        user.detailedInfo = userInfo;
      } catch (error) {
        console.error('获取用户详情失败:', error);
        // 如果获取失败，将卡片翻转回来
        user.isFlipped = false;
      }
    }
  }
};

// 角色映射
const roleMap: Record<string, string> = {
  'admin': '管理员',
  'teacher': '教师',
  'student': '学生'
};

// 角色颜色映射
const roleColorMap: Record<string, string> = {
  'admin': '#e74c3c',
  'teacher': '#3498db',
  'student': '#2ecc71'
};

// 编辑用户
const editUser = (user: User) => {
  if (!isDeleteMode.value) {
    user.isEditing = true;
    user.originalUsername = user.username; // 保存原始用户名
  } else {
    toggleUserSelection(user);
  }
};

// 完成编辑
const finishEdit = async (user: User) => {
  user.isEditing = false;
  
  // 检查用户名是否有变化
  if (user.originalUsername !== user.username) {
    try {
      // 调用API更新用户名
      if (user.id) {
        const result = await adminApi.updateUsername(user.id, user.username);
        console.log('用户名已更新:', result);
        
        // 如果有详细信息，同步更新
        if (user.detailedInfo) {
          user.detailedInfo.username = user.username;
        }
        
        // 显示成功消息
        showSuccessMessage('用户名更新成功');
      }
    } catch (error) {
      errorMessage.value = error instanceof Error ? error.message : '更新用户信息失败';
      console.error('更新用户名失败:', error);
      
      // 如果更新失败，恢复为原始用户名
      if (user.originalUsername) {
        user.username = user.originalUsername;
      }
    }
  }
  
  // 清除原始用户名
  user.originalUsername = undefined;
};

// 编辑用户邮箱
const editEmail = (user: User, event: Event) => {
  event.stopPropagation(); // 阻止事件冒泡
  if (!isDeleteMode.value) {
    user.isEmailEditing = true;
    user.originalEmail = user.email; // 保存原始邮箱
  } else {
    toggleUserSelection(user);
  }
};

// 完成邮箱编辑
const finishEmailEdit = async (user: User) => {
  user.isEmailEditing = false;
  
  // 检查邮箱是否有变化
  if (user.originalEmail !== user.email) {
    try {
      // 调用API更新邮箱
      if (user.id) {
        const result = await adminApi.updateEmail(user.id, user.email);
        console.log('邮箱已更新:', result);
        
        // 如果有详细信息，同步更新
        if (user.detailedInfo) {
          user.detailedInfo.email = user.email;
        }
        
        // 显示成功消息
        showSuccessMessage('邮箱更新成功');
      }
    } catch (error) {
      errorMessage.value = error instanceof Error ? error.message : '更新邮箱失败';
      console.error('更新邮箱失败:', error);
      
      // 如果更新失败，恢复为原始邮箱
      if (user.originalEmail) {
        user.email = user.originalEmail;
      }
    }
  }
  
  // 清除原始邮箱
  user.originalEmail = undefined;
};

// 自定义指令：自动聚焦并全选
const vFocus = {
  mounted(el: HTMLInputElement) {
    el.focus();
    el.select();
  }
};
</script>

<template>
  <div class="user-manage">
    <div class="user-header">
      <h2 class="user-title">用户管理 {{ users.length }}</h2>
      <div class="user-actions">
        <button 
          @click="toggleDeleteMode" 
          class="delete-button"
          :class="{ 'active': isDeleteMode }"
        >
          {{ isDeleteMode ? '取消' : '删除' }}
        </button>
        <button 
          v-if="isDeleteMode" 
          @click="deleteSelectedUsers" 
          class="confirm-delete-button"
        >
          确认删除
        </button>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      正在加载用户，请稍候...
    </div>
    
    <!-- 错误信息 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <!-- 成功信息 -->
    <div v-if="showSuccess" class="success-message">
      {{ successMessage }}
    </div>

    <div class="user-grid" v-if="!loading">
      <!-- 添加按钮卡片 -->
      <div class="user-card add-card" @click="addNewUser">
        <div class="add-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
        </div>
        <div class="add-text">添加用户</div>
      </div>
      
      <!-- 用户卡片 -->
      <div 
        v-for="user in users" 
        :key="user.id" 
        class="user-card" 
        :class="{
          'selected': user.isSelected,
          'flipped': user.isFlipped
        }"
        @click="handleUserClick(user)"
      >
        <div class="card-face-wrapper">
          <!-- 卡片正面 -->
          <div class="card-face card-front">
            <div class="user-avatar">
              <svg xmlns="http://www.w3.org/2000/svg" width="70" height="70" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="user-icon">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              <div v-if="isDeleteMode" class="selection-indicator">
                <svg v-if="user.isSelected" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
              </div>
            </div>
            
            <div class="user-info">
              <div class="user-name-section">
                <h3 v-if="!user.isEditing" @click.stop="editUser(user)">
                  {{ user.username }}
                </h3>
                <input 
                  v-else 
                  type="text" 
                  v-model="user.username" 
                  @blur="finishEdit(user)"
                  @keyup.enter="finishEdit(user)"
                  @click.stop
                  class="name-input"
                  v-focus
                />
              </div>
              
              <div class="user-email">
                <template v-if="!user.isEmailEditing">
                  <span @click.stop="editEmail(user, $event)" class="email-text">
                    {{ user.email }}
                  </span>
                </template>
                <input 
                  v-else 
                  type="email" 
                  v-model="user.email" 
                  @blur="finishEmailEdit(user)"
                  @keyup.enter="finishEmailEdit(user)"
                  @click.stop
                  class="email-input"
                  v-focus
                />
              </div>
              
              <div class="user-role">
                <span 
                  class="role-badge" 
                  :style="{ backgroundColor: roleColorMap[user.role || 'student'] }"
                >
                  {{ roleMap[user.role || 'student'] }}
                </span>
              </div>
            </div>
          </div>

          <!-- 卡片背面 -->
          <div class="card-face card-back">
                      <div class="card-back-header">
            <h3>用户详情</h3>
          </div>
            
            <div class="user-details" v-if="user.detailedInfo">
              <div class="detail-item">
                <span class="detail-label">用户名:</span>
                <span class="detail-value">{{ user.detailedInfo.username }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">邮箱:</span>
                <span class="detail-value">{{ user.detailedInfo.email }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">角色:</span>
                <span class="detail-value">{{ roleMap[user.detailedInfo.role || 'student'] }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">状态:</span>
                <span class="detail-value">{{ user.detailedInfo.status || '正常' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">创建时间:</span>
                <span class="detail-value">{{ user.detailedInfo.createdAt ? new Date(user.detailedInfo.createdAt).toLocaleString() : '未知' }}</span>
              </div>
            </div>
            <div v-else class="loading-details">
              加载中...
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-manage {
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
}

.user-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.user-title {
  font-size: 24px;
  margin: 0;
  color: #333;
}

.user-actions {
  display: flex;
  gap: 10px;
}

.delete-button {
  padding: 6px 12px;
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
  border: 1px solid rgba(231, 76, 60, 0.2);
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.delete-button:hover {
  background: rgba(231, 76, 60, 0.2);
}

.delete-button.active {
  background: #e74c3c;
  color: white;
}

.confirm-delete-button {
  padding: 6px 12px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.confirm-delete-button:hover {
  background: #c0392b;
}

.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  width: 100%;
}

.user-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.2);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  position: relative;
  min-height: 290px;
  height: 100%;
  perspective: 1000px; /* 3D效果视角 */
  transform-style: preserve-3d; /* 保持3D效果 */
}

.user-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.3);
  background: rgba(255, 255, 255, 0.95);
}

.user-card.selected {
  box-shadow: 0 0 0 2px #e74c3c, 0 12px 40px rgba(231, 76, 60, 0.3);
  background: rgba(255, 255, 255, 0.95);
}

.user-card.flipped .card-front {
  transform: rotateY(180deg);
}

.user-card.flipped .card-back {
  transform: rotateY(0deg);
}

.card-face-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
}

.card-face {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  transition: transform 0.6s ease-in-out;
}

.card-front {
  transform: rotateY(0deg);
}

.card-back {
  transform: rotateY(-180deg);
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 15px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.user-avatar {
  width: 100%;
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #a6d4fa 0%, #48d1cc 50%, #5dade2 100%);
  position: relative;
}

.user-icon {
  color: white;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  padding: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.user-icon:hover {
  background: rgba(255, 255, 255, 0.35);
  transform: scale(1.05);
}

.selection-indicator {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(231, 76, 60, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-info {
  padding: 16px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-name-section h3 {
  font-size: 16px;
  margin: 0;
  color: #333;
  text-align: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
  font-weight: 600;
}

.user-name-section h3:hover {
  background: rgba(72, 209, 204, 0.1);
}

.name-input {
  width: 100%;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  text-align: center;
  border: none;
  border-bottom: 1px solid #48d1cc;
  background: transparent;
  padding: 4px 8px;
  margin: 0;
  outline: none;
  border-radius: 6px 6px 0 0;
  transition: all 0.2s ease;
}

.name-input:focus {
  box-shadow: 0 4px 12px rgba(72, 209, 204, 0.2);
  background: rgba(255, 255, 255, 0.95);
}

.user-email {
  font-size: 13px;
  color: #666;
  text-align: center;
  background: rgba(248, 249, 250, 0.8);
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  margin-bottom: 2px;
  word-break: break-all;
  overflow: visible;
  min-height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.email-text {
  cursor: pointer;
  transition: color 0.2s ease;
}

.email-text:hover {
  color: #e74c3c;
}

.email-input {
  width: 100%;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  text-align: center;
  border: none;
  border-bottom: 1px solid #48d1cc;
  background: transparent;
  padding: 4px 8px;
  margin: 0;
  outline: none;
  border-radius: 6px 6px 0 0;
  transition: all 0.2s ease;
}

.email-input:focus {
  box-shadow: 0 4px 12px rgba(72, 209, 204, 0.2);
  background: rgba(255, 255, 255, 0.95);
}

.user-role {
  display: flex;
  justify-content: center;
  align-items: center;
}

.role-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 20px;
  color: white;
  font-size: 12px;
  font-weight: 500;
  text-align: center;
  min-width: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.add-card {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  background: rgba(166, 212, 250, 0.3);
  color: #1caafc;
  min-height: 226px;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 8px 32px rgba(72, 209, 204, 0.2);
  gap: 12px;
}

.add-card:hover {
  background: rgba(166, 212, 250, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 12px 40px rgba(72, 209, 204, 0.3);
  transform: translateY(-5px);
}

.add-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-text {
  font-size: 16px;
  font-weight: 500;
}

.loading-state {
  text-align: center;
  padding: 20px;
  font-size: 16px;
  color: #48d1cc;
}

.error-message {
  text-align: center;
  padding: 10px;
  margin-bottom: 20px;
  background-color: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
  border: 1px solid rgba(231, 76, 60, 0.2);
  border-radius: 6px;
}

.success-message {
  text-align: center;
  padding: 10px;
  margin-bottom: 20px;
  background-color: rgba(46, 204, 113, 0.1);
  color: #2ecc71;
  border: 1px solid rgba(46, 204, 113, 0.2);
  border-radius: 6px;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-back-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-back-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.back-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 5px;
  color: #666;
  transition: color 0.2s ease;
}

.back-button:hover {
  color: #e74c3c;
}

.user-details {
  padding: 10px;
  background: rgba(248, 249, 250, 0.9);
  border-radius: 8px;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
  min-height: 150px; /* 增加高度 */
  overflow: auto;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 14px;
  color: #555;
  white-space: nowrap;
  gap: 10px;
}

.detail-label {
  font-weight: 500;
  color: #333;
}

.detail-value {
  font-weight: 600;
  color: #2c3e50;
}

.editable {
  cursor: pointer;
  transition: color 0.2s ease;
}

.editable:hover {
  color: #e74c3c;
}

.detail-value-container {
  display: flex;
  align-items: center;
  gap: 5px;
}

.detail-input {
  width: 100%;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  text-align: center;
  border: none;
  border-bottom: 1px solid #48d1cc;
  background: transparent;
  padding: 4px 8px;
  margin: 0;
  outline: none;
  border-radius: 6px 6px 0 0;
  transition: all 0.2s ease;
}

.detail-input:focus {
  box-shadow: 0 4px 12px rgba(72, 209, 204, 0.2);
  background: rgba(255, 255, 255, 0.95);
}

.loading-details {
  text-align: center;
  padding: 20px;
  color: #95a5a6;
}
</style>
