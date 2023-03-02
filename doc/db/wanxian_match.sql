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

 Date: 23/06/2020 17:01:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wanxian_match
-- ----------------------------
DROP TABLE IF EXISTS `wanxian_match`;
CREATE TABLE `wanxian_match`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `p1` bigint(20) NULL DEFAULT NULL COMMENT '先手玩家',
  `p2` bigint(20) NULL DEFAULT NULL COMMENT '后手玩家',
  `wx_type` int(4) NULL DEFAULT NULL COMMENT '赛事0为常规赛9010金系赛9020牧野9030木系9040小兵9050水系9060中坚9070火9080新手9090平民9100土',
  `vid_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '战斗日志key',
  `season` int(10) NULL DEFAULT NULL COMMENT '赛季',
  `weekday` int(10) NULL DEFAULT NULL COMMENT '检索',
  `gid` int(2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12802 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
