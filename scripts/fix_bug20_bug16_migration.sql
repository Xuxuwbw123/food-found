-- Bug #20 fix: after_sales_order 关键字段加 NOT NULL 约束
-- Bug #16 fix: user_coupon 表加 (user_id, coupon_id) 唯一索引，防重复领
--
-- 执行方式:
--   mysql -u root -p123456 fresh_trace_shop < scripts/fix_bug20_bug16_migration.sql
--
-- 事前保证: 先跑 scripts/fix_bug20_bug16_check.sql 确认没有脏数据

USE fresh_trace_shop;

-- =========================================================
-- Bug #20 fix: after_sales_order NOT NULL 约束
-- =========================================================

-- 先清理历史脏数据（把 NULL 关键字段的售后单标记为已关闭）
UPDATE after_sales_order
SET status = 7, close_reason = '数据不完整-系统自动关闭'
WHERE (product_id IS NULL OR order_item_id IS NULL OR refund_amount IS NULL)
  AND status < 6;

-- 修改列 NOT NULL（对已关闭数据用默认值填充）
UPDATE after_sales_order SET product_id      = 0    WHERE product_id      IS NULL;
UPDATE after_sales_order SET order_item_id   = 0    WHERE order_item_id   IS NULL;
UPDATE after_sales_order SET refund_amount   = 0.00 WHERE refund_amount   IS NULL;

ALTER TABLE after_sales_order
    MODIFY COLUMN product_id     BIGINT       NOT NULL DEFAULT 0   COMMENT '商品ID',
    MODIFY COLUMN order_item_id  BIGINT       NOT NULL DEFAULT 0   COMMENT '订单明细ID',
    MODIFY COLUMN refund_amount  DECIMAL(10,2) NOT NULL DEFAULT 0  COMMENT '退款金额';

-- =========================================================
-- Bug #16 fix: user_coupon 唯一索引 (user_id, coupon_id)
-- =========================================================

-- 先清理重复数据（保留每个 (user_id, coupon_id) 最早那条）
DELETE uc1 FROM user_coupon uc1
INNER JOIN user_coupon uc2
    ON uc1.user_id = uc2.user_id
   AND uc1.coupon_id = uc2.coupon_id
   AND uc1.id > uc2.id;

-- 加唯一索引（如果不存在）
-- MySQL 8 支持 IF NOT EXISTS，但语法在 CREATE INDEX 上不同；用存在性检查
SET @idx_exists = (SELECT COUNT(*) FROM information_schema.statistics
                   WHERE table_schema = DATABASE() AND table_name = 'user_coupon'
                     AND index_name = 'uk_user_coupon');
SET @sql = IF(@idx_exists = 0,
              'CREATE UNIQUE INDEX uk_user_coupon ON user_coupon(user_id, coupon_id)',
              'SELECT ''index already exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 验证
SELECT '=== after_sales_order 约束检查 ===' AS section;
SELECT COLUMN_NAME, IS_NULLABLE, DATA_TYPE FROM information_schema.columns
WHERE table_schema = DATABASE() AND table_name = 'after_sales_order'
  AND column_name IN ('product_id','order_item_id','refund_amount');

SELECT '=== user_coupon 索引检查 ===' AS section;
SELECT INDEX_NAME, COLUMN_NAME, NON_UNIQUE FROM information_schema.statistics
WHERE table_schema = DATABASE() AND table_name = 'user_coupon' AND index_name = 'uk_user_coupon';
