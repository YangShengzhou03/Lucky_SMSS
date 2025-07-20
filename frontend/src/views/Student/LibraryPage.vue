<template>
  <div class="library-container">
    <!-- 图书馆检索卡片 -->
    <div class="modern-card">
      <div class="card-header">
        <h3><el-icon>
            <Search />
          </el-icon> 图书馆资源检索</h3>
        <el-tag size="small" type="info" v-if="searchResults.length > 0">
          {{ searchResults.length }} 条结果
        </el-tag>
      </div>

      <div class="search-bar">
        <input type="text" v-model="searchQuery" placeholder="输入书名、作者或ISBN..." @keyup.enter="performSearch" />
        <button @click="performSearch">
          <el-icon>
            <Search />
          </el-icon>
          搜索
        </button>
      </div>

      <div class="search-filters">
        <div class="filter-item">
          <label>类型:</label>
          <el-select v-model="selectedType" size="small" style="width: 120px">
            <el-option value="" label="全部"></el-option>
            <el-option value="book" label="图书"></el-option>
            <el-option value="journal" label="期刊"></el-option>
            <el-option value="thesis" label="论文"></el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <label>排序:</label>
          <el-select v-model="selectedSort" size="small" style="width: 120px">
            <el-option value="relevance" label="相关性"></el-option>
            <el-option value="newest" label="最新上架"></el-option>
            <el-option value="popular" label="最受欢迎"></el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <label>馆藏地:</label>
          <el-select v-model="selectedLocation" size="small" style="width: 120px">
            <el-option value="" label="全部"></el-option>
            <el-option value="main" label="主馆"></el-option>
            <el-option value="science" label="科技分馆"></el-option>
            <el-option value="arts" label="人文分馆"></el-option>
          </el-select>
        </div>
      </div>

      <!-- 搜索结果展示 -->
      <div class="search-results" v-if="searchResults.length > 0">
        <div class="result-list">
          <div class="result-item" v-for="item in searchResults" :key="item.id" @click="viewBookDetail(item)">
            <img :src="item.cover || defaultBookCover" alt="封面" class="result-cover" />
            <div class="result-info">
              <h4>{{ item.title }}</h4>
              <p class="result-meta">{{ item.author }} · {{ item.publisher }} · {{ item.year }}</p>
              <p class="result-location">
                <el-icon>
                  <Location />
                </el-icon>
                {{ item.location }} · {{ item.callNumber }}
              </p>
              <div class="result-status" :class="{
                available: item.available,
                limited: item.availableCopies > 0 && item.availableCopies < 3
              }">
                <span v-if="item.available && item.availableCopies > 2">可借阅 ({{ item.availableCopies }}本)</span>
                <span v-else-if="item.available && item.availableCopies <= 2">仅剩 {{ item.availableCopies }}本</span>
                <span v-else>已借出 (预计归还: {{ item.returnDate || '--' }})</span>
              </div>
            </div>
            <el-icon class="arrow">
              <ArrowRight />
            </el-icon>
          </div>
        </div>

        <!-- 分页控件 -->
        <div class="pagination" v-if="totalResults > pageSize">
          <el-pagination small layout="prev, pager, next" :total="totalResults" :page-size="pageSize"
            :current-page="currentPage" @current-change="handlePageChange" />
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else-if="searchQuery && searchResults.length === 0">
        <el-icon>
          <DocumentDelete />
        </el-icon>
        <p>没有找到匹配的图书</p>
        <el-button type="primary" size="small" @click="resetSearch">重新搜索</el-button>
      </div>

      <!-- 热门推荐 -->
      <div class="recommendations" v-else>
        <h4>热门推荐</h4>
        <div class="recommend-list">
          <div class="recommend-item" v-for="book in recommendedBooks" :key="book.id" @click="quickSearch(book.title)">
            <img :src="book.cover || defaultBookCover" alt="封面" />
            <p>{{ book.title }}</p>
            <div class="recommend-badge" v-if="book.isNew">新书</div>
          </div>
        </div>

        <div class="history-search" v-if="searchHistory.length > 0">
          <h4>搜索历史</h4>
          <div class="history-tags">
            <el-tag v-for="(query, index) in searchHistory" :key="index" size="small" closable
              @close="removeSearchHistory(index)" @click="quickSearch(query)">
              {{ query }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 原有借阅概览 -->
    <div class="summary-cards">
      <div class="summary-card" v-for="stat in stats" :key="stat.label">
        <div class="card-icon" :style="{ color: stat.color }">
          <component :is="stat.icon" />
        </div>
        <div class="card-content">
          <h3>{{ stat.value }}</h3>
          <p>{{ stat.label }}</p>
        </div>
      </div>
    </div>

    <!-- 当前借阅列表 -->
    <div class="modern-card">
      <h2>当前借阅</h2>
      <div class="book-list">
        <div class="book-card" v-for="book in currentBorrows" :key="book.id">
          <img :src="book.cover || defaultBookCover" alt="封面" class="book-cover" />
          <div class="book-info">
            <h3>{{ book.title }}</h3>
            <p class="author">{{ book.author }}</p>
            <div class="due-date" :class="{ overdue: isOverdue(book.dueDate) }">
              应还日期: {{ formatDate(book.dueDate) }}
              <span v-if="isOverdue(book.dueDate)" class="overdue-text">(已逾期)</span>
            </div>
            <div class="renew-info" v-if="book.renewTimes > 0">
              已续借 {{ book.renewTimes }} 次
            </div>
            <button class="renew-btn" @click="renewBook(book)" :disabled="!canRenew(book)">
              续借
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 图书详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="selectedBook?.title || '图书详情'" width="60%" top="5vh">
      <div class="book-detail" v-if="selectedBook">
        <div class="detail-main">
          <img :src="selectedBook.cover || defaultBookCover" alt="封面" class="detail-cover" />
          <div class="detail-info">
            <h3>{{ selectedBook.title }}</h3>
            <p><span class="label">作者:</span> {{ selectedBook.author }}</p>
            <p><span class="label">出版社:</span> {{ selectedBook.publisher }}</p>
            <p><span class="label">出版年:</span> {{ selectedBook.year }}</p>
            <p><span class="label">ISBN:</span> {{ selectedBook.isbn || '--' }}</p>
            <p><span class="label">页数:</span> {{ selectedBook.pages || '--' }}</p>
            <p><span class="label">索书号:</span> {{ selectedBook.callNumber }}</p>
            <p><span class="label">馆藏地:</span> {{ selectedBook.location }}</p>
            <div class="availability">
              <span class="label">状态:</span>
              <span :class="{
                'status-available': selectedBook.available,
                'status-unavailable': !selectedBook.available
              }">
                {{ selectedBook.available ? '可借阅' : '已借出' }}
                <span v-if="selectedBook.available">({{ selectedBook.availableCopies }}本可借)</span>
                <span v-else>(预计归还: {{ selectedBook.returnDate || '--' }})</span>
              </span>
            </div>
          </div>
        </div>
        <div class="detail-description">
          <h4>内容简介</h4>
          <p>{{ selectedBook.description || '暂无内容简介' }}</p>
        </div>
        <div class="detail-actions">
          <el-button type="primary" :disabled="!selectedBook.available" @click="reserveBook(selectedBook)">
            预约借书
          </el-button>
          <el-button @click="detailVisible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  Search, Location, ArrowRight, DocumentDelete,
  Book, Clock, Check, List
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 默认图书封面
const defaultBookCover = 'https://picsum.photos/200/300?random=book'

// 搜索相关数据
const searchQuery = ref('')
const selectedType = ref('')
const selectedSort = ref('relevance')
const selectedLocation = ref('')
const searchResults = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const totalResults = ref(0)
const searchHistory = ref([])

// 图书详情相关
const detailVisible = ref(false)
const selectedBook = ref(null)

// 热门推荐书籍
const recommendedBooks = ref([
  {
    id: 101,
    title: '深入理解计算机系统',
    cover: 'https://picsum.photos/200/300?random=101',
    isNew: true
  },
  {
    id: 102,
    title: '代码整洁之道',
    cover: 'https://picsum.photos/200/300?random=102',
    isNew: false
  },
  {
    id: 103,
    title: '设计模式',
    cover: 'https://picsum.photos/200/300?random=103',
    isNew: false
  },
  {
    id: 104,
    title: '算法导论',
    cover: 'https://picsum.photos/200/300?random=104',
    isNew: true
  },
  {
    id: 105,
    title: '计算机程序的构造和解释',
    cover: 'https://picsum.photos/200/300?random=105',
    isNew: false
  }
])

// 模拟搜索功能
const performSearch = () => {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  // 添加到搜索历史
  if (!searchHistory.value.includes(searchQuery.value)) {
    searchHistory.value.unshift(searchQuery.value)
    if (searchHistory.value.length > 5) {
      searchHistory.value.pop()
    }
    localStorage.setItem('librarySearchHistory', JSON.stringify(searchHistory.value))
  }

  currentPage.value = 1

  // 模拟API请求延迟
  setTimeout(() => {
    // 模拟API返回数据
    const allResults = [
      {
        id: 101,
        title: '深入理解计算机系统',
        author: 'Randal E.Bryant',
        publisher: '机械工业出版社',
        year: '2016',
        cover: 'https://picsum.photos/200/300?random=101',
        location: '三楼自然科学区',
        callNumber: 'TP311.5/B915',
        available: true,
        availableCopies: 5,
        isbn: '9787111544937',
        pages: '737',
        description: '本书从程序员的视角详细阐述计算机系统的本质概念，并展示这些概念如何实实在在地影响应用程序的正确性、性能和实用性。',
        type: 'book',
        library: 'main'
      },
      {
        id: 102,
        title: '代码整洁之道',
        author: 'Robert C. Martin',
        publisher: '人民邮电出版社',
        year: '2020',
        cover: 'https://picsum.photos/200/300?random=102',
        location: '二楼计算机科学区',
        callNumber: 'TP311.5/M379',
        available: false,
        availableCopies: 0,
        returnDate: '2023-07-30',
        isbn: '9787115279460',
        pages: '388',
        description: '本书讲述了一系列行之有效的整洁代码操作实践，帮助读者写出高质量的代码。',
        type: 'book',
        library: 'main'
      },
      {
        id: 103,
        title: '设计模式：可复用面向对象软件的基础',
        author: 'Erich Gamma等',
        publisher: '机械工业出版社',
        year: '2019',
        cover: 'https://picsum.photos/200/300?random=103',
        location: '二楼计算机科学区',
        callNumber: 'TP311.5/G191',
        available: true,
        availableCopies: 2,
        isbn: '9787111618048',
        pages: '395',
        description: '本书结合设计实例从面向对象的设计中精选出23个设计模式，总结了面向对象设计中最有价值的经验。',
        type: 'book',
        library: 'science'
      },
      {
        id: 104,
        title: '算法导论',
        author: 'Thomas H. Cormen',
        publisher: 'MIT Press',
        year: '2009',
        cover: 'https://picsum.photos/200/300?random=104',
        location: '三楼自然科学区',
        callNumber: 'TP311.5/C813',
        available: true,
        availableCopies: 3,
        isbn: '9780262033848',
        pages: '1292',
        description: '本书全面论述了算法的内容，是一本经典的算法教材。',
        type: 'book',
        library: 'science'
      },
      {
        id: 105,
        title: '计算机程序的构造和解释',
        author: 'Harold Abelson',
        publisher: 'MIT Press',
        year: '1996',
        cover: 'https://picsum.photos/200/300?random=105',
        location: '三楼自然科学区',
        callNumber: 'TP311.5/A139',
        available: true,
        availableCopies: 1,
        isbn: '9780262011532',
        pages: '657',
        description: '本书强调通过抽象来构建系统的方法，特别专注于不同抽象层次上系统的创建和使用。',
        type: 'book',
        library: 'main'
      },
      {
        id: 106,
        title: '人工智能：现代方法',
        author: 'Stuart Russell',
        publisher: 'Pearson',
        year: '2020',
        cover: 'https://picsum.photos/200/300?random=106',
        location: '四楼科技分馆',
        callNumber: 'TP18/R961',
        available: false,
        availableCopies: 0,
        returnDate: '2023-08-15',
        isbn: '9780134610993',
        pages: '1136',
        description: '本书全面系统地介绍了人工智能的理论和实践，阐述了人工智能领域的核心内容。',
        type: 'book',
        library: 'science'
      },
      {
        id: 107,
        title: '计算机网络：自顶向下方法',
        author: 'James F. Kurose',
        publisher: 'Pearson',
        year: '2017',
        cover: 'https://picsum.photos/200/300?random=107',
        location: '二楼计算机科学区',
        callNumber: 'TP393/K968',
        available: true,
        availableCopies: 4,
        isbn: '9780133594140',
        pages: '864',
        description: '本书采用自顶向下的方法讲解计算机网络的原理及其协议，是经典的计算机网络教材。',
        type: 'book',
        library: 'main'
      }
    ].filter(book =>
      book.title.includes(searchQuery.value) ||
      book.author.includes(searchQuery.value) ||
      (book.isbn && book.isbn.includes(searchQuery.value))
    )

    // 应用筛选条件
    let filteredResults = [...allResults]
    if (selectedType.value) {
      filteredResults = filteredResults.filter(book => book.type === selectedType.value)
    }
    if (selectedLocation.value) {
      filteredResults = filteredResults.filter(book => book.library === selectedLocation.value)
    }

    // 模拟排序
    if (selectedSort.value === 'newest') {
      filteredResults.sort((a, b) => b.year - a.year)
    } else if (selectedSort.value === 'popular') {
      filteredResults.sort(() => Math.random() - 0.5) // 模拟随机排序
    }

    totalResults.value = filteredResults.length
    searchResults.value = filteredResults.slice(
      (currentPage.value - 1) * pageSize.value,
      currentPage.value * pageSize.value
    )

    ElMessage.success(`找到 ${totalResults.value} 条结果`)
  }, 800)
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  performSearch() // 直接重新搜索，简化逻辑
}

// 快速搜索
const quickSearch = (title) => {
  searchQuery.value = title
  performSearch()
}

// 重置搜索
const resetSearch = () => {
  searchQuery.value = ''
  selectedType.value = ''
  selectedSort.value = 'relevance'
  selectedLocation.value = ''
  searchResults.value = []
  currentPage.value = 1
}

// 查看图书详情
const viewBookDetail = (book) => {
  selectedBook.value = book
  detailVisible.value = true
}

// 预约图书
const reserveBook = (book) => {
  ElMessage.success(`已预约《${book.title}》，请到${book.location}借阅`)
  detailVisible.value = false
}

// 从搜索历史中移除
const removeSearchHistory = (index) => {
  searchHistory.value.splice(index, 1)
  localStorage.setItem('librarySearchHistory', JSON.stringify(searchHistory.value))
}

// 加载搜索历史
onMounted(() => {
  const history = localStorage.getItem('librarySearchHistory')
  if (history) {
    searchHistory.value = JSON.parse(history)
  }
})

// 原有数据和方法
const stats = ref([
  {
    label: '当前借阅',
    value: 3,
    icon: Book,
    color: '#6366f1'
  },
  {
    label: '逾期数量',
    value: 0,
    icon: Clock,
    color: '#ef4444'
  },
  {
    label: '可续借',
    value: 2,
    icon: Check,
    color: '#10b981'
  },
  {
    label: '累计借阅',
    value: 42,
    icon: List,
    color: '#3b82f6'
  }
])

const currentBorrows = ref([
  {
    id: 1,
    title: '数据结构与算法分析',
    author: 'Mark Allen Weiss',
    cover: 'https://picsum.photos/200/300?random=1',
    borrowDate: '2023-06-15',
    dueDate: '2023-07-15',
    renewTimes: 1
  },
  {
    id: 2,
    title: '计算机网络：自顶向下方法',
    author: 'Andrew S. Tanenbaum',
    cover: 'https://picsum.photos/200/300?random=2',
    borrowDate: '2023-06-20',
    dueDate: '2023-07-20',
    renewTimes: 0
  },
  {
    id: 3,
    title: '操作系统概念',
    author: 'Abraham Silberschatz',
    cover: 'https://picsum.photos/200/300?random=3',
    borrowDate: '2023-06-25',
    dueDate: '2023-07-25',
    renewTimes: 0
  }
])

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const isOverdue = (dueDate) => {
  return new Date(dueDate) < new Date()
}

const canRenew = (book) => {
  return book.renewTimes < 2 && !isOverdue(book.dueDate)
}

const renewBook = (book) => {
  if (!canRenew(book)) return

  const index = currentBorrows.value.findIndex(item => item.id === book.id)
  if (index !== -1) {
    const newDueDate = new Date(book.dueDate)
    newDueDate.setDate(newDueDate.getDate() + 30)

    currentBorrows.value[index].renewTimes += 1
    currentBorrows.value[index].dueDate = newDueDate.toISOString().split('T')[0]
    ElMessage.success(`《${book.title}》续借成功，新应还日期: ${formatDate(newDueDate)}`)
  }
}
</script>

<style scoped lang="scss">
.library-container {
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s ease;
  gap: 30px; // 保持与系列设计一致的卡片间距
  padding: 0 15px;
  --mouse-x: 0;
  --mouse-y: 0;
}

// 现代化卡片样式 - 完全统一设计语言
.modern-card {
  position: relative;
  border-radius: 16px;
  padding: 30px;
  transition: all 0.3s ease;
  overflow: hidden;
  z-index: 1;

  // 卡片内部相对定位
  .card-content {
    position: relative;
    z-index: 2;
  }

  // 浅色模式
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);

  // 深色模式样式
  .dark & {
    background: rgba(30, 41, 59, 0.8);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  }

  // 卡片光影效果 - 微紫色
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

  // 卡片悬停效果 - 与系列设计完全一致
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);

    &::before {
      opacity: 1;
    }
  }

  // 卡片头部
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2,
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

/* 卡片头部样式 */
.card-header {
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
      color: #6366f1;
      text-shadow: 0 2px 4px rgba(99, 102, 241, 0.1);
    }
  }
}

/* 搜索栏样式 */
.search-bar {
  display: flex;
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  input {
    flex: 1;
    padding: 12px 16px;
    border: none;
    font-size: 15px;
    outline: none;
    background: #f8fafc;
    transition: all 0.3s ease;

    &:focus {
      background: white;
    }

    .dark & {
      background: rgba(40, 45, 55, 0.6);
      color: white;

      &:focus {
        background: rgba(50, 55, 65, 0.8);
      }
    }
  }

  button {
    padding: 0 20px;
    background: #6366f1;
    color: white;
    border: none;
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    transition: background 0.3s ease;

    &:hover {
      background: #4f46e5;
    }

    .el-icon {
      font-size: 16px;
    }
  }
}

/* 筛选条件样式 */
.search-filters {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;

  .filter-item {
    display: flex;
    align-items: center;
    gap: 10px;

    label {
      font-size: 14px;
      color: var(--text-secondary);
      white-space: nowrap;
    }

    :deep(.el-select) {
      .el-input__wrapper {
        background: #f8fafc;
        box-shadow: none;

        .dark & {
          background: rgba(40, 45, 55, 0.6);
        }
      }
    }
  }
}

/* 搜索结果列表样式 */
.search-results {
  margin-top: 20px;

  .result-list {
    display: grid;
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .result-item {
    display: flex;
    align-items: center;
    padding: 16px;
    border-radius: 12px;
    background: #f8fafc;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      background: white;

      .dark & {
        background: rgba(50, 55, 65, 0.6);
      }

      .arrow {
        opacity: 1;
        transform: translateX(0);
      }
    }

    .result-cover {
      width: 60px;
      height: 80px;
      object-fit: cover;
      border-radius: 4px;
      margin-right: 16px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }

    .result-info {
      flex: 1;

      h4 {
        margin: 0 0 6px;
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
      }

      .result-meta {
        margin: 0 0 6px;
        font-size: 13px;
        color: var(--text-secondary);
      }

      .result-location {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: #666;
        margin-bottom: 8px;

        .el-icon {
          color: #6366f1;
        }
      }

      .result-status {
        display: inline-flex;
        align-items: center;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
        background: #e2e8f0;
        color: #64748b;

        &.available {
          background: #d1fae5;
          color: #065f46;
        }

        &.limited {
          background: #fef3c7;
          color: #92400e;
        }

        span {
          margin-left: 6px;
          font-size: 11px;
          opacity: 0.8;
        }
      }
    }

    .arrow {
      position: absolute;
      right: 16px;
      opacity: 0;
      transform: translateX(-10px);
      transition: all 0.3s ease;
      color: var(--text-secondary);
    }
  }

  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid rgba(0, 0, 0, 0.1);

    .dark & {
      border-top-color: rgba(255, 255, 255, 0.1);
    }
  }
}

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  text-align: center;

  .el-icon {
    font-size: 48px;
    color: var(--text-secondary);
    margin-bottom: 16px;
  }

  p {
    margin: 0 0 16px;
    color: var(--text-secondary);
  }
}

/* 热门推荐样式 */
.recommendations {
  margin-top: 30px;

  h4 {
    margin: 0 0 16px;
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    display: flex;
    align-items: center;

    &::before {
      content: '';
      display: inline-block;
      width: 4px;
      height: 16px;
      background: #6366f1;
      margin-right: 10px;
      border-radius: 2px;
    }
  }

  .recommend-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 16px;
  }

  .recommend-item {
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;

    &:hover {
      transform: translateY(-4px);
    }

    img {
      width: 100%;
      height: 160px;
      object-fit: cover;
      border-radius: 8px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      margin-bottom: 8px;
    }

    p {
      margin: 0;
      font-size: 13px;
      color: var(--text-primary);
      text-align: center;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .recommend-badge {
      position: absolute;
      top: 8px;
      right: 8px;
      background: #f43f5e;
      color: white;
      font-size: 10px;
      padding: 2px 6px;
      border-radius: 10px;
    }
  }

  .history-search {
    margin-top: 30px;

    h4 {
      margin-bottom: 12px;
    }

    .history-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .el-tag {
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        }
      }
    }
  }
}

/* 原有样式 */
.summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.summary-card {
  display: flex;
  align-items: center;
  padding: 1.5rem;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(226, 232, 240, 0.6);

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

  .stat-icon {
    position: relative;
    margin-right: 1.25rem;
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    z-index: 1;

    .icon-bg {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      border-radius: 12px;
      background-color: var(--card-color);
      opacity: 0.1;
      transition: all 0.3s ease;
    }

    .el-icon {
      color: var(--card-color);
      z-index: 1;
    }
  }

  .stat-content {
    flex: 1;
    min-width: 0;
    z-index: 1;

    .stat-value {
      font-size: 1.75rem;
      font-weight: 700;
      color: #1e293b;
      line-height: 1.2;
      margin-bottom: 0.25rem;
      font-feature-settings: "tnum";

      .dark & {
        color: rgba(248, 250, 252, 0.9);
      }
    }

    .stat-label {
      font-size: 0.875rem;
      font-weight: 500;
      color: #64748b;
      line-height: 1.4;

      .dark & {
        color: rgba(203, 213, 225, 0.8);
      }
    }
  }

  .stat-trend {
    display: flex;
    align-items: center;
    margin-left: 0.75rem;
    font-size: 0.75rem;
    font-weight: 600;

    .el-icon {
      margin-right: 0.25rem;
      font-size: 0.875rem;
    }
  }
}

.card-icon {
  font-size: 24px;
  margin-right: 15px;
}

.card-content h3 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.card-content p {
  margin: 5px 0 0;
  color: #666;
  font-size: 14px;
}

.book-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.book-card {
  display: flex;
  gap: 15px;
  padding: 15px;
  border-radius: 8px;
  background: #f8fafc;
  transition: transform 0.2s, box-shadow 0.2s;

  .dark & {
    background: rgba(40, 45, 55, 0.6);
  }

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
}

.book-cover {
  width: 80px;
  height: 110px;
  object-fit: cover;
  border-radius: 4px;
}

.book-info {
  flex: 1;

  h3 {
    margin: 0 0 5px;
    font-size: 16px;
    font-weight: 600;
  }

  .author {
    margin: 0 0 10px;
    color: #666;
    font-size: 14px;
  }

  .due-date {
    font-size: 13px;
    margin-bottom: 8px;

    &.overdue {
      color: #ef4444;
      font-weight: 500;

      .overdue-text {
        font-size: 12px;
      }
    }
  }

  .renew-info {
    font-size: 12px;
    color: #666;
    margin-bottom: 10px;
  }

  .renew-btn {
    background: #6366f1;
    color: white;
    border: none;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 13px;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: #4f46e5;
    }

    &:disabled {
      background: #e2e8f0;
      color: #94a3b8;
      cursor: not-allowed;
    }
  }
}

/* 图书详情弹窗样式 */
.book-detail {
  .detail-main {
    display: flex;
    gap: 30px;
    margin-bottom: 20px;

    .detail-cover {
      width: 180px;
      height: 240px;
      object-fit: cover;
      border-radius: 8px;
      box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
    }

    .detail-info {
      flex: 1;

      h3 {
        margin: 0 0 16px;
        font-size: 22px;
        font-weight: 650;
        color: var(--text-primary);
      }

      p {
        margin: 0 0 10px;
        font-size: 15px;
        color: var(--text-primary);

        .label {
          display: inline-block;
          width: 70px;
          color: var(--text-secondary);
        }
      }

      .availability {
        display: flex;
        align-items: center;
        margin-top: 16px;

        .label {
          width: 70px;
          color: var(--text-secondary);
        }

        .status-available {
          color: #10b981;
          font-weight: 500;
        }

        .status-unavailable {
          color: #ef4444;
          font-weight: 500;
        }
      }
    }
  }

  .detail-description {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid rgba(0, 0, 0, 0.1);

    .dark & {
      border-top-color: rgba(255, 255, 255, 0.1);
    }

    h4 {
      margin: 0 0 12px;
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
    }

    p {
      margin: 0;
      font-size: 14px;
      line-height: 1.6;
      color: var(--text-primary);
    }
  }

  .detail-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid rgba(0, 0, 0, 0.1);

    .dark & {
      border-top-color: rgba(255, 255, 255, 0.1);
    }
  }
}

@media (max-width: 768px) {
  .search-filters {
    flex-direction: column;
    gap: 12px;
  }

  .recommend-list {
    grid-template-columns: repeat(2, 1fr);
  }

  .summary-cards {
    grid-template-columns: 1fr 1fr;
  }

  .book-list {
    grid-template-columns: 1fr;
  }

  .book-detail .detail-main {
    flex-direction: column;

    .detail-cover {
      align-self: center;
    }
  }
}

@media (max-width: 480px) {
  .modern-card {
    padding: 20px;
  }

  .result-item {
    flex-direction: column;
    align-items: flex-start;

    .result-cover {
      margin-bottom: 12px;
    }
  }

  .recommend-list {
    grid-template-columns: repeat(2, 1fr);
  }

  .summary-cards {
    grid-template-columns: 1fr;
  }
}
</style>