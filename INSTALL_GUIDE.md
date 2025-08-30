# 🚀 Lucky SMS 环境安装指南

## 必需软件安装

### 1. 安装 Maven

#### Windows 安装步骤：
1. 访问 [Maven官网](https://maven.apache.org/download.cgi)
2. 下载最新版本的二进制zip文件（如：apache-maven-3.9.9-bin.zip）
3. 解压到指定目录，例如：`C:\Program Files\apache-maven-3.9.9`
4. 配置环境变量：
   - 新建系统变量 `MAVEN_HOME` = `C:\Program Files\apache-maven-3.9.9`
   - 在Path中添加 `%MAVEN_HOME%\bin`
5. 验证安装：
   ```cmd
   mvn -version
   ```

#### 快速安装（PowerShell）：
```powershell
# 下载Maven
Invoke-WebRequest -Uri "https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip" -OutFile "maven.zip"

# 解压到Program Files
Expand-Archive -Path "maven.zip" -DestinationPath "C:\Program Files\"

# 设置环境变量
[System.Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\Program Files\apache-maven-3.9.9", "Machine")
$path = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
[System.Environment]::SetEnvironmentVariable("Path", "$path;C:\Program Files\apache-maven-3.9.9\bin", "Machine")

# 验证安装
mvn -version
```

### 2. 安装 Redis

#### Windows 安装步骤：
1. 访问 [Redis Windows版本](https://github.com/microsoftarchive/redis/releases)
2. 下载最新版本的Redis-x64-3.2.100.zip
3. 解压到指定目录，例如：`C:\Redis`
4. 启动Redis服务：
   ```cmd
   cd C:\Redis
   redis-server.exe redis.windows.conf
   ```

#### 使用Windows Subsystem for Linux (WSL)：
```powershell
# 启用WSL
wsl --install

# 安装Ubuntu
wsl --install -d Ubuntu

# 在Ubuntu中安装Redis
wsl sudo apt update
wsl sudo apt install redis-server

# 启动Redis服务
wsl sudo service redis-server start
```

### 3. 安装 MySQL 8.0

#### Windows 安装步骤：
1. 访问 [MySQL官网](https://dev.mysql.com/downloads/mysql/)
2. 下载MySQL Installer for Windows
3. 运行安装程序，选择"Server only"安装
4. 设置root密码，记住密码用于后续配置
5. 完成安装后启动MySQL服务

#### 使用Docker安装：
```powershell
# 安装Docker Desktop for Windows
# 下载地址: https://www.docker.com/products/docker-desktop/

# 拉取MySQL镜像
docker pull mysql:8.0

# 运行MySQL容器
docker run -d --name mysql8 \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=your_password \
  -e MYSQL_DATABASE=lucky_sms \
  -v mysql_data:/var/lib/mysql \
  mysql:8.0
```

## 🔧 项目配置

### 1. 数据库初始化

1. 登录MySQL：
   ```cmd
   mysql -u root -p
   ```

2. 创建数据库：
   ```sql
   CREATE DATABASE lucky_sms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. 执行初始化脚本：
   ```cmd
   mysql -u root -p lucky_sms < 创建数据库.sql
   ```

### 2. 后端配置

修改 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lucky_sms
    username: root
    password: your_mysql_password  # 修改为你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. 前端配置

前端配置通常无需修改，默认运行在8080端口。

## 🚀 项目启动

### 方法一：使用启动脚本（推荐）

```cmd
# 一键启动所有服务
scripts\start_project.bat
```

### 方法二：手动启动

1. **启动Redis**：
   ```cmd
   scripts\start_redis.bat
   ```

2. **启动后端**：
   ```cmd
   cd backend
   mvn spring-boot:run
   ```

3. **启动前端**：
   ```cmd
   cd frontend
   npm run serve
   ```

### 方法三：开发模式启动

```cmd
# 后端热部署开发模式
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 前端开发模式  
cd frontend
npm run dev
```

## 📊 验证安装

### 检查服务状态

1. **后端服务**: http://localhost:8081/api/health
2. **前端服务**: http://localhost:8080
3. **Redis服务**: 
   ```cmd
   redis-cli ping
   # 应该返回 PONG
   ```

### 测试API接口

1. 导入Postman集合：`postman/Lucky_SMS_API_Collection.json`
2. 按顺序执行测试用例
3. 验证抢课功能是否正常工作

## 🛠️ 故障排除

### 常见问题解决

#### 1. Maven命令找不到
```
解决方案：
- 检查MAVEN_HOME环境变量
- 重启命令行窗口使环境变量生效
- 验证mvn -version命令
```

#### 2. Redis连接失败
```
解决方案：
- 检查Redis服务是否启动
- 确认Redis运行在默认端口6379
- 检查防火墙设置
```

#### 3. MySQL连接失败
```
解决方案：
- 检查MySQL服务是否启动
- 确认application.yml中的数据库配置正确
- 检查MySQL用户权限
```

#### 4. 端口冲突
```
解决方案：
- 后端默认端口：8081
- 前端默认端口：8080
- Redis默认端口：6379
- 如端口被占用，修改对应配置文件中的端口号
```

### 日志查看

#### 后端日志：
```cmd
cd backend
mvn spring-boot:run
# 查看控制台输出
```

#### 前端日志：
```cmd
cd frontend
npm run serve
# 查看控制台输出
```

## 📞 技术支持

如遇到安装问题，请检查：

1. ✅ Java版本是否为17+
2. ✅ Node.js版本是否为16+
3. ✅ Maven是否正确安装
4. ✅ Redis服务是否运行
5. ✅ MySQL服务是否运行
6. ✅ 数据库配置是否正确

所有必需软件的下载链接已在文档中提供，按照步骤操作即可完成安装。