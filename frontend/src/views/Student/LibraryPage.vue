<template>
  <div class="library-page" :class="{ 'dark': isDarkMode }">
    <!-- 借阅概览卡片 -->
    <div class="borrow-overview modern-card mb-6">
      <div class="overview-header">
        <h2>借阅概览</h2>
        <div class="status-indicator">
          <el-tag type="success" size="small" effect="dark">
            <el-icon>
              <SuccessFilled />
            </el-icon>
            借阅状态良好
          </el-tag>
        </div>
      </div>

      <div class="stats-container">
        <div class="stats-card" v-for="stat in stats" :key="stat.label">
          <div class="card-icon" :style="{ backgroundColor: stat.bgColor }">
            <el-icon :size="24">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <div class="card-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div class="trend-indicator" :class="stat.trend">
            <el-icon v-if="stat.trend === 'up'">
              <Top />
            </el-icon>
            <el-icon v-else>
              <Bottom />
            </el-icon>
            <span>{{ stat.change }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 当前借阅列表 -->
    <div class="current-borrows modern-card mb-6">
      <div class="section-header">
        <h3>当前借阅</h3>
        <div class="actions">
          <el-button type="primary" size="small" @click="renewAllBooks" :disabled="!hasRenewableBooks">
            <el-icon>
              <Refresh />
            </el-icon>
            一键续借
          </el-button>
          <el-button type="info" size="small" @click="exportBorrowList">
            <el-icon>
              <Download />
            </el-icon>
            导出列表
          </el-button>
        </div>
      </div>

      <el-table :data="paginatedCurrentBorrows" border stripe class="borrow-table" style="width: 100%"
        v-loading="loading">
        <el-table-column prop="cover" label="封面" width="100" align="center">
          <template #default="scope">
            <el-image :src="scope.row.cover || defaultBookCover"
              :preview-src-list="[scope.row.cover || defaultBookCover]" fit="cover" class="book-cover"
              hide-on-click-modal>
              <template #error>
                <div class="image-error">
                  <el-icon>
                    <Picture />
                  </el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="书名" min-width="180" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="callNumber" label="索书号" width="120" />
        <el-table-column prop="borrowDate" label="借阅日期" width="120" :formatter="formatDate" sortable />
        <el-table-column prop="dueDate" label="应还日期" width="120" :formatter="formatDate" sortable>
          <template #default="scope">
            <span :class="{ 'text-danger': isOverdue(scope.row.dueDate) }">
              {{ formatDate(scope.row.dueDate) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="renewTimes" label="续借次数" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.renewTimes >= 2 ? 'danger' : 'info'" size="small">
              {{ scope.row.renewTimes }}/2
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getBookStatusType(scope.row)" size="small" effect="light">
              {{ getBookStatusText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="renewBook(scope.row)" :disabled="!canRenew(scope.row)" plain>
              续借
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 20]"
          :total="currentBorrows.length" layout="total, sizes, prev, pager, next, jumper" background />
      </div>
    </div>

    <!-- 借阅历史 -->
    <div class="borrow-history modern-card mb-6">
      <div class="section-header">
        <h3>借阅历史</h3>
        <div class="history-filter">
          <el-select v-model="historyFilter" placeholder="筛选类型" size="small">
            <el-option label="全部" value="all" />
            <el-option label="已归还" value="returned" />
            <el-option label="逾期" value="overdue" />
          </el-select>
        </div>
      </div>

      <el-table :data="filteredHistory" border stripe class="history-table">
        <el-table-column prop="title" label="书名" min-width="200" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="borrowDate" label="借阅日期" width="120" :formatter="formatDate" />
        <el-table-column prop="returnDate" label="归还日期" width="120" :formatter="formatDate" />
        <el-table-column prop="status" label="状态" width="100" :formatter="formatStatus" />
      </el-table>

      <div class="pagination" v-if="filteredHistory.length > 0">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[5, 10, 20]" :page-size="pageSize" :total="filteredHistory.length"
          layout="total, sizes, prev, pager, next" small />
      </div>
    </div>

    <!-- 图书搜索 -->
    <div class="book-search modern-card mb-6">
      <div class="search-header">
        <h3>图书搜索</h3>
        <div class="search-box">
          <el-input v-model="searchKeyword" placeholder="输入书名、作者或ISBN" clearable @clear="clearSearch"
            @keyup.enter="searchBooks">
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
            <template #append>
              <el-button @click="searchBooks">
                <el-icon>
                  <Search />
                </el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>

      <div class="search-filters">
        <div class="filter-group">
          <label>搜索类型</label>
          <el-radio-group v-model="searchType" size="small">
            <el-radio-button label="title">书名</el-radio-button>
            <el-radio-button label="author">作者</el-radio-button>
            <el-radio-button label="isbn">ISBN</el-radio-button>
          </el-radio-group>
        </div>

        <div class="filter-group">
          <label>图书分类</label>
          <el-select v-model="categoryFilter" placeholder="全部类别" size="small" clearable>
            <el-option v-for="category in bookCategories" :key="category.value" :label="category.label"
              :value="category.value" />
          </el-select>
        </div>

        <div class="filter-group">
          <label>馆藏位置</label>
          <el-select v-model="locationFilter" placeholder="全部位置" size="small" clearable>
            <el-option v-for="location in libraryLocations" :key="location.value" :label="location.label"
              :value="location.value" />
          </el-select>
        </div>
      </div>

      <div class="search-results" v-if="searchResults.length > 0">
        <div class="result-header">
          <div class="result-count">
            共找到 <span class="highlight">{{ searchResults.length }}</span> 本图书
          </div>
          <div class="result-sort">
            <el-select v-model="sortOption" placeholder="排序方式" size="small">
              <el-option label="相关度" value="relevance" />
              <el-option label="出版日期(新→旧)" value="date-desc" />
              <el-option label="出版日期(旧→新)" value="date-asc" />
              <el-option label="借阅量(高→低)" value="popularity" />
            </el-select>
          </div>
        </div>

        <div class="result-grid">
          <el-card v-for="book in sortedSearchResults" :key="book.id" class="book-card" shadow="hover">
            <div class="card-content">
              <div class="book-cover">
                <el-image :src="book.cover || defaultBookCover" :preview-src-list="[book.cover || defaultBookCover]"
                  fit="cover" class="cover-image" hide-on-click-modal>
                  <template #error>
                    <div class="image-error">
                      <el-icon>
                        <Picture />
                      </el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <div class="book-details">
                <h4 class="book-title">{{ book.title }}</h4>
                <div class="book-author">{{ book.author }}</div>
                <div class="book-meta">
                  <div class="meta-item">
                    <el-icon>
                      <Collection />
                    </el-icon>
                    {{ book.publisher }} ({{ book.year }})
                  </div>
                  <div class="meta-item">
                    <el-icon>
                      <Location />
                    </el-icon>
                    {{ getLocationName(book.location) }}
                  </div>
                  <div class="meta-item">
                    <el-icon>
                      <Document />
                    </el-icon>
                    {{ book.callNumber || '--' }}
                  </div>
                </div>
                <div class="book-actions">
                  <el-button type="primary" size="small" @click.stop="borrowBook(book)" :disabled="!book.available">
                    {{ book.available ? '借阅' : '已借出' }}
                  </el-button>
                  <el-button type="info" size="small" @click.stop="viewBookDetails(book)" plain>
                    详情
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <div class="no-results" v-else>
        <el-empty description="未找到匹配的图书" image="empty" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  Search, Refresh, Download, Picture, Collection,
  Location, Document, SuccessFilled, Top, Bottom
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 暗色模式
const isDarkMode = ref(false)

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

// 统计数据
const stats = ref([
  {
    label: '当前借阅',
    value: 3,
    icon: 'Book',
    bgColor: '#f0f9ff',
    trend: 'up',
    change: 5
  },
  {
    label: '累计借阅',
    value: 42,
    icon: 'Histogram',
    bgColor: '#f0f7ff',
    trend: 'up',
    change: 12
  },
  {
    label: '逾期数量',
    value: 0,
    icon: 'Clock',
    bgColor: '#fff0f0',
    trend: 'down',
    change: 20
  },
  {
    label: '可借数量',
    value: 7,
    icon: 'Star',
    bgColor: '#f0fff4',
    trend: 'up',
    change: 3
  }
])

// 搜索相关数据
const searchKeyword = ref('')
const searchType = ref('title')
const categoryFilter = ref('')
const locationFilter = ref('')
const searchResults = ref([])
const sortOption = ref('relevance')

// 图书分类和位置选项
const bookCategories = ref([
  { value: 'computer', label: '计算机科学' },
  { value: 'literature', label: '文学' },
  { value: 'history', label: '历史' },
  { value: 'mathematics', label: '数学' },
  { value: 'physics', label: '物理学' },
  { value: 'economics', label: '经济学' },
  { value: 'art', label: '艺术' }
])

const libraryLocations = ref([
  { value: 'main', label: '主馆' },
  { value: 'branch1', label: '分馆1' },
  { value: 'branch2', label: '分馆2' },
  { value: 'digital', label: '电子资源' }
])

// 推荐图书
const recommendedBooks = ref([
  {
    id: 201,
    title: 'Python数据分析实战',
    author: 'Wes McKinney',
    rating: 4.5,
    cover: 'https://picsum.photos/200/300?random=4',
    available: true,
    category: 'computer'
  },
  {
    id: 202,
    title: '深度学习',
    author: 'Ian Goodfellow',
    rating: 4.8,
    cover: 'https://picsum.photos/200/300?random=5',
    available: false,
    category: 'computer'
  },
  {
    id: 203,
    title: '算法导论',
    author: 'Thomas H. Cormen',
    rating: 4.9,
    cover: 'https://picsum.photos/200/300?random=6',
    available: true,
    category: 'computer'
  }
])

// 添加借阅历史数据
const borrowHistory = ref([
  {
    id: 101,
    title: '数据结构与算法分析',
    author: 'Mark Allen Weiss',
    borrowDate: '2023-05-10',
    returnDate: '2023-06-10',
    status: 'returned'
  },
  {
    id: 102,
    title: '计算机网络：自顶向下方法',
    author: 'Andrew S. Tanenbaum',
    borrowDate: '2023-04-15',
    returnDate: '2023-05-15',
    status: 'returned'
  },
  {
    id: 103,
    title: '操作系统概念',
    author: 'Abraham Silberschatz',
    borrowDate: '2023-03-20',
    returnDate: '',
    status: 'overdue'
  }
])

// 历史记录筛选
const historyFilter = ref('all')

// 定义 filteredHistory 计算属性
const filteredHistory = computed(() => {
  if (historyFilter.value === 'all') {
    return borrowHistory.value
  } else if (historyFilter.value === 'returned') {
    return borrowHistory.value.filter(item => item.status === 'returned')
  } else if (historyFilter.value === 'overdue') {
    return borrowHistory.value.filter(item => item.status === 'overdue')
  }
  return []
})

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 图书详情
const selectedBook = ref(null)
const bookDetailVisible = ref(false)

// 计算属性
const paginatedCurrentBorrows = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return currentBorrows.value.slice(start, end)
})

const hasRenewableBooks = computed(() => {
  return currentBorrows.value.some(book => canRenew(book))
})

const sortedSearchResults = computed(() => {
  const results = [...searchResults.value]
  switch (sortOption.value) {
    case 'date-desc':
      return results.sort((a, b) => new Date(b.year) - new Date(a.year))
    case 'date-asc':
      return results.sort((a, b) => new Date(a.year) - new Date(b.year))
    case 'popularity':
      return results.sort((a, b) => (b.popularity || 0) - (a.popularity || 0))
    default:
      return results
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

const isOverdue = (dueDate) => {
  return new Date(dueDate) < new Date()
}

const canRenew = (book) => {
  return book.renewTimes < 2 && !isOverdue(book.dueDate)
}

const getBookStatusType = (book) => {
  if (isOverdue(book.dueDate)) return 'danger'
  if (book.renewTimes >= 2) return 'warning'
  return 'success'
}

const getBookStatusText = (book) => {
  if (isOverdue(book.dueDate)) return '已逾期'
  if (book.renewTimes >= 2) return '不可续借'
  return '可续借'
}

const getLocationName = (location) => {
  const loc = libraryLocations.value.find(item => item.value === location)
  return loc ? loc.label : location
}

const renewBook = (book) => {
  if (!canRenew(book)) {
    ElMessage.warning('该书无法续借')
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
  const booksToRenew = currentBorrows.value.filter(canRenew)

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

const exportBorrowList = () => {
  ElMessage.success('导出成功')
  // 实际应用中这里应该调用导出API
}

const searchBooks = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  loading.value = true
  // 模拟API调用延迟
  setTimeout(() => {
    // 模拟搜索图书
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
        cover: 'https://picsum.photos/200/300?random=8',
        callNumber: `TP/${Math.floor(Math.random() * 1000)}`,
        popularity: Math.floor(Math.random() * 100)
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
        cover: 'https://picsum.photos/200/300?random=9',
        callNumber: `TP/${Math.floor(Math.random() * 1000)}`,
        popularity: Math.floor(Math.random() * 100)
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
        cover: 'https://picsum.photos/200/300?random=10',
        callNumber: `TP/${Math.floor(Math.random() * 1000)}`,
        popularity: Math.floor(Math.random() * 100)
      }
    ]
    loading.value = false
  }, 800)
}

const clearSearch = () => {
  searchKeyword.value = ''
  searchResults.value = []
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
      callNumber: book.callNumber || `TP/${Math.floor(Math.random() * 1000)}`,
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

    // 更新推荐图书中的状态
    const recIndex = recommendedBooks.value.findIndex(item => item.id === book.id)
    if (recIndex !== -1) {
      recommendedBooks.value[recIndex].available = false
    }

    ElMessage.success('借阅成功，请在借阅期限内归还')
  }).catch(() => {
    ElMessage.info('已取消借阅')
  })
}

const viewBookDetails = (book) => {
  // 模拟获取图书详情
  selectedBook.value = {
    ...book,
    description: '这是一本关于' + book.title + '的书籍，详细介绍了相关知识和应用。',
    pages: Math.floor(Math.random() * 500) + 200,
    language: '中文',
    borrowRecords: [
      {
        user: '张同学',
        borrowDate: '2023-05-10',
        returnDate: '2023-06-10',
        status: 'returned'
      },
      {
        user: '李同学',
        borrowDate: '2023-06-15',
        returnDate: '',
        status: 'borrowed'
      }
    ],
    reviews: [
      {
        id: 1,
        user: '王同学',
        rating: 5,
        date: '2023-04-15',
        content: '这本书非常棒，讲解清晰，适合初学者。'
      },
      {
        id: 2,
        user: '赵同学',
        rating: 4,
        date: '2023-03-20',
        content: '内容全面，但有些章节可以更详细些。'
      }
    ],
    ratingCount: 24
  }
  bookDetailVisible.value = true
}

const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 初始化数据
onMounted(() => {
  // 可以在这里添加初始化逻辑
})
</script>

<style scoped lang="scss">
.library-page {
  padding: 20px;
  min-height: calc(100vh - 60px);
  transition: background-color 0.3s ease;
}

// 现代化卡片样式
.modern-card {
  position: relative;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  overflow: hidden;
  z-index: 1;

  // 浅色模式
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

  // 深色模式样式
  .dark & {
    background: #2a2a2a;
    border: 1px solid rgba(255, 255, 255, 0.05);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }

  // 卡片悬停效果
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }

  // 卡片头部
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2,
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
}

// 借阅概览卡片
.borrow-overview {
  .overview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }

  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 16px;

    .stats-card {
      background: rgba(0, 0, 0, 0.02);
      border-radius: 10px;
      padding: 16px;
      display: flex;
      align-items: center;
      transition: all 0.3s ease;
      position: relative;

      .dark & {
        background: rgba(255, 255, 255, 0.05);
      }

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      }

      .card-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        color: var(--el-color-primary);
        background-color: var(--el-color-primary-light-9);

        .dark & {
          background-color: rgba(64, 158, 255, 0.2);
        }
      }

      .card-content {
        .stat-value {
          font-size: 22px;
          font-weight: 700;
          color: var(--el-text-color-primary);
          line-height: 1;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: var(--el-text-color-secondary);
        }
      }

      .trend-indicator {
        position: absolute;
        top: 12px;
        right: 12px;
        font-size: 12px;
        display: flex;
        align-items: center;

        &.up {
          color: var(--el-color-success);
        }

        &.down {
          color: var(--el-color-danger);
        }

        .el-icon {
          margin-right: 2px;
        }
      }
    }
  }
}

// 当前借阅列表
.current-borrows {
  .actions {
    display: flex;
    gap: 10px;
  }

  .borrow-table {
    margin-bottom: 20px;

    .book-cover {
      width: 60px;
      height: 80px;
      object-fit: cover;
      border-radius: 4px;
      display: block;
      margin: 0 auto;
    }

    .image-error {
      width: 60px;
      height: 80px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f5f5;
      border-radius: 4px;
      color: #999;
    }

    .text-danger {
      color: var(--el-color-danger);
      font-weight: 500;
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
  }
}

// 借阅历史
.borrow-history {
  .history-table {
    margin-bottom: 20px;
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
  }
}

// 图书搜索
.book-search {
  .search-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .search-box {
      flex: 1;
      max-width: 400px;
      display: flex;
      gap: 10px;
    }
  }

  .search-filters {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-bottom: 16px;

    .filter-group {
      display: flex;
      flex-direction: column;
      gap: 8px;

      label {
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }

      .el-radio-group,
      .el-select {
        width: 100%;
      }
    }
  }

  .result-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .result-count {
      font-size: 14px;

      .highlight {
        color: var(--el-color-primary);
        font-weight: 600;
      }
    }
  }

  .result-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 16px;
  }

  .book-card {
    .card-content {
      display: flex;
      gap: 16px;

      .book-cover {
        flex-shrink: 0;
        width: 100px;
        height: 140px;

        .cover-image {
          width: 100%;
          height: 100%;
          border-radius: 4px;
          object-fit: cover;
        }

        .image-error {
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #f5f5f5;
          border-radius: 4px;
          color: #999;
        }
      }

      .book-details {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .book-title {
          margin: 0 0 8px 0;
          font-size: 16px;
          font-weight: 600;
        }

        .book-author {
          font-size: 14px;
          color: var(--el-text-color-secondary);
          margin-bottom: 12px;
        }

        .book-meta {
          font-size: 13px;
          color: var(--el-text-color-secondary);
          margin-bottom: 12px;

          .meta-item {
            margin-bottom: 6px;
            display: flex;
            align-items: center;

            .el-icon {
              margin-right: 4px;
              font-size: 14px;
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
}

// 推荐图书
.recommended-books {
  .books-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .book-item {
    display: flex;
    gap: 12px;
    cursor: pointer;
    padding: 8px;
    border-radius: 8px;
    transition: all 0.2s;

    &:hover {
      background: rgba(0, 0, 0, 0.03);

      .dark & {
        background: rgba(255, 255, 255, 0.05);
      }
    }

    .book-cover {
      position: relative;
      flex-shrink: 0;
      width: 60px;
      height: 80px;

      .cover-image {
        width: 100%;
        height: 100%;
        border-radius: 4px;
        object-fit: cover;
      }

      .image-error {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f5f5;
        border-radius: 4px;
        color: #999;
      }

      .book-status {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: rgba(0, 0, 0, 0.7);
        color: white;
        font-size: 12px;
        text-align: center;
        padding: 2px 0;
        border-bottom-left-radius: 4px;
        border-bottom-right-radius: 4px;

        &.available {
          background: rgba(103, 194, 58, 0.8);
        }
      }
    }

    .book-info {
      flex: 1;
      overflow: hidden;

      .book-title {
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 4px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .book-author {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        margin-bottom: 6px;
      }

      .book-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .el-rate {
          --el-rate-icon-size: 14px;
        }

        .el-tag {
          height: 22px;
          padding: 0 6px;
        }
      }
    }
  }
}

// 图书详情弹窗
.book-detail {
  .detail-header {
    display: flex;
    gap: 24px;
    margin-bottom: 24px;

    @media (max-width: 768px) {
      flex-direction: column;
    }
  }

  .book-cover-large {
    flex-shrink: 0;
    width: 200px;
    height: 280px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

    .cover-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .image-error-large {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f5f5;
      color: #999;
      font-size: 48px;
    }
  }

  .book-info {
    flex: 1;

    .book-title {
      margin: 0 0 8px 0;
      font-size: 24px;
      font-weight: 600;
    }

    .book-author {
      font-size: 16px;
      color: var(--el-text-color-secondary);
      margin-bottom: 16px;
    }

    .book-rating {
      display: flex;
      align-items: center;
      margin-bottom: 16px;

      .el-rate {
        margin-right: 12px;
      }

      .rating-count {
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }
    }

    .book-status {
      margin-bottom: 24px;
    }

    .book-actions {
      display: flex;
      gap: 12px;
    }
  }

  .detail-tabs {
    margin-top: 24px;

    .book-description {
      margin-bottom: 24px;

      h4 {
        margin: 0 0 12px 0;
        font-size: 16px;
        font-weight: 600;
      }

      p {
        margin: 0;
        line-height: 1.6;
        color: var(--el-text-color-primary);
      }
    }

    .book-meta-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 16px;
      margin-bottom: 24px;

      .meta-item {
        display: flex;
        align-items: center;

        .label {
          font-weight: 500;
          margin-right: 8px;
          color: var(--el-text-color-secondary);
        }

        .value {
          color: var(--el-text-color-primary);
        }
      }
    }

    .book-reviews {
      .review-item {
        padding: 16px 0;
        border-bottom: 1px solid var(--el-border-color);

        &:last-child {
          border-bottom: none;
        }

        .review-header {
          display: flex;
          align-items: center;
          margin-bottom: 8px;

          .reviewer {
            font-weight: 500;
            margin-right: 12px;
          }

          .review-rating {
            margin-right: 12px;
          }

          .review-date {
            font-size: 12px;
            color: var(--el-text-color-secondary);
          }
        }

        .review-content {
          line-height: 1.6;
          color: var(--el-text-color-primary);
        }
      }

      .no-reviews {
        text-align: center;
        padding: 40px 0;
        color: var(--el-text-color-secondary);
      }
    }
  }
}

// 间距工具类
.mb-6 {
  margin-bottom: 24px;
}

// 响应式调整
@media (max-width: 768px) {
  .stats-container {
    grid-template-columns: 1fr 1fr !important;
  }

  .search-results .result-grid {
    grid-template-columns: 1fr;
  }

  .book-detail .detail-header {
    flex-direction: column;
  }

  .book-detail .book-cover-large {
    width: 100%;
    margin-bottom: 16px;
  }
}
</style>