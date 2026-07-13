-- MySQL dump 10.13  Distrib 5.7.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: fresh_trace_shop
-- ------------------------------------------------------
-- Server version	5.7.34-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `fresh_trace_shop`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `fresh_trace_shop` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `fresh_trace_shop`;

--
-- Table structure for table `after_sales_order`
--

DROP TABLE IF EXISTS `after_sales_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `after_sales_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `after_sales_no` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '售后单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单编号（冗余）',
  `order_item_id` bigint(20) DEFAULT NULL COMMENT '订单明细ID',
  `user_id` bigint(20) NOT NULL COMMENT '申请用户ID',
  `farmer_id` bigint(20) DEFAULT NULL COMMENT '商户ID',
  `product_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品名称（冗余）',
  `after_sales_type` tinyint(4) NOT NULL COMMENT '售后类型（1仅退款 2退货退款 3换货 4补发）',
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请原因',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '问题描述',
  `evidence_images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '举证图片（多个逗号分隔）',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `return_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退货地址',
  `return_logistics_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退货物流单号',
  `return_logistics_company` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退货物流公司',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0待审核 1审核通过 2审核拒绝 3待退货 4已退货 5处理中 6已完成 7已关闭）',
  `admin_id` bigint(20) DEFAULT NULL COMMENT '处理人ID',
  `admin_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理意见',
  `user_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户补充说明',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `close_time` datetime DEFAULT NULL COMMENT '关闭时间',
  `close_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关闭原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_after_sales_no` (`after_sales_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_apply_time` (`apply_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3500000000000006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='售后工单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `after_sales_order`
--

LOCK TABLES `after_sales_order` WRITE;
/*!40000 ALTER TABLE `after_sales_order` DISABLE KEYS */;
INSERT INTO `after_sales_order` VALUES (3500000000000001,'AS202504120001',2400000000000001,'ORD20250410000001',2500000000000002,1000000000000005,6000000000000001,9000000000000005,'有机黄瓜（5斤装）',2,'商品破损','收到发现3根黄瓜断裂，箱子有挤压痕迹','/evidence/as01_01.jpg,/evidence/as01_02.jpg',19.90,'湖北武汉黄陂区前川街农业园1号 张三 13900000001','SF1234567890','顺丰速运',6,1000000000000001,'已核实，同意退货退款','申请退货退款','2025-04-12 10:00:00','2025-04-12 14:00:00','2025-04-14 16:00:00',NULL,NULL,'2026-06-29 14:26:45','2026-06-29 14:26:45',0),(3500000000000002,'AS202504130001',2400000000000002,'ORD20250412000001',2500000000000003,1000000000000005,6000000000000002,9000000000000002,'从化桂味荔枝（3斤装）',1,'口感异常','荔枝有酒味，疑似变质','/evidence/as02_01.jpg',68.00,NULL,NULL,NULL,0,NULL,NULL,'收到就有酒味，不敢吃','2025-04-13 09:00:00',NULL,NULL,NULL,NULL,'2026-06-29 14:26:45','2026-06-29 14:26:45',0),(3500000000000003,'AS202504160001',2400000000000003,'ORD20250415000001',2500000000000004,1000000000000005,6000000000000004,9000000000000004,'鲜活鲈鱼（2条装）',4,'收到死亡','收到时鲈鱼已死亡，不新鲜','/evidence/as03_01.jpg',NULL,NULL,NULL,NULL,5,1000000000000002,'协调商户补发一条活鱼',NULL,'2025-04-16 11:00:00','2025-04-16 14:00:00',NULL,NULL,NULL,'2026-06-29 14:26:45','2026-06-29 14:26:45',0),(3500000000000004,'AS202504190001',2400000000000005,'ORD20250420000001',2500000000000005,1000000000000001,6000000000000001,9000000000000001,'有机番茄（5斤装）',3,'少发货','订单2箱只收到1箱','/evidence/as04_01.jpg',NULL,NULL,NULL,NULL,1,1000000000000001,'核实物流重量，确认少发，同意补发',NULL,'2025-04-19 08:00:00','2025-04-19 10:00:00',NULL,NULL,NULL,'2026-06-29 14:26:45','2026-06-29 14:26:45',0),(3500000000000005,'AS202504200001',2400000000000001,'ORD20250410000001',2500000000000001,1000000000000005,6000000000000001,9000000000000001,'有机番茄（5斤装）',2,'部分腐烂','箱子底部5个番茄发霉','/evidence/as05_01.jpg,/evidence/as05_02.jpg',14.95,'湖北武汉黄陂区前川街农业园1号 张三 13900000001',NULL,NULL,7,1000000000000002,'用户超时未退货，自动关闭','申请半箱退款','2025-04-20 15:00:00','2025-04-20 16:00:00',NULL,'2025-04-25 16:00:00','用户超时未退货','2026-06-29 14:26:45','2026-06-29 14:26:45',0);
/*!40000 ALTER TABLE `after_sales_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `banner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `link_type` tinyint(4) DEFAULT '0' COMMENT '链接类型（0无1商品2分类3外部链接）',
  `link_id` bigint(20) DEFAULT NULL COMMENT '链接ID（商品ID/分类ID）',
  `link_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部链接地址',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态（0禁用1启用）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB AUTO_INCREMENT=2075830397703913474 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (2800000000000001,'有机番茄限时特惠','/images/banners/番茄特惠.png',1,9000000000000001,'',1,1,'首页轮播','2026-06-29 12:51:02','2026-07-10 14:28:46',0),(2800000000000002,'从化荔枝预售开启','/images/banners/荔枝预售.png',1,9000000000000002,NULL,2,1,'荔枝季节促销','2026-06-29 12:51:02','2026-07-01 11:51:35',0),(2800000000000003,'溯源查询 - 吃得放心','/images/banners/溯源查询.png',3,NULL,'/pages/trace/query',3,1,'跳转溯源查询','2026-06-29 12:51:02','2026-07-01 11:51:35',0),(2800000000000004,'新用户专享红包','/images/banners/新用户红包.png',3,NULL,'/pages/activity/new',4,1,'新用户活动','2026-06-29 12:51:02','2026-07-01 11:51:35',0),(2800000000000005,'冷链物流保障','/images/banners/冷链物流.png',0,NULL,NULL,5,1,'物流保障宣传','2026-06-29 12:51:02','2026-07-07 15:15:28',0),(2075830397703913473,'t','/t.png',0,NULL,NULL,0,1,NULL,'2026-07-11 14:31:48','2026-07-11 15:38:39',1);
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coupon` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(200) DEFAULT '' COMMENT '优惠券名称',
  `type` varchar(32) DEFAULT 'full_reduce' COMMENT '类型: full_reduce/new_user/general',
  `face_value` decimal(10,2) DEFAULT '0.00' COMMENT '面额',
  `min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '使用门槛(0=无门槛)',
  `total_count` int(11) DEFAULT '0' COMMENT '发放总量',
  `taken_count` int(11) DEFAULT '0' COMMENT '已领取数量',
  `used_count` int(11) DEFAULT '0' COMMENT '已使用数量',
  `start_time` datetime DEFAULT NULL COMMENT '有效期开始',
  `end_time` datetime DEFAULT NULL COMMENT '有效期结束',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态: 0停用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon`
--

LOCK TABLES `coupon` WRITE;
/*!40000 ALTER TABLE `coupon` DISABLE KEYS */;
INSERT INTO `coupon` VALUES (9100000000000001,'新人专享券(注册自动发放)','new_user',10.00,0.00,9999,2,0,'2025-01-01 00:00:00','2030-12-31 00:00:00',1,'2026-07-08 15:37:11'),(9100000000000002,'满100减20','full_reduce',20.00,100.00,9999,1,0,'2025-01-01 00:00:00','2030-12-31 00:00:00',1,'2026-07-08 15:37:11'),(9100000000000003,'通用无门槛券','general',5.00,0.00,9999,2,0,'2025-01-01 00:00:00','2030-12-31 00:00:00',1,'2026-07-08 15:37:11'),(350215226769174528,'123','full_reduce',10.00,0.00,100,0,0,'2026-07-08 00:00:00','2026-08-09 00:00:00',1,'2026-07-08 16:03:36'),(350215274890432512,'123123','new_user',10.00,0.00,100,0,0,'2026-07-08 00:00:00','2026-08-08 00:00:00',1,'2026-07-08 16:03:48'),(350215332402737152,'123123123','general',10.00,0.00,100,0,0,'2026-07-08 00:00:00','2026-08-08 00:00:00',1,'2026-07-08 16:04:01'),(350917000278790144,'d','full_reduce',10.00,0.00,100,0,0,'2026-07-10 00:00:00','2026-07-10 00:00:00',1,'2026-07-10 14:32:12'),(2075830402590277634,'t','general',5.00,0.00,100,0,0,NULL,NULL,1,'2026-07-11 14:31:49');
/*!40000 ALTER TABLE `coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_service_log`
--

DROP TABLE IF EXISTS `customer_service_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_service_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `after_sales_id` bigint(20) NOT NULL COMMENT '售后工单ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID（冗余）',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `operator_type` tinyint(4) NOT NULL COMMENT '操作人类型（1用户 2客服/农户 3管理员 4系统）',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人名称',
  `msg_type` tinyint(4) DEFAULT '1' COMMENT '消息类型（1文字 2图片 3系统通知）',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '沟通内容',
  `image_urls` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片（多个逗号分隔）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_after_sales_id` (`after_sales_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2075825366053257219 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服沟通日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_service_log`
--

LOCK TABLES `customer_service_log` WRITE;
/*!40000 ALTER TABLE `customer_service_log` DISABLE KEYS */;
INSERT INTO `customer_service_log` VALUES (3600000000000001,3500000000000001,2400000000000001,1000000000000005,1,1000000000000005,'小王同学',1,'收到黄瓜有3根断了，箱子也压扁了','/evidence/as01_01.jpg','2025-04-12 10:05:00'),(3600000000000002,3500000000000001,2400000000000001,1000000000000005,3,1000000000000001,'张管理',1,'您好，已核实物流记录，同意退货退款，请将商品寄回','/evidence/as01_03.jpg','2025-04-12 14:00:00'),(3600000000000003,3500000000000001,2400000000000001,1000000000000005,1,1000000000000005,'小王同学',1,'好的，已寄回，快递单号SF1234567890',NULL,'2025-04-13 09:00:00'),(3600000000000004,3500000000000003,2400000000000003,1000000000000005,1,1000000000000005,'小王同学',1,'鲈鱼收到已经死了，充氧袋破了',NULL,'2025-04-16 11:05:00'),(3600000000000005,3500000000000003,2400000000000003,1000000000000005,2,1000000000000002,'运营小王',1,'非常抱歉，已协调商户安排补发一条活鱼，预计明天送达',NULL,'2025-04-16 14:00:00'),(3600000000000006,3500000000000001,2400000000000001,1000000000000005,2,NULL,NULL,1,'订单有问题\n',NULL,'2026-07-08 14:12:04'),(2075825366053257218,3500000000000005,2400000000000001,1000000000000005,2,NULL,NULL,1,'1',NULL,'2026-07-11 14:11:49');
/*!40000 ALTER TABLE `customer_service_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `farmer`
--

DROP TABLE IF EXISTS `farmer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `farmer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '关联用户ID',
  `farmer_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '农户/商户名称',
  `farmer_logo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商户Logo',
  `contact_person` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系电话',
  `id_card_front` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证正面照',
  `id_card_back` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证反面照',
  `business_license` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '营业执照',
  `province` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市',
  `district` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区县',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详细地址',
  `farm_area` decimal(10,2) DEFAULT NULL COMMENT '农场面积（亩）',
  `farm_description` text COLLATE utf8mb4_unicode_ci COMMENT '农场简介',
  `main_products` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主营产品',
  `audit_status` tinyint(4) DEFAULT '0' COMMENT '审核状态（0待审核1审核通过2审核拒绝）',
  `audit_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `auditor_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `level` tinyint(4) DEFAULT '1' COMMENT '商户等级（1普通2优质3金牌）',
  `score` decimal(3,2) DEFAULT '5.00' COMMENT '综合评分',
  `sales_count` int(11) DEFAULT '0' COMMENT '销量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB AUTO_INCREMENT=350847913599045633 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='农户/商户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farmer`
--

LOCK TABLES `farmer` WRITE;
/*!40000 ALTER TABLE `farmer` DISABLE KEYS */;
INSERT INTO `farmer` VALUES (6000000000000001,1000000000000003,'张三农户有机农场','/logo/farm01.jpg','张三','13900000001',NULL,NULL,NULL,'湖北省','武汉市','黄陂区','黄陂区前川街农场路1号',50.00,'专业有机蔬菜种植基地','有机番茄、有机黄瓜',1,'test','2026-07-10 21:40:13',NULL,1,4.80,2000,'2026-06-30 15:59:53','2026-07-10 22:01:45',0),(6000000000000002,1000000000000004,'从化荔枝果园','/logo/farm02.jpg','陈大伯','13900000002',NULL,NULL,NULL,'广东省','广州市','从化区','从化区温泉镇果园路88号',200.00,'百年荔枝老树果园','桂味荔枝、糯米糍',1,NULL,NULL,NULL,1,4.90,1500,'2026-06-30 15:59:53','2026-07-08 22:18:24',0),(6000000000000003,1000000000000005,'王五家庭农场','/logo/farm03.jpg','王五','13700000001','/idcard/front03.jpg','/idcard/back03.jpg',NULL,'山东省','烟台市','牟平区','牟平区龙泉镇果园路12号',150.00,'烟台苹果种植大户','烟台红富士苹果',0,NULL,NULL,NULL,1,5.00,0,'2026-06-29 12:46:07','2026-06-29 12:46:07',0),(6000000000000004,1000000000000006,'张大农户水产基地','/logo/farm04.jpg','张三','13900000001','/idcard/front04.jpg','/idcard/back04.jpg','/license/bl04.jpg','江苏省','苏州市','吴江区','吴江区同里镇水产路66号',300.00,'淡水鱼虾养殖基地','鲈鱼、大闸蟹、小龙虾',1,'审核通过','2025-04-10 09:00:00',1000000000000001,1,4.60,1200,'2026-06-29 12:46:07','2026-06-29 12:46:07',0),(6000000000000005,1000000000000007,'李大姐生态牧场','/logo/farm05.jpg','李四','13900000002','/idcard/front05.jpg','/idcard/back05.jpg','/license/bl05.jpg','内蒙古','呼和浩特','土默特左旗','土左旗敕勒川镇牧场大道1号',1000.00,'草原散养牛羊','草原羊肉、牛肉、牛奶',2,'养殖许可证过期','2025-04-15 16:00:00',1000000000000001,1,3.50,0,'2026-06-29 12:46:07','2026-06-29 12:46:07',0),(6000000000000006,17828724173376014,'王五家庭农场',NULL,'王五','13700000001',NULL,NULL,NULL,'山东省','烟台市','牟平区','牟平区龙泉镇果园路12号',60.00,'烟台苹果种植大户','烟台红富士苹果',1,'','2026-07-08 22:30:20',NULL,1,5.00,0,'2026-07-08 20:15:56','2026-07-08 22:30:20',0),(350847913599045632,350470571903033347,'123',NULL,'123','15810201666',NULL,NULL,NULL,'123','123','123','123',50.00,NULL,'',2,'z','2026-07-10 14:31:40',NULL,1,5.00,0,'2026-07-10 09:57:41','2026-07-10 14:31:40',0);
/*!40000 ALTER TABLE `farmer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `farmer_audit`
--

DROP TABLE IF EXISTS `farmer_audit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `farmer_audit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `farmer_id` bigint(20) NOT NULL COMMENT '农户ID',
  `auditor_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `auditor_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核人姓名',
  `audit_status` tinyint(4) NOT NULL COMMENT '审核状态（1通过2拒绝）',
  `audit_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_auditor_id` (`auditor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075575820685516803 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='农户审核记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farmer_audit`
--

LOCK TABLES `farmer_audit` WRITE;
/*!40000 ALTER TABLE `farmer_audit` DISABLE KEYS */;
INSERT INTO `farmer_audit` VALUES (7000000000000001,6000000000000001,1000000000000001,'张管理',1,'资质齐全，审核通过','2025-03-01 10:00:00','2026-06-29 12:51:02'),(7000000000000002,6000000000000002,1000000000000001,'张管理',1,'果园规模大，审核通过','2025-03-05 14:00:00','2026-06-29 12:51:02'),(7000000000000003,6000000000000004,1000000000000001,'张管理',1,'水产资质合格','2025-04-10 09:00:00','2026-06-29 12:51:02'),(7000000000000004,6000000000000005,1000000000000001,'张管理',2,'许可证过期，需更新','2025-04-15 16:00:00','2026-06-29 12:51:02'),(7000000000000005,6000000000000005,1000000000000002,'运营小王',2,'补充动物防疫合格证','2025-04-16 10:30:00','2026-06-29 12:51:02'),(7000000000000006,6000000000000006,NULL,NULL,1,'','2026-07-08 22:30:20','2026-07-08 22:27:34'),(350847913624215552,350847913599045632,NULL,NULL,1,'','2026-07-10 09:58:10','2026-07-10 09:57:41'),(350858068734644224,350847913599045632,NULL,NULL,2,'资质过期','2026-07-10 10:38:02','2026-07-10 10:38:02'),(350859301591584768,350847913599045632,NULL,NULL,1,'','2026-07-10 10:43:15','2026-07-10 10:42:56'),(350916867373871104,350847913599045632,NULL,NULL,2,'z','2026-07-10 14:31:40','2026-07-10 14:31:40'),(2075575820685516802,6000000000000001,NULL,NULL,2,'test','2026-07-10 21:40:13','2026-07-10 21:40:13');
/*!40000 ALTER TABLE `farmer_audit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logistics_track`
--

DROP TABLE IF EXISTS `logistics_track`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logistics_track` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `logistics_id` bigint(20) NOT NULL COMMENT '关联物流ID（trace_logistics.id）',
  `order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `station` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前站点',
  `location` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地理位置（经纬度或地址）',
  `temperature` decimal(5,2) DEFAULT NULL COMMENT '冷链温度（℃）',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '湿度（%）',
  `track_time` datetime DEFAULT NULL COMMENT '轨迹记录时间',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '轨迹描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_logistics_id` (`logistics_id`),
  KEY `idx_track_time` (`track_time`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=350203131004612609 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流轨迹/冷链温度明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logistics_track`
--

LOCK TABLES `logistics_track` WRITE;
/*!40000 ALTER TABLE `logistics_track` DISABLE KEYS */;
INSERT INTO `logistics_track` VALUES (3200000000000001,2000000000000001,NULL,'黄陂冷链仓','30.87°N 114.37°E',4.00,85.00,'2025-04-10 08:00:00','冷链车装车出发','2026-06-29 12:51:02'),(3200000000000002,2000000000000001,NULL,'岱黄高速服务区','30.95°N 114.40°E',4.50,84.00,'2025-04-10 09:30:00','途中温度监测正常','2026-06-29 12:51:02'),(3200000000000003,2000000000000002,NULL,'从化冷链仓','23.55°N 113.58°E',3.00,90.00,'2025-06-12 05:00:00','荔枝装车出发','2026-06-29 12:51:02'),(3200000000000004,2000000000000002,NULL,'京港澳高速广州段','23.20°N 113.40°E',3.50,88.00,'2025-06-12 07:00:00','高速运输中温度稳定','2026-06-29 12:51:02'),(3200000000000005,2000000000000002,NULL,'深圳龙华配送站','22.65°N 114.02°E',4.20,84.00,'2025-06-12 09:30:00','到达配送站等待派送','2026-06-29 12:51:02'),(350203130916515840,0,1782875863555,'已揽收','商家仓库',NULL,NULL,'2026-07-06 07:15:32','快递员已上门取件','2026-07-08 15:15:32'),(350203130945880064,0,1782875863555,'运输中','始发分拣中心',NULL,NULL,'2026-07-07 01:15:32','快件到达分拣中心','2026-07-08 15:15:32'),(350203130966855680,0,1782875863555,'运输中','中转站',NULL,NULL,'2026-07-07 15:15:32','快件发往目的地','2026-07-08 15:15:32'),(350203130992025600,0,1782875863555,'派送中','派送网点',NULL,NULL,'2026-07-08 04:15:32','快递员正在派送','2026-07-08 15:15:32'),(350203131004612608,0,1782875863555,'已签收','收件地址',NULL,NULL,'2026-07-08 07:15:32','快件已签收','2026-07-08 15:15:32');
/*!40000 ALTER TABLE `logistics_track` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marketing_activity`
--

DROP TABLE IF EXISTS `marketing_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marketing_activity` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(200) DEFAULT '' COMMENT '活动名称',
  `type` varchar(32) DEFAULT 'discount' COMMENT '类型: discount/seckill/new_user',
  `rule` varchar(500) DEFAULT '' COMMENT '活动规则(JSON)',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态: 0未开始 1进行中 2已结束',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marketing_activity`
--

LOCK TABLES `marketing_activity` WRITE;
/*!40000 ALTER TABLE `marketing_activity` DISABLE KEYS */;
INSERT INTO `marketing_activity` VALUES (2075919517465800706,'test activity','discount','{\"discount\":0.8}',NULL,NULL,1,'2026-07-11 20:25:56');
/*!40000 ALTER TABLE `marketing_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marketing_goods`
--

DROP TABLE IF EXISTS `marketing_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marketing_goods` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动关联商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marketing_goods`
--

LOCK TABLES `marketing_goods` WRITE;
/*!40000 ALTER TABLE `marketing_goods` DISABLE KEYS */;
INSERT INTO `marketing_goods` VALUES (2075919518950584322,2075919517465800706,9000000000000001);
/*!40000 ALTER TABLE `marketing_goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_level_config`
--

DROP TABLE IF EXISTS `member_level_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `member_level_config` (
  `id` bigint(20) NOT NULL,
  `level` int(11) NOT NULL COMMENT '等级: 0普通用户 1普通会员 2银卡会员 3金卡会员',
  `level_name` varchar(32) NOT NULL COMMENT '等级名称',
  `discount_rate` decimal(3,2) DEFAULT '1.00' COMMENT '折扣率(0.95=95折)',
  `upgrade_amount` decimal(10,2) DEFAULT '0.00' COMMENT '消费满多少自动升级',
  `recharge_min` decimal(10,2) DEFAULT '0.00' COMMENT '充值最低金额成为此等级',
  `points_rate` int(11) DEFAULT '1' COMMENT '积分倍率(消费1元得多少积分)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_level_config`
--

LOCK TABLES `member_level_config` WRITE;
/*!40000 ALTER TABLE `member_level_config` DISABLE KEYS */;
INSERT INTO `member_level_config` VALUES (1,0,'普通用户',1.00,0.00,0.00,1,'2026-07-11 20:17:16','2026-07-11 20:17:16'),(2,1,'普通会员',0.98,500.00,100.00,1,'2026-07-11 20:17:16','2026-07-11 20:17:16'),(3,2,'银卡会员',0.95,2000.00,500.00,2,'2026-07-11 20:17:16','2026-07-11 20:17:16'),(4,3,'金卡会员',0.90,5000.00,1000.00,3,'2026-07-11 20:17:16','2026-07-11 20:17:16');
/*!40000 ALTER TABLE `member_level_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_point`
--

DROP TABLE IF EXISTS `member_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `member_point` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_point` int(11) DEFAULT '0' COMMENT '累计积分',
  `available_point` int(11) DEFAULT '0' COMMENT '可用积分',
  `freeze_point` int(11) DEFAULT '0' COMMENT '冻结积分',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_point`
--

LOCK TABLES `member_point` WRITE;
/*!40000 ALTER TABLE `member_point` DISABLE KEYS */;
INSERT INTO `member_point` VALUES (1000000000000001,145,87,0,'2026-07-10 14:32:35'),(1000000000000005,0,0,0,'2026-07-10 18:55:42'),(350470571903033347,100,100,0,'2026-07-09 13:49:18'),(350470571903033361,0,0,0,'2026-07-11 14:29:11');
/*!40000 ALTER TABLE `member_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_info`
--

DROP TABLE IF EXISTS `order_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `farmer_id` bigint(20) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `plan_amount` decimal(10,2) DEFAULT '0.00' COMMENT '计划支付金额',
  `actual_amount` decimal(10,2) DEFAULT '0.00' COMMENT '实际支付金额',
  `marketing_id` bigint(20) DEFAULT NULL COMMENT '营销活动ID',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '优惠券ID',
  `freight_amount` decimal(10,2) DEFAULT '0.00' COMMENT '运费',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `total_quantity` int(11) DEFAULT NULL COMMENT '商品总数量',
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '支付方式（1微信2支付宝3余额）',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `order_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '订单状态（0待付款1待发货2已发货3已完成4已取消5售后中）',
  `delivery_type` tinyint(4) DEFAULT NULL COMMENT '配送方式（1快递2自提）',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '取消原因',
  `receiver_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人电话',
  `receiver_province` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省',
  `receiver_city` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '市',
  `receiver_district` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区',
  `receiver_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详细地址',
  `logistics_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '物流单号',
  `logistics_company` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '物流公司',
  `order_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单备注',
  `admin_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管理员备注',
  `is_invoiced` tinyint(4) DEFAULT '0' COMMENT '是否已开票（0否1是）',
  `invoice_type` tinyint(4) DEFAULT NULL COMMENT '发票类型',
  `invoice_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发票抬头',
  `is_comment` tinyint(4) DEFAULT '0' COMMENT '是否已评价（0否1是）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_pay_type` (`pay_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2075855143619268611 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_info`
--

LOCK TABLES `order_info` WRITE;
/*!40000 ALTER TABLE `order_info` DISABLE KEYS */;
INSERT INTO `order_info` VALUES (1782872711201,'ORD202607015534',17828724173376014,NULL,58.00,58.00,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-01 10:50:10',4,NULL,NULL,NULL,NULL,'2026-07-01 11:17:55',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-01 10:25:11','2026-07-01 11:18:55',1),(1782873884221,'ORD202607016190',17828724173376014,NULL,68.00,68.00,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-01 10:44:47',4,NULL,NULL,NULL,NULL,'2026-07-01 11:17:53',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-01 10:44:44','2026-07-01 11:18:53',1),(1782875786232,'ORD202607018596',17828724173376014,NULL,29.90,29.90,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-01 11:16:28',4,NULL,NULL,NULL,NULL,'2026-07-01 11:26:55',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-01 11:16:26','2026-07-01 11:26:55',0),(1782875863555,'ORD202607011882',17828724173376014,NULL,68.00,68.00,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-01 11:17:48',2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'fffdggh',NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-01 11:17:43','2026-07-01 11:18:42',0),(1782876442035,'ORD202607017550',17828724173376014,NULL,19.90,19.90,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-01 11:27:23',4,NULL,NULL,NULL,NULL,'2026-07-01 11:28:27',NULL,'','','','','','',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-01 11:27:22','2026-07-01 11:33:59',1),(1782876512584,'ORD202607013417',17828724173376014,NULL,29.90,29.90,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-01 11:28:33',4,NULL,NULL,NULL,NULL,'2026-07-01 11:35:44',NULL,'','','','','','',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-01 11:28:32','2026-07-01 11:36:13',1),(1782876884499,'ORD202607012386',17828724173376014,NULL,68.00,68.00,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-01 11:34:45',4,NULL,NULL,NULL,NULL,'2026-07-01 11:35:43',NULL,'','','','','','',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-01 11:34:44','2026-07-01 11:36:15',1),(1782876956034,'ORD202607011377',17828724173376014,NULL,29.90,29.90,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-01 11:35:56',4,NULL,NULL,NULL,NULL,'2026-07-08 14:12:49',NULL,'ymd','15810201456','北京','北京','石景山','金鼎街奶龙小区32号楼',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-01 11:35:56','2026-07-09 09:43:36',1),(1783347794191,'ORD202607066769',1000000000000001,NULL,29.90,29.90,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-06 22:23:18',4,NULL,NULL,NULL,NULL,'2026-07-09 08:26:08',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-06 22:23:14','2026-07-09 08:26:08',0),(2400000000000001,'ORD20250410000001',1000000000000005,6000000000000001,117.70,112.70,0.00,0.00,NULL,NULL,5.00,0.00,4,1,'2025-04-10 10:00:00',3,1,'2025-04-10 14:00:00','2025-04-11 09:00:00','2025-04-11 09:00:00',NULL,NULL,'王五','13700000001','湖北','武汉','洪山区','珞喻路1037号华科','WL20250410001','顺丰冷运','请尽快发货',NULL,0,NULL,NULL,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2400000000000002,'ORD20250412000001',1000000000000005,6000000000000002,136.00,126.00,0.00,0.00,NULL,NULL,0.00,10.00,2,2,'2025-04-12 15:00:00',2,1,'2025-04-13 08:00:00',NULL,NULL,NULL,NULL,'王五','13700000001','湖北','武汉','洪山区','珞喻路1037号华科','WL20250412001','京东冷链','荔枝要保鲜',NULL,0,NULL,NULL,0,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2400000000000003,'ORD20250415000001',1000000000000005,6000000000000004,58.00,58.00,0.00,0.00,NULL,NULL,0.00,0.00,1,1,'2025-04-15 09:30:00',2,1,'2026-07-11 14:33:04',NULL,NULL,NULL,NULL,'王五','13700000001','湖北','武汉','洪山区','珞喻路1037号华科','SF123456','shunfeng','要活的',NULL,0,NULL,NULL,0,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2400000000000005,'ORD20250420000001',1000000000000001,6000000000000001,29.90,29.90,0.00,0.00,NULL,NULL,0.00,0.00,1,1,'2025-04-20 11:00:00',3,1,'2025-04-20 15:00:00','2025-04-21 10:00:00','2025-04-21 10:00:00',NULL,NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号','WL20250420001','顺丰冷运',NULL,NULL,0,NULL,NULL,0,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(350129999820492800,'ORD202607084022',1000000000000001,NULL,88.00,88.00,0.00,0.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-08 10:24:57',4,NULL,NULL,NULL,NULL,'2026-07-09 08:26:10',NULL,'test','13800000001','','','','',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 10:24:57','2026-07-09 09:43:32',1),(350211328151015424,'ORD202607086207',17828724173376014,NULL,88.00,88.00,88.00,88.00,NULL,NULL,0.00,0.00,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,'ymd','15810201456','北京','北京','石景山','金鼎街奶龙小区32号楼',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 15:48:07','2026-07-08 15:48:07',0),(350211345611915264,'ORD202607088480',17828724173376014,NULL,68.00,68.00,68.00,68.00,NULL,NULL,0.00,0.00,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,'ymd','15810201456','北京','北京','石景山','金鼎街奶龙小区32号楼',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 15:48:11','2026-07-08 15:48:11',0),(350211364247220224,'ORD202607089101',17828724173376014,NULL,19.90,19.90,19.90,19.90,NULL,NULL,0.00,0.00,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,'ymd','15810201456','北京','北京','石景山','金鼎街奶龙小区32号楼',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 15:48:15','2026-07-08 15:48:15',0),(350211555654291456,'ORD202607085686',17828724173376014,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,'test','123','','','','',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 15:49:01','2026-07-08 15:49:01',0),(350211711413919744,'ORD202607085375',17828724173376014,NULL,88.00,88.00,88.00,88.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-08 15:49:40',4,NULL,NULL,NULL,NULL,'2026-07-08 15:49:52',NULL,'ymd','15810201456','北京','北京','石景山','金鼎街奶龙小区32号楼',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 15:49:38','2026-07-08 15:49:52',0),(350212367637946368,'ORD202607083774',17828724173376014,NULL,88.00,88.00,88.00,88.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-08 15:52:16',4,NULL,NULL,NULL,NULL,'2026-07-08 15:52:21',NULL,'ymd','15810201456','北京','北京','石景山','金鼎街奶龙小区32号楼',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 15:52:15','2026-07-08 15:52:21',0),(350213014609338368,'ORD202607087632',17828724173376014,NULL,88.00,88.00,88.00,88.00,NULL,NULL,0.00,0.00,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,'ymd','15810201456','北京','北京','石景山','金鼎街奶龙小区32号楼',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 15:54:49','2026-07-08 15:54:49',0),(350213033215283200,'ORD202607089794',17828724173376014,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,'ymd','15810201456','北京','北京','石景山','金鼎街奶龙小区32号楼',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 15:54:53','2026-07-08 15:54:53',0),(350216138338263040,'ORD202607088408',1000000000000001,NULL,88.00,88.00,88.00,88.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-08 16:07:15',4,NULL,NULL,NULL,NULL,'2026-07-09 08:26:07',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 16:07:14','2026-07-09 08:26:41',1),(350216402520731648,'ORD202607085180',1000000000000001,NULL,88.00,88.00,88.00,88.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-08 16:08:18',4,NULL,NULL,NULL,NULL,'2026-07-09 08:26:06',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 16:08:17','2026-07-09 08:26:42',1),(350216596926648320,'ORD202607081247',1000000000000001,NULL,88.00,88.00,88.00,88.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-08 16:09:05',4,NULL,NULL,NULL,NULL,'2026-07-08 17:01:16',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 16:09:03','2026-07-09 08:25:47',1),(350217754789457920,'ORD202607084611',1000000000000001,NULL,68.00,68.00,68.00,68.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-08 16:13:40',4,NULL,NULL,NULL,NULL,'2026-07-08 17:01:15',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 16:13:39','2026-07-09 08:25:45',1),(350218673182674944,'ORD202607083352',1000000000000001,NULL,88.00,88.00,88.00,88.00,NULL,NULL,0.00,0.00,NULL,1,'2026-07-08 16:17:19',4,NULL,NULL,NULL,NULL,'2026-07-08 17:01:14',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 16:17:18','2026-07-09 08:25:42',1),(350221631907663872,'ORD202607088401',1000000000000001,NULL,952.00,942.00,952.00,942.00,NULL,9100000000000001,0.00,0.00,NULL,1,'2026-07-08 16:29:05',4,NULL,NULL,NULL,NULL,'2026-07-08 17:01:13',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 16:29:03','2026-07-08 23:53:42',1),(350327038336442368,'ORD202607083128',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,NULL,1,'2026-07-08 23:27:56',4,NULL,NULL,NULL,NULL,'2026-07-09 08:26:05',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-08 23:27:54','2026-07-09 08:26:44',1),(350597968161812480,'ORD202607098493',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,NULL,1,'2026-07-09 17:24:30',4,NULL,NULL,NULL,NULL,'2026-07-09 17:24:34',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-09 17:24:29','2026-07-09 17:24:34',0),(350912961096880128,'ORD202607104861',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,NULL,1,'2026-07-10 14:16:11',1,NULL,NULL,NULL,NULL,NULL,NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-10 14:16:09','2026-07-10 14:16:11',0),(350915620143034368,'ORD202607102438',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,NULL,1,'2026-07-10 14:26:44',1,NULL,NULL,NULL,NULL,NULL,NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-10 14:26:43','2026-07-10 14:26:44',0),(350917084915666944,'ORD202607109936',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,NULL,1,'2026-07-10 14:32:35',4,NULL,NULL,NULL,NULL,'2026-07-11 16:10:15',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-10 14:32:32','2026-07-10 14:32:35',0),(2075776152451506177,'FD20260711105615454',1000000000000001,NULL,68.00,68.00,68.00,68.00,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 13:29:38',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 10:56:15','2026-07-11 10:56:15',0),(2075776627620012034,'FD20260711105808749',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 10:58:09',4,NULL,NULL,NULL,NULL,'2026-07-11 16:08:11',NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 10:58:09','2026-07-11 10:58:08',0),(2075776629108989954,'FD20260711105809110',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 10:58:09',NULL,'','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 10:58:09','2026-07-11 10:58:09',0),(2075776759161774082,'FD20260711105840110',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 10:58:40',1,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 10:58:40','2026-07-11 10:58:40',0),(2075777925211832322,'FD20260711110318107',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 13:29:37',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 11:03:18','2026-07-11 11:03:18',0),(2075780893608833026,'FD20260711111505839',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 13:29:35',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 11:15:06','2026-07-11 11:15:05',0),(2075781136651972609,'FD20260711111603785',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 11:16:04',1,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 11:16:04','2026-07-11 11:16:03',0),(2075781960560078849,'FD20260711111920219',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 11:19:20',1,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 11:19:20','2026-07-11 11:19:20',0),(2075782796036100097,'FD20260711112239407',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 15:36:55',NULL,'王五','13700000001','湖北','武汉','洪山区','珞喻路1037号华科紫菘公寓12栋305',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 11:22:39','2026-07-11 11:22:39',0),(2075786521819602946,'FD20260711113727697',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 11:37:28',1,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 11:37:28','2026-07-11 11:37:27',0),(2075803640011493377,'FD20260711124529006',1000000000000005,NULL,19.90,19.90,19.90,19.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 12:46:08',1,NULL,NULL,NULL,NULL,NULL,NULL,'新地址测试','13800009999','浙江','杭州','西湖区','文三路100号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 12:45:29','2026-07-11 12:45:29',0),(2075804537856790530,'FD20260711124903070',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 12:49:03',1,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 12:49:03','2026-07-11 12:49:03',0),(2075804730975129601,'FD20260711124949113',1000000000000005,NULL,45.00,45.00,45.00,45.00,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 15:38:40',NULL,'王五','13700000001','湖北','武汉','洪山区','珞喻路1037号华科紫菘公寓12栋305',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 12:49:49','2026-07-11 12:49:49',0),(2075805362381479937,'FD20260711125219650',1000000000000005,NULL,19.90,19.90,19.90,19.90,NULL,NULL,0.00,0.00,1,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,'王五','13700000001','湖北','武汉','武昌区','中南路99号保利广场A座1501',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 12:52:20','2026-07-11 12:52:19',0),(2075806036053778433,'FD20260711125500254',1000000000000005,NULL,68.00,68.00,68.00,68.00,NULL,NULL,0.00,0.00,1,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,'王五','13700000001','湖北','武汉','武昌区','中南路99号保利广场A座1501',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 12:55:00','2026-07-11 12:55:00',0),(2075807591091953665,'FD20260711130111002',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 13:01:11','2026-07-11 13:01:11',0),(2075807631076253698,'FD20260711130120550',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 13:01:21',1,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 13:01:21','2026-07-11 13:01:20',0),(2075807919845777410,'FD20260711130229383',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 13:02:30',1,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 13:02:29','2026-07-11 13:02:29',0),(2075808227422490626,'FD20260711130342718',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 13:03:43',1,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 13:03:43','2026-07-11 13:03:42',0),(2075808229083435009,'FD20260711130343124',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 13:03:43',NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 13:03:43','2026-07-11 13:30:10',1),(2075814802446974977,'FD20260711132950342',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 13:29:51',4,NULL,NULL,NULL,NULL,'2026-07-11 16:10:15',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 13:29:50','2026-07-11 13:29:50',0),(2075829735293288450,'FD20260711142910607',350470571903033361,NULL,59.80,59.80,59.80,59.80,NULL,NULL,0.00,0.00,2,1,'2026-07-11 14:29:11',1,NULL,NULL,NULL,NULL,NULL,NULL,'Test','13999998888','Zhejiang','Hangzhou','Xihu','Wensan Rd 100',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 14:29:11','2026-07-11 14:29:10',0),(2075829737914728450,'FD20260711142911232',350470571903033361,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 14:29:11',NULL,'t','13999998888','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 14:29:11','2026-07-11 14:29:11',0),(2075845307208916993,'FD20260711153103241',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 15:31:04',4,NULL,NULL,NULL,NULL,'2026-07-11 16:10:14',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 15:31:03','2026-07-11 15:31:03',0),(2075852083610329090,'FD20260711155758861',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 15:57:59',NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 15:57:59','2026-07-11 15:57:58',0),(2075852085363548162,'FD20260711155759276',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 15:57:59',1,NULL,NULL,NULL,NULL,NULL,NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 15:57:59','2026-07-11 15:57:59',0),(2075852451907969025,'FD20260711155926673',1000000000000005,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,NULL,NULL,4,NULL,NULL,NULL,NULL,'2026-07-11 15:59:34',NULL,'t','13800000000','t','t','t','t',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 15:59:27','2026-07-11 15:59:26',0),(2075853975098843138,'FD20260711160529819',1000000000000001,NULL,19.90,19.90,19.90,19.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 16:05:31',4,NULL,NULL,NULL,NULL,'2026-07-11 16:10:13',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 16:05:30','2026-07-11 16:05:29',0),(2075855143619268610,'FD20260711161008413',1000000000000001,NULL,29.90,29.90,29.90,29.90,NULL,NULL,0.00,0.00,1,1,'2026-07-11 16:10:09',4,NULL,NULL,NULL,NULL,'2026-07-11 16:10:12',NULL,'张管理','13800000001','湖北','武汉','武昌区','中南路99号',NULL,NULL,NULL,NULL,0,NULL,NULL,0,'2026-07-11 16:10:08','2026-07-11 16:10:08',0);
/*!40000 ALTER TABLE `order_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单编号（冗余）',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品图片',
  `product_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品编号',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `farmer_id` bigint(20) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '商品原价',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '商品总价',
  `unit` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位',
  `spec_info` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格信息',
  `trace_id` bigint(20) DEFAULT NULL COMMENT '溯源ID',
  `is_comment` tinyint(4) DEFAULT '0' COMMENT '是否已评价',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075855143657017346 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (461284274,1782872711201,'ORD202607015534',9000000000000004,'鲜活鲈鱼（2条装）','/product/seabass.jpg',NULL,NULL,NULL,58.00,NULL,1,58.00,'件',NULL,NULL,0,'2026-07-01 10:25:11'),(462465746,1782873884221,'ORD202607016190',9000000000000002,'从化桂味荔枝（3斤装）','/product/lychee.jpg',NULL,NULL,NULL,68.00,NULL,1,68.00,'件',NULL,NULL,0,'2026-07-01 10:44:44'),(464361514,1782875786232,'ORD202607018596',9000000000000001,'有机番茄（5斤装）','/product/tomato.jpg',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-01 11:16:26'),(464437139,1782875863555,'ORD202607011882',9000000000000002,'从化桂味荔枝（3斤装）','/product/lychee.jpg',NULL,NULL,NULL,68.00,NULL,1,68.00,'箱',NULL,1000000000000002,0,'2026-07-01 11:17:43'),(465017922,1782876442035,'ORD202607017550',9000000000000005,'有机黄瓜（5斤装）','/product/cucumber.jpg',NULL,NULL,NULL,19.90,NULL,1,19.90,'箱',NULL,1000000000000004,0,'2026-07-01 11:27:22'),(465085494,1782876512584,'ORD202607013417',9000000000000001,'有机番茄（5斤装）','/product/tomato.jpg',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-01 11:28:32'),(465466112,1782876884499,'ORD202607012386',9000000000000002,'从化桂味荔枝（3斤装）','/product/lychee.jpg',NULL,NULL,NULL,68.00,NULL,1,68.00,'箱',NULL,1000000000000002,0,'2026-07-01 11:34:44'),(465530900,1782876956034,'ORD202607011377',9000000000000001,'有机番茄（5斤装）','/product/tomato.jpg',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-01 11:35:56'),(936375722,1783347794191,'ORD202607066769',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-06 22:23:14'),(1066073070,350129999820492800,'ORD202607084022',1782894061841,'???','',NULL,NULL,NULL,88.00,NULL,1,88.00,'',NULL,NULL,0,'2026-07-08 10:24:57'),(2500000000000001,2400000000000001,'ORD20250410000001',9000000000000001,'有机番茄（5斤装）','/product/tomato.jpg','P20250401001',8000000000000001,6000000000000001,29.90,39.90,2,59.80,'箱','5斤/箱',1300000000000001,1,'2026-06-29 12:51:02'),(2500000000000002,2400000000000001,'ORD20250410000001',9000000000000005,'有机黄瓜（5斤装）','/product/cucumber.jpg','P20250401005',8000000000000001,6000000000000001,19.90,25.90,2,39.80,'箱','5斤/箱',1300000000000004,1,'2026-06-29 12:51:02'),(2500000000000003,2400000000000002,'ORD20250412000001',9000000000000002,'从化桂味荔枝（3斤装）','/product/lychee.jpg','P20250401002',8000000000000002,6000000000000002,68.00,88.00,2,136.00,'箱','3斤/箱',1300000000000002,0,'2026-06-29 12:51:02'),(2500000000000004,2400000000000003,'ORD20250415000001',9000000000000004,'鲜活鲈鱼（2条装）','/product/seabass.jpg','P20250401004',8000000000000004,6000000000000004,58.00,72.00,1,58.00,'件','2条/件',1300000000000003,0,'2026-06-29 12:51:02'),(2500000000000005,2400000000000005,'ORD20250420000001',9000000000000001,'有机番茄（5斤装）','/product/tomato.jpg','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱','5斤/箱',1300000000000001,0,'2026-06-29 12:51:02'),(350211328163602432,350211328151015424,'ORD202607086207',1782894061841,'阳山水蜜桃','/uploads/products/product-1782894031584-6c735019.png',NULL,NULL,NULL,88.00,NULL,1,88.00,'箱',NULL,1782894061809,0,'2026-07-08 15:48:07'),(350211345666445312,350211345611915264,'ORD202607088480',9000000000000002,'从化桂味荔枝（3斤装）','/images/products/荔枝.png',NULL,NULL,NULL,68.00,NULL,1,68.00,'箱',NULL,1000000000000002,0,'2026-07-08 15:48:11'),(350211364280778752,350211364247220224,'ORD202607089101',9000000000000005,'有机黄瓜（5斤装）','/images/products/黄瓜.png',NULL,NULL,NULL,19.90,NULL,1,19.90,'箱',NULL,1000000000000004,0,'2026-07-08 15:48:15'),(350211555675267072,350211555654291456,'ORD202607085686',9000000000000001,'test','',NULL,NULL,NULL,29.90,NULL,1,29.90,'kg',NULL,NULL,0,'2026-07-08 15:49:01'),(350211711476838400,350211711413919744,'ORD202607085375',1782894061841,'阳山水蜜桃','/uploads/products/product-1782894031584-6c735019.png',NULL,NULL,NULL,88.00,NULL,1,88.00,'箱',NULL,1782894061809,0,'2026-07-08 15:49:38'),(350212367654727680,350212367637946368,'ORD202607083774',1782894061841,'阳山水蜜桃','/uploads/products/product-1782894031584-6c735019.png',NULL,NULL,NULL,88.00,NULL,1,88.00,'箱',NULL,1782894061809,0,'2026-07-08 15:52:15'),(350213014655479808,350213014609338368,'ORD202607087632',1782894061841,'阳山水蜜桃','/uploads/products/product-1782894031584-6c735019.png',NULL,NULL,NULL,88.00,NULL,1,88.00,'箱',NULL,1782894061809,0,'2026-07-08 15:54:49'),(350213033240453120,350213033215283200,'ORD202607089794',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-08 15:54:53'),(350216138346655744,350216138338263040,'ORD202607088408',1782894061841,'阳山水蜜桃','/uploads/products/product-1782894031584-6c735019.png',NULL,NULL,NULL,88.00,NULL,1,88.00,'箱',NULL,1782894061809,0,'2026-07-08 16:07:14'),(350216402537512960,350216402520731648,'ORD202607085180',1782894061841,'阳山水蜜桃','/uploads/products/product-1782894031584-6c735019.png',NULL,NULL,NULL,88.00,NULL,1,88.00,'箱',NULL,1782894061809,0,'2026-07-08 16:08:17'),(350216596972789760,350216596926648320,'ORD202607081247',1782894061841,'阳山水蜜桃','/uploads/products/product-1782894031584-6c735019.png',NULL,NULL,NULL,88.00,NULL,1,88.00,'箱',NULL,1782894061809,0,'2026-07-08 16:09:03'),(350217754797850624,350217754789457920,'ORD202607084611',9000000000000002,'从化桂味荔枝（3斤装）','/images/products/荔枝.png',NULL,NULL,NULL,68.00,NULL,1,68.00,'箱',NULL,1000000000000002,0,'2026-07-08 16:13:39'),(350218673191067648,350218673182674944,'ORD202607083352',1782894061841,'阳山水蜜桃','/uploads/products/product-1782894031584-6c735019.png',NULL,NULL,NULL,88.00,NULL,1,88.00,'箱',NULL,1782894061809,0,'2026-07-08 16:17:18'),(350221631920250880,350221631907663872,'ORD202607088401',9000000000000002,'从化桂味荔枝（3斤装）','/images/products/荔枝.png',NULL,NULL,NULL,68.00,NULL,14,952.00,'',NULL,NULL,0,'2026-07-08 16:29:03'),(350327038353223680,350327038336442368,'ORD202607083128',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-08 23:27:54'),(350597968186982400,350597968161812480,'ORD202607098493',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,NULL,0,'2026-07-09 17:24:29'),(350912961109467136,350912961096880128,'ORD202607104861',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,NULL,0,'2026-07-10 14:16:09'),(350915620184981504,350915620143034368,'ORD202607102438',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,NULL,0,'2026-07-10 14:26:43'),(350917084961808384,350917084915666944,'ORD202607109936',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png',NULL,NULL,NULL,29.90,NULL,1,29.90,'箱',NULL,NULL,0,'2026-07-10 14:32:32'),(2075776152514420738,2075776152451506177,'FD20260711105615454',9000000000000002,'从化桂味荔枝（3斤装）','/images/products/荔枝.png','P20250401002',8000000000000002,6000000000000002,68.00,88.00,1,68.00,'箱',NULL,1000000000000002,0,'2026-07-11 10:56:15'),(2075776627620012035,2075776627620012034,'FD20260711105808749',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 10:58:09'),(2075776629108989955,2075776629108989954,'FD20260711105809110',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 10:58:09'),(2075776759161774083,2075776759161774082,'FD20260711105840110',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 10:58:40'),(2075777925211832323,2075777925211832322,'FD20260711110318107',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 11:03:18'),(2075780893671747585,2075780893608833026,'FD20260711111505839',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 11:15:06'),(2075781136651972610,2075781136651972609,'FD20260711111603785',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 11:16:04'),(2075781960560078850,2075781960560078849,'FD20260711111920219',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 11:19:20'),(2075782796036100098,2075782796036100097,'FD20260711112239407',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 11:22:39'),(2075786521819602947,2075786521819602946,'FD20260711113727697',9000000000000001,'test','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 11:37:28'),(2075803640078602242,2075803640011493377,'FD20260711124529006',9000000000000005,'有机黄瓜（5斤装）','/images/products/黄瓜.png','P20250401005',8000000000000001,6000000000000001,19.90,25.90,1,19.90,'箱',NULL,1000000000000004,0,'2026-07-11 12:45:29'),(2075804537856790531,2075804537856790530,'FD20260711124903070',9000000000000001,'test','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 12:49:03'),(2075804730975129602,2075804730975129601,'FD20260711124949113',9000000000000003,'烟台红富士苹果（10斤装）','/images/products/苹果.png','P20250401003',8000000000000002,6000000000000003,45.00,58.00,1,45.00,'箱',NULL,NULL,0,'2026-07-11 12:49:49'),(2075805362448588802,2075805362381479937,'FD20260711125219650',9000000000000005,'有机黄瓜（5斤装）','/images/products/黄瓜.png','P20250401005',8000000000000001,6000000000000001,19.90,25.90,1,19.90,'箱',NULL,1000000000000004,0,'2026-07-11 12:52:20'),(2075806036053778434,2075806036053778433,'FD20260711125500254',9000000000000002,'从化桂味荔枝（3斤装）','/images/products/荔枝.png','P20250401002',8000000000000002,6000000000000002,68.00,88.00,1,68.00,'箱',NULL,1000000000000002,0,'2026-07-11 12:55:00'),(2075807591091953666,2075807591091953665,'FD20260711130111002',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 13:01:11'),(2075807631076253699,2075807631076253698,'FD20260711130120550',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 13:01:21'),(2075807919845777411,2075807919845777410,'FD20260711130229383',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 13:02:29'),(2075808227422490627,2075808227422490626,'FD20260711130342718',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 13:03:43'),(2075808229083435010,2075808229083435009,'FD20260711130343124',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 13:03:43'),(2075814802509889538,2075814802446974977,'FD20260711132950342',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 13:29:50'),(2075829735293288451,2075829735293288450,'FD20260711142910607',9000000000000001,'organic tomato','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,2,59.80,'箱',NULL,1000000000000001,0,'2026-07-11 14:29:11'),(2075829737914728451,2075829737914728450,'FD20260711142911232',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 14:29:11'),(2075845307267637249,2075845307208916993,'FD20260711153103241',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 15:31:03'),(2075852083610329091,2075852083610329090,'FD20260711155758861',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 15:57:59'),(2075852085363548163,2075852085363548162,'FD20260711155759276',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 15:57:59'),(2075852451907969026,2075852451907969025,'FD20260711155926673',9000000000000001,'t','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 15:59:27'),(2075853975098843139,2075853975098843138,'FD20260711160529819',9000000000000005,'有机黄瓜（5斤装）','/images/products/黄瓜.png','P20250401005',8000000000000001,6000000000000001,19.90,25.90,1,19.90,'箱',NULL,1000000000000004,0,'2026-07-11 16:05:30'),(2075855143657017345,2075855143619268610,'FD20260711161008413',9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png','P20250401001',8000000000000001,6000000000000001,29.90,39.90,1,29.90,'箱',NULL,1000000000000001,0,'2026-07-11 16:10:08');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_log`
--

DROP TABLE IF EXISTS `order_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单编号（冗余）',
  `order_status` tinyint(4) NOT NULL COMMENT '变更后的订单状态',
  `operator_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '操作人类型（1用户2农户3管理员4系统）',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2075855172786458626 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单状态流转日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_log`
--

LOCK TABLES `order_log` WRITE;
/*!40000 ALTER TABLE `order_log` DISABLE KEYS */;
INSERT INTO `order_log` VALUES (3000000000000001,2400000000000001,'ORD20250410000001',0,1,1000000000000005,'用户下单，待付款','2026-06-29 12:51:02'),(3000000000000002,2400000000000001,'ORD20250410000001',1,1,1000000000000005,'用户支付成功','2026-06-29 12:51:02'),(3000000000000003,2400000000000001,'ORD20250410000001',2,2,1000000000000003,'农户确认发货','2026-06-29 12:51:02'),(3000000000000004,2400000000000001,'ORD20250410000001',3,1,1000000000000005,'用户确认收货','2026-06-29 12:51:02'),(2075776152514420740,2075776152451506177,'FD20260711105615454',0,4,NULL,'order created','2026-07-11 10:56:15'),(2075776627620012037,2075776627620012034,'FD20260711105808749',0,4,NULL,'order created','2026-07-11 10:58:09'),(2075776628651810817,2075776627620012034,'FD20260711105808749',1,1,1000000000000005,'user paid','2026-07-11 10:58:09'),(2075776629171904515,2075776629108989954,'FD20260711105809110',0,4,NULL,'order created','2026-07-11 10:58:09'),(2075776629754912770,2075776629108989954,'FD20260711105809110',4,1,1000000000000005,'user cancelled','2026-07-11 10:58:09'),(2075776759161774085,2075776759161774082,'FD20260711105840110',0,4,NULL,'order created','2026-07-11 10:58:40'),(2075776759954497538,2075776759161774082,'FD20260711105840110',1,1,1000000000000005,'user paid','2026-07-11 10:58:40'),(2075777925299912706,2075777925211832322,'FD20260711110318107',0,4,NULL,'order created','2026-07-11 11:03:18'),(2075780893738856451,2075780893608833026,'FD20260711111505839',0,4,NULL,'order created','2026-07-11 11:15:06'),(2075781136651972612,2075781136651972609,'FD20260711111603785',0,4,NULL,'order created','2026-07-11 11:16:04'),(2075781138023510017,2075781136651972609,'FD20260711111603785',1,1,1000000000000005,'user paid','2026-07-11 11:16:04'),(2075781960560078852,2075781960560078849,'FD20260711111920219',0,4,NULL,'order created','2026-07-11 11:19:20'),(2075781961730289665,2075781960560078849,'FD20260711111920219',1,1,1000000000000005,'user paid','2026-07-11 11:19:20'),(2075782796099014657,2075782796036100097,'FD20260711112239407',0,4,NULL,'order created','2026-07-11 11:22:39'),(2075786521819602949,2075786521819602946,'FD20260711113727697',0,4,NULL,'order created','2026-07-11 11:37:28'),(2075786522092232706,2075786521819602946,'FD20260711113727697',1,1,1000000000000005,'user paid','2026-07-11 11:37:28'),(2075803640179265538,2075803640011493377,'FD20260711124529006',0,4,NULL,'order created','2026-07-11 12:45:29'),(2075803803853590530,2075803640011493377,'FD20260711124529006',1,1,1000000000000005,'user paid','2026-07-11 12:46:08'),(2075804537902927873,2075804537856790530,'FD20260711124903070',0,4,NULL,'order created','2026-07-11 12:49:03'),(2075804538414632961,2075804537856790530,'FD20260711124903070',1,1,1000000000000005,'user paid','2026-07-11 12:49:03'),(2075804730975129604,2075804730975129601,'FD20260711124949113',0,4,NULL,'order created','2026-07-11 12:49:49'),(2075805362511503363,2075805362381479937,'FD20260711125219650',0,4,NULL,'order created','2026-07-11 12:52:20'),(2075806036154441731,2075806036053778433,'FD20260711125500254',0,4,NULL,'order created','2026-07-11 12:55:00'),(2075807591133896707,2075807591091953665,'FD20260711130111002',0,4,NULL,'order created','2026-07-11 13:01:11'),(2075807631076253701,2075807631076253698,'FD20260711130120550',0,4,NULL,'order created','2026-07-11 13:01:21'),(2075807631747342338,2075807631076253698,'FD20260711130120550',1,1,1000000000000005,'user paid','2026-07-11 13:01:21'),(2075807919900303362,2075807919845777410,'FD20260711130229383',0,4,NULL,'order created','2026-07-11 13:02:29'),(2075807920890159106,2075807919845777410,'FD20260711130229383',1,1,1000000000000005,'user paid','2026-07-11 13:02:30'),(2075808227468627971,2075808227422490626,'FD20260711130342718',0,4,NULL,'order created','2026-07-11 13:03:43'),(2075808228240379905,2075808227422490626,'FD20260711130342718',1,1,1000000000000005,'user paid','2026-07-11 13:03:43'),(2075808229083435012,2075808229083435009,'FD20260711130343124',0,4,NULL,'order created','2026-07-11 13:03:43'),(2075808229792272386,2075808229083435009,'FD20260711130343124',4,1,1000000000000005,'user cancelled','2026-07-11 13:03:43'),(2075814737959550978,2075780893608833026,'FD20260711111505839',4,1,1000000000000001,'user cancelled','2026-07-11 13:29:35'),(2075814745383469058,2075777925211832322,'FD20260711110318107',4,1,1000000000000001,'user cancelled','2026-07-11 13:29:37'),(2075814751041585154,2075776152451506177,'FD20260711105615454',4,1,1000000000000001,'user cancelled','2026-07-11 13:29:38'),(2075814802509889540,2075814802446974977,'FD20260711132950342',0,4,NULL,'order created','2026-07-11 13:29:50'),(2075814807262035970,2075814802446974977,'FD20260711132950342',1,1,1000000000000001,'user paid','2026-07-11 13:29:51'),(2075829735347814402,2075829735293288450,'FD20260711142910607',0,4,NULL,'order created','2026-07-11 14:29:11'),(2075829736153120770,2075829735293288450,'FD20260711142910607',1,1,350470571903033361,'user paid','2026-07-11 14:29:11'),(2075829737914728453,2075829737914728450,'FD20260711142911232',0,4,NULL,'order created','2026-07-11 14:29:11'),(2075829738422239233,2075829737914728450,'FD20260711142911232',4,1,350470571903033361,'user cancelled','2026-07-11 14:29:11'),(2075830715590213634,2400000000000003,'ORD20250415000001',2,2,NULL,'农户发货:SF123456','2026-07-11 14:33:04'),(2075845307305385987,2075845307208916993,'FD20260711153103241',0,4,NULL,'order created','2026-07-11 15:31:03'),(2075845311508078593,2075845307208916993,'FD20260711153103241',1,1,1000000000000001,'user paid','2026-07-11 15:31:04'),(2075846782270136321,2075782796036100097,'FD20260711112239407',4,1,1000000000000005,'user cancelled','2026-07-11 15:36:55'),(2075847223007600641,2075804730975129601,'FD20260711124949113',4,1,1000000000000005,'user cancelled','2026-07-11 15:38:40'),(2075852083673243650,2075852083610329090,'FD20260711155758861',0,4,NULL,'order created','2026-07-11 15:57:59'),(2075852084717625345,2075852083610329090,'FD20260711155758861',4,1,1000000000000005,'user cancelled','2026-07-11 15:57:59'),(2075852085363548165,2075852085363548162,'FD20260711155759276',0,4,NULL,'order created','2026-07-11 15:57:59'),(2075852085904613378,2075852085363548162,'FD20260711155759276',1,1,1000000000000005,'user paid','2026-07-11 15:57:59'),(2075852451907969028,2075852451907969025,'FD20260711155926673',0,4,NULL,'order created','2026-07-11 15:59:27'),(2075852484334133249,2075852451907969025,'FD20260711155926673',4,1,1000000000000005,'user cancelled','2026-07-11 15:59:34'),(2075853975174340611,2075853975098843138,'FD20260711160529819',0,4,NULL,'order created','2026-07-11 16:05:30'),(2075853978794024961,2075853975098843138,'FD20260711160529819',1,1,1000000000000001,'user paid','2026-07-11 16:05:31'),(2075854651434573825,2075776627620012034,'FD20260711105808749',4,1,1000000000000005,'user cancelled','2026-07-11 16:08:11'),(2075855143711543299,2075855143619268610,'FD20260711161008413',0,4,NULL,'order created','2026-07-11 16:10:08'),(2075855147029237762,2075855143619268610,'FD20260711161008413',1,1,1000000000000001,'user paid','2026-07-11 16:10:09'),(2075855157850542082,2075855143619268610,'FD20260711161008413',4,1,1000000000000001,'user cancelled','2026-07-11 16:10:12'),(2075855162967592961,2075853975098843138,'FD20260711160529819',4,1,1000000000000001,'user cancelled','2026-07-11 16:10:13'),(2075855167078010882,2075845307208916993,'FD20260711153103241',4,1,1000000000000001,'user cancelled','2026-07-11 16:10:14'),(2075855169884000257,2075814802446974977,'FD20260711132950342',4,1,1000000000000001,'user cancelled','2026-07-11 16:10:15'),(2075855172786458625,350917084915666944,'ORD202607109936',4,1,1000000000000001,'user cancelled','2026-07-11 16:10:15');
/*!40000 ALTER TABLE `order_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_info`
--

DROP TABLE IF EXISTS `payment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `payment_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付流水号',
  `pay_type` tinyint(4) NOT NULL COMMENT '支付方式（1微信2支付宝3余额）',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `pay_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '支付状态（0待支付1支付成功2支付失败3已退款）',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `third_party_no` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方支付单号',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `refund_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pay_status` (`pay_status`)
) ENGINE=InnoDB AUTO_INCREMENT=2075855143711543299 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_info`
--

LOCK TABLES `payment_info` WRITE;
/*!40000 ALTER TABLE `payment_info` DISABLE KEYS */;
INSERT INTO `payment_info` VALUES (2600000000000001,2400000000000001,'ORD20250410000001',1000000000000005,'PAY202504100001',1,112.70,1,'2025-04-10 10:01:00','WX2025041010010001',NULL,NULL,NULL,'2026-06-29 12:51:02','2026-06-29 12:51:02'),(2600000000000002,2400000000000002,'ORD20250412000001',1000000000000005,'PAY202504120001',2,126.00,1,'2025-04-12 15:02:00','ALI2025041215020001',NULL,NULL,NULL,'2026-06-29 12:51:02','2026-06-29 12:51:02'),(2600000000000003,2400000000000003,'ORD20250415000001',1000000000000005,'PAY202504150001',1,58.00,1,'2025-04-15 09:31:00','WX2025041509310001',NULL,NULL,NULL,'2026-06-29 12:51:02','2026-06-29 12:51:02'),(2600000000000005,2400000000000005,'ORD20250420000001',1000000000000001,'PAY202504200001',1,29.90,1,'2025-04-20 11:01:00','WX2025042011010001',NULL,NULL,NULL,'2026-06-29 12:51:02','2026-06-29 12:51:02'),(2075776152514420739,2075776152451506177,'FD20260711105615454',1000000000000001,'PAYFD20260711105615454',1,68.00,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 10:56:15','2026-07-11 10:56:15'),(2075776627620012036,2075776627620012034,'FD20260711105808749',1000000000000005,'PAYFD20260711105808749',1,29.90,3,'2026-07-11 10:58:09',NULL,NULL,NULL,NULL,'2026-07-11 10:58:09','2026-07-11 10:58:08'),(2075776629171904514,2075776629108989954,'FD20260711105809110',1000000000000005,'PAYFD20260711105809110',1,29.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 10:58:09','2026-07-11 10:58:09'),(2075776759161774084,2075776759161774082,'FD20260711105840110',1000000000000005,'PAYFD20260711105840110',1,29.90,1,'2026-07-11 10:58:40',NULL,NULL,NULL,NULL,'2026-07-11 10:58:40','2026-07-11 10:58:40'),(2075777925278941186,2075777925211832322,'FD20260711110318107',1000000000000001,'PAYFD20260711110318107',1,29.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 11:03:18','2026-07-11 11:03:18'),(2075780893738856450,2075780893608833026,'FD20260711111505839',1000000000000001,'PAYFD20260711111505839',1,29.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 11:15:06','2026-07-11 11:15:05'),(2075781136651972611,2075781136651972609,'FD20260711111603785',1000000000000005,'PAYFD20260711111603785',1,29.90,1,'2026-07-11 11:16:04',NULL,NULL,NULL,NULL,'2026-07-11 11:16:04','2026-07-11 11:16:03'),(2075781960560078851,2075781960560078849,'FD20260711111920219',1000000000000005,'PAYFD20260711111920219',1,29.90,1,'2026-07-11 11:19:20',NULL,NULL,NULL,NULL,'2026-07-11 11:19:20','2026-07-11 11:19:20'),(2075782796036100099,2075782796036100097,'FD20260711112239407',1000000000000005,'PAYFD20260711112239407',1,29.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 11:22:39','2026-07-11 11:22:39'),(2075786521819602948,2075786521819602946,'FD20260711113727697',1000000000000005,'PAYFD20260711113727697',1,29.90,1,'2026-07-11 11:37:28',NULL,NULL,NULL,NULL,'2026-07-11 11:37:28','2026-07-11 11:37:27'),(2075803640145711105,2075803640011493377,'FD20260711124529006',1000000000000005,'PAYFD20260711124529006',1,19.90,1,'2026-07-11 12:46:08',NULL,NULL,NULL,NULL,'2026-07-11 12:45:29','2026-07-11 12:45:29'),(2075804537856790532,2075804537856790530,'FD20260711124903070',1000000000000005,'PAYFD20260711124903070',1,29.90,1,'2026-07-11 12:49:03',NULL,NULL,NULL,NULL,'2026-07-11 12:49:03','2026-07-11 12:49:03'),(2075804730975129603,2075804730975129601,'FD20260711124949113',1000000000000005,'PAYFD20260711124949113',1,45.00,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 12:49:49','2026-07-11 12:49:49'),(2075805362511503362,2075805362381479937,'FD20260711125219650',1000000000000005,'PAYFD20260711125219650',1,19.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 12:52:20','2026-07-11 12:52:19'),(2075806036154441730,2075806036053778433,'FD20260711125500254',1000000000000005,'PAYFD20260711125500254',1,68.00,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 12:55:00','2026-07-11 12:55:00'),(2075807591133896706,2075807591091953665,'FD20260711130111002',1000000000000005,'PAYFD20260711130111002',1,29.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 13:01:11','2026-07-11 13:01:11'),(2075807631076253700,2075807631076253698,'FD20260711130120550',1000000000000005,'PAYFD20260711130120550',1,29.90,1,'2026-07-11 13:01:21',NULL,NULL,NULL,NULL,'2026-07-11 13:01:21','2026-07-11 13:01:20'),(2075807919900303361,2075807919845777410,'FD20260711130229383',1000000000000005,'PAYFD20260711130229383',1,29.90,1,'2026-07-11 13:02:30',NULL,NULL,NULL,NULL,'2026-07-11 13:02:29','2026-07-11 13:02:29'),(2075808227468627970,2075808227422490626,'FD20260711130342718',1000000000000005,'PAYFD20260711130342718',1,29.90,1,'2026-07-11 13:03:43',NULL,NULL,NULL,NULL,'2026-07-11 13:03:43','2026-07-11 13:03:42'),(2075808229083435011,2075808229083435009,'FD20260711130343124',1000000000000005,'PAYFD20260711130343124',1,29.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 13:03:43','2026-07-11 13:03:43'),(2075814802509889539,2075814802446974977,'FD20260711132950342',1000000000000001,'PAYFD20260711132950342',1,29.90,3,'2026-07-11 13:29:51',NULL,NULL,NULL,NULL,'2026-07-11 13:29:50','2026-07-11 13:29:50'),(2075829735347814401,2075829735293288450,'FD20260711142910607',350470571903033361,'PAYFD20260711142910607',1,59.80,1,'2026-07-11 14:29:11',NULL,NULL,NULL,NULL,'2026-07-11 14:29:11','2026-07-11 14:29:10'),(2075829737914728452,2075829737914728450,'FD20260711142911232',350470571903033361,'PAYFD20260711142911232',1,29.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 14:29:11','2026-07-11 14:29:11'),(2075845307305385986,2075845307208916993,'FD20260711153103241',1000000000000001,'PAYFD20260711153103241',1,29.90,3,'2026-07-11 15:31:04',NULL,NULL,NULL,NULL,'2026-07-11 15:31:03','2026-07-11 15:31:03'),(2075852083673243649,2075852083610329090,'FD20260711155758861',1000000000000005,'PAYFD20260711155758861',1,29.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 15:57:59','2026-07-11 15:57:58'),(2075852085363548164,2075852085363548162,'FD20260711155759276',1000000000000005,'PAYFD20260711155759276',1,29.90,1,'2026-07-11 15:57:59',NULL,NULL,NULL,NULL,'2026-07-11 15:57:59','2026-07-11 15:57:59'),(2075852451907969027,2075852451907969025,'FD20260711155926673',1000000000000005,'PAYFD20260711155926673',1,29.90,0,NULL,NULL,NULL,NULL,NULL,'2026-07-11 15:59:27','2026-07-11 15:59:26'),(2075853975174340610,2075853975098843138,'FD20260711160529819',1000000000000001,'PAYFD20260711160529819',1,19.90,3,'2026-07-11 16:05:31',NULL,NULL,NULL,NULL,'2026-07-11 16:05:30','2026-07-11 16:05:29'),(2075855143711543298,2075855143619268610,'FD20260711161008413',1000000000000001,'PAYFD20260711161008413',1,29.90,3,'2026-07-11 16:10:09',NULL,NULL,NULL,NULL,'2026-07-11 16:10:08','2026-07-11 16:10:08');
/*!40000 ALTER TABLE `payment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `point_log`
--

DROP TABLE IF EXISTS `point_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `point_log` (
  `id` bigint(20) NOT NULL COMMENT '主键ID (Snowflake)',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` varchar(32) DEFAULT '' COMMENT '类型: earn/use/refund/freeze',
  `point` int(11) DEFAULT '0' COMMENT '积分变动(正=获得,负=扣减)',
  `balance` int(11) DEFAULT '0' COMMENT '变动后余额',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `relation_id` bigint(20) DEFAULT NULL COMMENT '关联业务ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `point_log`
--

LOCK TABLES `point_log` WRITE;
/*!40000 ALTER TABLE `point_log` DISABLE KEYS */;
INSERT INTO `point_log` VALUES (350327044791488512,1000000000000001,'earn',29,29,'订单350327038336442368消费赠送积分',350327038336442368,'2026-07-08 23:27:56'),(350462476225228800,1000000000000001,'refund',-29,0,'订单350327038336442368取消退回积分',350327038336442368,'2026-07-09 08:26:05'),(350462479421296640,1000000000000001,'refund',-88,0,'订单350216402520731648取消退回积分',350216402520731648,'2026-07-09 08:26:06'),(350462482365706240,1000000000000001,'refund',-88,0,'订单350216138338263040取消退回积分',350216138338263040,'2026-07-09 08:26:07'),(350462490192285696,1000000000000001,'refund',-29,0,'订单1783347794191取消退回积分',1783347794191,'2026-07-09 08:26:08'),(350462497276473344,1000000000000001,'refund',-88,0,'订单350129999820492800取消退回积分',350129999820492800,'2026-07-09 08:26:10'),(350543816626941952,350470571903033347,'earn',100,100,'新用户注册赠送积分',NULL,'2026-07-09 13:49:18'),(350597974390366208,1000000000000001,'earn',29,29,'订单350597968161812480消费赠送积分',350597968161812480,'2026-07-09 17:24:30'),(350597990861406208,1000000000000001,'refund',-29,0,'订单350597968161812480取消退回积分',350597968161812480,'2026-07-09 17:24:34'),(350912967338016768,1000000000000001,'earn',29,29,'订单350912961096880128消费赠送积分',350912961096880128,'2026-07-10 14:16:11'),(350915624001806336,1000000000000001,'earn',29,58,'订单350915620143034368消费赠送积分',350915620143034368,'2026-07-10 14:26:44'),(350917097293070336,1000000000000001,'earn',29,87,'订单350917084915666944消费赠送积分',350917084915666944,'2026-07-10 14:32:35');
/*!40000 ALTER TABLE `point_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_exchange_rule`
--

DROP TABLE IF EXISTS `points_exchange_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `points_exchange_rule` (
  `id` bigint(20) NOT NULL,
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `points_cost` int(11) NOT NULL COMMENT '消耗积分',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '兑换的优惠券ID',
  `coupon_name` varchar(100) DEFAULT NULL COMMENT '优惠券名称(冗余)',
  `coupon_value` decimal(10,2) DEFAULT '0.00' COMMENT '优惠券面额',
  `coupon_min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠券使用门槛',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态: 0停用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_exchange_rule`
--

LOCK TABLES `points_exchange_rule` WRITE;
/*!40000 ALTER TABLE `points_exchange_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `points_exchange_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_no` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品编号',
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `category_id` bigint(20) DEFAULT '0',
  `farmer_id` bigint(20) DEFAULT '0',
  `main_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品主图',
  `price` decimal(10,2) NOT NULL COMMENT '销售价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `stock` int(11) DEFAULT '0' COMMENT '库存数量',
  `sales` int(11) DEFAULT '0' COMMENT '销量',
  `unit` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计量单位（斤/公斤/件/箱）',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '商品重量（kg）',
  `origin_place` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产地',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '商品详情描述',
  `specification` text COLLATE utf8mb4_unicode_ci COMMENT '规格参数（JSON格式）',
  `is_traceable` tinyint(4) DEFAULT '1' COMMENT '是否可溯源（0否1是）',
  `trace_id` bigint(20) DEFAULT NULL COMMENT '溯源信息ID',
  `status` tinyint(4) DEFAULT '0' COMMENT '商品状态（0下架1上架2待审核）',
  `audit_status` tinyint(4) DEFAULT '0' COMMENT '审核状态（0待审核1通过2拒绝）',
  `audit_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核意见',
  `is_recommend` tinyint(4) DEFAULT '0' COMMENT '是否推荐（0否1是）',
  `is_new` tinyint(4) DEFAULT '0' COMMENT '是否新品（0否1是）',
  `is_hot` tinyint(4) DEFAULT '0' COMMENT '是否热销（0否1是）',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览次数',
  `collect_count` int(11) DEFAULT '0' COMMENT '收藏次数',
  `comment_count` int(11) DEFAULT '0' COMMENT '评论数',
  `good_rate` decimal(5,2) DEFAULT '100.00' COMMENT '好评率',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_no` (`product_no`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_recommend` (`is_recommend`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_is_new` (`is_new`),
  KEY `idx_category_status` (`category_id`,`status`),
  FULLTEXT KEY `idx_product_name_ft` (`product_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2075920672195428354 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1782894061841,'P1782894061840','阳山水蜜桃',12,6000000000000001,'/uploads/products/product-1782894031584-6c735019.png',88.00,128.00,NULL,298,2,'箱',2.50,'江苏无锡惠山区阳山镇','阳山白凤水蜜桃，国家地理标志产品。果大色艳，皮薄肉厚，汁多味甜，入口即化。有机种植，自然成熟，每一颗都经过人工精选。',NULL,1,1782894061809,1,1,NULL,0,0,0,0,0,0,0,100.00,'2026-07-01 16:21:01','2026-07-11 18:54:52',1),(9000000000000001,'P20250401001','有机番茄（5斤装）',8000000000000001,6000000000000001,'/images/products/番茄.png',29.90,39.90,15.00,500,535,'箱',2.50,'湖北武汉黄陂','自然成熟的有机番茄','{\"重量\":\"5斤/箱\",\"产地\":\"武汉黄陂\"}',1,1000000000000001,1,1,NULL,1,1,1,1,5600,320,156,98.50,'2026-06-29 12:51:02','2026-07-10 14:40:05',0),(9000000000000002,'P20250401002','从化桂味荔枝（3斤装）',8000000000000002,6000000000000002,'/images/products/荔枝.png',68.00,88.00,35.00,197,201,'箱',1.50,'广东广州从化','肉厚核小，清甜多汁','{\"重量\":\"3斤/箱\",\"品种\":\"桂味\"}',1,1000000000000002,1,1,NULL,1,0,1,2,8900,560,230,99.10,'2026-06-29 12:51:02','2026-07-08 21:17:09',0),(9000000000000003,'P20250401003','烟台红富士苹果（10斤装）',8000000000000002,6000000000000003,'/images/products/苹果.png',45.00,58.00,22.00,799,1,'箱',5.00,'山东烟台牟平','果大皮薄，脆甜多汁','{\"重量\":\"10斤/箱\",\"品种\":\"红富士\"}',1,NULL,1,1,NULL,0,1,0,3,120,15,0,100.00,'2026-06-29 12:51:02','2026-07-09 10:17:35',0),(9000000000000004,'P20250401004','鲜活鲈鱼（2条装）',8000000000000004,6000000000000004,'/images/products/鲈鱼.png',58.00,72.00,30.00,100,100,'件',1.80,'江苏苏州吴江','活鲜发货，肉质细嫩','{\"数量\":\"2条/件\",\"方式\":\"活鲜发货\"}',1,1000000000000003,1,1,NULL,0,1,1,4,3200,180,89,97.80,'2026-06-29 12:51:02','2026-07-09 10:12:02',0),(9000000000000005,'P20250401005','有机黄瓜（5斤装）',8000000000000001,6000000000000001,'/images/products/黄瓜.png',19.90,25.90,8.00,997,1001,'箱',2.50,'湖北武汉黄陂','口感脆嫩，适合凉拌','{\"重量\":\"5斤/箱\",\"保质期\":\"5天\"}',1,1000000000000004,1,1,NULL,1,0,1,5,6800,420,280,99.20,'2026-06-29 12:51:02','2026-07-09 10:12:03',0),(350475531399073792,'','有机鸡蛋',8000000000000003,0,'/uploads/products/88f20411-0e3d-4dad-8520-4933e1ee7459.png',17.00,20.00,NULL,100,0,'kg',2.00,'山东菏泽曹县','全宇宙最牛逼的鸡蛋，不来试试么....',NULL,1,NULL,0,0,NULL,0,1,0,0,0,0,0,100.00,'2026-07-09 09:17:58','2026-07-09 09:31:14',1),(350480100011151360,'100000001','有机鸡蛋',8000000000000003,0,'/uploads/products/ed1a59bb-3cd6-47f8-9739-c92adef59c12.png',17.00,20.00,NULL,100,0,'kg',2.00,'山东菏泽曹县','全宇宙最牛逼的鸡蛋，不来试试么...',NULL,1,NULL,1,1,NULL,1,1,0,0,0,0,0,100.00,'2026-07-09 09:36:07','2026-07-10 20:21:48',0),(2075830719818072066,'P1783751585330','test product',12,6000000000000001,'/images/products/default.png',19.90,0.00,NULL,100,0,'kg',0.00,'','test',NULL,1,1000000000000001,0,0,NULL,0,0,0,0,0,0,0,100.00,'2026-07-11 14:33:05','2026-07-11 14:33:05',0);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父分类ID',
  `category_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `category_icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图标',
  `category_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图片',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态（0禁用1启用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075532303837782018 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
INSERT INTO `product_category` VALUES (8000000000000001,0,'新鲜蔬菜','icon-vegetable','/category/vegetable.jpg',1,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(8000000000000002,0,'时令水果','icon-fruit','/category/fruit.jpg',2,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(8000000000000003,0,'肉禽蛋奶','icon-meat','/category/meat.jpg',3,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(8000000000000004,0,'海鲜水产','icon-seafood','/category/seafood.jpg',4,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(8000000000000005,0,'粮油干货','icon-grain','/category/grain.jpg',5,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(8000000000000006,8000000000000001,'酸角州','icon-grain','D:\\视频\\NVIDIA\\Delta Force\\Delta Force Screenshot 2025.10.03 - 10.49.07.69.png',6,1,'2026-07-06 22:19:34','2026-07-08 23:02:50',1);
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_comment`
--

DROP TABLE IF EXISTS `product_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_item_id` bigint(20) DEFAULT NULL COMMENT '订单项ID',
  `rating` tinyint(4) NOT NULL COMMENT '评分（1-5星）',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '评论内容',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评论图片（多个逗号分隔）',
  `is_anonymous` tinyint(4) DEFAULT '0' COMMENT '是否匿名（0否1是）',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
  `reply_count` int(11) DEFAULT '0' COMMENT '回复数',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态（0隐藏1显示）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075866888157044739 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_comment`
--

LOCK TABLES `product_comment` WRITE;
/*!40000 ALTER TABLE `product_comment` DISABLE KEYS */;
INSERT INTO `product_comment` VALUES (1100000000000001,9000000000000001,1000000000000005,NULL,NULL,5,'番茄非常新鲜，汁水丰富！','/comment/c01.jpg',0,25,1,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(1100000000000002,9000000000000001,1000000000000005,NULL,NULL,4,'味道不错，有2个压坏了',NULL,0,8,1,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(1100000000000003,9000000000000002,1000000000000005,NULL,NULL,5,'荔枝超甜！下次还买','/comment/c02.jpg',0,42,0,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(1100000000000004,9000000000000004,1000000000000005,NULL,NULL,5,'鲈鱼收到还是活的','/comment/c03.jpg',0,36,1,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(1100000000000005,9000000000000005,1000000000000005,NULL,NULL,5,'有机黄瓜脆嫩爽口',NULL,1,18,0,1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2075829737499492354,9000000000000001,350470571903033361,2075829735293288450,NULL,5,'very good','',0,0,0,0,'2026-07-11 14:29:11','2026-07-11 16:34:49',1),(2075854318218076162,350480100011151360,1000000000000001,NULL,NULL,5,'非常新鲜','',0,0,0,1,'2026-07-11 16:06:52','2026-07-11 16:07:18',0),(2075856661546274817,9000000000000002,1000000000000003,NULL,NULL,5,'肉多核小','',0,0,0,2,'2026-07-11 16:16:10','2026-07-11 16:30:53',1),(2075861294662635522,9000000000000001,1000000000000001,NULL,NULL,5,'好吃','',0,0,0,1,'2026-07-11 16:34:35','2026-07-11 16:34:34',0),(2075861529535270914,9000000000000002,1000000000000001,NULL,NULL,5,'超甜','',0,0,0,1,'2026-07-11 16:35:31','2026-07-11 16:35:30',0),(2075863274529312770,1782894061841,1000000000000003,NULL,NULL,5,'1','',0,0,0,1,'2026-07-11 16:42:27','2026-07-11 16:42:26',0),(2075863303482593281,350480100011151360,1000000000000003,NULL,NULL,5,'1','',0,0,0,1,'2026-07-11 16:42:34','2026-07-11 16:42:33',0),(2075863835362287617,9000000000000001,1000000000000005,NULL,NULL,5,'pending test comment','',0,0,0,1,'2026-07-11 16:44:41','2026-07-11 16:44:41',0),(2075863837853704194,9000000000000001,1000000000000005,NULL,NULL,3,'will be rejected','',0,0,0,0,'2026-07-11 16:44:41','2026-07-11 16:44:41',1),(2075863839032303617,9000000000000001,1000000000000005,NULL,NULL,4,'second comment same product','',0,0,0,0,'2026-07-11 16:44:42','2026-07-11 16:44:41',0),(2075864217543098370,1782894061841,1000000000000001,NULL,NULL,5,'2','',0,0,0,1,'2026-07-11 16:46:12','2026-07-11 16:47:24',0),(2075864295481655297,1782894061841,1000000000000001,NULL,NULL,5,'w','',0,0,0,1,'2026-07-11 16:46:30','2026-07-11 16:47:21',0),(2075864596821426178,9000000000000005,1000000000000001,NULL,NULL,5,'1','',0,0,0,0,'2026-07-11 16:47:42','2026-07-11 16:47:42',0),(2075865819716820993,9000000000000005,1000000000000001,NULL,NULL,5,'2','',0,0,0,0,'2026-07-11 16:52:34','2026-07-11 16:52:51',1),(2075866888157044738,9000000000000001,1000000000000003,NULL,NULL,5,'1','',0,0,0,0,'2026-07-11 16:56:49','2026-07-11 16:56:48',0);
/*!40000 ALTER TABLE `product_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_favorite`
--

DROP TABLE IF EXISTS `product_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075829739533729794 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_favorite`
--

LOCK TABLES `product_favorite` WRITE;
/*!40000 ALTER TABLE `product_favorite` DISABLE KEYS */;
INSERT INTO `product_favorite` VALUES (1200000000000002,1000000000000005,9000000000000002,'2026-06-29 12:51:02'),(1200000000000003,1000000000000005,9000000000000004,'2026-06-29 12:51:02'),(1200000000000004,1000000000000005,9000000000000005,'2026-06-29 12:51:02'),(1200000000000005,1000000000000001,9000000000000001,'2026-06-29 12:51:02'),(350216467331141632,1000000000000001,1782894061841,'2026-07-08 16:08:32'),(2075827686904266753,1000000000000005,9000000000000001,'2026-07-11 14:21:02'),(2075829739533729793,350470571903033361,9000000000000001,'2026-07-11 14:29:12');
/*!40000 ALTER TABLE `product_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_image`
--

DROP TABLE IF EXISTS `product_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `image_type` tinyint(4) DEFAULT '1' COMMENT '图片类型（1主图2详情图3规格图）',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000000000006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_image`
--

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;
INSERT INTO `product_image` VALUES (1000000000000001,9000000000000001,'/product/tomato_01.jpg',1,1,'2026-06-29 12:51:02'),(1000000000000002,9000000000000001,'/product/tomato_02.jpg',2,2,'2026-06-29 12:51:02'),(1000000000000003,9000000000000002,'/product/lychee_01.jpg',1,1,'2026-06-29 12:51:02'),(1000000000000004,9000000000000004,'/product/seabass_01.jpg',1,1,'2026-06-29 12:51:02'),(1000000000000005,9000000000000005,'/product/cucumber_01.jpg',1,1,'2026-06-29 12:51:02');
/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recharge_record`
--

DROP TABLE IF EXISTS `recharge_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recharge_record` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `balance_before` decimal(10,2) DEFAULT '0.00' COMMENT '充值前余额',
  `balance_after` decimal(10,2) DEFAULT '0.00' COMMENT '充值后余额',
  `pay_type` tinyint(4) DEFAULT '1' COMMENT '支付方式',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recharge_record`
--

LOCK TABLES `recharge_record` WRITE;
/*!40000 ALTER TABLE `recharge_record` DISABLE KEYS */;
INSERT INTO `recharge_record` VALUES (2075919520649277441,1000000000000005,200.00,500.00,700.00,1,1,'2026-07-11 20:25:57'),(2075919861902032898,1000000000000005,200.00,700.00,900.00,1,1,'2026-07-11 20:27:18');
/*!40000 ALTER TABLE `recharge_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seckill`
--

DROP TABLE IF EXISTS `seckill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seckill` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `seckill_price` decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
  `stock` int(11) DEFAULT '0' COMMENT '秒杀库存(独立)',
  `sold` int(11) DEFAULT '0' COMMENT '已售',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态: 0关闭 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seckill`
--

LOCK TABLES `seckill` WRITE;
/*!40000 ALTER TABLE `seckill` DISABLE KEYS */;
INSERT INTO `seckill` VALUES (350235720918671360,1782894061841,1.01,100,0,'2026-07-08 00:00:00','2026-08-07 00:00:00',1,'2026-07-08 17:25:02');
/*!40000 ALTER TABLE `seckill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopping_cart`
--

DROP TABLE IF EXISTS `shopping_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shopping_cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品名称（冗余）',
  `product_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品图片（冗余）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '商品单价',
  `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
  `selected` tinyint(4) DEFAULT '1' COMMENT '是否选中（0未选中1选中）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075829734748028931 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shopping_cart`
--

LOCK TABLES `shopping_cart` WRITE;
/*!40000 ALTER TABLE `shopping_cart` DISABLE KEYS */;
INSERT INTO `shopping_cart` VALUES (2075608812908785666,1000000000000005,9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png',29.90,3,1,'2026-07-10 23:51:19','2026-07-10 23:51:18'),(2075829734748028930,350470571903033361,9000000000000001,'有机番茄（5斤装）','/images/products/番茄.png',29.90,2,1,'2026-07-11 14:29:10','2026-07-11 14:29:10');
/*!40000 ALTER TABLE `shopping_cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_config` (
  `id` bigint(20) NOT NULL COMMENT '主键ID (Snowflake)',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` varchar(500) DEFAULT '' COMMENT '配置值',
  `config_name` varchar(200) DEFAULT '' COMMENT '配置名称',
  `config_group` varchar(50) DEFAULT 'base' COMMENT '配置分组: base/trade/point/security',
  `remark` varchar(500) DEFAULT '' COMMENT '备注说明',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (9000000000000101,'login_max_attempts','50','登录最大尝试次数','security','超过此次数锁定账户','2026-07-08 17:15:10'),(9000000000000102,'login_lock_minutes','301','登录锁定时长(分钟)','security','账户锁定后自动解锁时间','2026-07-08 17:15:06'),(9000000000000103,'password_expire_days','80','密码有效期(天)','security','超过天数强制修改密码','2026-07-08 17:15:01'),(9000000000000104,'order_auto_confirm_days','20','订单自动收货天数','trade','发货后自动确认收货','2026-07-08 17:14:56'),(9000000000000105,'after_sales_deadline_days','5','售后申请时效(天)','trade','确认收货后可申请售后的天数','2026-07-08 17:14:48'),(9000000000000106,'point_exchange_rate','1000','积分兑换比例','point','多少积分兑换1元','2026-07-08 17:15:17'),(9000000000000107,'point_earn_rate','10','积分赠送比例','point','消费1元赠送多少积分','2026-07-08 17:15:14'),(9000000000000108,'footprint_max_count','500','浏览足迹最大保留数','base','每个用户最多保留的足迹条数','2026-07-08 17:15:22');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `menu_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `menu_type` char(1) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单类型（M目录C菜单F按钮）',
  `path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由地址',
  `component` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件路径',
  `perms` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单图标',
  `sort` int(11) DEFAULT '0' COMMENT '显示排序',
  `visible` tinyint(4) DEFAULT '1' COMMENT '是否显示（0隐藏1显示）',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态（0停用1正常）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9000000000000009 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (4000000000000001,0,'系统管理','M','/system',NULL,NULL,'setting',1,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000002,4000000000000001,'用户管理','C','/system/user','system/user/index','system:user:list','user',1,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000003,4000000000000001,'角色管理','C','/system/role','system/role/index','system:role:list','peoples',2,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000004,4000000000000001,'菜单管理','C','/system/menu','system/menu/index','system:menu:list','tree',3,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000005,0,'商品管理','M','/product',NULL,NULL,'shopping',2,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000006,4000000000000005,'商品列表','C','/product/list','product/list/index','product:list','list',1,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000007,0,'溯源管理','M','/trace',NULL,NULL,'trace',3,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000008,4000000000000007,'溯源列表','C','/trace/list','trace/list/index','trace:list','log',1,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000009,0,'订单管理','M','/order',NULL,NULL,'order',4,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000010,4000000000000009,'订单列表','C','/order/list','order/list/index','order:list','form',1,1,1,NULL,'2026-06-29 12:41:01','2026-06-29 12:41:01',0),(4000000000000011,4000000000000001,'管理员账号','C','/admin/admins',NULL,'system:admin:list','User',4,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000012,4000000000000007,'删除审核','C','/admin/trace-delete-audit',NULL,'trace:delete:audit','Delete',2,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000013,4000000000000009,'售后工单','C','/admin/after-sales',NULL,'order:after-sales','Service',2,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000014,4000000000000009,'支付记录','C','/admin/payments',NULL,'order:payment','Money',3,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000015,4000000000000005,'分类管理','C','/admin/categories',NULL,'product:category:list','Menu',2,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000016,4000000000000005,'评论审核','C','/admin/comments',NULL,'product:comment:list','ChatLineSquare',3,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000017,0,'运营管理','M',NULL,NULL,NULL,'TrendCharts',4,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000018,4000000000000017,'轮播图管理','C','/admin/banners',NULL,'product:banner:list','Picture',1,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000019,4000000000000017,'用户地址','C','/admin/addresses',NULL,'system:address:list','Location',2,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000020,0,'日志管理','M',NULL,NULL,NULL,'Document',5,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(4000000000000021,4000000000000020,'操作日志','C','/admin/operation-logs',NULL,'system:log:list','List',1,1,1,NULL,'2026-07-08 10:48:07','2026-07-08 10:48:07',0),(9000000000000001,0,'仪表盘','C','/admin/dashboard',NULL,NULL,'DataAnalysis',0,1,1,NULL,'2026-07-09 09:52:52','2026-07-09 09:52:52',0),(9000000000000002,0,'营销管理','M',NULL,NULL,NULL,'Present',0,1,1,NULL,'2026-07-09 09:52:52','2026-07-09 09:52:52',0),(9000000000000003,9000000000000002,'优惠券管理','C','/admin/coupons',NULL,NULL,'Ticket',1,1,1,NULL,'2026-07-09 09:52:52','2026-07-09 09:52:52',0),(9000000000000004,9000000000000002,'秒杀管理','C','/admin/seckill',NULL,NULL,'Timer',2,1,1,NULL,'2026-07-09 09:52:52','2026-07-09 09:52:52',0),(9000000000000005,9000000000000002,'营销活动','C','/admin/marketing',NULL,NULL,'Promotion',3,1,1,NULL,'2026-07-09 09:52:52','2026-07-09 09:52:52',0),(9000000000000006,0,'会员管理','C','/admin/members',NULL,NULL,'UserFilled',0,1,1,NULL,'2026-07-09 09:52:52','2026-07-09 09:52:52',0),(9000000000000007,4000000000000001,'农户审核','C','/admin/farmer-audit',NULL,NULL,'Stamp',5,1,1,NULL,'2026-07-09 09:52:52','2026-07-09 09:52:52',0),(9000000000000008,4000000000000001,'系统配置','C','/admin/config',NULL,NULL,'Setting',6,1,1,NULL,'2026-07-09 09:52:52','2026-07-09 09:52:52',0);
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_notice` (
  `id` bigint(20) NOT NULL COMMENT '主键ID (Snowflake)',
  `user_id` bigint(20) NOT NULL COMMENT '接收用户ID',
  `notice_type` varchar(32) DEFAULT 'system' COMMENT '消息类型: order/after_sales/system',
  `title` varchar(200) DEFAULT '' COMMENT '消息标题',
  `content` varchar(1000) DEFAULT '' COMMENT '消息内容',
  `relation_id` bigint(20) DEFAULT NULL COMMENT '关联业务ID(订单ID/售后ID等)',
  `is_read` tinyint(4) DEFAULT '0' COMMENT '是否已读 0否 1是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (350327044753735680,1000000000000001,'order','订单支付成功','您的订单已支付成功，商品将尽快发出',350327038336442368,1,'2026-07-08 23:27:56'),(350462476103589888,1000000000000001,'order','订单已取消','您的订单已取消',350327038336442368,1,'2026-07-09 08:26:05'),(350462479370960896,1000000000000001,'order','订单已取消','您的订单已取消',350216402520731648,1,'2026-07-09 08:26:06'),(350462482311176192,1000000000000001,'order','订单已取消','您的订单已取消',350216138338263040,1,'2026-07-09 08:26:07'),(350462490137755648,1000000000000001,'order','订单已取消','您的订单已取消',1783347794191,1,'2026-07-09 08:26:08'),(350462497213554688,1000000000000001,'order','订单已取消','您的订单已取消',350129999820492800,1,'2026-07-09 08:26:10'),(350597974348419072,1000000000000001,'order','订单支付成功','您的订单已支付成功，商品将尽快发出',350597968161812480,1,'2026-07-09 17:24:30'),(350597990827847680,1000000000000001,'order','订单已取消','您的订单已取消',350597968161812480,1,'2026-07-09 17:24:34'),(350848038169882624,350470571903033347,'system','农户认证通过','恭喜！您的农户认证已通过审核',NULL,1,'2026-07-10 09:58:10'),(350858068743036928,350470571903033347,'system','农户资质已过期','您的农户资质已被撤销，原因：资质过期。如需继续经营，请重新申请认证。',NULL,1,'2026-07-10 10:38:02'),(350859383724449792,350470571903033347,'system','农户认证通过','恭喜！您的农户认证已通过审核',NULL,1,'2026-07-10 10:43:15'),(350912967245737984,1000000000000001,'order','订单支付成功','您的订单已支付成功，商品将尽快发出',350912961096880128,1,'2026-07-10 14:16:11'),(350915623959859200,1000000000000001,'order','订单支付成功','您的订单已支付成功，商品将尽快发出',350915620143034368,1,'2026-07-10 14:26:44'),(350916867386458112,350470571903033347,'system','农户资质已过期','您的农户资质已被撤销，原因：z。如需继续经营，请重新申请认证。',NULL,0,'2026-07-10 14:31:40'),(350917097230151680,1000000000000001,'order','订单支付成功','您的订单已支付成功，商品将尽快发出',350917084915666944,1,'2026-07-10 14:32:35'),(2075913293999439875,0,'marketing','新营销活动上线','【summer sale】活动已开始，快来参与吧！',NULL,0,'2026-07-11 20:01:13'),(2075919517465800707,0,'marketing','新营销活动上线','【test activity】活动已开始，快来参与吧！',NULL,0,'2026-07-11 20:25:56'),(2075919520712192002,1000000000000005,'member','会员升级通知','恭喜您升级为普通会员，享受9.8折优惠！',NULL,0,'2026-07-11 20:25:57'),(2075919862355017730,0,'marketing','新营销活动上线','【test】活动已开始，快来参与吧！',NULL,0,'2026-07-11 20:27:19'),(2075923074084601858,1000000000000005,'member','会员等级变更','您的会员等级已调整为银卡会员',NULL,0,'2026-07-11 20:40:04'),(2075924360632836097,1000000000000005,'member','会员等级变更','您的会员等级已调整为金卡会员',NULL,0,'2026-07-11 20:45:11');
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_operation_log`
--

DROP TABLE IF EXISTS `sys_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `username` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `module` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模块',
  `action` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型(ADD/UPDATE/DELETE/QUERY/LOGIN)',
  `target` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作目标',
  `req_method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法',
  `req_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求路径',
  `req_ip` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求IP',
  `req_params` text COLLATE utf8mb4_unicode_ci COMMENT '请求参数(脱敏)',
  `result` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '结果(SUCCESS/FAIL)',
  `duration` int(11) DEFAULT NULL COMMENT '耗时(ms)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=154 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_operation_log`
--

LOCK TABLES `sys_operation_log` WRITE;
/*!40000 ALTER TABLE `sys_operation_log` DISABLE KEYS */;
INSERT INTO `sys_operation_log` VALUES (1,17828724173376014,'1.7828724173376014e16','favorite','ADD','favorite:1782894061841','POST','/api/favorite/add','127.0.0.1','{\"action\":\"favorite\"}','SUCCESS',0,'2026-07-08 14:44:18'),(2,17828724173376014,'1.7828724173376014e16','favorite','ADD','favorite:9000000000000002','POST','/api/favorite/add','127.0.0.1','{\"action\":\"favorite\"}','SUCCESS',0,'2026-07-08 14:44:36'),(3,17828724173376014,'1.7828724173376014e16','favorite','REMOVE','favorite:1782894061841','DELETE','/api/favorite/remove/1782894061841','127.0.0.1','{\"action\":\"unfavorite\"}','SUCCESS',0,'2026-07-08 14:58:02'),(4,17828724173376014,'1.7828724173376014e16','favorite','REMOVE','favorite:9000000000000002','DELETE','/api/favorite/remove/9000000000000002','127.0.0.1','{\"action\":\"unfavorite\"}','SUCCESS',0,'2026-07-08 14:58:08'),(5,17828724173376014,'1.7828724173376014e16','favorite','ADD','favorite:1782894061841','POST','/api/favorite/add','127.0.0.1','{\"action\":\"favorite\"}','SUCCESS',0,'2026-07-08 15:06:40'),(6,17828724173376014,'1.7828724173376014e16','favorite','REMOVE','favorite:1782894061841','DELETE','/api/favorite/remove/1782894061841','127.0.0.1','{\"action\":\"unfavorite\"}','SUCCESS',0,'2026-07-08 15:07:08'),(7,1000000000000001,'1.000000000000001e15','admin','CREATE','coupon:350205607980179456','POST','/api/admin/coupons','127.0.0.1','{\"id\":null,\"name\":\"\",\"type\":\"full_reduce\",\"faceValue\":10,\"minAmount\":0,\"totalCount\":100,\"status\":1,\"startTime\":\"2026-07-01 00:00:00\",\"endTime\":\"2026-07-02 00:00:00\"}','SUCCESS',0,'2026-07-08 15:25:23'),(8,1000000000000001,'1.000000000000001e15','admin','CREATE','coupon:350206784411144192','POST','/api/admin/coupons','127.0.0.1','{\"id\":null,\"name\":\"\",\"type\":\"full_reduce\",\"faceValue\":10,\"minAmount\":0,\"totalCount\":100,\"status\":1,\"startTime\":\"2026-07-08 00:00:00\",\"endTime\":\"2026-07-17 00:00:00\"}','SUCCESS',0,'2026-07-08 15:30:03'),(9,17828724173376014,'17828724173376014','coupon','TAKE','coupon:9100000000000003','POST','/api/coupon/take/9100000000000003','127.0.0.1','{\"userId\":\"17828724173376014\"}','SUCCESS',0,'2026-07-08 15:39:57'),(10,17828724173376014,'17828724173376014','coupon','TAKE','coupon:9100000000000003','POST','/api/coupon/take/9100000000000003','127.0.0.1','{\"userId\":\"17828724173376014\"}','SUCCESS',0,'2026-07-08 15:44:36'),(11,17828724173376014,'1.7828724173376014e16','coupon','TAKE','coupon:9100000000000001','POST','/api/coupon/take/9100000000000001','127.0.0.1','{\"userId\":17828724173376014}','SUCCESS',0,'2026-07-08 15:47:57'),(12,17828724173376014,'1.7828724173376014e16','coupon','TAKE','coupon:9100000000000002','POST','/api/coupon/take/9100000000000002','127.0.0.1','{\"userId\":17828724173376014}','SUCCESS',0,'2026-07-08 15:47:58'),(13,17828724173376014,'1.7828724173376014e16','order','CREATE','ORD202607085375','POST','/api/order/create','127.0.0.1','{\"planTotal\":88,\"actualTotal\":88,\"items\":1}','SUCCESS',0,'2026-07-08 15:49:38'),(14,17828724173376014,'1.7828724173376014e16','order','CREATE','ORD202607083774','POST','/api/order/create','127.0.0.1','{\"planTotal\":88,\"actualTotal\":88,\"items\":1}','SUCCESS',0,'2026-07-08 15:52:15'),(15,17828724173376014,'17828724173376014','order','CREATE','ORD202607081213','POST','/api/order/create','127.0.0.1','{\"planTotal\":29.9,\"actualTotal\":19.9,\"items\":1}','SUCCESS',0,'2026-07-08 15:53:35'),(16,17828724173376014,'17828724173376014','order','CREATE','ORD202607087632','POST','/api/order/create','127.0.0.1','{\"planTotal\":88,\"actualTotal\":88,\"items\":1}','SUCCESS',0,'2026-07-08 15:54:49'),(17,17828724173376014,'17828724173376014','order','CREATE','ORD202607089794','POST','/api/order/create','127.0.0.1','{\"planTotal\":29.9,\"actualTotal\":29.9,\"items\":1}','SUCCESS',0,'2026-07-08 15:54:53'),(18,1000000000000001,'1000000000000001','admin','CREATE','coupon:350215226769174528','POST','/api/admin/coupons','192.168.31.64','{\"id\":null,\"name\":\"123\",\"type\":\"full_reduce\",\"faceValue\":10,\"minAmount\":0,\"totalCount\":100,\"status\":1,\"startTime\":\"2026-07-08 00:00:00\",\"endTime\":\"2026-08-09 00:00:00\"}','SUCCESS',0,'2026-07-08 16:03:36'),(19,1000000000000001,'1000000000000001','admin','CREATE','coupon:350215274890432512','POST','/api/admin/coupons','192.168.31.64','{\"id\":null,\"name\":\"123123\",\"type\":\"new_user\",\"faceValue\":10,\"minAmount\":0,\"totalCount\":100,\"status\":1,\"startTime\":\"2026-07-08 00:00:00\",\"endTime\":\"2026-08-08 00:00:00\"}','SUCCESS',0,'2026-07-08 16:03:48'),(20,1000000000000001,'1000000000000001','admin','CREATE','coupon:350215332402737152','POST','/api/admin/coupons','192.168.31.64','{\"id\":null,\"name\":\"123123123\",\"type\":\"general\",\"faceValue\":10,\"minAmount\":0,\"totalCount\":100,\"status\":1,\"startTime\":\"2026-07-08 00:00:00\",\"endTime\":\"2026-08-08 00:00:00\"}','SUCCESS',0,'2026-07-08 16:04:01'),(21,1000000000000001,'1000000000000001','order','CREATE','ORD202607088408','POST','/api/order/create','192.168.31.64','{\"planTotal\":88,\"actualTotal\":88,\"items\":1}','SUCCESS',0,'2026-07-08 16:07:14'),(22,1000000000000001,'1000000000000001','favorite','ADD','favorite:9000000000000002','POST','/api/favorite/add','192.168.31.64','{\"action\":\"favorite\"}','SUCCESS',0,'2026-07-08 16:07:46'),(23,1000000000000001,'1000000000000001','favorite','REMOVE','favorite:9000000000000002','DELETE','/api/favorite/remove/9000000000000002','192.168.31.64','{\"action\":\"unfavorite\"}','SUCCESS',0,'2026-07-08 16:07:47'),(24,1000000000000001,'1000000000000001','coupon','TAKE','coupon:9100000000000001','POST','/api/coupon/take/9100000000000001','192.168.31.64','{\"userId\":\"1000000000000001\"}','SUCCESS',0,'2026-07-08 16:08:01'),(25,1000000000000001,'1000000000000001','coupon','TAKE','coupon:9100000000000002','POST','/api/coupon/take/9100000000000002','192.168.31.64','{\"userId\":\"1000000000000001\"}','SUCCESS',0,'2026-07-08 16:08:02'),(26,1000000000000001,'1000000000000001','order','CREATE','ORD202607085180','POST','/api/order/create','192.168.31.64','{\"planTotal\":88,\"actualTotal\":88,\"items\":1}','SUCCESS',0,'2026-07-08 16:08:17'),(27,1000000000000001,'1000000000000001','favorite','ADD','favorite:1782894061841','POST','/api/favorite/add','192.168.31.64','{\"action\":\"favorite\"}','SUCCESS',0,'2026-07-08 16:08:32'),(28,1000000000000001,'1000000000000001','coupon','TAKE','coupon:9100000000000003','POST','/api/coupon/take/9100000000000003','192.168.31.64','{\"userId\":\"1000000000000001\"}','SUCCESS',0,'2026-07-08 16:08:41'),(29,1000000000000001,'1000000000000001','coupon','TAKE','coupon:350215226769174528','POST','/api/coupon/take/350215226769174528','192.168.31.64','{\"userId\":\"1000000000000001\"}','SUCCESS',0,'2026-07-08 16:08:42'),(30,1000000000000001,'1000000000000001','coupon','TAKE','coupon:350215274890432512','POST','/api/coupon/take/350215274890432512','192.168.31.64','{\"userId\":\"1000000000000001\"}','SUCCESS',0,'2026-07-08 16:08:42'),(31,1000000000000001,'1000000000000001','coupon','TAKE','coupon:350215332402737152','POST','/api/coupon/take/350215332402737152','192.168.31.64','{\"userId\":\"1000000000000001\"}','SUCCESS',0,'2026-07-08 16:08:42'),(32,1000000000000001,'1000000000000001','order','CREATE','ORD202607081247','POST','/api/order/create','192.168.31.64','{\"planTotal\":88,\"actualTotal\":88,\"items\":1}','SUCCESS',0,'2026-07-08 16:09:03'),(33,1000000000000001,'1000000000000001','order','CREATE','ORD202607084611','POST','/api/order/create','192.168.31.64','{\"planTotal\":68,\"actualTotal\":68,\"items\":1}','SUCCESS',0,'2026-07-08 16:13:39'),(34,1000000000000001,'1000000000000001','order','CREATE','ORD202607083352','POST','/api/order/create','192.168.31.64','{\"planTotal\":88,\"actualTotal\":88,\"items\":1}','SUCCESS',0,'2026-07-08 16:17:18'),(35,1000000000000001,'1000000000000001','order','CREATE','ORD202607088401','POST','/api/order/create','192.168.31.64','{\"planTotal\":952,\"actualTotal\":942,\"items\":1}','SUCCESS',0,'2026-07-08 16:29:03'),(36,1000000000000001,'1000000000000001','user','UPDATE','user:1000000000000001','PUT','/api/user/updatePassword','192.168.31.64','{\"action\":\"changePassword\"}','SUCCESS',0,'2026-07-08 17:03:02'),(37,1000000000000001,'1000000000000001','admin','UPDATE','config:after_sales_deadline_days','PUT','/api/admin/config/update','192.168.31.64','{\"value\":\"5\"}','SUCCESS',0,'2026-07-08 17:14:48'),(38,1000000000000001,'1000000000000001','admin','UPDATE','config:order_auto_confirm_days','PUT','/api/admin/config/update','192.168.31.64','{\"value\":\"20\"}','SUCCESS',0,'2026-07-08 17:14:56'),(39,1000000000000001,'1000000000000001','admin','UPDATE','config:password_expire_days','PUT','/api/admin/config/update','192.168.31.64','{\"value\":\"80\"}','SUCCESS',0,'2026-07-08 17:15:01'),(40,1000000000000001,'1000000000000001','admin','UPDATE','config:login_lock_minutes','PUT','/api/admin/config/update','192.168.31.64','{\"value\":\"301\"}','SUCCESS',0,'2026-07-08 17:15:06'),(41,1000000000000001,'1000000000000001','admin','UPDATE','config:login_max_attempts','PUT','/api/admin/config/update','192.168.31.64','{\"value\":\"50\"}','SUCCESS',0,'2026-07-08 17:15:10'),(42,1000000000000001,'1000000000000001','admin','UPDATE','config:point_earn_rate','PUT','/api/admin/config/update','192.168.31.64','{\"value\":\"10\"}','SUCCESS',0,'2026-07-08 17:15:14'),(43,1000000000000001,'1000000000000001','admin','UPDATE','config:point_exchange_rate','PUT','/api/admin/config/update','192.168.31.64','{\"value\":\"1000\"}','SUCCESS',0,'2026-07-08 17:15:17'),(44,1000000000000001,'1000000000000001','admin','UPDATE','config:footprint_max_count','PUT','/api/admin/config/update','192.168.31.64','{\"value\":\"500\"}','SUCCESS',0,'2026-07-08 17:15:22'),(45,1000000000000001,'1000000000000001','admin','CREATE','seckill:350235720918671360','POST','/api/admin/seckill','192.168.31.64','{\"id\":null,\"productId\":1782894061841,\"seckillPrice\":1.01,\"stock\":100,\"status\":1,\"startTime\":\"2026-07-08 00:00:00\",\"endTime\":\"2026-08-07 00:00:00\"}','SUCCESS',0,'2026-07-08 17:25:02'),(46,1000000000000001,'1000000000000001','admin','AUDIT','farmer_audit:7000000000000006','PUT','/api/admin/farmer/audit/7000000000000006','127.0.0.1','{\"approve\":true,\"remark\":\"\"}','SUCCESS',0,'2026-07-08 22:30:20'),(47,1000000000000001,'1000000000000001','order','CREATE','ORD202607083128','POST','/api/order/create','127.0.0.1','{\"planTotal\":29.9,\"actualTotal\":29.9,\"items\":1}','SUCCESS',0,'2026-07-08 23:27:54'),(48,1000000000000001,'1000000000000001','order','DELETE','order:350216596926648300','DELETE','/admin/order/delete/350216596926648300','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:28:49'),(49,1000000000000001,'1000000000000001','order','DELETE','order:350216596926648300','DELETE','/admin/order/delete/350216596926648300','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:28:52'),(50,1000000000000001,'1000000000000001','order','DELETE','order:350216596926648300','DELETE','/admin/order/delete/350216596926648300','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:28:54'),(51,1000000000000001,'1000000000000001','order','DELETE','order:350217754789457900','DELETE','/admin/order/delete/350217754789457900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:28:57'),(52,1000000000000001,'1000000000000001','order','DELETE','order:350216596926648300','DELETE','/admin/order/delete/350216596926648300','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:29:02'),(53,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663900','DELETE','/admin/order/delete/350221631907663900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:45:03'),(54,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663900','DELETE','/admin/order/delete/350221631907663900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:49:08'),(55,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663900','DELETE','/admin/order/delete/350221631907663900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:49:10'),(56,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663900','DELETE','/admin/order/delete/350221631907663900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:49:14'),(57,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663900','DELETE','/admin/order/delete/350221631907663900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:49:16'),(58,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663900','DELETE','/admin/order/delete/350221631907663900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:50:43'),(59,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663900','DELETE','/admin/order/delete/350221631907663900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:51:32'),(60,1000000000000001,'1000000000000001','order','DELETE','order:350218673182674940','DELETE','/admin/order/delete/350218673182674940','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:51:37'),(61,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663900','DELETE','/admin/order/delete/350221631907663900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:51:43'),(62,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663900','DELETE','/admin/order/delete/350221631907663900','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:52:10'),(63,1000000000000001,'1000000000000001','order','DELETE','order:350212367637946400','DELETE','/admin/order/delete/350212367637946400','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:52:19'),(64,1000000000000001,'1000000000000001','order','DELETE','order:350221631907663872','DELETE','/admin/order/delete/350221631907663872','127.0.0.1','{}','SUCCESS',0,'2026-07-08 23:53:42'),(65,1000000000000001,'1000000000000001','order','DELETE','order:350218673182674944','DELETE','/admin/order/delete/350218673182674944','127.0.0.1','{}','SUCCESS',0,'2026-07-09 08:25:42'),(66,1000000000000001,'1000000000000001','order','DELETE','order:350217754789457920','DELETE','/admin/order/delete/350217754789457920','127.0.0.1','{}','SUCCESS',0,'2026-07-09 08:25:45'),(67,1000000000000001,'1000000000000001','order','DELETE','order:350216596926648320','DELETE','/admin/order/delete/350216596926648320','127.0.0.1','{}','SUCCESS',0,'2026-07-09 08:25:47'),(68,1000000000000001,'1000000000000001','order','DELETE','order:350216138338263040','DELETE','/admin/order/delete/350216138338263040','127.0.0.1','{}','SUCCESS',0,'2026-07-09 08:26:41'),(69,1000000000000001,'1000000000000001','order','DELETE','order:350216402520731648','DELETE','/admin/order/delete/350216402520731648','127.0.0.1','{}','SUCCESS',0,'2026-07-09 08:26:42'),(70,1000000000000001,'1000000000000001','order','DELETE','order:350327038336442368','DELETE','/admin/order/delete/350327038336442368','127.0.0.1','{}','SUCCESS',0,'2026-07-09 08:26:44'),(71,1000000000000001,'1000000000000001','user','CREATE','user:350470469910138880','POST','/admin/user/add','192.168.31.64','{\"username\":\"xuxu\"}','SUCCESS',0,'2026-07-09 08:57:51'),(72,1000000000000001,'1000000000000001','user','CREATE','user:350470571903033344','POST','/admin/user/add','192.168.31.64','{\"username\":\"xixixixi\"}','SUCCESS',0,'2026-07-09 08:58:15'),(73,1000000000000001,'1000000000000001','product','CREATE','product:350475531399073792','POST','/admin/product/add','192.168.31.64','{\"name\":\"有机鸡蛋\"}','SUCCESS',0,'2026-07-09 09:17:58'),(74,1000000000000001,'1000000000000001','product','DELETE','product:350475531399073800','DELETE','/admin/product/delete/350475531399073800','192.168.31.64','{}','SUCCESS',0,'2026-07-09 09:19:31'),(75,1000000000000001,'1000000000000001','product','UPDATE','product:/350475531399073800','PUT','/admin/product/change-status/350475531399073800?status=1','192.168.31.64','{\"status\":1}','SUCCESS',0,'2026-07-09 09:21:45'),(76,1000000000000001,'1000000000000001','product','UPDATE','product:/350475531399073800','PUT','/admin/product/change-status/350475531399073800?status=1','192.168.31.64','{\"status\":1}','SUCCESS',0,'2026-07-09 09:21:54'),(77,1000000000000001,'1000000000000001','product','UPDATE','product:/350475531399073800','PUT','/admin/product/change-status/350475531399073800?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 09:22:14'),(78,1000000000000001,'1000000000000001','product','UPDATE','product:/9000000000000001','PUT','/admin/product/change-status/9000000000000001?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 09:22:17'),(79,1000000000000001,'1000000000000001','product','UPDATE','product:/9000000000000001','PUT','/admin/product/change-status/9000000000000001?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 09:22:18'),(80,1000000000000001,'1000000000000001','product','UPDATE','product:/9000000000000001','PUT','/admin/product/change-status/9000000000000001?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 09:22:21'),(81,1000000000000001,'1000000000000001','product','DELETE','product:350475531399073800','DELETE','/admin/product/delete/350475531399073800','192.168.31.64','{}','SUCCESS',0,'2026-07-09 09:23:24'),(82,1000000000000001,'1000000000000001','product','DELETE','product:350475531399073800','DELETE','/admin/product/delete/350475531399073800','192.168.31.64','{}','SUCCESS',0,'2026-07-09 09:24:28'),(83,1000000000000001,'1000000000000001','product','UPDATE','product:/350475531399073800','PUT','/admin/product/change-status/350475531399073800?status=1','192.168.31.64','{\"status\":1}','SUCCESS',0,'2026-07-09 09:26:34'),(84,1000000000000001,'1000000000000001','product','DELETE','product:350475531399073800','DELETE','/admin/product/delete/350475531399073800','192.168.31.64','{}','SUCCESS',0,'2026-07-09 09:27:44'),(85,1000000000000001,'1000000000000001','product','UPDATE','product:/1782894061841','PUT','/admin/product/change-status/1782894061841?status=0','192.168.31.64','{\"status\":0}','SUCCESS',0,'2026-07-09 09:28:54'),(86,1000000000000001,'1000000000000001','product','UPDATE','product:/9000000000000001','PUT','/admin/product/change-status/9000000000000001?status=0','192.168.31.64','{\"status\":0}','SUCCESS',0,'2026-07-09 09:28:55'),(87,1000000000000001,'1000000000000001','product','UPDATE','product:/9000000000000002','PUT','/admin/product/change-status/9000000000000002?status=0','192.168.31.64','{\"status\":0}','SUCCESS',0,'2026-07-09 09:28:58'),(88,1000000000000001,'1000000000000001','product','UPDATE','product:/9000000000000004','PUT','/admin/product/change-status/9000000000000004?status=0','192.168.31.64','{\"status\":0}','SUCCESS',0,'2026-07-09 09:28:59'),(89,1000000000000001,'1000000000000001','product','UPDATE','product:/9000000000000001','PUT','/admin/product/change-status/9000000000000001?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 09:29:09'),(90,1000000000000001,'1000000000000001','product','DELETE','product:350475531399073800','DELETE','/admin/product/delete/350475531399073800','127.0.0.1','{}','SUCCESS',0,'2026-07-09 09:30:32'),(91,1000000000000001,'1000000000000001','product','DELETE','product:350475531399073792','DELETE','/admin/product/delete/350475531399073792','127.0.0.1','{}','SUCCESS',0,'2026-07-09 09:31:14'),(92,1000000000000001,'1000000000000001','product','UPDATE','product:1782894061841','PUT','/admin/product/change-status/1782894061841?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 09:35:08'),(93,1000000000000001,'1000000000000001','product','UPDATE','product:1782894061841','PUT','/admin/product/change-status/1782894061841?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 09:35:09'),(94,1000000000000001,'1000000000000001','product','CREATE','product:350480100011151360','POST','/admin/product/add','192.168.31.64','{\"name\":\"有机鸡蛋\"}','SUCCESS',0,'2026-07-09 09:36:07'),(95,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','192.168.31.64','{\"status\":1}','SUCCESS',0,'2026-07-09 09:36:17'),(96,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','192.168.31.64','{\"status\":0}','SUCCESS',0,'2026-07-09 09:36:19'),(97,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','192.168.31.64','{\"status\":1}','SUCCESS',0,'2026-07-09 09:36:19'),(98,1000000000000001,'1000000000000001','order','DELETE','order:350129999820492800','DELETE','/admin/order/delete/350129999820492800','127.0.0.1','{}','SUCCESS',0,'2026-07-09 09:43:32'),(99,1000000000000001,'1000000000000001','order','DELETE','order:1782876956034','DELETE','/admin/order/delete/1782876956034','127.0.0.1','{}','SUCCESS',0,'2026-07-09 09:43:36'),(100,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 09:52:17'),(101,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 09:52:17'),(102,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 09:52:19'),(103,1000000000000001,'1000000000000001','product','UPDATE','product:1782894061841','PUT','/admin/product/change-status/1782894061841?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 09:52:34'),(104,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 09:52:47'),(105,1000000000000001,'1000000000000001','product','UPDATE','product:1782894061841','PUT','/admin/product/change-status/1782894061841?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 09:52:48'),(106,1000000000000001,'1000000000000001','product','UPDATE','product:9000000000000003','PUT','/admin/product/change-status/9000000000000003?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 09:52:52'),(107,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 10:11:31'),(108,1000000000000001,'1000000000000001','product','UPDATE','product:1782894061841','PUT','/admin/product/change-status/1782894061841?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 10:11:32'),(109,1000000000000001,'1000000000000001','product','UPDATE','product:9000000000000003','PUT','/admin/product/change-status/9000000000000003?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 10:11:36'),(110,1000000000000001,'1000000000000001','product','UPDATE','product:9000000000000005','PUT','/admin/product/change-status/9000000000000005?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 10:11:37'),(111,1000000000000001,'1000000000000001','product','UPDATE','product:9000000000000004','PUT','/admin/product/change-status/9000000000000004?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 10:11:38'),(112,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 10:11:49'),(113,1000000000000001,'1000000000000001','product','UPDATE','product:1782894061841','PUT','/admin/product/change-status/1782894061841?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 10:12:00'),(114,1000000000000001,'1000000000000001','product','UPDATE','product:9000000000000003','PUT','/admin/product/change-status/9000000000000003?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 10:12:02'),(115,1000000000000001,'1000000000000001','product','UPDATE','product:9000000000000004','PUT','/admin/product/change-status/9000000000000004?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 10:12:02'),(116,1000000000000001,'1000000000000001','product','UPDATE','product:9000000000000005','PUT','/admin/product/change-status/9000000000000005?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 10:12:03'),(117,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 10:17:24'),(118,1000000000000001,'1000000000000001','product','UPDATE','product:9000000000000003','PUT','/admin/product/change-status/9000000000000003?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 10:17:27'),(119,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 10:17:34'),(120,1000000000000001,'1000000000000001','product','UPDATE','product:9000000000000003','PUT','/admin/product/change-status/9000000000000003?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 10:17:35'),(121,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','192.168.31.64','{\"status\":0}','SUCCESS',0,'2026-07-09 10:22:06'),(122,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','192.168.31.64','{\"status\":1}','SUCCESS',0,'2026-07-09 10:22:07'),(123,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','192.168.31.64','{\"status\":0}','SUCCESS',0,'2026-07-09 10:22:08'),(124,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','192.168.31.64','{\"status\":1}','SUCCESS',0,'2026-07-09 10:22:16'),(125,1000000000000001,'1000000000000001','comment','COMMENT','product:9000000000000001','POST','/api/comment/direct','127.0.0.1','{\"rating\":5}','SUCCESS',0,'2026-07-09 12:59:27'),(126,350470571903033345,'350470571903033345','comment','COMMENT','product:350480100011151360','POST','/api/comment/direct','127.0.0.1','{\"rating\":5}','SUCCESS',0,'2026-07-09 13:18:46'),(127,350470571903033345,'350470571903033345','comment','COMMENT','product:9000000000000002','POST','/api/comment/direct','127.0.0.1','{\"rating\":5}','SUCCESS',0,'2026-07-09 13:36:35'),(128,1000000000000001,'1000000000000001','comment','APPROVE','comment:350540616951808000','PUT','/admin/comment/approve/350540616951808000','127.0.0.1','{}','SUCCESS',0,'2026-07-09 13:37:03'),(129,1000000000000001,'1000000000000001','comment','DELETE','comment:350536132511023104','DELETE','/admin/comment/delete/350536132511023104','127.0.0.1','{}','SUCCESS',0,'2026-07-09 13:38:52'),(130,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 13:47:15'),(131,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 13:47:22'),(132,1000000000000001,'1000000000000001','order','CREATE','ORD202607098493','POST','/api/order/create','127.0.0.1','{\"planTotal\":29.9,\"actualTotal\":29.9,\"items\":1}','SUCCESS',0,'2026-07-09 17:24:29'),(133,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 17:26:53'),(134,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 17:26:54'),(135,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-09 17:26:56'),(136,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=1','127.0.0.1','{\"status\":1}','SUCCESS',0,'2026-07-09 17:27:13'),(137,1000000000000001,'1000000000000001','admin','EXPORT','table:products','GET','/api/admin/export/products','127.0.0.1','{\"rows\":7}','SUCCESS',0,'2026-07-10 09:23:13'),(138,350470571903033347,'350470571903033347','farmer','CREATE','farmer:350847913599045632','POST','/api/farmer/apply','127.0.0.1','{\"action\":\"apply\"}','SUCCESS',0,'2026-07-10 09:57:41'),(139,1000000000000001,'1000000000000001','admin','AUDIT','farmer_audit:350847913624215552','PUT','/api/admin/farmer/audit/350847913624215552','127.0.0.1','{\"approve\":true,\"remark\":\"\"}','SUCCESS',0,'2026-07-10 09:58:10'),(140,1000000000000001,'1000000000000001','admin','EXPIRE','farmer:350847913599045632','PUT','/api/admin/farmer/expire/350847913599045632','127.0.0.1','{\"remark\":\"资质过期\"}','SUCCESS',0,'2026-07-10 10:38:02'),(141,350470571903033347,'350470571903033347','farmer','CREATE','farmer:350847913599045632','POST','/api/farmer/apply','127.0.0.1','{\"action\":\"re-apply\"}','SUCCESS',0,'2026-07-10 10:42:56'),(142,1000000000000001,'1000000000000001','admin','AUDIT','farmer_audit:350859301591584768','PUT','/api/admin/farmer/audit/350859301591584768','127.0.0.1','{\"approve\":true,\"remark\":\"\"}','SUCCESS',0,'2026-07-10 10:43:15'),(143,1000000000000001,'1000000000000001','admin','EXPORT','table:users','GET','/api/admin/export/users','127.0.0.1','{\"rows\":12}','SUCCESS',0,'2026-07-10 13:51:21'),(144,1000000000000001,'1000000000000001','order','CREATE','ORD202607104861','POST','/api/order/create','127.0.0.1','{\"planTotal\":29.9,\"actualTotal\":29.9,\"items\":1}','SUCCESS',0,'2026-07-10 14:16:09'),(145,1000000000000001,'1000000000000001','coupon','TAKE','coupon:9100000000000001','POST','/api/coupon/take/9100000000000001','127.0.0.1','{\"userId\":\"1000000000000001\"}','SUCCESS',0,'2026-07-10 14:17:42'),(146,1000000000000001,'1000000000000001','order','CREATE','ORD202607102438','POST','/api/order/create','127.0.0.1','{\"planTotal\":29.9,\"actualTotal\":29.9,\"items\":1}','SUCCESS',0,'2026-07-10 14:26:43'),(147,1000000000000001,'1000000000000001','product','UPDATE','product:350480100011151360','PUT','/admin/product/change-status/350480100011151360?status=0','127.0.0.1','{\"status\":0}','SUCCESS',0,'2026-07-10 14:28:08'),(148,1000000000000001,'1000000000000001','comment','DELETE','comment:350540616951808000','DELETE','/admin/comment/delete/350540616951808000','127.0.0.1','{}','SUCCESS',0,'2026-07-10 14:29:26'),(149,1000000000000001,'1000000000000001','admin','EXPIRE','farmer:350847913599045632','PUT','/api/admin/farmer/expire/350847913599045632','127.0.0.1','{\"remark\":\"z\"}','SUCCESS',0,'2026-07-10 14:31:40'),(150,1000000000000001,'1000000000000001','admin','CREATE','coupon:350917000278790144','POST','/api/admin/coupons','127.0.0.1','{\"id\":null,\"name\":\"d\",\"type\":\"full_reduce\",\"faceValue\":10,\"minAmount\":0,\"totalCount\":100,\"status\":1,\"startTime\":\"2026-07-10 00:00:00\",\"endTime\":\"2026-07-10 00:00:00\"}','SUCCESS',0,'2026-07-10 14:32:12'),(151,1000000000000001,'1000000000000001','coupon','TAKE','coupon:9100000000000002','POST','/api/coupon/take/9100000000000002','127.0.0.1','{\"userId\":\"1000000000000001\"}','SUCCESS',0,'2026-07-10 14:32:19'),(152,1000000000000001,'1000000000000001','order','CREATE','ORD202607109936','POST','/api/order/create','127.0.0.1','{\"planTotal\":29.9,\"actualTotal\":29.9,\"items\":1}','SUCCESS',0,'2026-07-10 14:32:32'),(153,1000000000000001,'1000000000000001','order','CREATE','ORD202607108339','POST','/api/order/create','127.0.0.1','{\"planTotal\":29.9,\"actualTotal\":19.9,\"items\":1}','SUCCESS',0,'2026-07-10 14:40:05');
/*!40000 ALTER TABLE `sys_operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `role_sort` int(11) DEFAULT '0' COMMENT '显示排序',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态（0禁用1启用）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2000000000000004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (2000000000000001,'管理员','ROLE_ADMIN',1,1,'系统管理员，拥有所有权限','2026-06-29 12:41:01','2026-06-29 12:41:01',0),(2000000000000002,'农户','ROLE_FARMER',2,1,'农户/商户，可管理自己的商品和溯源信息','2026-06-29 12:41:01','2026-06-29 12:41:01',0),(2000000000000003,'普通用户','ROLE_USER',3,1,'普通消费者，可浏览商品、下单购买','2026-06-29 12:41:01','2026-06-29 12:41:01',0);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=350484358232199169 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (3000000000000011,2000000000000001,4000000000000011,'2026-07-08 10:48:07'),(3000000000000012,2000000000000001,4000000000000012,'2026-07-08 10:48:07'),(3000000000000013,2000000000000001,4000000000000013,'2026-07-08 10:48:07'),(3000000000000014,2000000000000001,4000000000000014,'2026-07-08 10:48:07'),(3000000000000015,2000000000000001,4000000000000015,'2026-07-08 10:48:07'),(3000000000000016,2000000000000001,4000000000000016,'2026-07-08 10:48:07'),(3000000000000017,2000000000000001,4000000000000017,'2026-07-08 10:48:07'),(3000000000000018,2000000000000001,4000000000000018,'2026-07-08 10:48:07'),(3000000000000019,2000000000000001,4000000000000019,'2026-07-08 10:48:07'),(3000000000000020,2000000000000001,4000000000000020,'2026-07-08 10:48:07'),(3000000000000021,2000000000000001,4000000000000021,'2026-07-08 10:48:07'),(5000000000000001,2000000000000001,4000000000000001,'2026-06-29 12:41:01'),(5000000000000002,2000000000000001,4000000000000002,'2026-06-29 12:41:01'),(5000000000000003,2000000000000001,4000000000000003,'2026-06-29 12:41:01'),(5000000000000004,2000000000000001,4000000000000004,'2026-06-29 12:41:01'),(5000000000000005,2000000000000001,4000000000000005,'2026-06-29 12:41:01'),(350484358190206976,2000000000000001,4000000000000006,'2026-07-09 09:53:02'),(350484358198599680,2000000000000001,4000000000000007,'2026-07-09 09:53:02'),(350484358202798080,2000000000000001,4000000000000008,'2026-07-09 09:53:02'),(350484358206996480,2000000000000001,4000000000000009,'2026-07-09 09:53:02'),(350484358211194880,2000000000000001,4000000000000010,'2026-07-09 09:53:02'),(350484358215393280,2000000000000001,9000000000000001,'2026-07-09 09:53:02'),(350484358215397376,2000000000000001,9000000000000002,'2026-07-09 09:53:02'),(350484358219595776,2000000000000001,9000000000000003,'2026-07-09 09:53:02'),(350484358223794176,2000000000000001,9000000000000004,'2026-07-09 09:53:02'),(350484358223798272,2000000000000001,9000000000000005,'2026-07-09 09:53:02'),(350484358227996672,2000000000000001,9000000000000006,'2026-07-09 09:53:02'),(350484358232195072,2000000000000001,9000000000000007,'2026-07-09 09:53:02'),(350484358232199168,2000000000000001,9000000000000008,'2026-07-09 09:53:02');
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名/登录账号',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint(4) DEFAULT '0' COMMENT '性别（0未知1男2女）',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态（0禁用1启用）',
  `user_type` tinyint(4) NOT NULL COMMENT '用户类型（1普通用户2农户3管理员）',
  `real_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '账户余额',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后登录IP',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除（0未删除1已删除）',
  `member_level` int(11) DEFAULT '0',
  `total_spent` decimal(12,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2075830400618954756 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1000000000000001,'admin','$2b$10$IfG1yOvVV9s5UIXFMxSJPuilGWdEVjowpXYonKLs.ubPSb114.Pmu','超级管理员','/avatar/admin.jpg','13800000001','admin@fresh.com',1,1,3,'张管理','110101199001011234',10000.00,'2026-07-11 20:45:11','192.168.1.1','系统超级管理员','2026-06-29 12:41:01','2026-07-11 20:45:10',0,0,0.00),(1000000000000002,'operator01','$2a$10$vvZrMDBK2gpA/RW3B7wnKeTdMD11WztsoupVfXae9Xo3uNXEOen6G','运营小王','/avatar/op01.jpg','13800000002','wang@fresh.com',1,1,3,'王运营','110101199202022345',5000.00,'2025-05-01 10:30:00','192.168.1.2','运营管理员','2026-06-29 12:41:01','2026-07-11 14:31:49',0,0,0.00),(1000000000000003,'farmer_zhang','$2b$10$IfG1yOvVV9s5UIXFMxSJPuilGWdEVjowpXYonKLs.ubPSb114.Pmu','张大农户','/avatar/farmer01.jpg','13900000001','zhang@farm.com',1,1,2,'张三','420101198505053456',3000.00,'2026-07-11 20:30:32','10.0.0.1','有机蔬菜种植户','2026-06-29 12:41:01','2026-07-11 20:30:31',0,0,0.00),(1000000000000004,'farmer_li','$2b$10$IfG1yOvVV9s5UIXFMxSJPuilGWdEVjowpXYonKLs.ubPSb114.Pmu','李大姐果园','/avatar/farmer02.jpg','13900000002','li@farm.com',2,1,2,'李四','440101199006064567',2000.00,'2025-04-29 07:30:00','10.0.0.2','水果种植户','2026-06-29 12:41:01','2026-07-08 18:27:07',0,0,0.00),(1000000000000005,'user_wang','123456','t','/avatar/user01.jpg','13700000001','wanguser@qq.com',1,1,1,'王五','310101199808085678',900.00,'2026-07-11 20:45:11','172.16.0.1','普通消费者','2026-06-29 12:41:01','2026-07-11 20:45:11',0,0,900.00),(1000000000000006,'farmer_zhang2','$2b$10$IfG1yOvVV9s5UIXFMxSJPuilGWdEVjowpXYonKLs.ubPSb114.Pmu','张大农户水产',NULL,'13900000003',NULL,1,1,2,'张三',NULL,0.00,NULL,NULL,NULL,'2026-06-29 12:44:21','2026-07-08 18:27:07',0,0,0.00),(1000000000000007,'farmer_li2','$2b$10$IfG1yOvVV9s5UIXFMxSJPuilGWdEVjowpXYonKLs.ubPSb114.Pmu','李大姐牧场',NULL,'13900000004','2336839716@qq.com',2,1,2,'李四',NULL,0.00,NULL,NULL,NULL,'2026-06-29 12:46:07','2026-07-09 09:28:09',0,0,0.00),(17828724173376014,'ymd233','$2b$10$IfG1yOvVV9s5UIXFMxSJPuilGWdEVjowpXYonKLs.ubPSb114.Pmu','ymd233',NULL,'15810201666',NULL,0,1,2,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-01 10:20:17','2026-07-09 09:28:10',0,0,0.00),(17828724173376022,'zqj','$2b$10$IfG1yOvVV9s5UIXFMxSJPuilGWdEVjowpXYonKLs.ubPSb114.Pmu','gliese436b',NULL,'13518552901','2336839716@qq.com',0,1,1,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-08 14:11:29','2026-07-08 18:27:07',1,0,0.00),(17828724173376024,'111234','$2b$10$IfG1yOvVV9s5UIXFMxSJPuilGWdEVjowpXYonKLs.ubPSb114.Pmu','大哈比',NULL,'13111111111',NULL,0,1,1,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-08 17:11:03','2026-07-09 08:51:45',1,0,0.00),(17828724173376025,'嘻嘻','123345','嘻嘻',NULL,'13112312312','123123@fresh.com',0,1,1,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-09 08:52:23','2026-07-09 09:28:31',0,0,0.00),(350470469910138880,'xuxu','$2b$10$0EaXv3/VRfl.0gDc9E0G0eUTeu4cyBgY4NhzB92vZ5dRA9SZDDJLO','12333',NULL,'13112312311','123123123@fresh.com',0,1,3,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-09 08:57:51','2026-07-09 09:01:07',1,0,0.00),(350470571903033344,'xixixixi','$2b$10$gdZ6wJCWhjEizN0jsARuIOiCAl5ciQIIoZJZ/nGKJlwQ.lu9T4Dly','123345',NULL,'13112113111','1231231231@fresh.com',0,1,2,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-09 08:58:15','2026-07-09 09:01:05',1,0,0.00),(350470571903033345,'nailong','$2b$10$cu6HDDv4lTiNGpbbdIDFWO9iBcZa5tQSWvwDkkDKG/hGZhB5Y6t2S','宇宙无敌奶龙战神',NULL,'13112323434',NULL,0,1,1,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-09 11:43:40','2026-07-09 11:43:40',0,0,0.00),(350470571903033346,'gliese436b','$2b$10$6N9Mv.mssQwJ/3sjz9GrHOArVBdcyFx80sGxbiWkEp6XFxg9z5Roq','奶龙',NULL,'13418552901',NULL,0,1,1,NULL,NULL,0.00,'2026-07-11 18:50:40',NULL,NULL,'2026-07-09 13:44:52','2026-07-11 18:50:39',0,0,0.00),(350470571903033347,'gliese','$2b$10$sAuQjlcQsYkQP.setKgWnehukCMmxBmxCpQouiiuFZfk01ASURkgm','奶龙',NULL,'14518552901',NULL,0,1,1,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-09 13:49:18','2026-07-10 14:31:40',1,0,0.00),(350470571903033354,'realtest001','Test123!','RealTest',NULL,'13999990001',NULL,0,1,1,NULL,NULL,0.00,'2026-07-10 23:32:09',NULL,NULL,'2026-07-10 23:32:09','2026-07-10 23:32:08',0,0,0.00),(2075830395023753217,'admintest777','$2a$10$xIfm2BUHyH9G5BL1ySUat.CA0wvb8JO2oUOeODKNSJfBnaxja0VoK',NULL,NULL,NULL,NULL,0,1,1,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-11 14:31:47','2026-07-11 14:31:47',0,0,0.00),(2075830400618954754,'admintest666','$2a$10$bPTHegwHQTU4EOPx8XTlvOD7ekJTo2M1iG63BhyOL5fvpMxOVPkSC',NULL,NULL,NULL,NULL,0,1,3,NULL,NULL,0.00,NULL,NULL,NULL,'2026-07-11 14:31:49','2026-07-11 14:31:49',0,0,0.00);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3000000000000008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (3000000000000001,1000000000000001,2000000000000001,'2026-06-29 12:41:01'),(3000000000000002,1000000000000002,2000000000000001,'2026-06-29 12:41:01'),(3000000000000003,1000000000000003,2000000000000002,'2026-06-29 12:41:01'),(3000000000000004,1000000000000004,2000000000000002,'2026-06-29 12:41:01'),(3000000000000005,1000000000000005,2000000000000003,'2026-06-29 12:41:01'),(3000000000000006,1000000000000006,2000000000000002,'2026-06-29 12:44:21'),(3000000000000007,1000000000000007,2000000000000002,'2026-06-29 12:46:07');
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_breeding`
--

DROP TABLE IF EXISTS `trace_breeding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_breeding` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `breed_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '养殖类型（畜禽/水产）',
  `breed_date` date DEFAULT NULL COMMENT '养殖/放养日期',
  `species` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '养殖品种',
  `breed_quantity` decimal(10,2) DEFAULT NULL COMMENT '养殖数量',
  `breed_unit` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位（头/只/尾/羽）',
  `breed_density` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '养殖密度',
  `breed_area` decimal(10,2) DEFAULT NULL COMMENT '养殖面积（亩/平方米）',
  `feed_brand` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '饲料品牌',
  `feed_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '饲料名称',
  `feed_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '饲料类型（天然饲料/配合饲料/有机饲料）',
  `feed_source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '饲料来源',
  `medicine_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '兽药名称',
  `medicine_brand` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '兽药品牌',
  `medicine_usage` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '兽药用量',
  `medicine_interval` int(11) DEFAULT NULL COMMENT '停药期（天）',
  `vaccination_batch` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '防疫批次号',
  `vaccination_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '疫苗名称',
  `vaccination_date` date DEFAULT NULL COMMENT '防疫日期',
  `disinfection_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '圈舍/塘口消毒方式',
  `disinfection_date` date DEFAULT NULL COMMENT '消毒日期',
  `disinfection_drug` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消毒药品',
  `water_quality` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水质指标（水产专用，如pH/溶氧/氨氮）',
  `environment` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '养殖环境（圈舍/池塘/网箱/水库）',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '养殖说明',
  `operator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3300000000000006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='养殖记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_breeding`
--

LOCK TABLES `trace_breeding` WRITE;
/*!40000 ALTER TABLE `trace_breeding` DISABLE KEYS */;
INSERT INTO `trace_breeding` VALUES (3300000000000001,1300000000000003,'水产','2024-09-01','鲈鱼',5000.00,'尾','15尾/m³',300.00,'通威','鲈鱼专用膨化料','配合饲料','通威饲料厂直供',NULL,NULL,NULL,NULL,'FB20240901001','鲈鱼出血病疫苗','2024-09-15','生石灰全塘消毒','2024-08-25','生石灰','pH 7.2 溶氧6.5mg/L 氨氮<0.02mg/L','标准化养殖池塘','放养前清塘消毒、培水','张三','/trace/breed01.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45'),(3300000000000002,1300000000000003,'水产','2024-10-15','鲈鱼',5000.00,'尾','15尾/m³',300.00,'通威','鲈鱼育成料','配合饲料','通威饲料厂直供',NULL,NULL,NULL,NULL,'FB20241015001','烂鳃病疫苗','2024-10-15','漂白粉消毒','2024-10-10','漂白粉','pH 7.0 溶氧7.0mg/L','标准化养殖池塘','生长期加强营养','张三','/trace/breed02.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45'),(3300000000000003,1300000000000005,'畜禽','2024-03-01','黑头羊',200.00,'只','1只/10m²',2000.00,'蒙羊','育肥羊精补料','配合饲料','本地饲料厂',NULL,NULL,NULL,NULL,'FB20240301001','羊三联四防疫苗','2024-03-10','喷雾消毒','2024-02-25','聚维酮碘',NULL,'草原散放+半封闭羊舍','春季防疫全覆盖','王五','/trace/breed03.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45'),(3300000000000004,1300000000000005,'畜禽','2024-04-20','黑头羊',200.00,'只','1只/10m²',2000.00,'蒙羊','哺乳母羊料','配合饲料','本地饲料厂','伊维菌素','海利尔','0.2mg/kg体重',14,'FB20240420001','口蹄疫疫苗','2024-04-20','全场火焰消毒','2024-04-15','酒精喷灯',NULL,'草原散放+半封闭羊舍','驱虫后14天内禁止出栏','王五','/trace/breed04.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45'),(3300000000000005,1300000000000003,'水产','2025-01-10','鲈鱼',4800.00,'尾','14尾/m³',300.00,'通威','鲈鱼越冬料','配合饲料','通威饲料厂直供',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'食盐水浸泡消毒','2025-01-05','粗盐','pH 7.1 溶氧6.8mg/L','标准化养殖池塘','越冬期减料保膘','张三','/trace/breed05.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45');
/*!40000 ALTER TABLE `trace_breeding` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_fertilizer`
--

DROP TABLE IF EXISTS `trace_fertilizer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_fertilizer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `fertilize_date` date DEFAULT NULL COMMENT '施肥日期',
  `fertilizer_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '肥料类型（有机肥/化肥/复合肥等）',
  `fertilizer_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '肥料名称',
  `fertilizer_brand` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '肥料品牌',
  `dosage` decimal(10,2) DEFAULT NULL COMMENT '用量（kg/亩）',
  `fertilize_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '施肥方式',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '施肥说明',
  `operator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075830718664638467 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='施肥记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_fertilizer`
--

LOCK TABLES `trace_fertilizer` WRITE;
/*!40000 ALTER TABLE `trace_fertilizer` DISABLE KEYS */;
INSERT INTO `trace_fertilizer` VALUES (1500000000000001,1300000000000001,'2025-03-01','有机肥','腐熟鸡粪','绿源',200.00,'沟施覆土','移栽前基肥','张三','/trace/fert01.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1500000000000002,1300000000000001,'2025-03-20','复合肥','NPK 15-15-15','金正大',30.00,'穴施浇水','追肥促进开花结果','张三','/trace/fert02.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1500000000000003,1300000000000002,'2024-05-01','有机肥','花生麸肥','本地农家',150.00,'环沟施','壮花肥','李四','/trace/fert03.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1500000000000004,1300000000000004,'2025-03-10','有机肥','生物有机肥','沃地丰',100.00,'撒施翻耕','配合微生物菌肥','张三','/trace/fert04.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1500000000000005,1300000000000005,'2024-05-15','有机肥','羊粪有机肥','蒙肥',300.00,'树盘撒施','每棵树5kg','王五','/trace/fert05.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02');
/*!40000 ALTER TABLE `trace_fertilizer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_harvest`
--

DROP TABLE IF EXISTS `trace_harvest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_harvest` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `harvest_date` datetime DEFAULT NULL COMMENT '采摘时间',
  `harvest_quantity` decimal(10,2) DEFAULT NULL COMMENT '采摘数量',
  `harvest_unit` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位（斤/公斤/吨）',
  `maturity` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '成熟度',
  `harvest_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '采摘方式',
  `storage_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '采收后储存方式',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '采摘说明',
  `operator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1800000000000006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采摘记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_harvest`
--

LOCK TABLES `trace_harvest` WRITE;
/*!40000 ALTER TABLE `trace_harvest` DISABLE KEYS */;
INSERT INTO `trace_harvest` VALUES (1800000000000001,1300000000000001,'2025-04-08 06:30:00',2500.00,'斤','九成熟','人工采摘','采后2小时内预冷','清晨采摘保证果皮完整','张三','/trace/harvest01.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1800000000000002,1300000000000002,'2025-06-10 05:00:00',3000.00,'斤','八成熟','人工剪枝采摘','冷库预冷至5°C','凌晨采摘保持新鲜','李四','/trace/harvest02.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1800000000000003,1300000000000003,'2025-04-18 07:00:00',500.00,'斤','商品规格','网箱捕捞','活水暂养池','充氧保活','张三','/trace/harvest03.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1800000000000004,1300000000000004,'2025-04-22 06:00:00',5000.00,'斤','九成熟','人工采摘','预冷分拣包装','瓜条翠绿无黄斑','张三','/trace/harvest04.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1800000000000005,1300000000000005,'2024-10-12 08:00:00',15000.00,'斤','完熟','人工采摘+辅助梯','分级入库气调储藏','着色85%以上采摘','王五','/trace/harvest05.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02');
/*!40000 ALTER TABLE `trace_harvest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_image`
--

DROP TABLE IF EXISTS `trace_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `image_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片类型（种植/施肥/农药/灌溉/采摘/质检/物流/仓储）',
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `image_desc` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片描述',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_image_type` (`image_type`)
) ENGINE=InnoDB AUTO_INCREMENT=178288990411475 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='溯源图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_image`
--

LOCK TABLES `trace_image` WRITE;
/*!40000 ALTER TABLE `trace_image` DISABLE KEYS */;
INSERT INTO `trace_image` VALUES (178288985882363,1300000000000001,'planting','/uploads/trace/1782889858821-586518c8.png',NULL,0,'2026-07-01 15:10:58','2026-07-01 15:10:58'),(178288987483617,1300000000000001,'fertilizer','/uploads/trace/1782889874834-af54608a.png',NULL,0,'2026-07-01 15:11:14','2026-07-01 15:11:14'),(178288987493394,1300000000000001,'fertilizer','/uploads/trace/1782889874930-5ee5e9ae.png',NULL,0,'2026-07-01 15:11:14','2026-07-01 15:11:14'),(178288988399828,1300000000000001,'pesticide','/uploads/trace/1782889883996-b5edc830.png',NULL,0,'2026-07-01 15:11:24','2026-07-01 15:11:24'),(178288988406545,1300000000000001,'pesticide','/uploads/trace/1782889884063-31845f43.png',NULL,0,'2026-07-01 15:11:24','2026-07-01 15:11:24'),(178288989384736,1300000000000001,'irrigation','/uploads/trace/1782889893845-a8a4c448.png',NULL,0,'2026-07-01 15:11:33','2026-07-01 15:11:33'),(178288989391881,1300000000000001,'irrigation','/uploads/trace/1782889893915-7d894ef5.png',NULL,0,'2026-07-01 15:11:33','2026-07-01 15:11:33'),(178288990411474,1300000000000001,'harvest','/uploads/trace/1782889904112-7a03a6d3.png',NULL,0,'2026-07-01 15:11:44','2026-07-01 15:11:44');
/*!40000 ALTER TABLE `trace_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_inspection`
--

DROP TABLE IF EXISTS `trace_inspection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_inspection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `inspection_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质检报告编号',
  `inspection_date` date DEFAULT NULL COMMENT '质检日期',
  `inspection_agency` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质检机构',
  `inspector` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质检员',
  `inspection_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质检类型（自检/第三方检测）',
  `inspection_items` text COLLATE utf8mb4_unicode_ci COMMENT '检测项目（JSON格式）',
  `inspection_result` tinyint(4) DEFAULT NULL COMMENT '检测结果（1合格2不合格）',
  `pesticide_residue` tinyint(4) DEFAULT NULL COMMENT '农药残留检测（1合格2不合格）',
  `heavy_metal` tinyint(4) DEFAULT NULL COMMENT '重金属检测（1合格2不合格）',
  `microorganism` tinyint(4) DEFAULT NULL COMMENT '微生物检测（1合格2不合格）',
  `report_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质检报告PDF URL',
  `conclusion` text COLLATE utf8mb4_unicode_ci COMMENT '质检结论',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '备注说明',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质检图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inspection_no` (`inspection_no`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1900000000000006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='质检报告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_inspection`
--

LOCK TABLES `trace_inspection` WRITE;
/*!40000 ALTER TABLE `trace_inspection` DISABLE KEYS */;
INSERT INTO `trace_inspection` VALUES (1900000000000001,1300000000000001,'QC20250401001','2025-04-09','湖北省农产品质量安全检测中心','陈检测','第三方检测','{\"农药残留\":\"有机磷<0.01mg/kg\",\"重金属\":\"铅<0.1mg/kg\"}',1,1,1,1,'/report/QC001.pdf','各项指标均合格','通过有机认证检测','/trace/insp01.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1900000000000002,1300000000000002,'QC20250401002','2025-06-11','广东省食品检验所','刘检验','第三方检测','{\"农药残留\":\"合格\",\"重金属\":\"合格\"}',1,1,1,1,'/report/QC002.pdf','品质优良','当日采摘当日送检','/trace/insp02.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1900000000000003,1300000000000003,'QC20250401003','2025-04-19','苏州市水产品质量检测中心','赵检测','第三方检测','{\"氯霉素\":\"未检出\",\"孔雀石绿\":\"未检出\"}',1,1,1,1,'/report/QC003.pdf','药残全部合格','活鱼取样送检','/trace/insp03.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1900000000000004,1300000000000004,'QC20250401004','2025-04-23','湖北省农产品质量安全检测中心','陈检测','自检','{\"农药残留\":\"合格\"}',1,1,1,1,'/report/QC004.pdf','各项指标合格','自检后送第三方复检','/trace/insp04.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1900000000000005,1300000000000005,'QC20250401005','2024-10-15','烟台市农产品质量检测中心','孙检测','第三方检测','{\"农药残留\":\"阿维菌素<0.005mg/kg\",\"糖度\":\"14.5Brix\"}',1,1,1,1,'/report/QC005.pdf','安全指标达标','入库前质检','/trace/insp05.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02');
/*!40000 ALTER TABLE `trace_inspection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_irrigation`
--

DROP TABLE IF EXISTS `trace_irrigation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_irrigation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `irrigation_date` date DEFAULT NULL COMMENT '灌溉日期',
  `irrigation_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '灌溉方式（滴灌/喷灌/漫灌等）',
  `water_volume` decimal(10,2) DEFAULT NULL COMMENT '用水量（立方米/亩）',
  `water_source` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水源类型',
  `duration` int(11) DEFAULT NULL COMMENT '灌溉时长（分钟）',
  `weather` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '天气情况',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '灌溉说明',
  `operator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075830719142789122 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='灌溉记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_irrigation`
--

LOCK TABLES `trace_irrigation` WRITE;
/*!40000 ALTER TABLE `trace_irrigation` DISABLE KEYS */;
INSERT INTO `trace_irrigation` VALUES (1700000000000001,1300000000000001,'2025-02-20','滴灌',3.50,'地下水',60,'晴','移栽后浇定根水','张三','/trace/irr01.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1700000000000002,1300000000000001,'2025-03-15','滴灌',2.80,'地下水',45,'多云','生长期补水','张三',NULL,'2026-06-29 12:51:02','2026-06-29 12:51:02'),(1700000000000003,1300000000000002,'2024-05-10','喷灌',5.00,'山泉水',90,'晴','膨果期灌溉','李四','/trace/irr02.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1700000000000004,1300000000000004,'2025-03-05','滴灌',3.00,'地下水',50,'阴','播种后浇透水','张三',NULL,'2026-06-29 12:51:02','2026-06-29 12:51:02'),(1700000000000005,1300000000000005,'2024-07-20','漫灌',8.00,'河水',120,'晴','夏季高温补水','王五','/trace/irr03.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02');
/*!40000 ALTER TABLE `trace_irrigation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_logistics`
--

DROP TABLE IF EXISTS `trace_logistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_logistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `logistics_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '物流单号',
  `carrier_company` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '承运公司',
  `driver_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '司机姓名',
  `driver_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '司机电话',
  `vehicle_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '车牌号',
  `vehicle_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '车辆类型（冷藏车/保温车等）',
  `start_place` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '出发地',
  `end_place` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目的地',
  `start_time` datetime DEFAULT NULL COMMENT '出发时间',
  `expect_arrive_time` datetime DEFAULT NULL COMMENT '预计到达时间',
  `actual_arrive_time` datetime DEFAULT NULL COMMENT '实际到达时间',
  `temperature_require` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '温度要求',
  `min_temperature` decimal(5,2) DEFAULT NULL COMMENT '最低温度',
  `max_temperature` decimal(5,2) DEFAULT NULL COMMENT '最高温度',
  `current_temperature` decimal(5,2) DEFAULT NULL COMMENT '当前温度',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '湿度（%）',
  `transport_status` tinyint(4) DEFAULT '0' COMMENT '运输状态（0待发货1运输中2已送达）',
  `current_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前位置',
  `route_description` text COLLATE utf8mb4_unicode_ci COMMENT '运输路线描述',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '物流图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_logistics_no` (`logistics_no`),
  KEY `idx_transport_status` (`transport_status`)
) ENGINE=InnoDB AUTO_INCREMENT=2000000000000006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='冷链物流记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_logistics`
--

LOCK TABLES `trace_logistics` WRITE;
/*!40000 ALTER TABLE `trace_logistics` DISABLE KEYS */;
INSERT INTO `trace_logistics` VALUES (2000000000000001,1300000000000001,'WL20250410001','顺丰冷运','周师傅','13600000001','鄂A·12345','冷藏车','湖北武汉黄陂','湖北武汉武昌','2025-04-10 08:00:00','2025-04-10 12:00:00','2025-04-10 11:30:00','2-8°C',2.00,8.00,5.00,85.00,2,'武汉武昌分拨中心','黄陂→岱黄高速→武昌配送站','/trace/log01.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(2000000000000002,1300000000000002,'WL20250612001','京东冷链','吴师傅','13600000002','粤A·67890','冷藏车','广东广州从化','广东深圳','2025-06-12 05:00:00','2025-06-12 10:00:00','2025-06-12 09:45:00','0-5°C',0.00,5.00,3.00,90.00,2,'深圳龙华配送站','从化→京港澳高速→深圳','/trace/log02.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(2000000000000003,1300000000000003,'WL20250419001','中通冷链','郑师傅','13600000003','苏E·11111','活鲜运输车','江苏苏州吴江','上海浦东','2025-04-19 06:00:00','2025-04-19 10:00:00','2025-04-19 09:30:00','活鲜充氧',15.00,22.00,18.00,NULL,2,'上海浦东配送站','吴江→G50→上海浦东','/trace/log03.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(2000000000000004,1300000000000004,'WL20250423001','顺丰冷运','周师傅','13600000001','鄂A·12345','冷藏车','湖北武汉黄陂','湖北武汉洪山','2025-04-23 07:00:00','2025-04-23 11:00:00',NULL,'2-8°C',2.00,8.00,4.50,88.00,1,'武汉洪山区配送中','黄陂→市区配送','/trace/log04.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(2000000000000005,1300000000000005,'WL20241020001','德邦冷链','马师傅','13600000004','鲁F·22222','冷藏车','山东烟台牟平','山东济南','2024-10-20 06:00:00','2024-10-20 14:00:00','2024-10-20 13:20:00','0-4°C',0.00,4.00,2.00,85.00,2,'济南历下配送站','牟平→沈海高速→济南','/trace/log05.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02');
/*!40000 ALTER TABLE `trace_logistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_pesticide`
--

DROP TABLE IF EXISTS `trace_pesticide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_pesticide` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `use_date` date DEFAULT NULL COMMENT '使用日期',
  `pesticide_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '农药类型（杀虫剂/杀菌剂/除草剂等）',
  `pesticide_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '农药名称',
  `pesticide_brand` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '农药品牌',
  `dosage` decimal(10,2) DEFAULT NULL COMMENT '用量',
  `dilution_ratio` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '稀释比例',
  `use_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '使用方式',
  `safety_interval` int(11) DEFAULT NULL COMMENT '安全间隔期（天）',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '使用说明',
  `operator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075830718924685314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='农药使用记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_pesticide`
--

LOCK TABLES `trace_pesticide` WRITE;
/*!40000 ALTER TABLE `trace_pesticide` DISABLE KEYS */;
INSERT INTO `trace_pesticide` VALUES (1600000000000001,1300000000000001,'2025-03-10','生物农药','苏云金杆菌','BT绿科',50.00,'1:500','叶面喷施',3,'有机种植专用','张三','/trace/pest01.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1600000000000002,1300000000000001,'2025-03-25','生物农药','苦参碱','植保源',30.00,'1:800','叶面喷施',5,'防治蚜虫','张三',NULL,'2026-06-29 12:51:02','2026-06-29 12:51:02'),(1600000000000003,1300000000000002,'2024-04-15','生物农药','印楝素','农博士',20.00,'1:1000','叶面喷施',7,'采摘前30天停用','李四','/trace/pest02.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1600000000000004,1300000000000004,'2025-03-20','生物农药','多抗霉素','科诺',25.00,'1:600','叶面喷施',3,'防治霜霉病','张三',NULL,'2026-06-29 12:51:02','2026-06-29 12:51:02'),(1600000000000005,1300000000000005,'2024-06-01','低毒农药','阿维菌素','海利尔',10.00,'1:2000','叶面喷施',14,'防治红蜘蛛','王五','/trace/pest03.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02');
/*!40000 ALTER TABLE `trace_pesticide` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_planting`
--

DROP TABLE IF EXISTS `trace_planting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_planting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `planting_date` date DEFAULT NULL COMMENT '种植日期',
  `seed_variety` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '种子品种',
  `seed_quantity` decimal(10,2) DEFAULT NULL COMMENT '播种量',
  `planting_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '种植方式',
  `soil_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '土壤类型',
  `climate_condition` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '气候条件',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '种植说明',
  `operator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现场图片（多个逗号分隔）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2075830718392008706 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='种植记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_planting`
--

LOCK TABLES `trace_planting` WRITE;
/*!40000 ALTER TABLE `trace_planting` DISABLE KEYS */;
INSERT INTO `trace_planting` VALUES (1782895481250,1782893910829,'2026-07-01','',NULL,'1','1',NULL,'','',NULL,'2026-07-01 16:44:41','2026-07-01 16:44:41'),(1400000000000001,1300000000000001,'2025-02-15','浙粉208',2.50,'大棚育苗移栽','沙壤土','晴天15-22°C','有机基质育苗，苗龄35天后移栽','张三','/trace/planting01.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1400000000000002,1300000000000002,'2024-03-20','从化桂味母树苗',500.00,'嫁接苗定植','红壤','多云20-28°C','行距4米，株距3米定植','李四','/trace/planting03.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1400000000000003,1300000000000003,'2024-09-01','吴江本地鲈鱼苗',5000.00,'池塘放养','鱼塘底泥','晴天水温22°C','鱼苗规格3-5cm，放养密度15尾/立方米','张三','/trace/planting04.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1400000000000004,1300000000000004,'2025-03-01','寿光绿优3号',1.80,'大棚直播','沙壤土','晴天12-20°C','穴盘育苗，每穴2粒','张三','/trace/planting05.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(1400000000000005,1300000000000005,'2024-04-10','烟台红富士脱毒苗',300.00,'矮化密植','棕壤','晴天15-25°C','M9T337矮化砧木','王五','/trace/planting06.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02');
/*!40000 ALTER TABLE `trace_planting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_processing`
--

DROP TABLE IF EXISTS `trace_processing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_processing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `processing_date` datetime DEFAULT NULL COMMENT '加工时间',
  `processing_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加工类型（分拣/清洗/切割/包装/灭菌/冷冻）',
  `processing_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '加工方式说明',
  `process_temperature` decimal(5,2) DEFAULT NULL COMMENT '加工温度（℃）',
  `process_duration` int(11) DEFAULT NULL COMMENT '加工时长（分钟）',
  `package_spec` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '包装规格（如500g/盒、2斤/箱）',
  `package_material` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '包装材料（真空袋/泡沫箱/纸箱）',
  `additive_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '添加剂名称（防腐剂/保鲜剂，无则为空）',
  `additive_brand` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '添加剂品牌',
  `additive_amount` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '添加剂用量',
  `batch_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '加工批次号',
  `quality_check` tinyint(4) DEFAULT NULL COMMENT '出厂检验（1合格 2不合格）',
  `output_quantity` decimal(10,2) DEFAULT NULL COMMENT '产出数量',
  `output_unit` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产出单位',
  `shelf_life` int(11) DEFAULT NULL COMMENT '加工后保质期（天）',
  `storage_require` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '储存要求',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '加工说明',
  `operator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现场图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3400000000000006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='加工包装记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_processing`
--

LOCK TABLES `trace_processing` WRITE;
/*!40000 ALTER TABLE `trace_processing` DISABLE KEYS */;
INSERT INTO `trace_processing` VALUES (3400000000000001,1300000000000001,'2025-04-08 10:00:00','分拣','人工分级筛选，剔除破损果',18.00,30,'按果径65mm以上分级','周转筐',NULL,NULL,NULL,'PR2025040801',1,2300.00,'斤',NULL,'阴凉通风','分拣合格率92%','张三','/trace/process01.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45'),(3400000000000002,1300000000000001,'2025-04-08 11:00:00','包装','净菜包装，每箱5斤',15.00,20,'5斤/箱','瓦楞纸箱+珍珠棉网套',NULL,NULL,NULL,'PR2025040801',1,460.00,'箱',7,'冷藏2-8°C','包装后贴溯源标签','张三','/trace/process02.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45'),(3400000000000003,1300000000000002,'2025-06-10 08:00:00','分拣','荔枝分级：特级/一级/二级',22.00,45,'按果径和着色度分级','周转筐',NULL,NULL,NULL,'PR2025061001',1,2800.00,'斤',NULL,'冷库预冷','淘汰率约6%','李四','/trace/process03.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45'),(3400000000000004,1300000000000002,'2025-06-10 09:30:00','包装','荔枝保鲜包装，冰袋+泡沫箱',3.00,15,'3斤/箱','泡沫箱+冰袋+吸水纸',NULL,NULL,NULL,'PR2025061001',1,1000.00,'箱',3,'冷藏0-5°C','包装内放冰袋控温','李四','/trace/process04.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45'),(3400000000000005,1300000000000003,'2025-04-18 10:00:00','清洗','活鱼清水暂养吐泥',18.00,120,'2条/件','充氧袋+泡沫箱',NULL,NULL,NULL,'PR2025041801',1,250.00,'件',1,'活水充氧15-22°C','暂养12小时吐净泥沙后充氧包装','张三','/trace/process05.jpg','2026-06-29 14:26:45','2026-06-29 14:26:45');
/*!40000 ALTER TABLE `trace_processing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_scan_log`
--

DROP TABLE IF EXISTS `trace_scan_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_scan_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '关联溯源ID',
  `trace_code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '溯源码',
  `user_id` bigint(20) DEFAULT NULL COMMENT '扫码用户ID（可为空，游客扫码）',
  `scan_ip` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '扫码IP地址',
  `user_agent` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浏览器UA信息',
  `scan_location` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '扫码地点',
  `scan_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '扫码时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_scan_time` (`scan_time`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3100000000000006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='溯源扫码日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_scan_log`
--

LOCK TABLES `trace_scan_log` WRITE;
/*!40000 ALTER TABLE `trace_scan_log` DISABLE KEYS */;
INSERT INTO `trace_scan_log` VALUES (3100000000000001,1300000000000001,'TR202504010001',1000000000000005,'172.16.0.1','Mozilla/5.0 iPhone','湖北武汉','2025-04-11 10:00:00'),(3100000000000002,1300000000000001,'TR202504010001',1000000000000001,'192.168.1.1','Mozilla/5.0 Windows','湖北武汉','2025-04-11 14:30:00'),(3100000000000003,1300000000000002,'TR202504010002',NULL,'203.0.0.1','Mozilla/5.0 Android','广东深圳','2025-06-12 15:00:00'),(3100000000000004,1300000000000004,'TR202504010004',1000000000000005,'172.16.0.1','Mozilla/5.0 iPhone','湖北武汉','2025-04-23 12:00:00'),(3100000000000005,1300000000000005,'TR202504010005',NULL,'10.0.0.5','Mozilla/5.0 iPad','山东济南','2024-10-21 09:00:00');
/*!40000 ALTER TABLE `trace_scan_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trace_storage`
--

DROP TABLE IF EXISTS `trace_storage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trace_storage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` bigint(20) NOT NULL COMMENT '溯源ID',
  `warehouse_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '仓库名称',
  `warehouse_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '仓库地址',
  `storage_location` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '库位',
  `in_time` datetime DEFAULT NULL COMMENT '入库时间',
  `out_time` datetime DEFAULT NULL COMMENT '出库时间',
  `storage_status` tinyint(4) DEFAULT '1' COMMENT '存储状态（1在库2已出库）',
  `temperature` decimal(5,2) DEFAULT NULL COMMENT '储存温度',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '储存湿度',
  `quantity` decimal(10,2) DEFAULT NULL COMMENT '存储数量',
  `unit` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位',
  `keeper` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '保管员',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '存储说明',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '仓储图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_storage_status` (`storage_status`)
) ENGINE=InnoDB AUTO_INCREMENT=2100000000000006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓储记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trace_storage`
--

LOCK TABLES `trace_storage` WRITE;
/*!40000 ALTER TABLE `trace_storage` DISABLE KEYS */;
INSERT INTO `trace_storage` VALUES (2100000000000001,1300000000000001,'黄陂农场冷库A','武汉黄陂区前川街1号','A-01-01','2025-04-08 09:00:00','2025-04-10 07:30:00',2,5.00,85.00,2500.00,'斤','张三','预冷24小时后出库','/trace/stor01.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(2100000000000002,1300000000000002,'从化荔枝冷库B','广州从化区温泉路88号','B-02-03','2025-06-10 06:00:00','2025-06-12 04:30:00',2,2.00,90.00,3000.00,'斤','李四','预冷后按订单分拣出库','/trace/stor02.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(2100000000000003,1300000000000003,'吴江水产暂养池','苏州吴江区同里镇66号','C-01-01','2025-04-18 08:00:00','2025-04-19 05:30:00',2,18.00,NULL,500.00,'斤','张三','暂养池充氧保活','/trace/stor03.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(2100000000000004,1300000000000004,'黄陂农场冷库A','武汉黄陂区前川街1号','A-01-02','2025-04-22 08:00:00',NULL,1,5.00,85.00,5000.00,'斤','张三','入库预冷中','/trace/stor04.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02'),(2100000000000005,1300000000000005,'牟平苹果气调库','烟台牟平区龙泉路12号','D-03-01','2024-10-12 10:00:00','2024-10-20 05:00:00',2,1.00,90.00,15000.00,'斤','王五','气调储藏按订单出库','/trace/stor05.jpg','2026-06-29 12:51:02','2026-06-29 12:51:02');
/*!40000 ALTER TABLE `trace_storage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traceability`
--

DROP TABLE IF EXISTS `traceability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `traceability` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '溯源编号（唯一）',
  `product_id` bigint(20) DEFAULT '0',
  `farmer_id` bigint(20) NOT NULL COMMENT '农户ID',
  `batch_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '批次号',
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品名称（冗余）',
  `origin_place` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产地',
  `farm_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '农场名称',
  `responsible_person` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人',
  `responsible_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人电话',
  `planting_area` decimal(10,2) DEFAULT NULL COMMENT '种植面积（亩）',
  `seed_source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '种子来源',
  `planting_date` date DEFAULT NULL COMMENT '种植日期',
  `expected_harvest_date` date DEFAULT NULL COMMENT '预计采摘日期',
  `actual_harvest_date` date DEFAULT NULL COMMENT '实际采摘日期',
  `shelf_life` int(11) DEFAULT NULL COMMENT '保质期（天）',
  `storage_condition` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '储存条件',
  `total_nodes` int(11) DEFAULT '0' COMMENT '溯源节点总数',
  `current_stage` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前阶段（种植/采摘/质检/物流/仓储/销售）',
  `qrcode_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二维码图片URL',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态（0无效1有效）',
  `audit_status` tinyint(4) DEFAULT '0' COMMENT '审核状态（0待审核1通过2拒绝）',
  `scan_count` int(11) DEFAULT '0' COMMENT '累计扫码次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trace_code` (`trace_code`),
  UNIQUE KEY `uk_batch_no` (`batch_no`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_batch_no` (`batch_no`),
  KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB AUTO_INCREMENT=2075920577274134530 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='溯源主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traceability`
--

LOCK TABLES `traceability` WRITE;
/*!40000 ALTER TABLE `traceability` DISABLE KEYS */;
INSERT INTO `traceability` VALUES (1782894061809,'TR202607014127',1782894061841,6000000000000001,'B20250601','阳山水蜜桃','江苏无锡惠山区阳山镇','å¼ å¤§å†œæˆ·æœ‰æœºå†œåœº','å¼ ä¸‰','',0.00,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,1,1,0,'2026-07-01 16:21:01','2026-07-11 18:57:19',1),(1300000000000001,'TR202504010001',9000000000000001,6000000000000001,'B20250401','有机番茄','湖北武汉黄陂','张大农户有机农场','张三','13900000001',50.00,'武汉农科院优质番茄种子','2025-02-15','2025-04-10','2025-04-08',7,'冷藏2-8°C',8,'销售','/qrcode/TR202504010001.png',1,1,178,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(1300000000000002,'TR202504010002',9000000000000002,6000000000000002,'B20250402','桂味荔枝','广东广州从化','李大姐精品果园','李四','13900000002',100.00,'从化本地桂味母树嫁接苗','2024-03-20','2025-06-15','2025-06-10',3,'冷藏0-5°C',7,'物流','/qrcode/TR202504010002.png',1,1,90,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(1300000000000003,'TR202504010003',9000000000000004,6000000000000004,'B20250403','鲜活鲈鱼','江苏苏州吴江','张大农户水产基地','张三','13900000001',300.00,'吴江本地鲈鱼苗','2024-09-01','2025-04-20','2025-04-18',1,'活水充氧运输',6,'仓储','/qrcode/TR202504010003.png',1,1,45,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(1300000000000004,'TR202504010004',9000000000000005,6000000000000001,'B20250404','有机黄瓜','湖北武汉黄陂','张大农户有机农场','张三','13900000001',30.00,'山东寿光优质黄瓜种','2025-03-01','2025-04-25','2025-04-22',5,'冷藏2-8°C通风',7,'质检','/qrcode/TR202504010004.png',1,1,211,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(1300000000000005,'TR202504010005',9000000000000003,6000000000000003,'B20250405','烟台红富士苹果','山东烟台牟平','王五家庭农场','王五','13700000001',150.00,'烟台农科院红富士脱毒苗','2024-04-10','2024-10-15','2024-10-12',60,'冷藏0-4°C',5,'种植','/qrcode/TR202504010005.png',1,0,5,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2075830715879620610,'TR202607111070',0,6000000000000001,'TEST20260711','test product','test','test farm','zhang','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,1,1,0,'2026-07-11 14:33:04','2026-07-11 18:57:19',0);
/*!40000 ALTER TABLE `traceability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_address`
--

DROP TABLE IF EXISTS `user_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市',
  `district` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区县',
  `detail_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `postal_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮政编码',
  `is_default` tinyint(4) DEFAULT '0' COMMENT '是否默认地址（0否1是）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_default` (`is_default`)
) ENGINE=InnoDB AUTO_INCREMENT=2075829734504759299 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

LOCK TABLES `user_address` WRITE;
/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (1782874569828,1000000000000005,'新地址测试','13800009999','浙江','杭州','西湖区','文三路100号','310000',0,'2026-07-01 10:56:09','2026-07-01 10:56:09',0),(1782874761939,17828724173376014,'ymd','15810201456','北京','北京','石景山','金鼎街奶龙小区32号楼','',1,'2026-07-01 10:59:21','2026-07-01 11:35:35',0),(2700000000000001,1000000000000005,'王五','13700000001','湖北','武汉','洪山区','珞喻路1037号华科紫菘公寓12栋305','430074',1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2700000000000002,1000000000000005,'王五','13700000001','湖北','武汉','武昌区','中南路99号保利广场A座1501','430071',0,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2700000000000003,1000000000000001,'张管理','13800000001','湖北','武汉','武昌区','中南路99号','430071',1,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2700000000000004,1000000000000005,'王妈妈','13700000002','湖北','黄冈','黄州区','赤壁大道88号锦绣家园5栋601','438000',0,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2700000000000005,1000000000000001,'张管理','13800000001','北京','北京','海淀区','中关村大街1号海龙大厦8层','100080',0,'2026-06-29 12:51:02','2026-06-29 12:51:02',0),(2700000000000006,17828724173376014,'zqi','15810201666','北京','北京','石景山','奶龙街','',0,'2026-07-08 14:26:45','2026-07-08 14:26:45',0),(2075608813156249602,1000000000000005,'t','13800000000','t','t','t','t',NULL,0,'2026-07-10 23:51:18','2026-07-10 23:51:18',0),(2075827685432066050,1000000000000005,'t','13800000000','t','t','t','t',NULL,0,'2026-07-11 14:21:01','2026-07-11 14:21:01',0);
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_coupon`
--

DROP TABLE IF EXISTS `user_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_coupon` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
  `status` varchar(16) DEFAULT 'unused' COMMENT '状态: unused/used/expired',
  `order_id` bigint(20) DEFAULT NULL COMMENT '使用订单ID',
  `take_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_coupon`
--

LOCK TABLES `user_coupon` WRITE;
/*!40000 ALTER TABLE `user_coupon` DISABLE KEYS */;
INSERT INTO `user_coupon` VALUES (350543816475938816,350470571903033347,9100000000000001,'unused',NULL,'2026-07-09 13:49:18',NULL),(350543816551440384,350470571903033347,350215274890432512,'unused',NULL,'2026-07-09 13:49:18',NULL),(350913348889661440,1000000000000001,9100000000000001,'unused',NULL,'2026-07-10 14:17:42',NULL),(350917028875563008,1000000000000001,9100000000000002,'unused',NULL,'2026-07-10 14:32:19',NULL),(2075818518931668994,1000000000000005,9100000000000001,'unused',NULL,'2026-07-11 13:44:36',NULL),(2075827691702550529,1000000000000005,9100000000000003,'unused',NULL,'2026-07-11 14:21:03',NULL),(2075829741257588737,350470571903033361,9100000000000003,'unused',NULL,'2026-07-11 14:29:12',NULL);
/*!40000 ALTER TABLE `user_coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_footprint`
--

DROP TABLE IF EXISTS `user_footprint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_footprint` (
  `id` bigint(20) NOT NULL COMMENT '主键ID (Snowflake)',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `browse_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_browse_time` (`browse_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览足迹表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_footprint`
--

LOCK TABLES `user_footprint` WRITE;
/*!40000 ALTER TABLE `user_footprint` DISABLE KEYS */;
INSERT INTO `user_footprint` VALUES (2075573138121863170,1000000000000001,350480100011151360,'2026-07-10 21:29:33'),(2075603016867196929,1000000000000001,1782894061841,'2026-07-10 23:28:17'),(2075603027155824641,1000000000000001,9000000000000001,'2026-07-10 23:28:19'),(2075608815085629441,1000000000000005,9000000000000001,'2026-07-10 23:51:19'),(2075776137515589634,1000000000000001,9000000000000002,'2026-07-11 10:56:12'),(2075803595455401985,1000000000000005,9000000000000002,'2026-07-11 12:45:18'),(2075803622701600770,1000000000000005,9000000000000005,'2026-07-11 12:45:25'),(2075804719512096770,1000000000000005,9000000000000003,'2026-07-11 12:49:46'),(2075829739974131714,350470571903033361,9000000000000001,'2026-07-11 14:29:12'),(2075853963438678018,1000000000000001,9000000000000005,'2026-07-11 16:05:27'),(2075855033011277826,1000000000000003,9000000000000001,'2026-07-11 16:09:42'),(2075856594470965250,1000000000000003,9000000000000002,'2026-07-11 16:15:54'),(2075863252899287042,1000000000000003,1782894061841,'2026-07-11 16:42:22'),(2075863287359688706,1000000000000003,350480100011151360,'2026-07-11 16:42:30'),(2075868013509783554,1000000000000003,9000000000000005,'2026-07-11 17:01:17');
/*!40000 ALTER TABLE `user_footprint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'fresh_trace_shop'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-11 20:59:37

-- ============================================================
-- 新增表：AI客服 + 一物一码 + 地理位置
-- ============================================================

-- 聊天消息表（AI客服 + 人工客服）
CREATE TABLE IF NOT EXISTS `chat_message` (
  `id` bigint(20) NOT NULL,
  `from_user_id` bigint(20) NOT NULL COMMENT '发送者ID（0=AI）',
  `to_user_id` bigint(20) NOT NULL COMMENT '接收者ID',
  `content` text NOT NULL COMMENT '消息内容',
  `msg_type` varchar(20) DEFAULT 'text' COMMENT '消息类型: text/image/system/refund/exchange',
  `is_read` int(11) DEFAULT '0' COMMENT '0未读 1已读',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_from_user` (`from_user_id`),
  KEY `idx_to_user` (`to_user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 基础地理点位表
CREATE TABLE IF NOT EXISTS `sys_location` (
  `id` bigint(20) NOT NULL,
  `location_name` varchar(200) DEFAULT NULL COMMENT '点位名称',
  `location_type` varchar(50) DEFAULT 'farm' COMMENT '类型: farm/warehouse/processing/delivery',
  `farmer_id` bigint(20) DEFAULT NULL COMMENT '所属农户ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '所属用户ID',
  `address` varchar(500) DEFAULT NULL COMMENT '文字地址',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `province` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `district` varchar(50) DEFAULT NULL,
  `status` int(11) DEFAULT '1' COMMENT '0禁用 1启用',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_farmer_id` (`farmer_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基础地理点位表';

-- 商品QR码表（一物一码）
CREATE TABLE IF NOT EXISTS `product_qrcode` (
  `id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `trace_id` bigint(20) DEFAULT NULL COMMENT '关联溯源批次',
  `order_id` bigint(20) DEFAULT NULL,
  `qrcode_content` varchar(500) NOT NULL,
  `qrcode_image` varchar(500) DEFAULT NULL,
  `scan_count` int(11) DEFAULT '0',
  `first_scan_time` datetime DEFAULT NULL,
  `first_scan_ip` varchar(64) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content` (`qrcode_content`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- QR码扫描日志
CREATE TABLE IF NOT EXISTS `qrcode_scan_log` (
  `id` bigint(20) NOT NULL,
  `qrcode_id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `scan_ip` varchar(64) DEFAULT NULL,
  `scan_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_qrcode_id` (`qrcode_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 生长周期时间轴
CREATE TABLE IF NOT EXISTS `growth_timeline` (
  `id` bigint(20) NOT NULL,
  `trace_id` bigint(20) NOT NULL,
  `order_ids` varchar(500) DEFAULT NULL COMMENT '关联的预售订单ID列表',
  `stage` varchar(32) NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `content` text,
  `image_urls` varchar(2000) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AI客服配置（插入到 sys_config 表）
INSERT IGNORE INTO `sys_config` (`id`, `config_key`, `config_value`, `config_name`, `config_group`, `remark`) VALUES
(200, 'ai_api_url', 'https://api.xiaomimimo.com/v1/chat/completions', 'AI接口地址', 'ai', 'AI客服API地址'),
(201, 'ai_api_key', '', 'AI接口密钥', 'ai', 'AI客服API Key（需管理员配置）'),
(202, 'ai_model', 'mimo-v2.5-pro', 'AI模型名称', 'ai', 'AI客服模型名称'),
(100, 'qrcode_base_url', 'http://localhost:8088', '溯源码服务器地址', 'trace', '手机扫码后访问的服务器地址');

-- 农户表新增经纬度字段
ALTER TABLE `farmer` ADD COLUMN IF NOT EXISTS `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度';
ALTER TABLE `farmer` ADD COLUMN IF NOT EXISTS `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度';

-- 商品表新增预售字段
ALTER TABLE `product` ADD COLUMN IF NOT EXISTS `is_presale` int(11) DEFAULT 0 COMMENT '是否预售';
ALTER TABLE `product` ADD COLUMN IF NOT EXISTS `presale_start` date DEFAULT NULL COMMENT '预计种植日期';
ALTER TABLE `product` ADD COLUMN IF NOT EXISTS `presale_end` date DEFAULT NULL COMMENT '预计成熟日期';

-- 订单表新增预售和优惠券字段
ALTER TABLE `order_info` ADD COLUMN IF NOT EXISTS `presale_status` int(11) DEFAULT 0 COMMENT '预售状态: 0普通 1等待种植 2生长中 3已成熟';
ALTER TABLE `order_info` ADD COLUMN IF NOT EXISTS `trace_id` bigint(20) DEFAULT NULL COMMENT '关联溯源批次ID';

-- 用户表新增会员字段
ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `member_level` int(11) DEFAULT 0 COMMENT '会员等级';
ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `total_spent` decimal(10,2) DEFAULT 0.00 COMMENT '累计消费';
