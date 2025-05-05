# 🎓 Lucky_SMSS（来财学生信息管理系统）

一个基于 Vue 3 和 Spring Boot 构建的学生信息管理系统，支持学生、教师及管理员三种角色操作。😊

## 目录

- [项目简介](#项目简介)
- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [环境准备](#环境准备)
- [获取代码](#获取代码)
- [数据库初始化](#数据库初始化)
  - [手动调整数据库配置](#手动调整数据库配置)
- [后端服务启动](#后端服务启动)
  - [手动调整后端端口](#手动调整后端端口)
- [前端应用构建与部署](#前端应用构建与部署)
  - [手动调整前端端口](#手动调整前端端口)
- [常见问题](#常见问题)
- [参与贡献](#参与贡献)
- [联系作者](#联系作者)

## 项目简介

Lucky_SMSS 是在2025年五一假期期间开发的一个开源项目，旨在为教学场景提供一个简单易用的学生信息管理系统。系统包括登录注册、权限控制、个人信息管理、课程管理等功能模块。🎉

## 功能特性

- **多角色支持**：学生、教师、管理员不同权限。
- **基础信息管理**：个人资料修改、头像上传等。
- **课程管理**：查看课程详情、选课（未来计划）。
- **任务管理**：发布任务、完成情况跟踪。
- **成绩录入**：教师录入学生成绩，学生查看成绩。

## 技术栈

### 前端
- Vue 3
- TypeScript
- Element Plus
- Axios
- Vue Router

### 后端
- Spring Boot
- JWT
- MyBatis
- Swagger UI (API 文档)

### 数据库
- MySQL

## 环境准备

确保已安装以下软件：
- Node.js (v16+)
- Java 17+
- Maven (3.8.x+)
- MySQL (8.0+)

如果你在中国，建议将 Maven 的中央仓库替换为阿里云镜像，以加快依赖下载速度。可以在 `~/.m2/settings.xml` 中添加如下配置：

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

## 获取代码

首先克隆仓库到本地：

    git clone https://gitee.com/yangshengzhou/lucky-ssms.git
    cd lucky-ssms

## 数据库初始化

进入 back-end 分支执行 SQL 脚本初始化数据库：

    mysql -u root -p < 创建数据库.sql

### 手动调整数据库配置

编辑 `back-end/src/main/resources/application.properties` 文件，修改以下配置项以匹配您的数据库设置：

    spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name?useSSL=false&serverTimezone=UTC
    spring.datasource.username=your_db_username
    spring.datasource.password=your_db_password

请将 `your_database_name` 替换为您创建的数据库名称，`your_db_username` 和 `your_db_password` 替换为您的数据库用户名和密码。

## 后端服务启动

切换到 back-end 目录，并运行以下命令来启动 Spring Boot 应用：

    mvn clean package
    java -jar target/your-application-name.jar

或者直接使用 Maven 插件运行应用：

    mvn spring-boot:run

默认情况下，Spring Boot 应用将在 http://localhost:8090 上运行。如果需要更改端口号，请编辑 `application.properties` 文件中的 `server.port` 属性，如下所示：

    server.port=自定义端口号

### 手动调整后端端口

如果您想更改后端服务的端口号，请在 `back-end/src/main/resources/application.properties` 文件中找到并修改 `server.port` 参数：

    server.port=自定义端口号

## 前端应用构建与部署

### 安装依赖并构建

切换到 front-end 目录，安装所有必要的 npm 包：

    npm install

然后，构建生产版本的 Vue 应用：

    npm run build

这将在 `dist` 目录下生成静态文件。

### 部署前端应用

您可以选择多种方式部署前端应用，这里提供两种常见的方法：

- **使用 Nginx**：将 `dist` 目录下的所有文件复制到 Nginx 的 HTML 目录中，并根据需要调整 Nginx 配置文件。示例 Nginx 配置如下：

      server {
          listen       80;
          server_name  localhost;

          location / {
              root   /path/to/dist/;
              index  index.html index.htm;
              try_files $uri $uri/ /index.html;
          }
      }

- **使用 Docker**：如果您更倾向于容器化部署，可以创建一个 Dockerfile 并基于 Nginx 或其他 Web 服务器来部署前端应用。

#### 手动调整前端端口

如果您想更改前端开发服务器的端口号，可以编辑 `front-end/vue.config.js` 文件，添加或修改以下配置：

    devServer: {
        port: 自定义端口号
    }

## 常见问题

### 第一次部署时缺少依赖

如果您是第一次部署该项目，可能会遇到缺少依赖的情况。确保您已经正确安装了所有依赖包：

- 对于前端，请确保在 `front-end` 目录下运行 `npm install`。
- 对于后端，请确保在 `back-end` 目录下运行 `mvn clean install`。如果在中国，记得替换 Maven 中央仓库为阿里云镜像以加快依赖下载速度。

### 其他问题

如果您遇到其他问题或有任何疑问，请随时通过 GitHub/Gitee 提交 Issue 或 PR。

## 参与贡献

欢迎任何形式的贡献！请阅读 CONTRIBUTING.md 获取更多信息。🤝

## 联系作者

如果有任何问题或建议，请通过以下方式联系我：

- GitHub/Gitee: [@yangshengzhou](https://gitee.com/yangshengzhou)
- Email: your.email@example.com

> 项目名称“来财”寓意吸引人才、培育英才。🎓

---

希望这份 README 文件能够帮助用户更好地理解和部署 Lucky_SMSS 项目，同时也为开发者提供了足够的指导和支持。 😊