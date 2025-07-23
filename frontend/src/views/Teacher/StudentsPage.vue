<template>
  <div class="student-dashboard" :class="{ 'dark': isDarkMode }">
    <!-- 顶部导航栏 -->
    <header class="bg-white dark:bg-dark-light shadow-sm sticky top-0 z-50 transition-all duration-300">
      <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <div class="flex items-center space-x-3">
          <div class="text-primary text-2xl">
            <i class="fa fa-graduation-cap"></i>
          </div>
          <h1 class="text-xl font-semibold">学生信息管理系统</h1>
        </div>
        
        <div class="flex items-center space-x-4">
          <el-button type="text" @click="isDarkMode = !isDarkMode">
            <el-icon>
              <i :class="isDarkMode ? 'fa fa-sun-o' : 'fa fa-moon-o'"></i>
            </el-icon>
          </el-button>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link cursor-pointer">
              <el-avatar size="small" src="https://picsum.photos/200/200?random=1" />
              <span class="ml-2">管理员</span>
              <i class="el-icon-arrow-down el-icon--right ml-1"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 数据概览卡片 -->
    <div class="stats-overview modern-card">
      <div class="card-header">
        <h2>数据概览</h2>
      </div>
      <div class="stats-container">
        <div class="stats-card" :class="{ 'high-light': selectedClass }">
          <div class="stat-header">
            <h3>学生总数</h3>
            <div class="stat-value">{{ totalStudents || '0' }}</div>
          </div>
          <div class="stat-trend">
            <span v-if="studentGrowth > 0" class="trend-up">
              <el-icon>
                <ArrowUp />
              </el-icon>
              {{ studentGrowth.toFixed(1) }}%
            </span>
            <span v-else-if="studentGrowth < 0" class="trend-down">
              <el-icon>
                <ArrowDown />
              </el-icon>
              {{ Math.abs(studentGrowth).toFixed(1) }}%
            </span>
            <span v-else>持平</span>
            <span class="text-sm ml-2">vs 上学期</span>
          </div>
        </div>

        <div class="stats-card" :class="{ 'high-light': selectedClass }">
          <div class="stat-header">
            <h3>班级数量</h3>
            <div class="stat-value">{{ classes.length || '0' }}</div>
          </div>
          <div class="stat-meta">
            <div class="text-sm text-secondary">
              {{ activeClasses || '0' }} 个活跃班级
            </div>
          </div>
        </div>

        <div class="stats-card" :class="{ 'high-light': selectedClass }">
          <div class="stat-header">
            <h3>男女比例</h3>
            <div class="stat-value">{{ maleRatio || '0' }} : {{ femaleRatio || '0' }}</div>
          </div>
          <div class="stat-chart">
            <el-progress :percentage="malePercentage" :stroke-width="24" :color="['#409eff', '#e4e7ed']" />
          </div>
          <div class="stat-meta mt-2">
            <div class="flex justify-between">
              <span class="text-sm"><span class="text-primary">男</span> {{ malePercentage }}%</span>
              <span class="text-sm"><span class="text-tertiary">女</span> {{ 100 - malePercentage }}%</span>
            </div>
          </div>
        </div>

        <div class="stats-card" :class="{ 'high-light': selectedClass }">
          <div class="stat-header">
            <h3>入学年份分布</h3>
            <div class="stat-value">{{ latestEnrollmentYear || '未知' }}</div>
          </div>
          <div class="stat-chart">
            <el-progress :percentage="latestYearPercentage" :stroke-width="18" color="#6366f1" />
          </div>
          <div class="stat-meta mt-2">
            <div class="text-sm text-secondary">
              {{ latestYearCount || '0' }} 人 (占 {{ latestYearPercentage || '0' }}%)
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 学生管理区域 -->
    <div class="student-management modern-card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2>学生信息管理</h2>
        <div class="header-actions">
          <el-button @click="addStudent" type="primary" size="small">
            <el-icon>
              <Plus />
            </el-icon> 添加学生
          </el-button>
          <el-button @click="importStudents" type="success" size="small" class="ml-2">
            <el-icon>
              <Upload />
            </el-icon> 导入学生
          </el-button>
        </div>
      </div>

      <!-- 筛选与操作区 -->
      <div class="management-controls">
        <!-- 顶部筛选区 -->
        <div class="filters-section">
          <div class="filter-group">
            <el-select v-model="selectedClass" placeholder="选择班级" size="small">
              <el-option v-for="classItem in classes" :key="classItem.id" :label="classItem.name"
                :value="classItem.id" />
            </el-select>
            <el-select v-model="selectedMajor" placeholder="选择专业" size="small">
              <el-option v-for="major in majors" :key="major.id" :label="major.name" :value="major.id" />
            </el-select>
            <el-select v-model="studentStatus" placeholder="学生状态" size="small">
              <el-option label="全部" value="all" />
              <el-option label="在校" value="active" />
              <el-option label="毕业" value="graduated" />
              <el-option label="休学" value="suspended" />
            </el-select>
          </div>

          <div class="action-buttons">
            <el-button @click="exportStudents" type="success" size="small" class="btn-export">
              <el-icon>
                <Download />
              </el-icon> 导出数据
            </el-button>
            <el-button @click="batchEdit" type="warning" size="small" class="btn-batch">
              <el-icon>
                <Edit />
              </el-icon> 批量编辑
            </el-button>
          </div>
        </div>

        <!-- 搜索与筛选区 -->
        <div class="search-section">
          <el-input v-model="searchKeyword" placeholder="输入学生姓名或学号" size="small" class="search-input" />
          <el-button @click="searchStudents" type="primary" size="small" class="ml-2">
            <el-icon>
              <Search />
            </el-icon> 搜索
          </el-button>
          <el-button @click="resetFilters" type="text" size="small" class="ml-2">
            重置筛选
          </el-button>
        </div>
      </div>

      <!-- 表格内容区 -->
      <div class="table-container">
        <el-table :data="filteredStudents" border stripe class="students-data-table" style="width: 100%"
          @row-dblclick="editStudent">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="gender" label="性别" width="80" :formatter="formatGender" />
          <el-table-column prop="class" label="班级" width="120" />
          <el-table-column prop="major" label="专业" width="120" />
          <el-table-column prop="enrollmentDate" label="入学日期" width="120" />
          <el-table-column prop="status" label="状态" width="100" :formatter="formatStatus" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button type="text" size="small" @click="viewStudent(scope.row)">
                <el-icon>
                  <View />
                </el-icon> 查看
              </el-button>
              <el-button type="text" size="small" @click="editStudent(scope.row)">
                <el-icon>
                  <Edit />
                </el-icon> 编辑
              </el-button>
              <el-button type="text" size="small" @click="deleteStudent(scope.row)">
                <el-icon>
                  <Delete />
                </el-icon> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页控件 -->
      <div class="pagination" v-if="filteredStudents.length > 0">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[10, 20, 50]" :page-size="pageSize" :total="filteredStudents.length"
          layout="total, sizes, prev, pager, next" small />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Upload, Download, Plus, Edit, Delete, Search, View, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const isDarkMode = ref(false)

// 筛选条件
const selectedClass = ref(null)
const selectedMajor = ref(null)
const studentStatus = ref('all')
const searchKeyword = ref('')

// 数据
const classes = ref([
  { id: 1, name: '计算机科学与技术1班' },
  { id: 2, name: '计算机科学与技术2班' },
  { id: 3, name: '软件工程1班' },
  { id: 4, name: '软件工程2班' },
  { id: 5, name: '数据科学与大数据技术1班' },
  { id: 6, name: '人工智能1班' }
])

const majors = ref([
  { id: 1, name: '计算机科学与技术' },
  { id: 2, name: '软件工程' },
  { id: 3, name: '数据科学与大数据技术' },
  { id: 4, name: '人工智能' },
  { id: 5, name: '网络工程' }
])

const allStudents = ref([
  {
    id: 1,
    studentId: '2023001',
    name: '张三',
    gender: '男',
    classId: 1,
    majorId: 1,
    enrollmentDate: '2023-09-01',
    phone: '13800138001',
    email: 'zhangsan@example.com',
    status: 'active'
  },
  {
    id: 2,
    studentId: '2023002',
    name: '李四',
    gender: '男',
    classId: 1,
    majorId: 1,
    enrollmentDate: '2023-09-01',
    phone: '13800138002',
    email: 'lisi@example.com',
    status: 'active'
  },
  {
    id: 3,
    studentId: '2023003',
    name: '王五',
    gender: '女',
    classId: 2,
    majorId: 1,
    enrollmentDate: '2023-09-01',
    phone: '13800138003',
    email: 'wangwu@example.com',
    status: 'active'
  },
  {
    id: 4,
    studentId: '2023004',
    name: '赵六',
    gender: '男',
    classId: 2,
    majorId: 1,
    enrollmentDate: '2023-09-01',
    phone: '13800138004',
    email: 'zhaoliu@example.com',
    status: 'active'
  },
  {
    id: 5,
    studentId: '2023005',
    name: '钱七',
    gender: '女',
    classId: 3,
    majorId: 2,
    enrollmentDate: '2023-09-01',
    phone: '13800138005',
    email: 'qianqi@example.com',
    status: 'active'
  },
  {
    id: 6,
    studentId: '2023006',
    name: '孙八',
    gender: '男',
    classId: 3,
    majorId: 2,
    enrollmentDate: '2023-09-01',
    phone: '13800138006',
    email: 'sunba@example.com',
    status: 'active'
  },
  {
    id: 7,
    studentId: '2022001',
    name: '周九',
    gender: '女',
    classId: 4,
    majorId: 2,
    enrollmentDate: '2022-09-01',
    phone: '13800138007',
    email: 'zhoujiu@example.com',
    status: 'active'
  },
  {
    id: 8,
    studentId: '2022002',
    name: '吴十',
    gender: '男',
    classId: 4,
    majorId: 2,
    enrollmentDate: '2022-09-01',
    phone: '13800138008',
    email: 'wushi@example.com',
    status: 'graduated'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)

// 计算属性
const filteredStudents = computed(() => {
  let students = allStudents.value

  // 筛选班级
  if (selectedClass.value) {
    students = students.filter(student => student.classId === selectedClass.value)
  }

  // 筛选专业
  if (selectedMajor.value) {
    students = students.filter(student => student.majorId === selectedMajor.value)
  }

  // 筛选状态
  if (studentStatus.value !== 'all') {
    students = students.filter(student => student.status === studentStatus.value)
  }

  // 搜索关键词
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    students = students.filter(student =>
      student.name.toLowerCase().includes(keyword) ||
      student.studentId.includes(keyword)
    )
  }

  return students
})

const totalStudents = computed(() => filteredStudents.value.length)
const activeClasses = computed(() => {
  const activeClassIds = new Set()
  filteredStudents.value.forEach(student => {
    activeClassIds.add(student.classId)
  })
  return activeClassIds.size
})

// 计算男女比例
const maleStudents = computed(() => {
  return filteredStudents.value.filter(student => student.gender === '男').length
})

const femaleStudents = computed(() => {
  return filteredStudents.value.filter(student => student.gender === '女').length
})

const maleRatio = computed(() => {
  if (femaleStudents.value === 0) return maleStudents.value
  return (maleStudents.value / femaleStudents.value).toFixed(1)
})

const femaleRatio = computed(() => {
  if (maleStudents.value === 0) return femaleStudents.value
  return (femaleStudents.value / maleStudents.value).toFixed(1)
})

const malePercentage = computed(() => {
  if (totalStudents.value === 0) return 0
  return Math.round((maleStudents.value / totalStudents.value) * 100)
})

// 计算入学年份分布
const enrollmentYears = computed(() => {
  const years = {}
  filteredStudents.value.forEach(student => {
    const year = student.enrollmentDate.split('-')[0]
    years[year] = (years[year] || 0) + 1
  })
  return years
})

const latestEnrollmentYear = computed(() => {
  const years = Object.keys(enrollmentYears.value)
  return years.length > 0 ? Math.max(...years) : '未知'
})

const latestYearCount = computed(() => {
  return enrollmentYears.value[latestEnrollmentYear.value] || 0
})

const latestYearPercentage = computed(() => {
  if (totalStudents.value === 0) return 0
  return Math.round((latestYearCount.value / totalStudents.value) * 100)
})

const studentGrowth = computed(() => {
  // 模拟与上学期对比数据
  return (Math.random() * 10 - 3).toFixed(1) * 1
})

// 工具方法
const formatGender = (row) => {
  return row.gender === '男' ? '男' : '女'
}

const formatStatus = (row) => {
  const statusMap = {
    'active': '在校',
    'graduated': '毕业',
    'suspended': '休学',
    'withdrawn': '退学'
  }
  return statusMap[row.status] || '未知'
}

// 事件处理
const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const addStudent = () => {
  ElMessageBox.prompt('请输入学生信息，以逗号分隔（学号,姓名,性别,班级ID,专业ID,入学日期）', '添加学生')
    .then(({ value }) => {
      if (!value) return
      
      const [studentId, name, gender, classId, majorId, enrollmentDate] = value.split(',')
      
      if (!studentId || !name || !gender || !classId || !majorId || !enrollmentDate) {
        ElMessage.error('输入信息不完整，请重新输入')
        return
      }
      
      const newStudent = {
        id: allStudents.value.length + 1,
        studentId,
        name,
        gender,
        classId: parseInt(classId),
        majorId: parseInt(majorId),
        enrollmentDate,
        phone: '',
        email: '',
        status: 'active'
      }
      
      allStudents.value.push(newStudent)
      ElMessage.success('添加学生成功')
    })
    .catch(() => {
      ElMessage.info('已取消添加')
    })
}

const editStudent = (row) => {
  const studentData = `${row.studentId},${row.name},${row.gender},${row.classId},${row.majorId},${row.enrollmentDate}`
  
  ElMessageBox.prompt('编辑学生信息，以逗号分隔（学号,姓名,性别,班级ID,专业ID,入学日期）', '编辑学生', {
    inputValue: studentData
  })
    .then(({ value }) => {
      if (!value) return
      
      const [studentId, name, gender, classId, majorId, enrollmentDate] = value.split(',')
      
      if (!studentId || !name || !gender || !classId || !majorId || !enrollmentDate) {
        ElMessage.error('输入信息不完整，请重新输入')
        return
      }
      
      const studentIndex = allStudents.value.findIndex(s => s.id === row.id)
      
      if (studentIndex !== -1) {
        allStudents.value[studentIndex] = {
          ...allStudents.value[studentIndex],
          studentId,
          name,
          gender,
          classId: parseInt(classId),
          majorId: parseInt(majorId),
          enrollmentDate
        }
        
        ElMessage.success('编辑学生成功')
      }
    })
    .catch(() => {
      ElMessage.info('已取消编辑')
    })
}

const viewStudent = (row) => {
  const studentClass = classes.value.find(c => c.id === row.classId)?.name || '未知班级'
  const studentMajor = majors.value.find(m => m.id === row.majorId)?.name || '未知专业'
  
  const studentInfo = `
    <p><strong>学号:</strong> ${row.studentId}</p>
    <p><strong>姓名:</strong> ${row.name}</p>
    <p><strong>性别:</strong> ${row.gender}</p>
    <p><strong>班级:</strong> ${studentClass}</p>
    <p><strong>专业:</strong> ${studentMajor}</p>
    <p><strong>入学日期:</strong> ${row.enrollmentDate}</p>
    <p><strong>状态:</strong> ${formatStatus(row)}</p>
    <p><strong>电话:</strong> ${row.phone || '未填写'}</p>
    <p><strong>邮箱:</strong> ${row.email || '未填写'}</p>
  `
  
  ElMessageBox.alert(studentInfo, '学生详情', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭',
    callback: () => {}
  })
}

const deleteStudent = (row) => {
  ElMessageBox.confirm(
    `确定要删除 ${row.name} 的信息吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const studentIndex = allStudents.value.findIndex(s => s.id === row.id)
    
    if (studentIndex !== -1) {
      allStudents.value.splice(studentIndex, 1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const importStudents = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.csv,.xlsx'
  
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    ElMessage.info(`已选择文件: ${file.name}`)
    
    // 模拟导入处理
    setTimeout(() => {
      ElMessage.success('导入成功，已添加20名学生')
      
      // 模拟添加20名学生
      for (let i = 1; i <= 20; i++) {
        const randomId = Math.floor(Math.random() * 1000000)
        const randomGender = Math.random() > 0.5 ? '男' : '女'
        const randomClassId = Math.floor(Math.random() * classes.value.length) + 1
        const randomMajorId = Math.floor(Math.random() * majors.value.length) + 1
        const randomYear = Math.floor(Math.random() * 3) + 2021
        
        allStudents.value.push({
          id: allStudents.value.length + 1,
          studentId: `${randomYear}${randomId.toString().padStart(6, '0')}`,
          name: `学生${allStudents.value.length + 1}`,
          gender: randomGender,
          classId: randomClassId,
          majorId: randomMajorId,
          enrollmentDate: `${randomYear}-09-01`,
          phone: '',
          email: '',
          status: 'active'
        })
      }
    }, 1000)
  }
  
  input.click()
}

const exportStudents = () => {
  if (filteredStudents.value.length === 0) {
    ElMessage.warning('没有可导出的学生数据')
    return
  }
  
  // 创建CSV内容
  let csvContent = "学号,姓名,性别,班级,专业,入学日期,状态\n"
  
  filteredStudents.value.forEach(student => {
    const studentClass = classes.value.find(c => c.id === student.classId)?.name || '未知班级'
    const studentMajor = majors.value.find(m => m.id === student.majorId)?.name || '未知专业'
    const status = formatStatus(student)
    
    csvContent += `${student.studentId},${student.name},${student.gender},${studentClass},${studentMajor},${student.enrollmentDate},${status}\n`
  })
  
  // 创建下载链接
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.setAttribute('href', url)
  link.setAttribute('download', `学生数据_${new Date().toLocaleDateString()}.csv`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  ElMessage.success('导出学生数据成功')
}

const batchEdit = () => {
  ElMessageBox.prompt('请输入要批量修改的字段和值，格式为"字段=值"（例如：status=graduated）', '批量编辑')
    .then(({ value }) => {
      if (!value || !value.includes('=')) {
        ElMessage.error('输入格式不正确，请使用"字段=值"格式')
        return
      }
      
      const [field, newValue] = value.split('=')
      
      if (!field || !newValue) {
        ElMessage.error('输入格式不正确，请使用"字段=值"格式')
        return
      }
      
      // 检查字段是否存在
      if (field === 'id' || field === 'studentId') {
        ElMessage.error('不能批量修改ID或学号')
        return
      }
      
      // 应用批量修改
      filteredStudents.value.forEach(student => {
        const studentIndex = allStudents.value.findIndex(s => s.id === student.id)
        
        if (studentIndex !== -1) {
          // 特殊处理
          if (field === 'classId') {
            allStudents.value[studentIndex].classId = parseInt(newValue)
          } else if (field === 'majorId') {
            allStudents.value[studentIndex].majorId = parseInt(newValue)
          } else {
            allStudents.value[studentIndex][field] = newValue
          }
        }
      })
      
      ElMessage.success(`已成功批量修改 ${filteredStudents.value.length} 条记录`)
    })
    .catch(() => {
      ElMessage.info('已取消批量编辑')
    })
}

const searchStudents = () => {
  console.log('搜索学生:', searchKeyword.value)
}

const resetFilters = () => {
  // 重置筛选条件
  selectedClass.value = null
  selectedMajor.value = null
  studentStatus.value = 'all'
  searchKeyword.value = ''
}

const handleCommand = (command) => {
  switch(command) {
    case 'profile':
      ElMessage.info('查看个人信息')
      break
    case 'settings':
      ElMessage.info('系统设置')
      break
    case 'logout':
      ElMessage.confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('已成功退出登录')
      }).catch(() => {
        ElMessage.info('已取消退出')
      })
      break
  }
}

onMounted(() => {
  // 初始化时默认选择第一个班级
  if (classes.value.length > 0) {
    selectedClass.value = classes.value[0].id
  }
  
  // 模拟加载动画
  setTimeout(() => {
    const loadingElements = document.querySelectorAll('.skeleton-loading')
    loadingElements.forEach(el => el.classList.remove('skeleton-loading'))
  }, 1000)
  
  // 监听鼠标移动，用于卡片的视觉效果
  document.addEventListener('mousemove', (e) => {
    const x = e.clientX
    const y = e.clientY
    
    document.documentElement.style.setProperty('--mouse-x', `${x}px`)
    document.documentElement.style.setProperty('--mouse-y', `${y}px`)
  })
})
</script>

<style scoped lang="scss">
:root {
  --text-primary: #303133;
  --text-secondary: #606266;
  --text-tertiary: #909399;
  --text-highlight: #409eff;
  --border-color: #ebeef5;
  --card-bg: white;
  --card-hover-bg: #f5f7fa;
  --trend-up: #10b981;
  --trend-down: #ef4444;
  --shadow-light: 0 4px 12px rgba(0, 0, 0, 0.05);
  --shadow-medium: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.dark {
  --text-primary: #ffffff;
  --text-secondary: rgba(255, 255, 255, 0.7);
  --text-tertiary: rgba(255, 255, 255, 0.5);
  --text-highlight: #64748b;
  --border-color: rgba(255, 255, 255, 0.1);
  --card-bg: rgba(30, 41, 59, 0.8);
  --card-hover-bg: rgba(51, 65, 85, 0.8);
  --trend-up: #22c55e;
  --trend-down: #ef4444;
  --shadow-light: 0 4px 12px rgba(0, 0, 0, 0.15);
  --shadow-medium: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.student-dashboard {
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s ease;
  gap: 30px;
  padding: 0 15px;
  min-height: 100vh;

  .dark & {
    background-color: #1e293b;
  }
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px 30px;
  background: linear-gradient(135deg, #6366f1 0%, #818cf8 100%);
  color: white;
  border-radius: 16px;
  box-shadow: 0 10px 25px -5px rgba(99, 102, 241, 0.3);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;

  .header-content {
    z-index: 10;
  }

  h1 {
    margin: 0;
    font-size: 28px;
    font-weight: 600;
    margin-bottom: 8px;
    letter-spacing: 1px;
  }

  .subtitle {
    font-size: 16px;
    opacity: 0.8;
    letter-spacing: 0.5px;
  }

  .header-actions {
    z-index: 10;
  }

  .dark & {
    background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
    box-shadow: 0 10px 25px -5px rgba(99, 102, 241, 0.5);
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 15px 30px -5px rgba(99, 102, 241, 0.4);
  }
}

.stats-overview {
  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;

    .stats-card {
      position: relative;
      background: var(--card-bg);
      border-radius: 16px;
      padding: 24px;
      border: 1px solid var(--border-color);
      transition: all 0.3s ease;
      overflow: hidden;
      box-shadow: var(--shadow-light);
      display: flex;
      flex-direction: column;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: radial-gradient(600px circle at var(--mouse-x) var(--mouse-y),
          rgba(99, 102, 241, 0.05) 0%,
          transparent 70%);
        opacity: 0;
        transition: opacity 0.3s ease;
        z-index: 1;
      }

      &:hover {
        transform: translateY(-6px);
        box-shadow: var(--shadow-medium);
        border-color: rgba(99, 102, 241, 0.2);
      }

      &:hover::before {
        opacity: 1;
      }

      .dark & {
        background: rgba(30, 41, 59, 0.9);
        border-color: rgba(74, 85, 104, 0.4);
      }

      .stat-header {
        display: flex;
        flex-direction: column;
        margin-bottom: 16px;

        h3 {
          margin: 0;
          font-size: 16px;
          font-weight: 500;
          color: var(--text-secondary);
        }

        .stat-value {
          font-size: 36px;
          font-weight: 700;
          color: var(--text-primary);
          margin-top: 8px;
          line-height: 1.2;
        }
      }

      .stat-trend {
        display: flex;
        align-items: center;
        font-size: 14px;
        margin-top: auto;

        .trend-up {
          color: var(--trend-up);
          display: flex;
          align-items: center;
        }

        .trend-down {
          color: var(--trend-down);
          display: flex;
          align-items: center;
        }

        .text-sm {
          font-size: 12px;
          color: var(--text-tertiary);
        }
      }

      .stat-chart {
        margin-top: 16px;
      }

      .stat-meta {
        margin-top: 8px;

        .text-sm {
          font-size: 14px;
          color: var(--text-tertiary);
        }
      }
    }
  }
}

/* 学生管理卡片样式 */
.student-management {
  .management-controls {
    display: flex;
    flex-direction: column;
    gap: 16px;
    margin-bottom: 20px;
  }

  .filters-section {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: var(--card-bg);
    border-radius: 12px;
    border: 1px solid var(--border-color);
    transition: all 0.3s ease;

    .filter-group {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
    }

    .action-buttons {
      display: flex;
      gap: 12px;
    }

    .el-select {
      min-width: 200px;
      transition: all 0.2s ease;

      &:hover {
        .el-input__wrapper {
          border-color: #6366f1;
        }
      }
    }

    .el-button {
      transition: all 0.2s ease;
      border-radius: 6px;

      &:hover {
        transform: translateY(-2px);
      }
    }

    .dark & {
      background: rgba(30, 41, 59, 0.9);
      border-color: rgba(74, 85, 104, 0.4);
    }
  }

  .search-section {
    display: flex;
    gap: 12px;
    align-items: center;

    .search-input {
      flex: 1;
      max-width: 320px;
      transition: all 0.2s ease;

      .el-input__wrapper {
        transition: all 0.2s ease;
      }

      &:hover .el-input__wrapper {
        border-color: #6366f1;
      }
    }
  }

  .table-container {
    position: relative;
    border-radius: 12px;
    overflow: hidden;
    border: 1px solid var(--border-color);
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba(99, 102, 241, 0.3);
    }

    .dark & {
      border-color: rgba(74, 85, 104, 0.4);
    }
  }

  .students-data-table {
    margin: 0;
    border-radius: 12px;
    border-collapse: separate;

    .el-table__header-wrapper {
      background-color: rgba(245, 247, 250, 0.8);

      .dark & {
        background-color: rgba(42, 53, 71, 0.8);
      }
    }

    .el-table__body tr {
      transition: all 0.2s ease;

      &:hover>td {
        background-color: rgba(99, 102, 241, 0.05);
      }
    }

    .el-table__body tr.el-table__row--current>td {
      background-color: rgba(99, 102, 241, 0.1);
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    padding: 10px 0;
  }
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
    background: rgba(30, 41, 59, 0.9);
    border-color: rgba(74, 85, 104, 0.4);
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
    border-color: rgba(199, 210, 254, 0.8);
  }

  .dark &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.3);
    border-color: rgba(99, 102, 241, 0.5);
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

    h2 {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }
}

/* 骨架屏效果 */
.skeleton-loading {
  position: relative;
  overflow: hidden;
  background-color: #e2e8f0;
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    transform: translateX(-100%);
    background: linear-gradient(90deg,
        rgba(255, 255, 255, 0) 0%,
        rgba(255, 255, 255, 0.2) 20%,
        rgba(255, 255, 255, 0.5) 60%,
        rgba(255, 255, 255, 0) 100%);
    animation: shimmer 2s infinite;
  }
  
  @keyframes shimmer {
    100% {
      transform: translateX(100%);
    }
  }
}

/* 动画效果 */
.fade-in {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 按钮效果 */
.btn-hover {
  transition: all 0.2s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  }
}

/* 表格行悬停效果 */
.el-table__row {
  transition: all 0.2s ease;
  
  &:hover {
    transform: translateX(2px);
  }
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: rgba(99, 102, 241, 0.3);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(99, 102, 241, 0.5);
}

.dark ::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.dark ::-webkit-scrollbar-thumb {
  background: rgba(99, 102, 241, 0.5);
}
</style>