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

 Date: 15/04/2020 14:35:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ins_city_own_detail
-- ----------------------------
DROP TABLE IF EXISTS `ins_city_own_detail`;
CREATE TABLE `ins_city_own_detail` (
  `id` bigint(20) unsigned NOT NULL COMMENT '数据ID',
  `server_group` int(11) NOT NULL COMMENT '区服组',
  `sid` int(11) NOT NULL COMMENT '区服ID',
  `uid` bigint(20) NOT NULL COMMENT '玩家ID',
  `gu_lv` int(11) NOT NULL COMMENT '玩家等级',
  `pay` int(11) NOT NULL COMMENT '累计充值金额',
  `city_id` int(11) NOT NULL COMMENT '城池ID',
  `city_country` int(11) NOT NULL COMMENT '城池所属区域',
  `city_name` varchar(6) NOT NULL COMMENT '城池名称',
  `city_lv` int(11) NOT NULL DEFAULT '0' COMMENT '城池级别',
  `city_lv_num` int(11) NOT NULL COMMENT '第几座对应级别占有城',
  `city_num` int(11) NOT NULL COMMENT '第几座占有城池',
  `own_time` bigint(20) NOT NULL COMMENT '攻城时间',
  `role_life` varchar(16) NOT NULL COMMENT '自角色创建到现在的时间',
  `role_life_minutes` int(11) NOT NULL COMMENT '自角色创建到现在的时间(秒)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='神仙大会战斗日志';

SET FOREIGN_KEY_CHECKS = 1;
