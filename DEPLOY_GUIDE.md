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
SELECT COUNT(*) FROM sys_user WHERE deleted=0;    -- 应有 14 条
SELECT COUNT(*) FROM product WHERE deleted=0;     -- 应有 9 条
SELECT COUNT(*) FROM order_info WHERE deleted=0;  -- 应有 47 条
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
- 注册/登录/退出
- 首页（轮播图、分类导航、推荐商品）
- 商品搜索/详情/分类筛选
- 购物车（增删改、结算）
- 订单（创建、支付、取消、确认收货）
- 评价（提交、查看）
- 收货地址管理
- 商品收藏
- 浏览足迹
- 积分/优惠券
- 消息通知
- 溯源查询
- 农户认证申请

### 管理后台（http://localhost:8088，admin账号登录后访问）
- 仪表盘（统计数据、待处理事项）
- 用户管理（增删改查、批量启禁用、导出）
- 分类管理（增删改查）
- 商品管理（增删改查、上下架、推荐、图片）
- 轮播图管理
- 订单管理（列表、详情、发货、删除）
- 支付记录
- 评论管理（审核、删除）
- 售后管理（审核、关闭、客服记录）
- 农户审核（通过、驳回、资质过期）
- 管理员账号管理
- 用户地址查看
- 系统配置
- 操作日志
- 优惠券管理
- 秒杀管理
- 营销活动管理
- 会员管理
- 溯源管理
- 导出CSV

### 农户端（farmer账号登录后访问）
- 农户工作台（统计数据）
- 溯源批次管理（创建、删除申请）
- 溯源记录（种植、施肥、农药、灌溉、收获）
- 溯源图片上传
- 商品发布（提交审核）
- 订单管理（查看、发货）
- 商品库存管理

---

## 九、常见问题

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

---

## 十、项目结构

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

## 十一、修改前端后重新编译

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
