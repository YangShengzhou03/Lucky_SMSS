<template>
    <div class="edu-communication-container">
        <div class="edu-main-content">
            <!-- 联系人列表区域 -->
            <div class="edu-contact-list">
                <div class="edu-search-box">
                    <el-input v-model="searchQuery" placeholder="搜索对话" clearable>
                        <template #prefix>
                            <el-icon>
                                <Search />
                            </el-icon>
                        </template>
                    </el-input>
                </div>

                <div class="edu-contact-items">
                    <div v-for="(chat, index) in filteredChats" :key="index"
                        :class="['edu-contact-item', { 'active': currentChatIndex === index }]"
                        @click="selectChat(index)">
                        <el-avatar size="40" :src="chat.avatar" :alt="chat.name"></el-avatar>
                        <div class="edu-contact-info">
                            <div class="edu-contact-header">
                                <h3 class="edu-contact-name">{{ chat.name }}</h3>
                                <span class="edu-contact-time">{{ chat.time }}</span>
                            </div>
                            <p class="edu-contact-preview">{{ chat.preview }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 聊天内容区域 -->
            <div class="edu-chat-container">
                <div class="edu-chat-header">
                    <h2>{{ currentChat ? currentChat.name : '选择一个对话' }}</h2>
                    <div class="edu-chat-actions">
                        <el-button type="text" icon="More" class="operation-btn">
                            <el-icon>
                                <More />
                            </el-icon>
                        </el-button>
                    </div>
                </div>

                <div class="edu-chat-content-wrapper">
                    <div class="edu-messages" ref="messageContainer">
                        <div v-if="!currentChat" class="edu-empty-chat">
                            <el-empty description="请选择一个对话开始交流"></el-empty>
                        </div>

                        <div v-else>
                            <!-- 系统消息 -->
                            <div class="edu-system-message" v-if="currentChat.systemMessage">
                                <p>{{ currentChat.systemMessage }}</p>
                            </div>

                            <!-- 消息区域 -->
                            <div v-for="(message, msgIndex) in currentChat.messages" :key="msgIndex"
                                :class="['edu-message', message.type === 'user' ? 'edu-user-message' : 'edu-bot-message']">
                                <el-avatar v-if="message.type === 'bot'" size="36"
                                    src="https://picsum.photos/id/237/200/200" alt="师生头像"></el-avatar>

                                <div class="edu-message-content">
                                    <div class="edu-message-bubble" v-html="formatMessage(message.content)"></div>
                                    <span class="edu-message-time">{{ message.time }}</span>
                                </div>

                                <el-avatar v-if="message.type === 'user'" size="36"
                                    src="https://picsum.photos/id/64/200/200" alt="用户头像"></el-avatar>
                            </div>
                        </div>
                    </div>

                    <!-- 消息输入区域 -->
                    <div class="edu-input-area" v-if="currentChat">
                        <div class="edu-message-input-wrapper">
                            <el-input v-model="newMessage" type="textarea" :rows="3" placeholder="输入消息..."
                                @keyup.enter.exact.prevent="sendMessage" resize="none">
                            </el-input>
                            <div class="edu-input-actions">
                                <el-button type="text" icon="Picture" class="operation-btn">
                                    <el-icon>
                                        <Picture />
                                    </el-icon>
                                </el-button>
                                <el-button type="text" icon="VideoCamera" class="operation-btn">
                                    <el-icon>
                                        <VideoCamera />
                                    </el-icon>
                                </el-button>
                                <el-button type="text" icon="File" class="operation-btn">
                                    <el-icon>
                                        <File />
                                    </el-icon>
                                </el-button>
                                <el-button :disabled="!newMessage.trim()" type="primary" @click="sendMessage"
                                    class="btn-submit">
                                    发送
                                </el-button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed, watch } from 'vue'
import {
    Search,
    More,
    Picture,
    VideoCamera,
    File
} from '@element-plus/icons-vue'

const chatHistory = ref([
    {
        id: 1,
        name: '师生助手',
        avatar: 'https://picsum.photos/id/237/200/200',
        time: '10:25',
        preview: '你好！我是师生助手，有什么可以帮助你的吗？',
        systemMessage: '今天10:20: 师生助手加入了对话',
        messages: [
            { type: 'bot', content: '你好！我是师生助手，有什么可以帮助你的吗？', time: '10:25' },
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
            { type: 'bot', content: '<a href="#" class="view-details-btn" @click.prevent="downloadFile">下学期课程安排.xlsx</a>', time: '昨天 16:45' },
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
const newMessage = ref('')

const filteredChats = computed(() => {
    if (!searchQuery.value.trim()) return chatHistory.value

    const query = searchQuery.value.toLowerCase().trim()
    return chatHistory.value.filter(chat =>
        chat.name.toLowerCase().includes(query) ||
        chat.preview.toLowerCase().includes(query)
    )
})

const currentChat = computed(() => {
    if (currentChatIndex.value === null || !chatHistory.value.length) return null
    return chatHistory.value[currentChatIndex.value]
})

const selectChat = (index) => {
    currentChatIndex.value = index
    nextTick(() => {
        scrollToBottom()
    })
}

const formatMessage = (content) => {
    return content
}

const sendMessage = () => {
    if (!currentChat.value || !newMessage.value.trim()) return

    const message = {
        type: 'user',
        content: newMessage.value.trim(),
        time: formatTime(new Date())
    }

    currentChat.value.messages.push(message)
    currentChat.value.preview = message.content.length > 30 ?
        message.content.substring(0, 30) + '...' : message.content
    currentChat.value.time = message.time
    newMessage.value = ''

    nextTick(() => {
        scrollToBottom()
    })

    if (currentChat.value.id === 1) {
        setTimeout(() => {
            const replies = [
                "非常感谢你的提问，我会尽快为你解答。",
                "我正在查看相关资料，请稍等片刻。",
                "这个问题很好，我认为可以从以下几个方面来考虑...",
                "根据最新的教学指南，我们应该...",
                "我已经将你的问题记录下来，会在下次会议上提出讨论。"
            ]

            const randomReply = replies[Math.floor(Math.random() * replies.length)]

            const botMessage = {
                type: 'bot',
                content: randomReply,
                time: formatTime(new Date())
            }

            currentChat.value.messages.push(botMessage)
            currentChat.value.preview = randomReply.length > 30 ?
                randomReply.substring(0, 30) + '...' : randomReply
            currentChat.value.time = botMessage.time

            nextTick(() => {
                scrollToBottom()
            })
        }, 800)
    }
}

const formatTime = (date) => {
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${hours}:${minutes}`
}

const scrollToBottom = () => {
    if (messageContainer.value) {
        messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
}

// 监听消息变化自动滚动到底部
watch(() => currentChat.value?.messages.length, () => {
    scrollToBottom()
})

onMounted(() => {
    scrollToBottom()
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
    --shadow-light: 0 1px 3px rgba(0, 0, 0, 0.1);
    --input-area-height: auto;
}

.dark {
    --text-primary: #ffffff;
    --text-secondary: rgba(255, 255, 255, 0.7);
    --text-tertiary: rgba(255, 255, 255, 0.5);
    --text-highlight: #66b1ff;
    --border-color: rgba(255, 255, 255, 0.1);
    --card-bg: rgba(30, 41, 59, 0.9);
    --card-hover-bg: rgba(51, 65, 85, 0.8);
}

.edu-communication-container {
    display: flex;
    flex-direction: column;
    height: 76vh;
    background-color: var(--card-hover-bg);
}

.edu-main-content {
    display: flex;
    flex: 1;
    height: calc(76vh - 60px);
    background-color: var(--card-bg);
    border-radius: 8px;
    box-shadow: var(--shadow-light);
    overflow: hidden;
}

.edu-contact-list {
    flex: 0 0 320px;
    border-right: 1px solid var(--border-color);
    display: flex;
    flex-direction: column;
    background-color: var(--card-bg);
    height: 100%;
    overflow: hidden;

    .edu-search-box {
        padding: 16px;
        border-bottom: 1px solid var(--border-color);

        .el-input {
            :deep(.el-input__wrapper) {
                border-radius: 18px;
                padding-left: 12px;
            }
        }
    }

    .edu-contact-items {
        flex: 1;
        overflow-y: auto;
        padding: 8px;

        .edu-contact-item {
            display: flex;
            padding: 12px;
            cursor: pointer;
            transition: all 0.2s ease;
            border-radius: 8px;
            margin-bottom: 4px;

            &:hover {
                background-color: var(--card-hover-bg);
            }

            &.active {
                background-color: rgba(64, 158, 255, 0.1);

                .edu-contact-name {
                    color: var(--text-highlight);
                    font-weight: 600;
                }
            }

            .edu-contact-info {
                margin-left: 12px;
                flex: 1;
                overflow: hidden;

                .edu-contact-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;

                    .edu-contact-name {
                        font-size: 15px;
                        font-weight: 500;
                        color: var(--text-primary);
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                    }

                    .edu-contact-time {
                        font-size: 12px;
                        color: var(--text-tertiary);
                    }
                }

                .edu-contact-preview {
                    font-size: 13px;
                    color: var(--text-secondary);
                    margin-top: 4px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                }
            }
        }
    }
}

.edu-chat-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden;
}

.edu-chat-header {
    flex-shrink: 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 64px;
    padding: 0 24px;
    border-bottom: 1px solid var(--border-color);

    h2 {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0;
    }
}

.edu-chat-content-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    position: relative;
}

.edu-messages {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    display: flex;
    flex-direction: column;
    scroll-behavior: smooth;
    min-height: 0;
    /* 修复flex滚动问题 */

    .edu-empty-chat {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .edu-system-message {
        text-align: center;
        margin-bottom: 24px;

        p {
            display: inline-block;
            background-color: rgba(64, 158, 255, 0.1);
            color: var(--text-highlight);
            font-size: 12px;
            padding: 6px 16px;
            border-radius: 16px;
        }
    }

    .edu-message {
        display: flex;
        margin-bottom: 16px;
        align-items: flex-start;

        .edu-message-content {
            max-width: 75%;
            display: flex;
            flex-direction: column;

            .edu-message-bubble {
                padding: 12px 16px;
                font-size: 14px;
                line-height: 1.6;
                position: relative;
                border-radius: 8px;

                .view-details-btn {
                    color: var(--text-highlight);
                    text-decoration: none;
                    cursor: pointer;

                    &:hover {
                        text-decoration: underline;
                    }
                }
            }

            .edu-message-time {
                font-size: 12px;
                color: var(--text-tertiary);
                margin-top: 6px;
                display: block;
            }
        }
    }

    .edu-user-message {
        justify-content: flex-end;

        .edu-message-content {
            align-items: flex-end;
            margin-right: 12px;

            .edu-message-bubble {
                background-color: var(--text-highlight);
                color: white;
                border-radius: 12px 12px 0 12px;
            }
        }
    }

    .edu-bot-message {
        justify-content: flex-start;

        .edu-message-content {
            align-items: flex-start;
            margin-left: 12px;

            .edu-message-bubble {
                background-color: var(--card-bg);
                color: var(--text-primary);
                box-shadow: var(--shadow-light);
                border-radius: 12px 12px 12px 0;
            }
        }
    }
}

.edu-input-area {
    flex-shrink: 0;
    padding: 16px;
    border-top: 1px solid var(--border-color);
    background-color: var(--card-bg);
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);

    .edu-message-input-wrapper {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .el-textarea {
            :deep(.el-textarea__inner) {
                border-radius: 8px;
                padding: 12px;
                resize: none;
                min-height: 80px;
                max-height: 150px;
                transition: height 0.2s ease;
            }
        }

        .edu-input-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .el-button {
                padding: 8px;
            }

            .btn-submit {
                margin-left: auto;
            }
        }
    }
}

.operation-btn {
    color: var(--text-secondary);

    &:hover {
        color: var(--text-highlight);
    }
}

.btn-submit {
    background-color: var(--text-highlight);
    border-color: var(--text-highlight);
    color: white;
    padding: 8px 16px !important;

    &:hover {
        background-color: #66b1ff;
        border-color: #66b1ff;
    }

    &:disabled {
        opacity: 0.6;
    }
}

/* 滚动条样式 */
::-webkit-scrollbar {
    width: 6px;
    height: 6px;
}

::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.05);
    border-radius: 3px;
}

::-webkit-scrollbar-thumb {
    background: rgba(0, 0, 0, 0.2);
    border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
    background: rgba(0, 0, 0, 0.3);
}

.dark {
    ::-webkit-scrollbar-track {
        background: rgba(255, 255, 255, 0.05);
    }

    ::-webkit-scrollbar-thumb {
        background: rgba(255, 255, 255, 0.2);
    }

    ::-webkit-scrollbar-thumb:hover {
        background: rgba(255, 255, 255, 0.3);
    }

    .edu-input-area {
        box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.2);
    }
}
</style>
