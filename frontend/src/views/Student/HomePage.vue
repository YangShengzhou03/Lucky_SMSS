<template>
  <div class="student-home">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <h2>欢迎回来，<span class="username">{{ student?.name || '--' }}</span> 👋</h2>
      <p class="subtitle">学号：{{ student?.id || '--' }} | 班级：{{ student?.class || '--' }}</p>
    </div>

    <!-- 快速信息卡片 -->
    <div class="info-cards">
      <!-- 成绩卡片 -->
      <div class="card score-card">
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
            <el-progress
              :percentage="student?.rank && student?.classSize ? (student.rank / student.classSize) * 100 : 0"
              :show-text="false" :stroke-width="8" color="#e6a23c" />
            <div class="progress-labels">
              <span>班级排名</span>
              <span>{{ student?.rank || '--' }} / {{ student?.classSize || '--' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 课程卡片 -->
      <div class="card course-card">
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

      <!-- 待办事项卡片 -->
      <div class="card todo-card">
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
              <div class="todo-item" v-for="(item, index) in filteredTodos" :key="index"
                :class="{ 'urgent': isUrgent(item.dueDate) }">
                <el-checkbox v-model="item.completed" @change="updateTodo(item)" />
                <div class="todo-content">
                  <span :class="{ completed: item.completed }">{{ item.text }}</span>
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
              暂无待办事项
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 公告区域 -->
    <div class="announcements">
      <div class="section-header">
        <h3><el-icon>
            <Bell />
          </el-icon> 校园公告</h3>
        <el-link type="primary" :underline="false">查看更多</el-link>
      </div>
      <el-scrollbar>
        <div class="announcement-list">
          <template v-if="announcements?.length">
            <div class="announcement-item" v-for="(item, index) in announcements" :key="index"
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
            暂无公告
          </div>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  Trophy, Notebook, List, ArrowRight, StarFilled, Bell
} from '@element-plus/icons-vue'

// 学生数据 - 初始化为null
const student = ref(null)
// 公告数据 - 初始化为null
const announcements = ref(null)

// 计算属性
const pendingCount = computed(() => {
  return student.value?.todos?.filter(todo => !todo.completed).length || 0
})

const filteredTodos = computed(() => {
  return student.value?.todos?.slice(0, 4) || []
})

// 方法
const updateTodo = (item) => {
  console.log('待办事项更新:', item)
  // 这里用后端更新待办事项状态
  // await axios.put(`/api/todos/${item.id}`, item)
}

const viewAnnouncement = (item) => {
  console.log('查看公告:', item)
  // 实际应用中这里可以跳转到详情页
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
  if (dueDate.includes('天')) return 'warning'
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

// 从后端获取数据
const fetchData = async () => {
  try {
    // 这里用后端获取学生数据
    // const studentResponse = await axios.get('/api/student/data')
    // student.value = studentResponse.data

    // 这里用后端获取公告数据
    // const announcementResponse = await axios.get('/api/announcements')
    // announcements.value = announcementResponse.data

    // 这里用后端获取学习进度数据
    // const progressResponse = await axios.get('/api/progress')
    // progressData.value = progressResponse.data

    // 模拟数据 - 实际开发时应删除这部分
    setTimeout(() => {
      // student.value = {
      //   name: '张三',
      //   id: '20230001',
      //   class: '计算机科学与技术2023级1班',
      //   gpa: '3.75',
      //   rank: 5,
      //   classSize: 30,
      //   courseCount: 6,
      //   nextCourse: {
      //     name: '数据结构与算法',
      //     time: '14:00-15:30',
      //     location: '逸夫楼305'
      //   },
      //   todos: [
      //     { text: '完成数据结构作业', dueDate: '明天', completed: false, important: true }
      //   ]
      // }

      announcements.value = [
        // {
        //   title: '关于2023-2024学年期末考试安排的通知',
        //   date: '2023-12-01',
        //   department: '教务处',
        //   type: 'important',
        //   content: '本学期期末考试将于12月20日开始，请同学们提前做好准备。具体考试安排请登录教务系统查询。'
        // }
      ]

      // progressData.value = {
      //   courseCompletion: 75,
      //   assignmentSubmission: 92,
      //   attendanceRate: 98
      // }
    }, 500)
  } catch (error) {
    console.error('获取数据失败:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.student-home {
  padding: 20px;
  color: #666;
  max-width: 1400px;
  margin: 0 auto;
}

// 欢迎区域 
.welcome-section {
  position: relative;
  margin-bottom: 30px;
  padding: 24px;
  background: rgba(245, 247, 250, 0.88);
  backdrop-filter: blur(6px);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;

  // 夜间模式适配 
  @media (prefers-color-scheme: dark) {
    background: rgba(30, 35, 45, 0.8);
    border-color: rgba(255, 255, 255, 0.1);

    .subtitle {
      color: rgba(255, 255, 255, 0.7);
    }
  }

  h2 {
    font-size: 28px;
    margin-bottom: 8px;
    font-weight: 600;
    color: #333;

    @media (prefers-color-scheme: dark) {
      color: #fff;
    }
  }

  .username {
    color: #409eff;
    font-weight: 700;
  }

  .subtitle {
    color: rgba(0, 0, 0, 0.6);
    font-size: 15px;
  }

  // 悬停效果 
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

    @media (prefers-color-scheme: dark) {
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    }
  }
}

/* 信息卡片 */
.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 30px;

  .card {
    background: rgba(250, 250, 250, 0.95);
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    padding: 20px;
    border: 1px solid #ebeef5;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h3 {
      font-size: 18px;
      margin: 0;
      display: flex;
      align-items: center;
      font-weight: 600;

      .el-icon {
        margin-right: 8px;
        font-size: 20px;
      }
    }

    .card-badge {
      background: #e6a23c;
      color: white;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: bold;
    }
  }
}

/* 成绩卡片 */
.score-card {
  .gpa {
    display: flex;
    align-items: flex-end;
    margin-bottom: 16px;

    .value {
      font-size: 36px;
      font-weight: 700;
      margin-right: 8px;
      color: #e6a23c;
    }

    .label {
      font-size: 14px;
      color: #909399;
      padding-bottom: 5px;
    }
  }

  .progress-labels {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #909399;
    margin-top: 6px;
  }
}

/* 课程卡片 */
.course-card {
  .course-count {
    font-size: 14px;
    color: #909399;
    margin-bottom: 16px;

    .highlight {
      font-size: 24px;
      font-weight: 700;
      color: #67c23a;
      margin-right: 4px;
    }
  }

  .next-course {
    background: #f8f8f8;
    border-radius: 8px;
    padding: 12px;

    .course-time {
      font-size: 14px;
      color: #67c23a;
      font-weight: 500;
      margin-bottom: 4px;
    }

    .course-name {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 4px;
    }

    .course-location {
      font-size: 13px;
      color: #909399;
    }
  }
}

/* 待办事项卡片 */
.todo-card {
  .todo-list {
    max-height: 200px;
    overflow-y: auto;
    padding-right: 8px;
  }

  .todo-item {
    display: flex;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px dashed #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .el-checkbox {
      margin-right: 12px;
    }

    .todo-content {
      flex: 1;

      span {
        font-size: 14px;

        &.completed {
          text-decoration: line-through;
          color: #c0c4cc;
        }
      }

      .todo-meta {
        display: flex;
        align-items: center;
        margin-top: 4px;

        .el-tag {
          margin-right: 8px;
        }
      }
    }
  }
}

/* 公告区域 */
.announcements {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 20px;
  margin-bottom: 30px;
  border: 1px solid #ebeef5;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      display: flex;
      align-items: center;

      .el-icon {
        margin-right: 8px;
        font-size: 20px;
        color: #e6a23c;
      }
    }
  }

  .announcement-list {
    .announcement-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      cursor: pointer;
      border-bottom: 1px solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .announcement-tag {
        margin-right: 12px;
      }

      .announcement-content {
        flex: 1;

        .title {
          font-size: 15px;
          font-weight: 500;
          margin-bottom: 4px;
        }

        .meta {
          display: flex;
          font-size: 12px;
          color: #909399;

          .date {
            margin-right: 12px;
          }
        }
      }
    }
  }

  .el-scrollbar {
    max-height: 300px;
  }
}

/* 空状态样式 */
.empty-todos,
.empty-announcements {
  text-align: center;
  padding: 20px;
  color: #909399;
  font-size: 14px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .welcome-section h2 {
    font-size: 24px;
  }
}
</style>
