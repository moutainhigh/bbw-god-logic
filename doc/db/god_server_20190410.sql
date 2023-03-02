/*
 Navicat Premium Data Transfer

 Source Server         : 刘少军的MAC
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : localhost:3306
 Source Schema         : godserver_98

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : 65001

 Date: 10/04/2019 08:03:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ins_receipt
-- ----------------------------
DROP TABLE IF EXISTS `ins_receipt`;
CREATE TABLE `ins_receipt` (
  `id` bigint(11) unsigned NOT NULL COMMENT '订单ID',
  `sid` int(10) unsigned NOT NULL COMMENT '支付时候的区服ID。',
  `uid` bigint(11) NOT NULL COMMENT '玩家ID',
  `pid` int(11) NOT NULL COMMENT '产品ID',
  `quantity` int(11) NOT NULL COMMENT '购买产品数量',
  `purchase_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  `status` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '1付款订单。0:未付款订单',
  `pay_type` int(11) NOT NULL COMMENT '支付方式:0:渠道的支付方式；1:微信支付；2:支付宝支付。   ',
  `user_receipt_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '下发的用户收据',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `game_user_id` (`uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='充值明细';

-- ----------------------------
-- Table structure for ins_server_data
-- ----------------------------
DROP TABLE IF EXISTS `ins_server_data`;
CREATE TABLE `ins_server_data` (
  `data_id` bigint(20) unsigned NOT NULL COMMENT '资源ID',
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `data_type` varchar(20) NOT NULL COMMENT '资源类型',
  `data_json` json NOT NULL COMMENT '资源JSON',
  PRIMARY KEY (`data_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区服数据';

-- ----------------------------
-- Table structure for ins_user
-- ----------------------------
DROP TABLE IF EXISTS `ins_user`;
CREATE TABLE `ins_user` (
  `uid` bigint(20) unsigned NOT NULL COMMENT '区服角色ID',
  `sid` int(11) unsigned NOT NULL COMMENT '所属区服ID',
  `nickname` varchar(32) NOT NULL COMMENT '昵称',
  `username` varchar(64) NOT NULL COMMENT '游戏账号',
  `level` int(11) unsigned NOT NULL DEFAULT '1' COMMENT '等级',
  `experience` int(11) unsigned NOT NULL DEFAULT '1' COMMENT '经验',
  `gold` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '元宝',
  `copper` int(11) unsigned NOT NULL DEFAULT '5000' COMMENT '铜钱',
  `head` int(11) unsigned NOT NULL DEFAULT '1' COMMENT '头像标识',
  `data_json` json NOT NULL COMMENT '玩家数据JSON',
  `last_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上次更新时间',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家属性、资源等数据';

-- ----------------------------
-- Table structure for ins_user_data
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_data`;
CREATE TABLE `ins_user_data` (
  `data_id` bigint(20) unsigned NOT NULL COMMENT '资源ID',
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家区服ID',
  `data_type` varchar(20) NOT NULL COMMENT '资源类型',
  `data_json` json NOT NULL COMMENT '资源JSON',
  PRIMARY KEY (`data_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家相关数据';

-- ----------------------------
-- Table structure for ins_user_detail
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail`;
CREATE TABLE `ins_user_detail` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_0
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_0`;
CREATE TABLE `ins_user_detail_0` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_1
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_1`;
CREATE TABLE `ins_user_detail_1` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_2
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_2`;
CREATE TABLE `ins_user_detail_2` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_3
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_3`;
CREATE TABLE `ins_user_detail_3` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_4
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_4`;
CREATE TABLE `ins_user_detail_4` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_5
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_5`;
CREATE TABLE `ins_user_detail_5` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_6
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_6`;
CREATE TABLE `ins_user_detail_6` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_7
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_7`;
CREATE TABLE `ins_user_detail_7` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_8
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_8`;
CREATE TABLE `ins_user_detail_8` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

-- ----------------------------
-- Table structure for ins_user_detail_9
-- ----------------------------
DROP TABLE IF EXISTS `ins_user_detail_9`;
CREATE TABLE `ins_user_detail_9` (
  `id` bigint(20) unsigned NOT NULL,
  `sid` int(11) unsigned NOT NULL COMMENT '区服ID',
  `uid` bigint(20) unsigned NOT NULL COMMENT '玩家ID',
  `user_level` int(11) NOT NULL COMMENT '玩家等级',
  `opdate` int(11) unsigned NOT NULL COMMENT '操作日期',
  `optime` int(11) unsigned NOT NULL COMMENT '操作时间',
  `award_type` int(11) unsigned NOT NULL COMMENT '资源类型。AwardEnum.java',
  `award_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源ID',
  `award_name` varchar(255) NOT NULL COMMENT '名称',
  `value_change` int(11) NOT NULL COMMENT '变化数量。正数为加，负数为扣除。',
  `after_value` int(11) unsigned NOT NULL COMMENT '变化后的值',
  `way` int(11) unsigned NOT NULL COMMENT '途经。WayEnum.java',
  `way_name` varchar(255) NOT NULL COMMENT '途径名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家资源明细表';

SET FOREIGN_KEY_CHECKS = 1;
