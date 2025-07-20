<template>
  <div class="course-selection-page">
    <!-- 选课概览卡片 -->
    <div class="selection-overview">
      <div class="overview-header">
        <h2>选课概览</h2>
        <div class="semester-selector">
          <el-select v-model="currentSemester" placeholder="选择学期" size="small">
            <el-option v-for="item in semesters" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
      </div>

      <div class="stats-container">
        <div class="stats-card">
          <div class="card-icon">
            <el-icon>
              <Calendar />
            </el-icon>
          </div>
          <div class="card-content">
            <div class="stat-value">{{ availableCourses.length }}</div>
            <div class="stat-label">可选课程</div>
          </div>
        </div>

        <div class="stats-card">
          <div class="card-icon">
            <el-icon>
              <Check />
            </el-icon>
          </div>
          <div class="card-content">
            <div class="stat-value">{{ selectedCourses.length }}</div>
            <div class="stat-label">已选课程</div>
          </div>
        </div>

        <div class="stats-card">
          <div class="card-icon">
            <el-icon>
              <Clock />
            </el-icon>
          </div>
          <div class="card-content">
            <div class="stat-value">{{ selectedCredits }}/{{ maxCredits }}</div>
            <div class="stat-label">已选学分</div>
          </div>
        </div>

        <div class="stats-card">
          <div class="card-icon">
            <el-icon><Time /></el-icon>
          </div>
          <div class="card-content">
            <div class="stat-value">{{ selectionDeadline }}</div>
            <div class="stat-label">选课截止</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 可选课程列表 -->
    <div class="available-courses">
      <div class="section-header">
        <h3>可选课程</h3>
        <div class="course-filters">
          <el-input v-model="searchKeyword" placeholder="搜索课程名称" prefix-icon="Search" size="small"
            class="search-input" />

          <el-select v-model="categoryFilter" placeholder="课程分类" size="small">
            <el-option label="全部类别" value="" />
            <el-option label="专业必修课" value="required" />
            <el-option label="专业选修课" value="elective" />
            <el-option label="公共必修课" value="general_required" />
            <el-option label="公共选修课" value="general_elective" />
          </el-select>

          <el-select v-model="timeFilter" placeholder="上课时间" size="small">
            <el-option label="全部时间" value="" />
            <el-option label="周一上午" value="mon_morning" />
            <el-option label="周一下午" value="mon_afternoon" />
            <el-option label="周二上午" value="tue_morning" />
            <el-option label="周二下午" value="tue_afternoon" />
            <!-- 更多时间选项 -->
          </el-select>
        </div>
      </div>

      <el-table :data="filteredAvailableCourses" border stripe class="course-table">
        <el-table-column prop="code" label="课程代码" width="120" />
        <el-table-column prop="name" label="课程名称" min-width="200" />
        <el-table-column prop="teacher" label="授课教师" width="120" />
        <el-table-column prop="category" label="课程类别" width="120" :formatter="formatCategory" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="time" label="上课时间" min-width="150" />
        <el-table-column prop="location" label="上课地点" width="120" />
        <el-table-column prop="capacity" label="容量/已选" width="120" :formatter="formatCapacity" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="selectCourse(scope.row)"
              :disabled="!scope.row.available || isConflict(scope.row)">
              {{ isConflict(scope.row) ? '时间冲突' : (scope.row.available ? '选课' : '已满') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" v-if="filteredAvailableCourses.length > 0">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[5, 10, 20]" :page-size="pageSize" :total="filteredAvailableCourses.length"
          layout="total, sizes, prev, pager, next" small />
      </div>
    </div>

    <!-- 已选课程列表 -->
    <div class="selected-courses">
      <div class="section-header">
        <h3>已选课程</h3>
        <div class="credit-info">
          已选学分: <span class="credit-value">{{ selectedCredits }}</span> / 最大学分: <span class="credit-value">{{ maxCredits
            }}</span>
        </div>
      </div>

      <el-table :data="selectedCourses" border stripe class="course-table">
        <el-table-column prop="code" label="课程代码" width="120" />
        <el-table-column prop="name" label="课程名称" min-width="200" />
        <el-table-column prop="teacher" label="授课教师" width="120" />
        <el-table-column prop="category" label="课程类别" width="120" :formatter="formatCategory" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="time" label="上课时间" min-width="150" />
        <el-table-column prop="location" label="上课地点" width="120" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="danger" size="small" @click="dropCourse(scope.row)">
              退选
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="selection-actions">
        <el-button type="primary" size="small" @click="confirmSelection">
          确认选课
        </el-button>
        <el-button type="warning" size="small" @click="clearSelection">
          清空已选
        </el-button>
      </div>
    </div>

    <!-- 课程表预览 -->
    <div class="schedule-preview">
      <div class="section-header">
        <h3>课程表预览</h3>
        <div class="preview-options">
          <el-radio-group v-model="previewType">
            <el-radio-button label="text">文本视图</el-radio-button>
            <el-radio-button label="grid">网格视图</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <div class="preview-content">
        <!-- 文本视图 -->
        <div v-if="previewType === 'text'" class="text-preview">
          <el-card class="day-card" v-for="day in weekdays" :key="day" shadow="hover">
            <template #header>
              <div class="day-header">{{ day }}</div>
            </template>

            <div class="day-courses">
              <div class="course-item" v-for="course in getCoursesByDay(day)" :key="course.id"
                :style="{ backgroundColor: course.color }">
                <div class="course-time">{{ course.timeSlot }}</div>
                <div class="course-name">{{ course.name }}</div>
                <div class="course-teacher">{{ course.teacher }}</div>
                <div class="course-location">{{ course.location }}</div>
              </div>

              <div class="no-courses" v-if="getCoursesByDay(day).length === 0">
                今天没有课程
              </div>
            </div>
          </el-card>
        </div>

        <!-- 网格视图 -->
        <div v-else class="grid-preview">
          <div class="grid-header">
            <div class="time-column"></div>
            <div class="day-column" v-for="day in weekdays" :key="day">{{ day }}</div>
          </div>

          <div class="grid-body">
            <div class="time-slot" v-for="time in timeSlots" :key="time">
              <div class="time-label">{{ time }}</div>
              <div class="day-slot" v-for="day in weekdays" :key="day">
                <!-- 课程卡片 -->
                <div class="course-card" v-for="course in getCoursesByDayAndTime(day, time)" :key="course.id"
                  :style="{ backgroundColor: course.color }">
                  <div class="course-name">{{ course.name }}</div>
                  <div class="course-teacher">{{ course.teacher }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 选课指南 -->
    <div class="selection-guide">
      <div class="section-header">
        <h3>选课指南</h3>
      </div>

      <div class="guide-content">
        <el-collapse accordion>
          <el-collapse-item title="选课流程" name="1">
            <div class="guide-item">
              <ol>
                <li>在"可选课程"列表中浏览或搜索感兴趣的课程</li>
                <li>点击"选课"按钮选择课程，系统会自动检查时间冲突</li>
                <li>选择的课程会显示在"已选课程"列表中</li>
                <li>如需调整，可在"已选课程"列表中点击"退选"按钮取消选课</li>
                <li>确认选课无误后，点击"确认选课"按钮完成选课</li>
              </ol>
            </div>
          </el-collapse-item>

          <el-collapse-item title="选课须知" name="2">
            <div class="guide-item">
              <ul>
                <li>每学期最多可选修 {{ maxCredits }} 学分的课程</li>
                <li>所选课程时间不能冲突</li>
                <li>专业必修课必须在规定学期内完成</li>
                <li>公共选修课需满足学校规定的最低学分要求</li>
                <li>选课截止日期后将无法修改选课</li>
              </ul>
            </div>
          </el-collapse-item>

          <el-collapse-item title="常见问题" name="3">
            <div class="guide-item">
              <p><strong>Q: 如何解决选课时间冲突问题？</strong></p>
              <p>A: 可以调整课程选择，避免选择同一时间段的课程，或使用课程表预览功能查看时间安排。</p>

              <p><strong>Q: 选课成功后可以退选吗？</strong></p>
              <p>A: 可以，在选课截止日期前，您可以在"已选课程"列表中退选课程。</p>

              <p><strong>Q: 课程容量已满怎么办？</strong></p>
              <p>A: 您可以选择其他课程，或定期查看课程容量，部分学生会在截止前退选。</p>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Calendar, Check, Clock, Time } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 学期数据
const currentSemester = ref('2023-2024-2')
const semesters = ref([
  { value: '2023-2024-2', label: '2023-2024学年第二学期' },
  { value: '2024-2025-1', label: '2024-2025学年第一学期' }
])

// 可选课程数据
const availableCourses = ref([
  {
    id: 1,
    code: 'CS101',
    name: '数据结构与算法',
    teacher: '李教授',
    category: 'required',
    credits: 4,
    time: '周一 10:00-11:40, 周四 14:00-15:40',
    location: '逸夫楼305',
    capacity: 100,
    selected: 85,
    available: true,
    color: '#409eff',
    timeSlots: [
      { day: '周一', time: '10:00', slot: '10:00-11:40' },
      { day: '周四', time: '14:00', slot: '14:00-15:40' }
    ]
  },
  {
    id: 2,
    code: 'CS102',
    name: '计算机网络',
    teacher: '王老师',
    category: 'required',
    credits: 3,
    time: '周二 08:00-09:40, 周五 08:00-09:40',
    location: '主楼101',
    capacity: 80,
    selected: 78,
    available: true,
    color: '#67c23a',
    timeSlots: [
      { day: '周二', time: '08:00', slot: '08:00-09:40' },
      { day: '周五', time: '08:00', slot: '08:00-09:40' }
    ]
  },
  {
    id: 3,
    code: 'CS103',
    name: '操作系统',
    teacher: '张教授',
    category: 'required',
    credits: 4,
    time: '周三 14:00-15:40, 周六 10:00-11:40',
    location: '计算机学院203',
    capacity: 60,
    selected: 60,
    available: false,
    color: '#e6a23c',
    timeSlots: [
      { day: '周三', time: '14:00', slot: '14:00-15:40' },
      { day: '周六', time: '10:00', slot: '10:00-11:40' }
    ]
  },
  {
    id: 4,
    code: 'CS104',
    name: '人工智能导论',
    teacher: '赵教授',
    category: 'elective',
    credits: 3,
    time: '周一 14:00-15:40, 周三 08:00-09:40',
    location: '信息楼401',
    capacity: 90,
    selected: 45,
    available: true,
    color: '#9d5cd6',
    timeSlots: [
      { day: '周一', time: '14:00', slot: '14:00-15:40' },
      { day: '周三', time: '08:00', slot: '08:00-09:40' }
    ]
  }
])

// 已选课程数据
const selectedCourses = ref([
  {
    id: 1,
    code: 'CS101',
    name: '数据结构与算法',
    teacher: '李教授',
    category: 'required',
    credits: 4,
    time: '周一 10:00-11:40, 周四 14:00-15:40',
    location: '逸夫楼305',
    color: '#409eff',
    timeSlots: [
      { day: '周一', time: '10:00', slot: '10:00-11:40' },
      { day: '周四', time: '14:00', slot: '14:00-15:40' }
    ]
  }
])

// 搜索与筛选
const searchKeyword = ref('')
const categoryFilter = ref('')
const timeFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 课程表预览
const previewType = ref('text')
const weekdays = ref(['周一', '周二', '周三', '周四', '周五', '周六', '周日'])
const timeSlots = ref(['08:00', '10:00', '14:00', '16:00', '19:00'])

// 配置参数
const maxCredits = 25
const selectionDeadline = '2023-07-30'

// 计算属性
const filteredAvailableCourses = computed(() => {
  return availableCourses.value
    .filter(course =>
      course.name.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      course.code.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      course.teacher.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
    .filter(course =>
      !categoryFilter.value || course.category === categoryFilter.value
    )
    .filter(course =>
      !timeFilter.value || course.time.toLowerCase().includes(timeFilter.value.toLowerCase())
    )
})

const selectedCredits = computed(() => {
  return selectedCourses.value.reduce((sum, course) => sum + course.credits, 0)
})

// 方法
const formatCategory = (row) => {
  const categories = {
    'required': '专业必修',
    'elective': '专业选修',
    'general_required': '公共必修',
    'general_elective': '公共选修'
  }
  return categories[row.category] || row.category
}

const formatCapacity = (row) => {
  return `${row.selected}/${row.capacity}`
}

const selectCourse = (course) => {
  if (selectedCredits.value + course.credits > maxCredits) {
    ElMessage.warning(`已超过最大学分限制(${maxCredits})`)
    return
  }

  if (isConflict(course)) {
    ElMessage.warning('该课程与已选课程时间冲突')
    return
  }

  // 检查是否已选
  const isAlreadySelected = selectedCourses.value.some(item => item.id === course.id)
  if (isAlreadySelected) {
    ElMessage.warning('该课程已选')
    return
  }

  // 添加到已选课程
  selectedCourses.value.push({
    ...course,
    available: false
  })

  // 更新可选课程状态
  const index = availableCourses.value.findIndex(item => item.id === course.id)
  if (index !== -1) {
    availableCourses.value[index].selected += 1
    if (availableCourses.value[index].selected >= availableCourses.value[index].capacity) {
      availableCourses.value[index].available = false
    }
  }

  ElMessage.success('选课成功')
}

const dropCourse = (course) => {
  ElMessageBox.confirm(
    `确定要退选《${course.name}》吗？`,
    '确认退选',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 从已选课程中移除
    selectedCourses.value = selectedCourses.value.filter(item => item.id !== course.id)

    // 更新可选课程状态
    const index = availableCourses.value.findIndex(item => item.id === course.id)
    if (index !== -1) {
      availableCourses.value[index].selected -= 1
      if (availableCourses.value[index].selected < availableCourses.value[index].capacity) {
        availableCourses.value[index].available = true
      }
    }

    ElMessage.success('退选成功')
  }).catch(() => {
    ElMessage.info('已取消退选')
  })
}

const isConflict = (course) => {
  // 检查与已选课程是否有时间冲突
  for (const timeSlot of course.timeSlots) {
    for (const selectedCourse of selectedCourses.value) {
      for (const selectedTimeSlot of selectedCourse.timeSlots) {
        if (timeSlot.day === selectedTimeSlot.day && timeSlot.time === selectedTimeSlot.time) {
          return true
        }
      }
    }
  }
  return false
}

const confirmSelection = () => {
  if (selectedCourses.value.length === 0) {
    ElMessage.warning('请先选择课程')
    return
  }

  ElMessageBox.confirm(
    `确定要提交当前选课吗？共选择了 ${selectedCourses.value.length} 门课程，总学分为 ${selectedCredits.value}。`,
    '确认提交',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 模拟提交选课
    ElMessage.success('选课提交成功，请等待审核')
  }).catch(() => {
    ElMessage.info('已取消提交')
  })
}

const clearSelection = () => {
  if (selectedCourses.value.length === 0) {
    ElMessage.info('没有已选课程')
    return
  }

  ElMessageBox.confirm(
    '确定要清空所有已选课程吗？',
    '确认清空',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 恢复可选课程状态
    selectedCourses.value.forEach(course => {
      const index = availableCourses.value.findIndex(item => item.id === course.id)
      if (index !== -1) {
        availableCourses.value[index].selected -= 1
        if (availableCourses.value[index].selected < availableCourses.value[index].capacity) {
          availableCourses.value[index].available = true
        }
      }
    })

    // 清空已选课程
    selectedCourses.value = []

    ElMessage.success('已清空所有已选课程')
  }).catch(() => {
    ElMessage.info('已取消清空')
  })
}

const getCoursesByDay = (day) => {
  return selectedCourses.value.filter(course =>
    course.timeSlots.some(slot => slot.day === day)
  ).map(course => {
    const slot = course.timeSlots.find(s => s.day === day)
    return {
      ...course,
      timeSlot: slot.slot
    }
  })
}

const getCoursesByDayAndTime = (day, time) => {
  return selectedCourses.value.filter(course =>
    course.timeSlots.some(slot => slot.day === day && slot.time === time)
  )
}

const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 初始化数据
onMounted(() => {
  // 模拟从API获取数据
})
</script>

<style scoped lang="scss">
.course-selection-page {
  padding: 20px;
  background: transparent;
  min-height: calc(100vh - 60px);
}

// 选课概览卡片样式
.selection-overview {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);

  .overview-header {
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
  }

  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;

    .stats-card {
      background: rgba(245, 247, 250, 0.7);
      border-radius: 12px;
      padding: 16px;
      display: flex;
      align-items: center;

      .card-icon {
        font-size: 32px;
        color: #409eff;
        margin-right: 16px;
      }

      .card-content {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #333;
        }

        .stat-label {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
}

// 可选课程和已选课程列表样式
.available-courses,
.selected-courses,
.schedule-preview,
.selection-guide {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .course-filters {
      display: flex;
      gap: 12px;
    }

    .credit-info {
      font-size: 14px;
      color: #666;

      .credit-value {
        font-weight: 500;
        color: #409eff;
      }
    }
  }

  .course-table {
    width: 100%;
    margin-bottom: 16px;
  }

  .pagination {
    text-align: right;
  }
}

// 已选课程操作按钮
.selection-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  justify-content: flex-end;
}

// 课程表预览样式
.schedule-preview {
  .preview-content {
    margin-top: 16px;

    // 文本视图
    .text-preview {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 16px;

      .day-card {
        .day-header {
          font-size: 16px;
          font-weight: 500;
          color: #333;
        }

        .day-courses {
          .course-item {
            padding: 8px;
            border-radius: 4px;
            margin-bottom: 8px;
            color: white;

            .course-time {
              font-size: 12px;
              margin-bottom: 2px;
            }

            .course-name {
              font-size: 14px;
              font-weight: 500;
              margin-bottom: 2px;
            }

            .course-teacher,
            .course-location {
              font-size: 12px;
              opacity: 0.8;
            }
          }

          .no-courses {
            padding: 8px;
            text-align: center;
            color: #909399;
            font-size: 14px;
          }
        }
      }
    }

    // 网格视图
    .grid-preview {
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
            min-height: 60px;
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
          }
        }
      }
    }
  }
}
</style>