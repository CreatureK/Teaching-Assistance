import { createRouter, createWebHistory } from 'vue-router'
import * as authApi from '../hooks/api/auth'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/HomeView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/AuthForm.vue')
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../views/Admin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  // 如果访问不存在的路由，重定向到首页
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查用户是否已登录
    if (!authApi.checkAuthentication()) {
      // 未登录，重定向到登录页
      next({ name: 'login' })
    } else {
      // 已登录，检查用户角色
      const isAdmin = localStorage.getItem('isAdmin') === 'true'
      
      if (to.path === '/' && isAdmin) {
        // 管理员访问首页，重定向到管理页
        next('/admin')
      } else if (to.matched.some(record => record.meta.requiresAdmin) && !isAdmin) {
        // 非管理员访问需要管理员权限的页面，重定向到首页
        next('/')
      } else {
        // 其他情况允许访问
        next()
      }
    }
  } else {
    // 不需要认证的路由，直接访问
    next()
  }
})

export default router 