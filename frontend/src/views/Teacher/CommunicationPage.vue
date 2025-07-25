<template>
  <div class="modern-chat-app">
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>消息</h2>
        <el-button type="text" icon="Plus" circle @click="showNewChatDialog = true" />
      </div>

      <div class="search-box">
        <el-input v-model="searchQuery" placeholder="搜索对话" clearable :prefix-icon="Search" />
      </div>

      <div class="chat-list">
        <div v-for="(chat, index) in filteredChats" :key="chat.id"
          :class="['chat-item', { active: currentChatIndex === index }]" @click="selectChat(index)">
          <el-avatar :size="48" :src="chat.avatar" :alt="chat.name" />
          <div class="chat-info">
            <div class="chat-header">
              <h3>{{ chat.name }}</h3>
              <span>{{ formatRelativeTime(chat.lastMessageTime) }}</span>
            </div>
            <p class="last-message">
              <template v-if="chat.lastMessageType === 'image'">
                <el-icon>
                  <Picture />
                </el-icon> 图片
              </template>
              <template v-if="chat.lastMessageType === 'file'">
                <el-icon>
                  <Document />
                </el-icon> 文件
              </template>
              <template v-else>{{ chat.preview }}</template>
            </p>
          </div>
          <el-badge :value="chat.unreadCount" :max="99" :hidden="!chat.unreadCount" type="primary" />
        </div>
      </div>
    </div>

    <div class="chat-area">
      <div v-if="!currentChat" class="empty-chat">
        <div class="empty-content">
          <el-icon>
            <ChatRound />
          </el-icon>
          <h3>选择一个对话开始聊天</h3>
          <p>或创建一个新的对话</p>
          <el-button type="primary" @click="showNewChatDialog = true">
            新建对话
          </el-button>
        </div>
      </div>

      <div v-else class="active-chat">
        <div class="chat-header">
          <div class="header-left">
            <el-avatar :size="40" :src="currentChat.avatar" :alt="currentChat.name" />
            <div class="user-info">
              <h3>{{ currentChat.name }}</h3>
              <p v-if="currentChat.online" class="online-status">
                <span class="online-dot"></span> 在线
              </p>
              <p v-else class="online-status">
                上次在线 {{ formatRelativeTime(currentChat.lastOnline) }}
              </p>
            </div>
          </div>
          <div class="header-actions">
            <el-button type="text" icon="Phone" circle />
            <el-button type="text" icon="VideoCamera" circle />
            <el-dropdown>
              <el-button type="text" icon="MoreFilled" circle />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="clearChatHistory">
                    <el-icon>
                      <Delete />
                    </el-icon> 清空聊天记录
                  </el-dropdown-item>
                  <el-dropdown-item @click="muteChat">
                    <el-icon>
                      <BellFilled />
                    </el-icon> 静音
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <div class="messages-container">
          <div class="messages" ref="messagesContainer">
            <div class="message-group" v-for="(group, index) in messageGroups" :key="index">
              <div class="date-divider">
                <span>{{ formatMessageDate(group.date) }}</span>
              </div>

              <div v-for="message in group.messages" :key="message.id" :class="['message', message.type]">
                <el-avatar v-if="message.type === 'bot'" :size="36" :src="currentChat.avatar" />

                <div class="message-content">
                  <div class="message-bubble">
                    <img v-if="message.contentType === 'image'" :src="message.content"
                      @click="previewImage(message.content)" />
                    <div v-else-if="message.contentType === 'file'" class="file-message">
                      <el-icon>
                        <Document />
                      </el-icon>
                      <div class="file-info">
                        <div class="file-name">{{ getFileName(message.content) }}</div>
                        <div class="file-size">{{ message.fileSize }}</div>
                      </div>
                      <el-button type="primary" text @click="downloadFile(message.content)">
                        下载
                      </el-button>
                    </div>
                    <div v-else v-html="formatMessage(message.content)"></div>
                  </div>

                  <div class="message-meta">
                    <span>{{ formatMessageTime(message.time) }}</span>
                    <el-icon v-if="message.type === 'user'" :class="['status-icon', message.status]">
                      <template v-if="message.status === 'sending'">
                        <Loading />
                      </template>
                      <template v-else-if="message.status === 'sent'">
                        <CircleCheck />
                      </template>
                      <template v-else-if="message.status === 'read'"><Select /></template>
                      <template v-else-if="message.status === 'failed'">
                        <CircleClose />
                      </template>
                    </el-icon>
                  </div>
                </div>

                <el-dropdown v-if="message.type === 'user'" trigger="click"
                  @command="handleMessageCommand($event, message)">
                  <el-button type="text" icon="MoreFilled" class="message-actions" />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="copy">复制</el-dropdown-item>
                      <el-dropdown-item command="recall" :disabled="message.status === 'sending'">
                        撤回
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>
        </div>

        <div class="input-area">
          <div class="input-tools">
            <el-button type="text" icon="Picture" @click="triggerFileInput('image')" />
            <el-button type="text" icon="VideoCamera" @click="triggerFileInput('video')" />
            <el-button type="text" icon="Document" @click="triggerFileInput('file')" />
            <el-button type="text" icon="Sunny" @click="toggleDarkMode" />
            <input type="file" ref="fileInput" style="display: none" @change="handleFileUpload" />
          </div>

          <div class="input-wrapper">
            <el-input v-model="newMessage" type="textarea" :rows="2" :maxlength="500" placeholder="输入消息..."
              resize="none" @keyup.enter.exact.prevent="sendMessage" class="embedded-input" />
            <div class="send-button-container">
              <el-button class="send-button" type="primary" :icon="Promotion" circle
                :disabled="!newMessage.trim() && !fileToUpload" @click="sendMessage" :loading="sending" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showNewChatDialog" title="添加联系人" width="480px" append-to-body
      class="custom-dialog new-chat-dialog" :before-close="handleCloseNewChatDialog" :close-on-click-modal="false">
      <template #title>
        <div class="dialog-title flex items-center">
          <span class="font-medium text-lg">添加联系人</span>
        </div>
      </template>

      <div class="dialog-content p-4">
        <el-input v-model="newChatSearch" placeholder="输入要对话的联系人" :prefix-icon="Search" class="search-input mb-4"
          @input="onSearchInput" clearable @clear="clearSearch" />

        <div v-if="!searchPerformed" class="no-search-hint flex flex-col items-center justify-center py-16" />

        <div v-else class="contact-list max-h-[400px] overflow-y-auto">
          <div v-if="filteredContacts.length > 0">
            <div v-for="contact in filteredContacts" :key="contact.id" class="contact-card custom-contact-card"
              @click="createNewChat(contact)">
              <el-avatar :size="48" :src="contact.avatar" :alt="contact.name" class="mr-3" />
              <div class="contact-info">
                <h3 class="contact-name">
                  <span v-html="highlightMatch(contact.name, newChatSearch)"></span>
                </h3>
                <p class="contact-desc">
                  {{ contact.online ? '在线' : '离线' }} | {{ contact.department }}
                </p>
              </div>
            </div>
          </div>

          <div v-else-if="newChatSearch.trim()" class="empty-result flex flex-col items-center justify-center py-10">
            <h3 class="text-lg font-medium text-gray-800 mb-2">未找到联系人</h3>
            <p class="text-gray-500 text-center max-w-xs">尝试使用不同的关键词或添加新联系人</p>
            <el-button type="text" size="small" class="mt-4 text-primary-color" @click="clearSearch">
              <el-icon class="mr-1">
                <RefreshRight />
              </el-icon>
              重新搜索
            </el-button>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer flex justify-end px-4 py-3 border-t border-gray-100">
          <el-button @click="showNewChatDialog = false" class="px-4 py-2 rounded-md hover:bg-gray-50 transition-colors">
            <el-icon class="mr-1">
              <Close />
            </el-icon>
            取消
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="imagePreviewVisible" title="图片预览" width="80%">
      <img :src="previewImageUrl" class="preview-image" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import {
  Search,
  Picture,
  Document,
  Promotion,
  Loading,
  CircleCheck,
  Select,
  CircleClose,
  Delete,
  BellFilled,
  ChatRound,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useClipboard } from '@vueuse/core'

const { copy } = useClipboard()

const contacts = ref([
  { id: 1, name: '张教授', avatar: 'https://randomuser.me/api/portraits/men/32.jpg', online: true, department: '计算机学院' },
  { id: 2, name: '李老师', avatar: 'https://randomuser.me/api/portraits/women/44.jpg', online: false, lastOnline: new Date(Date.now() - 3600000), department: '数学系' },
  { id: 3, name: '王老师', avatar: 'https://randomuser.me/api/portraits/men/45.jpg', online: true, department: '计算机学院' },
  { id: 4, name: '赵老师', avatar: 'https://randomuser.me/api/portraits/women/68.jpg', online: true, department: '外语系' },
  { id: 5, name: '教务处', avatar: 'https://randomuser.me/api/portraits/women/68.jpg', online: false, lastOnline: new Date(Date.now() - 86400000), department: '行政部门' },
  { id: 6, name: '学生事务办公室', avatar: 'https://randomuser.me/api/portraits/lego/3.jpg', online: true, department: '行政部门' },
])

const chatHistory = ref([
  {
    id: 1,
    name: '张教授',
    avatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    lastMessageTime: new Date(),
    lastMessageType: 'text',
    preview: '关于下周的研讨会，你有什么想法？',
    unreadCount: 0,
    online: true,
    department: '计算机学院',
    messages: [
      {
        id: 101,
        type: 'bot',
        contentType: 'text',
        content: '你好！关于下周的研讨会，你有什么想法？',
        time: new Date(Date.now() - 3600000 * 2),
        status: 'read'
      },
      {
        id: 102,
        type: 'user',
        contentType: 'text',
        content: '我认为可以重点讨论一下人工智能在教育中的应用',
        time: new Date(Date.now() - 3600000 * 1.8),
        status: 'read'
      },
      {
        id: 103,
        type: 'bot',
        contentType: 'text',
        content: '这是个很好的主题！我们可以准备以下几个方面的内容:<br>1. AI辅助教学系统<br>2. 智能评测系统<br>3. 个性化学习路径',
        time: new Date(Date.now() - 3600000 * 1.5),
        status: 'read'
      }
    ]
  },
  {
    id: 2,
    name: '李老师',
    avatar: 'https://randomuser.me/api/portraits/women/44.jpg',
    lastMessageTime: new Date(Date.now() - 86400000),
    lastMessageType: 'file',
    preview: '这是本学期教学大纲，请查收',
    unreadCount: 3,
    online: false,
    lastOnline: new Date(Date.now() - 3600000),
    department: '数学系',
    messages: [
      {
        id: 201,
        type: 'bot',
        contentType: 'text',
        content: '这是本学期教学大纲，请查收',
        time: new Date(Date.now() - 86400000),
        status: 'read'
      },
      {
        id: 202,
        type: 'bot',
        contentType: 'file',
        content: 'https://example.com/files/syllabus.pdf',
        fileSize: '2.1MB',
        time: new Date(Date.now() - 86400000),
        status: 'read'
      }
    ]
  },
  {
    id: 3,
    name: '计算机学院',
    avatar: 'https://randomuser.me/api/portraits/lego/3.jpg',
    lastMessageTime: new Date(Date.now() - 86400000 * 2),
    lastMessageType: 'text',
    preview: '提醒：教师培训工作坊将于本周五举行',
    unreadCount: 0,
    online: true,
    department: '行政部门',
    messages: [
      {
        id: 301,
        type: 'bot',
        contentType: 'text',
        content: '提醒：教师培训工作坊将于本周五下午2点在A101举行',
        time: new Date(Date.now() - 86400000 * 2),
        status: 'read'
      },
      {
        id: 302,
        type: 'bot',
        contentType: 'text',
        content: '主题：创新教学方法与技术应用',
        time: new Date(Date.now() - 86400000 * 2),
        status: 'read'
      }
    ]
  }
])

const searchQuery = ref('')
const newChatSearch = ref('')
const searchPerformed = ref(false)
const currentChatIndex = ref(0)
const newMessage = ref('')
const sending = ref(false)
const fileInput = ref(null)
const fileToUpload = ref(null)
const uploadType = ref('file')
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')
const showNewChatDialog = ref(false)
const messagesContainer = ref(null)

const filteredChats = computed(() => {
  if (!searchQuery.value.trim()) return chatHistory.value
  return chatHistory.value.filter(chat =>
    chat.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const filteredContacts = computed(() => {
  if (!newChatSearch.value.trim()) return []
  return contacts.value.filter(contact =>
    contact.name.toLowerCase().includes(newChatSearch.value.toLowerCase())
  )
})

const currentChat = computed(() => {
  if (currentChatIndex.value === null || !chatHistory.value.length) return null
  return chatHistory.value[currentChatIndex.value]
})

const messageGroups = computed(() => {
  if (!currentChat.value) return []

  const groups = []
  let currentGroup = null

  currentChat.value.messages.forEach(message => {
    const messageDate = new Date(message.time).toDateString()

    if (!currentGroup || currentGroup.date !== messageDate) {
      currentGroup = {
        date: messageDate,
        messages: []
      }
      groups.push(currentGroup)
    }

    currentGroup.messages.push(message)
  })

  return groups
})

const selectChat = (index) => {
  currentChatIndex.value = index
  if (currentChat.value?.unreadCount > 0) {
    currentChat.value.unreadCount = 0
  }
  nextTick(() => {
    scrollToBottom()
  })
}

const formatMessage = (content) => {
  if (!content) return ''
  const urlRegex = /(https?:\/\/[^\s]+)/g
  return content.replace(urlRegex, url => {
    return `<a href="${url}" target="_blank" class="message-link">${url}</a>`
  })
}

const formatRelativeTime = (date) => {
  if (!date) return ''

  const now = new Date()
  const diffInSeconds = Math.floor((now - new Date(date)) / 1000)

  if (diffInSeconds < 60) return '刚刚'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}分钟前`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}小时前`
  if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}天前`

  return new Date(date).toLocaleDateString()
}

const formatMessageTime = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const formatMessageDate = (dateString) => {
  const date = new Date(dateString)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)

  if (date.toDateString() === today.toDateString()) return '今天'
  if (date.toDateString() === yesterday.toDateString()) return '昨天'

  return date.toLocaleDateString([], { month: 'short', day: 'numeric' })
}

const sendMessage = async () => {
  if ((!newMessage.value.trim() && !fileToUpload.value) || !currentChat.value) return

  sending.value = true

  try {
    let messageContent = newMessage.value.trim()
    let contentType = 'text'
    let fileSize = null

    if (fileToUpload.value) {
      showUploadProgress()
      await new Promise(resolve => setTimeout(resolve, 800))

      if (uploadType.value === 'image') {
        contentType = 'image'
        messageContent = URL.createObjectURL(fileToUpload.value)
      } else {
        contentType = 'file'
        messageContent = `https://example.com/files/${fileToUpload.value.name}`
        fileSize = formatFileSize(fileToUpload.value.size)
      }
    }

    const message = {
      id: Date.now(),
      type: 'user',
      contentType,
      content: messageContent,
      fileSize,
      time: new Date(),
      status: 'sending'
    }

    currentChat.value.messages.push(message)
    updateChatPreview(currentChat.value, message)
    newMessage.value = ''
    fileToUpload.value = null

    nextTick(() => {
      scrollToBottom()
    })

    setTimeout(() => {
      message.status = 'sent'

      if (currentChat.value.id === 1) {
        setTimeout(() => {
          const replies = [
            "好的，我会把这些建议纳入研讨会讨论。",
            "这个方向很有价值，我们可以深入探讨。",
            "感谢你的建议，我会准备相关资料。"
          ]

          const botMessage = {
            id: Date.now(),
            type: 'bot',
            contentType: 'text',
            content: replies[Math.floor(Math.random() * replies.length)],
            time: new Date(),
            status: 'read'
          }

          currentChat.value.messages.push(botMessage)
          updateChatPreview(currentChat.value, botMessage)

          nextTick(() => {
            scrollToBottom()
          })
        }, 1500)
      }
    }, 800)
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  } finally {
    sending.value = false
  }
}

const showUploadProgress = () => {
  ElMessage({
    message: '正在上传文件...',
    type: 'info',
    duration: 1000
  })
}

const updateChatPreview = (chat, message) => {
  chat.preview = message.contentType === 'text'
    ? (message.content.length > 30 ? message.content.substring(0, 30) + '...' : message.content)
    : message.contentType === 'image' ? '[图片]' : '[文件]'
  chat.lastMessageTime = message.time
  chat.lastMessageType = message.contentType
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const triggerFileInput = (type) => {
  uploadType.value = type
  if (fileInput.value) {
    fileInput.value.click()
  }
}

const handleFileUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (uploadType.value === 'image' && !file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }

  const maxSize = uploadType.value === 'image' ? 5 * 1024 * 1024 : 20 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.warning(`文件大小不能超过${formatFileSize(maxSize)}`)
    return
  }

  fileToUpload.value = file
  event.target.value = ''
}

const downloadFile = (url) => {
  ElMessage.success('开始下载文件: ' + getFileName(url))
}

const getFileName = (url) => {
  return url.substring(url.lastIndexOf('/') + 1)
}

const previewImage = (url) => {
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

const handleMessageCommand = (command, message) => {
  if (command === 'copy') {
    copyMessage(message.content)
  } else if (command === 'recall') {
    recallMessage(message)
  }
}

const copyMessage = async (content) => {
  try {
    await copy(content)
    ElMessage.success('已复制到剪贴板')
  } catch (err) {
    ElMessage.error('复制失败')
  }
}

const recallMessage = (message) => {
  if (!currentChat.value) return

  const index = currentChat.value.messages.findIndex(m => m.id === message.id)
  if (index === -1) return

  if (new Date() - new Date(message.time) > 2 * 60 * 1000) {
    ElMessage.warning('只能撤回2分钟内的消息')
    return
  }

  currentChat.value.messages.splice(index, 1)
  ElMessage.success('消息已撤回')
}

const clearChatHistory = () => {
  if (!currentChat.value) return

  ElMessageBox.confirm(
    '确定要清空当前聊天记录吗？此操作不可撤销',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    currentChat.value.messages = []
    currentChat.value.preview = ''
    ElMessage.success('聊天记录已清空')
  }).catch(() => { })
}

const muteChat = () => {
  ElMessage.success('对话已静音')
}

const createNewChat = (contact) => {
  const existingChat = chatHistory.value.find(chat => chat.id === contact.id)

  if (existingChat) {
    currentChatIndex.value = chatHistory.value.indexOf(existingChat)
  } else {
    const newChat = {
      id: contact.id,
      name: contact.name,
      avatar: contact.avatar,
      lastMessageTime: new Date(),
      lastMessageType: 'text',
      preview: '新对话已开始',
      unreadCount: 0,
      online: contact.online,
      lastOnline: contact.lastOnline,
      department: contact.department,
      messages: []
    }

    chatHistory.value.unshift(newChat)
    currentChatIndex.value = 0
  }

  showNewChatDialog.value = false
  newChatSearch.value = ''
}


const handleCloseNewChatDialog = (done) => {
  done()
  newChatSearch.value = ''
}

const onSearchInput = () => {
  searchPerformed.value = newChatSearch.value.trim() !== ''
}

const clearSearch = () => {
  newChatSearch.value = ''
  searchPerformed.value = false
}

const highlightMatch = (text, search) => {
  if (!search.trim()) return text
  const regex = new RegExp(`(${search})`, 'gi')
  return text.replace(regex, '<span class="text-primary-color font-medium">$1</span>')
}
</script>

<style scoped lang="scss">
.modern-chat-app {
  display: flex;
  height: 100%;
  min-height: 500px;
  height: 80vh;
  width: 100%;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  box-sizing: border-box;

  --primary-color: #3b82f6;
  --primary-hover: #2563eb;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --bg-primary: #ffffff;
  --bg-secondary: #f8fafc;
  --border-color: #e2e8f0;
  --online-dot: #10b981;
  --message-user-bg: #3b82f6;
  --message-bot-bg: #ffffff;
  --message-user-color: #ffffff;
  --message-bot-color: #1e293b;

  .sidebar {
    width: 360px;
    background: var(--bg-primary);
    border-right: 1px solid var(--border-color);
    display: flex;
    flex-direction: column;
    height: 100%;
    flex-shrink: 0;

    &-header {
      padding: 16px 20px;
      border-bottom: 1px solid var(--border-color);
      display: flex;
      align-items: center;
      justify-content: space-between;
      flex-shrink: 0;

      h2 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
      }

      .el-button {
        color: var(--text-secondary);
        transition: all 0.2s;

        &:hover {
          background: #f1f5f9;
        }
      }
    }

    .search-box {
      padding: 12px 16px;
      flex-shrink: 0;

      :deep(.el-input__wrapper) {
        background: var(--bg-secondary);
        border-radius: 18px;
        box-shadow: none;
        border: 1px solid var(--border-color);

        &:hover {
          border-color: #cbd5e1;
        }

        .el-input__inner::placeholder {
          color: var(--text-secondary);
        }
      }
    }

    .chat-list {
      flex: 1;
      overflow-y: auto;
      padding: 8px;
      scrollbar-width: thin;
      scrollbar-color: rgba(156, 163, 175, 0.5) transparent;

      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-track {
        background: transparent;
      }

      &::-webkit-scrollbar-thumb {
        background-color: rgba(156, 163, 175, 0.5);
        border-radius: 3px;
      }

      .chat-item {
        display: flex;
        align-items: center;
        padding: 12px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s;
        margin-bottom: 4px;

        &:hover {
          background: #f1f5f9;
        }

        &.active {
          background: #e0e7ff;
        }

        .chat-info {
          flex: 1;
          margin-left: 12px;
          overflow: hidden;

          .chat-header {
            display: flex;
            justify-content: space-between;
            align-items: center;

            h3 {
              margin: 0;
              font-size: 14px;
              color: var(--text-primary);
              white-space: nowrap;
              text-overflow: ellipsis;
              overflow: hidden;
            }

            span {
              font-size: 12px;
              color: var(--text-secondary);
            }
          }

          .last-message {
            margin-top: 4px;
            font-size: 13px;
            color: var(--text-secondary);
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;

            .el-icon {
              margin-right: 4px;
              font-size: 12px;
            }
          }
        }

        .el-badge :deep(.el-badge__content) {
          top: 0;
          right: 0;
        }
      }
    }
  }

  .chat-area {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: var(--bg-secondary);
    height: 100%;
    min-width: 0;

    .empty-chat {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      text-align: center;

      .empty-content {
        .el-icon {
          font-size: 60px;
          color: var(--text-secondary);
          margin-bottom: 20px;
        }

        h3 {
          margin: 0 0 8px;
          font-size: 18px;
          color: var(--text-primary);
        }

        p {
          margin: 0 0 20px;
          color: var(--text-secondary);
          font-size: 14px;
        }
      }
    }

    .active-chat {
      display: flex;
      flex-direction: column;
      height: 100%;

      .chat-header {
        padding: 16px 24px;
        background: var(--bg-primary);
        border-bottom: 1px solid var(--border-color);
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-shrink: 0;

        .header-left {
          display: flex;
          align-items: center;

          .el-avatar {
            margin-right: 12px;
          }

          .user-info {
            h3 {
              margin: 0;
              font-size: 16px;
              color: var(--text-primary);
            }

            .online-status {
              margin: 2px 0 0;
              font-size: 12px;
              color: var(--text-secondary);

              .online-dot {
                display: inline-block;
                width: 8px;
                height: 8px;
                background: var(--online-dot);
                border-radius: 50%;
                margin-right: 4px;
              }
            }
          }
        }

        .header-actions {
          .el-button {
            color: var(--text-secondary);
            margin-left: 8px;

            &:hover {
              color: var(--primary-color);
            }
          }
        }
      }

      .messages-container {
        flex: 1;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        position: relative;
        min-height: 0;
      }

      .messages {
        flex: 1;
        overflow-y: auto;
        padding: 20px;
        background: var(--bg-secondary);
        width: 100%;
        box-sizing: border-box;
        min-height: 0;
        scrollbar-width: thin;
        scrollbar-color: rgba(156, 163, 175, 0.5) transparent;

        &::-webkit-scrollbar {
          width: 6px;
        }

        &::-webkit-scrollbar-track {
          background: transparent;
        }

        &::-webkit-scrollbar-thumb {
          background-color: rgba(156, 163, 175, 0.5);
          border-radius: 3px;
        }
      }

      .message-group {
        margin-bottom: 16px;
      }

      .date-divider {
        display: flex;
        justify-content: center;
        margin: 20px 0;
        position: relative;

        &::before {
          content: '';
          position: absolute;
          top: 50%;
          left: 0;
          right: 0;
          height: 1px;
          background: var(--border-color);
          z-index: 1;
        }

        span {
          background: var(--bg-secondary);
          padding: 0 12px;
          font-size: 12px;
          color: var(--text-secondary);
          position: relative;
          z-index: 2;
        }
      }

      .message {
        display: flex;
        margin-bottom: 16px;
        align-items: flex-start;
        opacity: 0;
        transform: translateY(10px);
        animation: fadeIn 0.3s ease forwards;

        &.user {
          flex-direction: row-reverse;

          .message-content {
            align-items: flex-end;
            margin-right: 12px;

            .message-bubble {
              background: var(--message-user-bg);
              color: var(--message-user-color);
              border-radius: 18px 18px 0 18px;

              :deep(a.message-link) {
                color: #cce0ff;
                text-decoration: underline;
              }
            }
          }
        }

        &.bot {
          .message-content {
            align-items: flex-start;
            margin-left: 12px;

            .message-bubble {
              background: var(--message-bot-bg);
              color: var(--message-bot-color);
              border-radius: 18px 18px 18px 0;
              box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

              :deep(a.message-link) {
                color: var(--primary-color);
              }
            }
          }
        }

        .message-content {
          max-width: 70%;
          display: flex;
          flex-direction: column;

          .message-bubble {
            padding: 12px 16px;
            font-size: 15px;
            line-height: 1.5;
            word-break: break-word;

            img {
              max-width: 300px;
              max-height: 300px;
              border-radius: 8px;
              cursor: pointer;
              transition: transform 0.2s;

              &:hover {
                transform: scale(1.02);
              }
            }

            .file-message {
              display: flex;
              align-items: center;
              padding: 8px;

              .el-icon {
                font-size: 32px;
                color: var(--primary-color);
                margin-right: 12px;
              }

              .file-info {
                flex: 1;

                .file-name {
                  font-weight: 5;
                  margin-bottom: 4px;
                }

                .file-size {
                  font-size: 12px;
                  color: var(--text-secondary);
                }
              }
            }
          }

          .message-meta {
            display: flex;
            align-items: center;
            margin-top: 6px;

            span {
              font-size: 11px;
              color: var(--text-secondary);
            }

            .status-icon {
              font-size: 14px;
              margin-left: 4px;

              &.sending {
                color: var(--text-secondary);
                animation: rotate 1s linear infinite;
              }

              &.sent {
                color: var(--text-secondary);
              }

              &.read {
                color: var(--primary-color);
              }

              &.failed {
                color: #f56c6c;
              }
            }
          }
        }

        .message-actions {
          opacity: 0;
          transition: opacity 0.2s;
          color: var(--text-secondary);
          margin: 0 4px;

          &:hover {
            color: var(--primary-color);
          }
        }

        &:hover .message-actions {
          opacity: 1;
        }
      }

      .input-area {
        padding: 16px;
        background: var(--bg-primary);
        border-top: 1px solid var(--border-color);
        flex-shrink: 0;

        .input-tools {
          display: flex;
          margin-bottom: 8px;

          .el-button {
            color: var(--text-secondary);
            padding: 4px;

            &:hover {
              color: var(--primary-color);
            }
          }
        }

        .input-wrapper {
          position: relative;

          .embedded-input {
            :deep(.el-textarea__inner) {
              border-radius: 8px;
              padding: 12px 60px 12px 16px;
              resize: none;
              border: 1px solid var(--border-color);
              background: var(--bg-secondary);
              box-shadow: none;
              transition: all 0.3s;
              min-height: 48px;

              &:focus {
                border-color: var(--primary-color);
                background: var(--bg-primary);
                box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.2);
              }
            }
          }

          .send-button-container {
            position: absolute;
            right: 6px;
            bottom: 6px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: var(--bg-secondary);
            border-radius: 50%;
            transition: all 0.3s;

            &::before {
              content: '';
              position: absolute;
              top: -6px;
              right: -6px;
              bottom: -6px;
              left: -6px;
              border: 2px solid var(--bg-secondary);
              border-radius: 50%;
              z-index: -1;
            }
          }

          .send-button {
            width: 32px;
            height: 32px;
            transition: all 0.3s;

            &:disabled {
              opacity: 0.5;
              transform: scale(0.9);
              background-color: #c0c4cc;
              border-color: #c0c4cc;
            }

            &:not(:disabled) {
              &:hover {
                transform: scale(1.05);
                box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
              }

              &:active {
                transform: scale(0.95);
              }
            }
          }

          &:hover,
          &:focus-within {
            .send-button-container {
              background: var(--bg-primary);

              &::before {
                border-color: var(--bg-primary);
              }
            }
          }

          &:focus-within {
            .send-button-container {
              &::before {
                border-color: rgba(64, 158, 255, 0.2);
              }
            }
          }
        }
      }
    }
  }

  .dark & {
    --primary-color: #3b82f6;
    --primary-hover: #2563eb;
    --text-primary: #e2e8f0;
    --text-secondary: #94a3b8;
    --bg-primary: #1e293b;
    --bg-secondary: #0f172a;
    --border-color: #334155;
    --online-dot: #10b981;
    --message-user-bg: #3b82f6;
    --message-bot-bg: #1e293b;
    --message-user-color: #ffffff;
    --message-bot-color: #e2e8f0;

    .sidebar {
      .chat-list {
        .chat-item {
          &.active {
            background: rgba(59, 130, 246, 0.2);
          }

          &:hover {
            background: rgba(59, 130, 246, 0.1);
          }
        }
      }
    }

    .messages {
      .message-bubble {
        &.bot {
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);

          :deep(a.message-link) {
            color: #60a5fa;
          }
        }
      }
    }

    .chat-header,
    .input-area {
      border-color: var(--border-color);
    }

    .search-box :deep(.el-input__wrapper) {
      background: var(--bg-secondary);
      border-color: var(--border-color);

      &:hover {
        border-color: #475569;
      }
    }

    .el-input :deep(.el-input__inner),
    .el-textarea :deep(.el-textarea__inner) {
      background: var(--bg-secondary);
      border-color: var(--border-color);
      color: var(--text-primary);

      &::placeholder {
        color: var(--text-secondary);
      }

      &:focus {
        border-color: var(--primary-color);
        box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.2);
      }
    }

    .date-divider::before {
      background: var(--border-color);
    }

    .date-divider span {
      background: var(--bg-secondary);
    }

    .el-button {
      &:hover {
        background: rgba(59, 130, 246, 0.1);
      }
    }

    .el-dropdown-menu {
      background: var(--bg-primary);
      border-color: var(--border-color);

      .el-dropdown-item {
        color: var(--text-primary);

        &:hover {
          background: rgba(59, 130, 246, 0.1);
        }
      }
    }

    .el-badge :deep(.el-badge__content) {
      background-color: var(--primary-color);
    }

    .el-dialog {
      background: var(--bg-primary);
      border-color: var(--border-color);

      .el-dialog__title {
        color: var(--text-primary);
      }

      .el-dialog__content {
        color: var(--text-secondary);
      }

      .el-button--primary {
        background-color: var(--primary-color);
        border-color: var(--primary-color);

        &:hover {
          background-color: var(--primary-hover);
          border-color: var(--primary-hover);
        }
      }
    }
  }
}

.custom-contact-card {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  background-color: #1f2b3d;
  color: #fff;
  margin-bottom: 8px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.contact-name {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 4px 0;
}

.contact-desc {
  font-size: 14px;
  color: #c0c0c0;
  margin: 0;
}

.custom-contact-card:hover {
  background-color: #2c3e57;
}

@keyframes fadeIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}
</style>