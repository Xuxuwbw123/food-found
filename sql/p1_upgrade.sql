-- ================================================================
-- P1 运营提效与业务深化 — 数据库升级脚本
-- 全部幂等：IF NOT EXISTS / INSERT IGNORE
-- 顺序：建表 → 加字段 → 加索引 → 初始化数据
-- ================================================================

-- ============ 模块1：站内消息通知中心 ============
CREATE TABLE IF NOT EXISTS sys_notice (
  id           BIGINT       NOT NULL COMMENT '主键ID (Snowflake)',
  user_id      BIGINT       NOT NULL COMMENT '接收用户ID',
  notice_type  VARCHAR(32)  DEFAULT 'system' COMMENT '消息类型: order/after_sales/system',
  title        VARCHAR(200) DEFAULT '' COMMENT '消息标题',
  content      VARCHAR(1000) DEFAULT '' COMMENT '消息内容',
  relation_id  BIGINT       DEFAULT NULL COMMENT '关联业务ID(订单ID/售后ID等)',
  is_read      TINYINT      DEFAULT 0 COMMENT '是否已读 0否 1是',
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_user_id (user_id),
  INDEX idx_is_read (is_read),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息通知表';

-- ============ 模块2：会员积分体系 ============
CREATE TABLE IF NOT EXISTS member_point (
  user_id         BIGINT  NOT NULL COMMENT '用户ID',
  total_point     INT     DEFAULT 0 COMMENT '累计积分',
  available_point INT     DEFAULT 0 COMMENT '可用积分',
  freeze_point    INT     DEFAULT 0 COMMENT '冻结积分',
  update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分表';

CREATE TABLE IF NOT EXISTS point_log (
  id           BIGINT       NOT NULL COMMENT '主键ID (Snowflake)',
  user_id      BIGINT       NOT NULL COMMENT '用户ID',
  type         VARCHAR(32)  DEFAULT '' COMMENT '类型: earn/use/refund/freeze',
  point        INT          DEFAULT 0 COMMENT '积分变动(正=获得,负=扣减)',
  balance      INT          DEFAULT 0 COMMENT '变动后余额',
  remark       VARCHAR(500) DEFAULT '' COMMENT '备注',
  relation_id  BIGINT       DEFAULT NULL COMMENT '关联业务ID',
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_user_id (user_id),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分流水表';

-- ============ 模块3：浏览足迹 ============
CREATE TABLE IF NOT EXISTS user_footprint (
  id           BIGINT   NOT NULL COMMENT '主键ID (Snowflake)',
  user_id      BIGINT   NOT NULL COMMENT '用户ID',
  product_id   BIGINT   NOT NULL COMMENT '商品ID',
  browse_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (id),
  UNIQUE INDEX uk_user_product (user_id, product_id),
  INDEX idx_user_id (user_id),
  INDEX idx_browse_time (browse_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览足迹表';

-- ============ 模块4：系统配置中心 ============
CREATE TABLE IF NOT EXISTS sys_config (
  id           BIGINT       NOT NULL COMMENT '主键ID (Snowflake)',
  config_key   VARCHAR(100) NOT NULL COMMENT '配置键',
  config_value VARCHAR(500) DEFAULT '' COMMENT '配置值',
  config_name  VARCHAR(200) DEFAULT '' COMMENT '配置名称',
  config_group VARCHAR(50)  DEFAULT 'base' COMMENT '配置分组: base/trade/point/security',
  remark       VARCHAR(500) DEFAULT '' COMMENT '备注说明',
  update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE INDEX uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ============ 初始化配置数据 ============
INSERT IGNORE INTO sys_config (id, config_key, config_value, config_name, config_group, remark) VALUES
(9000000000000101, 'login_max_attempts', '5', '登录最大尝试次数', 'security', '超过此次数锁定账户'),
(9000000000000102, 'login_lock_minutes', '30', '登录锁定时长(分钟)', 'security', '账户锁定后自动解锁时间'),
(9000000000000103, 'password_expire_days', '90', '密码有效期(天)', 'security', '超过天数强制修改密码'),
(9000000000000104, 'order_auto_confirm_days', '15', '订单自动收货天数', 'trade', '发货后自动确认收货'),
(9000000000000105, 'after_sales_deadline_days', '7', '售后申请时效(天)', 'trade', '确认收货后可申请售后的天数'),
(9000000000000106, 'point_exchange_rate', '100', '积分兑换比例', 'point', '多少积分兑换1元'),
(9000000000000107, 'point_earn_rate', '1', '积分赠送比例', 'point', '消费1元赠送多少积分'),
(9000000000000108, 'footprint_max_count', '50', '浏览足迹最大保留数', 'base', '每个用户最多保留的足迹条数');

-- ============ 补充索引 (幂等) ============
-- customer_service_log 索引
SELECT COUNT(*) INTO @idx_cs_exists FROM information_schema.STATISTICS WHERE table_schema=DATABASE() AND table_name='customer_service_log' AND index_name='idx_after_sales_id';
SET @sql_cs = IF(@idx_cs_exists=0, 'ALTER TABLE customer_service_log ADD INDEX idx_after_sales_id (after_sales_id)', 'SELECT 1');
PREPARE stmt_cs FROM @sql_cs; EXECUTE stmt_cs; DEALLOCATE PREPARE stmt_cs;

-- farmer_audit 索引 - 仅在表存在且有 status 列时添加
SELECT COUNT(*) INTO @fa_exists FROM information_schema.TABLES WHERE table_schema=DATABASE() AND table_name='farmer_audit';
SELECT COUNT(*) INTO @fa_col FROM information_schema.COLUMNS WHERE table_schema=DATABASE() AND table_name='farmer_audit' AND column_name='audit_status';
SET @sql_fa = IF(@fa_exists>0 AND @fa_col>0, 'ALTER TABLE farmer_audit ADD INDEX idx_audit_status (audit_status)', 'SELECT 1');
SELECT COUNT(*) INTO @fa_idx FROM information_schema.STATISTICS WHERE table_schema=DATABASE() AND table_name='farmer_audit' AND index_name='idx_audit_status';
SET @sql_fa = IF(@fa_exists>0 AND @fa_col>0 AND @fa_idx=0, 'ALTER TABLE farmer_audit ADD INDEX idx_audit_status (audit_status)', 'SELECT 1');
PREPARE stmt_fa FROM @sql_fa; EXECUTE stmt_fa; DEALLOCATE PREPARE stmt_fa;
