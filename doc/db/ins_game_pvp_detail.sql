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

 Date: 26/07/2019 10:19:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ins_game_pvp_detail
-- ----------------------------
DROP TABLE IF EXISTS `ins_game_pvp_detail`;
CREATE TABLE `ins_game_pvp_detail` (
  `id` bigint(20) unsigned NOT NULL COMMENT '数据ID',
  `server_group` int(11) NOT NULL COMMENT '区服组',
  `fight_type` int(11) NOT NULL,
  `fight_type_name` varchar(10) NOT NULL,
  `room_id` int(11) NOT NULL COMMENT '房间ID，重启强连网后会重复',
  `user1` bigint(20) NOT NULL COMMENT '座位号1的玩家ID',
  `user2` bigint(20) NOT NULL COMMENT '座位号2的玩家ID',
  `winner` bigint(20) NOT NULL DEFAULT '0' COMMENT '胜者玩家ID',
  `data_json` json NOT NULL COMMENT '战斗详情数据',
  `fight_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='神仙大会战斗日志';

SET FOREIGN_KEY_CHECKS = 1;
