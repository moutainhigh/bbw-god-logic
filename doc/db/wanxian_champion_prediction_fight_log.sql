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

 Date: 29/05/2020 16:25:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wanxian_champion_prediction_fight_log
-- ----------------------------
DROP TABLE IF EXISTS `wanxian_champion_prediction_fight_log`;
CREATE TABLE `wanxian_champion_prediction_fight_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) NULL DEFAULT NULL,
  `lv` int(3) NULL DEFAULT NULL,
  `vidkey` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频存储键：例 13002N... 表示周二13:00轮',
  `click_date` datetime(0) NULL DEFAULT NULL COMMENT '点击时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
