# 一码鲜踪 — 生鲜农产品溯源电商平台

一码鲜踪是一个全栈生鲜农产品溯源电商平台，实现"一码一物，全程可追溯"。消费者扫码即可查看农产品从种植、施肥、灌溉到收获、上市的全过程溯源信息，同时支持完整的电商购物流程（浏览、下单、支付、售后）。

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 前端 | Vue 3 + Element Plus + Vite 5 | Composition API，Pinia 状态管理，ECharts 图表 |
| 后端 | Spring Boot 3.2.5 + MyBatis-Plus 3.5.5 | Java 17，RESTful API |
| 数据库 | MySQL 8 | 71 张数据表，utf8mb4 编码 |
| 认证 | JWT | Access Token（24h）+ Refresh Token（7d） |
| 实时通信 | WebSocket | AI 智能客服 + 人工客服在线聊天 |
| 构建工具 | Maven（后端）+ Vite（前端） | 前后端一体化部署 |

## 核心功能

### 用户端
- 商品浏览、搜索、分类筛选
- 购物车、下单、支付、确认收货
- 商品评价、收藏、浏览记录
- 收货地址管理
- 优惠券中心（领券、使用）
- 积分系统（积分兑换优惠券）
- 会员体系（等级、折扣、余额充值）
- **溯源查询** — 扫码查看农产品全生命周期溯源信息
- **防伪验证** — 一码一物，扫码确认真伪
- 在线客服（AI 智能 + 人工，WebSocket 实时聊天）
- 农户认证申请
- 团购、秒杀、预售、盲盒、营销活动

### 农户端
- 仪表盘数据统计
- 溯源批次管理（创建、删除申请）
- 溯源记录（种植、施肥、农药、灌溉、收获）
- 溯源图片上传
- 批量生成溯源码（QR 码），导出 PDF
- 商品发布（提交审核）
- 订单管理、发货
- 库存管理
- 售后工单处理
- 农户认证（地图选址，自动解析坐标）

### 管理端
- 数据仪表盘（统计图表、待办事项）
- 用户管理、商品管理、分类管理
- 商品发布审核、溯源批次审核
- 订单管理、支付记录
- 评价审核
- 售后管理
- 农户认证审核
- 轮播图管理
- 管理员账户管理
- 会员等级配置、积分兑换规则
- 优惠券管理、秒杀管理、营销活动
- 溯源扫码服务器配置
- AI 客服设置（API 地址、Key、模型）
- 操作日志
- CSV 数据导出（用户、商品、订单）

## 项目结构

```
freshtrack-marketplace/
├── src/                          # 后端：Spring Boot 应用
│   └── main/java/com/freshtrace/unified/
│       ├── controller/           # REST 控制器（~40 个）
│       ├── service/              # 业务逻辑层
│       ├── entity/               # 数据实体
│       ├── mapper/               # MyBatis 映射
│       ├── config/               # 配置（JWT、WebSocket、CORS 等）
│       └── dto/                  # 数据传输对象
├── frontend/                     # 前端：Vue 3 SPA
│   └── src/
│       ├── views/user/           # 用户端页面
│       ├── views/admin/          # 管理端页面
│       ├── views/farmer/         # 农户端页面
│       ├── api/                  # API 调用封装
│       ├── router/               # 路由配置
│       └── stores/               # Pinia 状态管理
├── sql/                          # 数据库脚本（完整库、建表语句、升级脚本）
├── deploy/                       # 部署脚本
├── scripts/                      # 数据修复脚本
└── DEPLOY_GUIDE.md               # 详细部署指南
```

## 快速开始

### 环境要求

- **JDK** 17+
- **Node.js** 18+
- **MySQL** 8.0+
- **Maven** 3.8+

### 1. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS fresh_trace_shop DEFAULT CHARACTER SET utf8mb4;"

# 导入完整数据
mysql -u root -p fresh_trace_shop < sql/fresh_trace_shop_full.sql
```

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fresh_trace_shop?useUnicode=true&characterEncoding=utf8mb4
    username: root
    password: 你的数据库密码
```

### 3. 启动后端

```bash
# 方式一：Maven 启动
mvn spring-boot:run

# 方式二：先打包再运行
mvn clean package -DskipTests
java -jar target/fresh-trace-unified-1.0.0.jar
```

后端启动后运行在 `http://localhost:8088`

### 4. 启动前端（开发模式）

```bash
cd frontend
npm install
npm run dev
```

前端开发服务器运行在 `http://localhost:3000`，自动代理 API 请求到后端。

### 5. 访问系统

| 地址 | 说明 |
|------|------|
| http://localhost:3000 | 用户端 / 农户端 |
| http://localhost:3000/admin | 管理后台 |

## 详细部署

更多部署细节（Docker 部署、生产环境配置、Nginx 反向代理等）请参考 [DEPLOY_GUIDE.md](DEPLOY_GUIDE.md)。
