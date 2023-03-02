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

 Date: 23/06/2020 17:01:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wanxian_rank
-- ----------------------------
DROP TABLE IF EXISTS `wanxian_rank`;
CREATE TABLE `wanxian_rank`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) NULL DEFAULT NULL,
  `qualifying_score` int(10) NULL DEFAULT NULL COMMENT '资格赛分数',
  `qualifying_rank` int(10) NULL DEFAULT NULL COMMENT '资格赛名次',
  `group_score` int(3) NULL DEFAULT 0 COMMENT '小组赛分数',
  `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分组',
  `group_rank` int(2) NULL DEFAULT 0 COMMENT '小组赛排名',
  `rank` int(2) NULL DEFAULT NULL COMMENT '总排名，当多名玩家在同一轮淘汰时，此处名次可能会有误差',
  `season` int(10) NULL DEFAULT NULL COMMENT '赛季',
  `wx_type` int(4) NULL DEFAULT NULL COMMENT '赛事类型',
  `gid` int(2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 349 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
