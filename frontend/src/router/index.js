// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '../views/LoginPage.vue';
import RegisterPage from '../views/RegisterPage.vue';

// 新增：创建一个空组件作为首页占位符（无需实际内容）
const HomePlaceholder = { template: '<div></div>' };

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePlaceholder, // 根路径对应空组件（实际内容在 App.vue 中）
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterPage,
    meta: { title: '注册' }
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

// 恢复导航守卫（用于设置页面标题）
router.beforeEach((to) => {
  if (to.meta.title) {
    document.title = to.meta.title;
  } else {
    document.title = 'Lucky_SMS';
  }
  return true;
});

export default router;