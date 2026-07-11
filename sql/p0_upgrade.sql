-- ============================================================
-- 农产品溯源电商平台 P0 核心交易闭环 — 数据库升级脚本
-- 执行方式：mysql -u root -p < p0_upgrade.sql
-- 特性：幂等执行，重复运行不报错不丢数据
-- ============================================================

SET NAMES utf8mb4;
USE fresh_trace_shop;

-- ============================================================
-- 模块1：C端用户 — 订单评价
-- ============================================================

-- order_info 补充 is_commented 字段（兼容原有 is_comment）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='order_info' AND COLUMN_NAME='is_commented';
SET @sql = IF(@col_exists=0, 'ALTER TABLE order_info ADD COLUMN is_commented TINYINT DEFAULT 0 COMMENT ''是否已评价 0否 1是'' AFTER is_comment', 'SELECT ''is_commented 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- product_comment 补充索引
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='product_comment' AND INDEX_NAME='idx_user_id';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE product_comment ADD INDEX idx_user_id (user_id)', 'SELECT ''idx_user_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='product_comment' AND INDEX_NAME='idx_product_id';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE product_comment ADD INDEX idx_product_id (product_id)', 'SELECT ''idx_product_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- product_comment 加 order_item_id 索引
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='product_comment' AND INDEX_NAME='idx_order_id';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE product_comment ADD INDEX idx_order_id (order_id)', 'SELECT ''idx_order_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ============================================================
-- 模块2：C端用户 — 默认收货地址
-- ============================================================

-- user_address.is_default 已存在，补充索引
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='user_address' AND INDEX_NAME='idx_user_id';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE user_address ADD INDEX idx_user_id (user_id)', 'SELECT ''idx_user_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='user_address' AND INDEX_NAME='idx_is_default';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE user_address ADD INDEX idx_is_default (is_default)', 'SELECT ''idx_is_default 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ============================================================
-- 模块3：C端用户 — 物流轨迹
-- ============================================================

-- logistics_track 新增 order_id 字段，支持按订单查物流
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='logistics_track' AND COLUMN_NAME='order_id';
SET @sql = IF(@col_exists=0, 'ALTER TABLE logistics_track ADD COLUMN order_id BIGINT DEFAULT NULL COMMENT ''关联订单ID'' AFTER logistics_id', 'SELECT ''order_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 加索引
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='logistics_track' AND INDEX_NAME='idx_order_id';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE logistics_track ADD INDEX idx_order_id (order_id)', 'SELECT ''idx_order_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ============================================================
-- 模块4：C端用户 — 商品搜索（全文索引）
-- ============================================================

SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='product' AND INDEX_NAME='idx_product_name_ft';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE product ADD FULLTEXT INDEX idx_product_name_ft (product_name)', 'SELECT ''全文索引已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='product' AND INDEX_NAME='idx_category_status';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE product ADD INDEX idx_category_status (category_id, status)', 'SELECT ''idx_category_status 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ============================================================
-- 模块5：C端用户 — 商品收藏
-- ============================================================

SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='product_favorite' AND INDEX_NAME='uk_user_product';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE product_favorite ADD UNIQUE INDEX uk_user_product (user_id, product_id)', 'SELECT ''uk_user_product 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ============================================================
-- 模块6：农户端 — 订单发货 + 库存管理
-- ============================================================

-- order_log 补充索引
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='order_log' AND INDEX_NAME='idx_order_id';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE order_log ADD INDEX idx_order_id (order_id)', 'SELECT ''idx_order_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- order_info 加 farmer 相关索引
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='order_info' AND INDEX_NAME='idx_farmer_id';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE order_info ADD INDEX idx_farmer_id (farmer_id)', 'SELECT ''idx_farmer_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- product 补充 audit_remark（审核驳回原因）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='product' AND COLUMN_NAME='audit_remark';
SET @sql = IF(@col_exists=0, 'ALTER TABLE product ADD COLUMN audit_remark VARCHAR(500) DEFAULT NULL COMMENT ''审核驳回原因'' AFTER audit_status', 'SELECT ''audit_remark 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ============================================================
-- 模块7：管理后台 — 操作日志索引
-- ============================================================

SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='sys_operation_log' AND INDEX_NAME='idx_operator_id';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE sys_operation_log ADD INDEX idx_operator_id (user_id)', 'SELECT ''idx_operator_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ============================================================
-- 模块8：管理后台 — 角色权限菜单补充
-- ============================================================

-- 补充缺失的菜单项（INSERT IGNORE 幂等）
INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, perms, icon, sort, status, visible) VALUES
-- 系统管理子菜单补充
(4000000000000011, 4000000000000001, '管理员账号', 'C', '/admin/admins', 'system:admin:list', 'User', 4, 1, 1),
-- 溯源管理子菜单补充
(4000000000000012, 4000000000000007, '删除审核', 'C', '/admin/trace-delete-audit', 'trace:delete:audit', 'Delete', 2, 1, 1),
-- 订单管理子菜单补充
(4000000000000013, 4000000000000009, '售后工单', 'C', '/admin/after-sales', 'order:after-sales', 'Service', 2, 1, 1),
(4000000000000014, 4000000000000009, '支付记录', 'C', '/admin/payments', 'order:payment', 'Money', 3, 1, 1),
-- 商品管理子菜单补充
(4000000000000015, 4000000000000005, '分类管理', 'C', '/admin/categories', 'product:category:list', 'Menu', 2, 1, 1),
(4000000000000016, 4000000000000005, '评论审核', 'C', '/admin/comments', 'product:comment:list', 'ChatLineSquare', 3, 1, 1),
-- 运营管理
(4000000000000017, 0, '运营管理', 'M', NULL, NULL, 'TrendCharts', 4, 1, 1),
(4000000000000018, 4000000000000017, '轮播图管理', 'C', '/admin/banners', 'product:banner:list', 'Picture', 1, 1, 1),
(4000000000000019, 4000000000000017, '用户地址', 'C', '/admin/addresses', 'system:address:list', 'Location', 2, 1, 1),
-- 日志管理
(4000000000000020, 0, '日志管理', 'M', NULL, NULL, 'Document', 5, 1, 1),
(4000000000000021, 4000000000000020, '操作日志', 'C', '/admin/operation-logs', 'system:log:list', 'List', 1, 1, 1);

-- 补充超级管理员角色菜单关联（ROLE_ADMIN id=2000000000000001）
INSERT IGNORE INTO sys_role_menu (id, role_id, menu_id) VALUES
(3000000000000011, 2000000000000001, 4000000000000011),
(3000000000000012, 2000000000000001, 4000000000000012),
(3000000000000013, 2000000000000001, 4000000000000013),
(3000000000000014, 2000000000000001, 4000000000000014),
(3000000000000015, 2000000000000001, 4000000000000015),
(3000000000000016, 2000000000000001, 4000000000000016),
(3000000000000017, 2000000000000001, 4000000000000017),
(3000000000000018, 2000000000000001, 4000000000000018),
(3000000000000019, 2000000000000001, 4000000000000019),
(3000000000000020, 2000000000000001, 4000000000000020),
(3000000000000021, 2000000000000001, 4000000000000021);


-- ============================================================
-- 模块9：管理后台 — 管理员账号管理
-- ============================================================

-- sys_user 补充状态索引
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='sys_user' AND INDEX_NAME='idx_user_type';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE sys_user ADD INDEX idx_user_type (user_type)', 'SELECT ''idx_user_type 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='sys_user' AND INDEX_NAME='idx_status';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE sys_user ADD INDEX idx_status (status)', 'SELECT ''idx_status 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ============================================================
-- 模块10：通用 — 购物车索引补充
-- ============================================================

SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='shopping_cart' AND INDEX_NAME='idx_user_id';
SET @sql = IF(@idx_exists=0, 'ALTER TABLE shopping_cart ADD INDEX idx_user_id (user_id)', 'SELECT ''idx_user_id 已存在''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ============================================================
-- 验证脚本
-- ============================================================
SELECT 'P0 升级脚本执行完毕' AS result;
SELECT COUNT(*) AS 菜单总数 FROM sys_menu;
SELECT COUNT(*) AS 角色菜单关联 FROM sys_role_menu;
SELECT COUNT(*) AS 操作日志索引 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA='fresh_trace_shop' AND TABLE_NAME='sys_operation_log';
