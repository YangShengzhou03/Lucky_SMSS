import { createRouter, createWebHistory } from 'vue-router';
import App from '../App.vue'

const routes = [
  {
    path: '/',
    name: 'App',
    component: App,
    meta: { title: '首页' }
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 全局前置守卫
router.beforeEach((to, next) => {
  if (to.meta.title) {
    document.title = to.meta.title;
  } else {
    document.title = 'Lucky_SMS';
  }
  next();
});

export default router
