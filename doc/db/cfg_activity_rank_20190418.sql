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

 Date: 18/04/2019 16:23:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cfg_activity_rank
-- ----------------------------
DROP TABLE IF EXISTS `cfg_activity_rank`;
CREATE TABLE `cfg_activity_rank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `serial` int(11) DEFAULT NULL,
  `scope` int(11) NOT NULL DEFAULT '20',
  `type` int(11) NOT NULL COMMENT '10000 充值排行  10010 富豪榜   10020 元宝消耗榜   10030 铜钱消耗榜  10040 体力消耗榜   10050 元素消耗榜   10060 战斗宝箱榜	10070 攻城榜 10080 玩家排行榜 10090 远征榜',
  `name` varchar(50) NOT NULL,
  `awards` varchar(1000) DEFAULT NULL COMMENT '奖励',
  `min_rank` int(11) DEFAULT NULL,
  `max_rank` int(11) DEFAULT NULL,
  `min_value` varchar(100) DEFAULT NULL,
  `status` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10946 DEFAULT CHARSET=utf8mb4 COMMENT='活动';

-- ----------------------------
-- Records of cfg_activity_rank
-- ----------------------------
BEGIN;
INSERT INTO `cfg_activity_rank` VALUES (10010, 10000, 20, 10000, '充值排行第1名', '[{\"awardId\":10010,\"item\":60,\"num\":10},{\"awardId\":850,\"item\":60,\"num\":6},{\"awardId\":10020,\"item\":60,\"num\":100},{\"item\":20,\"num\":2000000}]', 1, 1, '3000', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10015, 10000, 20, 10000, '充值排行第2名', '[{\"awardId\":10010,\"item\":60,\"num\":8},{\"awardId\":850,\"item\":60,\"num\":4},{\"awardId\":10020,\"item\":60,\"num\":80},{\"item\":20,\"num\":1600000}]', 2, 2, '1000', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10020, 10000, 20, 10000, '充值排行第3名', '[{\"awardId\":10010,\"item\":60,\"num\":6},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 3, 3, '1000', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10025, 10000, 20, 10000, '充值排行第4-10名', '[{\"item\":40,\"star\":4,\"num\":1},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 4, 10, '300', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10030, 10000, 20, 10000, '充值排行第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10035, 10000, 20, 10000, '充值排行第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10040, 10000, 20, 10000, '充值排行第51-100名', '[{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10045, 10000, 20, 10000, '充值排行第101-200名', '[{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10110, 10010, 20, 10010, '富豪榜第1名', '[{\"awardId\":10010,\"item\":60,\"num\":8},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":80},{\"item\":20,\"num\":1600000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10115, 10010, 20, 10010, '富豪榜第2名', '[{\"awardId\":10010,\"item\":60,\"num\":6},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10120, 10010, 20, 10010, '富豪榜第3名', '[{\"item\":40,\"star\":4,\"num\":1},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":1000000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10125, 10010, 20, 10010, '富豪榜第4-10名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10130, 10010, 20, 10010, '富豪榜第11-20名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10135, 10010, 20, 10010, '富豪榜第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10140, 10010, 20, 10010, '富豪榜第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10145, 10010, 20, 10010, '富豪榜第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10210, 10020, 20, 10020, '元宝消耗第1名', '[{\"awardId\":10010,\"item\":60,\"num\":10},{\"awardId\":850,\"item\":60,\"num\":10},{\"awardId\":10,\"item\":50,\"num\":50},{\"awardId\":20,\"item\":50,\"num\":50},{\"awardId\":30,\"item\":50,\"num\":50},{\"awardId\":40,\"item\":50,\"num\":50},{\"awardId\":50,\"item\":50,\"num\":50}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10215, 10020, 20, 10020, '元宝消耗第2名', '[{\"awardId\":10010,\"item\":60,\"num\":8},{\"awardId\":850,\"item\":60,\"num\":8},{\"awardId\":10,\"item\":50,\"num\":30},{\"awardId\":20,\"item\":50,\"num\":30},{\"awardId\":30,\"item\":50,\"num\":30},{\"awardId\":40,\"item\":50,\"num\":30},{\"awardId\":50,\"item\":50,\"num\":30}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10220, 10020, 20, 10020, '元宝消耗第3名', '[{\"awardId\":10010,\"item\":60,\"num\":6},{\"awardId\":850,\"item\":60,\"num\":5},{\"awardId\":10,\"item\":50,\"num\":20},{\"awardId\":20,\"item\":50,\"num\":20},{\"awardId\":30,\"item\":50,\"num\":20},{\"awardId\":40,\"item\":50,\"num\":20},{\"awardId\":50,\"item\":50,\"num\":20}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10225, 10020, 20, 10020, '元宝消耗第4-10名', '[{\"item\":40,\"star\":4,\"num\":1},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10,\"item\":50,\"num\":15},{\"awardId\":20,\"item\":50,\"num\":15},{\"awardId\":30,\"item\":50,\"num\":15},{\"awardId\":40,\"item\":50,\"num\":15},{\"awardId\":50,\"item\":50,\"num\":15}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10230, 10020, 20, 10020, '元宝消耗第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10,\"item\":50,\"num\":10},{\"awardId\":20,\"item\":50,\"num\":10},{\"awardId\":30,\"item\":50,\"num\":10},{\"awardId\":40,\"item\":50,\"num\":10},{\"awardId\":50,\"item\":50,\"num\":10}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10235, 10020, 20, 10020, '元宝消耗第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":5},{\"awardId\":10,\"item\":50,\"num\":8},{\"awardId\":20,\"item\":50,\"num\":8},{\"awardId\":30,\"item\":50,\"num\":8},{\"awardId\":40,\"item\":50,\"num\":8},{\"awardId\":50,\"item\":50,\"num\":8}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10240, 10020, 20, 10020, '元宝消耗第51-100名', '[{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10,\"item\":50,\"num\":5},{\"awardId\":20,\"item\":50,\"num\":5},{\"awardId\":30,\"item\":50,\"num\":5},{\"awardId\":40,\"item\":50,\"num\":5},{\"awardId\":50,\"item\":50,\"num\":5}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10245, 10020, 20, 10020, '元宝消耗第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10,\"item\":50,\"num\":3},{\"awardId\":20,\"item\":50,\"num\":3},{\"awardId\":30,\"item\":50,\"num\":3},{\"awardId\":40,\"item\":50,\"num\":3},{\"awardId\":50,\"item\":50,\"num\":3}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10510, 10050, 20, 10050, '元素消耗第1名', '[{\"awardId\":10010,\"item\":60,\"num\":4},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 1, 1, '500', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10515, 10050, 20, 10050, '元素消耗第2名', '[{\"awardId\":10010,\"item\":60,\"num\":3},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 2, 2, '200', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10520, 10050, 20, 10050, '元素消耗第3名', '[{\"awardId\":10010,\"item\":60,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 3, 3, '200', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10525, 10050, 20, 10050, '元素消耗第4-10名', '[{\"item\":40,\"star\":3,\"num\":3},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 4, 10, '30', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10530, 10050, 20, 10050, '元素消耗第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10535, 10050, 20, 10050, '元素消耗第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10540, 10050, 20, 10050, '元素消耗第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10545, 10050, 20, 10050, '元素消耗第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":5},{\"item\":20,\"num\":100000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10610, 10060, 20, 10060, '胜利宝箱第1名', '[{\"awardId\":10010,\"item\":60,\"num\":4},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10615, 10060, 20, 10060, '胜利宝箱第2名', '[{\"awardId\":10010,\"item\":60,\"num\":3},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10620, 10060, 20, 10060, '胜利宝箱第3名', '[{\"awardId\":10010,\"item\":60,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10625, 10060, 20, 10060, '胜利宝箱第4-10名', '[{\"item\":40,\"star\":3,\"num\":3},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10630, 10060, 20, 10060, '胜利宝箱第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10635, 10060, 20, 10060, '胜利宝箱第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10640, 10060, 20, 10060, '胜利宝箱第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10645, 10060, 20, 10060, '胜利宝箱第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":5},{\"item\":20,\"num\":100000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10710, 10070, 20, 10070, '攻城榜第1名', '[{\"awardId\":10010,\"item\":60,\"num\":10},{\"awardId\":850,\"item\":60,\"num\":6},{\"awardId\":10020,\"item\":60,\"num\":100},{\"item\":20,\"num\":2000000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10715, 10070, 20, 10070, '攻城榜第2名', '[{\"awardId\":10010,\"item\":60,\"num\":8},{\"awardId\":850,\"item\":60,\"num\":4},{\"awardId\":10020,\"item\":60,\"num\":80},{\"item\":20,\"num\":1600000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10720, 10070, 20, 10070, '攻城榜第3名', '[{\"awardId\":10010,\"item\":60,\"num\":6},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10725, 10070, 20, 10070, '攻城榜第4-10名', '[{\"item\":40,\"star\":4,\"num\":1},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10730, 10070, 20, 10070, '攻城榜第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10735, 10070, 20, 10070, '攻城榜第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10740, 10070, 20, 10070, '攻城榜第51-100名', '[{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10745, 10070, 20, 10070, '攻城榜第101-200名', '[{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10810, 10080, 20, 10080, '玩家等级榜第1名', '[{\"awardId\":10010,\"item\":60,\"num\":4},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10815, 10080, 20, 10080, '玩家等级榜第2名', '[{\"awardId\":10010,\"item\":60,\"num\":3},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10820, 10080, 20, 10080, '玩家等级榜第3名', '[{\"awardId\":10010,\"item\":60,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10825, 10080, 20, 10080, '玩家等级榜第4-10名', '[{\"item\":40,\"star\":3,\"num\":3},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10830, 10080, 20, 10080, '玩家等级榜第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10835, 10080, 20, 10080, '玩家等级榜第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10840, 10080, 20, 10080, '玩家等级榜第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10845, 10080, 20, 10080, '玩家等级榜第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":5},{\"item\":20,\"num\":100000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10910, 10090, 20, 10090, '远征榜第1名', '[{\"awardId\":10010,\"item\":60,\"num\":4},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10915, 10090, 20, 10090, '远征榜第2名', '[{\"awardId\":10010,\"item\":60,\"num\":3},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10920, 10090, 20, 10090, '远征榜第3名', '[{\"awardId\":10010,\"item\":60,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10925, 10090, 20, 10090, '远征榜第4-10名', '[{\"item\":40,\"star\":3,\"num\":3},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10930, 10090, 20, 10090, '远征榜第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10935, 10090, 20, 10090, '远征榜第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10940, 10090, 20, 10090, '远征榜第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10945, 10090, 20, 10090, '远征榜第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":5},{\"item\":20,\"num\":100000}]', 101, 200, '0', b'1');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
