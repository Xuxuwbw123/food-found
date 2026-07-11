-- ================================================================
-- P0 验收修复 #1: 创建 sys_operation_log 表（致命问题）
-- 验收报告问题: sys_operation_log 表从未创建，p0_upgrade.sql 执行报错
-- 目标: 字段与 log-middleware.js 完全匹配，幂等执行
-- ================================================================

CREATE TABLE IF NOT EXISTS sys_operation_log (
  id            BIGINT        NOT NULL COMMENT '主键ID (Snowflake)',
  user_id       BIGINT        DEFAULT NULL COMMENT '操作人ID',
  username      VARCHAR(64)   DEFAULT '' COMMENT '操作人用户名',
  module        VARCHAR(100)  DEFAULT '' COMMENT '操作模块',
  action        VARCHAR(100)  DEFAULT '' COMMENT '操作类型 (CREATE/UPDATE/DELETE)',
  target        VARCHAR(200)  DEFAULT '' COMMENT '操作目标',
  req_method    VARCHAR(10)   DEFAULT '' COMMENT '请求方法 (GET/POST/PUT/DELETE)',
  req_path      VARCHAR(500)  DEFAULT '' COMMENT '请求路径',
  req_ip        VARCHAR(50)   DEFAULT '' COMMENT '请求IP',
  req_params    VARCHAR(1000) DEFAULT '' COMMENT '请求参数 (截断500字符)',
  result        VARCHAR(20)   DEFAULT 'SUCCESS' COMMENT '操作结果 (SUCCESS/FAIL)',
  duration      INT           DEFAULT 0 COMMENT '执行耗时 (ms)',
  create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_user_id (user_id),
  INDEX idx_module (module),
  INDEX idx_create_time (create_time),
  INDEX idx_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';
