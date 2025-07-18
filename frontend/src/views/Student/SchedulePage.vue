<template>
  <div class="course-page">
    <!-- 课程概览卡片 -->
    <div class="course-overview-card">
      <div class="card-header">
        <h2>课程概览</h2>
        <div class="semester-selector">
          <el-select v-model="currentSemester" placeholder="选择学期" size="small">
            <el-option
              v-for="item in semesters"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
      </div>
      
      <div class="overview-stats">
        <div class="stat-item">
          <div class="stat-value">{{ currentCourses.length }}</div>
          <div class="stat-label">当前课程</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ completedCoursesCount }}</div>
          <div class="stat-label">已完成课程</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ failedCoursesCount }}</div>
          <div class="stat-label">未通过课程</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ avgScore }}分</div>
          <div class="stat-label">平均成绩</div>
        </div>
      </div>
    </div>

    <!-- 课程表与课程进度 -->
    <div class="course-content">
      <!-- 课程表 -->
      <div class="course-schedule">
        <h3 class="section-title">
          <el-icon><Calendar /></el-icon> 本周课程表
        </h3>
        
        <div class="schedule-grid">
          <div class="grid-header">
            <div class="time-column"></div>
            <div class="day-column" v-for="day in weekdays" :key="day">{{ day }}</div>
          </div>
          
          <div class="grid-body">
            <div class="time-slot" v-for="time in timeSlots" :key="time">
              <div class="time-label">{{ time }}</div>
              <div class="day-slot" v-for="day in weekdays" :key="day">
                <!-- 课程卡片 -->
                <div 
                  class="course-card" 
                  v-for="course in getCoursesByDayAndTime(day, time)" 
                  :key="course.id"
                  :style="{ backgroundColor: course.color }"
                >
                  <div class="course-name">{{ course.name }}</div>
                  <div class="course-teacher">{{ course.teacher }}</div>
                  <div class="course-location">{{ course.location }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 课程进度 -->
      <div class="course-progress">
        <h3 class="section-title">
          <el-icon><Notebook /></el-icon> 课程进度
        </h3>
        
        <div class="progress-list">
          <el-card 
            class="progress-card" 
            v-for="course in currentCourses" 
            :key="course.id"
            shadow="hover"
          >
            <div class="card-content">
              <div class="course-info">
                <div class="course-name">{{ course.name }}</div>
                <div class="course-code">{{ course.code }}</div>
              </div>
              
              <div class="progress-bar">
                <el-progress 
                  :percentage="course.progress" 
                  :color="courseProgressColor(course.progress)" 
                  :stroke-width="10"
                />
              </div>
              
              <div class="course-details">
                <div class="detail-item">
                  <span class="label">授课教师:</span>
                  <span class="value">{{ course.teacher }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">学分:</span>
                  <span class="value">{{ course.credits }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">进度:</span>
                  <span class="value">{{ course.progress }}%</span>
                </div>
              </div>
            </div>
            
            <div class="card-actions">
              <el-button 
                type="text" 
                size="small" 
                @click="viewCourseDetails(course)"
              >
                查看详情 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 历史课程记录 -->
    <div class="history-courses">
      <h3 class="section-title">
        <el-icon><History /></el-icon> 历史课程记录
      </h3>
      
      <el-table 
        :data="historyCourses" 
        border 
        stripe
        class="history-table"
      >
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="code" label="课程代码" width="120" />
        <el-table-column prop="teacher" label="授课教师" width="120" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="score" label="成绩" width="80" />
        <el-table-column prop="status" label="状态" width="100" 
          :formatter="formatCourseStatus" />
      </el-table>
      
      <div class="pagination" v-if="historyCourses.length > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          :total="historyCourses.length"
          layout="total, sizes, prev, pager, next"
          small
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Calendar, Notebook, History, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 课程状态配置
const courseStatusConfig = {
  passed: { label: '通过', type: 'success' },
  failed: { label: '未通过', type: 'danger' },
  retaking: { label: '重修中', type: 'warning' },
  auditing: { label: '旁听', type: 'info' }
}

// 响应式数据
const currentSemester = ref('2023-2024-2')
const semesters = ref([
  { value: '2023-2024-2', label: '2023-2024学年第二学期' },
  { value: '2023-2024-1', label: '2023-2024学年第一学期' },
  { value: '2022-2023-2', label: '2022-2023学年第二学期' },
  { value: '2022-2023-1', label: '2022-2023学年第一学期' }
])

const weekdays = ref(['周一', '周二', '周三', '周四', '周五', '周六', '周日'])
const timeSlots = ref(['08:00', '10:00', '14:00', '16:00', '19:00'])

const currentCourses = ref([
  {
    id: 1,
    name: '数据结构与算法',
    code: 'CS101',
    teacher: '李教授',
    credits: 4,
    progress: 65,
    schedule: [
      { day: '周一', time: '10:00', location: '逸夫楼305' },
      { day: '周四', time: '14:00', location: '实验楼201' }
    ],
    color: '#409eff'
  },
  {
    id: 2,
    name: '计算机网络',
    code: 'CS102',
    teacher: '王老师',
    credits: 3,
    progress: 75,
    schedule: [
      { day: '周二', time: '08:00', location: '主楼101' },
      { day: '周五', time: '08:00', location: '主楼101' }
    ],
    color: '#67c23a'
  },
  {
    id: 3,
    name: '操作系统',
    code: 'CS103',
    teacher: '张教授',
    credits: 4,
    progress: 50,
    schedule: [
      { day: '周三', time: '14:00', location: '计算机学院203' },
      { day: '周六', time: '10:00', location: '实验室B2' }
    ],
    color: '#e6a23c'
  }
])

const historyCourses = ref([
  {
    semester: '2023-2024-1',
    name: '高等数学（下）',
    code: 'MATH102',
    teacher: '刘教授',
    credits: 5,
    score: 85,
    status: 'passed'
  },
  {
    semester: '2023-2024-1',
    name: '线性代数',
    code: 'MATH103',
    teacher: '陈老师',
    credits: 4,
    score: 78,
    status: 'passed'
  },
  {
    semester: '2023-2024-1',
    name: '程序设计基础',
    code: 'CS001',
    teacher: '赵老师',
    credits: 4,
    score: 92,
    status: 'passed'
  }
])

const currentPage = ref(1)
const pageSize = ref(10)

// 计算属性
const completedCoursesCount = computed(() => {
  return historyCourses.value.filter(course => course.status === 'passed').length
})

const failedCoursesCount = computed(() => {
  return historyCourses.value.filter(course => course.status === 'failed').length
})

const avgScore = computed(() => {
  const passedCourses = historyCourses.value.filter(course => course.status === 'passed')
  if (passedCourses.length === 0) return 0
  const totalScore = passedCourses.reduce((sum, course) => sum + course.score, 0)
  return Math.round(totalScore / passedCourses.length)
})

// 方法
const getCoursesByDayAndTime = (day, time) => {
  return currentCourses.value.filter(course => 
    course.schedule.some(s => s.day === day && s.time === time)
  )
}

const courseProgressColor = (progress) => {
  if (progress >= 80) return '#67c23a'
  if (progress >= 60) return '#e6a23c'
  return '#f56c6c'
}

const formatCourseStatus = (row) => {
  return courseStatusConfig[row.status]?.label || '未知'
}

const viewCourseDetails = (course) => {
  ElMessage.info(`查看课程详情: ${course.name}`)
  // 实际应用中这里可以跳转到课程详情页
}

const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 初始化数据
onMounted(() => {
  // 模拟从API获取课程数据
})
</script>

<style scoped lang="scss">
.course-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

// 课程概览卡片样式
.course-overview-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
    
    .semester-selector {
      width: 220px;
    }
  }
  
  .overview-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 16px;
    
    .stat-item {
      text-align: center;
      padding: 16px;
      background: rgba(245, 247, 250, 0.7);
      border-radius: 8px;
      
      .stat-value {
        font-size: 24px;
        font-weight: 700;
        color: #409eff;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 14px;
        color: #666;
      }
    }
  }
}

// 课程内容区域样式
.course-content {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
  
  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

// 课程表样式
.course-schedule {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
  
  .section-title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
    
    .el-icon {
      margin-right: 8px;
    }
  }
  
  .schedule-grid {
    width: 100%;
    border-collapse: collapse;
    
    .grid-header {
      display: grid;
      grid-template-columns: 80px repeat(7, 1fr);
      margin-bottom: 8px;
      
      .time-column {
        width: 80px;
        text-align: center;
        font-weight: 500;
        color: #666;
      }
      
      .day-column {
        text-align: center;
        font-weight: 500;
        color: #666;
        padding: 8px 0;
      }
    }
    
    .grid-body {
      .time-slot {
        display: grid;
        grid-template-columns: 80px repeat(7, 1fr);
        margin-bottom: 8px;
        
        .time-label {
          width: 80px;
          text-align: center;
          font-size: 14px;
          color: #666;
          padding-top: 8px;
        }
        
        .day-slot {
          position: relative;
          min-height: 80px;
          border: 1px solid #ebeef5;
          border-radius: 4px;
          margin: 0 4px;
        }
        
        .course-card {
          position: absolute;
          width: calc(100% - 8px);
          margin: 4px;
          padding: 8px;
          border-radius: 4px;
          color: white;
          font-size: 12px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
          
          .course-name {
            font-weight: 500;
            margin-bottom: 2px;
          }
          
          .course-teacher, .course-location {
            font-size: 11px;
            opacity: 0.8;
          }
        }
      }
    }
  }
}

// 课程进度样式
.course-progress {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
  
  .section-title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
    
    .el-icon {
      margin-right: 8px;
    }
  }
  
  .progress-list {
    .progress-card {
      margin-bottom: 16px;
      
      .card-content {
        .course-info {
          display: flex;
          justify-content: space-between;
          margin-bottom: 12px;
          
          .course-name {
            font-size: 16px;
            font-weight: 500;
            color: #333;
          }
          
          .course-code {
            font-size: 14px;
            color: #909399;
          }
        }
        
        .progress-bar {
          margin-bottom: 12px;
        }
        
        .course-details {
          display: flex;
          justify-content: space-between;
          font-size: 14px;
          
          .detail-item {
            .label {
              color: #909399;
            }
            
            .value {
              color: #333;
            }
          }
        }
      }
      
      .card-actions {
        text-align: right;
      }
    }
  }
}

// 历史课程记录样式
.history-courses {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
  
  .section-title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
    
    .el-icon {
      margin-right: 8px;
    }
  }
  
  .history-table {
    width: 100%;
    margin-bottom: 16px;
  }
  
  .pagination {
    text-align: right;
  }
}
</style>