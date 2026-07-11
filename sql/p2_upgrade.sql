-- ================================================================
-- P2 营销增长体系 + 管理端业务全闭环 — 数据库升级脚本
-- 全部幂等: IF NOT EXISTS / INSERT IGNORE
-- ================================================================

-- ============ 模块1：优惠券体系 ============
CREATE TABLE IF NOT EXISTS coupon (
  id           BIGINT       NOT NULL COMMENT '主键ID',
  name         VARCHAR(200) DEFAULT '' COMMENT '优惠券名称',
  type         VARCHAR(32)  DEFAULT 'full_reduce' COMMENT '类型: full_reduce/new_user/general',
  face_value   DECIMAL(10,2) DEFAULT 0 COMMENT '面额',
  min_amount   DECIMAL(10,2) DEFAULT 0 COMMENT '使用门槛(0=无门槛)',
  total_count  INT          DEFAULT 0 COMMENT '发放总量',
  taken_count  INT          DEFAULT 0 COMMENT '已领取数量',
  used_count   INT          DEFAULT 0 COMMENT '已使用数量',
  start_time   DATETIME     DEFAULT NULL COMMENT '有效期开始',
  end_time     DATETIME     DEFAULT NULL COMMENT '有效期结束',
  status       TINYINT      DEFAULT 1 COMMENT '状态: 0停用 1启用',
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_type (type),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

CREATE TABLE IF NOT EXISTS user_coupon (
  id           BIGINT       NOT NULL COMMENT '主键ID',
  user_id      BIGINT       NOT NULL COMMENT '用户ID',
  coupon_id    BIGINT       NOT NULL COMMENT '优惠券ID',
  status       VARCHAR(16)  DEFAULT 'unused' COMMENT '状态: unused/used/expired',
  order_id     BIGINT       DEFAULT NULL COMMENT '使用订单ID',
  take_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  use_time     DATETIME     DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (id),
  INDEX idx_user_id (user_id),
  INDEX idx_coupon_id (coupon_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券记录表';

-- ============ 模块2：营销活动 ============
CREATE TABLE IF NOT EXISTS marketing_activity (
  id           BIGINT       NOT NULL COMMENT '主键ID',
  name         VARCHAR(200) DEFAULT '' COMMENT '活动名称',
  type         VARCHAR(32)  DEFAULT 'discount' COMMENT '类型: discount/seckill/new_user',
  rule         VARCHAR(500) DEFAULT '' COMMENT '活动规则(JSON)',
  start_time   DATETIME     DEFAULT NULL COMMENT '开始时间',
  end_time     DATETIME     DEFAULT NULL COMMENT '结束时间',
  status       TINYINT      DEFAULT 0 COMMENT '状态: 0未开始 1进行中 2已结束',
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_status (status),
  INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动表';

CREATE TABLE IF NOT EXISTS marketing_goods (
  id           BIGINT NOT NULL COMMENT '主键ID',
  activity_id  BIGINT NOT NULL COMMENT '活动ID',
  product_id   BIGINT NOT NULL COMMENT '商品ID',
  PRIMARY KEY (id),
  INDEX idx_activity_id (activity_id),
  INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动关联商品表';

-- ============ 模块3：秒杀 ============
CREATE TABLE IF NOT EXISTS seckill (
  id            BIGINT        NOT NULL COMMENT '主键ID',
  product_id    BIGINT        NOT NULL COMMENT '商品ID',
  seckill_price DECIMAL(10,2) DEFAULT 0 COMMENT '秒杀价',
  stock         INT           DEFAULT 0 COMMENT '秒杀库存(独立)',
  sold          INT           DEFAULT 0 COMMENT '已售',
  start_time    DATETIME      DEFAULT NULL COMMENT '开始时间',
  end_time      DATETIME      DEFAULT NULL COMMENT '结束时间',
  status        TINYINT       DEFAULT 1 COMMENT '状态: 0关闭 1启用',
  create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE INDEX uk_product_id (product_id),
  INDEX idx_status (status),
  INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

-- ============ 模块4：补充order_info字段 ============
SELECT COUNT(*) INTO @col_plan FROM information_schema.COLUMNS WHERE table_schema=DATABASE() AND table_name='order_info' AND column_name='plan_amount';
SET @sql_plan = IF(@col_plan=0, 'ALTER TABLE order_info ADD COLUMN plan_amount DECIMAL(10,2) DEFAULT 0 COMMENT ''计划支付金额'' AFTER pay_amount', 'SELECT 1');
PREPARE stmt_plan FROM @sql_plan; EXECUTE stmt_plan; DEALLOCATE PREPARE stmt_plan;

SELECT COUNT(*) INTO @col_act FROM information_schema.COLUMNS WHERE table_schema=DATABASE() AND table_name='order_info' AND column_name='actual_amount';
SET @sql_act = IF(@col_act=0, 'ALTER TABLE order_info ADD COLUMN actual_amount DECIMAL(10,2) DEFAULT 0 COMMENT ''实际支付金额'' AFTER plan_amount', 'SELECT 1');
PREPARE stmt_act FROM @sql_act; EXECUTE stmt_act; DEALLOCATE PREPARE stmt_act;

SELECT COUNT(*) INTO @col_mkt FROM information_schema.COLUMNS WHERE table_schema=DATABASE() AND table_name='order_info' AND column_name='marketing_id';
SET @sql_mkt = IF(@col_mkt=0, 'ALTER TABLE order_info ADD COLUMN marketing_id BIGINT DEFAULT NULL COMMENT ''营销活动ID'' AFTER actual_amount', 'SELECT 1');
PREPARE stmt_mkt FROM @sql_mkt; EXECUTE stmt_mkt; DEALLOCATE PREPARE stmt_mkt;

SELECT COUNT(*) INTO @col_cpn FROM information_schema.COLUMNS WHERE table_schema=DATABASE() AND table_name='order_info' AND column_name='coupon_id';
SET @sql_cpn = IF(@col_cpn=0, 'ALTER TABLE order_info ADD COLUMN coupon_id BIGINT DEFAULT NULL COMMENT ''优惠券ID'' AFTER marketing_id', 'SELECT 1');
PREPARE stmt_cpn FROM @sql_cpn; EXECUTE stmt_cpn; DEALLOCATE PREPARE stmt_cpn;

-- 初始化数据：一张新人专享券
INSERT IGNORE INTO coupon (id, name, type, face_value, min_amount, total_count, start_time, end_time, status) VALUES
(9100000000000001, '新人专享券', 'new_user', 10.00, 0, 9999, '2025-01-01', '2030-12-31', 1),
(9100000000000002, '满100减20', 'full_reduce', 20.00, 100.00, 500, '2025-01-01', '2030-12-31', 1),
(9100000000000003, '通用无门槛券', 'general', 5.00, 0, 1000, '2025-01-01', '2030-12-31', 1);
