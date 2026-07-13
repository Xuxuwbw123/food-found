# 生鲜农产品溯源电商平台 — 本地部署清单

## 一、环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 推荐 JDK 17.0.11 |
| MySQL | 5.7 或 8.x | 均可 |
| Maven | 3.9+ | 用于编译打包 |
| Node.js | 16+ | 仅修改前端时需要 |

---

## 二、安装 JDK 17

### Windows

1. 下载：https://adoptium.net/temurin/releases/?version=17
   - 选择 **Windows** / **x64** / **JDK** / **17**
2. 安装，勾选 "Set JAVA_HOME variable"
3. 验证：
```powershell
java -version
# 输出 java version "17.0.x" 即成功
```

### 如果 JAVA_HOME 未自动设置

```powershell
# PowerShell 临时设置（当前窗口有效）
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"

# 或永久设置（管理员 PowerShell）
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "User")
```

---

## 三、安装 MySQL

### Windows

1. 下载：https://dev.mysql.com/downloads/mysql/
2. 安装时设置 root 密码为 `123456`（或自定义，需同步修改配置）
3. 验证：
```powershell
mysql -u root -p123456 -e "SELECT VERSION();"
```

---

## 四、创建数据库并导入数据

### 方式一：使用导出的完整 SQL（推荐）

```powershell
mysql -u root -p123456 < sql/fresh_trace_shop_full.sql
```

### 方式二：手动创建

```sql
CREATE DATABASE fresh_trace_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fresh_trace_shop;
SOURCE sql/fresh_trace_shop_full.sql;
```

### 验证数据

```sql
USE fresh_trace_shop;
SELECT COUNT(*) FROM sys_user WHERE deleted=0;        -- 应有 14 条
SELECT COUNT(*) FROM product WHERE deleted=0;         -- 应有 9 条
SELECT COUNT(*) FROM order_info WHERE deleted=0;      -- 应有 47 条
SELECT COUNT(*) FROM member_level_config;             -- 应有 4 条（4个会员等级）
SELECT COUNT(*) FROM marketing_activity;              -- 营销活动
SELECT COUNT(*) FROM marketing_goods;                 -- 营销关联商品
SELECT COUNT(*) FROM chat_message;                    -- 聊天消息
SELECT COUNT(*) FROM sys_config WHERE config_group='ai'; -- AI配置（3条）
```

---

## 五、配置数据库连接

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/fresh_trace_shop?serverTimezone=Asia/Shanghai&useSSL=false&characterEncoding=utf-8&allowPublicKeyRetrieval=true
    username: root
    password: 123456   # 改成你的MySQL密码
```

---

## 六、编译运行

### 方式一：命令行

```powershell
cd fresh-trace-unified

# 编译打包
mvnw.cmd clean package -DskipTests

# 运行
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
& "$env:JAVA_HOME\bin\java" -jar target\fresh-trace-unified-1.0.0.jar
```

### 方式二：IntelliJ IDEA

1. File → Open → 选择 `fresh-trace-unified` 文件夹
2. 等待 Maven 加载依赖
3. 找到 `UnifiedApplication.java` → 右键 → Run

### 启动成功标志

```
Started UnifiedApplication in X seconds
```

浏览器打开：**http://localhost:8088**

---

## 七、测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 管理后台全部权限 |
| 管理员 | operator01 | 123456 | 运营账号 |
| 农户 | farmer_zhang | 123456 | 已认证农户，可管理溯源和商品 |
| 农户 | farmer_li | 123456 | 已认证农户 |
| 普通用户 | user_wang | 123456 | 有历史订单和收货地址 |

---

## 八、功能清单

### 用户端（http://localhost:8088）
- 注册/登录/退出（JWT鉴权，24小时有效期）
- 首页（轮播图、分类导航、推荐/新品/热销商品）
- 商品搜索/详情/分类筛选
- 购物车（增删改、结算、选择优惠券）
- 订单（创建、支付、取消、确认收货、物流查询）
- 评价（提交、查看、直接评论）
- 收货地址管理（增删改、设默认）
- 商品收藏
- 浏览足迹
- 积分（查看积分、积分记录、积分兑换优惠券）
- 优惠券（领券中心、我的优惠券、下单抵扣）
- 消息通知（订单/售后/系统/会员通知）
- 溯源查询（批次号查询、完整溯源链展示）
- 农户认证申请
- 会员中心（查看等级、折扣率、余额、充值）
- 营销活动（查看有效活动、商品适用活动）
- **一物一码防伪验证**（扫码查看溯源信息、正品认证）
- **在线客服**（AI智能客服 + 人工客服、发送订单、退款/换货申请）

### 管理后台（http://localhost:8088，admin账号登录后访问）
- 仪表盘（统计数据、待处理事项、数据概览饼图）
- 用户管理（增删改查、批量启禁用、导出CSV、会员等级显示）
- 分类管理（增删改查、树形展示）
- 商品管理（增删改查、上下架、推荐、图片管理、批量操作、导出CSV）
- 商品发布审核（查看待审核、通过/拒绝）
- 轮播图管理
- 订单管理（列表、详情、发货、删除、导出CSV）
- 支付记录
- 评论管理（审核、删除）
- 售后管理（审核、关闭、客服沟通记录）
- 农户审核（通过、驳回、资质过期、地图查看农场位置）
- 溯源批次审核（查看待审核批次含详情图片、通过/拒绝）
- 溯源删除审核（通过/拒绝）
- 管理员账号管理（增删改、重置密码）
- 用户地址查看
- 系统配置
- 操作日志
- 会员等级配置（折扣率、升级门槛、充值最低金额、积分倍率）
- 积分兑换规则（新增/编辑/删除、关联优惠券）
- 会员管理（查看、调整等级、启用禁用）
- 优惠券管理（增删改查、领取记录）
- 秒杀管理
- 营销活动管理（增删改查、关联商品、开启通知）
- **溯源设置**（配置扫码服务器地址）
- **客服会话**（实时聊天、处理退款、处理换货、一键发送退货地址）
- **AI客服设置**（配置API地址、Key、模型名称、测试连接）
- 导出CSV（用户/商品/订单）

### 农户端（farmer账号登录后访问）
- 农户工作台（统计数据）
- 农户认证申请（支持重新申请、填写农场地址自动解析坐标）
- 溯源批次管理（创建、删除申请）
- 溯源记录（种植、施肥、农药、灌溉、收获）
- 溯源图片上传
- **生成溯源码**（批量生成一物一码QR码、导出PDF）
- 商品发布（提交管理员审核）
- 订单管理（查看、发货）
- 商品库存管理（更新库存、上下架、批量操作）
- 售后工单处理（审核、拒绝）

---

## 九、新增功能详细说明

### 9.1 会员储蓄卡系统

**功能概述：**
用户可充值到会员卡账户，余额用于消费，享受会员等级折扣。

**会员等级：**

| 等级 | 折扣 | 升级门槛 | 积分倍率 |
|------|------|---------|---------|
| 普通用户 | 无折扣 | - | 1x |
| 普通会员 | 9.8折 | ¥500 | 1x |
| 银卡会员 | 9.5折 | ¥2000 | 2x |
| 金卡会员 | 9.0折 | ¥5000 | 3x |

**支付流程：**
1. 用户下单 → 系统自动计算：商品总价 × 会员折扣 - 优惠券 = 实付金额
2. 支付方式：微信 / 支付宝 / 会员卡余额
3. 会员卡支付直接从余额扣款

**退款逻辑：**
- 取消订单：自动返还余额 + 扣除积分 + 返还优惠券
- 客服退款：同步更新订单状态、库存、售后工单、积分、余额
- 会员卡支付的订单退款到余额，其他方式原路退回

**相关配置：**
- 管理后台 → 系统管理 → 会员等级配置
- 管理后台 → 系统管理 → 积分兑换规则
- 管理后台 → 会员管理

---

### 9.2 AI智能客服系统

**功能概述：**
用户发消息先由AI自动回复，AI无法解决的问题自动转人工客服。

**AI能力：**
- 回答简单问题：发货时间、送达时间、物流查询
- 闲聊过滤：与业务无关的问题拒绝回答
- 退款/换货/投诉：自动转人工
- 每次对话最多10轮，超出自动转人工

**人工客服功能：**
- 实时聊天（WebSocket）
- 处理退款：一键退款，同步所有模块
- 处理换货：一键发送退货地址
- 查看用户订单信息

**AI配置：**
管理后台 → AI客服设置，可配置：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| API地址 | 兼容OpenAI格式的接口地址 | https://api.xiaomimimo.com/v1/chat/completions |
| API Key | 服务商提供的密钥 | - |
| 模型名称 | 使用的模型 | mimo-v2.5-pro |

**支持的AI服务商：**
- 小米 MiMo（https://api.xiaomimimo.com/v1）
- OpenAI（https://api.openai.com/v1）
- 硅基流动（https://api.siliconflow.cn/v1）
- 通义千问（https://dashscope.aliyuncs.com/compatible-mode/v1）
- 智谱AI（https://open.bigmodel.cn/api/paas/v4）

**数据库配置：**
```sql
-- AI相关配置存储在 sys_config 表
SELECT * FROM sys_config WHERE config_group = 'ai';
-- 包含：ai_api_url、ai_api_key、ai_model
```

---

### 9.3 一物一码防伪溯源

**功能概述：**
农户为每个商品生成唯一溯源二维码，用户扫码查看溯源信息和正品认证。

**流程：**
1. 农户在溯源管理页面批量生成QR码
2. 导出PDF，打印贴标
3. 用户手机扫码 → 跳转独立溯源页面
4. 显示：正品认证 + 完整溯源链 + 图片 + 产地地图

**API配置：**
管理后台 → 高级功能 → 溯源设置，配置扫码服务器地址（如 `http://192.168.31.79:8088`）

---

## 十、数据库表清单（50张表）

| 模块 | 表名 | 说明 |
|------|------|------|
| 用户权限 | sys_user | 用户表（含会员等级、累计消费、余额） |
| | sys_role | 角色表 |
| | sys_user_role | 用户角色关联 |
| | sys_menu | 菜单权限表 |
| | sys_role_menu | 角色菜单关联 |
| 农户 | farmer | 农户信息表（含经纬度） |
| | farmer_audit | 农户审核记录 |
| 商品 | product_category | 商品分类 |
| | product | 商品表（含预售字段） |
| | product_image | 商品图片 |
| | product_comment | 商品评论 |
| | product_favorite | 商品收藏 |
| 溯源 | traceability | 溯源主表 |
| | trace_planting | 种植记录 |
| | trace_fertilizer | 施肥记录 |
| | trace_pesticide | 农药记录 |
| | trace_irrigation | 灌溉记录 |
| | trace_harvest | 收获记录 |
| | trace_inspection | 质检报告 |
| | trace_logistics | 冷链物流 |
| | trace_storage | 仓储记录 |
| | trace_image | 溯源图片 |
| | trace_breeding | 养殖记录 |
| | trace_processing | 加工记录 |
| | trace_scan_log | 扫码日志 |
| | trace_location | 溯源地理位置 |
| | product_qrcode | 商品QR码（一物一码） |
| | qrcode_scan_log | QR码扫描日志 |
| | growth_timeline | 生长周期时间轴 |
| | sys_location | 基础地理点位库 |
| 订单购物 | shopping_cart | 购物车 |
| | order_info | 订单主表（含预售状态） |
| | order_item | 订单明细 |
| | order_log | 订单日志 |
| | payment_info | 支付记录 |
| 地址轮播 | user_address | 收货地址 |
| | banner | 轮播图 |
| 售后客服 | after_sales_order | 售后工单 |
| | customer_service_log | 客服记录 |
| | chat_message | 聊天消息（AI+人工客服） |
| 会员积分 | member_point | 积分余额 |
| | point_log | 积分记录 |
| | member_level_config | 会员等级配置 |
| | recharge_record | 充值记录 |
| | points_exchange_rule | 积分兑换规则 |
| 营销 | coupon | 优惠券 |
| | user_coupon | 用户优惠券 |
| | seckill | 秒杀活动 |
| | marketing_activity | 营销活动 |
| | marketing_goods | 活动关联商品 |
| 其他 | sys_config | 系统配置（含AI配置） |
| | sys_notice | 消息通知 |
| | sys_operation_log | 操作日志 |
| | user_footprint | 浏览足迹 |
| | logistics_track | 物流轨迹 |

---

## 十一、常见问题

### 端口被占用
```powershell
# 查看占用端口的进程
netstat -ano | findstr :8088
# 停止进程
Stop-Process -Id <PID> -Force
```

### 修改端口
编辑 `src/main/resources/application.yml`：
```yaml
server:
  port: 9090  # 改成你想要的端口
```

### 数据库连接失败
- 确认 MySQL 服务已启动
- 确认密码正确
- 确认数据库 `fresh_trace_shop` 已创建

### 图片不显示
- 确认 `uploads/` 目录存在且有写入权限
- 确认 `public/images/` 目录完整

### AI客服不回复
- 管理后台 → AI客服设置 → 检查API地址和Key是否正确
- 点"测试连接"验证
- 确认AI服务商账户有余额

### 手机扫码无法访问
- 确认手机和电脑在同一WiFi网络
- 管理后台 → 溯源设置 → 设置服务器地址为电脑局域网IP（如 `http://192.168.31.79:8088`）
- 用 `ipconfig` 查看电脑IP地址

---

## 十二、项目结构

```
fresh-trace-unified/
├── frontend/              # 前端源码（Vue 3 + Element Plus）
│   ├── src/               # Vue 组件、路由、API
│   ├── public/            # 静态图片资源
│   ├── package.json       # 前端依赖
│   └── vite.config.js     # Vite 配置
├── src/                   # Spring Boot 后端
│   └── main/
│       ├── java/          # Java 源码
│       └── resources/
│           ├── static/    # 编译后的前端文件
│           └── application.yml  # 配置文件
├── sql/                   # 数据库脚本
│   └── fresh_trace_shop_full.sql  # 完整数据库（结构+数据）
├── uploads/               # 用户上传的文件
├── pom.xml                # Maven 配置
└── DEPLOY_GUIDE.md        # 本文档
```

---

## 十三、修改前端后重新编译

```powershell
cd frontend
npm install          # 安装依赖（首次）
npm run build        # 编译

# 复制编译产物到Spring Boot
Remove-Item -Recurse src/main/resources/static/*
Copy-Item -Recurse frontend/dist/* src/main/resources/static/

# 重新打包
mvnw.cmd clean package -DskipTests
```
