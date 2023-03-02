/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : localhost:3306
 Source Schema         : god_game

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : 65001

 Date: 25/05/2020 12:03:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ins_mall_detail
-- ----------------------------
DROP TABLE IF EXISTS `ins_mall_detail`;
CREATE TABLE `ins_mall_detail` (
  `id` bigint(20) unsigned NOT NULL COMMENT '数据ID',
  `server_group` int(11) NOT NULL COMMENT '区服组',
  `sid` int(11) NOT NULL COMMENT '区服ID',
  `uid` bigint(20) NOT NULL COMMENT '玩家ID',
  `gu_lv` int(11) NOT NULL COMMENT '玩家等级',
  `item` int(11) NOT NULL COMMENT '购买商品分类',
  `good_id` int(11) NOT NULL COMMENT '商品标识',
  `good_name` varchar(10) NOT NULL COMMENT '商品名称',
  `price` int(11) NOT NULL COMMENT '道具价格',
  `buy_num` int(11) NOT NULL COMMENT '购买数量',
  `pay` varchar(6) NOT NULL COMMENT '实际支付',
  `unit` int(11) NOT NULL COMMENT '货币单位',
  `unit_name` varchar(10) NOT NULL COMMENT '货币单位名称',
  `own_money` int(11) NOT NULL COMMENT '购买时拥有的货币数',
  `recharge_amount` int(11) NOT NULL COMMENT '累计充值金额',
  `buy_time` datetime NOT NULL COMMENT '购买时间',
  `role_life` varchar(16) NOT NULL COMMENT '自角色创建到现在的时间',
  `role_life_minutes` int(11) NOT NULL COMMENT '自角色创建到现在的时间(分)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='神仙大会战斗日志';

SET FOREIGN_KEY_CHECKS = 1;
