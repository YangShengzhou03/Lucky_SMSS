<template>
    <div class="doubao-communication-container">
        <!-- 顶部导航栏 -->
        <div class="doubao-header">
            <div class="doubao-logo">
                <span class="doubao-icon">豆包</span>
                <span class="doubao-title">交流</span>
            </div>
            <div class="doubao-user-menu">
                <el-avatar size="small" src="https://picsum.photos/id/64/200/200" alt="用户头像"></el-avatar>
                <span class="doubao-username">教师用户</span>
            </div>
        </div>

        <!-- 主内容区 -->
        <div class="doubao-main-content">
            <!-- 对话历史列表 -->
            <div class="doubao-chat-history">
                <div class="doubao-search-box">
                    <el-input v-model="searchQuery" placeholder="搜索对话" clearable prefix-icon="Search">
                        <template #prefix>
                            <el-icon>
                                <Search />
                            </el-icon>
                        </template>
                    </el-input>
                </div>
                
                <div class="doubao-chat-items">
                    <div v-for="(chat, index) in filteredChats" :key="index" 
                         :class="['doubao-chat-item', { 'active': currentChatIndex === index }]"
                         @click="selectChat(index)">
                        <el-avatar size="40" :src="chat.avatar" :alt="chat.name"></el-avatar>
                        <div class="doubao-chat-info">
                            <div class="doubao-chat-header">
                                <h3 class="doubao-chat-name">{{ chat.name }}</h3>
                                <span class="doubao-chat-time">{{ chat.time }}</span>
                            </div>
                            <p class="doubao-chat-preview">{{ chat.preview }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 聊天内容区 -->
            <div class="doubao-chat-container">
                <!-- 聊天头部 -->
                <div class="doubao-chat-header">
                    <h2>{{ currentChat ? currentChat.name : '选择一个对话' }}</h2>
                    <div class="doubao-chat-actions">
                        <el-button type="text" icon="More">
                            <el-icon>
                                <More />
                            </el-icon>
                        </el-button>
                    </div>
                </div>

                <!-- 聊天消息区 -->
                <div class="doubao-messages" ref="messageContainer">
                    <div v-if="!currentChat" class="doubao-empty-chat">
                        <el-empty description="请选择一个对话开始交流"></el-empty>
                    </div>
                    
                    <div v-else>
                        <!-- 系统消息 -->
                        <div class="doubao-system-message" v-if="currentChat.systemMessage">
                            <p>{{ currentChat.systemMessage }}</p>
                        </div>
                        
                        <!-- 用户消息 -->
                        <div v-for="(message, msgIndex) in currentChat.messages" :key="msgIndex" 
                             :class="['doubao-message', message.type === 'user' ? 'doubao-user-message' : 'doubao-bot-message']">
                            <el-avatar v-if="message.type === 'bot'" size="36" src="https://picsum.photos/id/237/200/200" alt="豆包头像"></el-avatar>
                            <el-avatar v-else size="36" src="https://picsum.photos/id/64/200/200" alt="用户头像"></el-avatar>
                            
                            <div class="doubao-message-content">
                                <div class="doubao-message-bubble" v-html="formatMessage(message.content)"></div>
                                <span class="doubao-message-time">{{ message.time }}</span>
                            </div>
                        </div>
                    </div>
                
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { 
    Search, 
    More
} from '@element-plus/icons-vue'

// 模拟对话数据
const chatHistory = ref([
    {
        id: 1,
        name: '豆包助手',
        avatar: 'https://picsum.photos/id/237/200/200',
        time: '10:25',
        preview: '你好！我是豆包助手，有什么可以帮助你的吗？',
        systemMessage: '今天10:20: 豆包助手加入了对话',
        messages: [
            { type: 'bot', content: '你好！我是豆包助手，有什么可以帮助你的吗？', time: '10:25' },
            { type: 'user', content: '你好，我想咨询一下教学计划的制定方法', time: '10:27' },
            { type: 'bot', content: '制定教学计划需要考虑以下几个方面：<br>1. 明确教学目标和学习成果<br>2. 设计合理的教学内容和进度安排<br>3. 选择合适的教学方法和评估方式<br>4. 准备必要的教学资源<br><br>你需要我为你详细解释哪一部分呢？', time: '10:30' },
            { type: 'user', content: '请详细解释一下如何设计教学内容和进度安排', time: '10:32' }
        ]
    },
    {
        id: 2,
        name: '教务处王老师',
        avatar: 'https://picsum.photos/id/1005/200/200',
        time: '昨天 16:45',
        preview: '下学期的课程安排已经出来了，请查收附件',
        systemMessage: '昨天16:30: 王老师邀请你查看课程安排',
        messages: [
            { type: 'bot', content: '李老师，你好！下学期的课程安排已经出来了，请查收附件。如有疑问，请随时联系我。', time: '昨天 16:45' },
            { type: 'bot', content: '<el-link href="#" @click.stop="() => ElMessage.info(\'下载课程安排\')">下学期课程安排.xlsx</el-link>', time: '昨天 16:45' },
            { type: 'user', content: '收到，谢谢！我会尽快确认', time: '昨天 16:50' }
        ]
    },
    {
        id: 3,
        name: '计算机科学系',
        avatar: 'https://picsum.photos/id/1012/200/200',
        time: '周一 09:15',
        preview: '系里将于本周四下午召开教学研讨会',
        systemMessage: '周一09:00: 计算机科学系发布了新通知',
        messages: [
            { type: 'bot', content: '各位老师，系里将于本周四下午2点在3号楼会议室召开教学研讨会，请各位老师安排好时间参加。', time: '周一 09:15' },
            { type: 'bot', content: '会议议程：<br>1. 讨论新版培养方案<br>2. 分享教学经验和创新实践<br>3. 讨论下学期的教学安排', time: '周一 09:15' },
            { type: 'user', content: '收到，我会参加', time: '周一 09:30' }
        ]
    }
])

const searchQuery = ref('')
const currentChatIndex = ref(0)
const messageContainer = ref(null)

// 计算属性 - 过滤对话列表
const filteredChats = computed(() => {
    if (!searchQuery.value.trim()) return chatHistory.value
    
    const query = searchQuery.value.toLowerCase().trim()
    return chatHistory.value.filter(chat => 
        chat.name.toLowerCase().includes(query) || 
        chat.preview.toLowerCase().includes(query)
    )
})

// 当前选中的对话
const currentChat = computed(() => {
    if (currentChatIndex.value === null || !chatHistory.value.length) return null
    return chatHistory.value[currentChatIndex.value]
})

// 选择对话
const selectChat = (index) => {
    currentChatIndex.value = index
    scrollToBottom()
}

// 格式化消息内容（支持HTML）
const formatMessage = (content) => {
    return content
}

// 滚动到底部
const scrollToBottom = () => {
    nextTick(() => {
        if (messageContainer.value) {
            messageContainer.value.scrollTop = messageContainer.value.scrollHeight
        }
    })
}

onMounted(() => {
    scrollToBottom()
})
</script>

<style scoped lang="scss">
.doubao-communication-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    overflow: hidden;
}

.doubao-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 60px;
    padding: 0 20px;
    background-color: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    
    .dark & {
        background-color: #1e293b;
        border-color: #334155;
    }
    
    .doubao-logo {
        display: flex;
        align-items: center;
        
        .doubao-icon {
            font-size: 24px;
            font-weight: bold;
            color: #6366f1;
            margin-right: 8px;
        }
        
        .doubao-title {
            font-size: 18px;
            color: #334155;
            
            .dark & {
                color: #f8fafc;
            }
        }
    }
    
    .doubao-user-menu {
        display: flex;
        align-items: center;
        
        .doubao-username {
            margin-left: 8px;
            color: #334155;
            
            .dark & {
                color: #f8fafc;
            }
        }
    }
}

.doubao-main-content {
    display: flex;
    flex: 1;
    overflow: hidden;
}

.doubao-chat-history {
    flex: 0 0 320px;
    border-right: 1px solid #e2e8f0;
    display: flex;
    flex-direction: column;
    
    .dark & {
        border-color: #334155;
    }
    
    .doubao-search-box {
        padding: 12px;
        border-bottom: 1px solid #e2e8f0;
        
        .dark & {
            border-color: #334155;
        }
        
        .el-input {
            --el-input-bg-color: rgba(248, 250, 252, 0.8);
            --el-input-border-color: #e2e8f0;
            
            .dark & {
                --el-input-bg-color: rgba(30, 41, 59, 0.8);
                --el-input-border-color: #334155;
                --el-input-text-color: #f8fafc;
            }
        }
    }
    
    .doubao-chat-items {
        flex: 1;
        overflow-y: auto;
        padding: 8px 0;
        
        .doubao-chat-item {
            display: flex;
            padding: 12px;
            cursor: pointer;
            transition: background-color 0.2s ease;
            
            &:hover {
                background-color: rgba(241, 245, 249, 0.5);
            }
            
            &.active {
                background-color: rgba(99, 102, 241, 0.1);
                
                .doubao-chat-name {
                    color: #6366f1;
                }
            }
            
            .doubao-chat-info {
                margin-left: 12px;
                flex: 1;
                overflow: hidden;
                
                .doubao-chat-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    
                    .doubao-chat-name {
                        font-size: 15px;
                        font-weight: 500;
                        color: #1e293b;
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        
                        .dark & {
                            color: #f8fafc;
                        }
                    }
                    
                    .doubao-chat-time {
                        font-size: 12px;
                        color: #94a3b8;
                        
                        .dark & {
                            color: #64748b;
                        }
                    }
                }
                
                .doubao-chat-preview {
                    font-size: 13px;
                    color: #64748b;
                    margin-top: 4px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    
                    .dark & {
                        color: #94a3b8;
                    }
                }
            }
        }
    }
}

.doubao-chat-container {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.doubao-chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 60px;
    padding: 0 20px;
    border-bottom: 1px solid #e2e8f0;
    
    .dark & {
        border-color: #334155;
        background-color: #1e293b;
    }
    
    h2 {
        font-size: 18px;
        font-weight: 500;
        color: #1e293b;
        
        .dark & {
            color: #f8fafc;
        }
    }
}

.doubao-messages {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    background-color: #f8fafc;
    
    .dark & {
        background-color: #0f172a;
    }
    
    .doubao-empty-chat {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .doubao-system-message {
        text-align: center;
        margin-bottom: 16px;
        
        p {
            display: inline-block;
            background-color: #e2e8f0;
            color: #64748b;
            font-size: 12px;
            padding: 4px 12px;
            border-radius: 12px;
            
            .dark & {
                background-color: #1e293b;
                color: #94a3b8;
            }
        }
    }
    
    .doubao-message {
        display: flex;
        margin-bottom: 16px;
        
        .doubao-message-content {
            max-width: 70%;
            margin: 0 12px;
            
            .doubao-message-bubble {
                padding: 10px 16px;
                border-radius: 16px;
                font-size: 14px;
                line-height: 1.6;
                position: new;
                
                img {
                    max-width: 100%;
                    border-radius: 8px;
                }
                
                a {
                    color: #6366f1;
                    text-decoration: underline;
                    
                    &:hover {
                        color: #4f46e5;
                    }
                }
            }
            
            .doubao-message-time {
                font-size: 12px;
                color: #94a3b8;
                margin-top: 4px;
                display: block;
                text-align: right;
            }
        }
    }
    
    .doubao-user-message {
        justify-content: flex-end;
        
        .doubao-message-bubble {
            background-color: #6366f1;
            color: white;
        }
    }
    
    .doubao-bot-message {
        justify-content: flex-start;
        
        .doubao-message-bubble {
            background-color: white;
            color: #1e293b;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            
            .dark & {
                background-color: #1e293b;
                color: #f8fafc;
            }
        }
    }
}

.doubao-input-area {
    padding: 16px;
    border-top: 1px solid #e2e8f0;
    background-color: white;
    
    .dark & {
        border-color: #334155;
        background-color: #1e293b;
    }
    
    .doubao-message-input {
        --el-input-bg-color: rgba(248, 250, 252, 0.8);
        --el-input-border-color: #e2e8f0;
        
        .dark & {
            --el-input-bg-color: rgba(30, 41, 59, 0.8);
            --el-input-border-color: #334155;
            --el-input-text-color: #f8fafc;
        }
    }
    
    .doubao-input-actions {
        display: flex;
        margin-top: 8px;
        
        .el-button {
            color: #64748b;
            
            &:hover {
                color: #6366f1;
            }
            
            .dark & {
                color: #94a3b8;
                
                &:hover {
                    color: #818cf8;
                }
            }
        }
    }
}
</style>