# 🎓 Lucky_SMSS 来财学生信息管理系统（后端）

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Gitee Stars](https://gitee.com/Yangshengzhou/lucky-smss-backend/badge/star.svg?theme=dark)](https://gitee.com/Yangshengzhou/lucky-smss-backend) 

## 视频介绍 🎥

🔗 [系统视频演示链接](https://www.bilibili.com/video/BV1byVsziErM/)

## 概述 🌟

Lucky_SMSS 是一个基于 **Vue 3** 和 **Spring Boot** 构建的学生信息管理系统 📚，支持以下三种角色：

- 👨‍🎓 学生  
- 👩‍🏫 教师  
- 🧑‍💼 管理员  

该项目完全由我一人手搓开发完成 💻，适合用于课程设计、毕业设计以及商业用途 💼。2025 年五一假期独立开发，目标是为教学场景提供一个 **结构清晰、易于扩展、界面友好** 的开源系统 🌱。

无论是新手还是有经验的开发者，都可以轻松上手，并根据需求进行二次开发 🛠️！

🖼️ ![来财预览图](PreviewPictures/1.png)

---

## 目录 📚

📌 点击快速跳转👇

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [系统架构](#系统架构)
- [工作原理](#工作原理)
- [环境准备](#环境准备)
- [获取代码](#获取代码)
- [数据库初始化](#数据库初始化)
- [后端服务启动](#后端服务启动)
- [JWT 的使用](#jwt-的使用)
- [CORS 问题解决](#cors-问题解决)
- [前端应用构建与部署](#前端应用构建与部署)
- [常见问题](#常见问题)
- [参与贡献](#参与贡献)
- [联系作者](#联系作者)

---

## 功能特性 🧰

✅ 支持多角色权限管理  
✅ 用户登录注册 + 修改密码 + 头像上传  
✅ 学生成绩查看 / 教师成绩录入  
✅ 教师发布任务 / 学生提交作业  
✅ 基础个人信息维护  
⏳ 未来计划：选课系统、忘记密码、邮件验证码等

🖼️ ![来财预览图](PreviewPictures/2.png)

---

## 技术栈 🛠️

### 前端 🖥️
- **Vue 3** – Composition API 新体验
- **TypeScript** – 强类型更安全
- **Element Plus** – Vue 3 UI 组件库
- **Axios** – 发送 HTTP 请求
- **Vue Router** – 实现页面跳转

### 后端 ⚙️
- **Spring Boot** – 快速搭建 Java Web 应用
- **JWT** – 安全身份认证
- **MyBatis** – ORM 数据访问
- **Swagger UI** – 自动生成接口文档
- **Maven** – 依赖管理工具

### 数据库 🗃️
- **MySQL 8.0+** – 高性能关系型数据库

---

## 系统架构 🏗️

前后端分离架构，便于维护和拓展 🔄：

```
+------------------+        +-------------------+
|    Frontend      | <----> |     Backend       |
| (Vue 3, Element+)        | (Spring Boot, JWT) |
+------------------+        +-------------------+
          |                          |
          v                          v
   +-------------+            +--------------+
   | Web Browser |            |    MySQL     |
   +-------------+            +--------------+
```

---

## 文件目录结构 🗂️

以下是 `Yangshengzhou/Lucky_SMSS` 项目的基本文件目录结构及其作用说明：

```
Yangshengzhou/Lucky_SMSS/
├── .mvn/wrapper/          # Maven Wrapper 相关文件，用于确保所有开发者使用相同的 Maven 版本
│   ├── mvnw               # Linux/Mac 下的 Maven Wrapper 脚本
│   └── mvnw.cmd           # Windows 下的 Maven Wrapper 脚本
│   └── maven-wrapper.jar  # Maven Wrapper 的 jar 包
│   └── maven-wrapper.properties  # Maven Wrapper 配置文件
├── PreviewPictures/        # 存放系统预览图片
│   └── ...                 # 各种预览图
├── src/                    # 后端代码主目录
│   ├── main/               # 主要源码和资源
│   │   ├── java/           # Java 源代码
│   │   └── resources/      # 配置文件、静态资源等
│   └── test/               # 测试代码
├── .gitattributes          # Git 属性配置文件
├── .gitignore              # Git 忽略规则文件，定义哪些文件不需要纳入版本控制
├── LICENSE                 # 许可证文件
├── README.md               # 项目文档，包含安装指南、使用说明等
├── pom.xml                 # Maven 项目的构建配置文件，管理依赖关系和插件配置
├── 建数据库.sql            # 初始化数据库脚本
└── mvnw                    # Linux/Mac 下的 Maven Wrapper 脚本 (重复项)
└── mvnw.cmd                # Windows 下的 Maven Wrapper 脚本 (重复项)
```

## 工作原理 🔁

1. **用户登录/注册** → 前端将账号密码发送给后端 📤
2. **生成 JWT Token** → 登录成功返回加密 Token 🔐
3. **携带 Token 请求** → 所有请求带上 Token 访问接口
4. **权限控制** → 后端解析角色，返回对应数据
5. **数据持久化** → MyBatis 操作 MySQL 数据库 🗃️

🖼️ ![来财预览图](PreviewPictures/3.png)

---

## 环境准备 🧪

请确保本地已安装以下环境 ✅：

| 工具         | 版本要求     |
|--------------|--------------|
| Node.js      | `v16.x` 或以上 📦 |
| Java         | `JDK 17+` ☕ |
| Maven        | `3.8.x+` 📦 |
| MySQL        | `8.0+` 🗃️ |
| Git          | 最新版本 🔄 |

💡 国内推荐配置镜像加速下载：

#### Maven 镜像设置（`~/.m2/settings.xml`）：
```xml
<mirrors>
    <mirror>
        <id>aliyun-central</id>
        <mirrorOf>central</mirrorOf>
        <name>Aliyun Central</name>
        <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
</mirrors>
```

#### NPM 镜像设置：
```bash
npm config set registry https://registry.npmmirror.com
```

🖼️ ![来财预览图](PreviewPictures/4.png)

---

## 获取代码 📂

```bash
# 克隆仓库到本地
git clone https://gitee.com/Yangshengzhou/lucky-smss-backend.git

# 进入项目根目录
cd lucky-smss-backend
```

⚠️ 注意：前端和后端需分别运行，请参考各自说明部署。

---

## 数据库初始化 🗃️

进入 `back-end` 分支并执行 SQL 脚本初始化数据库：
```bash
mysql -u root -p < 创建数据库.sql
```

### 修改数据库连接配置：

编辑文件路径：`back-end/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:数据库端口号/lucky_smss?useSSL=false&serverTimezone=UTC
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
```

🖼️ ![来财预览图](PreviewPictures/5.png)

---

## 后端服务启动 ⚡

切换到 back-end 目录并运行：

```bash
mvn clean package
java -jar target/lucky-smss.jar
```

默认运行地址：http://localhost:8090 🌐

如需修改端口，在 `application.properties` 中调整：
```properties
server.port=自定义端口号
```

🖼️ ![来财预览图](PreviewPictures/6.png)

---

## JWT 的使用 🔐

登录成功后生成 Token，包含用户 ID、角色、过期时间等信息。

Token 加密方式为 HS512，密钥长度必须大于 512 位：

```properties
# This is key and it must be 512 Bit
jwt.secret=ZheGeShiXiYangShengZhouZai2025NianWuYiJiaQiXieDedeZheGe512WeiDeDeMiYueOWoZenJiaoFuLeBiuBiu
jwt.expiration=86400
```

---

## CORS 问题解决 🌐

前后端跨域时，可在 Controller 上加注解：

```java
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {
    ...
}
```

或通过全局配置类处理。

---

## 前端应用构建与部署 🛠️

进入 `front-end` 目录：

```bash
npm install
npm run build
```

### 部署建议：

- **Nginx**：将 `dist/` 文件夹复制到 Nginx 根目录
- **Docker**：创建 Dockerfile 使用 Nginx 容器部署

### 修改前端端口：

编辑 `vue.config.js`：

```js
module.exports = {
  devServer: {
    port: 3000
  }
}
```

🖼️ ![来财预览图](PreviewPictures/7.png)

---

## 常见问题 ❓

🔍 **Q1：缺少依赖怎么办？**  
A：更换源，使用阿里云镜像或淘宝 npm 镜像。

🔍 **Q2：如何切换角色？**  
A：当前为模拟登录，可直接访问 `/student-index` 或 `/teacher-index` 页面。

🔍 **Q3：忘记密码功能没实现？**  
A：正在规划中，后续将通过邮箱验证码实现。

🔍 **Q4：API 地址在哪改？**  
A：修改 `src/api/index.ts` 中的 `BASE_URL` 即可。

🔍 **Q5：哪些功能待完善？**  
A：目前未实现的功能包括：忘记密码、邮件验证、选课系统等，欢迎 PR 提交 🙌！

---

## 参与贡献 🤝

欢迎任何形式的贡献 🙏！无论你是提交 Issue、PR，还是优化文档，都非常欢迎！

请阅读 [CONTRIBUTING.md](CONTRIBUTING.md) 获取更多信息 📘。

---

## 联系作者 📬

- Gitee / GitHub：[@yangshengzhou](https://gitee.com/yangshengzhou)  
- Email：yangsz03@foxmail.com 📧

---

🎉 **感谢您的关注和支持！如果你喜欢这个项目，请不要吝啬你的 Star⭐ 和 Fork🔄！**

🌟 **Star** 它表示你的认可和支持 🤝  
🔄 **Fork** 它意味着你可以自由地修改、学习和分享 🧭  
💬 **Issue** 它是你提出建议和反馈的地方 🗣️  
🧩 **PR** 它是我们一起完善项目的桥梁 🤝

---

> 程序员的理想是改变世界，但现实是先得养活自己。“来财”这个名字，既是对项目的期许，也是对每一位使用者的祝福：学有所成，用有所得，早“来财”，早自由！🎓  
我们相信开源的价值不仅在于分享，更在于共建与成长。无论你是学习中遇到困难，还是希望为项目添砖加瓦，都欢迎随时加入我们。让我们一起把“来财”打造成一个真正有用、有温度、还能带来机会的好项目！😊

---