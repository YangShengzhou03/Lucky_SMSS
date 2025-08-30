# Lucky SMS 项目部署与使用指南

## 项目概述

Lucky SMS（来财学生管理系统）是一个基于Spring Boot + Vue 3的全栈学生管理系统，具备完整的用户权限管理、课程管理、选课抢课、成绩管理等功能。

## 技术栈

### 后端技术
- Spring Boot 3.4.5
- MyBatis 3.0.4
- MySQL 8.0
- Redis 7.0+
- Maven

### 前端技术
- Vue 3.2.13
- Element Plus 2.10.4
- Vue Router 4.5.1
- ECharts 5.6.0

## 环境要求

### 必需软件
1. **JDK 17** - [下载地址](https://adoptium.net/)
2. **Node.js 16+** - [下载地址](https://nodejs.org/)
3. **MySQL 8.0** - [下载地址](https://dev.mysql.com/downloads/mysql/)
4. **Redis 7.0+** - [下载地址](https://redis.io/download/)
5. **Maven 3.6+** - [下载地址](https://maven.apache.org/)

### 可选工具
1. **Postman** - API测试工具
2. **Redis Desktop Manager** - Redis可视化工具

## 快速开始

### 1. 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE lucky_sms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库脚本：
```bash
mysql -u root -p lucky_sms < 创建数据库.sql
```

### 2. 后端配置

1. 修改数据库连接配置 (`backend/src/main/resources/application.yml`):
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lucky_sms
    username: your_username
    password: your_password
```

2. 安装依赖并启动：
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 3. 前端配置

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run serve
```

### 4. Redis配置

1. 启动Redis服务器：
```bash
redis-server
```

2. 或者使用提供的脚本：
```bash
scripts\start_redis.bat
```

### 5. 一键启动

使用提供的启动脚本：
```bash
scripts\start_project.bat
```

## API接口说明

### 用户认证
- `POST /api/login` - 用户登录
- 参数: `phone`, `keyhash`

### 课程管理
- `GET /api/course/available` - 获取可选课程列表
- `GET /api/course/capacity/{assignmentId}` - 查询课程剩余容量
- `POST /api/course/select` - 抢课
- `POST /api/course/drop` - 退课

### 抢课功能

抢课功能采用Redis实现高并发控制：

1. **分布式锁**：防止重复提交
2. **原子操作**：保证库存准确性
3. **容量控制**：实时更新课程剩余容量
4. **防超卖**：Redis + 数据库双重校验

## Postman测试

1. 导入Postman集合：`postman/Lucky_SMS_API_Collection.json`
2. 修改环境变量中的基础URL
3. 按顺序执行测试用例

## 项目结构

```
Lucky_SMS/
├── backend/                 # 后端Spring Boot项目
│   ├── src/main/java/
│   │   └── com/yangshengzhou/lucky_sms/
│   │       ├── config/      # 配置类
│   │       ├── controller/ # 控制器
│   │       ├── entity/      # 实体类
│   │       ├── mapper/      # MyBatis映射器
│   │       ├── service/     # 服务层
│   │       └── utils/      # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml # 应用配置
│   │   └── mapper/         # MyBatis XML文件
│   └── pom.xml
├── frontend/                # 前端Vue项目
│   ├── src/
│   │   ├── components/     # 公共组件
│   │   ├── views/          # 页面组件
│   │   ├── router/         # 路由配置
│   │   └── main.js        # 入口文件
│   └── package.json
├── scripts/                 # 启动脚本
├── postman/                # API测试文件
└── 创建数据库.sql          # 数据库初始化脚本
```

## 常见问题

### Q: Redis连接失败
A: 检查Redis服务是否启动，默认端口6379

### Q: 数据库连接失败  
A: 检查application.yml中的数据库配置

### Q: 跨域问题
A: 后端已配置CORS，确保前端访问正确端口

### Q: 抢课并发问题
A: 系统使用Redis分布式锁和原子操作保证并发安全

## 开发建议

1. **代码规范**: 遵循阿里巴巴Java开发规范
2. **日志记录**: 使用SLF4J记录操作日志
3. **异常处理**: 统一使用GlobalExceptionHandler处理异常
4. **性能优化**: 合理使用Redis缓存热点数据
5. **安全考虑**: 对用户输入进行参数校验和SQL注入防护

## 后续开发计划

- [ ] 完善用户管理功能
- [ ] 添加成绩管理模块
- [ ] 实现消息推送功能
- [ ] 添加数据统计和分析
- [ ] 优化移动端适配
- [ ] 增加第三方登录
- [ ] 实现文件上传功能

## 技术支持

如有问题请提交Issue或联系开发团队。