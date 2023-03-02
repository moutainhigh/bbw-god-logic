/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : localhost:3306
 Source Schema         : god_game

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 23/06/2020 17:01:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wanxian_fight_detail
-- ----------------------------
DROP TABLE IF EXISTS `wanxian_fight_detail`;
CREATE TABLE `wanxian_fight_detail`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `p1` bigint(20) NULL DEFAULT NULL,
  `p2` bigint(20) NULL DEFAULT NULL,
  `winner` int(2) NULL DEFAULT NULL,
  `win_type` int(2) NULL DEFAULT NULL COMMENT '胜利方式1血量为0 2卡牌数为0',
  `wx_type` int(4) NULL DEFAULT NULL,
  `round` int(2) NULL DEFAULT NULL,
  `vid_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `season` int(10) NULL DEFAULT NULL,
  `weekday` int(2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23720 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
