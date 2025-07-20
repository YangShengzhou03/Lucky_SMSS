<template>
  <div class="student-home" @mousemove="handleMouseMove">
    <div v-if="loading" class="loading-container">
      <el-skeleton :loading="true" class="welcome-skeleton" />
      <div class="info-cards">
        <el-skeleton :loading="true" class="card-skeleton" />
        <el-skeleton :loading="true" class="card-skeleton" />
        <el-skeleton :loading="true" class="card-skeleton" />
      </div>
      <el-skeleton :loading="true" class="announcement-skeleton" />
    </div>
    <div v-else-if="error" class="error-container">
      <el-icon class="error-icon">
        <WarningFilled />
      </el-icon>
      <div class="error-message">{{ error }}</div>
      <el-button type="primary" @click="fetchData">重试</el-button>
    </div>
    <div v-else>
      <div class="welcome-section modern-card">
        <h2>欢迎回来，<span class="username">{{ student?.name || '--' }}</span> 👋</h2>
        <p class="subtitle">学号：{{ student?.id || '--' }} | 班级：{{ student?.class || '--' }}</p>
      </div>
      <div class="info-cards">
        <div class="modern-card score-card">
          <div class="card-header">
            <h3><el-icon>
                <Trophy />
              </el-icon> 学业成绩</h3>
            <div class="card-badge" v-if="student?.rank && student.rank <= 3">TOP {{ student.rank }}</div>
          </div>
          <div class="card-content">
            <div class="gpa">
              <span class="value">{{ student?.gpa || '--' }}</span>
              <span class="label">平均绩点</span>
            </div>
            <div class="progress-container">
              <el-progress :percentage="rankPercentage" :show-text="false" :stroke-width="8" color="#e6a23c" />
              <div class="progress-labels">
                <span>班级排名</span>
                <span>{{ student?.rank || '--' }} / {{ student?.classSize || '--' }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="modern-card course-card">
          <div class="card-header">
            <h3><el-icon>
                <Notebook />
              </el-icon> 我的课程</h3>
          </div>
          <div class="card-content">
            <div class="course-count">
              <span class="highlight">{{ student?.courseCount || '--' }}</span> 门课程进行中
            </div>
            <div class="next-course">
              <div class="course-time">{{ formatCourseTime(student?.nextCourse?.time || '--') }}</div>
              <div class="course-name">{{ student?.nextCourse?.name || '暂无课程' }}</div>
              <div class="course-location">{{ student?.nextCourse?.location || '待定教室' }}</div>
            </div>
          </div>
        </div>
        <div class="modern-card todo-card">
          <div class="card-header">
            <h3><el-icon>
                <List />
              </el-icon> 待办事项</h3>
            <el-tag size="small" type="danger" v-if="pendingCount > 0">
              {{ pendingCount }} 项待完成
            </el-tag>
          </div>
          <div class="card-content">
            <div class="todo-list">
              <template v-if="student?.todos?.length">
                <div class="todo-item" v-for="item in filteredTodos" :key="item.id"
                  :class="{ 'urgent': isUrgent(item.dueDate), 'completed': item.completed }">
                  <el-checkbox v-model="item.completed" @change="updateTodo(item)" />
                  <div class="todo-content">
                    <span>{{ item.text }}</span>
                    <div class="todo-meta">
                      <el-tag size="mini" :type="getDueTagType(item.dueDate)" effect="plain">
                        {{ item.dueDate }}
                      </el-tag>
                      <el-icon v-if="item.important" color="#F56C6C">
                        <StarFilled />
                      </el-icon>
                    </div>
                  </div>
                </div>
              </template>
              <div v-else class="empty-todos">
                <p>暂无待办事项</p>
                <el-button type="primary" size="small" @click="addFirstTodo">
                  <el-icon>
                    <Plus />
                  </el-icon> 添加第一个待办事项
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="modern-card announcements">
        <div class="section-header">
          <h3><el-icon>
              <Bell />
            </el-icon> 校园公告</h3>
          <el-link type="primary" :underline="false">查看更多</el-link>
        </div>
        <el-scrollbar>
          <div class="announcement-list">
            <template v-if="announcements?.length">
              <div class="announcement-item" v-for="item in announcements" :key="item.id"
                @click="viewAnnouncement(item)">
                <el-tag class="announcement-tag" size="small" :type="item.type || 'info'">
                  {{ getAnnouncementType(item.type) }}
                </el-tag>
                <div class="announcement-content">
                  <div class="title">{{ item.title }}</div>
                  <div class="meta">
                    <span class="date">{{ formatDate(item.date) }}</span>
                    <span class="department">{{ item.department }}</span>
                  </div>
                </div>
                <el-icon class="arrow">
                  <ArrowRight />
                </el-icon>
              </div>
            </template>
            <div v-else class="empty-announcements">
              <p>暂无公告</p>
              <el-link type="primary" :underline="false">查看历史公告</el-link>
            </div>
          </div>
        </el-scrollbar>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import {
  Trophy, Notebook, List, ArrowRight, StarFilled, Bell,
  WarningFilled, Plus
} from '@element-plus/icons-vue'
import { ElMessage, ElSkeleton, ElButton, ElLink } from 'element-plus'

const student = ref(null)
const announcements = ref(null)
const loading = ref(true)
const error = ref(null)

const pendingCount = computed(() => {
  return student.value?.todos?.filter(todo => !todo.completed).length || 0
})

const filteredTodos = computed(() => {
  return student.value?.todos?.slice(0, 4) || []
})

const rankPercentage = computed(() => {
  if (student.value?.rank && student.value?.classSize) {
    return (1 - (student.value.rank - 1) / student.value.classSize) * 100
  }
  return 0
})

const updateTodo = async (item) => {
  const originalState = item.completed

  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('待办事项已更新')
  } catch (err) {
    item.completed = originalState
    ElMessage.error('更新失败，请重试')
    console.error('更新待办事项失败:', err)
  }
}

const viewAnnouncement = (item) => {
  console.log('查看公告:', item)
  ElMessage.info(`查看公告: ${item.title}`)
}

const formatDate = (dateString) => {
  return dateString?.replace(/-/g, '.') || '--'
}

const formatCourseTime = (timeString) => {
  return timeString?.replace('-', ' - ') || '--'
}

const isUrgent = (dueDate) => {
  return dueDate?.includes('天') || dueDate?.includes('明天')
}

const getDueTagType = (dueDate) => {
  if (!dueDate) return 'info'
  if (dueDate.includes('明天')) return 'danger'
  if (dueDate.includes('天') && parseInt(dueDate) <= 3) return 'warning'
  return 'info'
}

const getAnnouncementType = (type) => {
  const types = {
    'important': '重要',
    'notice': '通知',
    'info': '资讯',
    'activity': '活动'
  }
  return types[type] || '公告'
}

const addFirstTodo = () => {
  ElMessage.info('跳转到添加待办事项页面')
}

const handleMouseMove = (e) => {
  document.documentElement.style.setProperty('--mouse-x', `${e.clientX}px`)
  document.documentElement.style.setProperty('--mouse-y', `${e.clientY}px`)
}

const fetchData = async () => {
  loading.value = true
  error.value = null

  try {
    await new Promise(resolve => setTimeout(resolve, 800))

    student.value = {
      name: '张三',
      id: '20230001',
      class: '计算机科学与技术2023级1班',
      gpa: '3.75',
      rank: 5,
      classSize: 30,
      courseCount: 6,
      nextCourse: {
        name: '数据结构与算法',
        time: '14:00-15:30',
        location: '逸夫楼305'
      },
      todos: [
        { id: 1, text: '完成数据结构作业', dueDate: '明天', completed: false, important: true },
        { id: 2, text: '准备英语听力测试', dueDate: '3天', completed: false, important: false },
        { id: 3, text: '提交实验报告', dueDate: '5天', completed: true, important: false },
        { id: 4, text: '复习线性代数', dueDate: '7天', completed: false, important: true },
        { id: 5, text: '参加编程竞赛', dueDate: '10天', completed: false, important: true }
      ]
    }

    announcements.value = [
      {
        id: 1,
        title: '关于2023-2024学年期末考试安排的通知',
        date: '2023-12-01',
        department: '教务处',
        type: 'important',
        content: '本学期期末考试将于12月20日开始，请同学们提前做好准备。'
      },
      {
        id: 2,
        title: '2023年度校园文化节活动预告',
        date: '2023-11-25',
        department: '学生会',
        type: 'activity',
        content: '校园文化节将于12月10日至15日举行，欢迎广大同学积极参与。'
      },
      {
        id: 3,
        title: '关于调整作息时间的通知',
        date: '2023-11-20',
        department: '教务处',
        type: 'notice',
        content: '根据学校安排，自2023年12月1日起，学校作息时间将进行调整。'
      }
    ]

  } catch (err) {
    error.value = '获取数据失败，请稍后重试'
    console.error('获取数据失败:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})

onUnmounted(() => {
  document.querySelector('.student-home')?.removeEventListener('mousemove', handleMouseMove)
})
</script>

<style scoped lang="scss">
:root {
  --text-primary: #303133;
  --text-secondary: #606266;
}

.dark {
  --text-primary: #ffffff;
  --text-secondary: rgba(255, 255, 255, 0.7);
}

.student-home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s ease;
  padding: 0 15px;
  --mouse-x: 0;
  --mouse-y: 0;
}

.modern-card {
  position: relative;
  border-radius: 16px;
  padding: 30px;
  transition: all 0.3s ease;
  overflow: hidden;
  z-index: 1;

  .card-content {
    position: relative;
    z-index: 2;
  }

  background: white;
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);

  .dark & {
    background: rgba(30, 41, 59, 0.8);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(600px circle at var(--mouse-x) var(--mouse-y),
        rgba(99, 102, 241, 0.08) 0%,
        transparent 70%);
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: -1;
    pointer-events: none;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);

    &::before {
      opacity: 1;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h3 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .progress-indicator {
      background-color: rgba(64, 158, 255, 0.1);
      color: #409eff;
      padding: 6px 12px;
      border-radius: 999px;
      font-size: 14px;
    }
  }
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.welcome-section {
  margin-bottom: 30px;
}

.score-card {
  .gpa {
    .value {
      font-size: 40px;
      font-weight: 700;
      margin-right: 8px;
      color: #e6a23c;
      text-shadow: 0 2px 4px rgba(230, 162, 60, 0.1);
    }

    .label {
      font-size: 14px;
      color: var(--text-secondary);
      padding-bottom: 6px;
    }
  }

  .progress-labels {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: var(--text-secondary);
    margin-top: 8px;
  }
}

.course-card {
  .course-count {
    font-size: 14px;
    color: var(--text-secondary);
    margin-bottom: 20px;

    .highlight {
      font-size: 28px;
      font-weight: 700;
      color: #67c23a;
      margin-right: 6px;
      text-shadow: 0 2px 4px rgba(103, 194, 58, 0.1);
    }
  }

  .next-course {
    background: rgba(248, 248, 248, 0.6);
    border-radius: 12px;
    padding: 16px;
    transition: transform 0.3s ease;

    &:hover {
      transform: translateX(4px);
    }

    .dark & {
      background: rgba(40, 45, 55, 0.6);
    }

    .course-time {
      font-size: 14px;
      color: #67c23a;
      font-weight: 500;
      margin-bottom: 6px;
    }

    .course-name {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 6px;
      color: var(--text-primary);
    }

    .course-location {
      font-size: 13px;
      color: var(--text-secondary);
    }
  }
}

.todo-card {
  .todo-list {
    max-height: 240px;
    overflow-y: auto;
    padding-right: 12px;
    scrollbar-width: thin;
    scrollbar-color: rgba(0, 0, 0, 0.1) transparent;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.1);
      border-radius: 3px;
    }

    .dark & {
      scrollbar-color: rgba(255, 255, 255, 0.1) transparent;

      &::-webkit-scrollbar-thumb {
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }

  .todo-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px dashed rgba(0, 0, 0, 0.08);
    transition: all 0.2s ease;

    &:hover {
      transform: translateX(4px);
    }

    &:last-child {
      border-bottom: none;
    }

    .el-checkbox {
      margin-right: 14px;
    }

    .todo-content {
      flex: 1;

      span {
        font-size: 14px;
        color: var(--text-primary);
        transition: all 0.2s ease;
      }

      .todo-meta {
        display: flex;
        align-items: center;
        margin-top: 6px;

        .el-tag {
          margin-right: 10px;
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
        }
      }
    }

    &.urgent {
      .todo-content span {
        color: #F56C6C;
      }
    }

    &.completed {
      .todo-content span {
        text-decoration: line-through;
        color: #c0c4cc;
      }
    }
  }
}

.announcements {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h3 {
      margin: 0;
      font-size: 20px;
      font-weight: 650;
      display: flex;
      align-items: center;
      color: var(--text-primary);
      letter-spacing: -0.3px;

      .el-icon {
        margin-right: 12px;
        font-size: 22px;
        color: #e6a23c;
        text-shadow: 0 2px 4px rgba(230, 162, 60, 0.1);
      }
    }
  }

  .announcement-item {
    display: flex;
    align-items: flex-start;
    padding: 16px 0;
    cursor: pointer;
    border-bottom: 1px dashed rgba(0, 0, 0, 0.08);
    transition: all 0.2s ease;

    &:hover {
      transform: translateX(8px);
      background: rgba(245, 247, 250, 0.4);
      border-radius: 8px;
      padding: 16px;
      margin: 0 -16px;

      .dark & {
        background: rgba(40, 45, 55, 0.6);
      }
    }

    &:last-child {
      border-bottom: none;
    }

    .announcement-tag {
      color: #409eff;
      margin-right: 16px;
      flex-shrink: 0;
      margin-top: 2px;
    }

    .announcement-content {
      flex: 1;

      .title {
        font-size: 16px;
        font-weight: 500;
        margin-bottom: 6px;
        color: var(--text-primary);
        line-height: 1.4;
      }

      .meta {
        display: flex;
        font-size: 13px;
        color: var(--text-secondary);

        .date {
          margin-right: 16px;
          position: relative;

          &::after {
            content: '•';
            position: absolute;
            right: -10px;
            opacity: 0.6;
          }
        }

        .department {
          display: flex;
          align-items: center;
        }
      }
    }
  }

  .el-scrollbar {
    max-height: 340px;
    padding-right: 8px;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.1);
      border-radius: 3px;
    }

    .dark & {
      &::-webkit-scrollbar-thumb {
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h3 {
    font-size: 18px;
    margin: 0;
    display: flex;
    align-items: center;
    font-weight: 600;
    color: var(--text-primary);

    .el-icon {
      margin-right: 10px;
      font-size: 20px;
    }
  }

  .card-badge {
    background: #e6a23c;
    color: white;
    padding: 4px 10px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: bold;
    box-shadow: 0 2px 6px rgba(230, 162, 60, 0.2);
  }
}

.empty-todos,
.empty-announcements {
  text-align: center;
  padding: 20px;
  color: var(--text-secondary);
  font-size: 14px;
}

@media (max-width: 768px) {
  .welcome-section h2 {
    font-size: 24px;
  }

  .info-cards {
    grid-template-columns: 1fr;
  }
}
</style>