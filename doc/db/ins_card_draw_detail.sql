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

 Date: 31/05/2020 09:54:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ins_card_draw_detail
-- ----------------------------
DROP TABLE IF EXISTS `ins_card_draw_detail`;
CREATE TABLE `ins_card_draw_detail` (
  `id` bigint(20) unsigned NOT NULL COMMENT '数据ID',
  `server_group` int(11) NOT NULL COMMENT '区服组',
  `sid` int(11) NOT NULL COMMENT '区服ID',
  `uid` bigint(20) NOT NULL COMMENT '玩家ID',
  `gu_lv` int(11) NOT NULL COMMENT '玩家等级',
  `way` int(11) NOT NULL COMMENT '抽卡途径',
  `way_name` varchar(10) NOT NULL COMMENT '抽卡途径名称',
  `draw_num` int(11) NOT NULL COMMENT '抽卡次数',
  `result` varchar(100) NOT NULL COMMENT '抽卡结果',
  `new_cards_num` int(11) NOT NULL COMMENT '新卡牌数量',
  `new_cards` varchar(100) NOT NULL COMMENT '新卡牌',
  `max_star` int(11) NOT NULL COMMENT '本次抽卡最大星级',
  `draw_time` datetime NOT NULL COMMENT '购买时间',
  `recharge_amount` int(11) NOT NULL COMMENT '累计充值金额',
  `last_recharge_time` datetime DEFAULT NULL COMMENT '最近充值时间',
  `role_life` varchar(16) NOT NULL COMMENT '自角色创建到现在的时间',
  `role_life_minutes` int(11) NOT NULL COMMENT '自角色创建到现在的时间(分)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='神仙大会战斗日志';

SET FOREIGN_KEY_CHECKS = 1;
