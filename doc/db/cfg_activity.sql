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

 Date: 18/08/2020 14:32:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cfg_activity
-- ----------------------------
DROP TABLE IF EXISTS `cfg_activity`;
CREATE TABLE `cfg_activity`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_type` int(11) NOT NULL,
  `type` int(11) NOT NULL COMMENT '10首冲，20累计充值，30连续登录，40累计登录，50补充体力，60邀请好友',
  `scope` int(11) NOT NULL DEFAULT 20,
  `serial` int(11) DEFAULT NULL,
  `series` int(11) DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `need_value` int(11) DEFAULT NULL,
  `awards` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '奖励',
  `status` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100801 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cfg_activity
-- ----------------------------
INSERT INTO `cfg_activity` VALUES (1, 60, 10, 10, 1, NULL, '首冲心动大礼', NULL, 1, '[{\"item\":40,\"awardId\":413,\"num\":1},{\"item\":40,\"awardId\":414,\"num\":1},{\"item\":60,\"awardId\":11330,\"num\":1},{\"item\":60,\"awardId\":11210,\"num\":1},{\"item\":60,\"awardId\":11220,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10, 60, 11, 10, 2, NULL, '限定卡牌', NULL, 68, '[{\"awardId\":606,\"item\":60,\"num\":1},{\"awardId\":609,\"item\":60,\"num\":1},{\"awardId\":603,\"item\":60,\"num\":1},{\"awardId\":615,\"item\":60,\"num\":1},{\"awardId\":612,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (20, 60, 12, 10, 3, NULL, '3倍返利', NULL, 128, '[]', b'1');
INSERT INTO `cfg_activity` VALUES (21, 40, 12, 10, 3, 1, '攻下1座1级城', NULL, 1, '[{\"item\":10,\"num\":120}]', b'1');
INSERT INTO `cfg_activity` VALUES (22, 40, 12, 10, 3, 1, '攻下3座1级城', NULL, 3, '[{\"item\":10,\"num\":120}]', b'1');
INSERT INTO `cfg_activity` VALUES (23, 40, 12, 10, 3, 1, '攻下5座1级城', NULL, 5, '[{\"item\":10,\"num\":120}]', b'1');
INSERT INTO `cfg_activity` VALUES (24, 40, 12, 10, 3, 2, '攻下1座2级城', NULL, 1, '[{\"item\":10,\"num\":160}]', b'1');
INSERT INTO `cfg_activity` VALUES (25, 40, 12, 10, 3, 2, '攻下3座2级城', NULL, 3, '[{\"item\":10,\"num\":160}]', b'1');
INSERT INTO `cfg_activity` VALUES (26, 40, 12, 10, 3, 2, '攻下5座2级城', NULL, 5, '[{\"item\":10,\"num\":160}]', b'1');
INSERT INTO `cfg_activity` VALUES (27, 40, 12, 10, 3, 2, '攻下7座2级城', NULL, 7, '[{\"item\":10,\"num\":160}]', b'1');
INSERT INTO `cfg_activity` VALUES (28, 40, 12, 10, 3, 3, '攻下1座3级城', NULL, 1, '[{\"item\":10,\"num\":220}]', b'1');
INSERT INTO `cfg_activity` VALUES (29, 40, 12, 10, 3, 3, '攻下3座3级城', NULL, 3, '[{\"item\":10,\"num\":220}]', b'1');
INSERT INTO `cfg_activity` VALUES (30, 40, 12, 10, 3, 3, '攻下5座3级城', NULL, 5, '[{\"item\":10,\"num\":220}]', b'1');
INSERT INTO `cfg_activity` VALUES (31, 40, 12, 10, 3, 3, '攻下7座3级城', NULL, 7, '[{\"item\":10,\"num\":220}]', b'1');
INSERT INTO `cfg_activity` VALUES (32, 40, 12, 10, 3, 4, '攻下1座4级城', NULL, 1, '[{\"item\":10,\"num\":320}]', b'1');
INSERT INTO `cfg_activity` VALUES (33, 40, 12, 10, 3, 4, '攻下3座4级城', NULL, 3, '[{\"item\":10,\"num\":320}]', b'1');
INSERT INTO `cfg_activity` VALUES (34, 40, 12, 10, 3, 4, '攻下5座4级城', NULL, 5, '[{\"item\":10,\"num\":320}]', b'1');
INSERT INTO `cfg_activity` VALUES (35, 40, 12, 10, 3, 5, '攻下1座5级城', NULL, 1, '[{\"item\":10,\"num\":500}]', b'1');
INSERT INTO `cfg_activity` VALUES (36, 40, 12, 10, 3, 5, '攻下2座5级城', NULL, 2, '[{\"item\":10,\"num\":500}]', b'1');
INSERT INTO `cfg_activity` VALUES (201, 60, 20, 20, 5, NULL, '累计充值30元', NULL, 30, '[{\"awardId\":10110,\"week\":1,\"item\":60,\"num\":2},{\"awardId\":10110,\"week\":0,\"item\":60,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (202, 60, 20, 20, 5, NULL, '累计充值50元', NULL, 50, '[{\"awardId\":10110,\"week\":1,\"item\":60,\"num\":3},{\"awardId\":10110,\"week\":0,\"item\":60,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (203, 60, 20, 20, 5, NULL, '累计充值100元', NULL, 100, '[{\"item\":40,\"week\":1,\"star\":4,\"num\":1},{\"awardId\":10110,\"week\":0,\"item\":60,\"num\":5}]', b'1');
INSERT INTO `cfg_activity` VALUES (204, 60, 20, 20, 5, NULL, '累计充值200元', NULL, 200, '[{\"item\":40,\"week\":1,\"star\":4,\"num\":1},{\"awardId\":10180,\"week\":0,\"item\":60,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (205, 60, 20, 20, 5, NULL, '累计充值500元', NULL, 500, '[{\"item\":40,\"week\":1,\"star\":4,\"num\":2},{\"awardId\":10180,\"week\":0,\"item\":60,\"num\":20}]', b'1');
INSERT INTO `cfg_activity` VALUES (206, 60, 20, 20, 5, NULL, '累计充值1000元', NULL, 1000, '[{\"awardId\":10010,\"item\":60,\"week\":1,\"num\":8},{\"awardId\":10180,\"week\":0,\"item\":60,\"num\":25}]', b'1');
INSERT INTO `cfg_activity` VALUES (207, 60, 20, 20, 5, NULL, '累计充值1500元', NULL, 1500, '[{\"awardId\":10180,\"item\":60,\"week\":1,\"num\":20},{\"awardId\":10180,\"week\":0,\"item\":60,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (208, 60, 20, 20, 5, NULL, '累计充值2000元', NULL, 2000, '[{\"awardId\":10180,\"item\":60,\"week\":1,\"num\":25},{\"awardId\":10180,\"week\":0,\"item\":60,\"num\":35}]', b'1');
INSERT INTO `cfg_activity` VALUES (209, 60, 20, 20, 5, NULL, '累计充值3000元', NULL, 3000, '[{\"awardId\":10180,\"item\":60,\"week\":1,\"num\":50},{\"awardId\":10180,\"week\":0,\"item\":60,\"num\":70}]', b'1');
INSERT INTO `cfg_activity` VALUES (210, 60, 20, 20, 5, NULL, '累计充值4000元', NULL, 4000, '[{\"awardId\":10180,\"item\":60,\"week\":1,\"num\":60},{\"awardId\":10180,\"week\":0,\"item\":60,\"num\":80}]', b'1');
INSERT INTO `cfg_activity` VALUES (211, 60, 20, 20, 5, NULL, '累计充值5000元', NULL, 5000, '[{\"awardId\":527,\"item\":40,\"week\":1,\"num\":1},{\"awardId\":10180,\"week\":0,\"item\":60,\"num\":100}]', b'1');
INSERT INTO `cfg_activity` VALUES (233, 60, 23, 20, 6, NULL, '今日累充30元', NULL, 30, '[{\"item\":60,\"awardId\":10110,\"num\":2},{\"item\":60,\"awardId\":20,\"num\":1},{\"item\":60,\"awardId\":10020,\"num\":18}]', b'1');
INSERT INTO `cfg_activity` VALUES (234, 60, 23, 20, 6, NULL, '今日累充68元', NULL, 68, '[{\"item\":60,\"awardId\":10110,\"num\":2},{\"item\":20,\"num\":100000},{\"item\":60,\"awardId\":10020,\"num\":28}]', b'1');
INSERT INTO `cfg_activity` VALUES (235, 60, 23, 20, 6, NULL, '今日累充98元', NULL, 98, '[{\"item\":60,\"awardId\":10110,\"num\":2},{\"item\":20,\"num\":100000},{\"item\":60,\"awardId\":10020,\"num\":28}]', b'1');
INSERT INTO `cfg_activity` VALUES (236, 60, 23, 20, 6, NULL, '今日累充128元', NULL, 128, '[{\"item\":60,\"awardId\":10110,\"num\":2},{\"item\":20,\"num\":150000},{\"item\":60,\"awardId\":10020,\"num\":38}]', b'1');
INSERT INTO `cfg_activity` VALUES (237, 60, 23, 20, 6, NULL, '今日累充198元', NULL, 198, '[{\"item\":60,\"awardId\":10110,\"num\":3},{\"item\":20,\"num\":200000},{\"item\":60,\"awardId\":10020,\"num\":38}]', b'1');
INSERT INTO `cfg_activity` VALUES (238, 60, 23, 20, 6, NULL, '今日累充328元', NULL, 328, '[{\"item\":60,\"awardId\":10180,\"num\":15},{\"item\":20,\"num\":400000},{\"item\":60,\"awardId\":10020,\"num\":48}]', b'1');
INSERT INTO `cfg_activity` VALUES (239, 60, 23, 20, 6, NULL, '今日累充448元', NULL, 448, '[{\"item\":60,\"awardId\":10180,\"num\":18},{\"item\":20,\"num\":500000},{\"item\":60,\"awardId\":10020,\"num\":58}]', b'1');
INSERT INTO `cfg_activity` VALUES (240, 60, 23, 20, 6, NULL, '今日累充648元', NULL, 648, '[{\"item\":60,\"awardId\":10180,\"num\":28},{\"item\":20,\"num\":600000},{\"item\":60,\"awardId\":10020,\"num\":58}]', b'1');
INSERT INTO `cfg_activity` VALUES (241, 60, 23, 20, 6, NULL, '今日累充1000元', NULL, 1000, '[{\"item\":60,\"awardId\":10180,\"num\":38},{\"item\":20,\"num\":1300000},{\"item\":60,\"awardId\":10020,\"num\":98}]', b'1');
INSERT INTO `cfg_activity` VALUES (242, 60, 23, 20, 6, NULL, '今日累充1500元', NULL, 1500, '[{\"item\":60,\"awardId\":10180,\"num\":38},{\"item\":20,\"num\":1600000},{\"item\":60,\"awardId\":10020,\"num\":108}]', b'1');
INSERT INTO `cfg_activity` VALUES (243, 60, 23, 20, 6, NULL, '今日累充2000元', NULL, 2000, '[{\"item\":60,\"awardId\":10180,\"num\":38},{\"item\":20,\"num\":1800000},{\"item\":60,\"awardId\":10020,\"num\":108}]', b'1');
INSERT INTO `cfg_activity` VALUES (244, 60, 23, 20, 6, NULL, '今日累充3000元', NULL, 3000, '[{\"item\":60,\"awardId\":10180,\"num\":78},{\"item\":60,\"awardId\":20220,\"num\":1},{\"item\":60,\"awardId\":20120,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (245, 60, 23, 20, 6, NULL, '今日累充4000元', NULL, 4000, '[{\"item\":60,\"awardId\":10180,\"num\":88},{\"item\":60,\"awardId\":20220,\"num\":1},{\"item\":60,\"awardId\":20120,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (246, 60, 23, 20, 6, NULL, '今日累充5000元', NULL, 5000, '[{\"item\":60,\"awardId\":10180,\"num\":98},{\"item\":60,\"awardId\":20230,\"num\":1},{\"item\":60,\"awardId\":20130,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (247, 60, 23, 20, 6, NULL, '今日累充7000元', NULL, 7000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20230,\"num\":1},{\"item\":60,\"awardId\":20130,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (248, 60, 23, 20, 6, NULL, '今日累充10000元', NULL, 10000, '[{\"item\":60,\"awardId\":11065,\"num\":1},{\"item\":60,\"awardId\":20240,\"num\":1},{\"item\":60,\"awardId\":20140,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (301, 90, 30, 10, 1, NULL, '新手登入1天', NULL, 1, '[{\"item\":20,\"num\":20000},{\"awardId\":10020,\"item\":60,\"num\":5},{\"awardId\":510,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (302, 90, 30, 10, 1, NULL, '新手登入2天', NULL, 2, '[{\"awardId\":326,\"item\":40,\"num\":1},{\"awardId\":160,\"item\":60,\"num\":2},{\"awardId\":10120,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (303, 90, 30, 10, 1, NULL, '新手登入3天', NULL, 3, '[{\"awardId\":206,\"item\":40,\"num\":1},{\"awardId\":80,\"item\":60,\"num\":1},{\"awardId\":10130,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (304, 90, 30, 10, 1, NULL, '新手登入4天', NULL, 4, '[{\"awardId\":530,\"item\":60,\"num\":1},{\"awardId\":120,\"item\":60,\"num\":1},{\"awardId\":10140,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (305, 90, 30, 10, 1, NULL, '新手登入5天', NULL, 5, '[{\"awardId\":10110,\"item\":60,\"num\":2},{\"awardId\":110,\"item\":60,\"num\":1},{\"awardId\":10150,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (306, 90, 30, 10, 1, NULL, '新手登入6天', NULL, 6, '[{\"awardId\":20,\"item\":60,\"num\":1},{\"awardId\":60,\"item\":60,\"num\":1},{\"awardId\":10160,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (307, 90, 30, 10, 1, NULL, '新手登入7天', NULL, 7, '[{\"awardId\":201,\"item\":40,\"num\":1},{\"awardId\":10180,\"item\":60,\"num\":3}{\"awardId\":10020,\"item\":60,\"num\":5}]', b'1');
INSERT INTO `cfg_activity` VALUES (401, 60, 40, 10, 9, NULL, '累计登入1天', NULL, 1, '[{\"item\":50,\"awardId\":0,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (402, 60, 40, 10, 9, NULL, '累计登入2天', NULL, 2, '[{\"item\":20,\"num\":5000}]', b'1');
INSERT INTO `cfg_activity` VALUES (403, 60, 40, 10, 9, NULL, '累计登入3天', NULL, 3, '[{\"awardId\":810,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (404, 60, 40, 10, 9, NULL, '累计登入4天', NULL, 4, '[{\"item\":60,\"star\":1,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (405, 60, 40, 10, 9, NULL, '累计登入5天', NULL, 5, '[{\"item\":10,\"num\":40}]', b'1');
INSERT INTO `cfg_activity` VALUES (406, 60, 40, 10, 9, NULL, '累计登入6天', NULL, 6, '[{\"item\":50,\"awardId\":0,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (407, 60, 40, 10, 9, NULL, '累计登入7天', NULL, 7, '[{\"item\":20,\"num\":10000}]', b'1');
INSERT INTO `cfg_activity` VALUES (408, 60, 40, 10, 9, NULL, '累计登入8天', NULL, 8, '[{\"awardId\":820,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (409, 60, 40, 10, 9, NULL, '累计登入9天', NULL, 9, '[{\"item\":60,\"star\":2,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (410, 60, 40, 10, 9, NULL, '累计登入10天', NULL, 10, '[{\"item\":10,\"num\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (411, 60, 40, 10, 9, NULL, '累计登入11天', NULL, 11, '[{\"item\":50,\"awardId\":0,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (412, 60, 40, 10, 9, NULL, '累计登入12天', NULL, 12, '[{\"item\":20,\"num\":20000}]', b'1');
INSERT INTO `cfg_activity` VALUES (413, 60, 40, 10, 9, NULL, '累计登入13天', NULL, 13, '[{\"awardId\":830,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (414, 60, 40, 10, 9, NULL, '累计登入14天', NULL, 14, '[{\"item\":60,\"star\":3,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (415, 60, 40, 10, 9, NULL, '累计登入15天', NULL, 15, '[{\"item\":10,\"num\":80}]', b'1');
INSERT INTO `cfg_activity` VALUES (416, 60, 40, 10, 9, NULL, '累计登入16天', NULL, 16, '[{\"item\":50,\"awardId\":0,\"num\":4}]', b'1');
INSERT INTO `cfg_activity` VALUES (417, 60, 40, 10, 9, NULL, '累计登入17天', NULL, 17, '[{\"item\":20,\"num\":40000}]', b'1');
INSERT INTO `cfg_activity` VALUES (418, 60, 40, 10, 9, NULL, '累计登入18天', NULL, 18, '[{\"awardId\":840,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (419, 60, 40, 10, 9, NULL, '累计登入19天', NULL, 19, '[{\"item\":60,\"star\":4,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (420, 60, 40, 10, 9, NULL, '累计登入20天', NULL, 20, '[{\"item\":10,\"num\":100}]', b'1');
INSERT INTO `cfg_activity` VALUES (421, 60, 40, 10, 9, NULL, '累计登入21天', NULL, 21, '[{\"item\":50,\"awardId\":0,\"num\":5}]', b'1');
INSERT INTO `cfg_activity` VALUES (422, 60, 40, 10, 9, NULL, '累计登入22天', NULL, 22, '[{\"item\":20,\"num\":50000}]', b'1');
INSERT INTO `cfg_activity` VALUES (423, 60, 40, 10, 9, NULL, '累计登入23天', NULL, 23, '[{\"awardId\":850,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (424, 60, 40, 10, 9, NULL, '累计登入24天', NULL, 24, '[{\"item\":60,\"star\":5,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (425, 60, 40, 10, 9, NULL, '累计登入25天', NULL, 25, '[]', b'1');
INSERT INTO `cfg_activity` VALUES (502, 60, 50, 10, 10, 1, '12点前补充体力', NULL, 12, '[{\"item\":30,\"num\":100}]', b'1');
INSERT INTO `cfg_activity` VALUES (503, 60, 50, 10, 10, 2, '12点后补充体力', NULL, 12, '[{\"item\":30,\"num\":100}]', b'1');
INSERT INTO `cfg_activity` VALUES (601, 20, 60, 10, 70, NULL, '邀请1好友', NULL, 1, '[{\"item\":50,\"awardId\":10,\"num\":1},{\"item\":50,\"awardId\":20,\"num\":1},{\"item\":50,\"awardId\":30,\"num\":1},{\"item\":50,\"awardId\":40,\"num\":1},{\"item\":50,\"awardId\":50,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (602, 20, 60, 10, 70, NULL, '邀请5好友', NULL, 5, '[{\"item\":20,\"num\":100000}]', b'1');
INSERT INTO `cfg_activity` VALUES (603, 20, 60, 10, 70, NULL, '邀请10好友', NULL, 10, '[{\"item\":10,\"num\":200}]', b'1');
INSERT INTO `cfg_activity` VALUES (604, 20, 60, 10, 70, NULL, '邀请20好友', NULL, 20, '[{\"awardId\":225,\"item\":40,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (1002, 30, 120, 10, NULL, NULL, '父亲节快乐', '父爱是宽阔的海洋，拥有包容一切的力量，把温暖纳入胸膛，把坚强放在身旁，为儿女撑起幸福的阴凉，为孩子洒下万丈的阳光，祝各位成为父亲的召唤师们：父亲节快乐！', 1, '[{\"num\":1,\"awardId\":10,\"item\":60},{\"num\":1,\"awardId\":80,\"item\":60},{\"num\":1,\"awardId\":60,\"item\":60},{\"num\":1,\"awardId\":30,\"item\":60},{\"num\":200000,\"item\":20}]', b'1');
INSERT INTO `cfg_activity` VALUES (1003, 30, 130, 10, NULL, NULL, '经验加倍', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1004, 30, 140, 10, NULL, NULL, '铜钱加倍', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1005, 30, 55, 10, NULL, NULL, '补充体力翻倍', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1006, 20, 1010, 10, 35, 30, '月卡', NULL, NULL, '[{\"item\":10,\"num\":120}]', b'1');
INSERT INTO `cfg_activity` VALUES (1007, 20, 1010, 10, 35, 90, '季卡', NULL, NULL, '[{\"item\":10,\"num\":120}]', b'1');
INSERT INTO `cfg_activity` VALUES (1009, 30, 180, 10, NULL, NULL, '每日首充翻倍活动', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1010, 30, 190, 20, NULL, NULL, '点将台', '', NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1011, 30, 165, 10, NULL, NULL, '首冲翻倍重置', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (2002, 30, 210, 10, NULL, NULL, '商城产品八折销售', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (2006, 30, 205, 10, NULL, NULL, '今日累冲第6次满130元奖励', '今日累冲第6次满130元奖励。\n现为你发放奖励,祝您游戏愉快！', 7800, '[{\"num\":12,\"awardId\":10180,\"item\":60},{\"num\":6,\"awardId\":11010,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2007, 30, 205, 10, NULL, NULL, '今日累冲第5次满130元奖励', '今日累冲第5次满130元奖励。\n现为你发放奖励,祝您游戏愉快！', 6500, '[{\"num\":12,\"awardId\":10180,\"item\":60},{\"num\":6,\"awardId\":11010,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2008, 30, 205, 10, NULL, NULL, '今日累冲第4次满130元奖励', '今日累冲第4次满130元奖励。\n现为你发放奖励,祝您游戏愉快！', 5200, '[{\"num\":12,\"awardId\":10180,\"item\":60},{\"num\":6,\"awardId\":11010,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2009, 30, 205, 10, NULL, NULL, '今日累冲第3次满130元奖励', '今日累冲第3次满130元奖励。\n现为你发放奖励,祝您游戏愉快！', 3900, '[{\"num\":12,\"awardId\":10180,\"item\":60},{\"num\":6,\"awardId\":11010,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2010, 30, 205, 10, NULL, NULL, '今日累冲第2次满130元奖励', '今日累冲第2次满130元奖励。\n现为你发放奖励,祝您游戏愉快！', 2600, '[{\"num\":12,\"awardId\":10180,\"item\":60},{\"num\":6,\"awardId\":11010,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2011, 30, 205, 10, NULL, NULL, '今日累冲第1次满130元奖励', '今日累冲第1次满130元奖励。\n现为你发放奖励,祝您游戏愉快！', 1300, '[{\"num\":12,\"awardId\":10180,\"item\":60},{\"num\":6,\"awardId\":11010,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2012, 30, 205, 10, NULL, NULL, '今日累冲满68元奖励', '您今日累冲满68元。\n现为你发放奖励,祝您游戏愉快！', 680, '[{\"num\":3,\"awardId\":10110,\"item\":60},{\"num\":3,\"awardId\":580,\"item\":60},{\"num\":1,\"awardId\":520,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2013, 30, 205, 10, NULL, NULL, '今日累冲满30元奖励', '您今日累冲满30元。\n现为你发放奖励,祝您游戏愉快！', 300, '[{\"num\":1,\"awardId\":10110,\"item\":60},{\"num\":1,\"awardId\":20,\"item\":60},{\"num\":1,\"awardId\":10,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2014, 30, 205, 10, NULL, NULL, '今日累冲满18元奖励', '您今日累冲满18元。\n现为你发放奖励,祝您游戏愉快！', 180, '[{\"num\":1,\"awardId\":10110,\"item\":60},{\"num\":1,\"awardId\":60,\"item\":60},{\"num\":1,\"star\":4,\"item\":40,isSpecial:1}]', b'1');
INSERT INTO `cfg_activity` VALUES (2015, 30, 205, 10, NULL, NULL, '今日累冲满6元奖励', '您今日累冲满6元。\n现为你发放奖励,祝您游戏愉快！', 60, '[{\"num\":1,\"awardId\":530,\"item\":60},{\"num\":2,\"awardId\":570,\"item\":60},{\"num\":90,\"item\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (2112, 30, 200, 10, NULL, NULL, '累计充值满100元奖励', '您活动期间已累计充值满100元。\n现为你发放奖励,祝您游戏愉快！', 1000, '[{\"num\":1,\"awardId\":850,\"item\":60},{\"num\":1,\"awardId\":840,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2113, 30, 200, 10, NULL, NULL, '累计充值满200元奖励', '您活动期间累计充值满200元。\n现为你发放奖励,祝您游戏愉快！', 2000, '[{\"num\":2020,\"item\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (2114, 30, 200, 10, NULL, NULL, '累计充值满500元奖励', '您活动期间累计充值满500元。\n现为你发放奖励,祝您游戏愉快！', 5000, '[{\"num\":1,\"awardId\":11060,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2115, 30, 200, 10, NULL, NULL, '累计充值满800元奖励', '您活动期间累计充值满800元。\n现为你发放奖励,祝您游戏愉快！', 8000, '-', b'1');
INSERT INTO `cfg_activity` VALUES (2116, 30, 200, 10, NULL, NULL, '累计充值满1000元奖励', '您活动期间累计充值满1000元。\n现为你发放奖励,祝您游戏愉快！', 10000, '[{\"num\":30,\"awardId\":10180,\"item\":60},{\"num\":20,\"awardId\":11010,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2117, 30, 200, 10, NULL, NULL, '累计充值满1800元奖励', '您活动期间累计充值满1800元。\n现为你发放奖励,祝您游戏愉快！', 18000, '-', b'1');
INSERT INTO `cfg_activity` VALUES (2118, 30, 200, 10, NULL, NULL, '累计充值满2000元奖励', '您活动期间累计充值满2000元。\n现为你发放奖励,祝您游戏愉快！', 20000, '[{\"num\":60,\"awardId\":10180,\"item\":60},{\"num\":40,\"awardId\":11010,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2119, 30, 200, 10, NULL, NULL, '累计充值满3000元奖励', '您活动期间累计充值满3000元。\n现为你发放奖励,祝您游戏愉快！', 30000, '[{\"num\":60,\"awardId\":10180,\"item\":60},{\"num\":40,\"awardId\":11010,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2120, 30, 200, 10, NULL, NULL, '累计充值满5000元奖励', '您活动期间累计充值满5000元。\n现为你发放奖励,祝您游戏愉快！', 50000, '[{\"num\":1,\"awardId\":11065,\"item\":60},{\"num\":120,\"awardId\":10180,\"item\":60},{\"num\":80,\"awardId\":11010,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (9101, 10, 9010, 20, 24, NULL, '新手特惠', NULL, NULL, NULL, b'0');
INSERT INTO `cfg_activity` VALUES (9103, 10, 9013, 20, 24, NULL, '开服必备', NULL, NULL, NULL, b'0');
INSERT INTO `cfg_activity` VALUES (9201, 90, 9020, 20, 2, NULL, '首位占领5级城', NULL, 1, '[{\"awardId\":850,\"item\":60,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (9211, 90, 9020, 20, 2, 1, '攻占5座初级城', NULL, 5, '[{\"item\":20,\"num\":10000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9212, 90, 9020, 20, 2, 1, '攻占10座初级城', NULL, 10, '[{\"item\":20,\"num\":20000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9213, 90, 9020, 20, 2, 1, '攻占15座初级城', NULL, 15, '[{\"item\":20,\"num\":50000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9214, 90, 9020, 20, 2, 1, '攻占20座初级城', NULL, 20, '[{\"item\":20,\"num\":150000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9221, 90, 9020, 20, 2, 2, '攻占5座二级城', NULL, 5, '[{\"awardId\":80,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9222, 90, 9020, 20, 2, 2, '攻占7座二级城', NULL, 7, '[{\"awardId\":10020,\"item\":60,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (9223, 90, 9020, 20, 2, 2, '攻占10座二级城', NULL, 10, '[{\"awardId\":10020,\"item\":60,\"num\":20}]', b'1');
INSERT INTO `cfg_activity` VALUES (9224, 90, 9020, 20, 2, 2, '攻占15座二级城', NULL, 15, '[{\"awardId\":10020,\"item\":60,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (9231, 90, 9020, 20, 2, 3, '攻占5座三级城', NULL, 5, '[{\"item\":40,\"star\":3,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9232, 90, 9020, 20, 2, 3, '攻占10座三级城', NULL, 10, '[{\"item\":40,\"star\":4,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9233, 90, 9020, 20, 2, 3, '攻占15座三级城', NULL, 15, '[{\"item\":40,\"star\":4,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9241, 90, 9020, 20, 2, 4, '攻占3座四级城', NULL, 3, '[{\"item\":40,\"star\":5,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9251, 90, 9020, 20, 2, 5, '攻占1座主城', NULL, 1, '[{\"awardId\":850,\"item\":60,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (9301, 10, 9030, 20, 24, NULL, '每日消费100元宝', NULL, 100, '[{\"awardId\":10020,\"item\":60,\"num\":10},{\"awardId\":10040,\"item\":60,\"num\":1}]', b'0');
INSERT INTO `cfg_activity` VALUES (9302, 10, 9030, 20, 24, NULL, '每日消费300元宝', NULL, 300, '[{\"awardId\":10020,\"item\":60,\"num\":20},{\"awardId\":80,\"item\":60,\"num\":1}]', b'0');
INSERT INTO `cfg_activity` VALUES (9303, 10, 9030, 20, 24, NULL, '每日消费600元宝', NULL, 600, '[{\"awardId\":10030,\"item\":60,\"num\":1},{\"awardId\":60,\"item\":60,\"num\":1}]', b'0');
INSERT INTO `cfg_activity` VALUES (9304, 10, 9030, 20, 24, NULL, '每日消费1000元宝', NULL, 1000, '[{\"awardId\":10030,\"item\":60,\"num\":1},{\"awardId\":10010,\"item\":60,\"num\":1}]', b'0');
INSERT INTO `cfg_activity` VALUES (9305, 10, 9030, 20, 24, NULL, '每日消费1500元宝', NULL, 1500, '[{\"awardId\":10030,\"item\":60,\"num\":1},{\"awardId\":10,\"item\":60,\"num\":1}]', b'0');
INSERT INTO `cfg_activity` VALUES (9306, 10, 9030, 20, 24, NULL, '每日消费2000元宝', NULL, 2000, '[{\"awardId\":10030,\"item\":60,\"num\":1},{\"awardId\":570,\"item\":60,\"num\":1}]', b'0');
INSERT INTO `cfg_activity` VALUES (9307, 10, 9030, 20, 24, NULL, '每日消费3000元宝', NULL, 3000, '[{\"awardId\":10030,\"item\":60,\"num\":2},{\"awardId\":20,\"item\":60,\"num\":1}]', b'0');
INSERT INTO `cfg_activity` VALUES (9308, 10, 9030, 20, 24, NULL, '每日消费5000元宝', NULL, 5000, '[{\"awardId\":10030,\"item\":60,\"num\":4},{\"awardId\":10030,\"item\":60,\"num\":2}]', b'0');
INSERT INTO `cfg_activity` VALUES (9309, 10, 9030, 20, 24, NULL, '每日消费7000元宝', NULL, 7000, '[{\"awardId\":10030,\"item\":60,\"num\":4},{\"awardId\":10030,\"item\":60,\"num\":2}]', b'0');
INSERT INTO `cfg_activity` VALUES (9310, 10, 9030, 20, 24, NULL, '每日消费10000元宝', NULL, 10000, '[{\"awardId\":10030,\"item\":60,\"num\":6},{\"awardId\":10030,\"item\":60,\"num\":4}]', b'0');
INSERT INTO `cfg_activity` VALUES (9401, 60, 9040, 10, 8, NULL, '星君宝库', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (9501, 90, 9050, 20, 3, NULL, '累计抽卡3次', NULL, 3, '[{\"item\":10,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (9505, 90, 9050, 20, 3, NULL, '累计抽卡5次', NULL, 5, '[{\"item\":10,\"num\":50}]', b'1');
INSERT INTO `cfg_activity` VALUES (9510, 90, 9050, 20, 3, NULL, '累计抽卡10次', NULL, 10, '[{\"item\":10,\"num\":90}]', b'1');
INSERT INTO `cfg_activity` VALUES (9515, 90, 9050, 20, 3, NULL, '累计抽卡20次', NULL, 20, '[{\"item\":10,\"num\":90},{\"awardId\":10180,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9520, 90, 9050, 20, 3, NULL, '累计抽卡30次', NULL, 30, '[{\"item\":10,\"num\":135},{\"awardId\":10180,\"item\":60,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (9525, 90, 9050, 20, 3, NULL, '累计抽卡60次', NULL, 60, '[{\"item\":10,\"num\":270},{\"awardId\":10180,\"item\":60,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9530, 90, 9050, 20, 3, NULL, '累计抽卡100次', NULL, 100, '[{\"item\":10,\"num\":450},{\"awardId\":10180,\"item\":60,\"num\":5},{\"awardId\":3060,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9535, 90, 9050, 20, 3, NULL, '累计抽卡150次', NULL, 150, '[{\"item\":10,\"num\":675},{\"awardId\":10180,\"item\":60,\"num\":7}]', b'1');
INSERT INTO `cfg_activity` VALUES (9540, 90, 9050, 20, 3, NULL, '累计抽卡200次', NULL, 200, '[{\"item\":10,\"num\":900},{\"awardId\":10180,\"item\":60,\"num\":9},{\"awardId\":10010,\"item\":60,\"num\":8}]', b'1');
INSERT INTO `cfg_activity` VALUES (9545, 90, 9050, 20, 3, NULL, '累计抽卡250次', NULL, 250, '[{\"item\":10,\"num\":1125},{\"awardId\":10180,\"item\":60,\"num\":12}]', b'1');
INSERT INTO `cfg_activity` VALUES (9550, 90, 9050, 20, 3, NULL, '累计抽卡300次', NULL, 300, '[{\"item\":10,\"num\":1350},{\"awardId\":10180,\"item\":60,\"num\":14},{\"awardId\":31020,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9555, 90, 9050, 20, 3, NULL, '累计抽卡400次', NULL, 400, '[{\"item\":10,\"num\":1800},{\"awardId\":10180,\"item\":60,\"num\":18}]', b'1');
INSERT INTO `cfg_activity` VALUES (9560, 90, 9050, 20, 3, NULL, '累计抽卡500次', NULL, 500, '[{\"item\":10,\"num\":2250},{\"awardId\":10180,\"item\":60,\"num\":23},{\"awardId\":10170,\"item\":60,\"num\":8}]', b'1');
INSERT INTO `cfg_activity` VALUES (9610, 60, 9060, 20, 4, NULL, '消费福利', NULL, 8888, '[{\"item\":40,\"star\":4,\"isNotOwn\":1,\"week\":0,\"num\":1,\"strategy\":\"消费福利可选卡牌\"},{\"item\":40,\"star\":4,\"isNotOwn\":1,\"week\":2,\"num\":1,\"strategy\":\"消费福利第2和3周\"},{\"item\":40,\"star\":4,\"isNotOwn\":1,\"week\":3,\"num\":1,\"strategy\":\"消费福利第2和3周\"},{\"awardId\":10180,\"item\":60,\"num\":50,\"week\":0,},{\"awardId\":10180,\"item\":60,\"num\":50,\"week\":2,},{\"awardId\":10180,\"item\":60,\"num\":50,\"week\":3,}]', b'1');
INSERT INTO `cfg_activity` VALUES (9701, 30, 9070, 20, 25, NULL, '神力横扫', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (9801, 30, 9080, 10, 25, NULL, '破军星', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (9901, 10, 9090, 10, 5, NULL, '周度礼包', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (9902, 10, 9095, 10, 7, NULL, '月度礼包', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (9903, 50, 10010, 10, 10, NULL, '节日登录礼包', NULL, 1, '[{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":60,\"awardId\":10,\"num\":1},{\"item\":60,\"awardId\":60,\"num\":1},{\"item\":60,\"awardId\":30,\"num\":1},{\"item\":20,\"num\":200000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9904, 50, 10020, 10, 20, NULL, '节日签到第一天', NULL, 1, '[{\"item\":20,\"num\":300000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9905, 50, 10020, 10, 20, NULL, '节日签到第二天', NULL, 2, '[{\"item\":60,\"awardId\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9906, 50, 10020, 10, 20, NULL, '节日签到第三天', NULL, 3, '[{\"item\":40,\"star\":4,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9907, 50, 10020, 10, 20, NULL, '节日签到第四天', NULL, 4, '[{\"item\":60,\"awardId\":530,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9908, 50, 10020, 10, 20, NULL, '节日签到第五天', NULL, 5, '[{\"item\":60,\"awardId\":10110,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9909, 50, 10030, 10, 50, NULL, '今日累计充值6元', NULL, 6, '[{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":30,\"num\":90},{\"item\":60,\"awardId\":570,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (9910, 50, 10030, 10, 50, NULL, '今日累计充值18元', NULL, 18, '[{\"item\":60,\"awardId\":10110,\"num\":1},{\"item\":40,\"star\":4,\"num\":1},{\"item\":60,\"awardId\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9911, 50, 10030, 10, 50, NULL, '今日累计充值30元', NULL, 30, '[{\"item\":60,\"awardId\":10110,\"num\":1},{\"item\":60,\"awardId\":20,\"num\":1},{\"item\":60,\"awardId\":10,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9912, 50, 10030, 10, 50, NULL, '今日累计充值68元', NULL, 68, '[{\"item\":60,\"awardId\":12005,\"num\":1},{\"item\":60,\"awardId\":580,\"num\":3},{\"item\":60,\"awardId\":520,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9913, 50, 10030, 10, 50, NULL, '今日累计充值128元', NULL, 128, '[{\"item\":60,\"awardId\":10180,\"num\":12},{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10030,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9914, 50, 10030, 10, 50, NULL, '今日累计充值256元', NULL, 256, '[{\"item\":60,\"awardId\":10180,\"num\":12},{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10030,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9915, 50, 10030, 10, 50, NULL, '今日累计充值384元', NULL, 384, '[{\"item\":60,\"awardId\":10180,\"num\":12},{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10030,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9916, 50, 10030, 10, 50, NULL, '今日累计充值512元', NULL, 512, '[{\"item\":60,\"awardId\":10180,\"num\":12},{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10030,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9917, 50, 10030, 10, 50, NULL, '今日累计充值640元', NULL, 640, '[{\"item\":60,\"awardId\":10180,\"num\":12},{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10030,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9918, 50, 10030, 10, 50, NULL, '今日累计充值768元', NULL, 768, '[{\"item\":60,\"awardId\":10180,\"num\":12},{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10030,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9919, 50, 10035, 10, 50, NULL, '今日累计充值6元', NULL, 6, '[{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":30,\"num\":90},{\"item\":60,\"awardId\":20,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9920, 50, 10035, 10, 50, NULL, '今日累计充值18元', NULL, 18, '[{\"item\":60,\"awardId\":10110,\"num\":1},{\"item\":40,\"star\":4,\"num\":1},{\"item\":60,\"awardId\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9921, 50, 10035, 10, 50, NULL, '今日累计充值30元', NULL, 30, '[{\"item\":60,\"awardId\":10110,\"num\":1},{\"item\":60,\"awardId\":20,\"num\":1},{\"item\":60,\"awardId\":10,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9922, 50, 10035, 10, 50, NULL, '今日累计充值68元', NULL, 68, '[{\"item\":60,\"awardId\":12005,\"num\":1},{\"item\":60,\"awardId\":580,\"num\":3},{\"item\":60,\"awardId\":520,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9923, 50, 10035, 10, 50, NULL, '今日累计充值198元', NULL, 198, '[{\"item\":60,\"awardId\":12005,\"num\":1},{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10030,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9924, 50, 10035, 10, 50, NULL, '今日累计充值328元', NULL, 328, '[{\"item\":60,\"awardId\":12005,\"num\":1},{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10030,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9925, 50, 10035, 10, 50, NULL, '今日累计充值648元', NULL, 648, '[{\"item\":60,\"awardId\":12005,\"num\":3},{\"item\":60,\"awardId\":11010,\"num\":10},{\"item\":60,\"awardId\":10030,\"num\":6}]', b'1');
INSERT INTO `cfg_activity` VALUES (9926, 50, 10035, 10, 50, NULL, '今日累计充值1000元', NULL, 1000, '[{\"item\":60,\"awardId\":12006,\"num\":1},{\"item\":60,\"awardId\":11010,\"num\":10},{\"item\":60,\"awardId\":10030,\"num\":6}]', b'1');
INSERT INTO `cfg_activity` VALUES (9927, 50, 10040, 10, 60, NULL, '节日累计充值1388元', NULL, 1388, '[{\"item\":60,\"awardId\":12006,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9928, 50, 10050, 10, 40, NULL, '节日兑换', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (9929, 50, 10060, 10, 30, NULL, '节日特殊野怪', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (10001, 70, 10001, 10, 20, NULL, '英雄回归-回归大礼', NULL, 0, '[{\"item\":10,\"num\":100},{\"awardId\":60,\"item\":60,\"num\":2},{\"awardId\":80,\"item\":60,\"num\":1},{\"awardId\":50,\"item\":60,\"num\":1},{\"awardId\":520,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10002, 70, 10002, 10, 20, NULL, '英雄回归-归来签到1', NULL, 1, '[{\"item\":20,\"num\":500000}]', b'1');
INSERT INTO `cfg_activity` VALUES (10003, 70, 10002, 10, 20, NULL, '英雄回归-归来签到2', NULL, 2, '[{\"awardId\":80,\"item\":60,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (10004, 70, 10002, 10, 20, NULL, '英雄回归-归来签到3', NULL, 3, '[{\"awardId\":10110,\"item\":60,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (10005, 70, 10002, 10, 20, NULL, '英雄回归-归来签到4', NULL, 4, '[{\"awardId\":30,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10006, 70, 10002, 10, 20, NULL, '英雄回归-归来签到5', NULL, 5, '[{\"item\":10,\"num\":200}]', b'1');
INSERT INTO `cfg_activity` VALUES (10007, 70, 10002, 10, 20, NULL, '英雄回归-归来签到6', NULL, 6, '[{\"awardId\":10,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10008, 70, 10002, 10, 20, NULL, '英雄回归-归来签到7', NULL, 7, '[{\"awardId\":106,\"item\":40,\"num\":1},{\"awardId\":205,\"item\":40,\"num\":1},{\"awardId\":304,\"item\":40,\"num\":1},{\"awardId\":406,\"item\":40,\"num\":1},{\"awardId\":504,\"item\":40,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10009, 70, 10003, 10, 20, NULL, '英雄回归-重拾荣光', NULL, 0, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (10010, 70, 10004, 10, 20, NULL, '英雄回归-特惠福利', NULL, 1, '[{\"awardId\":10110,\"item\":60,\"num\":9},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":500000}]', b'1');
INSERT INTO `cfg_activity` VALUES (10053, 80, 11010, 10, 1, NULL, '奖券', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (10054, 80, 11020, 10, 2, NULL, '夺宝', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (10055, 90, 11030, 10, 4, NULL, '新手礼包', NULL, 0, '[{\"item\":60,\"awardId\":11190,\"num\":1},{\"item\":60,\"awardId\":20,\"num\":1},{\"item\":20,\"num\":200000},{\"item\":60,\"awardId\":10020,\"num\":15},{\"item\":30,\"num\":180}]', b'1');
INSERT INTO `cfg_activity` VALUES (10056, 90, 11030, 10, 4, NULL, '萌新礼包', NULL, 0, '[{\"num\": 1, \"item\": 60, \"awardId\": 11180},{\"num\": 1, \"item\": 60, \"awardId\": 60},{\"num\": 50000, \"item\": 20},{\"num\": 5, \"item\": 60, \"awardId\": 10020},{\"num\": 20, \"item\": 30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10057, 90, 11040, 10, 5, NULL, '仙人垂青', NULL, 0, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (10061, 60, 31, 10, 7, NULL, '多日累计充值12元', NULL, 12, '[{\"item\":60,\"awardId\":580,\"num\":1},{\"item\":60,\"awardId\":110,\"num\":1},{\"item\":60,\"awardId\":120,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (10062, 60, 31, 10, 7, NULL, '多日累计充值36元', NULL, 36, '[{\"item\":60,\"awardId\":10110,\"num\":2},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10063, 60, 31, 10, 7, NULL, '多日累计充值68元', NULL, 68, '[{\"item\":60,\"awardId\":10110,\"num\":2},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10064, 60, 31, 10, 7, NULL, '多日累计充值100元', NULL, 100, '[{\"item\":60,\"awardId\":10110,\"num\":2},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10065, 60, 31, 10, 7, NULL, '多日累计充值130元', '', 130, '[{\"item\":60,\"awardId\":10110,\"num\":2},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10066, 60, 31, 10, 7, NULL, '多日累计充值160元', NULL, 160, '[{\"item\":60,\"awardId\":10110,\"num\":2},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10067, 60, 31, 10, 7, NULL, '多日累计充值260元', NULL, 260, '[{\"item\":60,\"awardId\":10110,\"num\":6},{\"item\":60,\"awardId\":10180,\"num\":9},{\"item\":60,\"awardId\":10020,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10068, 60, 31, 10, 7, NULL, '多日累计充值360元', NULL, 360, '[{\"item\":60,\"awardId\":10110,\"num\":6},{\"item\":60,\"awardId\":10180,\"num\":9},{\"item\":60,\"awardId\":10020,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10069, 60, 31, 10, 7, NULL, '多日累计充值460元', NULL, 460, '[{\"item\":60,\"awardId\":10110,\"num\":6},{\"item\":60,\"awardId\":10180,\"num\":9},{\"item\":60,\"awardId\":10020,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10070, 60, 31, 10, 7, NULL, '多日累计充值600元', NULL, 600, '[{\"item\":60,\"awardId\":10110,\"num\":9},{\"item\":60,\"awardId\":10180,\"num\":14},{\"item\":60,\"awardId\":10020,\"num\":45}]', b'1');
INSERT INTO `cfg_activity` VALUES (10071, 60, 31, 10, 7, NULL, '多日累计充值800元', NULL, 800, '[{\"item\":60,\"awardId\":10110,\"num\":13},{\"item\":60,\"awardId\":10180,\"num\":20},{\"item\":60,\"awardId\":10020,\"num\":65}]', b'1');
INSERT INTO `cfg_activity` VALUES (10072, 60, 31, 10, 7, NULL, '多日累计充值1000元', NULL, 1000, '[{\"item\":60,\"awardId\":10110,\"num\":13},{\"item\":60,\"awardId\":10180,\"num\":20},{\"item\":60,\"awardId\":10020,\"num\":65}]', b'1');
INSERT INTO `cfg_activity` VALUES (10073, 60, 31, 10, 7, NULL, '多日累计充值1200元', NULL, 1200, '[{\"item\":60,\"awardId\":10110,\"num\":13},{\"item\":60,\"awardId\":10180,\"num\":20},{\"item\":60,\"awardId\":10020,\"num\":65}]', b'1');
INSERT INTO `cfg_activity` VALUES (10074, 60, 31, 10, 7, NULL, '多日累计充值1500元', NULL, 1500, '[{\"item\":60,\"awardId\":10110,\"num\":19},{\"item\":60,\"awardId\":10180,\"num\":29},{\"item\":60,\"awardId\":10020,\"num\":95}]', b'1');
INSERT INTO `cfg_activity` VALUES (10075, 60, 31, 10, 7, NULL, '多日累计充值2000元', NULL, 2000, '[{\"item\":60,\"awardId\":10110,\"num\":32},{\"item\":60,\"awardId\":10180,\"num\":48},{\"item\":60,\"awardId\":10020,\"num\":160}]', b'1');
INSERT INTO `cfg_activity` VALUES (10076, 60, 31, 10, 7, NULL, '多日累计充值3000元', NULL, 3000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20220,\"num\":1},{\"item\":60,\"awardId\":20120,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10077, 60, 31, 10, 7, NULL, '多日累计充值4000元', NULL, 4000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20220,\"num\":1},{\"item\":60,\"awardId\":20120,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10078, 60, 31, 10, 7, NULL, '多日累计充值5000元', NULL, 5000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20230,\"num\":1},{\"item\":60,\"awardId\":20130,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10079, 60, 31, 10, 7, NULL, '多日累计充值7000元', NULL, 7000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20230,\"num\":1},{\"item\":60,\"awardId\":20130,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10080, 60, 31, 10, 7, NULL, '多日累计充值10000元', NULL, 10000, '[{\"item\":60,\"awardId\":11065,\"num\":1},{\"item\":60,\"awardId\":20240,\"num\":1},{\"item\":60,\"awardId\":20140,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10081, 60, 32, 10, 7, NULL, '多日累计充值12元', NULL, 12, '[{\"item\":60,\"awardId\":580,\"num\":1},{\"item\":60,\"awardId\":510,\"num\":1},{\"item\":60,\"awardId\":120,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (10082, 60, 32, 10, 7, NULL, '多日累计充值36元', NULL, 36, '[{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10083, 60, 32, 10, 7, NULL, '多日累计充值68元', NULL, 68, '[{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10084, 60, 32, 10, 7, NULL, '多日累计充值100元', NULL, 100, '[{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10085, 60, 32, 10, 7, NULL, '多日累计充值130元', '', 130, '[{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10086, 60, 32, 10, 7, NULL, '多日累计充值160元', NULL, 160, '[{\"item\":60,\"awardId\":11010,\"num\":5},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10087, 60, 32, 10, 7, NULL, '多日累计充值260元', NULL, 260, '[{\"item\":60,\"awardId\":11010,\"num\":15},{\"item\":60,\"awardId\":10180,\"num\":9},{\"item\":60,\"awardId\":10020,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10088, 60, 32, 10, 7, NULL, '多日累计充值360元', NULL, 360, '[{\"item\":60,\"awardId\":11010,\"num\":15},{\"item\":60,\"awardId\":10180,\"num\":9},{\"item\":60,\"awardId\":10020,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10089, 60, 32, 10, 7, NULL, '多日累计充值460元', NULL, 460, '[{\"item\":60,\"awardId\":11010,\"num\":15},{\"item\":60,\"awardId\":10180,\"num\":9},{\"item\":60,\"awardId\":10020,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10090, 60, 32, 10, 7, NULL, '多日累计充值600元', NULL, 600, '[{\"item\":60,\"awardId\":11010,\"num\":23},{\"item\":60,\"awardId\":10180,\"num\":14},{\"item\":60,\"awardId\":10020,\"num\":45}]', b'1');
INSERT INTO `cfg_activity` VALUES (10091, 60, 32, 10, 7, NULL, '多日累计充值800元', NULL, 800, '[{\"item\":60,\"awardId\":11010,\"num\":33},{\"item\":60,\"awardId\":10180,\"num\":20},{\"item\":60,\"awardId\":10020,\"num\":65}]', b'1');
INSERT INTO `cfg_activity` VALUES (10092, 60, 32, 10, 7, NULL, '多日累计充值1000元', NULL, 1000, '[{\"item\":60,\"awardId\":11010,\"num\":33},{\"item\":60,\"awardId\":10180,\"num\":20},{\"item\":60,\"awardId\":10020,\"num\":65}]', b'1');
INSERT INTO `cfg_activity` VALUES (10093, 60, 32, 10, 7, NULL, '多日累计充值1200元', NULL, 1200, '[{\"item\":60,\"awardId\":11010,\"num\":33},{\"item\":60,\"awardId\":10180,\"num\":20},{\"item\":60,\"awardId\":10020,\"num\":65}]', b'1');
INSERT INTO `cfg_activity` VALUES (10094, 60, 32, 10, 7, NULL, '多日累计充值1500元', NULL, 1500, '[{\"item\":60,\"awardId\":11010,\"num\":47},{\"item\":60,\"awardId\":10180,\"num\":29},{\"item\":60,\"awardId\":10020,\"num\":95}]', b'1');
INSERT INTO `cfg_activity` VALUES (10095, 60, 32, 10, 7, NULL, '多日累计充值2000元', NULL, 2000, '[{\"item\":60,\"awardId\":11010,\"num\":80},{\"item\":60,\"awardId\":10180,\"num\":48},{\"item\":60,\"awardId\":10020,\"num\":160}]', b'1');
INSERT INTO `cfg_activity` VALUES (10096, 60, 32, 10, 7, NULL, '多日累计充值3000元', NULL, 3000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20220,\"num\":1},{\"item\":60,\"awardId\":20120,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10097, 60, 32, 10, 7, NULL, '多日累计充值4000元', NULL, 4000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20220,\"num\":1},{\"item\":60,\"awardId\":20120,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10098, 60, 32, 10, 7, NULL, '多日累计充值5000元', NULL, 5000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20230,\"num\":1},{\"item\":60,\"awardId\":20130,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10099, 60, 32, 10, 7, NULL, '多日累计充值7000元', NULL, 7000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20230,\"num\":1},{\"item\":60,\"awardId\":20130,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10100, 60, 32, 10, 7, NULL, '多日累计充值10000元', NULL, 10000, '[{\"item\":60,\"awardId\":11065,\"num\":1},{\"item\":60,\"awardId\":20240,\"num\":1},{\"item\":60,\"awardId\":20140,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10101, 60, 33, 10, 7, NULL, '多日累计充值12元', NULL, 12, '[{\"item\":60,\"awardId\":580,\"num\":1},{\"item\":60,\"awardId\":510,\"num\":1},{\"item\":60,\"awardId\":120,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (10102, 60, 33, 10, 7, NULL, '多日累计充值36元', NULL, 36, '[{\"item\":60,\"awardId\":11090,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10103, 60, 33, 10, 7, NULL, '多日累计充值68元', NULL, 68, '[{\"item\":60,\"awardId\":11090,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10104, 60, 33, 10, 7, NULL, '多日累计充值100元', '', 100, '[{\"item\":60,\"awardId\":11090,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10105, 60, 33, 10, 7, NULL, '多日累计充值130元', '', 130, '[{\"item\":60,\"awardId\":11090,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10106, 60, 33, 10, 7, NULL, '多日累计充值160元', NULL, 160, '[{\"item\":60,\"awardId\":11090,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":3},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10107, 60, 33, 10, 7, NULL, '多日累计充值260元', NULL, 260, '[{\"item\":60,\"awardId\":11090,\"num\":9},{\"item\":60,\"awardId\":10180,\"num\":9},{\"item\":60,\"awardId\":10020,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10108, 60, 33, 10, 7, NULL, '多日累计充值360元', NULL, 360, '[{\"item\":60,\"awardId\":11090,\"num\":9},{\"item\":60,\"awardId\":10180,\"num\":9},{\"item\":60,\"awardId\":10020,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10109, 60, 33, 10, 7, NULL, '多日累计充值460元', '', 460, '[{\"item\":60,\"awardId\":11090,\"num\":9},{\"item\":60,\"awardId\":10180,\"num\":9},{\"item\":60,\"awardId\":10020,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (10110, 60, 33, 10, 7, NULL, '多日累计充值600元', NULL, 600, '[{\"item\":60,\"awardId\":11090,\"num\":14},{\"item\":60,\"awardId\":10180,\"num\":14},{\"item\":60,\"awardId\":10020,\"num\":45}]', b'1');
INSERT INTO `cfg_activity` VALUES (10111, 60, 33, 10, 7, NULL, '多日累计充值800元', NULL, 800, '[{\"item\":60,\"awardId\":11090,\"num\":20},{\"item\":60,\"awardId\":10180,\"num\":20},{\"item\":60,\"awardId\":10020,\"num\":65}]', b'1');
INSERT INTO `cfg_activity` VALUES (10112, 60, 33, 10, 7, NULL, '多日累计充值1000元', NULL, 1000, '[{\"item\":60,\"awardId\":11090,\"num\":20},{\"item\":60,\"awardId\":10180,\"num\":20},{\"item\":60,\"awardId\":10020,\"num\":65}]', b'1');
INSERT INTO `cfg_activity` VALUES (10113, 60, 33, 10, 7, NULL, '多日累计充值1200元', NULL, 1200, '[{\"item\":60,\"awardId\":11090,\"num\":20},{\"item\":60,\"awardId\":10180,\"num\":20},{\"item\":60,\"awardId\":10020,\"num\":65}]', b'1');
INSERT INTO `cfg_activity` VALUES (10114, 60, 33, 10, 7, NULL, '多日累计充值1500元', NULL, 1500, '[{\"item\":60,\"awardId\":11090,\"num\":29},{\"item\":60,\"awardId\":10180,\"num\":29},{\"item\":60,\"awardId\":10020,\"num\":95}]', b'1');
INSERT INTO `cfg_activity` VALUES (10115, 60, 33, 10, 7, NULL, '多日累计充值2000元', NULL, 2000, '[{\"item\":60,\"awardId\":11090,\"num\":48},{\"item\":60,\"awardId\":10180,\"num\":48},{\"item\":60,\"awardId\":10020,\"num\":160}]', b'1');
INSERT INTO `cfg_activity` VALUES (10116, 60, 33, 10, 7, NULL, '多日累计充值3000元', NULL, 3000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20220,\"num\":1},{\"item\":60,\"awardId\":20120,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10117, 60, 33, 10, 7, NULL, '多日累计充值4000元', NULL, 4000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20220,\"num\":1},{\"item\":60,\"awardId\":20120,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10118, 60, 33, 10, 7, NULL, '多日累计充值5000元', NULL, 5000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20230,\"num\":1},{\"item\":60,\"awardId\":20130,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10119, 60, 33, 10, 7, NULL, '多日累计充值7000元', NULL, 7000, '[{\"item\":60,\"awardId\":11060,\"num\":1},{\"item\":60,\"awardId\":20230,\"num\":1},{\"item\":60,\"awardId\":20130,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10120, 60, 33, 10, 7, NULL, '多日累计充值10000元', NULL, 10000, '[{\"item\":60,\"awardId\":11065,\"num\":1},{\"item\":60,\"awardId\":20240,\"num\":1},{\"item\":60,\"awardId\":20140,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10121, 60, 34, 10, 6, NULL, '周日充值6元', NULL, 6, '[{\"item\":60,\"awardId\":520,\"num\":1},{\"item\":60,\"awardId\":110,\"num\":1},{\"item\":60,\"awardId\":120,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (10122, 60, 34, 10, 6, NULL, '周日充值30元', NULL, 30, '[{\"item\":10,\"num\":150},{\"item\":60,\"awardId\":20,\"num\":1},{\"item\":60,\"awardId\":10020,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (10123, 60, 34, 10, 6, NULL, '周日充值36元', NULL, 36, '[{\"item\":10,\"num\":150},{\"item\":60,\"awardId\":11100,\"num\":1},{\"item\":60,\"awardId\":10180,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (10124, 60, 34, 10, 6, NULL, '周日充值66元', NULL, 66, '[{\"item\":10,\"num\":150},{\"item\":60,\"awardId\":11010,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (10125, 60, 34, 10, 6, NULL, '周日充值100元', NULL, 100, '[{\"item\":10,\"num\":150},{\"item\":60,\"awardId\":11100,\"num\":1},{\"item\":60,\"awardId\":10180,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (10126, 60, 34, 10, 6, NULL, '周日充值130元', NULL, 130, '[{\"item\":10,\"num\":150},{\"item\":60,\"awardId\":11010,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (10127, 60, 34, 10, 6, NULL, '周日充值160元', NULL, 160, '[{\"item\":10,\"num\":150},{\"item\":60,\"awardId\":11100,\"num\":1},{\"item\":60,\"awardId\":10180,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (10128, 60, 34, 10, 6, NULL, '周日充值190元', NULL, 190, '[{\"item\":10,\"num\":150},{\"item\":60,\"awardId\":11010,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (10129, 60, 34, 10, 6, NULL, '周日充值230元', NULL, 230, '[{\"item\":10,\"num\":200},{\"item\":60,\"awardId\":11100,\"num\":2},{\"item\":60,\"awardId\":10180,\"num\":3}]', b'1');

SET FOREIGN_KEY_CHECKS = 1;
