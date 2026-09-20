/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.19 : Database - wy-vue
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`wy-vue` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

/*Table structure for table `biz_blacklist_record` */

DROP TABLE IF EXISTS `biz_blacklist_record`;

CREATE TABLE `biz_blacklist_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Record ID',
  `scope` varchar(32) NOT NULL DEFAULT 'all' COMMENT 'Search scope',
  `entity_type` varchar(32) NOT NULL COMMENT 'Entity type',
  `name` varchar(128) NOT NULL COMMENT 'Entity name',
  `id_no` varchar(64) NOT NULL DEFAULT '' COMMENT 'ID number',
  `country` varchar(64) NOT NULL DEFAULT '' COMMENT 'Country or region',
  `source` varchar(128) NOT NULL DEFAULT '' COMMENT 'Source',
  `list_name` varchar(128) NOT NULL DEFAULT '' COMMENT 'List name',
  `list_category` varchar(128) NOT NULL DEFAULT '' COMMENT 'List category',
  `publish_date` date DEFAULT NULL COMMENT 'Publish date',
  `effective_date` date DEFAULT NULL COMMENT 'Effective date',
  `expire_date` date DEFAULT NULL COMMENT 'Expire date',
  `risk_level` varchar(32) NOT NULL DEFAULT '' COMMENT 'Risk level',
  `tags` varchar(255) NOT NULL DEFAULT '' COMMENT 'Comma separated tags',
  `remarks` varchar(500) NOT NULL DEFAULT '' COMMENT 'Remarks',
  `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT 'Created by',
  `create_time` datetime DEFAULT NULL COMMENT 'Created time',
  `update_by` varchar(64) NOT NULL DEFAULT '' COMMENT 'Updated by',
  `update_time` datetime DEFAULT NULL COMMENT 'Updated time',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT 'Delete flag',
  PRIMARY KEY (`record_id`),
  KEY `idx_blacklist_scope` (`scope`),
  KEY `idx_blacklist_type` (`entity_type`),
  KEY `idx_blacklist_name` (`name`),
  KEY `idx_blacklist_id_no` (`id_no`),
  KEY `idx_blacklist_publish_date` (`publish_date`),
  KEY `idx_blacklist_risk_level` (`risk_level`)
) ENGINE=InnoDB AUTO_INCREMENT=10011 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Blacklist lookup record table';

/*Data for the table `biz_blacklist_record` */

insert  into `biz_blacklist_record`(`record_id`,`scope`,`entity_type`,`name`,`id_no`,`country`,`source`,`list_name`,`list_category`,`publish_date`,`effective_date`,`expire_date`,`risk_level`,`tags`,`remarks`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`) values 
(10001,'sanction','natural_person','Zhang San','CN1234567890','China','China MFA','China MFA Sanctions List','Sanction List','2024-09-15','2023-10-01',NULL,'High risk','Natural person,High risk','Sample person record','admin','2026-03-27 14:36:01','',NULL,'0'),
(10002,'sanction','natural_person','Manuel Pinto da Costa','PT9988776655','Portugal','United Nations','UN Consolidated Sanctions List','Sanction List','2024-06-05','2023-03-15',NULL,'High risk','Natural person,High risk','Sample person record','admin','2026-03-27 14:36:01','',NULL,'0'),
(10003,'sanction','enterprise','Oceanic Trade Ltd.','HK5566778899','Hong Kong','OFAC','OFAC SDN List','Sanction List','2024-05-20','2022-12-01',NULL,'High risk','Enterprise,High risk','Sample enterprise record','admin','2026-03-27 14:36:01','',NULL,'0'),
(10004,'highRisk','enterprise','ShuiDi Logistics Co., Ltd.','US4455667788','United States','China Customs','Export Control Watchlist','Watchlist','2023-11-11','2023-11-15',NULL,'Medium risk','Enterprise,Medium risk','Sample enterprise record','admin','2026-03-27 14:36:01','',NULL,'0'),
(10005,'sanction','vessel','Blue Horizon','IMO9312345','Malta','International Maritime','International Maritime Sanctions List','Sanction List','2023-08-08','2023-08-15',NULL,'High risk','Vessel,High risk','Sample vessel record','admin','2026-03-27 14:36:01','',NULL,'0'),
(10006,'highRisk','aircraft','Boeing 737-900','REG-B7379','United States','Aviation Safety','Aviation Safety Watchlist','Watchlist','2022-09-01','2022-09-10',NULL,'Medium risk','Aircraft,Medium risk','Sample aircraft record','admin','2026-03-27 14:36:01','',NULL,'0'),
(10007,'sanction','natural_person','Ivana Petrov','RU6677889900','Russia','European Union','EU Sanctions List','Sanction List','2024-01-03','2024-01-05',NULL,'High risk','Natural person,High risk','Sample person record','admin','2026-03-27 14:36:01','',NULL,'0'),
(10008,'sanction','enterprise','Northern Mining Group','CA1122334455','Canada','United Nations','UN Consolidated Sanctions List','Sanction List','2024-02-10','2024-02-12',NULL,'High risk','Enterprise,High risk','Sample enterprise record','admin','2026-03-27 14:36:01','',NULL,'0'),
(10009,'sanction','vessel','Red Sand','IMO9076543','Panama','International Maritime','International Maritime Sanctions List','Sanction List','2022-05-18','2022-05-20',NULL,'High risk','Vessel,High risk','Sample vessel record','admin','2026-03-27 14:36:01','',NULL,'0'),
(10010,'highRisk','aircraft','Airbus A320','REG-A320X','France','Aviation Safety','Aviation Safety Watchlist','Watchlist','2023-03-09','2023-03-12','2025-03-12','Low risk','Aircraft,Low risk','Sample aircraft record','admin','2026-03-27 14:36:01','',NULL,'0');

/*Table structure for table `gen_table` */

DROP TABLE IF EXISTS `gen_table`;

CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `form_col_num` int DEFAULT '1' COMMENT '表单布局（单列 双列 三列）',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';

/*Data for the table `gen_table` */

/*Table structure for table `gen_table_column` */

DROP TABLE IF EXISTS `gen_table_column`;

CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';

/*Data for the table `gen_table_column` */

/*Table structure for table `prop_building` */

DROP TABLE IF EXISTS `prop_building`;

CREATE TABLE `prop_building` (
  `building_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'building id',
  `building_name` varchar(80) NOT NULL COMMENT 'building name',
  `address` varchar(200) DEFAULT '' COMMENT 'address',
  `floors` int DEFAULT '0' COMMENT 'floors',
  `status` char(1) DEFAULT '0' COMMENT '0 normal 1 disabled',
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`building_id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='property building';

/*Data for the table `prop_building` */

insert  into `prop_building`(`building_id`,`building_name`,`address`,`floors`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(100,'1号楼','幸福社区东门北侧',18,'0','admin','2026-07-01 13:44:45','',NULL,NULL),
(101,'2号楼','幸福社区中心花园旁',24,'0','admin','2026-07-01 13:44:45','',NULL,NULL),
(102,'3号楼',NULL,15,'0','admin','2026-07-01 14:01:01','',NULL,NULL);

/*Table structure for table `prop_repair_category` */

DROP TABLE IF EXISTS `prop_repair_category`;

CREATE TABLE `prop_repair_category` (
  `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'category id',
  `category_name` varchar(80) NOT NULL COMMENT 'category name',
  `order_num` int DEFAULT '0' COMMENT 'sort order',
  `status` char(1) DEFAULT '0' COMMENT '0 normal 1 disabled',
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='repair category';

/*Data for the table `prop_repair_category` */

insert  into `prop_repair_category`(`category_id`,`category_name`,`order_num`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(100,'水电维修',1,'0','admin','2026-07-01 13:44:45','',NULL,NULL),
(101,'门窗维修',2,'0','admin','2026-07-01 13:44:45','',NULL,NULL),
(102,'公共设施',3,'0','admin','2026-07-01 13:44:45','',NULL,NULL),
(103,'其他',0,'0','property01','2026-07-01 15:16:44','',NULL,NULL);

/*Table structure for table `prop_repair_evaluation` */

DROP TABLE IF EXISTS `prop_repair_evaluation`;

CREATE TABLE `prop_repair_evaluation` (
  `evaluation_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'evaluation id',
  `order_id` bigint NOT NULL COMMENT 'order id',
  `order_no` varchar(40) NOT NULL COMMENT 'order no',
  `owner_id` bigint NOT NULL COMMENT 'owner user id',
  `repair_user_id` bigint DEFAULT NULL COMMENT 'repair user id',
  `score` int NOT NULL COMMENT '1-5 score',
  `content` varchar(500) DEFAULT '' COMMENT 'evaluation content',
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`evaluation_id`),
  UNIQUE KEY `uk_eval_order` (`order_id`),
  KEY `idx_eval_repair_user` (`repair_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='repair evaluation';

/*Data for the table `prop_repair_evaluation` */

/*Table structure for table `prop_repair_order` */

DROP TABLE IF EXISTS `prop_repair_order`;

CREATE TABLE `prop_repair_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'order id',
  `order_no` varchar(40) NOT NULL COMMENT 'order no',
  `owner_id` bigint NOT NULL COMMENT 'owner user id',
  `room_id` bigint DEFAULT NULL COMMENT 'room id',
  `category_id` bigint DEFAULT NULL COMMENT 'category id',
  `title` varchar(120) NOT NULL COMMENT 'repair title',
  `content` varchar(1000) DEFAULT '' COMMENT 'repair content',
  `images` varchar(1000) DEFAULT '' COMMENT 'repair images',
  `status` char(1) DEFAULT '0' COMMENT '0 pending 1 accepted 2 assigned 3 processing 4 waiting confirm 5 completed 6 rejected 7 canceled 8 rework',
  `repair_user_id` bigint DEFAULT NULL COMMENT 'repair user id',
  `reject_reason` varchar(500) DEFAULT '' COMMENT 'reject or rework reason',
  `finish_result` varchar(1000) DEFAULT '' COMMENT 'finish result',
  `finish_images` varchar(1000) DEFAULT '' COMMENT 'finish images',
  `accept_time` datetime DEFAULT NULL,
  `assign_time` datetime DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `confirm_time` datetime DEFAULT NULL,
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_repair_order_no` (`order_no`),
  KEY `idx_repair_status` (`status`),
  KEY `idx_repair_owner` (`owner_id`),
  KEY `idx_repair_user` (`repair_user_id`),
  KEY `idx_repair_category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='repair order';

/*Data for the table `prop_repair_order` */

insert  into `prop_repair_order`(`order_id`,`order_no`,`owner_id`,`room_id`,`category_id`,`title`,`content`,`images`,`status`,`repair_user_id`,`reject_reason`,`finish_result`,`finish_images`,`accept_time`,`assign_time`,`start_time`,`finish_time`,`confirm_time`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(100,'BX202607011453078540AC',100,602,1,'马桶坏了','马桶坏了',NULL,'1',NULL,'','','',NULL,NULL,NULL,NULL,NULL,'owner01','2026-07-01 14:53:07','','2026-07-01 15:16:58',NULL),
(101,'BX202607011515330E12ED',100,100,102,'马桶坏了','马桶坏了',NULL,'1',NULL,'','','',NULL,NULL,NULL,NULL,NULL,'owner01','2026-07-01 15:15:33','','2026-07-01 15:16:50',NULL),
(102,'BX2026070210461502D0F1',100,100,103,'得到','ddd',NULL,'5',102,'','已完成','',NULL,NULL,NULL,NULL,NULL,'owner01','2026-07-02 10:46:15','','2026-07-02 10:58:45',NULL),
(103,'BX20260702145629C21F9A',100,100,100,'马桶坏了','去 aa','/profile/upload/2026/07/02/5a5ec1fb0085e02609953924259aa32e_20260702145624A001.png,/profile/upload/2026/07/02/008ebmHfgy1i3yknbb2grj31bl2y9b29_20260702145627A002.jpg','0',NULL,'','','',NULL,NULL,NULL,NULL,NULL,'owner01','2026-07-02 14:56:29','',NULL,NULL);

/*Table structure for table `prop_room` */

DROP TABLE IF EXISTS `prop_room`;

CREATE TABLE `prop_room` (
  `room_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'room id',
  `building_id` bigint NOT NULL COMMENT 'building id',
  `unit_no` varchar(30) DEFAULT '' COMMENT 'unit no',
  `room_no` varchar(30) NOT NULL COMMENT 'room no',
  `owner_id` bigint DEFAULT NULL COMMENT 'sys_user owner id',
  `owner_name` varchar(64) DEFAULT '' COMMENT 'owner name',
  `status` char(1) DEFAULT '0' COMMENT '0 normal 1 disabled',
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`room_id`),
  KEY `idx_room_building` (`building_id`),
  KEY `idx_room_owner` (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='property room';

/*Data for the table `prop_room` */

insert  into `prop_room`(`room_id`,`building_id`,`unit_no`,`room_no`,`owner_id`,`owner_name`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(100,100,'1单元','101',100,'业主张三','0','admin','2026-07-01 13:44:45','',NULL,NULL);

/*Table structure for table `qrtz_blob_triggers` */

DROP TABLE IF EXISTS `qrtz_blob_triggers`;

CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Blob类型的触发器表';

/*Data for the table `qrtz_blob_triggers` */

/*Table structure for table `qrtz_calendars` */

DROP TABLE IF EXISTS `qrtz_calendars`;

CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日历信息表';

/*Data for the table `qrtz_calendars` */

/*Table structure for table `qrtz_cron_triggers` */

DROP TABLE IF EXISTS `qrtz_cron_triggers`;

CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cron类型的触发器表';

/*Data for the table `qrtz_cron_triggers` */

/*Table structure for table `qrtz_fired_triggers` */

DROP TABLE IF EXISTS `qrtz_fired_triggers`;

CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='已触发的触发器表';

/*Data for the table `qrtz_fired_triggers` */

/*Table structure for table `qrtz_job_details` */

DROP TABLE IF EXISTS `qrtz_job_details`;

CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务详细信息表';

/*Data for the table `qrtz_job_details` */

/*Table structure for table `qrtz_locks` */

DROP TABLE IF EXISTS `qrtz_locks`;

CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储的悲观锁信息表';

/*Data for the table `qrtz_locks` */

/*Table structure for table `qrtz_paused_trigger_grps` */

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;

CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='暂停的触发器表';

/*Data for the table `qrtz_paused_trigger_grps` */

/*Table structure for table `qrtz_scheduler_state` */

DROP TABLE IF EXISTS `qrtz_scheduler_state`;

CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调度器状态表';

/*Data for the table `qrtz_scheduler_state` */

/*Table structure for table `qrtz_simple_triggers` */

DROP TABLE IF EXISTS `qrtz_simple_triggers`;

CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简单触发器的信息表';

/*Data for the table `qrtz_simple_triggers` */

/*Table structure for table `qrtz_simprop_triggers` */

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;

CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='同步机制的行锁表';

/*Data for the table `qrtz_simprop_triggers` */

/*Table structure for table `qrtz_triggers` */

DROP TABLE IF EXISTS `qrtz_triggers`;

CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='触发器详细信息表';

/*Data for the table `qrtz_triggers` */

/*Table structure for table `sys_config` */

DROP TABLE IF EXISTS `sys_config`;

CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';

/*Data for the table `sys_config` */

insert  into `sys_config`(`config_id`,`config_name`,`config_key`,`config_value`,`config_type`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2026-07-01 13:44:22','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),
(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2026-07-01 13:44:22','',NULL,'初始化密码 123456'),
(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2026-07-01 13:44:22','',NULL,'深色主题theme-dark，浅色主题theme-light'),
(4,'账号自助-验证码开关','sys.account.captchaEnabled','true','Y','admin','2026-07-01 13:44:22','',NULL,'是否开启验证码功能（true开启，false关闭）'),
(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','true','Y','admin','2026-07-01 13:44:22','',NULL,'是否开启注册用户功能（true开启，false关闭）'),
(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2026-07-01 13:44:22','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),
(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2026-07-01 13:44:22','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),
(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2026-07-01 13:44:22','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框'),
(9,'用户管理-密码字符范围','sys.account.chrtype','0','Y','admin','2026-07-01 13:44:22','',NULL,'默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数字和特殊字符（目前支持的特殊字符包括：~!@#$%^&*()-=_+）');

/*Table structure for table `sys_dept` */

DROP TABLE IF EXISTS `sys_dept`;

CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';

/*Data for the table `sys_dept` */

insert  into `sys_dept`(`dept_id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`) values 
(100,0,'0','幸福社区物业服务中心',0,'赵经理','13800000010','service@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33'),
(101,100,'0,100','客服与受理中心',1,'李主管','13800000011','service@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33'),
(102,100,'0,100','维修服务中心',2,'王主管','13800000012','repair@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33'),
(103,101,'0,100,101','报修受理组',1,'陈客服','13800000013','service@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33'),
(104,101,'0,100,101','工单调度组',2,'刘调度','13800000014','dispatch@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33'),
(105,101,'0,100,101','业主服务组',3,'张客服','13800000015','owner@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33'),
(106,102,'0,100,102','水电维修组',1,'周师傅','13800000016','water@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33'),
(107,102,'0,100,102','公共设施维修组',2,'王师傅','13800000017','facility@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33'),
(108,102,'0,100,102','门窗维修组',3,'吴师傅','13800000018','door@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33'),
(109,102,'0,100,102','环境巡查组',4,'孙巡查','13800000019','patrol@property.local','0','0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33');

/*Table structure for table `sys_dict_data` */

DROP TABLE IF EXISTS `sys_dict_data`;

CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=209 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';

/*Data for the table `sys_dict_data` */

insert  into `sys_dict_data`(`dict_code`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,1,'男','0','sys_user_sex','','','Y','0','admin','2026-07-01 13:44:22','',NULL,'性别男'),
(2,2,'女','1','sys_user_sex','','','N','0','admin','2026-07-01 13:44:22','',NULL,'性别女'),
(3,3,'未知','2','sys_user_sex','','','N','0','admin','2026-07-01 13:44:22','',NULL,'性别未知'),
(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2026-07-01 13:44:22','',NULL,'显示菜单'),
(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2026-07-01 13:44:22','',NULL,'隐藏菜单'),
(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2026-07-01 13:44:22','',NULL,'正常状态'),
(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2026-07-01 13:44:22','',NULL,'停用状态'),
(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2026-07-01 13:44:22','',NULL,'正常状态'),
(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2026-07-01 13:44:22','',NULL,'停用状态'),
(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2026-07-01 13:44:22','',NULL,'默认分组'),
(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2026-07-01 13:44:22','',NULL,'系统分组'),
(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2026-07-01 13:44:22','',NULL,'系统默认是'),
(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2026-07-01 13:44:22','',NULL,'系统默认否'),
(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2026-07-01 13:44:22','',NULL,'通知'),
(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2026-07-01 13:44:22','',NULL,'公告'),
(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2026-07-01 13:44:22','',NULL,'正常状态'),
(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2026-07-01 13:44:22','',NULL,'关闭状态'),
(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2026-07-01 13:44:22','',NULL,'其他操作'),
(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2026-07-01 13:44:22','',NULL,'新增操作'),
(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2026-07-01 13:44:22','',NULL,'修改操作'),
(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2026-07-01 13:44:22','',NULL,'删除操作'),
(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2026-07-01 13:44:22','',NULL,'授权操作'),
(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2026-07-01 13:44:22','',NULL,'导出操作'),
(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2026-07-01 13:44:22','',NULL,'导入操作'),
(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2026-07-01 13:44:22','',NULL,'强退操作'),
(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2026-07-01 13:44:22','',NULL,'生成操作'),
(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2026-07-01 13:44:22','',NULL,'清空操作'),
(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2026-07-01 13:44:22','',NULL,'正常状态'),
(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2026-07-01 13:44:22','',NULL,'停用状态'),
(200,0,'待受理','0','prop_repair_status','','primary','N','0','admin','2026-07-01 13:44:45','',NULL,NULL),
(201,1,'已受理','1','prop_repair_status','','info','N','0','admin','2026-07-01 13:44:45','',NULL,NULL),
(202,2,'已分配','2','prop_repair_status','','warning','N','0','admin','2026-07-01 13:44:45','',NULL,NULL),
(203,3,'处理中','3','prop_repair_status','','warning','N','0','admin','2026-07-01 13:44:45','',NULL,NULL),
(204,4,'待确认','4','prop_repair_status','','primary','N','0','admin','2026-07-01 13:44:45','',NULL,NULL),
(205,5,'已完成','5','prop_repair_status','','success','N','0','admin','2026-07-01 13:44:45','',NULL,NULL),
(206,6,'已驳回','6','prop_repair_status','','danger','N','0','admin','2026-07-01 13:44:45','',NULL,NULL),
(207,7,'已取消','7','prop_repair_status','','info','N','0','admin','2026-07-01 13:44:45','',NULL,NULL),
(208,8,'返工中','8','prop_repair_status','','danger','N','0','admin','2026-07-01 13:44:45','',NULL,NULL);

/*Table structure for table `sys_dict_type` */

DROP TABLE IF EXISTS `sys_dict_type`;

CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';

/*Data for the table `sys_dict_type` */

insert  into `sys_dict_type`(`dict_id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'用户性别','sys_user_sex','0','admin','2026-07-01 13:44:22','',NULL,'用户性别列表'),
(2,'菜单状态','sys_show_hide','0','admin','2026-07-01 13:44:22','',NULL,'菜单状态列表'),
(3,'系统开关','sys_normal_disable','0','admin','2026-07-01 13:44:22','',NULL,'系统开关列表'),
(4,'任务状态','sys_job_status','0','admin','2026-07-01 13:44:22','',NULL,'任务状态列表'),
(5,'任务分组','sys_job_group','0','admin','2026-07-01 13:44:22','',NULL,'任务分组列表'),
(6,'系统是否','sys_yes_no','0','admin','2026-07-01 13:44:22','',NULL,'系统是否列表'),
(7,'通知类型','sys_notice_type','0','admin','2026-07-01 13:44:22','',NULL,'通知类型列表'),
(8,'通知状态','sys_notice_status','0','admin','2026-07-01 13:44:22','',NULL,'通知状态列表'),
(9,'操作类型','sys_oper_type','0','admin','2026-07-01 13:44:22','',NULL,'操作类型列表'),
(10,'系统状态','sys_common_status','0','admin','2026-07-01 13:44:22','',NULL,'登录状态列表'),
(200,'报修状态','prop_repair_status','0','admin','2026-07-01 13:44:45','',NULL,'repair workflow status');

/*Table structure for table `sys_job` */

DROP TABLE IF EXISTS `sys_job`;

CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';

/*Data for the table `sys_job` */

insert  into `sys_job`(`job_id`,`job_name`,`job_group`,`invoke_target`,`cron_expression`,`misfire_policy`,`concurrent`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2026-07-01 13:44:23','',NULL,''),
(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2026-07-01 13:44:23','',NULL,''),
(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2026-07-01 13:44:23','',NULL,'');

/*Table structure for table `sys_job_log` */

DROP TABLE IF EXISTS `sys_job_log`;

CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';

/*Data for the table `sys_job_log` */

/*Table structure for table `sys_logininfor` */

DROP TABLE IF EXISTS `sys_logininfor`;

CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';

/*Data for the table `sys_logininfor` */

insert  into `sys_logininfor`(`info_id`,`user_name`,`ipaddr`,`login_location`,`browser`,`os`,`status`,`msg`,`login_time`) values 
(100,'admin','127.0.0.1','内网IP','Chrome 144','Windows10','0','登录成功','2026-07-01 14:00:03'),
(101,'admin','127.0.0.1','内网IP','Chrome 144','Windows10','0','退出成功','2026-07-01 14:29:34'),
(102,'admin','127.0.0.1','内网IP','Chrome 144','Windows10','0','登录成功','2026-07-01 14:43:25'),
(103,'owner01','127.0.0.1','内网IP','Chrome 144','Windows10','1','验证码已失效','2026-07-01 14:52:08'),
(104,'owner01','127.0.0.1','内网IP','Chrome 144','Windows10','0','登录成功','2026-07-01 14:52:13'),
(105,'owner01','127.0.0.1','内网IP','Chrome 144','Windows10','0','退出成功','2026-07-01 15:05:33'),
(106,'owner01','127.0.0.1','内网IP','Chrome 144','Windows10','0','登录成功','2026-07-01 15:05:46'),
(107,'owner01','127.0.0.1','内网IP','Chrome 144','Windows10','0','退出成功','2026-07-01 15:15:45'),
(108,'property01','127.0.0.1','内网IP','Chrome 144','Windows10','0','登录成功','2026-07-01 15:15:53'),
(109,'admin','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 10:44:41'),
(110,'admin','127.0.0.1','内网IP','Chrome 149','Windows10','0','退出成功','2026-07-02 10:45:52'),
(111,'owner01','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 10:45:58'),
(112,'owner01','127.0.0.1','内网IP','Chrome 149','Windows10','0','退出成功','2026-07-02 10:47:48'),
(113,'property01','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 10:47:58'),
(114,'property01','127.0.0.1','内网IP','Chrome 149','Windows10','0','退出成功','2026-07-02 10:48:35'),
(115,'repair01','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 10:48:57'),
(116,'repair01','127.0.0.1','内网IP','Chrome 149','Windows10','0','退出成功','2026-07-02 10:58:14'),
(117,'owner01','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 10:58:31'),
(118,'admin','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 14:07:03'),
(119,'admin','127.0.0.1','内网IP','Chrome 149','Windows10','0','退出成功','2026-07-02 14:49:18'),
(120,'admin','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 14:49:23'),
(121,'admin','127.0.0.1','内网IP','Chrome 149','Windows10','0','退出成功','2026-07-02 14:49:49'),
(122,'owner01','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 14:50:20'),
(123,'owner01','127.0.0.1','内网IP','Chrome 149','Windows10','0','退出成功','2026-07-02 15:02:22'),
(124,'admin','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 15:02:41'),
(125,'admin','127.0.0.1','内网IP','Chrome 149','Windows10','0','退出成功','2026-07-02 15:12:52'),
(126,'admin','127.0.0.1','内网IP','Chrome 149','Windows10','0','登录成功','2026-07-02 15:12:54');

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2064 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';

/*Data for the table `sys_menu` */

insert  into `sys_menu`(`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'系统管理',0,1,'system',NULL,'','',1,0,'M','0','0','','system','admin','2026-07-01 13:44:20','',NULL,'系统管理目录'),
(2,'系统监控',0,2,'monitor',NULL,'','',1,0,'M','0','0','','monitor','admin','2026-07-01 13:44:20','',NULL,'系统监控目录'),
(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2026-07-01 13:44:20','',NULL,'用户管理菜单'),
(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2026-07-01 13:44:20','',NULL,'角色管理菜单'),
(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','admin','2026-07-01 13:44:20','',NULL,'菜单管理菜单'),
(103,'小区组织',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2026-07-01 13:44:20','admin','2026-07-02 14:17:00','小区组织菜单'),
(104,'服务岗位',1,5,'post','system/post/index','','',1,0,'C','0','0','system:post:list','post','admin','2026-07-01 13:44:20','admin','2026-07-02 14:17:00','服务岗位菜单'),
(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','admin','2026-07-01 13:44:21','',NULL,'字典管理菜单'),
(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','0','0','system:config:list','edit','admin','2026-07-01 13:44:21','',NULL,'参数设置菜单'),
(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','0','0','system:notice:list','message','admin','2026-07-01 13:44:21','',NULL,'通知公告菜单'),
(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2026-07-01 13:44:21','',NULL,'日志管理菜单'),
(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2026-07-01 13:44:21','',NULL,'在线用户菜单'),
(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2026-07-01 13:44:21','',NULL,'操作日志菜单'),
(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2026-07-01 13:44:21','',NULL,'登录日志菜单'),
(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2026-07-01 13:44:21','',NULL,''),
(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2026-07-01 13:44:21','',NULL,''),
(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2026-07-01 13:44:21','',NULL,''),
(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2026-07-01 13:44:21','',NULL,''),
(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2026-07-01 13:44:21','',NULL,''),
(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2026-07-01 13:44:21','',NULL,''),
(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2026-07-01 13:44:21','',NULL,''),
(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2026-07-01 13:44:21','',NULL,''),
(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2026-07-01 13:44:21','',NULL,''),
(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2026-07-01 13:44:21','',NULL,''),
(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2026-07-01 13:44:21','',NULL,''),
(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2026-07-01 13:44:21','',NULL,''),
(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2026-07-01 13:44:21','',NULL,''),
(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2026-07-01 13:44:21','',NULL,''),
(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2026-07-01 13:44:21','',NULL,''),
(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2026-07-01 13:44:21','',NULL,''),
(1016,'组织查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2026-07-01 13:44:21','admin','2026-07-02 14:17:00',''),
(1017,'组织新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2026-07-01 13:44:21','admin','2026-07-02 14:17:00',''),
(1018,'组织修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2026-07-01 13:44:21','admin','2026-07-02 14:17:00',''),
(1019,'组织删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2026-07-01 13:44:21','admin','2026-07-02 14:17:00',''),
(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2026-07-01 13:44:21','admin','2026-07-02 14:17:00',''),
(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2026-07-01 13:44:21','admin','2026-07-02 14:17:00',''),
(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2026-07-01 13:44:21','admin','2026-07-02 14:17:00',''),
(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2026-07-01 13:44:21','admin','2026-07-02 14:17:00',''),
(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2026-07-01 13:44:21','admin','2026-07-02 14:17:00',''),
(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2026-07-01 13:44:21','',NULL,''),
(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2026-07-01 13:44:21','',NULL,''),
(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2026-07-01 13:44:21','',NULL,''),
(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2026-07-01 13:44:21','',NULL,''),
(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2026-07-01 13:44:21','',NULL,''),
(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2026-07-01 13:44:21','',NULL,''),
(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2026-07-01 13:44:21','',NULL,''),
(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2026-07-01 13:44:21','',NULL,''),
(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2026-07-01 13:44:21','',NULL,''),
(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2026-07-01 13:44:21','',NULL,''),
(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2026-07-01 13:44:21','',NULL,''),
(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2026-07-01 13:44:21','',NULL,''),
(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2026-07-01 13:44:21','',NULL,''),
(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2026-07-01 13:44:21','',NULL,''),
(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2026-07-01 13:44:21','',NULL,''),
(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2026-07-01 13:44:21','',NULL,''),
(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2026-07-01 13:44:21','',NULL,''),
(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2026-07-01 13:44:21','',NULL,''),
(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2026-07-01 13:44:21','',NULL,''),
(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2026-07-01 13:44:21','',NULL,''),
(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2026-07-01 13:44:21','',NULL,''),
(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2026-07-01 13:44:21','',NULL,''),
(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2026-07-01 13:44:21','',NULL,''),
(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2026-07-01 13:44:21','',NULL,''),
(2000,'物业报修',0,4,'property',NULL,'','',1,0,'M','0','0','','build','admin','2026-07-01 13:44:45','',NULL,'property repair root'),
(2001,'首页看板',2000,1,'dashboard','property/dashboard/index','','',1,0,'C','0','0','property:dashboard:list','dashboard','admin','2026-07-01 13:44:45','',NULL,''),
(2002,'楼栋管理',2000,2,'building','property/building/index','','',1,0,'C','0','0','property:building:list','tree','admin','2026-07-01 13:44:45','',NULL,''),
(2003,'房屋管理',2000,3,'room','property/room/index','','',1,0,'C','0','0','property:room:list','nested','admin','2026-07-01 13:44:45','',NULL,''),
(2004,'报修分类',2000,4,'category','property/category/index','','',1,0,'C','0','0','property:category:list','dict','admin','2026-07-01 13:44:45','',NULL,''),
(2005,'报修工单',2000,5,'order','property/order/index','','',1,0,'C','0','0','property:order:list','form','admin','2026-07-01 13:44:45','',NULL,''),
(2006,'服务评价',2000,6,'evaluation','property/evaluation/index','','',1,0,'C','0','0','property:evaluation:list','rate','admin','2026-07-01 13:44:45','',NULL,''),
(2011,'新增楼栋',2002,1,'','','','',1,0,'F','0','0','property:building:add','#','admin','2026-07-01 13:44:45','',NULL,''),
(2012,'修改楼栋',2002,2,'','','','',1,0,'F','0','0','property:building:edit','#','admin','2026-07-01 13:44:45','',NULL,''),
(2013,'删除楼栋',2002,3,'','','','',1,0,'F','0','0','property:building:remove','#','admin','2026-07-01 13:44:45','',NULL,''),
(2021,'新增房屋',2003,1,'','','','',1,0,'F','0','0','property:room:add','#','admin','2026-07-01 13:44:45','',NULL,''),
(2022,'修改房屋',2003,2,'','','','',1,0,'F','0','0','property:room:edit','#','admin','2026-07-01 13:44:45','',NULL,''),
(2023,'删除房屋',2003,3,'','','','',1,0,'F','0','0','property:room:remove','#','admin','2026-07-01 13:44:45','',NULL,''),
(2031,'新增分类',2004,1,'','','','',1,0,'F','0','0','property:category:add','#','admin','2026-07-01 13:44:45','',NULL,''),
(2032,'修改分类',2004,2,'','','','',1,0,'F','0','0','property:category:edit','#','admin','2026-07-01 13:44:45','',NULL,''),
(2033,'删除分类',2004,3,'','','','',1,0,'F','0','0','property:category:remove','#','admin','2026-07-01 13:44:45','',NULL,''),
(2040,'查询报修',2005,0,'','','','',1,0,'F','0','0','property:order:query','#','admin','2026-07-02 15:02:18','',NULL,''),
(2041,'新增报修',2005,1,'','','','',1,0,'F','0','0','property:order:add','#','admin','2026-07-01 13:44:45','',NULL,''),
(2042,'修改报修',2005,2,'','','','',1,0,'F','0','0','property:order:edit','#','admin','2026-07-01 13:44:45','',NULL,''),
(2043,'删除报修',2005,3,'','','','',1,0,'F','0','0','property:order:remove','#','admin','2026-07-01 13:44:45','',NULL,''),
(2044,'受理工单',2005,4,'','','','',1,0,'F','0','0','property:order:accept','#','admin','2026-07-01 13:44:45','',NULL,''),
(2045,'驳回工单',2005,5,'','','','',1,0,'F','0','0','property:order:reject','#','admin','2026-07-01 13:44:45','',NULL,''),
(2046,'分配工单',2005,6,'','','','',1,0,'F','0','0','property:order:assign','#','admin','2026-07-01 13:44:45','',NULL,''),
(2047,'开始维修',2005,7,'','','','',1,0,'F','0','0','property:order:start','#','admin','2026-07-01 13:44:45','',NULL,''),
(2048,'完成维修',2005,8,'','','','',1,0,'F','0','0','property:order:finish','#','admin','2026-07-01 13:44:45','',NULL,''),
(2049,'确认完成',2005,9,'','','','',1,0,'F','0','0','property:order:confirm','#','admin','2026-07-01 13:44:45','',NULL,''),
(2050,'取消报修',2005,10,'','','','',1,0,'F','0','0','property:order:cancel','#','admin','2026-07-01 13:44:45','',NULL,''),
(2051,'返工处理',2005,11,'','','','',1,0,'F','0','0','property:order:rework','#','admin','2026-07-01 13:44:45','',NULL,''),
(2061,'新增评价',2006,1,'','','','',1,0,'F','0','0','property:evaluation:add','#','admin','2026-07-01 13:44:45','',NULL,''),
(2062,'修改评价',2006,2,'','','','',1,0,'F','0','0','property:evaluation:edit','#','admin','2026-07-01 13:44:45','',NULL,''),
(2063,'删除评价',2006,3,'','','','',1,0,'F','0','0','property:evaluation:remove','#','admin','2026-07-01 13:44:45','',NULL,'');

/*Table structure for table `sys_notice` */

DROP TABLE IF EXISTS `sys_notice`;

CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';

/*Data for the table `sys_notice` */

insert  into `sys_notice`(`notice_id`,`notice_title`,`notice_type`,`notice_content`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'温馨提醒：请保持楼道与消防通道畅通','2','<p>为保障小区公共安全，请各位业主不要在楼道、电梯前室、消防通道停放电动车或堆放杂物。</p><p>物业服务中心将持续开展巡查，如发现安全隐患会及时联系业主协助整改。感谢大家共同维护整洁、安全的居住环境。</p>','0','admin','2026-07-02 14:43:49','',NULL,'物业服务中心'),
(2,'维护通知：本周六进行小区水泵房例行检修','1','<p>为保证供水设备稳定运行，物业维修组计划于本周六 09:00-11:00 对水泵房进行例行巡检和保养。</p><p>检修期间高楼层可能出现短时水压波动，请住户提前做好用水安排。如遇异常情况，可通过物业报修系统提交工单。</p>','0','admin','2026-07-02 14:43:49','',NULL,'物业服务中心'),
(3,'报修指南：线上提交工单处理更及时','1','<p>住户遇到公共设施、门窗、水电、环境等问题时，可在“物业报修 - 报修工单”中提交报修。</p><p>提交时请尽量选择房屋与报修分类，并描述现场情况；如有照片，可上传现场图片，便于物业管理员快速受理并分配维修人员。</p><p>维修完成后，请在系统中确认完成并进行服务评价，帮助物业持续改进服务质量。</p>','0','admin','2026-07-02 14:43:49','',NULL,'物业服务中心');

/*Table structure for table `sys_notice_read` */

DROP TABLE IF EXISTS `sys_notice_read`;

CREATE TABLE `sys_notice_read` (
  `read_id` bigint NOT NULL AUTO_INCREMENT COMMENT '已读主键',
  `notice_id` int NOT NULL COMMENT '公告id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `read_time` datetime NOT NULL COMMENT '阅读时间',
  PRIMARY KEY (`read_id`),
  UNIQUE KEY `uk_user_notice` (`user_id`,`notice_id`) COMMENT '同一用户同一公告只记录一次'
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告已读记录表';

/*Data for the table `sys_notice_read` */

insert  into `sys_notice_read`(`read_id`,`notice_id`,`user_id`,`read_time`) values 
(1,3,1,'2026-07-02 14:27:30'),
(2,2,1,'2026-07-02 14:36:22'),
(4,1,1,'2026-07-02 14:44:41'),
(12,3,100,'2026-07-02 14:50:21'),
(13,2,100,'2026-07-02 14:50:27'),
(14,1,100,'2026-07-02 14:50:28');

/*Table structure for table `sys_oper_log` */

DROP TABLE IF EXISTS `sys_oper_log`;

CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';

/*Data for the table `sys_oper_log` */

insert  into `sys_oper_log`(`oper_id`,`title`,`business_type`,`method`,`request_method`,`operator_type`,`oper_name`,`dept_name`,`oper_url`,`oper_ip`,`oper_location`,`oper_param`,`json_result`,`status`,`error_msg`,`oper_time`,`cost_time`) values 
(100,'property building',1,'com.wuye.web.controller.property.PropBuildingController.add()','POST',1,'admin','研发部门','/property/building','127.0.0.1','内网IP','{\"buildingId\":102,\"buildingName\":\"3号楼\",\"createBy\":\"admin\",\"floors\":15,\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-07-01 14:01:01',50),
(101,'菜单管理',3,'com.wuye.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/4','127.0.0.1','内网IP','4 ','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-07-01 14:06:19',19),
(102,'repair order',1,'com.wuye.web.controller.property.PropRepairOrderController.add()','POST',1,'owner01','测试部门','/property/order','127.0.0.1','内网IP','{\"categoryId\":1,\"content\":\"马桶坏了\",\"createBy\":\"owner01\",\"orderId\":100,\"orderNo\":\"BX202607011453078540AC\",\"ownerId\":100,\"ownerName\":\"owner01\",\"params\":{},\"roomId\":602,\"status\":\"0\",\"title\":\"马桶坏了\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-07-01 14:53:07',42),
(103,'repair order',1,'com.wuye.web.controller.property.PropRepairOrderController.add()','POST',1,'owner01','测试部门','/property/order','127.0.0.1','内网IP','{\"categoryId\":102,\"content\":\"马桶坏了\",\"createBy\":\"owner01\",\"orderId\":101,\"orderNo\":\"BX202607011515330E12ED\",\"ownerId\":100,\"ownerName\":\"owner01\",\"params\":{},\"roomId\":100,\"status\":\"0\",\"title\":\"马桶坏了\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-07-01 15:15:34',484),
(104,'repair category',1,'com.wuye.web.controller.property.PropRepairCategoryController.add()','POST',1,'property01','研发部门','/property/category','127.0.0.1','内网IP','{\"categoryId\":103,\"categoryName\":\"其他\",\"createBy\":\"property01\",\"orderNum\":0,\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-07-01 15:16:44',22),
(105,'repair order',1,'com.wuye.web.controller.property.PropRepairOrderController.add()','POST',1,'owner01','测试部门','/property/order','127.0.0.1','内网IP','{\"categoryId\":103,\"content\":\"ddd\",\"createBy\":\"owner01\",\"orderId\":102,\"orderNo\":\"BX2026070210461502D0F1\",\"ownerId\":100,\"ownerName\":\"owner01\",\"params\":{},\"roomId\":100,\"status\":\"0\",\"title\":\"得到\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-07-02 10:46:15',45),
(106,'用户头像',2,'com.wuye.web.controller.system.SysProfileController.avatar()','POST',1,'owner01','测试部门','/system/user/profile/avatar','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2026/07/02/8a611bbfc1ad45f5ac17bdaebe57fb46.png\",\"code\":200}',0,NULL,'2026-07-02 11:26:42',229),
(107,'repair order',1,'com.wuye.web.controller.property.PropRepairOrderController.add()','POST',1,'owner01','业主服务组','/property/order','127.0.0.1','内网IP','{\"categoryId\":100,\"content\":\"去 aa\",\"createBy\":\"owner01\",\"images\":\"/profile/upload/2026/07/02/5a5ec1fb0085e02609953924259aa32e_20260702145624A001.png,/profile/upload/2026/07/02/008ebmHfgy1i3yknbb2grj31bl2y9b29_20260702145627A002.jpg\",\"orderId\":103,\"orderNo\":\"BX20260702145629C21F9A\",\"ownerId\":100,\"ownerName\":\"owner01\",\"params\":{},\"roomId\":100,\"status\":\"0\",\"title\":\"马桶坏了\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-07-02 14:56:29',87);

/*Table structure for table `sys_post` */

DROP TABLE IF EXISTS `sys_post`;

CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';

/*Data for the table `sys_post` */

insert  into `sys_post`(`post_id`,`post_code`,`post_name`,`post_sort`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'property_manager','物业管理员',1,'0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33',''),
(2,'dispatcher','工单调度员',2,'0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33',''),
(3,'repair_worker','维修人员',3,'0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33',''),
(4,'customer_service','客服专员',4,'0','admin','2026-07-01 13:44:20','admin','2026-07-02 14:22:33','');

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';

/*Data for the table `sys_role` */

insert  into `sys_role`(`role_id`,`role_name`,`role_key`,`role_sort`,`data_scope`,`menu_check_strictly`,`dept_check_strictly`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2026-07-01 13:44:20','',NULL,'超级管理员'),
(2,'普通角色','common',2,'2',1,1,'0','0','admin','2026-07-01 13:44:20','',NULL,'普通角色'),
(100,'业主/住户','property_owner',10,'2',1,1,'0','0','admin','2026-07-01 14:48:48','',NULL,'提交报修、查看进度、确认完成、评价维修'),
(101,'物业管理员','property_manager',11,'1',1,1,'0','0','admin','2026-07-01 14:48:48','',NULL,'审核报修、分配维修人员、跟踪工单、查看统计'),
(102,'维修人员','repair_worker',12,'2',1,1,'0','0','admin','2026-07-01 14:48:48','',NULL,'接单、处理报修、上传维修结果'),
(103,'系统管理员','system_admin',13,'1',1,1,'0','0','admin','2026-07-01 14:48:48','',NULL,'管理用户、角色、权限、小区信息、公告等基础数据');

/*Table structure for table `sys_role_dept` */

DROP TABLE IF EXISTS `sys_role_dept`;

CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';

/*Data for the table `sys_role_dept` */

insert  into `sys_role_dept`(`role_id`,`dept_id`) values 
(2,100),
(2,101),
(2,105);

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';

/*Data for the table `sys_role_menu` */

insert  into `sys_role_menu`(`role_id`,`menu_id`) values 
(1,2000),
(1,2001),
(1,2002),
(1,2003),
(1,2004),
(1,2005),
(1,2006),
(1,2011),
(1,2012),
(1,2013),
(1,2021),
(1,2022),
(1,2023),
(1,2031),
(1,2032),
(1,2033),
(1,2040),
(1,2041),
(1,2042),
(1,2043),
(1,2044),
(1,2045),
(1,2046),
(1,2047),
(1,2048),
(1,2049),
(1,2050),
(1,2051),
(1,2061),
(1,2062),
(1,2063),
(2,1),
(2,2),
(2,100),
(2,101),
(2,102),
(2,103),
(2,104),
(2,105),
(2,106),
(2,107),
(2,108),
(2,109),
(2,500),
(2,501),
(2,1000),
(2,1001),
(2,1002),
(2,1003),
(2,1004),
(2,1005),
(2,1006),
(2,1007),
(2,1008),
(2,1009),
(2,1010),
(2,1011),
(2,1012),
(2,1013),
(2,1014),
(2,1015),
(2,1016),
(2,1017),
(2,1018),
(2,1019),
(2,1020),
(2,1021),
(2,1022),
(2,1023),
(2,1024),
(2,1025),
(2,1026),
(2,1027),
(2,1028),
(2,1029),
(2,1030),
(2,1031),
(2,1032),
(2,1033),
(2,1034),
(2,1035),
(2,1036),
(2,1037),
(2,1038),
(2,1039),
(2,1040),
(2,1041),
(2,1042),
(2,1043),
(2,1044),
(2,1045),
(2,1046),
(2,1047),
(2,1048),
(100,2000),
(100,2005),
(100,2006),
(100,2040),
(100,2041),
(100,2042),
(100,2049),
(100,2050),
(100,2061),
(100,2062),
(101,2000),
(101,2001),
(101,2002),
(101,2003),
(101,2004),
(101,2005),
(101,2006),
(101,2011),
(101,2012),
(101,2013),
(101,2021),
(101,2022),
(101,2023),
(101,2031),
(101,2032),
(101,2033),
(101,2040),
(101,2041),
(101,2042),
(101,2043),
(101,2044),
(101,2045),
(101,2046),
(101,2051),
(101,2061),
(101,2062),
(101,2063),
(101,1),
(101,107),
(101,1035),
(101,1036),
(101,1037),
(101,1038),
(102,2000),
(102,2005),
(102,2040),
(102,2047),
(102,2048),
(103,1),
(103,2),
(103,100),
(103,101),
(103,102),
(103,103),
(103,104),
(103,105),
(103,106),
(103,107),
(103,108),
(103,109),
(103,500),
(103,501),
(103,1000),
(103,1001),
(103,1002),
(103,1003),
(103,1004),
(103,1005),
(103,1006),
(103,1007),
(103,1008),
(103,1009),
(103,1010),
(103,1011),
(103,1012),
(103,1013),
(103,1014),
(103,1015),
(103,1016),
(103,1017),
(103,1018),
(103,1019),
(103,1020),
(103,1021),
(103,1022),
(103,1023),
(103,1024),
(103,1025),
(103,1026),
(103,1027),
(103,1028),
(103,1029),
(103,1030),
(103,1031),
(103,1032),
(103,1033),
(103,1034),
(103,1035),
(103,1036),
(103,1037),
(103,1038),
(103,1039),
(103,1040),
(103,1041),
(103,1042),
(103,1043),
(103,1044),
(103,1045),
(103,1046),
(103,1047),
(103,1048),
(103,2000),
(103,2001),
(103,2002),
(103,2003),
(103,2004),
(103,2005),
(103,2006),
(103,2011),
(103,2012),
(103,2013),
(103,2021),
(103,2022),
(103,2023),
(103,2031),
(103,2032),
(103,2033),
(103,2040),
(103,2041),
(103,2042),
(103,2043),
(103,2044),
(103,2045),
(103,2046),
(103,2047),
(103,2048),
(103,2049),
(103,2050),
(103,2051),
(103,2061),
(103,2062),
(103,2063);

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`dept_id`,`user_name`,`nick_name`,`user_type`,`email`,`phonenumber`,`sex`,`avatar`,`password`,`status`,`del_flag`,`login_ip`,`login_date`,`pwd_update_date`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,103,'admin','超级管理员','00','1298824544@163.com','15888888888','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-07-02 15:12:54','2026-07-01 13:44:20','admin','2026-07-01 13:44:20','',NULL,'管理员'),
(100,105,'owner01','业主张三','00','owner01@example.com','13800000001','0','/profile/avatar/2026/07/02/8a611bbfc1ad45f5ac17bdaebe57fb46.png','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-07-02 14:50:21','2026-07-01 14:48:48','admin','2026-07-01 14:48:48','','2026-07-02 11:26:41','业主/住户示例账号'),
(101,103,'property01','物业管理员李明','00','property01@example.com','13800000002','0','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-07-02 10:47:59','2026-07-01 14:48:48','admin','2026-07-01 14:48:48','',NULL,'物业管理员示例账号'),
(102,107,'repair01','维修人员王师傅','00','repair01@example.com','13800000003','0','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-07-02 10:48:57','2026-07-01 14:48:48','admin','2026-07-01 14:48:48','',NULL,'维修人员示例账号'),
(103,103,'sysadmin01','系统管理员赵强','00','sysadmin01@example.com','13800000004','0','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','',NULL,'2026-07-01 14:48:48','admin','2026-07-01 14:48:48','',NULL,'系统管理员示例账号');

/*Table structure for table `sys_user_post` */

DROP TABLE IF EXISTS `sys_user_post`;

CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';

/*Data for the table `sys_user_post` */

insert  into `sys_user_post`(`user_id`,`post_id`) values 
(1,1),
(2,2);

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`user_id`,`role_id`) values 
(1,1),
(2,2),
(100,100),
(101,101),
(102,102),
(103,103);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
