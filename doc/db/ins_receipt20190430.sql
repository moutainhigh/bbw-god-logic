/*
 Navicat Premium Data Transfer

 Source Server         : 阿里云_dba
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : rm-wz9bv437rb04kz00n6o.mysql.rds.aliyuncs.com:3306
 Source Schema         : as25

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 30/04/2019 17:18:29
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
  `price` int(11) unsigned DEFAULT '0' COMMENT '产品价格',
  `product_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '产品名称',
  `quantity` int(11) NOT NULL COMMENT '购买产品数量',
  `purchase_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  `status` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '1付款订单。0:未付款订单',
  `pay_type` int(11) NOT NULL COMMENT '支付方式:0:渠道的支付方式；1:微信支付；2:支付宝支付。   ',
  `user_receipt_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '下发的用户收据',
  `dispatch_golds` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '实际下发元宝数量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `game_user_id` (`uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='充值明细';

SET FOREIGN_KEY_CHECKS = 1;
