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

 Date: 28/11/2019 09:25:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ins_game_pve_detail
-- ----------------------------
DROP TABLE IF EXISTS `ins_game_pve_detail`;
CREATE TABLE `ins_game_pve_detail`  (
  `id` bigint(20) NOT NULL,
  `site` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地点',
  `site_id` int(10) NULL DEFAULT NULL COMMENT '地点ID',
  `city_order` int(2) NULL DEFAULT NULL COMMENT '当前是攻城/进阶第几座当前级别的城池',
  `city_hv` int(2) NULL DEFAULT NULL COMMENT '战后城池阶级',
  `city_lv` int(2) NULL DEFAULT NULL COMMENT '城池级别',
  `uid` bigint(20) NULL DEFAULT NULL COMMENT '玩家ID',
  `sid` int(10) NULL DEFAULT NULL COMMENT '区服ID',
  `fight_type` int(5) NULL DEFAULT NULL COMMENT '战斗类型',
  `is_win` int(1) NULL DEFAULT NULL COMMENT '胜负  0 为负  1 为胜',
  `lv` int(5) NULL DEFAULT NULL COMMENT '玩家等级',
  `ai_lv` int(5) NULL DEFAULT NULL COMMENT '对手等级',
  `round` int(2) NULL DEFAULT NULL COMMENT '回合数',
  `use_weapon` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法宝',
  `result_type` int(5) NULL DEFAULT NULL COMMENT '结束类 1召唤师血量扣完，2卡牌打完，3回合已满',
  `recording_time` bigint(15) NULL DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;