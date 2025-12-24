# IntelliJ IDEA 本地测试指南

## 📋 测试前准备

### 1. 数据库准备

#### 方式一：使用数据库初始化脚本（推荐）
1. 打开 MySQL 客户端（如 MySQL Workbench、Navicat 或命令行）
2. 连接到你的 MySQL 服务器（端口 3006）
3. 执行初始化脚本：
   ```sql
   -- 在 MySQL 客户端中执行
   source E:/FinLedger/src/main/resources/db/init.sql
   ```
   或者直接复制 `src/main/resources/db/init.sql` 文件内容到 MySQL 客户端执行

#### 方式二：手动创建数据库
```sql
CREATE DATABASE finledger CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE finledger;
-- 然后执行 init.sql 中的表创建语句
```

### 2. 检查数据库配置

确认 `src/main/resources/application.properties` 中的配置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3006/finledger?...
spring.datasource.username=root
spring.datasource.password=root  # 修改为你的实际密码
```

---

## 🚀 后端启动（Spring Boot）

### 方法一：使用 IntelliJ IDEA 运行（推荐）

1. **打开项目**
   - 在 IntelliJ IDEA 中打开 `E:\FinLedger` 目录
   - 等待 Maven 依赖自动下载完成（右下角会显示进度）

2. **配置 JDK**
   - `File` → `Project Structure` → `Project`
   - 确保 `Project SDK` 设置为 **JDK 17** 或更高版本
   - `Project language level` 设置为 **17**

3. **运行主类**
   - 找到 `src/main/java/com/yuanzhi/finledger/FinLedgerApplication.java`
   - 右键点击 `FinLedgerApplication.java`
   - 选择 `Run 'FinLedgerApplication'` 或点击绿色运行按钮
   - 或者使用快捷键 `Shift + F10`

4. **查看启动日志**
   - 控制台会显示 Spring Boot 启动日志
   - 看到 `Started FinLedgerApplication in X.XXX seconds` 表示启动成功
   - 后端服务运行在：`http://localhost:8080`

### 方法二：使用 Maven 命令

1. 打开 IntelliJ IDEA 的 Terminal（底部工具栏）
2. 执行命令：
   ```bash
   mvn spring-boot:run
   ```

### 验证后端是否启动成功

1. 打开浏览器访问：`http://localhost:8080/api/auth/login`
2. 如果返回 JSON 响应（即使是错误），说明后端已启动

---

## 🎨 前端启动（Vue 3）

### 方法一：使用 IntelliJ IDEA Terminal（推荐）

1. **打开 Terminal**
   - 在 IntelliJ IDEA 底部点击 `Terminal` 标签
   - 或者使用快捷键 `Alt + F12`

2. **进入前端目录**
   ```bash
   cd FinLedger-ui
   ```

3. **安装依赖**（如果还没安装）
   ```bash
   npm install
   ```
   ⚠️ 注意：如果 `node_modules` 已存在且完整，可跳过此步

4. **启动开发服务器**
   ```bash
   npm run dev
   ```

5. **查看启动信息**
   - 终端会显示类似信息：
     ```
     VITE v7.2.4  ready in 500 ms
     
     ➜  Local:   http://localhost:5173/
     ➜  Network: use --host to expose
     ```
   - 前端服务运行在：`http://localhost:5173`

### 方法二：使用外部命令行

1. 打开 PowerShell 或 CMD
2. 进入项目目录：
   ```bash
   cd E:\FinLedger\FinLedger-ui
   ```
3. 执行：
   ```bash
   npm install  # 如果还没安装依赖
   npm run dev
   ```

---

## ✅ 测试步骤

### 1. 测试后端 API

#### 使用浏览器测试
- 访问：`http://localhost:8080/api/auth/login`
- 应该返回 JSON 响应

#### 使用 Postman 或 API 测试工具
- **注册接口**：
  ```
  POST http://localhost:8080/api/auth/register
  Content-Type: application/json
  
  {
    "username": "test",
    "password": "123456",
    "email": "test@example.com",
    "role": "ACCOUNTANT"
  }
  ```

- **登录接口**：
  ```
  POST http://localhost:8080/api/auth/login
  Content-Type: application/json
  
  {
    "username": "test",
    "password": "123456"
  }
  ```

### 2. 测试前端页面

1. **打开浏览器**
   - 访问：`http://localhost:5173`

2. **注册账号**
   - 点击"还没有账号？立即注册"
   - 填写信息：
     - 用户名：test
     - 密码：123456
     - 邮箱：test@example.com
     - 角色：选择"会计"
   - 点击"注册"

3. **登录系统**
   - 使用注册的账号登录
   - 登录成功后会自动跳转到 Dashboard

4. **测试功能模块**
   - ✅ Dashboard：查看统计数据
   - ✅ 交易管理：创建、查看、搜索交易
   - ✅ 账户管理：查看、创建会计科目
   - ✅ 分录管理：创建分录、查看待审核分录
   - ✅ 支付管理：查看支付记录
   - ✅ 报表管理：查看财务报表

---

## 🔧 常见问题解决

### 问题1：后端启动失败 - 数据库连接错误

**错误信息**：
```
Communications link failure
Access denied for user 'root'@'localhost'
```

**解决方法**：
1. 检查 MySQL 服务是否启动
2. 确认数据库端口是否为 3006
3. 检查 `application.properties` 中的用户名和密码是否正确
4. 确认数据库 `finledger` 是否已创建

### 问题2：后端启动失败 - 端口被占用

**错误信息**：
```
Port 8080 is already in use
```

**解决方法**：
1. 修改 `application.properties` 中的端口：
   ```properties
   server.port=8081
   ```
2. 同时修改前端 API 配置 `FinLedger-ui/src/api/index.js`：
   ```javascript
   baseURL: 'http://localhost:8081/api'
   ```

### 问题3：前端启动失败 - 依赖安装错误

**错误信息**：
```
npm ERR! code ERESOLVE
```

**解决方法**：
```bash
cd FinLedger-ui
rm -rf node_modules package-lock.json
npm install --legacy-peer-deps
```

### 问题4：前端无法连接后端

**错误信息**：
```
Network Error
CORS error
```

**解决方法**：
1. 确认后端已启动（访问 `http://localhost:8080`）
2. 检查 `application.properties` 中的跨域配置
3. 确认前端 API 地址正确（`FinLedger-ui/src/api/index.js`）

### 问题5：Maven 依赖下载慢

**解决方法**：
1. 配置 Maven 镜像（`File` → `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Maven`）
2. 或修改 `pom.xml` 添加阿里云镜像（在 `<project>` 标签内添加）：
   ```xml
   <repositories>
       <repository>
           <id>aliyun</id>
           <url>https://maven.aliyun.com/repository/public</url>
       </repository>
   </repositories>
   ```

### 问题6：JDK 版本不匹配

**错误信息**：
```
java: 错误: 不支持发行版本 X
```

**解决方法**：
1. 确保安装 JDK 17 或更高版本
2. 在 IntelliJ IDEA 中配置：
   - `File` → `Project Structure` → `Project` → `SDK` 选择 JDK 17
   - `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Java Compiler` → `Project bytecode version` 选择 17

---

## 📊 测试检查清单

- [ ] MySQL 数据库已创建并执行初始化脚本
- [ ] 数据库连接配置正确（端口、用户名、密码）
- [ ] 后端 Spring Boot 应用启动成功（端口 8080）
- [ ] 前端 Vue 应用启动成功（端口 5173）
- [ ] 可以访问前端页面 `http://localhost:5173`
- [ ] 可以注册新用户
- [ ] 可以登录系统
- [ ] Dashboard 页面显示统计数据
- [ ] 可以创建交易记录
- [ ] 可以查看交易列表
- [ ] 可以创建会计科目
- [ ] 可以创建分录

---

## 🎯 快速测试命令

### 后端测试
```bash
# 在项目根目录执行
mvn clean spring-boot:run
```

### 前端测试
```bash
# 在 FinLedger-ui 目录执行
cd FinLedger-ui
npm run dev
```

### 同时启动（需要两个终端）
- **终端1（后端）**：`mvn spring-boot:run`
- **终端2（前端）**：`cd FinLedger-ui && npm run dev`

---

## 💡 调试技巧

### 后端调试
1. 在代码中设置断点
2. 使用 `Debug 'FinLedgerApplication'` 而不是 `Run`
3. 查看控制台日志输出

### 前端调试
1. 打开浏览器开发者工具（F12）
2. 查看 `Console` 标签的错误信息
3. 查看 `Network` 标签的 API 请求
4. 使用 Vue DevTools 浏览器插件

---

## 📞 需要帮助？

如果遇到问题，请检查：
1. 控制台错误信息
2. 浏览器控制台错误
3. 数据库连接状态
4. 端口占用情况

祝测试顺利！🎉


