<template>
  <div class="app-container" :class="{ 'dark-mode': isDarkMode }">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="container">
        <div class="nav-content">
          <!-- 左侧Logo区域 -->
          <div class="nav-left">
            <div class="nav-logo">
              <span class="logo-text">Lucky SMS</span>
              <span class="logo-badge">STU</span>
            </div>
          </div>

          <!-- 中间搜索区域 -->
          <div class="nav-center">
            <div class="nav-search">
              <el-input 
                v-model="searchQuery" 
                placeholder="搜索课程/成绩/图书..." 
                size="small" 
                class="search-input"
                @keyup.enter="handleSearch" 
                clearable
                :class="{ 'focus-visible': isSearchFocused }"
              >
                <template #prefix>
                  <el-icon class="search-icon">
                    <Search />
                  </el-icon>
                </template>
              </el-input>
            </div>
          </div>

          <!-- 右侧功能区 -->
          <div class="nav-right">
            <!-- 暗色模式切换 -->
            <button 
              class="dark-mode-toggle action-btn" 
              @click="toggleDarkMode" 
              aria-label="切换暗色模式"
              :title="isDarkMode ? '切换至亮色模式' : '切换至暗色模式'"
            >
              <el-icon :size="20" :class="isDarkMode ? 'icon-moon' : 'icon-sun'">
                <Sunny v-if="!isDarkMode" />
                <Moon v-else />
              </el-icon>
            </button>

            <!-- 通知中心 -->
            <div class="notification-center">
              <el-badge :value="unreadCount" max="99" :hidden="unreadCount === 0">
                <button 
                  class="notification-btn action-btn" 
                  @click="toggleNotification"
                  aria-label="通知中心"
                  title="通知中心"
                >
                  <el-icon :size="20">
                    <Bell />
                  </el-icon>
                </button>
              </el-badge>

              <el-popover 
                v-model:visible="notificationVisible" 
                trigger="click" 
                placement="bottom-end" 
                width="320"
                title="通知中心"
                :close-on-click-outside="true"
              >
                <template #content>
                  <div class="notification-list">
                    <div 
                      v-for="(item, index) in notifications" 
                      :key="item.id" 
                      class="notification-item"
                      :class="{ 'unread': item.unread }" 
                      @click="readNotification(index)"
                    >
                      <el-icon :size="16" class="notification-icon" :class="`type-${item.type}`">
                        <CheckCircle v-if="item.type === 'success'" />
                        <InfoFilled v-else-if="item.type === 'info'" />
                        <WarningFilled v-else />
                      </el-icon>
                      <div class="notification-content">
                        <p class="notification-title">{{ item.title }}</p>
                        <p class="notification-time">{{ item.time }}</p>
                      </div>
                      <el-icon 
                        class="notification-close" 
                        @click.stop="removeNotification(index)"
                        title="删除通知"
                      >
                        <Close />
                      </el-icon>
                    </div>
                    <div 
                      class="notification-footer" 
                      @click="markAllAsRead"
                      v-if="notifications.length > 0"
                    >
                      全部标为已读
                    </div>
                    <div class="notification-empty" v-else>
                      暂无通知
                    </div>
                  </div>
                </template>
              </el-popover>
            </div>

            <!-- 用户区域 -->
            <div class="user-area">
              <el-popover 
                v-model:visible="userMenuVisible" 
                trigger="click" 
                placement="bottom-end" 
                width="200"
                :close-on-click-outside="true"
              >
                <template #content>
                  <div class="user-menu">
                    <div class="user-info">
                      <el-avatar :size="48" class="user-avatar-big">
                        <User />
                      </el-avatar>
                      <div class="user-details">
                        <p class="username">{{ userInfo.name }}</p>
                        <p class="user-email">{{ userInfo.studentId }}</p>
                        <p class="user-role">{{ userInfo.college }}</p>
                      </div>
                    </div>
                    <el-divider />
                    <el-menu 
                      class="user-dropdown-menu" 
                      mode="vertical" 
                      @select="handleUserMenuSelect"
                      background-color="transparent"
                    >
                      <el-menu-item index="profile">
                        <el-icon>
                          <User />
                        </el-icon>
                        <span>个人中心</span>
                      </el-menu-item>
                      <el-menu-item index="settings">
                        <el-icon>
                          <Setting />
                        </el-icon>
                        <span>账户设置</span>
                      </el-menu-item>
                      <el-menu-item index="help">
                        <el-icon>
                          <Question />
                        </el-icon>
                        <span>帮助中心</span>
                      </el-menu-item>
                      <el-divider />
                      <el-menu-item index="logout" class="logout-item">
                        <el-icon>
                          <Logout />
                        </el-icon>
                        <span>退出登录</span>
                      </el-menu-item>
                    </el-menu>
                  </div>
                </template>
                <el-avatar :size="36" class="user-avatar" @click.stop="userMenuVisible = true">
                  <User />
                </el-avatar>
              </el-popover>
            </div>

            <!-- 移动端菜单按钮 -->
            <button 
              class="mobile-menu-btn action-btn" 
              v-if="isMobile && showSidebar" 
              @click="toggleMobileSidebar"
              aria-label="移动端菜单"
              title="菜单"
            >
              <el-icon>
                <Menu />
              </el-icon>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 主体内容区 -->
    <div class="body-container">
      <!-- 侧边栏 -->
      <aside 
        v-if="showSidebar" 
        class="sidebar" 
        :class="{ 'sidebar-mobile-open': mobileSidebarOpen }"
        :aria-hidden="!mobileSidebarOpen && isMobile"
      >
        <el-menu 
          :default-active="activeMenuIndex" 
          class="sidebar-menu" 
          @select="handleMenuSelect" 
          background-color="var(--sidebar-bg)"
          text-color="var(--sidebar-text)" 
          active-text-color="var(--primary-color)"
        >
          <el-menu-item index="1">
            <el-icon>
              <House />
            </el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="2">
            <el-icon>
              <Notebook />
            </el-icon>
            <template #title>学籍信息</template>
          </el-menu-item>
          <el-menu-item index="3">
            <el-icon>
              <Histogram />
            </el-icon>
            <template #title>成绩查询</template>
          </el-menu-item>
          <el-menu-item index="4">
            <el-icon>
              <Calendar />
            </el-icon>
            <template #title>课表查询</template>
          </el-menu-item>
          <el-menu-item index="5">
            <el-icon>
              <Reading />
            </el-icon>
            <template #title>图书借阅</template>
          </el-menu-item>
          <el-menu-item index="6">
            <el-icon>
              <EditPen />
            </el-icon>
            <template #title>选课系统</template>
          </el-menu-item>
          <el-menu-item index="7">
            <el-icon>
              <Setting />
            </el-icon>
            <template #title>系统设置</template>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 主内容区 -->
      <main class="main-content">
        <!-- 面包屑导航 -->
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">Lucky-SMS</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageName }}</el-breadcrumb-item>
          </el-breadcrumb>
          <div class="page-actions">
            <el-button 
              v-if="currentPageName === '选课系统'" 
              type="primary" 
              size="small" 
              @click="handleCourseSelect"
              :loading="isCourseLoading"
            >
              <el-icon>
                <Plus />
              </el-icon>
              <span>选课</span>
            </el-button>
          </div>
        </div>

        <!-- 路由视图容器 -->
        <div class="router-view-container">
          <!-- 加载状态 -->
          <div class="content-loading" v-if="isLoading">
            <el-spinner size="40" type="ball-clip-rotate" />
            <p class="loading-text">加载中...</p>
          </div>

          <!-- 错误状态 -->
          <div class="content-error" v-else-if="hasError">
            <el-icon size="60" class="error-icon">
              <Warning />
            </el-icon>
            <p class="error-message">{{ errorMessage }}</p>
            <el-button size="small" type="primary" @click="retry" round>
              点击重试
            </el-button>
          </div>

          <!-- 主内容路由视图 -->
          <router-view v-else v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
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
      :transition-duration="300"
    >
      <el-icon :size="20">
        <Top />
      </el-icon>
    </el-backtop>

    <!-- 移动端底部导航 -->
    <nav class="mobile-bottom-nav" v-if="isMobile && showSidebar">
      <button 
        v-for="item in mobileNavItems" 
        :key="item.index" 
        @click="handleMobileNav(item)"
        :class="{ 'active': route.path === item.path }"
        :aria-current="route.path === item.path ? 'page' : undefined"
      >
        <el-icon :size="20">
          <component :is="item.icon" />
        </el-icon>
        <span>{{ item.text }}</span>
      </button>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import {
  User, Notebook, Histogram, Setting, Calendar, Reading, EditPen,
  Menu, Search, Bell, CheckCircle, InfoFilled, WarningFilled,
  Moon, Sunny, Logout, Question, Warning, Plus, Top, Close, House
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus';

// 路由和路由实例
const router = useRouter()
const route = useRoute()

// 用户信息状态
const userInfo = ref({
  name: '张三',
  studentId: '20230001',
  college: '计算机学院'
})

// 移动端导航项配置
const mobileNavItems = [
  { index: '1', icon: Notebook, text: '学籍', path: '/info' },
  { index: '2', icon: Histogram, text: '成绩', path: '/grades' },
  { index: '3', icon: Calendar, text: '课表', path: '/schedule' },
  { index: '4', icon: Reading, text: '图书', path: '/library' }
]

// 状态管理
const showBackToTop = ref(false)
const currentPageName = ref('')
const searchQuery = ref('')
const isSearchFocused = ref(false)
const isDarkMode = ref(false)
const notificationVisible = ref(false)
const userMenuVisible = ref(false)
const mobileSidebarOpen = ref(false)
const isMobile = ref(false)
const isLoading = ref(false)
const hasError = ref(false)
const errorMessage = ref('')
const unreadCount = ref(3)
const isCourseLoading = ref(false)

// 通知数据
const notifications = ref([
  {
    id: 1,
    title: '您有新的成绩发布',
    time: '10分钟前',
    type: 'info',
    unread: true
  },
  {
    id: 2,
    title: '系统维护通知',
    time: '2小时前',
    type: 'warning',
    unread: true
  },
  {
    id: 3,
    title: '选课成功',
    time: '昨天',
    type: 'success',
    unread: false
  }
])

// 页面加载状态控制
const setPageLoading = (loading, errorMsg = '') => {
  isLoading.value = loading
  hasError.value = !loading && !!errorMsg
  errorMessage.value = errorMsg || '加载失败，请重试'
}

// 控制侧边栏显示
const showSidebar = computed(() => {
  return !['/login', '/register'].includes(route.path)
})

// 计算当前激活的菜单索引
const activeMenuIndex = computed(() => {
  const routeMap = {
    '/': '1',
    '/info': '2',
    '/grades': '3',
    '/schedule': '4',
    '/library': '5',
    '/course': '6',
    '/settings': '7'
  }
  return routeMap[route.path] || '1'
})

// 监听路由变化
watch(route, (newRoute) => {
  setPageLoading(true)
  // 模拟加载延迟
  const timer = setTimeout(() => {
    const pageTitles = {
      '/': '首页',
      '/info': '学籍信息',
      '/grades': '成绩查询',
      '/schedule': '课表查询',
      '/library': '图书借阅',
      '/course': '选课系统',
      '/settings': '系统设置',
      '/login': '登录',
      '/register': '注册'
    }
    currentPageName.value = pageTitles[newRoute.path] || '首页'
    setPageLoading(false)
    clearTimeout(timer)
  }, 500)
}, { immediate: true })

// 监听滚动事件，控制返回顶部按钮显示
const handleScroll = () => {
  showBackToTop.value = window.scrollY > 300
}

// 移动端导航处理
const handleMobileNav = (item) => {
  router.push(item.path)
}

// 移动端侧边栏切换
const toggleMobileSidebar = () => {
  mobileSidebarOpen.value = !mobileSidebarOpen.value
  // 控制页面滚动锁定
  document.body.style.overflow = mobileSidebarOpen.value ? 'hidden' : ''
}

// 侧边栏菜单选择处理
const handleMenuSelect = (index) => {
  const routeMap = {
    '1': '/',
    '2': '/info',
    '3': '/grades',
    '4': '/schedule',
    '5': '/library',
    '6': '/course',
    '7': '/settings'
  }
  if (routeMap[index]) {
    router.push(routeMap[index])
    if (isMobile.value) {
      mobileSidebarOpen.value = false
      document.body.style.overflow = ''
    }
  }
}

// 点击外部关闭下拉菜单
const closeDropdowns = (e) => {
  const userMenuEl = document.querySelector('.user-area .el-popover')
  const notificationEl = document.querySelector('.notification-center .el-popover')
  const sidebarEl = document.querySelector('.sidebar')
  const mobileMenuBtn = document.querySelector('.mobile-menu-btn')

  // 关闭用户菜单
  if (userMenuVisible.value && userMenuEl && !userMenuEl.contains(e.target)) {
    userMenuVisible.value = false
  }

  // 关闭通知菜单
  if (notificationVisible.value && notificationEl && !notificationEl.contains(e.target)) {
    notificationVisible.value = false
  }

  // 关闭移动端侧边栏
  if (isMobile.value && mobileSidebarOpen.value && sidebarEl && mobileMenuBtn && 
      !sidebarEl.contains(e.target) && !mobileMenuBtn.contains(e.target)) {
    mobileSidebarOpen.value = false
    document.body.style.overflow = ''
  }
}

// 用户菜单选择处理
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

// 搜索处理
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    console.log('搜索:', searchQuery.value)
    // 这里可以添加实际的搜索逻辑
  }
}

// 暗色模式切换
const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  localStorage.setItem('isDarkMode', isDarkMode.value)
  // 添加暗色模式切换动画效果
  const appContainer = document.querySelector('.app-container')
  appContainer.classList.add('dark-mode-transition')
  setTimeout(() => {
    appContainer.classList.remove('dark-mode-transition')
  }, 300)
}

// 通知中心切换
const toggleNotification = () => {
  notificationVisible.value = !notificationVisible.value
}

// 阅读通知
const readNotification = (index) => {
  if (notifications.value[index].unread) {
    notifications.value[index].unread = false
    unreadCount.value--
  }
}

// 删除通知
const removeNotification = (index) => {
  if (notifications.value[index].unread) {
    unreadCount.value--
  }
  notifications.value.splice(index, 1)
}

// 全部标为已读
const markAllAsRead = () => {
  notifications.value.forEach(item => {
    if (item.unread) {
      item.unread = false
    }
  })
  unreadCount.value = 0
  notificationVisible.value = false
}

// 选课处理
const handleCourseSelect = () => {
  isCourseLoading.value = true
  // 模拟选课请求延迟
  setTimeout(() => {
    isCourseLoading.value = false
    ElMessageBox.confirm('确定要选择该课程吗？', '选课确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 添加选课成功通知
      notifications.value.unshift({
        id: Date.now(),
        title: '选课成功',
        time: '刚刚',
        type: 'success',
        unread: true
      })
      unreadCount.value++
      ElMessageBox({
        message: '选课成功！',
        type: 'success',
        title: '提示'
      })
    }).catch(() => {})
  }, 800)
}

// 退出登录处理
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清空用户信息
    userInfo.value = {
      name: '',
      studentId: '',
      college: ''
    }
    router.push('/login')
  }).catch(() => {})
}

// 重试加载
const retry = () => {
  setPageLoading(true)
  // 模拟重新加载
  setTimeout(() => {
    setPageLoading(false)
  }, 1000)
}

// 检查屏幕尺寸
const checkScreenSize = () => {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) {
    mobileSidebarOpen.value = false
  }
}

// 生命周期钩子
onMounted(() => {
  // 从本地存储获取暗色模式设置
  const savedDarkMode = localStorage.getItem('isDarkMode')
  if (savedDarkMode !== null) {
    isDarkMode.value = savedDarkMode === 'true'
  }

  // 添加事件监听器
  document.addEventListener('click', closeDropdowns)
  window.addEventListener('scroll', handleScroll)
  window.addEventListener('resize', checkScreenSize)
  
  // 初始化检查屏幕尺寸
  checkScreenSize()
  
  // 初始化页面加载状态
  setPageLoading(true)
  setTimeout(() => {
    setPageLoading(false)
  }, 800)
})

onUnmounted(() => {
  // 移除事件监听器
  document.removeEventListener('click', closeDropdowns)
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', checkScreenSize)
})
</script>

<style lang="scss">
/* 全局样式定义 */
:root {
  --primary-color: #4361ee;
  --primary-light: #e0e7ff;
  --primary-dark: #3a56d5;
  --secondary-color: #6b7280;
  --success-color: #10b981;
  --warning-color: #f59e0b;
  --danger-color: #ef4444;
  --info-color: #3b82f6;
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-300: #d1d5db;
  --gray-400: #9ca3af;
  --gray-500: #6b7280;
  --gray-600: #4b5563;
  --gray-700: #374151;
  --gray-800: #1f2937;
  --gray-900: #111827;
  --white: #ffffff;
  --sidebar-bg: var(--white);
  --sidebar-text: var(--gray-600);
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  --border-radius: 8px;
  --border-radius-lg: 16px;
  --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-fast: all 0.15s cubic-bezier(0.4, 0, 0.2, 1);
}

.dark-mode {
  --primary-color: #6366f1;
  --primary-light: #4338ca;
  --primary-dark: #a5b4fc;
  --gray-50: #111827;
  --gray-100: #1f2937;
  --gray-200: #374151;
  --gray-300: #4b5563;
  --gray-400: #6b7280;
  --gray-500: #9ca3af;
  --gray-600: #d1d5db;
  --gray-700: #e5e7eb;
  --gray-800: #f3f4f6;
  --gray-900: #f9fafb;
  --white: #1f2937;
  --sidebar-bg: #111827;
  --sidebar-text: var(--gray-400);
}

/* 暗色模式切换动画 */
.dark-mode-transition {
  transition: all 0.3s ease-in-out;
}
</style>

<style scoped lang="scss">
/* 组件样式 */
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
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
}

.top-nav {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  padding: 8px 0;
  box-shadow: var(--shadow-sm);
  position: sticky;
  z-index: 1000;
  border-right: 1px solid var(--gray-200);
  transition: var(--transition);
}

.dark-mode .top-nav {
  background: rgba(17, 24, 39, 0.95);
  border-right: 1px solid var(--gray-800);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
  gap: 16px;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 0 0 auto;
}

.nav-logo {
  position: relative;
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color);
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(59, 130, 246, 0.2);
}

.logo-badge {
  position: absolute;
  top: -12px;
  right: -20px;
  font-size: 10px;
  background: var(--success-color);
  color: var(--white);
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: bold;
}

.nav-center {
  flex: 1;
  display: flex;
  justify-content: center;
  max-width: 600px;
  margin: 0 20px;
}

.nav-search {
  width: 100%;
  max-width: 500px;
}

.search-input {
  border-radius: 20px;
  border: 1px solid var(--gray-300);
  transition: all 0.3s ease;
  background-color: var(--gray-50);
  box-shadow: none;

  :deep(.el-input__wrapper) {
    background-color: transparent;
    box-shadow: none;
    padding: 0 16px;
    height: 36px;
  }

  :deep(.el-input__prefix) {
    color: var(--gray-400);
    margin-right: 8px;
    transition: var(--transition);
  }
}

.search-input:hover,
.search-input:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(67, 97, 238, 0.1);

  :deep(.el-input__prefix) {
    color: var(--primary-color);
  }
}

.search-icon {
  transition: all 0.3s ease;
}

.search-input:focus-within .search-icon {
  transform: scale(1.1);
}

:deep(.el-input__clear) {
  color: var(--gray-400);
  transition: all 0.2s ease;

  &:hover {
    color: var(--primary-color);
    transform: scale(1.1);
  }
}

.dark-mode {
  .search-input {
    background-color: var(--gray-800);
    border-color: var(--gray-600);

    :deep(.el-input__inner) {
      color: var(--gray-200);
    }
  }

  .search-input:hover,
  .search-input:focus-within {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
  }
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 0 0 auto;
}

.action-btn {
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
  position: relative;
  outline: none;
}

.action-btn:hover {
  background-color: var(--gray-100);
  color: var(--primary-color);
}

.action-btn:active {
  transform: scale(0.95);
}

.dark-mode .action-btn:hover {
  background-color: var(--gray-800);
}

.notification-center {
  position: relative;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px 0;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: var(--transition);
  position: relative;
  border-bottom: 1px solid var(--gray-100);
}

.notification-item:hover {
  background-color: var(--gray-50);
}

.dark-mode .notification-item {
  border-bottom: 1px solid var(--gray-800);
}

.dark-mode .notification-item:hover {
  background-color: var(--gray-200);
}

.notification-item.unread {
  background-color: var(--primary-light);
}

.dark-mode .notification-item.unread {
  background-color: rgba(67, 56, 202, 0.2);
}

.notification-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.notification-icon.type-success {
  color: var(--success-color);
}

.notification-icon.type-info {
  color: var(--info-color);
}

.notification-icon.type-warning {
  color: var(--warning-color);
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 0.875rem;
  color: var(--gray-800);
  margin-bottom: 4px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-time {
  font-size: 0.75rem;
  color: var(--gray-500);
}

.notification-close {
  color: var(--gray-400);
  opacity: 0;
  transition: var(--transition-fast);
  flex-shrink: 0;
  margin-left: 8px;
}

.notification-item:hover .notification-close {
  opacity: 1;
}

.notification-footer {
  padding: 12px 16px;
  text-align: center;
  font-size: 0.875rem;
  color: var(--primary-color);
  cursor: pointer;
  transition: var(--transition);
  font-weight: 500;
}

.notification-footer:hover {
  background-color: var(--gray-50);
}

.dark-mode .notification-footer:hover {
  background-color: var(--gray-200);
}

.user-area {
  position: relative;
  margin-left: 8px;
}

.user-avatar {
  cursor: pointer;
  background-color: var(--primary-light);
  color: var(--primary-color);
  transition: var(--transition);
  border: 2px solid transparent;
  box-shadow: 0 2px 5px rgba(67, 97, 238, 0.1);
  font-weight: 600;
}

.user-avatar:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 12px rgba(67, 97, 238, 0.2);
  border-color: var(--primary-color);
}

.user-avatar-big {
  background-color: var(--primary-light);
  color: var(--primary-color);
  font-weight: 600;
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
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 2px;
}

.user-email {
  font-size: 0.75rem;
  color: var(--gray-500);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-role {
  font-size: 0.75rem;
  color: var(--gray-500);
  background-color: var(--gray-100);
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}

.dark-mode .user-role {
  background-color: var(--gray-800);
}

.user-dropdown-menu {
  border: none !important;
  background: transparent !important;
}

:deep(.user-dropdown-menu .el-menu-item) {
  height: 40px !important;
  line-height: 40px !important;
  border-radius: var(--border-radius) !important;
  margin: 2px 0 !important;
  transition: var(--transition) !important;
}

:deep(.user-dropdown-menu .el-menu-item:hover) {
  background-color: var(--gray-100) !important;
}

.dark-mode :deep(.user-dropdown-menu .el-menu-item:hover) {
  background-color: var(--gray-200) !important;
}

.logout-item {
  color: var(--danger-color) !important;
}

.logout-item:hover {
  background-color: rgba(239, 68, 68, 0.1) !important;
}

.body-container {
  display: flex;
  flex: 1;
  overflow: hidden;
  height: calc(100vh - 72px);
}

/* 侧边栏样式 */
.sidebar {
  width: 220px;
  background: var(--sidebar-bg);
  border-right: 1px solid var(--gray-200);
  display: flex;
  flex-direction: column;
  transition: all var(--transition);
  z-index: 100;
  height: 100%;
  position: relative;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
  padding: 16px 8px;
  overflow-x: hidden;
  overflow-y: auto;
}

:deep(.sidebar-menu .el-menu-item) {
  height: 44px;
  line-height: 44px;
  padding: 0 12px !important;
  margin: 4px 0 !important;
  border-radius: var(--border-radius) !important;
  transition: var(--transition) !important;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
}

:deep(.sidebar-menu .el-menu-item:hover:not(.is-active)) {
  background-color: var(--gray-50) !important;
  color: var(--primary-color) !important;
}

:deep(.sidebar-menu .el-menu-item.is-active) {
  background-color: var(--primary-light) !important;
  color: var(--primary-color) !important;
  font-weight: 600 !important;
}

:deep(.sidebar-menu .el-menu-item .el-icon) {
  margin-right: 12px !important;
  font-size: 18px !important;
}

.dark-mode {
  :deep(.sidebar-menu .el-menu-item.is-active) {
    background-color: rgba(67, 56, 202, 0.2) !important;
  }

  :deep(.sidebar-menu .el-menu-item:hover:not(.is-active)) {
    background-color: var(--gray-800) !important;
  }
}

.main-content {
  flex: 1;
  padding: 24px;
  background-color: var(--gray-50);
  transition: var(--transition);
  position: relative;
  overflow-y: auto;
  height: 100%;
}

.main-content::after {
  content: '';
  position: fixed;
  top: 72px;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 99;
  opacity: 0;
  pointer-events: none;
  transition: opacity var(--transition);
}

.router-view-container {
  min-height: calc(100% - 40px);
  background: var(--white);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  padding: 24px;
  transition: var(--transition);
}

.router-view-container:hover {
  box-shadow: var(--shadow-md);
}

.breadcrumb-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.breadcrumb {
  font-size: 0.875rem;
  color: var(--gray-500);
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--primary-color) !important;
  font-weight: 500;
}

/* 面包屑第一个屑 */
.breadcrumb :deep(.el-breadcrumb__inner) {
  color: var(--gray-600);
  transition: var(--transition);
}

.breadcrumb :deep(.el-breadcrumb__inner:hover) {
  color: var(--primary-color);
}

.dark-mode .breadcrumb :deep(.el-breadcrumb__inner) {
  color: var(--gray-400);
}

.dark-mode .breadcrumb :deep(.el-breadcrumb__inner:hover) {
  color: var(--primary-light);
}

.page-actions {
  display: flex;
  gap: 8px;
}

.content-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  z-index: 10;
  border-radius: var(--border-radius-lg);
}

.loading-text {
  color: var(--gray-600);
  font-size: 0.875rem;
}

.dark-mode .content-loading {
  background-color: rgba(17, 24, 39, 0.9);
}

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
  border-radius: var(--border-radius-lg);
  text-align: center;
  padding: 24px;
}

.error-icon {
  color: var(--warning-color);
}

.error-message {
  color: var(--gray-600);
  max-width: 400px;
}

.back-to-top {
  background: var(--primary-color) !important;
  color: white !important;
  width: 44px !important;
  height: 44px !important;
  box-shadow: var(--shadow-md) !important;
  transition: var(--transition) !important;
  border-radius: 50% !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.back-to-top:hover {
  background: var(--primary-dark) !important;
  transform: translateY(-3px) scale(1.05) !important;
  box-shadow: var(--shadow-lg) !important;
}

.mobile-bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  background: var(--white);
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding: 8px 0;

  button {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: none;
    border: none;
    color: var(--gray-500);
    cursor: pointer;
    transition: var(--transition-fast);
    padding: 4px 0;

    .el-icon {
      margin-bottom: 4px;
      font-size: 1.25rem;
    }

    span {
      font-size: 0.75rem;
    }

    &.active {
      color: var(--primary-color);

      .el-icon {
        color: var(--primary-color);
      }
    }

    &:hover:not(.active) {
      color: var(--gray-700);
    }
  }
}

.dark-mode .mobile-bottom-nav {
  background: var(--gray-800);
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.3);

  button {
    color: var(--gray-400);

    &:hover:not(.active) {
      color: var(--gray-300);
    }
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1024px) {
  .nav-search {
    max-width: 400px;
  }

  .main-content {
    padding: 16px;
  }

  .router-view-container {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 72px;
    height: calc(100vh - 72px);
    transform: translateX(-100%);
    z-index: 1001;
  }

  .sidebar.sidebar-mobile-open {
    transform: translateX(0);
    box-shadow: var(--shadow-xl);
  }

  .main-content::after {
    display: block;
  }

  .sidebar-mobile-open+.main-content::after {
    opacity: 1;
    pointer-events: auto;
  }

  .nav-center {
    display: none;
  }

  .breadcrumb-container {
    flex-direction: column;
    align-items: flex-start;
  }

  .main-content {
    margin-left: 0 !important;
    width: 100% !important;
    padding-bottom: 72px;
  }

  .router-view-container {
    height: auto;
    min-height: calc(100vh - 180px);
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 16px;
  }

  .logo-text {
    font-size: 1rem;
  }

  .main-content {
    padding: 12px;
    padding-bottom: 72px;
  }

  .router-view-container {
    padding: 12px;
  }
}
</style>
