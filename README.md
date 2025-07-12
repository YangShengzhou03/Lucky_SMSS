# 🎓 Lucky_SMSS 来财学生信息管理系统

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0) 
![GitHub Stars](https://img.shields.io/github/stars/YangShengzhou03/Lucky_SMSS?style=social)
[![Gitee Stars](https://gitee.com/Yangshengzhou/lucky-smss-backend/badge/star.svg?theme=dark)](https://gitee.com/Yangshengzhou/lucky-smss-backend)
[![Gitee Fork](https://gitee.com/Yangshengzhou/lucky-smss-backend/badge/fork.svg?theme=dark)](https://gitee.com/Yangshengzhou/lucky-smss-backend)
[![Downloads](https://img.shields.io/badge/downloads-30K%2B-brightgreen)](https://github.com/YangShengzhou03/lucky-smss-backend/releases)

## 🎥 视频介绍
[系统演示视频](https://www.bilibili.com/video/BV1byVsziErM/) | 快速了解系统功能和操作流程

## 🌟 项目概述
Lucky_SMSS 是一个基于 **Vue 3** 和 **Spring Boot** 构建的学生信息管理系统，支持多角色管理：
- 👨‍🎓 学生  
- 👩‍🏫 教师  
- 🧑‍💼 管理员  

> 💻 **项目亮点**  
> 2025年五一假期独立开发完成，适合课程设计、毕业设计及商业用途  
> 提供**结构清晰、易于扩展、界面友好**的开源解决方案  
> 无论新手还是有经验开发者，都可轻松上手并进行二次开发  

![系统预览](PreviewPictures/1.png)

---

## 📚 目录导航
- [✨ 功能特性](#功能特性)
- [🛠️ 技术栈](#技术栈)
- [🏗️ 系统架构](#系统架构)
- [🔁 工作原理](#工作原理)
- [🧪 环境准备](#环境准备)
- [📥 获取代码](#获取代码)
- [🗃️ 数据库初始化](#数据库初始化)
- [⚡ 后端启动](#后端服务启动)
- [🔐 JWT 认证](#jwt-的使用)
- [🌐 CORS 配置](#cors-问题解决)
- [🚀 前端部署](#前端应用构建与部署)
- [❓ 常见问题](#常见问题)
- [🤝 参与贡献](#参与贡献)
- [📬 联系作者](#联系作者)

---

## ✨ 功能特性
### 核心功能
- ✅ **多角色权限管理**：学生/教师/管理员分级权限
- ✅ **账户管理**：登录注册 + 修改密码 + 头像上传
- ✅ **成绩管理**：学生查看成绩 / 教师录入成绩
- ✅ **作业管理**：教师发布任务 / 学生提交作业
- ✅ **信息维护**：基础个人信息管理

### 🚧 未来计划
- 选课系统
- 忘记密码功能
- 邮件验证码验证

![功能预览](PreviewPictures/2.png)

---

## 🛠️ 技术栈
### 前端技术
| 技术 | 作用 |
|------|------|
| Vue 3 | 核心框架 |
| TypeScript | 强类型支持 |
| Element Plus | UI组件库 |
| Axios | HTTP请求 |
| Vue Router | 路由管理 |

### 后端技术
| 技术 | 作用 |
|------|------|
| Spring Boot | 后端框架 |
| JWT | 安全认证 |
| MyBatis | ORM框架 |
| Swagger UI | 接口文档 |
| Maven | 依赖管理 |

### 数据库
- MySQL 8.0+ 高性能关系型数据库

---

## 🏗️ 系统架构
采用前后端分离架构，便于维护和扩展：

```
+------------------+        +-------------------+
|    前端应用      | <----> |     后端服务      |
| (Vue 3, Element+)        | (Spring Boot, JWT) |
+------------------+        +-------------------+
          |                          |
          v                          v
   +-------------+            +--------------+
   |   浏览器    |            |   MySQL数据库 |
   +-------------+            +--------------+
```

---

## 📂 文件目录结构
```
lucky-smss-backend/
├── .mvn/                  # Maven配置
├── PreviewPictures/       # 系统预览图
├── src/                   # 源代码
│   ├── main/              # 主代码
│   │   ├── java/          # Java源码
│   │   └── resources/     # 配置文件
│   └── test/              # 测试代码
├── .gitattributes         # Git配置
├── .gitignore             # Git忽略规则
├── LICENSE                # 许可证
├── README.md              # 项目文档
├── pom.xml                # Maven配置
└── 建数据库.sql           # 数据库初始化脚本
```

---

## 🔁 工作原理
1. **用户认证** → 前端发送账号密码至后端
2. **Token生成** → 登录成功返回JWT Token
3. **请求验证** → 所有API请求携带Token
4. **权限控制** → 后端解析角色返回对应数据
5. **数据持久化** → MyBatis操作MySQL数据库

![系统流程](PreviewPictures/3.png)

---

## 🧪 环境准备
### 必备工具
| 工具       | 版本要求       |
|------------|----------------|
| Node.js    | ≥ v16.x        |
| Java       | ≥ JDK 17       |
| Maven      | ≥ 3.8.x        |
| MySQL      | ≥ 8.0          |
| Git        | 最新版         |

### 🚀 加速配置
#### Maven镜像配置（~/.m2/settings.xml）
```xml
<mirror>
  <id>aliyun-central</id>
  <mirrorOf>central</mirrorOf>
  <name>Aliyun Central</name>
  <url>https://maven.aliyun.com/repository/central</url>
</mirror>
```

#### NPM镜像配置
```bash
npm config set registry https://registry.npmmirror.com
```

![环境配置](PreviewPictures/4.png)

---

## 📥 获取代码
```bash
# 克隆仓库
git clone https://gitee.com/Yangshengzhou/lucky-smss-backend.git

# 进入项目目录
cd lucky-smss-backend
```

> ⚠️ 注意：前端和后端需分别部署运行

---

## 🗃️ 数据库初始化
1. 执行SQL脚本初始化数据库：
```bash
mysql -u root -p < 建数据库.sql
```

2. 修改数据库连接配置（`src/main/resources/application.properties`）：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lucky_smss?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

![数据库配置](PreviewPictures/5.png)

---

## ⚡ 后端服务启动
```bash
# 打包项目
mvn clean package

# 启动服务
java -jar target/lucky-smss.jar
```

默认访问地址：http://localhost:8090

### 自定义端口
修改 `application.properties`：
```properties
server.port=8080  # 修改为所需端口
```

![服务启动](PreviewPictures/6.png)

---

## 🔐 JWT 认证
### 配置参数（application.properties）
```properties
# 512位密钥
jwt.secret=YourSecretKeyOver512BitsLong
jwt.expiration=86400  # Token有效期(秒)
```

### Token包含信息
- 用户ID
- 用户角色
- 过期时间
- 签发时间

---

## 🌐 CORS 配置
### 单控制器配置
```java
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {...}
```

### 全局配置
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

---

## 🚀 前端应用构建与部署
### 开发模式
```bash
npm install   # 安装依赖
npm run dev   # 启动开发服务器
```

### 生产构建
```bash
npm run build  # 生成dist目录
```

### 部署方案
| 方案 | 说明 |
|------|------|
| Nginx | 部署dist目录到web服务器 |
| Docker | 使用Nginx容器化部署 |
| 静态托管 | 上传到GitHub Pages/Vercel等 |

### 配置修改
`vue.config.js`:
```js
module.exports = {
  devServer: {
    port: 3000,       // 开发端口
    proxy: {
      '/api': {
        target: 'http://localhost:8090',  // 后端地址
        changeOrigin: true
      }
    }
  }
}
```

![前端预览](PreviewPictures/7.png)

---

## ❓ 常见问题
### Q1：依赖安装失败怎么办？
- 检查网络连接
- 更换镜像源（阿里云/淘宝npm镜像）
- 删除node_modules后重试

### Q2：如何切换用户角色？
当前支持直接访问路由切换：
- `/student-index` 学生界面
- `/teacher-index` 教师界面
- `/admin-index` 管理员界面

### Q3：忘记密码功能未实现？
该功能正在开发中，后续将通过邮箱验证码实现密码重置

### Q4：如何修改API地址？
编辑 `src/api/index.ts`：
```ts
const BASE_URL = 'http://your-new-api-address:port';
```

### Q5：待开发功能有哪些？
- 选课系统
- 邮件验证
- 忘记密码
- 更多扩展模块

> 💡 欢迎提交PR贡献代码！

---

## 🤝 参与贡献
我们欢迎各种形式的贡献：
- 🐛 提交Bug报告
- 💡 提出新功能建议
- 📝 改进文档
- 💻 提交代码PR

请阅读 [ CONTRIBUTING.md](https://github.com/YangShengzhou03/Lucky_SMSS/blob/Back-End/%20CONTRIBUTING.md) 了解贡献指南

---

## 📬 联系作者
- **GitHub**: [@yangshengzhou](https://github.com/YangShengzhou03)
- **Gitee**: [@yangshengzhou](https://gitee.com/Yangshengzhou)
- **邮箱**: yangsz03@foxmail.com
- **B站**: [@yangshengzhou](https://space.bilibili.com/652391792)
- **社区交流**：  
  ![WeChat Group](https://img.shields.io/badge/微信-YSZFortune-brightgreen?logo=wechat)  
  ![QQ Group](https://img.shields.io/badge/QQ群-1021471813-blue?logo=tencentqq)  

---

<div align="center">
  <h3>🎉 感谢您的支持！</h3>
  <p>
    ⭐ <strong>Star</strong> 表示你的认可<br>
    🔄 <strong>Fork</strong> 自由修改和扩展<br>
    💬 <strong>Issue</strong> 提出建议和反馈<br>
    🧩 <strong>PR</strong> 共同完善项目
  </p>
  
  <blockquote>
  <p>📌 "来财"寓意学有所成，用有所得</p>
  <p>🌟 开源的价值在于共建共享，欢迎加入我们！</p>
  </blockquote>
</div>