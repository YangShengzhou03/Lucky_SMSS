<template>
  <div class="app-container" :class="{ 'dark-mode': isDarkMode }">
    <!-- 顶部导航 -->
    <header class="top-nav">
      <div class="container">
        <div class="nav-content">
          <div class="nav-logo">
            <span class="logo-text">Lucky SMS</span>
            <span class="logo-badge">Stu</span>
          </div>
          
          <!-- 搜索框 -->
          <div class="nav-search">
            <el-input 
              v-model="searchQuery"
              placeholder="搜索联系人/短信..." 
              size="small" 
              class="search-input"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          
          <div class="nav-actions">
            <!-- 深色模式切换 -->
            <button 
              class="dark-mode-toggle" 
              @click="toggleDarkMode"
              aria-label="Toggle dark mode"
            >
              <el-icon :size="20">
                <Sunny v-if="!isDarkMode" />
                <Moon v-else />
              </el-icon>
            </button>
            
            <!-- 通知中心 -->
            <div class="notification-center">
              <el-badge :value="unreadCount" max="99">
                <button 
                  class="notification-btn" 
                  @click="toggleNotification"
                  aria-label="Notification center"
                >
                  <el-icon :size="20"><Bell /></el-icon>
                </button>
              </el-badge>
              
              <!-- 通知面板 -->
              <el-popover
                v-model:visible="notificationVisible"
                trigger="click"
                placement="bottom-end"
                width="320"
                title="通知中心"
              >
                <template #content>
                  <div class="notification-list">
                    <div 
                      v-for="(item, index) in notifications" 
                      :key="index" 
                      class="notification-item"
                      :class="{ 'unread': item.unread }"
                    >
                      <el-icon :size="16" class="notification-icon">
                        <CheckCircle v-if="item.type === 'success'" />
                        <InfoFilled v-else-if="item.type === 'info'" />
                        <WarningFilled v-else />
                      </el-icon>
                      <div class="notification-content">
                        <p class="notification-title">{{ item.title }}</p>
                        <p class="notification-time">{{ item.time }}</p>
                      </div>
                    </div>
                    <div class="notification-footer" @click="markAllAsRead">
                      全部标为已读
                    </div>
                  </div>
                </template>
                <div />
              </el-popover>
            </div>
            
            <!-- 用户菜单 -->
            <div class="user-area">
              <el-popover
                v-model:visible="userMenuVisible"
                trigger="click"
                placement="bottom-end"
                width="200"
              >
                <template #content>
                  <div class="user-menu">
                    <div class="user-info">
                      <el-avatar :size="48" class="user-avatar-big">
                        <User />
                      </el-avatar>
                      <div class="user-details">
                        <p class="username">用户名</p>
                        <p class="user-role">学生版</p>
                      </div>
                    </div>
                    <el-divider />
                    <el-menu 
                      class="user-dropdown-menu"
                      mode="vertical"
                      @select="handleUserMenuSelect"
                    >
                      <el-menu-item index="profile">
                        <el-icon><User /></el-icon>
                        <span>个人资料</span>
                      </el-menu-item>
                      <el-menu-item index="settings">
                        <el-icon><Setting /></el-icon>
                        <span>账户设置</span>
                      </el-menu-item>
                      <el-menu-item index="help">
                        <el-icon><Question /></el-icon>
                        <span>帮助中心</span>
                      </el-menu-item>
                      <el-divider />
                      <el-menu-item index="logout" class="logout-item">
                        <el-icon><Logout /></el-icon>
                        <span>退出登录</span>
                      </el-menu-item>
                    </el-menu>
                  </div>
                </template>
                <el-avatar 
                  :size="36" 
                  class="user-avatar"
                  @click.stop="userMenuVisible = true"
                >
                  <User />
                </el-avatar>
              </el-popover>
            </div>
            
            <!-- 移动端菜单按钮 -->
            <button 
              class="mobile-menu-btn" 
              v-if="isMobile && showSidebar" 
              @click="toggleMobileSidebar"
              aria-label="Mobile menu"
            >
              <el-icon><Menu /></el-icon>
            </button>
          </div>
        </div>
      </div>
    </header>


    <!-- 页面区域 -->
    <div class="body-container">
      <!-- 侧边栏 -->
      <aside 
        v-if="showSidebar" 
        class="sidebar" 
        :class="{ 
          'sidebar-mobile-open': mobileSidebarOpen
        }"
      >
        <el-menu
          default-active="1"
          class="sidebar-menu"
          @select="handleMenuSelect"
          background-color="var(--sidebar-bg)"
          text-color="var(--sidebar-text)"
          active-text-color="var(--primary-color)"
        >
          <el-menu-item index="1">
            <el-icon><Message /></el-icon>
            <template #title>短信发送</template>
          </el-menu-item>
          <el-menu-item index="2">
            <el-icon><Notebook /></el-icon>
            <template #title>联系人管理</template>
          </el-menu-item>
          <el-menu-item index="3">
            <el-icon><Histogram /></el-icon>
            <template #title>发送统计</template>
          </el-menu-item>
          <el-menu-item index="4">
            <el-icon><Setting /></el-icon>
            <template #title>账户设置</template>
          </el-menu-item>
        </el-menu>
      </aside>
      
      <!-- 主内容区域 -->
      <main class="main-content">
        <!-- 面包屑导航 -->
        <el-breadcrumb v-if="showSidebar" separator=">" class="breadcrumb">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ currentPageName }}</el-breadcrumb-item>
        </el-breadcrumb>
        
        <!-- 主内容容器 -->
        <div class="content-wrapper overflow-hidden">
          <!-- 加载状态 -->
          <div class="content-loading" v-if="isLoading">
            <el-spinner size="40" type="ball-clip-rotate" />
          </div>
          
          <!-- 错误状态 -->
          <div class="content-error" v-else-if="hasError">
            <el-icon size="60" class="error-icon"><Warning /></el-icon>
            <p class="error-message">{{ errorMessage }}</p>
            <el-button size="small" type="primary" @click="retry">
              点击重试
            </el-button>
          </div>
          
          <!-- 正常内容 -->
          <router-view v-else />
        </div>
      </main>
    </div>
    
    <!-- 返回顶部按钮 -->
    <el-backtop 
      v-show="showBackToTop" 
      class="back-to-top" 
      :right="24" 
      :bottom="24"
      :visibility-height="300"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { 
  User, Message, Notebook, Histogram, Setting, 
  Menu, Search, Bell, CheckCircle, InfoFilled, WarningFilled,
  Moon, Sunny, Logout, Question, Warning
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus';

const router = useRouter()
const route = useRoute()

// 状态管理
const showBackToTop = ref(false)
const sidebarCollapsed = ref(false)
const currentPageName = ref('')
const searchQuery = ref('')
const isDarkMode = ref(false)
const notificationVisible = ref(false)
const userMenuVisible = ref(false)
const mobileSidebarOpen = ref(false)
const isMobile = ref(false)
const isLoading = ref(false)
const hasError = ref(false)
const errorMessage = ref('')
const unreadCount = ref(3)

// ✅ 提前定义setPageLoading函数
const setPageLoading = (loading, errorMsg = '') => {
  isLoading.value = loading
  hasError.value = !loading && !!errorMsg
  errorMessage.value = errorMsg || '加载失败，请重试'
}

// 根据路由判断是否显示侧边栏
const showSidebar = computed(() => {
  return !['/login', '/register'].includes(route.path)
})

// 监听路由变化更新页面标题
watch(route, (newRoute) => {
  setPageLoading(true)
  setTimeout(() => {
    const pageTitles = {
      '/': '短信发送',
      '/contacts': '联系人管理',
      '/statistics': '发送统计',
      '/settings': '账户设置',
      '/login': '登录',
      '/register': '注册'
    }
    currentPageName.value = pageTitles[newRoute.path] || '首页'
    setPageLoading(false)
  }, 500)
}, { immediate: true })

// 其他代码保持不变...

// 处理滚动事件
const handleScroll = (e) => {
  const scrollTop = e.target.scrollTop
  showBackToTop.value = scrollTop > 300
}

// 移动端侧边栏切换
const toggleMobileSidebar = () => {
  mobileSidebarOpen.value = !mobileSidebarOpen.value
}

// 菜单选择
const handleMenuSelect = (index) => {
  const routeMap = {
    '1': '/',
    '2': '/contacts',
    '3': '/statistics',
    '4': '/settings'
  }
  if (routeMap[index]) {
    router.push(routeMap[index])
    // 移动端点击菜单后自动收起侧边栏
    if (isMobile.value) {
      mobileSidebarOpen.value = false
    }
  }
}

// 用户菜单选择
const handleUserMenuSelect = (index) => {
  userMenuVisible.value = false
  switch (index) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'help':
      router.push('/help')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 处理搜索
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    // 执行搜索逻辑
    console.log('搜索:', searchQuery.value)
    // 这里可以添加搜索结果展示逻辑
  }
}

// 切换深色模式
const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  localStorage.setItem('isDarkMode', isDarkMode.value)
}

// 登出处理
const handleLogout = () => {
  // 显示确认对话框
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 执行登出逻辑
    console.log('用户登出')
    router.push('/login')
  }).catch(() => {
    // 取消操作
  })
}

// 重试加载
const retry = () => {
  setPageLoading(true)
  // 模拟重新加载
  setTimeout(() => {
    setPageLoading(false)
  }, 1000)
}

// 检测屏幕尺寸
const checkScreenSize = () => {
  isMobile.value = window.innerWidth < 768
  // 小屏幕下默认收起侧边栏
  if (isMobile.value) {
    mobileSidebarOpen.value = false
  }
}

// 生命周期
onMounted(() => {
  // 从本地存储恢复设置
  const savedSidebarState = localStorage.getItem('sidebarCollapsed')
  if (savedSidebarState !== null) {
    sidebarCollapsed.value = savedSidebarState === 'true'
  }
  
  const savedDarkMode = localStorage.getItem('isDarkMode')
  if (savedDarkMode !== null) {
    isDarkMode.value = savedDarkMode === 'true'
  }
  
  // 监听主内容区域滚动
  const mainContent = document.querySelector('.main-content')
  if (mainContent) {
    mainContent.addEventListener('scroll', handleScroll)
  }
  
  // 监听窗口大小变化
  window.addEventListener('resize', checkScreenSize)
  checkScreenSize()
  
  // 模拟页面加载错误
  // setPageLoading(true)
  // setTimeout(() => {
  //   setPageLoading(false, '服务器连接超时')
  // }, 2000)
})

onUnmounted(() => {
  const mainContent = document.querySelector('.main-content')
  if (mainContent) {
    mainContent.removeEventListener('scroll', handleScroll)
  }
  
  window.removeEventListener('resize', checkScreenSize)
})

// 点击外部关闭下拉菜单
const closeDropdowns = (e) => {
  const userMenuEl = document.querySelector('.user-area .el-popover')
  const notificationEl = document.querySelector('.notification-center .el-popover')
  
  if (userMenuVisible.value && userMenuEl && !userMenuEl.contains(e.target)) {
    userMenuVisible.value = false
  }
  
  if (notificationVisible.value && notificationEl && !notificationEl.contains(e.target)) {
    notificationVisible.value = false
  }
}

// 添加点击外部关闭事件
document.addEventListener('click', closeDropdowns)

// 组件卸载时移除事件
onUnmounted(() => {
  document.removeEventListener('click', closeDropdowns)
})
</script>

<style scoped>
/* 全局样式变量 */
:root {
  --primary-color: #3b82f6;
  --primary-light: #dbeafe;
  --primary-dark: #2563eb;
  --secondary-color: #64748b;
  --success-color: #10b981;
  --warning-color: #f59e0b;
  --danger-color: #ef4444;
  --gray-50: #f8fafc;
  --gray-100: #f1f5f9;
  --gray-200: #e2e8f0;
  --gray-300: #cbd5e1;
  --gray-400: #94a3b8;
  --gray-500: #64748b;
  --gray-600: #475569;
  --gray-700: #334155;
  --gray-800: #1e293b;
  --gray-900: #0f172a;
  --white: #ffffff;
  --sidebar-bg: var(--white);
  --sidebar-text: var(--gray-600);
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --border-radius: 8px;
  --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 深色模式变量 */
.dark-mode {
  --primary-color: #60a5fa;
  --primary-light: #1e40af;
  --primary-dark: #93c5fd;
  --gray-50: #0f172a;
  --gray-100: #1e293b;
  --gray-200: #334155;
  --gray-300: #475569;
  --gray-400: #64748b;
  --gray-500: #94a3b8;
  --gray-600: #cbd5e1;
  --gray-700: #e2e8f0;
  --gray-800: #f1f5f9;
  --gray-900: #f8fafc;
  --white: #1e293b;
  --sidebar-bg: #0f172a;
  --sidebar-text: var(--gray-400);
}

/* 基础样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app-container {
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  line-height: 1.5;
  color: var(--gray-800);
  background-color: var(--gray-50);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止页面级滚动条 */
}

.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 顶部导航 */
.top-nav {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  padding: 12px 0;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 1px solid var(--gray-200);
  transition: var(--transition);
}

.dark-mode .top-nav {
  background: rgba(30, 41, 59, 0.95);
  border-bottom: 1px solid var(--gray-800);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 44px;
}

.nav-logo {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--primary-color);
  letter-spacing: 0.3px;
  transition: var(--transition);
}

.logo-badge {
  font-size: 0.75rem;
  background: var(--success-color);
  color: var(--white);
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 600;
  transform: translateY(-4px);
  box-shadow: 0 2px 4px rgba(16, 185, 129, 0.2);
}

/* 搜索框 */
.nav-search {
  flex: 1;
  max-width: 300px;
  margin: 0 24px;
}

.search-input {
  border-radius: 999px;
  border: 1px solid var(--gray-200);
  transition: var(--transition);
}

.search-input:hover {
  border-color: var(--primary-color);
}

.dark-mode .search-input {
  background-color: var(--gray-100);
  border-color: var(--gray-300);
  color: var(--gray-800);
}

/* 右上角操作区 */
.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 侧边栏伸缩按钮样式 */
.sidebar-toggle,
.dark-mode-toggle,
.notification-btn,
.mobile-menu-btn {
  background: none;
  border: none;
  color: var(--gray-600);
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
}

.sidebar-toggle:hover,
.dark-mode-toggle:hover,
.notification-btn:hover,
.mobile-menu-btn:hover {
  background-color: var(--gray-100);
  color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.dark-mode .sidebar-toggle:hover,
.dark-mode .dark-mode-toggle:hover,
.dark-mode .notification-btn:hover,
.dark-mode .mobile-menu-btn:hover {
  background-color: var(--gray-200);
}

/* 通知中心 */
.notification-center {
  position: relative;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
  padding: 8px 0;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: var(--transition);
}

.notification-item:hover {
  background-color: var(--gray-50);
}

.dark-mode .notification-item:hover {
  background-color: var(--gray-200);
}

.notification-item.unread {
  background-color: var(--primary-light);
}

.dark-mode .notification-item.unread {
  background-color: var(--primary-dark);
  background-opacity: 0.1;
}

.notification-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 0.875rem;
  color: var(--gray-800);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 0.75rem;
  color: var(--gray-500);
  margin-top: 2px;
}

.notification-footer {
  padding: 8px 16px;
  text-align: center;
  font-size: 0.875rem;
  color: var(--primary-color);
  cursor: pointer;
  transition: var(--transition);
}

.notification-footer:hover {
  background-color: var(--gray-50);
}

.dark-mode .notification-footer:hover {
  background-color: var(--gray-200);
}

/* 用户菜单 */
.user-area {
  position: relative;
}

.user-avatar {
  cursor: pointer;
  background-color: var(--primary-light);
  color: var(--primary-color);
  transition: var(--transition);
  border: 2px solid transparent;
  box-shadow: 0 2px 5px rgba(59, 130, 246, 0.1);
}

.user-avatar:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
  border-color: var(--primary-color);
}

.user-avatar-big {
  background-color: var(--primary-light);
  color: var(--primary-color);
}

.user-menu {
  padding: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 1rem;
  font-weight: 500;
  color: var(--gray-800);
}

.user-role {
  font-size: 0.875rem;
  color: var(--gray-500);
}

.user-dropdown-menu {
  border: none !important;
  background: transparent !important;
}

.logout-item {
  color: var(--danger-color) !important;
}

.logout-item:hover {
  background-color: rgba(239, 68, 68, 0.1) !important;
}

/* 主体布局 */
.body-container {
  display: flex;
  flex: 1;
  height: calc(100vh - 68px); /* 减去顶部导航高度 */
  overflow: hidden; /* 防止容器本身出现滚动条 */
}

/* 侧边栏样式 */
.sidebar {
  width: 240px;
  background: var(--sidebar-bg);
  border-right: 1px solid var(--gray-200);
  display: flex;
  flex-direction: column;
  transition: width var(--transition);
  z-index: 100;
  box-shadow: 1px 0 3px rgba(0, 0, 0, 0.02);
  
  /* 移动端侧边栏 */
  position: fixed;
  left: 0;
  top: 68px;
  height: calc(100vh - 68px);
  transform: translateX(-100%);
  transition: transform var(--transition);
}

.sidebar.sidebar-mobile-open {
  transform: translateX(0);
  box-shadow: var(--shadow-lg);
}

@media (min-width: 768px) {
  .sidebar {
    position: static;
    transform: none;
  }
}

.sidebar.sidebar-collapsed {
  width: 64px;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
  padding-top: 16px;
}

/* 优化菜单样式 */
:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  padding: 0 24px !important;
  margin: 0 8px !important;
  border-radius: var(--border-radius);
  margin-bottom: 4px !important;
  transition: var(--transition) !important;
}

:deep(.el-menu-item:hover:not(.is-active)) {
  background-color: var(--gray-50) !important;
  color: var(--primary-color) !important;
}

:deep(.el-menu-item.is-active) {
  background-color: var(--primary-light) !important;
  color: var(--primary-color) !important;
  font-weight: 500 !important;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.1) !important;
  border-left: 3px solid var(--primary-color) !important;
  padding-left: 21px !important;
  transform: translateX(2px);
}

:deep(.el-menu-item .el-icon) {
  margin-right: 12px !important;
  font-size: 1.1rem !important;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 24px;
  background-color: var(--gray-50);
  overflow-y: auto; /* 仅主内容区域有滚动条 */
  transition: var(--transition);
  scroll-behavior: smooth; /* 平滑滚动 */
  /* 移动端侧边栏打开时添加遮罩 */
  position: relative;
}

.main-content::after {
  content: '';
  position: fixed;
  top: 68px;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 99;
  opacity: 0;
  pointer-events: none;
  transition: opacity var(--transition);
}

.sidebar-mobile-open + .main-content::after {
  opacity: 1;
  pointer-events: auto;
}

@media (min-width: 768px) {
  .main-content::after {
    display: none;
  }
}

/* 自定义滚动条样式 */
.main-content::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.main-content::-webkit-scrollbar-track {
  background: var(--gray-100);
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb {
  background: var(--gray-300);
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: var(--gray-400);
}

/* 面包屑导航 */
.breadcrumb {
  margin-bottom: 20px;
  font-size: 0.875rem;
  color: var(--gray-500);
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--primary-color) !important;
  font-weight: 500;
}

/* 内容容器 */
.content-wrapper {
  background: var(--white);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
  padding: 24px;
  min-height: calc(100% - 44px); /* 减去面包屑高度 */
  transition: var(--transition);
  position: relative;
}

/* 主页面添加overflow-hidden */
.overflow-hidden {
  overflow: hidden;
}

.content-wrapper:hover {
  box-shadow: var(--shadow-md);
}

/* 加载状态 */
.content-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.dark-mode .content-loading {
  background-color: rgba(30, 41, 59, 0.8);
}

/* 错误状态 */
.content-error {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--white);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  z-index: 10;
}

.error-icon {
  color: var(--warning-color);
}

.error-message {
  color: var(--gray-600);
}

/* 返回顶部按钮 */
.back-to-top {
  background: var(--primary-color) !important;
  color: white !important;
  width: 40px;
  height: 40px;
  box-shadow: var(--shadow-md) !important;
  transition: var(--transition) !important;
  border-radius: 50% !important;
}

.back-to-top:hover {
  background: var(--primary-dark) !important;
  transform: translateY(-3px) scale(1.05);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .nav-search {
    max-width: 200px;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .content-wrapper {
    padding: 16px;
  }
}
</style>