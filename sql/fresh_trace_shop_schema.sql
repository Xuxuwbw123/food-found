-- ============================================================
-- 生鲜农产品溯源电商平台 - 完整建库脚本（仅表结构，不含测试数据）
-- MySQL 5.7 兼容 | 35张表
-- 执行方式：mysql -u root -p < fresh_trace_shop_schema.sql
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `fresh_trace_shop`;
CREATE DATABASE `fresh_trace_shop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `fresh_trace_shop`;

-- ============================================================
-- 一、用户权限模块（5张表）
-- ============================================================

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `username` varchar(64) NOT NULL COMMENT '用户名/登录账号',
  `password` varchar(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint DEFAULT 0 COMMENT '性别（0未知1男2女）',
  `status` tinyint DEFAULT 1 COMMENT '状态（0禁用1启用）',
  `user_type` tinyint NOT NULL COMMENT '用户类型（1普通用户2农户3管理员）',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(32) DEFAULT NULL COMMENT '身份证号',
  `balance` decimal(10,2) DEFAULT 0.00 COMMENT '账户余额',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除（0未删除1已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_name` varchar(64) NOT NULL COMMENT '角色名称',
  `role_code` varchar(64) NOT NULL COMMENT '角色编码',
  `role_sort` int DEFAULT 0 COMMENT '显示排序',
  `status` tinyint DEFAULT 1 COMMENT '状态（0禁用1启用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `parent_id` bigint DEFAULT 0 COMMENT '父菜单ID',
  `menu_name` varchar(64) NOT NULL COMMENT '菜单名称',
  `menu_type` char(1) NOT NULL COMMENT '菜单类型（M目录C菜单F按钮）',
  `path` varchar(255) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `perms` varchar(128) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(64) DEFAULT NULL COMMENT '菜单图标',
  `sort` int DEFAULT 0 COMMENT '显示排序',
  `visible` tinyint DEFAULT 1 COMMENT '是否显示（0隐藏1显示）',
  `status` tinyint DEFAULT 1 COMMENT '状态（0停用1正常）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ============================================================
-- 二、农户商户模块（2张表）
-- ============================================================

DROP TABLE IF EXISTS `farmer`;
CREATE TABLE `farmer` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `farmer_name` varchar(128) NOT NULL COMMENT '农户/商户名称',
  `farmer_logo` varchar(255) DEFAULT NULL COMMENT '商户Logo',
  `contact_person` varchar(64) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `id_card_front` varchar(255) DEFAULT NULL COMMENT '身份证正面照',
  `id_card_back` varchar(255) DEFAULT NULL COMMENT '身份证反面照',
  `business_license` varchar(255) DEFAULT NULL COMMENT '营业执照',
  `province` varchar(64) DEFAULT NULL COMMENT '省份',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `district` varchar(64) DEFAULT NULL COMMENT '区县',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `farm_area` decimal(10,2) DEFAULT NULL COMMENT '农场面积（亩）',
  `farm_description` text COMMENT '农场简介',
  `main_products` varchar(500) DEFAULT NULL COMMENT '主营产品',
  `audit_status` tinyint DEFAULT 0 COMMENT '审核状态（0待审核1审核通过2审核拒绝）',
  `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `auditor_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `level` tinyint DEFAULT 1 COMMENT '商户等级（1普通2优质3金牌）',
  `score` decimal(3,2) DEFAULT 5.00 COMMENT '综合评分',
  `sales_count` int DEFAULT 0 COMMENT '销量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='农户/商户信息表';

DROP TABLE IF EXISTS `farmer_audit`;
CREATE TABLE `farmer_audit` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `farmer_id` bigint NOT NULL COMMENT '农户ID',
  `auditor_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `auditor_name` varchar(64) DEFAULT NULL COMMENT '审核人姓名',
  `audit_status` tinyint NOT NULL COMMENT '审核状态（1通过2拒绝）',
  `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_auditor_id` (`auditor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='农户审核记录表';

-- ============================================================
-- 三、商品管理模块（5张表）
-- ============================================================

DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `parent_id` bigint DEFAULT 0 COMMENT '父分类ID',
  `category_name` varchar(64) NOT NULL COMMENT '分类名称',
  `category_icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `category_image` varchar(255) DEFAULT NULL COMMENT '分类图片',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态（0禁用1启用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `product_no` varchar(64) NOT NULL COMMENT '商品编号',
  `product_name` varchar(255) NOT NULL COMMENT '商品名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `farmer_id` bigint NOT NULL COMMENT '农户/商户ID',
  `main_image` varchar(255) DEFAULT NULL COMMENT '商品主图',
  `price` decimal(10,2) NOT NULL COMMENT '销售价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `stock` int DEFAULT 0 COMMENT '库存数量',
  `sales` int DEFAULT 0 COMMENT '销量',
  `unit` varchar(32) DEFAULT NULL COMMENT '计量单位（斤/公斤/件/箱）',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '商品重量（kg）',
  `origin_place` varchar(255) DEFAULT NULL COMMENT '产地',
  `description` text COMMENT '商品详情描述',
  `specification` text COMMENT '规格参数（JSON格式）',
  `is_traceable` tinyint DEFAULT 1 COMMENT '是否可溯源（0否1是）',
  `trace_id` bigint DEFAULT NULL COMMENT '溯源信息ID',
  `status` tinyint DEFAULT 0 COMMENT '商品状态（0下架1上架2待审核）',
  `audit_status` tinyint DEFAULT 0 COMMENT '审核状态（0待审核1通过2拒绝）',
  `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `is_recommend` tinyint DEFAULT 0 COMMENT '是否推荐（0否1是）',
  `is_new` tinyint DEFAULT 0 COMMENT '是否新品（0否1是）',
  `is_hot` tinyint DEFAULT 0 COMMENT '是否热销（0否1是）',
  `sort` int DEFAULT 0 COMMENT '排序',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int DEFAULT 0 COMMENT '收藏次数',
  `comment_count` int DEFAULT 0 COMMENT '评论数',
  `good_rate` decimal(5,2) DEFAULT 100.00 COMMENT '好评率',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_no` (`product_no`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_recommend` (`is_recommend`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_is_new` (`is_new`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `image_type` tinyint DEFAULT 1 COMMENT '图片类型（1主图2详情图3规格图）',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

DROP TABLE IF EXISTS `product_comment`;
CREATE TABLE `product_comment` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint DEFAULT NULL COMMENT '订单ID',
  `order_item_id` bigint DEFAULT NULL COMMENT '订单项ID',
  `rating` tinyint NOT NULL COMMENT '评分（1-5星）',
  `content` text COMMENT '评论内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '评论图片（多个逗号分隔）',
  `is_anonymous` tinyint DEFAULT 0 COMMENT '是否匿名（0否1是）',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `reply_count` int DEFAULT 0 COMMENT '回复数',
  `status` tinyint DEFAULT 1 COMMENT '状态（0隐藏1显示）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评论表';

DROP TABLE IF EXISTS `product_favorite`;
CREATE TABLE `product_favorite` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品收藏表';

-- ============================================================
-- 四、溯源核心模块（10张表）
-- ============================================================

DROP TABLE IF EXISTS `traceability`;
CREATE TABLE `traceability` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_code` varchar(64) NOT NULL COMMENT '溯源编号（唯一）',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `farmer_id` bigint NOT NULL COMMENT '农户ID',
  `batch_no` varchar(64) DEFAULT NULL COMMENT '批次号',
  `product_name` varchar(255) DEFAULT NULL COMMENT '商品名称（冗余）',
  `origin_place` varchar(255) DEFAULT NULL COMMENT '产地',
  `farm_name` varchar(128) DEFAULT NULL COMMENT '农场名称',
  `responsible_person` varchar(64) DEFAULT NULL COMMENT '负责人',
  `responsible_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
  `planting_area` decimal(10,2) DEFAULT NULL COMMENT '种植面积（亩）',
  `seed_source` varchar(255) DEFAULT NULL COMMENT '种子来源',
  `planting_date` date DEFAULT NULL COMMENT '种植日期',
  `expected_harvest_date` date DEFAULT NULL COMMENT '预计采摘日期',
  `actual_harvest_date` date DEFAULT NULL COMMENT '实际采摘日期',
  `shelf_life` int DEFAULT NULL COMMENT '保质期（天）',
  `storage_condition` varchar(255) DEFAULT NULL COMMENT '储存条件',
  `total_nodes` int DEFAULT 0 COMMENT '溯源节点总数',
  `current_stage` varchar(64) DEFAULT NULL COMMENT '当前阶段（种植/采摘/质检/物流/仓储/销售）',
  `qrcode_url` varchar(255) DEFAULT NULL COMMENT '二维码图片URL',
  `status` tinyint DEFAULT 1 COMMENT '状态（0无效1有效）',
  `audit_status` tinyint DEFAULT 0 COMMENT '审核状态（0待审核1通过2拒绝）',
  `scan_count` int DEFAULT 0 COMMENT '累计扫码次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trace_code` (`trace_code`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_batch_no` (`batch_no`),
  KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='溯源主表';

DROP TABLE IF EXISTS `trace_planting`;
CREATE TABLE `trace_planting` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `planting_date` date DEFAULT NULL COMMENT '种植日期',
  `seed_variety` varchar(128) DEFAULT NULL COMMENT '种子品种',
  `seed_quantity` decimal(10,2) DEFAULT NULL COMMENT '播种量',
  `planting_method` varchar(255) DEFAULT NULL COMMENT '种植方式',
  `soil_type` varchar(64) DEFAULT NULL COMMENT '土壤类型',
  `climate_condition` varchar(255) DEFAULT NULL COMMENT '气候条件',
  `description` text COMMENT '种植说明',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) DEFAULT NULL COMMENT '现场图片（多个逗号分隔）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='种植记录表';

DROP TABLE IF EXISTS `trace_fertilizer`;
CREATE TABLE `trace_fertilizer` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `fertilize_date` date DEFAULT NULL COMMENT '施肥日期',
  `fertilizer_type` varchar(64) DEFAULT NULL COMMENT '肥料类型（有机肥/化肥/复合肥等）',
  `fertilizer_name` varchar(128) DEFAULT NULL COMMENT '肥料名称',
  `fertilizer_brand` varchar(128) DEFAULT NULL COMMENT '肥料品牌',
  `dosage` decimal(10,2) DEFAULT NULL COMMENT '用量（kg/亩）',
  `fertilize_method` varchar(255) DEFAULT NULL COMMENT '施肥方式',
  `description` text COMMENT '施肥说明',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='施肥记录表';

DROP TABLE IF EXISTS `trace_pesticide`;
CREATE TABLE `trace_pesticide` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `use_date` date DEFAULT NULL COMMENT '使用日期',
  `pesticide_type` varchar(64) DEFAULT NULL COMMENT '农药类型（杀虫剂/杀菌剂/除草剂等）',
  `pesticide_name` varchar(128) DEFAULT NULL COMMENT '农药名称',
  `pesticide_brand` varchar(128) DEFAULT NULL COMMENT '农药品牌',
  `dosage` decimal(10,2) DEFAULT NULL COMMENT '用量',
  `dilution_ratio` varchar(64) DEFAULT NULL COMMENT '稀释比例',
  `use_method` varchar(255) DEFAULT NULL COMMENT '使用方式',
  `safety_interval` int DEFAULT NULL COMMENT '安全间隔期（天）',
  `description` text COMMENT '使用说明',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='农药使用记录表';

DROP TABLE IF EXISTS `trace_irrigation`;
CREATE TABLE `trace_irrigation` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `irrigation_date` date DEFAULT NULL COMMENT '灌溉日期',
  `irrigation_type` varchar(64) DEFAULT NULL COMMENT '灌溉方式（滴灌/喷灌/漫灌等）',
  `water_volume` decimal(10,2) DEFAULT NULL COMMENT '用水量（立方米/亩）',
  `water_source` varchar(64) DEFAULT NULL COMMENT '水源类型',
  `duration` int DEFAULT NULL COMMENT '灌溉时长（分钟）',
  `weather` varchar(64) DEFAULT NULL COMMENT '天气情况',
  `description` text COMMENT '灌溉说明',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='灌溉记录表';

DROP TABLE IF EXISTS `trace_harvest`;
CREATE TABLE `trace_harvest` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `harvest_date` datetime DEFAULT NULL COMMENT '采摘时间',
  `harvest_quantity` decimal(10,2) DEFAULT NULL COMMENT '采摘数量',
  `harvest_unit` varchar(32) DEFAULT NULL COMMENT '单位（斤/公斤/吨）',
  `maturity` varchar(64) DEFAULT NULL COMMENT '成熟度',
  `harvest_method` varchar(255) DEFAULT NULL COMMENT '采摘方式',
  `storage_method` varchar(255) DEFAULT NULL COMMENT '采收后储存方式',
  `description` text COMMENT '采摘说明',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采摘记录表';

DROP TABLE IF EXISTS `trace_inspection`;
CREATE TABLE `trace_inspection` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `inspection_no` varchar(64) DEFAULT NULL COMMENT '质检报告编号',
  `inspection_date` date DEFAULT NULL COMMENT '质检日期',
  `inspection_agency` varchar(255) DEFAULT NULL COMMENT '质检机构',
  `inspector` varchar(64) DEFAULT NULL COMMENT '质检员',
  `inspection_type` varchar(64) DEFAULT NULL COMMENT '质检类型（自检/第三方检测）',
  `inspection_items` text COMMENT '检测项目（JSON格式）',
  `inspection_result` tinyint DEFAULT NULL COMMENT '检测结果（1合格2不合格）',
  `pesticide_residue` tinyint DEFAULT NULL COMMENT '农药残留检测（1合格2不合格）',
  `heavy_metal` tinyint DEFAULT NULL COMMENT '重金属检测（1合格2不合格）',
  `microorganism` tinyint DEFAULT NULL COMMENT '微生物检测（1合格2不合格）',
  `report_url` varchar(255) DEFAULT NULL COMMENT '质检报告PDF URL',
  `conclusion` text COMMENT '质检结论',
  `description` text COMMENT '备注说明',
  `images` varchar(1000) DEFAULT NULL COMMENT '质检图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inspection_no` (`inspection_no`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='质检报告表';

DROP TABLE IF EXISTS `trace_logistics`;
CREATE TABLE `trace_logistics` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `logistics_no` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `carrier_company` varchar(128) DEFAULT NULL COMMENT '承运公司',
  `driver_name` varchar(64) DEFAULT NULL COMMENT '司机姓名',
  `driver_phone` varchar(20) DEFAULT NULL COMMENT '司机电话',
  `vehicle_no` varchar(32) DEFAULT NULL COMMENT '车牌号',
  `vehicle_type` varchar(64) DEFAULT NULL COMMENT '车辆类型（冷藏车/保温车等）',
  `start_place` varchar(255) DEFAULT NULL COMMENT '出发地',
  `end_place` varchar(255) DEFAULT NULL COMMENT '目的地',
  `start_time` datetime DEFAULT NULL COMMENT '出发时间',
  `expect_arrive_time` datetime DEFAULT NULL COMMENT '预计到达时间',
  `actual_arrive_time` datetime DEFAULT NULL COMMENT '实际到达时间',
  `temperature_require` varchar(128) DEFAULT NULL COMMENT '温度要求',
  `min_temperature` decimal(5,2) DEFAULT NULL COMMENT '最低温度',
  `max_temperature` decimal(5,2) DEFAULT NULL COMMENT '最高温度',
  `current_temperature` decimal(5,2) DEFAULT NULL COMMENT '当前温度',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '湿度（%）',
  `transport_status` tinyint DEFAULT 0 COMMENT '运输状态（0待发货1运输中2已送达）',
  `current_location` varchar(255) DEFAULT NULL COMMENT '当前位置',
  `route_description` text COMMENT '运输路线描述',
  `images` varchar(1000) DEFAULT NULL COMMENT '物流图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_logistics_no` (`logistics_no`),
  KEY `idx_transport_status` (`transport_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='冷链物流记录表';

DROP TABLE IF EXISTS `trace_storage`;
CREATE TABLE `trace_storage` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `warehouse_name` varchar(128) DEFAULT NULL COMMENT '仓库名称',
  `warehouse_address` varchar(255) DEFAULT NULL COMMENT '仓库地址',
  `storage_location` varchar(128) DEFAULT NULL COMMENT '库位',
  `in_time` datetime DEFAULT NULL COMMENT '入库时间',
  `out_time` datetime DEFAULT NULL COMMENT '出库时间',
  `storage_status` tinyint DEFAULT 1 COMMENT '存储状态（1在库2已出库）',
  `temperature` decimal(5,2) DEFAULT NULL COMMENT '储存温度',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '储存湿度',
  `quantity` decimal(10,2) DEFAULT NULL COMMENT '存储数量',
  `unit` varchar(32) DEFAULT NULL COMMENT '单位',
  `keeper` varchar(64) DEFAULT NULL COMMENT '保管员',
  `description` text COMMENT '存储说明',
  `images` varchar(1000) DEFAULT NULL COMMENT '仓储图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_storage_status` (`storage_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓储记录表';

DROP TABLE IF EXISTS `trace_image`;
CREATE TABLE `trace_image` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `image_type` varchar(64) DEFAULT NULL COMMENT '图片类型（种植/施肥/农药/灌溉/采摘/质检/物流/仓储）',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `image_desc` varchar(255) DEFAULT NULL COMMENT '图片描述',
  `sort` int DEFAULT 0 COMMENT '排序',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_image_type` (`image_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='溯源图片表';

-- ============================================================
-- 五、订单购物模块（6张表）
-- ============================================================

DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(255) DEFAULT NULL COMMENT '商品名称（冗余）',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片（冗余）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '商品单价',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `selected` tinyint DEFAULT 1 COMMENT '是否选中（0未选中1选中）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `farmer_id` bigint DEFAULT NULL COMMENT '商户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `freight_amount` decimal(10,2) DEFAULT 0.00 COMMENT '运费',
  `discount_amount` decimal(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `total_quantity` int DEFAULT NULL COMMENT '商品总数量',
  `pay_type` tinyint DEFAULT NULL COMMENT '支付方式（1微信2支付宝3余额）',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `order_status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态（0待付款1待发货2已发货3已完成4已取消5售后中）',
  `delivery_type` tinyint DEFAULT NULL COMMENT '配送方式（1快递2自提）',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
  `receiver_name` varchar(64) DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '收货人电话',
  `receiver_province` varchar(64) DEFAULT NULL COMMENT '省',
  `receiver_city` varchar(64) DEFAULT NULL COMMENT '市',
  `receiver_district` varchar(64) DEFAULT NULL COMMENT '区',
  `receiver_address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `logistics_no` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `logistics_company` varchar(128) DEFAULT NULL COMMENT '物流公司',
  `order_remark` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `admin_remark` varchar(500) DEFAULT NULL COMMENT '管理员备注',
  `is_invoiced` tinyint DEFAULT 0 COMMENT '是否已开票（0否1是）',
  `invoice_type` tinyint DEFAULT NULL COMMENT '发票类型',
  `invoice_title` varchar(255) DEFAULT NULL COMMENT '发票抬头',
  `is_comment` tinyint DEFAULT 0 COMMENT '是否已评价（0否1是）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_pay_type` (`pay_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单编号（冗余）',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(255) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `product_no` varchar(64) DEFAULT NULL COMMENT '商品编号',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `farmer_id` bigint DEFAULT NULL COMMENT '商户ID',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '商品原价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '商品总价',
  `unit` varchar(32) DEFAULT NULL COMMENT '单位',
  `spec_info` varchar(500) DEFAULT NULL COMMENT '规格信息',
  `trace_id` bigint DEFAULT NULL COMMENT '溯源ID',
  `is_comment` tinyint DEFAULT 0 COMMENT '是否已评价',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

DROP TABLE IF EXISTS `payment_info`;
CREATE TABLE `payment_info` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `payment_no` varchar(64) DEFAULT NULL COMMENT '支付流水号',
  `pay_type` tinyint NOT NULL COMMENT '支付方式（1微信2支付宝3余额）',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `pay_status` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态（0待支付1支付成功2支付失败3已退款）',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `third_party_no` varchar(128) DEFAULT NULL COMMENT '第三方支付单号',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `refund_reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pay_status` (`pay_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单编号（冗余）',
  `order_status` tinyint NOT NULL COMMENT '变更后的订单状态',
  `operator_type` tinyint NOT NULL DEFAULT 1 COMMENT '操作人类型（1用户2农户3管理员4系统）',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '操作说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单状态流转日志表';

-- ============================================================
-- 六、地址与轮播模块（2张表）
-- ============================================================

DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(64) DEFAULT NULL COMMENT '省份',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `district` varchar(64) DEFAULT NULL COMMENT '区县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `postal_code` varchar(20) DEFAULT NULL COMMENT '邮政编码',
  `is_default` tinyint DEFAULT 0 COMMENT '是否默认地址（0否1是）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';

DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `link_type` tinyint DEFAULT 0 COMMENT '链接类型（0无1商品2分类3外部链接）',
  `link_id` bigint DEFAULT NULL COMMENT '链接ID（商品ID/分类ID）',
  `link_url` varchar(255) DEFAULT NULL COMMENT '外部链接地址',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态（0禁用1启用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- ============================================================
-- 七、扩展模块（2张表）
-- ============================================================

DROP TABLE IF EXISTS `trace_scan_log`;
CREATE TABLE `trace_scan_log` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '关联溯源ID',
  `trace_code` varchar(64) DEFAULT NULL COMMENT '溯源码',
  `user_id` bigint DEFAULT NULL COMMENT '扫码用户ID（可为空，游客扫码）',
  `scan_ip` varchar(64) DEFAULT NULL COMMENT '扫码IP地址',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '浏览器UA信息',
  `scan_location` varchar(128) DEFAULT NULL COMMENT '扫码地点',
  `scan_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '扫码时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_scan_time` (`scan_time`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='溯源扫码日志表';

DROP TABLE IF EXISTS `logistics_track`;
CREATE TABLE `logistics_track` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `logistics_id` bigint NOT NULL COMMENT '关联物流ID（trace_logistics.id）',
  `station` varchar(128) DEFAULT NULL COMMENT '当前站点',
  `location` varchar(128) DEFAULT NULL COMMENT '地理位置（经纬度或地址）',
  `temperature` decimal(5,2) DEFAULT NULL COMMENT '冷链温度（℃）',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '湿度（%）',
  `track_time` datetime DEFAULT NULL COMMENT '轨迹记录时间',
  `description` varchar(255) DEFAULT NULL COMMENT '轨迹描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_logistics_id` (`logistics_id`),
  KEY `idx_track_time` (`track_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流轨迹/冷链温度明细表';

-- ============================================================
-- 八、溯源养殖加工模块（2张表）
-- ============================================================

DROP TABLE IF EXISTS `trace_breeding`;
CREATE TABLE `trace_breeding` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `breed_type` varchar(32) NOT NULL COMMENT '养殖类型（畜禽/水产）',
  `breed_date` date DEFAULT NULL COMMENT '养殖/放养日期',
  `species` varchar(128) DEFAULT NULL COMMENT '养殖品种',
  `breed_quantity` decimal(10,2) DEFAULT NULL COMMENT '养殖数量',
  `breed_unit` varchar(32) DEFAULT NULL COMMENT '单位（头/只/尾/羽）',
  `breed_density` varchar(64) DEFAULT NULL COMMENT '养殖密度',
  `breed_area` decimal(10,2) DEFAULT NULL COMMENT '养殖面积（亩/平方米）',
  `feed_brand` varchar(128) DEFAULT NULL COMMENT '饲料品牌',
  `feed_name` varchar(128) DEFAULT NULL COMMENT '饲料名称',
  `feed_type` varchar(64) DEFAULT NULL COMMENT '饲料类型（天然饲料/配合饲料/有机饲料）',
  `feed_source` varchar(255) DEFAULT NULL COMMENT '饲料来源',
  `medicine_name` varchar(128) DEFAULT NULL COMMENT '兽药名称',
  `medicine_brand` varchar(128) DEFAULT NULL COMMENT '兽药品牌',
  `medicine_usage` varchar(64) DEFAULT NULL COMMENT '兽药用量',
  `medicine_interval` int DEFAULT NULL COMMENT '停药期（天）',
  `vaccination_batch` varchar(64) DEFAULT NULL COMMENT '防疫批次号',
  `vaccination_name` varchar(128) DEFAULT NULL COMMENT '疫苗名称',
  `vaccination_date` date DEFAULT NULL COMMENT '防疫日期',
  `disinfection_method` varchar(255) DEFAULT NULL COMMENT '圈舍/塘口消毒方式',
  `disinfection_date` date DEFAULT NULL COMMENT '消毒日期',
  `disinfection_drug` varchar(128) DEFAULT NULL COMMENT '消毒药品',
  `water_quality` varchar(255) DEFAULT NULL COMMENT '水质指标（水产专用，如pH/溶氧/氨氮）',
  `environment` varchar(255) DEFAULT NULL COMMENT '养殖环境（圈舍/池塘/网箱/水库）',
  `description` text COMMENT '养殖说明',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='养殖记录表';

DROP TABLE IF EXISTS `trace_processing`;
CREATE TABLE `trace_processing` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `processing_date` datetime DEFAULT NULL COMMENT '加工时间',
  `processing_type` varchar(64) NOT NULL COMMENT '加工类型（分拣/清洗/切割/包装/灭菌/冷冻）',
  `processing_method` varchar(255) DEFAULT NULL COMMENT '加工方式说明',
  `process_temperature` decimal(5,2) DEFAULT NULL COMMENT '加工温度（℃）',
  `process_duration` int DEFAULT NULL COMMENT '加工时长（分钟）',
  `package_spec` varchar(128) DEFAULT NULL COMMENT '包装规格（如500g/盒、2斤/箱）',
  `package_material` varchar(128) DEFAULT NULL COMMENT '包装材料（真空袋/泡沫箱/纸箱）',
  `additive_name` varchar(128) DEFAULT NULL COMMENT '添加剂名称（防腐剂/保鲜剂，无则为空）',
  `additive_brand` varchar(128) DEFAULT NULL COMMENT '添加剂品牌',
  `additive_amount` varchar(64) DEFAULT NULL COMMENT '添加剂用量',
  `batch_no` varchar(64) DEFAULT NULL COMMENT '加工批次号',
  `quality_check` tinyint DEFAULT NULL COMMENT '出厂检验（1合格 2不合格）',
  `output_quantity` decimal(10,2) DEFAULT NULL COMMENT '产出数量',
  `output_unit` varchar(32) DEFAULT NULL COMMENT '产出单位',
  `shelf_life` int DEFAULT NULL COMMENT '加工后保质期（天）',
  `storage_require` varchar(255) DEFAULT NULL COMMENT '储存要求',
  `description` text COMMENT '加工说明',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='加工包装记录表';

-- ============================================================
-- 九、售后客服模块（2张表）
-- ============================================================

DROP TABLE IF EXISTS `after_sales_order`;
CREATE TABLE `after_sales_order` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `after_sales_no` varchar(64) NOT NULL COMMENT '售后单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单编号（冗余）',
  `order_item_id` bigint DEFAULT NULL COMMENT '订单明细ID',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `farmer_id` bigint DEFAULT NULL COMMENT '商户ID',
  `product_id` bigint DEFAULT NULL COMMENT '商品ID',
  `product_name` varchar(255) DEFAULT NULL COMMENT '商品名称（冗余）',
  `after_sales_type` tinyint NOT NULL COMMENT '售后类型（1仅退款 2退货退款 3换货 4补发）',
  `reason` varchar(255) NOT NULL COMMENT '申请原因',
  `description` text COMMENT '问题描述',
  `evidence_images` varchar(1000) DEFAULT NULL COMMENT '举证图片（多个逗号分隔）',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `return_address` varchar(255) DEFAULT NULL COMMENT '退货地址',
  `return_logistics_no` varchar(64) DEFAULT NULL COMMENT '退货物流单号',
  `return_logistics_company` varchar(128) DEFAULT NULL COMMENT '退货物流公司',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0待审核 1审核通过 2审核拒绝 3待退货 4已退货 5处理中 6已完成 7已关闭）',
  `admin_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `admin_remark` varchar(500) DEFAULT NULL COMMENT '处理意见',
  `user_remark` varchar(500) DEFAULT NULL COMMENT '用户补充说明',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `close_time` datetime DEFAULT NULL COMMENT '关闭时间',
  `close_reason` varchar(500) DEFAULT NULL COMMENT '关闭原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_after_sales_no` (`after_sales_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_apply_time` (`apply_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='售后工单表';

DROP TABLE IF EXISTS `customer_service_log`;
CREATE TABLE `customer_service_log` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `after_sales_id` bigint NOT NULL COMMENT '售后工单ID',
  `order_id` bigint DEFAULT NULL COMMENT '订单ID（冗余）',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `operator_type` tinyint NOT NULL COMMENT '操作人类型（1用户 2客服/农户 3管理员 4系统）',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人名称',
  `msg_type` tinyint DEFAULT 1 COMMENT '消息类型（1文字 2图片 3系统通知）',
  `content` text COMMENT '沟通内容',
  `image_urls` varchar(1000) DEFAULT NULL COMMENT '图片（多个逗号分隔）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_after_sales_id` (`after_sales_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服沟通日志表';
