# 🎓 Lucky_SMSS 来财学生信息管理系统（后端）

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Gitee Stars](https://gitee.com/Yangshengzhou/lucky-smss-backend/badge/star.svg?theme=dark)](https://gitee.com/Yangshengzhou/lucky-smss-backend) 

## 视频介绍

🔗 [系统视频演示链接](https://www.bilibili.com/video/BV1byVsziErM/)

## 概述

Lucky_SMSS 是一个基于 Vue 3 和 Spring Boot 构建的学生信息管理系统，支持 **学生、教师、管理员** 三种角色操作。项目完全手搓开发，适合用于课程设计、毕业设计以及商业用途。

本项目于 2025 年五一假期独立开发完成，目标是为教学场景提供一个结构清晰、易于扩展、界面友好的开源系统。无论是新手还是有经验的开发者，都可以轻松上手，并根据需求进行二次开发。

![来财预览图](PreviewPictures/1.png)

## 目录

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

## 功能特性

- **多角色支持**：学生、教师、管理员不同权限。
- **基础信息管理**：个人资料修改、头像上传等。
- **课程管理**：查看课程详情、选课（未来计划）。
- **任务管理**：发布任务、完成情况跟踪。
- **成绩录入**：教师录入学生成绩，学生查看成绩。

![来财预览图](PreviewPictures/2.png)

## 技术栈

### 前端
- **Vue 3**: 最新一代的 Vue.js 框架，支持 Composition API 和更好的性能。
- **TypeScript**: 强类型语言，帮助减少错误，提高代码质量。
- **Element Plus**: 基于 Vue 3 的 UI 组件库，提供了丰富的组件供快速开发使用。
- **Axios**: HTTP 客户端，用于发送请求并与后端交互。
- **Vue Router**: Vue.js 的官方路由管理器，用于实现单页应用中的页面导航。

### 后端
- **Spring Boot**: 简化了新 Spring 应用的初始搭建以及开发过程。
- **JWT (JSON Web Token)**: 用于用户身份验证和授权，确保安全性。
- **MyBatis**: 一种持久层框架，简化了数据库访问代码的编写。
- **Swagger UI**: 提供了一套完整的接口文档工具，方便前后端联调。

### 数据库
- **MySQL**: 关系型数据库管理系统，适用于各种规模的应用程序。

## 系统架构

系统采用前后端分离的设计模式，前端使用 Vue 3 + Element Plus 实现用户界面，后端基于 Spring Boot 提供 RESTful API。JWT 用于用户身份验证和授权。

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

## 工作原理

1. 用户登录或注册：用户通过前端页面输入用户名和密码进行登录或注册操作。
2. 生成 JWT 并返回：登录成功后，后端生成 JWT 并返回给前端，前端将该 Token 存储在本地存储中。
3. 携带 Token 请求：在后续请求中，前端会在请求头中携带该 Token，后端通过解析 Token 来确认用户身份并执行相应操作。
4. 权限控制：不同的角色（学生、教师、管理员）拥有不同的权限，后端根据用户角色返回不同的数据和操作选项。

![来财预览图](PreviewPictures/3.png)

## 环境准备

为了顺利运行该项目，请确保您的计算机已安装以下软件：

- **Node.js (v16+)**: 前端开发环境，建议使用 Node Version Manager (nvm) 进行版本管理。
- **Java 17+**: 后端开发环境，推荐使用 JDK 17。
- **Maven (3.8.x+)**: Java 项目的构建工具，用于管理依赖关系。
- **MySQL (8.0+)**: 数据库管理系统，用于存储和管理应用程序的数据。

如果您在中国，建议将 Maven 的中央仓库替换为阿里云镜像，以加快依赖下载速度。可以在 `~/.m2/settings.xml` 中添加如下配置：
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

对于 npm 包的下载，您可以使用淘宝镜像源加速下载：
```bash
npm config set registry https://registry.npmmirror.com
```

![来财预览图](PreviewPictures/4.png)

## 获取代码

首先克隆仓库到本地：
```bash
git clone https://gitee.com/Yangshengzhou/lucky-smss-backend.git
cd lucky-smss-backend
```

请注意，您需要分别下载前端和后端代码，并按照各自的说明进行部署。

## 数据库初始化

进入 back-end 分支执行 SQL 脚本初始化数据库：
```bash
mysql -u root -p < 创建数据库.sql
```

### 手动调整数据库配置

编辑 `back-end/src/main/resources/application.properties` 文件，修改以下配置项以匹配您的数据库设置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lucky_smss?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=123456
```

请将 `3306` 替换为您实际数据库的端口号，请将 `lucky_smss` 替换为您创建的数据库名称，`root` 和 `123456` 替换为您的数据库用户名和密码。

![来财预览图](PreviewPictures/5.png)

## 后端服务启动

切换到 back-end 目录，并运行以下命令来启动 Spring Boot 应用：
```bash
mvn clean package
java -jar target/your-application-name.jar
```

或者直接使用 Maven 插件运行应用：
```bash
mvn spring-boot:run
```

默认情况下，Spring Boot 应用将在 http://localhost:8090 上运行。如果需要更改端口号，请编辑 `application.properties` 文件中的 `server.port` 属性，如下所示：
```properties
server.port=自定义端口号
```

![来财预览图](PreviewPictures/6.png)

### JWT 的使用

在我们的项目中，JWT 用于用户身份验证和授权。每当用户登录成功后，后端会生成一个包含用户ID和角色信息的 Token，并将其返回给前端。这个 Token 类似于一张电子门票，包含以下数据：
- 用户ID
- 角色信息（如学生、教师、管理员）
- 过期时间

Token 的过期时间和密钥可以在 `application.properties` 文件中找到并修改：
```properties
# This is key and it must be 512 Bit
jwt.secret=ZheGeShiXiYangShengZhouZai2025NianWuYiJiaQiXieDedeZheGe512WeiDeDeMiYueOWoZenJiaoFuLeBiuBiu
jwt.expiration=86400
```

这里的 `jwt.secret` 是用来加密 Token 的密钥（要求长度大于512字节），而 `jwt.expiration` 则是 Token 的过期时间（秒）。

### CORS 问题解决

CORS（跨域资源共享）问题是当你的前端和后端不在同一个域名下时遇到的问题。解决方法是在后端配置中允许特定域名访问。在 Spring Boot 中，可以通过添加注解 `@CrossOrigin` 或者在配置类中进行全局配置。

例如，在控制器类上添加 `@CrossOrigin(origins = "http://localhost:8080")`，这将允许来自 `http://localhost:8080` 的请求访问你的 API。

## 前端应用构建与部署

### 安装依赖并构建

切换到 front-end 目录，安装所有必要的 npm 包：
```bash
npm install
```

然后，构建生产版本的 Vue 应用：
```bash
npm run build
```

这将在 `dist` 目录下生成静态文件。

### 部署前端应用

您可以选择多种方式部署前端应用，这里提供两种常见的方法：

- **使用 Nginx**：将 `dist` 目录下的所有文件复制到 Nginx 的 HTML 目录中，并根据需要调整 Nginx 配置文件。示例 Nginx 配置如下：
```nginx
server {
    listen       80;
    server_name  localhost;

    location / {
        root   /path/to/dist/;
        index  index.html index.htm;
        try_files $uri $uri/ /index.html;
    }
}
```

- **使用 Docker**：如果您更倾向于容器化部署，可以创建一个 Dockerfile 并基于 Nginx 或其他 Web 服务器来部署前端应用。

### 手动调整前端端口

如果您想更改前端开发服务器的端口号，可以编辑 `front-end/vue.config.js` 文件，添加或修改以下配置：
```javascript
devServer: {
    port: 自定义端口号
}
```

![来财预览图](PreviewPictures/7.png)

## 常见问题

- **第一次部署时缺少依赖**
  如果您是第一次部署该项目，可能会遇到缺少依赖的情况。确保您已经正确安装了所有依赖包：
  - 对于前端，请确保在 `front-end` 目录下运行 `npm install`。如果网络较慢，可以尝试更换 npm 镜像源：
  ```bash
  npm config set registry https://registry.npmmirror.com
  ```
  - 对于后端，请确保在 `back-end` 目录下运行 `mvn clean install`。如果在中国，记得替换 Maven 中央仓库为阿里云镜像以加快依赖下载速度：
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

- **如何解决前后端端口不一致的问题？**
  由于前后端分离，前端和后端通常会运行在不同的端口上。在开发环境中，您需要确保前端能够正确地向后端发起请求。可以在前端项目的 `src/api/index.js` 文件中修改 API 请求的基础 URL：
  ```javascript
  export const BASE_URL = 'http://localhost:8090'; // 修改为后端实际运行的地址
  ```

- **忘记密码的逻辑未实现**
  由于时间原因，忘记密码的后端逻辑尚未实现。未来可以通过邮箱发送验证码的方式实现真实的验证登录。

- **数据库密码在哪改？**
  数据库密码可以在 `back-end/src/main/resources/application.properties` 文件中找到并修改：
  ```properties
  spring.datasource.password=your_db_password
  ```

- **目前后端哪些功能不完善等待你们完善**
  目前后端的一些功能如忘记密码、邮件验证码发送等功能尚未实现，期待大家的贡献和完善！

## 参与贡献

欢迎任何形式的贡献！请阅读 CONTRIBUTING.md 获取更多信息。

## 联系作者

如果有任何问题或建议，请通过以下方式联系我：

- GitHub/Gitee: [@yangshengzhou](https://gitee.com/yangshengzhou)
- Email: yangsz03@foxmail.com

> 程序员的理想是改变世界，但现实是先得养活自己。“来财”这个名字，既是对项目的期许，也是对每一位使用者的祝福：学有所成，用有所得，早“来财”，早自由！🎓

---