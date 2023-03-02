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

 Date: 17/04/2020 14:38:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ins_card_comment
-- ----------------------------
DROP TABLE IF EXISTS `ins_card_comment`;
CREATE TABLE `ins_card_comment`  (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT '主键id',
  `card_id` int(4) NOT NULL COMMENT '卡牌id',
  `server_group` int(4) NOT NULL COMMENT '区服组',
  `rid` bigint(20) NOT NULL COMMENT '评论人的角色id',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `comment_time` datetime(0) NOT NULL COMMENT '评论时间',
  `favorite_count` int(6) NOT NULL COMMENT '点赞总数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `card_id`(`card_id`, `server_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
