/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : god_game

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 17/04/2020 14:39:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ins_card_comment_favorite_detail
-- ----------------------------
DROP TABLE IF EXISTS `ins_card_comment_favorite_detail`;
CREATE TABLE `ins_card_comment_favorite_detail`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `card_id` int(4) NOT NULL COMMENT '卡牌id',
  `comment_id` bigint(20) NOT NULL COMMENT '评论id',
  `rid` bigint(20) NOT NULL COMMENT '点赞的玩家id',
  `favorite_time` datetime(0) NOT NULL COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `card_id`(`card_id`, `comment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
