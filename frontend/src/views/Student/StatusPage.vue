<template>
  <div class="status-content">
    <!-- 学籍状态卡片 -->
    <div class="status-card">
      <div class="card-header">
        <h2>学籍状态概览</h2>
        <el-button 
          type="primary" 
          size="small" 
          @click="handleRefresh"
          class="refresh-btn"
        >
          <el-icon><Refresh /></el-icon> 刷新数据
        </el-button>
      </div>
      
      <div class="status-main">
        <div class="status-indicator">
          <div class="status-badge">
            <el-tag 
              :type="statusConfig[status].type" 
              size="large"
              effect="dark"
            >
              {{ statusConfig[status].label }}
            </el-tag>
          </div>
          <div class="status-meta">
            <p>状态生效日期: {{ formatDate(effectiveDate) }}</p>
            <p>预计毕业日期: {{ formatDate(graduationDate) }}</p>
          </div>
        </div>
        
        <div class="status-stats">
          <div class="stat-item">
            <div class="stat-value">{{ credits }}</div>
            <div class="stat-label">已修学分</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ attendanceRate }}%</div>
            <div class="stat-label">出勤率</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ performanceLevel }}</div>
            <div class="stat-label">学业等级</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表与数据区域 -->
    <div class="charts-container">
      <!-- 学分进度图表 -->
      <div class="chart-card">
        <h3>学分完成进度</h3>
        <div class="chart-wrapper">
          <el-progress
            :percentage="creditProgress"
            :stroke-width="10"
            :stroke-linecap="'round'"
            class="credit-progress"
          />
          <div class="credit-info">
            <div class="credit-detail">
              <span class="detail-label">已修:</span>
              <span class="detail-value">{{ credits }}/{{ totalCredits }} 学分</span>
            </div>
            <div class="credit-detail">
              <span class="detail-label">进度:</span>
              <span class="detail-value">{{ creditProgress }}%</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 学籍状态趋势图 -->
      <div class="chart-card">
        <h3>学籍状态变化趋势</h3>
        <div class="chart-wrapper">
          <el-chart :option="statusTrendOption" class="trend-chart" />
        </div>
      </div>
    </div>

    <!-- 状态变更记录 -->
    <div class="history-card">
      <div class="history-header">
        <h3>学籍状态变更记录</h3>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索变更原因"
          prefix-icon="Search"
          size="small"
          class="search-input"
        />
      </div>
      
      <el-table 
        :data="filteredHistory" 
        border 
        stripe
        class="history-table"
      >
        <el-table-column 
          prop="date" 
          label="变更日期" 
          width="160"
          :formatter="formatDate"
        />
        <el-table-column 
          prop="previousStatus" 
          label="变更前状态"
          :formatter="formatStatus"
        />
        <el-table-column 
          prop="currentStatus" 
          label="变更后状态"
          :formatter="formatStatus"
        />
        <el-table-column 
          prop="reason" 
          label="变更原因" 
          min-width="200"
        />
        <el-table-column 
          prop="operator" 
          label="操作人" 
          width="120"
        />
        <el-table-column 
          prop="remark" 
          label="备注" 
          min-width="150"
        />
      </el-table>
      
      <div class="pagination" v-if="statusHistory.length > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          :total="statusHistory.length"
          layout="total, sizes, prev, pager, next"
          small
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 状态配置映射
const statusConfig = {
  normal: { label: '正常', type: 'success' },
  suspended: { label: '休学', type: 'warning' },
  probation: { label: '试读', type: 'info' },
  graduated: { label: '已毕业', type: 'primary' },
  dropped: { label: '退学', type: 'danger' }
}

// 响应式数据
const status = ref('normal')
const effectiveDate = ref('2023-09-01')
const graduationDate = ref('2027-06-30')
const credits = ref(68)
const totalCredits = ref(140)
const attendanceRate = ref(96)
const performanceLevel = ref('良好')
const statusHistory = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 计算属性
const creditProgress = computed(() => {
  return Math.round((credits.value / totalCredits.value) * 100)
})

const filteredHistory = computed(() => {
  return statusHistory.value.filter(item => 
    item.reason.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

// 状态趋势图数据
const statusTrendOption = ref({
  xAxis: {
    type: 'category',
    data: ['大一上', '大一下', '大二上', '大二下', '大三上', '当前']
  },
  yAxis: {
    type: 'value',
    min: 0,
    max: 100,
    interval: 20
  },
  series: [
    {
      data: [85, 88, 92, 89, 94, 96],
      type: 'line',
      smooth: true,
      lineStyle: { width: 3 },
      itemStyle: { radius: 6 },
      color: '#409eff'
    }
  ],
  tooltip: {
    trigger: 'axis'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  }
})

// 方法
const formatDate = (dateStr) => {
  if (!dateStr) return '--'
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).replace(/\//g, '-')
}

const formatStatus = (statusCode) => {
  return statusConfig[statusCode]?.label || '未知'
}

const handleRefresh = () => {
  // 模拟数据刷新
  const loading = ElMessage.loading({
    message: '正在刷新数据...',
    duration: 0
  })
  
  setTimeout(() => {
    loading.close()
    ElMessage.success('数据刷新成功')
  }, 800)
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 初始化数据
onMounted(() => {
  // 模拟从API获取状态变更历史
  statusHistory.value = [
    {
      date: '2023-09-01',
      previousStatus: '',
      currentStatus: 'normal',
      reason: '新生入学',
      operator: '系统',
      remark: '初始学籍状态'
    },
    {
      date: '2024-02-15',
      previousStatus: 'normal',
      currentStatus: 'normal',
      reason: '学期注册',
      operator: '张老师',
      remark: '春季学期正常注册'
    },
    {
      date: '2024-09-01',
      previousStatus: 'normal',
      currentStatus: 'normal',
      reason: '学期注册',
      operator: '李老师',
      remark: '秋季学期正常注册'
    }
  ]
})
</script>

<style scoped lang="scss">
.status-content {
  padding: 20px;
  background: transparent;
  min-height: calc(100vh - 60px);
}

// 学籍状态卡片样式
.status-card {
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
    
    .refresh-btn {
      display: flex;
      align-items: center;
    }
  }
  
  .status-main {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .status-indicator {
      .status-badge {
        margin-bottom: 12px;
        
        .el-tag {
          font-size: 16px;
          padding: 6px 16px;
        }
      }
      
      .status-meta {
        p {
          margin: 4px 0;
          color: #666;
          font-size: 14px;
        }
      }
    }
    
    .status-stats {
      display: flex;
      gap: 30px;
      
      .stat-item {
        text-align: center;
        
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
}

// 图表容器样式
.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
  
  .chart-card {
    background: rgba(255, 255, 255, 0.75);
    backdrop-filter: blur(12px);
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.4);
    
    h3 {
      margin: 0 0 16px 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
    
    .chart-wrapper {
      width: 100%;
      height: 160px;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }
  }
}

// 学分进度样式
.credit-progress {
  width: 100%;
  margin-bottom: 16px;
}

.credit-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  
  .credit-detail {
    .detail-label {
      color: #666;
      margin-right: 4px;
    }
    
    .detail-value {
      font-weight: 500;
      color: #333;
    }
  }
}

// 状态变更历史样式
.history-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.4);
  
  .history-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
    
    .search-input {
      width: 300px;
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

// 响应式调整
@media (max-width: 768px) {
  .status-main {
    flex-direction: column;
    align-items: flex-start !important;
    
    .status-stats {
      margin-top: 16px;
      width: 100%;
      justify-content: space-between;
    }
  }
  
  .charts-container {
    grid-template-columns: 1fr;
  }
  
  .history-header {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 12px;
    
    .search-input {
      width: 100%;
    }
  }
}
</style>