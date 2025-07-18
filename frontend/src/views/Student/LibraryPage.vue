<template>
  <div class="library-page">
    <!-- 借阅概览卡片 -->
    <div class="borrow-overview">
      <div class="overview-header">
        <h2>借阅概览</h2>
        <div class="status-indicator">
          <el-tag 
            type="success" 
            size="small"
          >
            借阅状态良好
          </el-tag>
        </div>
      </div>
      
      <div class="stats-container">
        <div class="stats-card">
          <div class="card-icon">
            <el-icon><Book /></el-icon>
          </div>
          <div class="card-content">
            <div class="stat-value">{{ currentBorrows.length }}</div>
            <div class="stat-label">当前借阅</div>
          </div>
        </div>
        
        <div class="stats-card">
          <div class="card-icon">
            <el-icon><History /></el-icon>
          </div>
          <div class="card-content">
            <div class="stat-value">{{ totalBorrows }}</div>
            <div class="stat-label">累计借阅</div>
          </div>
        </div>
        
        <div class="stats-card">
          <div class="card-icon">
            <el-icon><Time /></el-icon>
          </div>
          <div class="card-content">
            <div class="stat-value">{{ overdueCount }}</div>
            <div class="stat-label">逾期数量</div>
          </div>
        </div>
        
        <div class="stats-card">
          <div class="card-icon">
            <el-icon><Star /></el-icon>
          </div>
          <div class="card-content">
            <div class="stat-value">{{ availableBooks }}</div>
            <div class="stat-label">可借数量</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 当前借阅列表 -->
    <div class="current-borrows">
      <div class="section-header">
        <h3>当前借阅</h3>
        <div class="renew-all">
          <el-button 
            type="primary" 
            size="small" 
            @click="renewAllBooks"
          >
            一键续借
          </el-button>
        </div>
      </div>
      
      <el-table 
        :data="currentBorrows" 
        border 
        stripe
        class="borrow-table"
      >
        <el-table-column prop="cover" label="封面" width="100">
          <template #default="scope">
            <img 
              :src="scope.row.cover || defaultBookCover" 
              alt="图书封面" 
              class="book-cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="书名" min-width="200" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="callNumber" label="索书号" width="120" />
        <el-table-column prop="borrowDate" label="借阅日期" width="120" 
          :formatter="formatDate" />
        <el-table-column prop="dueDate" label="应还日期" width="120" 
          :formatter="formatDate" />
        <el-table-column prop="renewTimes" label="续借次数" width="100" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="renewBook(scope.row)"
              :disabled="scope.row.renewTimes >= 2 || isOverdue(scope.row.dueDate)"
            >
              续借
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination" v-if="currentBorrows.length > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          :total="currentBorrows.length"
          layout="total, sizes, prev, pager, next"
          small
        />
      </div>
    </div>

    <!-- 借阅历史 -->
    <div class="borrow-history">
      <div class="section-header">
        <h3>借阅历史</h3>
        <div class="history-filter">
          <el-select 
            v-model="historyFilter" 
            placeholder="筛选类型" 
            size="small"
          >
            <el-option label="全部" value="all" />
            <el-option label="已归还" value="returned" />
            <el-option label="逾期" value="overdue" />
          </el-select>
        </div>
      </div>
      
      <el-table 
        :data="filteredHistory" 
        border 
        stripe
        class="history-table"
      >
        <el-table-column prop="title" label="书名" min-width="200" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="borrowDate" label="借阅日期" width="120" 
          :formatter="formatDate" />
        <el-table-column prop="returnDate" label="归还日期" width="120" 
          :formatter="formatDate" />
        <el-table-column prop="status" label="状态" width="100" 
          :formatter="formatStatus" />
      </el-table>
      
      <div class="pagination" v-if="filteredHistory.length > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          :total="filteredHistory.length"
          layout="total, sizes, prev, pager, next"
          small
        />
      </div>
    </div>

    <!-- 图书搜索 -->
    <div class="book-search">
      <div class="search-header">
        <h3>图书搜索</h3>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="输入书名、作者或ISBN"
            prefix-icon="Search"
            size="small"
            class="search-input"
          />
          <el-button 
            type="primary" 
            size="small" 
            @click="searchBooks"
          >
            搜索
          </el-button>
        </div>
      </div>
      
      <div class="search-filters">
        <el-radio-group v-model="searchType">
          <el-radio-button label="title">书名</el-radio-button>
          <el-radio-button label="author">作者</el-radio-button>
          <el-radio-button label="isbn">ISBN</el-radio-button>
        </el-radio-group>
        
        <el-select 
          v-model="categoryFilter" 
          placeholder="图书分类" 
          size="small"
        >
          <el-option label="全部类别" value="" />
          <el-option label="计算机科学" value="computer" />
          <el-option label="文学" value="literature" />
          <el-option label="历史" value="history" />
          <el-option label="数学" value="mathematics" />
          <el-option label="物理学" value="physics" />
        </el-select>
        
        <el-select 
          v-model="locationFilter" 
          placeholder="馆藏位置" 
          size="small"
        >
          <el-option label="全部位置" value="" />
          <el-option label="主馆" value="main" />
          <el-option label="分馆1" value="branch1" />
          <el-option label="分馆2" value="branch2" />
          <el-option label="电子资源" value="digital" />
        </el-select>
      </div>
      
      <div class="search-results" v-if="searchResults.length > 0">
        <h4 class="result-count">搜索结果: 共找到 {{ searchResults.length }} 本图书</h4>
        
        <el-card 
          class="book-card" 
          v-for="book in searchResults" 
          :key="book.id"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <div class="book-title">{{ book.title }}</div>
              <div class="book-status">
                <el-tag 
                  :type="book.available ? 'success' : 'danger'" 
                  size="small"
                >
                  {{ book.available ? '可借阅' : '已借出' }}
                </el-tag>
              </div>
            </div>
          </template>
          
          <div class="card-content">
            <div class="book-info">
              <img 
                :src="book.cover || defaultBookCover" 
                alt="图书封面" 
                class="book-cover-small"
              />
              
              <div class="book-details">
                <div class="detail-row">
                  <span class="label">作者:</span>
                  <span class="value">{{ book.author }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">出版社:</span>
                  <span class="value">{{ book.publisher }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">出版年份:</span>
                  <span class="value">{{ book.year }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">ISBN:</span>
                  <span class="value">{{ book.isbn }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">馆藏位置:</span>
                  <span class="value">{{ getLocationName(book.location) }}</span>
                </div>
              </div>
            </div>
            
            <div class="book-actions">
              <el-button 
                type="primary" 
                size="small" 
                @click="borrowBook(book)"
                :disabled="!book.available"
              >
                {{ book.available ? '借阅' : '已借出' }}
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                @click="reserveBook(book)"
                :disabled="book.available"
              >
                预约
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                @click="viewDetails(book)"
              >
                详情
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <div class="no-results" v-else>
        <el-empty 
          description="未找到匹配的图书"
          image="empty"
        />
      </div>
    </div>

    <!-- 推荐图书 -->
    <div class="recommended-books">
      <div class="section-header">
        <h3>推荐图书</h3>
        <div class="change-recommendations">
          <el-button 
            type="text" 
            size="small" 
            @click="refreshRecommendations"
          >
            <el-icon><Refresh /></el-icon> 换一批
          </el-button>
        </div>
      </div>
      
      <div class="books-grid">
        <el-card 
          class="recommended-book-card" 
          v-for="book in recommendedBooks" 
          :key="book.id"
          shadow="hover"
        >
          <img 
            :src="book.cover || defaultBookCover" 
            alt="图书封面" 
            class="recommended-cover"
          />
          
          <div class="book-info">
            <div class="book-title">{{ book.title }}</div>
            <div class="book-author">{{ book.author }}</div>
            <div class="book-rating">
              <el-rate 
                :value="book.rating" 
                disabled 
                show-text 
                text-color="#ff9900"
                :texts="['很差', '一般', '良好', '优秀', '极佳']"
                size="small"
              />
            </div>
          </div>
          
          <div class="book-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click="borrowBook(book)"
              :disabled="!book.available"
            >
              {{ book.available ? '借阅' : '已借出' }}
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Book, History, Time, Star, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 默认图书封面
const defaultBookCover = 'https://picsum.photos/200/300?random=book'

// 响应式数据
const currentBorrows = ref([
  {
    id: 1,
    title: '数据结构与算法分析',
    author: 'Mark Allen Weiss',
    callNumber: 'TP301.6/W22',
    cover: 'https://picsum.photos/200/300?random=1',
    borrowDate: '2023-06-15',
    dueDate: '2023-07-15',
    renewTimes: 1
  },
  {
    id: 2,
    title: '计算机网络：自顶向下方法',
    author: 'Andrew S. Tanenbaum',
    callNumber: 'TP393/T23',
    cover: 'https://picsum.photos/200/300?random=2',
    borrowDate: '2023-06-20',
    dueDate: '2023-07-20',
    renewTimes: 0
  },
  {
    id: 3,
    title: '操作系统概念',
    author: 'Abraham Silberschatz',
    callNumber: 'TP316/S54',
    cover: 'https://picsum.photos/200/300?random=3',
    borrowDate: '2023-06-25',
    dueDate: '2023-07-25',
    renewTimes: 0
  }
])

const borrowHistory = ref([
  {
    id: 101,
    title: '计算机组成与设计',
    author: 'David A. Patterson',
    borrowDate: '2023-03-10',
    returnDate: '2023-04-10',
    status: 'returned'
  },
  {
    id: 102,
    title: '编译原理',
    author: 'Alfred V. Aho',
    borrowDate: '2023-02-15',
    returnDate: '2023-03-15',
    status: 'returned'
  },
  {
    id: 103,
    title: '人工智能：一种现代方法',
    author: 'Stuart Russell',
    borrowDate: '2023-01-20',
    returnDate: '2023-02-20',
    status: 'returned'
  },
  {
    id: 104,
    title: '数据库系统概念',
    author: 'Abraham Silberschatz',
    borrowDate: '2022-12-10',
    returnDate: '2023-01-15',
    status: 'overdue'
  }
])

const searchKeyword = ref('')
const searchType = ref('title')
const categoryFilter = ref('')
const locationFilter = ref('')
const searchResults = ref([])
const historyFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)

const recommendedBooks = ref([
  {
    id: 201,
    title: 'Python数据分析实战',
    author: 'Wes McKinney',
    rating: 4.5,
    cover: 'https://picsum.photos/200/300?random=4',
    available: true
  },
  {
    id: 202,
    title: '深度学习',
    author: 'Ian Goodfellow',
    rating: 4.8,
    cover: 'https://picsum.photos/200/300?random=5',
    available: false
  },
  {
    id: 203,
    title: '算法导论',
    author: 'Thomas H. Cormen',
    rating: 4.9,
    cover: 'https://picsum.photos/200/300?random=6',
    available: true
  },
  {
    id: 204,
    title: '计算机程序的构造和解释',
    author: 'Harold Abelson',
    rating: 4.7,
    cover: 'https://picsum.photos/200/300?random=7',
    available: true
  }
])

// 计算属性
const totalBorrows = computed(() => {
  return borrowHistory.value.length
})

const overdueCount = computed(() => {
  return borrowHistory.value.filter(item => item.status === 'overdue').length
})

const availableBooks = computed(() => {
  return 10 - currentBorrows.value.length
})

const filteredHistory = computed(() => {
  if (historyFilter.value === 'all') {
    return borrowHistory.value
  } else if (historyFilter.value === 'returned') {
    return borrowHistory.value.filter(item => item.status === 'returned')
  } else {
    return borrowHistory.value.filter(item => item.status === 'overdue')
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

const formatStatus = (row) => {
  if (row.status === 'returned') {
    return '已归还'
  } else if (row.status === 'overdue') {
    return '逾期归还'
  }
  return '未知'
}

const isOverdue = (dueDate) => {
  return new Date(dueDate) < new Date()
}

const getLocationName = (location) => {
  const locations = {
    'main': '主馆',
    'branch1': '分馆1',
    'branch2': '分馆2',
    'digital': '电子资源'
  }
  return locations[location] || location
}

const renewBook = (book) => {
  if (book.renewTimes >= 2) {
    ElMessage.warning('每本书最多可续借2次')
    return
  }
  
  if (isOverdue(book.dueDate)) {
    ElMessage.warning('该书已逾期，无法续借')
    return
  }
  
  ElMessageBox.confirm(
    `确定要续借《${book.title}》吗？续借后归还日期将延长30天。`,
    '确认续借',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 更新续借次数和归还日期
    const index = currentBorrows.value.findIndex(item => item.id === book.id)
    if (index !== -1) {
      const newDueDate = new Date(book.dueDate)
      newDueDate.setDate(newDueDate.getDate() + 30)
      
      currentBorrows.value[index].renewTimes += 1
      currentBorrows.value[index].dueDate = newDueDate.toISOString().split('T')[0]
      
      ElMessage.success('续借成功')
    }
  }).catch(() => {
    ElMessage.info('已取消续借')
  })
}

const renewAllBooks = () => {
  const booksToRenew = currentBorrows.value.filter(
    book => book.renewTimes < 2 && !isOverdue(book.dueDate)
  )
  
  if (booksToRenew.length === 0) {
    ElMessage.info('没有可续借的图书')
    return
  }
  
  ElMessageBox.confirm(
    `确定要续借所有${booksToRenew.length}本图书吗？`,
    '确认批量续借',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 更新所有可续借图书
    booksToRenew.forEach(book => {
      const index = currentBorrows.value.findIndex(item => item.id === book.id)
      if (index !== -1) {
        const newDueDate = new Date(book.dueDate)
        newDueDate.setDate(newDueDate.getDate() + 30)
        
        currentBorrows.value[index].renewTimes += 1
        currentBorrows.value[index].dueDate = newDueDate.toISOString().split('T')[0]
      }
    })
    
    ElMessage.success(`成功续借${booksToRenew.length}本图书`)
  }).catch(() => {
    ElMessage.info('已取消批量续借')
  })
}

const searchBooks = () => {
  // 模拟搜索图书
  ElMessage.info(`搜索关键词: ${searchKeyword.value}`)
  
  // 这里应该调用API进行实际搜索
  searchResults.value = [
    {
      id: 301,
      title: `深入理解计算机系统（第${Math.floor(Math.random() * 3) + 3}版）`,
      author: 'Randal E. Bryant',
      publisher: '机械工业出版社',
      year: '2022',
      isbn: '978-7-111-69670-0',
      location: 'main',
      available: Math.random() > 0.5,
      cover: 'https://picsum.photos/200/300?random=8'
    },
    {
      id: 302,
      title: `计算机网络与互联网（第${Math.floor(Math.random() * 3) + 5}版）`,
      author: 'Douglas E. Comer',
      publisher: '电子工业出版社',
      year: '2021',
      isbn: '978-7-121-39877-2',
      location: 'branch1',
      available: Math.random() > 0.5,
      cover: 'https://picsum.photos/200/300?random=9'
    },
    {
      id: 303,
      title: `Python编程：从入门到实践（第${Math.floor(Math.random() * 2) + 2}版）`,
      author: 'Eric Matthes',
      publisher: '人民邮电出版社',
      year: '2021',
      isbn: '978-7-115-56882-7',
      location: 'main',
      available: Math.random() > 0.5,
      cover: 'https://picsum.photos/200/300?random=10'
    }
  ]
}

const borrowBook = (book) => {
  if (!book.available) {
    ElMessage.warning('该书已被借出，无法借阅')
    return
  }
  
  if (currentBorrows.value.length >= 10) {
    ElMessage.warning('您已达到最大借阅数量（10本）')
    return
  }
  
  ElMessageBox.confirm(
    `确定要借阅《${book.title}》吗？`,
    '确认借阅',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 添加到当前借阅列表
    const borrowDate = new Date().toISOString().split('T')[0]
    const dueDate = new Date()
    dueDate.setDate(dueDate.getDate() + 30)
    
    currentBorrows.value.push({
      id: book.id,
      title: book.title,
      author: book.author,
      callNumber: `TP/${Math.floor(Math.random() * 1000)}`,
      cover: book.cover,
      borrowDate: borrowDate,
      dueDate: dueDate.toISOString().split('T')[0],
      renewTimes: 0
    })
    
    // 更新搜索结果中的图书状态
    const index = searchResults.value.findIndex(item => item.id === book.id)
    if (index !== -1) {
      searchResults.value[index].available = false
    }
    
    ElMessage.success('借阅成功，请在借阅期限内归还')
  }).catch(() => {
    ElMessage.info('已取消借阅')
  })
}

const reserveBook = (book) => {
  ElMessageBox.confirm(
    `确定要预约《${book.title}》吗？该书目前已被借出，预约成功后将在图书归还时通知您。`,
    '确认预约',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    ElMessage.success('预约成功，我们将在该书归还后通知您')
  }).catch(() => {
    ElMessage.info('已取消预约')
  })
}

const viewDetails = (book) => {
  ElMessage.info(`查看图书详情: ${book.title}`)
  // 实际应用中这里可以跳转到图书详情页
}

const refreshRecommendations = () => {
  // 模拟刷新推荐图书
  recommendedBooks.value = recommendedBooks.value.map(book => ({
    ...book,
    cover: `https://picsum.photos/200/300?random=${Math.floor(Math.random() * 100)}`
  }))
  
  ElMessage.info('推荐图书已更新')
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
.library-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

// 借阅概览卡片样式
.borrow-overview {
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

// 当前借阅列表样式
.current-borrows, .borrow-history, .book-search, .recommended-books {
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
  }
  
  .borrow-table, .history-table {
    width: 100%;
    margin-bottom: 16px;
  }
  
  .book-cover {
    width: 60px;
    height: 80px;
    object-fit: cover;
    border-radius: 4px;
  }
  
  .pagination {
    text-align: right;
  }
}

// 图书搜索样式
.book-search {
  .search-header {
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
    
    .search-box {
      display: flex;
      gap: 12px;
      
      .search-input {
        width: 300px;
      }
    }
  }
  
  .search-filters {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;
    align-items: center;
  }
  
  .result-count {
    margin-bottom: 16px;
    font-size: 14px;
    color: #666;
  }
  
  .book-card {
    margin-bottom: 16px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .book-title {
        font-size: 16px;
        font-weight: 500;
        color: #333;
      }
    }
    
    .card-content {
      .book-info {
        display: flex;
        gap: 16px;
        margin-bottom: 16px;
        
        .book-cover-small {
          width: 80px;
          height: 120px;
          object-fit: cover;
          border-radius: 4px;
        }
        
        .book-details {
          flex: 1;
          
          .detail-row {
            display: flex;
            margin-bottom: 8px;
            
            .label {
              width: 60px;
              color: #909399;
            }
            
            .value {
              color: #333;
            }
          }
        }
      }
      
      .book-actions {
        display: flex;
        gap: 8px;
      }
    }
  }
}

// 推荐图书样式
.recommended-books {
  .books-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .recommended-book-card {
    padding: 16px;
    text-align: center;
    
    .recommended-cover {
      width: 120px;
      height: 180px;
      object-fit: cover;
      border-radius: 4px;
      margin-bottom: 12px;
    }
    
    .book-info {
      margin-bottom: 12px;
      
      .book-title {
        font-size: 16px;
        font-weight: 500;
        color: #333;
        margin-bottom: 4px;
      }
      
      .book-author {
        font-size: 14px;
        color: #666;
        margin-bottom: 8px;
      }
    }
  }
}
</style>