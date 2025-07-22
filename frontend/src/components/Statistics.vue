<template>
    <div class="statistics-container">
      <div class="title-section">
        <h2>系统数据统计</h2>
        <el-button type="primary" size="small" @click="fetchData">
          <el-icon><Refresh /></el-icon>刷新数据
        </el-button>
      </div>
  
      <!-- 顶部卡片统计 -->
      <el-row :gutter="20" class="stat-cards">
        <el-col :xs="12" :sm="6" :md="6" :lg="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-card-inner">
              <div class="stat-icon user-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">总用户数</div>
                <div class="stat-value">{{ dashboardData.userStats?.totalUsers || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6" :md="6" :lg="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-card-inner">
              <div class="stat-icon course-icon">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">总课程数</div>
                <div class="stat-value">{{ dashboardData.courseStats?.totalCourses || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6" :md="6" :lg="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-card-inner">
              <div class="stat-icon teacher-icon">
                <el-icon><Avatar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">教师用户</div>
                <div class="stat-value">{{ dashboardData.userStats?.teacherCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6" :md="6" :lg="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-card-inner">
              <div class="stat-icon admin-icon">
                <el-icon><Setting /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">管理员用户</div>
                <div class="stat-value">{{ dashboardData.userStats?.adminCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </template>
  
  <script>
  import { getDashboardData } from '@/api/admin'
  import { User, Reading, Avatar, Setting, Refresh } from '@element-plus/icons-vue'
  
  export default {
    name: 'Statistics',
    components: {
      User,
      Reading,
      Avatar,
      Setting,
      Refresh
    },
    data() {
      return {
        dashboardData: {
          userStats: {
            totalUsers: 0,
            teacherCount: 0,
            adminCount: 0
          },
          courseStats: {
            totalCourses: 0,
            pendingCount: 0,
            approvedCount: 0,
            rejectedCount: 0
          }
        },
        loading: false
      }
    },
    mounted() {
      this.fetchData()
    },
    methods: {
      async fetchData() {
        this.loading = true
        try {
          const res = await getDashboardData()
          this.dashboardData = res.data
        } catch (error) {
          console.error('获取统计数据失败:', error)
          this.$message.error('获取统计数据失败')
        } finally {
          this.loading = false
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .statistics-container {
    padding: 20px;
  }
  
  .title-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .stat-cards {
    margin-bottom: 20px;
  }
  
  .stat-card {
    height: 120px;
    margin-bottom: 20px;
  }
  
  .stat-card-inner {
    display: flex;
    align-items: center;
    height: 100%;
  }
  
  .stat-icon {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 15px;
  }
  
  .stat-icon .el-icon {
    font-size: 30px;
    color: #fff;
  }
  
  .user-icon {
    background-color: #409EFF;
  }
  
  .course-icon {
    background-color: #67C23A;
  }
  
  .teacher-icon {
    background-color: #E6A23C;
  }
  
  .admin-icon {
    background-color: #F56C6C;
  }
  
  .stat-info {
    flex-grow: 1;
  }
  
  .stat-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 5px;
  }
  
  .stat-value {
    font-size: 28px;
    font-weight: bold;
  }
  </style>