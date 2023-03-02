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

 Date: 26/11/2019 17:44:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ins_game_city_war
-- ----------------------------
DROP TABLE IF EXISTS `ins_game_city_war`;
CREATE TABLE `ins_game_city_war`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `detail_id` bigint(20) NULL DEFAULT NULL COMMENT '对应的PVE日志Id',
  `uid` bigint(20) NULL DEFAULT NULL COMMENT '玩家ID',
  `city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城池名称',
  `city_id` int(5) NULL DEFAULT NULL COMMENT '城池Id',
  `card_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '卡名',
  `card_lv` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '卡等级',
  `card_hv` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '卡阶级',
  `card_json` json NULL COMMENT '卡数据',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
