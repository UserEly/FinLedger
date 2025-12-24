# FinLedger 财务管理系统

FinLedger 是一个类似 GnuCash 的开源财务管理系统，专为个人用户和小型企业设计，采用双式记账原则。

## 技术栈

### 后端
- Spring Boot 4.0.1
- Spring Data JPA
- Spring Security
- MySQL
- JWT 认证
- Lombok

### 前端
- Vue 3.5.24
- Element Plus 2.8.8
- Vue Router 4.4.5
- Pinia 2.2.6
- Axios
- Vite 7.2.4

## 功能特性

### 用户角色
- **会计 (ACCOUNTANT)**: 录入交易、创建分录、提交审核
- **财务经理 (MANAGER)**: 审核分录、审批支付、管理账户
- **老板 (BOSS)**: 查看报表、最终审批支付、监控现金流

### 核心功能模块

1. **用户认证**
   - 用户注册（支持三种角色）
   - 用户登录（JWT 认证）
   - 个人信息管理

2. **交易管理**
   - 交易记录录入
   - 交易列表查看
   - 交易状态管理（待过账、已过账、已支付）

3. **账户管理**
   - 会计科目管理（资产、负债、权益、收入、费用）
   - 科目层级结构
   - 科目余额查询

4. **分录管理**
   - 创建分录（基于交易记录）
   - 分录拆分（支持多科目）
   - 借贷平衡验证
   - 分录审核流程

5. **支付管理**
   - 支付申请
   - 多级审批（财务经理 → 老板）
   - 支付状态跟踪

6. **报表管理**
   - 利润表
   - 资产负债表
   - 现金流分析

## 项目结构

```
FinLedger/
├── src/main/java/com/yuanzhi/finledger/
│   ├── config/          # 配置类（Security等）
│   ├── controller/      # REST API 控制器
│   ├── dto/             # 数据传输对象
│   ├── entity/          # JPA 实体类
│   ├── repository/      # 数据访问层
│   ├── service/         # 业务逻辑层
│   └── util/            # 工具类（JWT等）
├── FinLedger-ui/
│   ├── src/
│   │   ├── api/         # API 接口定义
│   │   ├── components/  # Vue 组件
│   │   ├── layouts/     # 布局组件
│   │   ├── router/      # 路由配置
│   │   ├── stores/      # Pinia 状态管理
│   │   └── views/       # 页面组件
│   └── package.json
└── pom.xml
```

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE finledger CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application.properties` 中的数据库连接信息：
```properties
spring.datasource.url=jdbc:mysql://localhost:3006/finledger?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
```

**注意**：如果你的 MySQL 端口不是 3006，请修改为实际端口号。

### 后端启动

1. 进入项目根目录
2. 编译项目：
```bash
mvn clean install
```

3. 运行应用：
```bash
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. 进入前端目录：
```bash
cd FinLedger-ui
```

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

## API 接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

### 交易接口
- `GET /api/transactions` - 获取所有交易
- `GET /api/transactions/pending` - 获取待过账交易
- `POST /api/transactions` - 创建交易
- `PUT /api/transactions/{id}` - 更新交易
- `DELETE /api/transactions/{id}` - 删除交易

### 账户接口
- `GET /api/accounts` - 获取所有账户
- `POST /api/accounts` - 创建账户
- `PUT /api/accounts/{id}` - 更新账户
- `DELETE /api/accounts/{id}` - 删除账户

### 分录接口
- `GET /api/entries` - 获取所有分录
- `GET /api/entries/submitted` - 获取待审核分录
- `POST /api/entries` - 创建分录
- `PUT /api/entries/{id}/status` - 更新分录状态

### 支付接口
- `GET /api/payments` - 获取所有支付
- `GET /api/payments/pending` - 获取待审批支付
- `POST /api/payments` - 创建支付
- `PUT /api/payments/{id}/approve` - 审批支付
- `PUT /api/payments/{id}/reject` - 拒绝支付

## 业务规则

1. **借贷平衡**: 每笔分录的借方总额必须等于贷方总额
2. **账期管理**: 账期不得超过设定值，逾期需标记
3. **信用额度**: 客户应付款不得超信用额度
4. **税费计算**: 税费基于税率和金额计算并记录
5. **数据校验**: 财务数据保存前校验无负值或空值

## 开发说明

### 添加新功能

1. 在后端创建对应的 Entity、Repository、Service、Controller
2. 在前端创建对应的 API 接口和页面组件
3. 在路由中添加新页面路由

### 数据库迁移

项目使用 JPA 的 `ddl-auto=update` 模式，首次启动会自动创建表结构。生产环境建议使用 Flyway 或 Liquibase 进行数据库版本管理。

## 许可证

本项目采用开源许可证（具体许可证待定）

## 贡献

欢迎提交 Issue 和 Pull Request！

