/*
 Navicat Premium Data Transfer

 Source Server         : 刘少军的MAC
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : localhost:3306
 Source Schema         : god_game

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : 65001

 Date: 09/04/2019 16:30:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cfg_achievement
-- ----------------------------
DROP TABLE IF EXISTS `cfg_achievement`;
CREATE TABLE `cfg_achievement` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `serial` int(11) DEFAULT NULL,
  `series` int(11) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `details` varchar(1000) NOT NULL,
  `value` int(11) unsigned NOT NULL COMMENT '成就达成条件，用于判定成就完成与否',
  `award` varchar(200) NOT NULL COMMENT '奖励json串',
  `is_valid` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效 1有效 0无效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=751 DEFAULT CHARSET=utf8mb4 COMMENT='成就\nvalue:成就达成的条件值，用于判定成就完成与否';

-- ----------------------------
-- Records of cfg_achievement
-- ----------------------------
BEGIN;
INSERT INTO `cfg_achievement` VALUES (10, 10, NULL, '小有名气', '玩家等级达到10级', 10, '[{\"num\":20,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (20, 10, NULL, '街坊传名', '玩家等级达到20级', 20, '[{\"num\":40,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (30, 10, NULL, '龙将之才', '玩家等级达到30级', 30, '[{\"num\":60,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (40, 10, NULL, '威名远扬', '玩家等级达到40级', 40, '[{\"num\":100,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (50, 10, NULL, '主帅风范', '玩家等级达到50级', 50, '[{\"num\":2,\"awardId\":260,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (55, 10, NULL, '帝王之相', '玩家等级达到60级', 60, '[{\"num\":3,\"awardId\":210,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (60, 10, NULL, '世界英雄', '玩家等级达到70级', 70, '[{\"num\":1,\"awardId\":425,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (110, 20, NULL, '爱才如命', '收集卡牌达到10张', 10, '[{\"num\":20,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (120, 20, NULL, '广纳贤能', '收集卡牌达到50张', 50, '[{\"num\":50,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (130, 20, NULL, '封神传奇', '收集卡牌达到160张', 160, '[{\"num\":1,\"awardId\":525,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (135, 30, NULL, '师之尊者', '集齐3张一阶韦护、韩毒龙和薛恶虎', 1, '[{\"num\":\"1\",\"awardId\":\"132\",\"item\":\"40\"}]', b'1');
INSERT INTO `cfg_achievement` VALUES (140, 40, NULL, '金之尊者', '收集20张以上的金属性卡牌', 20, '[{\"num\":1,\"awardId\":108,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (150, 50, NULL, '木之尊者', '收集20张以上的木属性卡牌', 20, '[{\"num\":1,\"awardId\":204,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (160, 60, NULL, '水之尊者', '收集20张以上的水属性卡牌', 20, '[{\"num\":1,\"awardId\":307,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (170, 70, NULL, '火之尊者', '收集20张以上的火属性卡牌', 20, '[{\"num\":1,\"awardId\":403,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (180, 80, NULL, '土之尊者', '收集20张以上的土属性卡牌', 20, '[{\"num\":1,\"awardId\":503,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (210, 90, NULL, '爱才育才', '累计消耗100个元素升级卡牌', 100, '[{\"num\":50,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (220, 100, NULL, '金之术士', '累计消耗100个金元素升级卡牌', 100, '[{\"num\":1,\"awardId\":114,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (230, 110, NULL, '木之术士', '累计消耗100个木元素升级卡牌', 100, '[{\"num\":1,\"awardId\":209,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (240, 120, NULL, '水之术士', '累计消耗100个水元素升级卡牌', 100, '[{\"num\":1,\"awardId\":309,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (250, 130, NULL, '火之术士', '累计消耗100个火元素升级卡牌', 100, '[{\"num\":1,\"awardId\":412,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (260, 140, NULL, '土之术士', '累计消耗100个土元素升级卡牌', 100, '[{\"num\":1,\"awardId\":511,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (310, 150, NULL, '初试商道', '累计卖出100件特产', 100, '[{\"num\":50,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (320, 160, NULL, '小积财富', '累计铜钱达到10万', 100000, '[{\"num\":30,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (330, 160, NULL, '守财有方', '累计铜钱达到100万', 1000000, '[{\"num\":6,\"awardId\":19,\"item\":70}]', b'1');
INSERT INTO `cfg_achievement` VALUES (333, 160, NULL, '腰缠万贯', '累计铜钱达到500万', 5000000, '[{\"num\":200,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (336, 160, NULL, '富可敌国', '累计铜钱达到1000万', 10000000, '[{\"num\":300,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (410, 170, NULL, '高屋建瓴', '盖满一座城的所有建筑', 1, '[{\"num\":3,\"awardId\":30,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (420, 170, NULL, '广厦万间', '建满所有城池的建筑', 85, '[{\"num\":1,\"awardId\":226,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (510, 200, NULL, '首战告捷', '攻下第一座城池', 1, '[{\"num\":1,\"awardId\":20,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (520, 210, NULL, '出师得利', '攻下所有的一级城', 25, '[{\"num\":5,\"awardId\":400,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (525, 220, NULL, '常胜将军', '攻下所有的二级城', 20, '[{\"num\":10,\"awardId\":160,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (530, 230, NULL, '所向披靡', '攻下所有的三级城', 20, '[{\"num\":6,\"awardId\":110,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (535, 240, NULL, '功高盖世', '攻下所有的四级城', 15, '[{\"num\":8,\"awardId\":120,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (536, 250, NULL, '势不可挡', '攻下所有的五级城', 5, '[{\"num\":10,\"awardId\":90,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (541, 260, NULL, '称雄西岐', '攻下西岐的所有城池', 17, '[{\"num\":500000,\"item\":20}]', b'1');
INSERT INTO `cfg_achievement` VALUES (542, 270, NULL, '称雄东鲁', '攻下东鲁的所有城池', 17, '[{\"num\":500000,\"item\":20}]', b'1');
INSERT INTO `cfg_achievement` VALUES (543, 280, NULL, '称雄北崇', '攻下北崇的所有城池', 17, '[{\"num\":500000,\"item\":20}]', b'1');
INSERT INTO `cfg_achievement` VALUES (544, 290, NULL, '称雄南都', '攻下南都的所有城池', 17, '[{\"num\":500000,\"item\":20}]', b'1');
INSERT INTO `cfg_achievement` VALUES (545, 300, NULL, '称雄殷商', '攻下殷商的所有城池', 17, '[{\"num\":500000,\"item\":20}]', b'1');
INSERT INTO `cfg_achievement` VALUES (550, 200, NULL, '一统天下', '打下所有城池统一世界', 85, '[{\"num\":1,\"awardId\":126,\"item\":40}]', b'1');
INSERT INTO `cfg_achievement` VALUES (610, 320, NULL, '小试牛刀', '获得第一场野怪战斗胜利', 1, '[{\"num\":20,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (620, 320, NULL, '为民除害', '获得100次野怪战斗胜利', 100, '[{\"num\":100,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (630, 330, NULL, '大义助友', '帮好友打胜30次怪', 30, '[{\"num\":20,\"item\":30}]', b'1');
INSERT INTO `cfg_achievement` VALUES (640, 340, NULL, '练兵有方', '获得100次练兵战斗胜利', 100, '[{\"num\":2,\"awardId\":270,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (650, 350, NULL, '催枯拉朽', '击败一个15级以上的召唤师且未损将', 1, '[{\"num\":2,\"awardId\":330,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (660, 360, NULL, '兵不血刃', '击败一个15级以上的召唤师且未损血', 1, '[{\"num\":5,\"awardId\":370,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (710, 370, NULL, '竞技天下', '获得10次竞技场战斗胜利', 10, '[{\"num\":40,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (720, 370, NULL, '打架之王', '获得100次竞技场战斗胜利', 100, '[{\"num\":200,\"item\":10}]', b'1');
INSERT INTO `cfg_achievement` VALUES (730, 380, NULL, '初试进阶', '第一次进阶卡牌', 1, '[{\"num\":3,\"awardId\":830,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (740, 380, NULL, '进阶新手', '累计进阶10次卡牌', 20, '[{\"num\":2,\"awardId\":840,\"item\":60}]', b'1');
INSERT INTO `cfg_achievement` VALUES (750, 380, NULL, '进阶大师', '累计进阶50次卡牌', 50, '[{\"num\":1,\"awardId\":850,\"item\":60}]', b'1');
COMMIT;

-- ----------------------------
-- Table structure for cfg_activity
-- ----------------------------
DROP TABLE IF EXISTS `cfg_activity`;
CREATE TABLE `cfg_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_type` int(11) NOT NULL,
  `type` int(11) NOT NULL COMMENT '10首冲，20累计充值，30连续登录，40累计登录，50补充体力，60邀请好友',
  `serial` int(11) DEFAULT NULL,
  `series` int(11) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `detail` varchar(500) DEFAULT NULL,
  `need_value` int(11) DEFAULT NULL,
  `awards` varchar(1000) DEFAULT NULL COMMENT '奖励',
  `status` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9402 DEFAULT CHARSET=utf8mb4 COMMENT='活动';

-- ----------------------------
-- Records of cfg_activity
-- ----------------------------
BEGIN;
INSERT INTO `cfg_activity` VALUES (1, 20, 10, 40, NULL, '首冲心动大礼', NULL, 1, '[{\"awardId\":131,\"item\":40,\"num\":1},{\"awardId\":160,\"item\":60,\"num\":3},{\"item\":50,\"awardId\":0,\"num\":10},{\"item\":20,\"num\":100000}]', b'1');
INSERT INTO `cfg_activity` VALUES (201, 10, 20, 22, NULL, '累计充值500元宝', NULL, 500, '[{\"awardId\":510,\"item\":60,\"num\":5}]', b'1');
INSERT INTO `cfg_activity` VALUES (202, 10, 20, 22, NULL, '累计充值1000元宝', NULL, 1000, '[{\"item\":40,\"star\":4,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (203, 10, 20, 22, NULL, '累计充值2000元宝', NULL, 2000, '[{\"item\":40,\"star\":4,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (204, 10, 20, 22, NULL, '累计充值5000元宝', NULL, 5000, '[{\"item\":40,\"star\":4,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (205, 10, 20, 22, NULL, '累计充值1万元宝', NULL, 10000, '[{\"item\":40,\"star\":5,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (206, 10, 20, 22, NULL, '累计充值2万元宝', NULL, 20000, '[{\"awardId\":850,\"item\":60,\"num\":5}]', b'1');
INSERT INTO `cfg_activity` VALUES (207, 10, 20, 22, NULL, '累计充值3万元宝', NULL, 30000, '[{\"awardId\":850,\"item\":60,\"num\":8}]', b'1');
INSERT INTO `cfg_activity` VALUES (208, 10, 20, 22, NULL, '累计充值5万元宝', NULL, 50000, '[{\"awardId\":527,\"item\":40,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (209, 10, 20, 22, NULL, '累计充值7万元宝', NULL, 70000, '[{\"awardId\":850,\"item\":60,\"num\":20}]', b'1');
INSERT INTO `cfg_activity` VALUES (210, 10, 20, 22, NULL, '累计充值10万元宝', NULL, 100000, '[{\"awardId\":850,\"item\":60,\"num\":40}]', b'1');
INSERT INTO `cfg_activity` VALUES (230, 10, 23, 21, NULL, '今日累充0元宝', NULL, 0, '[{\"item\":30,\"week\":1,\"num\":3,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":5000,\"isFhb\":0},{\"item\":30,\"week\":2,\"num\":3,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":5000,\"isFhb\":0},{\"item\":30,\"week\":0,\"num\":3,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":5000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (231, 10, 23, 21, NULL, '今日累充60元宝', NULL, 60, '[{\"awardId\":530,\"item\":60,\"week\":1,\"num\":1,\"isFhb\":0},{\"awardId\":60,\"item\":60,\"week\":1,\"num\":1,\"isFhb\":0},{\"awardId\":10040,\"item\":60,\"week\":1,\"num\":1,\"isFhb\":0},{\"awardId\":530,\"item\":60,\"week\":2,\"num\":1,\"isFhb\":0},{\"awardId\":60,\"item\":60,\"week\":2,\"num\":1,\"isFhb\":0},{\"awardId\":10040,\"item\":60,\"week\":2,\"num\":1,\"isFhb\":0},{\"awardId\":530,\"item\":60,\"week\":0,\"num\":1,\"isFhb\":0},{\"awardId\":60,\"item\":60,\"week\":0,\"num\":1,\"isFhb\":0},{\"awardId\":10040,\"item\":60,\"week\":0,\"num\":1,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (232, 10, 23, 21, NULL, '今日累充180元宝', NULL, 180, '[{\"awardId\":10,\"item\":60,\"week\":1,\"num\":1,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":1,\"num\":8,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":60000,\"isFhb\":0},{\"awardId\":10,\"item\":60,\"week\":2,\"num\":1,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":2,\"num\":10,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":120000,\"isFhb\":0},{\"awardId\":10,\"item\":60,\"week\":0,\"num\":1,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":0,\"num\":12,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":180000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (233, 10, 23, 21, NULL, '今日累充300元宝', NULL, 300, '[{\"awardId\":20,\"item\":60,\"week\":1,\"num\":1,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":1,\"num\":10,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":60000,\"isFhb\":0},{\"awardId\":20,\"item\":60,\"week\":2,\"num\":1,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":2,\"num\":12,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":120000,\"isFhb\":0},{\"awardId\":20,\"item\":60,\"week\":0,\"num\":1,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":0,\"num\":15,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":180000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (234, 10, 23, 21, NULL, '今日累充680元宝', NULL, 680, '[{\"awardId\":830,\"item\":60,\"week\":1,\"num\":5,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":1,\"num\":15,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":150000,\"isFhb\":0},{\"awardId\":830,\"item\":60,\"week\":2,\"num\":5,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":2,\"num\":18,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":300000,\"isFhb\":0},{\"awardId\":830,\"item\":60,\"week\":0,\"num\":5,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":0,\"num\":23,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":450000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (235, 10, 23, 21, NULL, '今日累充980元宝', NULL, 980, '[{\"awardId\":830,\"item\":60,\"week\":1,\"num\":8,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":1,\"num\":20,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":160000,\"isFhb\":0},{\"awardId\":830,\"item\":60,\"week\":2,\"num\":8,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":2,\"num\":24,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":320000,\"isFhb\":0},{\"awardId\":830,\"item\":60,\"week\":0,\"num\":8,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":0,\"num\":30,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":480000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (236, 10, 23, 21, NULL, '今日累充1380元宝', NULL, 1380, '[{\"awardId\":830,\"item\":60,\"week\":1,\"num\":10,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":1,\"num\":25,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":200000,\"isFhb\":0},{\"awardId\":830,\"item\":60,\"week\":2,\"num\":10,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":2,\"num\":30,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":400000,\"isFhb\":0},{\"awardId\":830,\"item\":60,\"week\":0,\"num\":10,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":0,\"num\":38,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":600000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (237, 10, 23, 21, NULL, '今日累充3280元宝', NULL, 3280, '[{\"awardId\":840,\"item\":60,\"week\":1,\"num\":5,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":1,\"num\":30,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":300000,\"isFhb\":0},{\"awardId\":840,\"item\":60,\"week\":2,\"num\":5,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":2,\"num\":36,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":600000,\"isFhb\":0},{\"awardId\":840,\"item\":60,\"week\":0,\"num\":5,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":0,\"num\":45,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":900000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (238, 10, 23, 21, NULL, '今日累充6480元宝', NULL, 6480, '[{\"awardId\":840,\"item\":60,\"week\":1,\"num\":8,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":1,\"num\":40,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":500000,\"isFhb\":0},{\"awardId\":840,\"item\":60,\"week\":2,\"num\":8,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":2,\"num\":50,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":1000000,\"isFhb\":0},{\"awardId\":840,\"item\":60,\"week\":0,\"num\":8,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":0,\"num\":60,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":1500000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (239, 10, 23, 21, NULL, '今日累充10000元宝', NULL, 10000, '[{\"awardId\":840,\"item\":60,\"week\":1,\"num\":10,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":1,\"num\":65,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":750000,\"isFhb\":0},{\"awardId\":840,\"item\":60,\"week\":2,\"num\":10,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":2,\"num\":80,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":1500000,\"isFhb\":0},{\"awardId\":840,\"item\":60,\"week\":0,\"num\":10,\"isFhb\":0},{\"awardId\":10020,\"item\":60,\"week\":0,\"num\":100,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":2250000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (240, 10, 23, 21, NULL, '今日累充20000元宝', NULL, 20000, '[{\"awardId\":850,\"item\":60,\"week\":1,\"num\":5,\"isFhb\":0},{\"item\":40,\"week\":1,\"star\":5,\"num\":1,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":1500000,\"isFhb\":0},{\"awardId\":850,\"item\":60,\"week\":2,\"num\":5,\"isFhb\":0},{\"awardId\":10010,\"item\":60,\"week\":2,\"num\":8,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":3000000,\"isFhb\":0},{\"awardId\":850,\"item\":60,\"week\":0,\"num\":5,\"isFhb\":0},{\"awardId\":10010,\"item\":60,\"week\":0,\"num\":8,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":4500000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (241, 10, 23, 21, NULL, '今日累充30000元宝', NULL, 30000, '[{\"awardId\":850,\"item\":60,\"week\":1,\"num\":8,\"isFhb\":0},{\"awardId\":10010,\"item\":60,\"week\":1,\"num\":8,\"isFhb\":0},{\"item\":20,\"week\":1,\"num\":1800000,\"isFhb\":0},{\"awardId\":850,\"item\":60,\"week\":2,\"num\":8,\"isFhb\":0},{\"awardId\":10010,\"item\":60,\"week\":2,\"num\":8,\"isFhb\":0},{\"item\":20,\"week\":2,\"num\":3600000,\"isFhb\":0},{\"awardId\":850,\"item\":60,\"week\":0,\"num\":8,\"isFhb\":0},{\"awardId\":10010,\"item\":60,\"week\":0,\"num\":8,\"isFhb\":0},{\"item\":20,\"week\":0,\"num\":5400000,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (242, 10, 23, 21, NULL, '今日累充50000元宝', NULL, 50000, '[{\"awardId\":850,\"item\":60,\"week\":1,\"num\":30,\"isFhb\":0},{\"awardId\":10010,\"item\":60,\"week\":1,\"num\":8,\"isFhb\":0},{\"awardId\":850,\"item\":60,\"week\":2,\"num\":30,\"isFhb\":0},{\"awardId\":10010,\"item\":60,\"week\":2,\"num\":8,\"isFhb\":0},{\"awardId\":850,\"item\":60,\"week\":0,\"num\":30,\"isFhb\":0},{\"awardId\":10010,\"item\":60,\"week\":0,\"num\":8,\"isFhb\":0}]', b'1');
INSERT INTO `cfg_activity` VALUES (261, 10, 26, 23, NULL, '累计充值天数达1天', NULL, 1, '[{\"item\":20,\"num\":10000},{\"item\":30,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (262, 10, 26, 23, NULL, '累计充值天数达2天', NULL, 2, '[{\"item\":40,\"star\":3,\"num\":1},{\"item\":30,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (263, 10, 26, 23, NULL, '累计充值天数达3天', NULL, 3, '[{\"item\":20,\"num\":10000},{\"item\":30,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (264, 10, 26, 23, NULL, '累计充值天数达4天', NULL, 4, '[{\"item\":40,\"star\":3,\"num\":1},{\"item\":30,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (265, 10, 26, 23, NULL, '累计充值天数达5天', NULL, 5, '[{\"item\":20,\"num\":10000},{\"item\":30,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (266, 10, 26, 23, NULL, '累计充值天数达6天', NULL, 6, '[{\"awardId\":10010,\"item\":60,\"num\":1},{\"item\":30,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (267, 10, 26, 23, NULL, '累计充值天数达7天', NULL, 7, '[{\"item\":40,\"star\":4,\"num\":1},{\"item\":30,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (301, 20, 30, 30, NULL, '新手登入1天', NULL, 1, '[{\"item\":20,\"num\":20000}]', b'1');
INSERT INTO `cfg_activity` VALUES (302, 20, 30, 30, NULL, '新手登入2天', NULL, 2, '[{\"item\":10,\"num\":50}]', b'1');
INSERT INTO `cfg_activity` VALUES (303, 20, 30, 30, NULL, '新手登入3天', NULL, 3, '[{\"item\":40,\"star\":3,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (304, 20, 30, 30, NULL, '新手登入4天', NULL, 4, '[{\"awardId\":520,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (305, 20, 30, 30, NULL, '新手登入5天', NULL, 5, '[{\"awardId\":50,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (306, 20, 30, 30, NULL, '新手登入6天', NULL, 6, '[{\"awardId\":20,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (307, 20, 30, 30, NULL, '新手登入7天', NULL, 7, '[{\"awardId\":326,\"item\":40,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (401, 20, 40, 20, NULL, '累计登入1天', NULL, 1, '[{\"item\":50,\"awardId\":0,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (402, 20, 40, 20, NULL, '累计登入2天', NULL, 2, '[{\"item\":20,\"num\":5000}]', b'1');
INSERT INTO `cfg_activity` VALUES (403, 20, 40, 20, NULL, '累计登入3天', NULL, 3, '[{\"awardId\":810,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (404, 20, 40, 20, NULL, '累计登入4天', NULL, 4, '[{\"item\":60,\"star\":1,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (405, 20, 40, 20, NULL, '累计登入5天', NULL, 5, '[{\"item\":10,\"num\":40}]', b'1');
INSERT INTO `cfg_activity` VALUES (406, 20, 40, 20, NULL, '累计登入6天', NULL, 6, '[{\"item\":50,\"awardId\":0,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (407, 20, 40, 20, NULL, '累计登入7天', NULL, 7, '[{\"item\":20,\"num\":10000}]', b'1');
INSERT INTO `cfg_activity` VALUES (408, 20, 40, 20, NULL, '累计登入8天', NULL, 8, '[{\"awardId\":820,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (409, 20, 40, 20, NULL, '累计登入9天', NULL, 9, '[{\"item\":60,\"star\":2,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (410, 20, 40, 20, NULL, '累计登入10天', NULL, 10, '[{\"item\":10,\"num\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (411, 20, 40, 20, NULL, '累计登入11天', NULL, 11, '[{\"item\":50,\"awardId\":0,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (412, 20, 40, 20, NULL, '累计登入12天', NULL, 12, '[{\"item\":20,\"num\":20000}]', b'1');
INSERT INTO `cfg_activity` VALUES (413, 20, 40, 20, NULL, '累计登入13天', NULL, 13, '[{\"awardId\":830,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (414, 20, 40, 20, NULL, '累计登入14天', NULL, 14, '[{\"item\":60,\"star\":3,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (415, 20, 40, 20, NULL, '累计登入15天', NULL, 15, '[{\"item\":10,\"num\":80}]', b'1');
INSERT INTO `cfg_activity` VALUES (416, 20, 40, 20, NULL, '累计登入16天', NULL, 16, '[{\"item\":50,\"awardId\":0,\"num\":4}]', b'1');
INSERT INTO `cfg_activity` VALUES (417, 20, 40, 20, NULL, '累计登入17天', NULL, 17, '[{\"item\":20,\"num\":40000}]', b'1');
INSERT INTO `cfg_activity` VALUES (418, 20, 40, 20, NULL, '累计登入18天', NULL, 18, '[{\"awardId\":840,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (419, 20, 40, 20, NULL, '累计登入19天', NULL, 19, '[{\"item\":60,\"star\":4,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (420, 20, 40, 20, NULL, '累计登入20天', NULL, 20, '[{\"item\":10,\"num\":100}]', b'1');
INSERT INTO `cfg_activity` VALUES (421, 20, 40, 20, NULL, '累计登入21天', NULL, 21, '[{\"item\":50,\"awardId\":0,\"num\":5}]', b'1');
INSERT INTO `cfg_activity` VALUES (422, 20, 40, 20, NULL, '累计登入22天', NULL, 22, '[{\"item\":20,\"num\":50000}]', b'1');
INSERT INTO `cfg_activity` VALUES (423, 20, 40, 20, NULL, '累计登入23天', NULL, 23, '[{\"awardId\":850,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (424, 20, 40, 20, NULL, '累计登入24天', NULL, 24, '[{\"item\":60,\"star\":5,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (425, 20, 40, 20, NULL, '累计登入25天', NULL, 25, '[]', b'1');
INSERT INTO `cfg_activity` VALUES (502, 20, 50, 40, NULL, '12点前补充体力', NULL, 12, '[{\"item\":30,\"num\":15}]', b'1');
INSERT INTO `cfg_activity` VALUES (503, 20, 50, 40, NULL, '12点后补充体力', NULL, 12, '[{\"item\":30,\"num\":15}]', b'1');
INSERT INTO `cfg_activity` VALUES (601, 20, 60, 70, NULL, '邀请1好友', NULL, 1, '[{\"item\":50,\"awardId\":10,\"num\":1},{\"item\":50,\"awardId\":20,\"num\":1},{\"item\":50,\"awardId\":30,\"num\":1},{\"item\":50,\"awardId\":40,\"num\":1},{\"item\":50,\"awardId\":50,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (602, 20, 60, 70, NULL, '邀请5好友', NULL, 5, '[{\"item\":20,\"num\":100000}]', b'1');
INSERT INTO `cfg_activity` VALUES (603, 20, 60, 70, NULL, '邀请10好友', NULL, 10, '[{\"item\":10,\"num\":200}]', b'1');
INSERT INTO `cfg_activity` VALUES (604, 20, 60, 70, NULL, '邀请20好友', NULL, 20, '[{\"awardId\":225,\"item\":40,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (1002, 30, 120, NULL, NULL, '佳节到，闹元宵', '亲爱的召唤师：\r\n月儿圆圆，元宵圆圆；汤圆甜甜，笑容甜甜；情谊满满，祝福满满。祝你元宵节：生活万事不难，收入年年有余，事业红红火火，好事成双成对！ ', 1, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1003, 30, 130, NULL, NULL, '经验加倍', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1004, 30, 140, NULL, NULL, '铜钱加倍', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1005, 30, 55, NULL, NULL, '补充体力翻倍', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1006, 20, 1010, 35, NULL, '月卡', NULL, NULL, '[{\"item\":10,\"num\":120}]', b'1');
INSERT INTO `cfg_activity` VALUES (1009, 30, 180, NULL, NULL, '每日首充翻倍活动', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1010, 30, 190, NULL, NULL, '点将台', '', NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (1011, 30, 165, NULL, NULL, '首冲翻倍重置', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (2002, 30, 210, NULL, NULL, '商城产品八折销售', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (2008, 30, 200, NULL, NULL, '今日累冲第6次满115元奖励', '今日累冲第6次满115元奖励。\n现为你发放奖励,祝您游戏愉快！', 6900, '[{\"num\":1,\"awardId\":850,\"item\":60},{\"num\":1,\"awardId\":840,\"item\":60},{\"num\":1,\"awardId\":830,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2009, 30, 200, NULL, NULL, '今日累冲第5次满115元奖励', '今日累冲第5次满115元奖励。\n现为你发放奖励,祝您游戏愉快！', 5750, '[{\"num\":1,\"awardId\":850,\"item\":60},{\"num\":1,\"awardId\":840,\"item\":60},{\"num\":1,\"awardId\":830,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2010, 30, 200, NULL, NULL, '今日累冲第4次满115元奖励', '今日累冲第4次满115元奖励。\n现为你发放奖励,祝您游戏愉快！', 4600, '[{\"num\":1,\"awardId\":850,\"item\":60},{\"num\":1,\"awardId\":840,\"item\":60},{\"num\":1,\"awardId\":830,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2011, 30, 200, NULL, NULL, '今日累冲第3次满115元奖励', '今日累冲第3次满115元奖励。\n现为你发放奖励,祝您游戏愉快！', 3450, '[{\"num\":1,\"awardId\":850,\"item\":60},{\"num\":1,\"awardId\":840,\"item\":60},{\"num\":1,\"awardId\":830,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2012, 30, 200, NULL, NULL, '今日累冲第2次满115元奖励', '今日累冲第2次满115元奖励。\n现为你发放奖励,祝您游戏愉快！', 2300, '[{\"num\":1,\"awardId\":850,\"item\":60},{\"num\":1,\"awardId\":840,\"item\":60},{\"num\":1,\"awardId\":830,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2013, 30, 200, NULL, NULL, '今日累冲第1次满115元奖励', '今日累冲第1次满115元奖励。\n现为你发放奖励,祝您游戏愉快！', 1150, '[{\"num\":1,\"awardId\":850,\"item\":60},{\"num\":1,\"awardId\":840,\"item\":60},{\"num\":1,\"awardId\":830,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2014, 30, 200, NULL, NULL, '今日累冲满11.5元奖励', '您今日累冲满11.5元。\n现为你发放奖励,祝您游戏愉快！', 115, '-', b'1');
INSERT INTO `cfg_activity` VALUES (2015, 30, 200, NULL, NULL, '今日累冲满1.15元奖励', '您今日累冲满1.15元。\n现为你发放奖励,祝您游戏愉快！', 11, '[{\"num\":1,\"awardId\":20,\"item\":60},{\"num\":1,\"awardId\":530,\"item\":60},{\"num\":15,\"item\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (2111, 30, 200, NULL, NULL, '累计充值满50元奖励', '您活动期间已累计充值满50元。\n现为你发放奖励,祝您游戏愉快！', 50, '-', b'1');
INSERT INTO `cfg_activity` VALUES (2112, 30, 200, NULL, NULL, '累计充值满100元奖励', '您活动期间已累计充值满100元。\n现为你发放奖励,祝您游戏愉快！', 100, '[{\"num\":1,\"awardId\":850,\"item\":60},{\"num\":1,\"awardId\":840,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2113, 30, 200, NULL, NULL, '累计充值每满200元奖励', '您活动期间累计充值满200元。\n现为你发放奖励,祝您游戏愉快！', 200, '[{\"num\":2019,\"item\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (2114, 30, 200, NULL, NULL, '累计充值每满500元奖励', '您活动期间累计充值满500元。\n现为你发放奖励,祝您游戏愉快！', 500, '-', b'1');
INSERT INTO `cfg_activity` VALUES (2115, 30, 200, NULL, NULL, '累计充值每满1000元奖励', '您活动期间累计充值满1000元。\n现为你发放奖励,祝您游戏愉快！', 1000, '[{\"num\":3,\"awardId\":850,\"item\":60},{\"num\":5,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2116, 30, 200, NULL, NULL, '累计充值每满2000元奖励', '您活动期间累计充值满2000元。\n现为你发放奖励,祝您游戏愉快！', 2000, '[{\"num\":5,\"awardId\":850,\"item\":60},{\"num\":10,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2117, 30, 200, NULL, NULL, '累计充值每满3000元奖励', '您活动期间累计充值满3000元。\n现为你发放奖励,祝您游戏愉快！', 3000, '[{\"num\":5,\"awardId\":850,\"item\":60},{\"num\":10,\"awardId\":10030,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (2118, 30, 200, NULL, NULL, '累计充值每满5000元奖励', '您活动期间累计充值满5000元。\n现为你发放奖励,祝您游戏愉快！', 5000, '-', b'1');
INSERT INTO `cfg_activity` VALUES (2119, 30, 200, NULL, NULL, '累计充值每满8000元奖励', '您活动期间累计充值满8000元。\n现为你发放奖励,祝您游戏愉快！', 8000, '[{\"num\":30,\"awardId\":850,\"item\":60}]', b'1');
INSERT INTO `cfg_activity` VALUES (9101, 10, 9010, 24, NULL, '新手特惠', NULL, NULL, NULL, b'1');
INSERT INTO `cfg_activity` VALUES (9201, 10, 9020, 20, NULL, '首位占领5级城', NULL, 1, '[{\"awardId\":850,\"item\":60,\"num\":30}]', b'1');
INSERT INTO `cfg_activity` VALUES (9211, 10, 9020, 20, 1, '攻占5座初级城', NULL, 5, '[{\"item\":20,\"num\":10000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9212, 10, 9020, 20, 1, '攻占10座初级城', NULL, 10, '[{\"item\":20,\"num\":20000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9213, 10, 9020, 20, 1, '攻占15座初级城', NULL, 15, '[{\"item\":20,\"num\":50000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9214, 10, 9020, 20, 1, '攻占20座初级城', NULL, 20, '[{\"item\":20,\"num\":150000}]', b'1');
INSERT INTO `cfg_activity` VALUES (9221, 10, 9020, 20, 2, '攻占5座二级城', NULL, 5, '[{\"awardId\":80,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9222, 10, 9020, 20, 2, '攻占7座二级城', NULL, 7, '[{\"awardId\":80,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9223, 10, 9020, 20, 2, '攻占10座二级城', NULL, 10, '[{\"awardId\":80,\"item\":60,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (9224, 10, 9020, 20, 2, '攻占15座二级城', NULL, 15, '[{\"awardId\":80,\"item\":60,\"num\":3}]', b'1');
INSERT INTO `cfg_activity` VALUES (9231, 10, 9020, 20, 3, '攻占5座三级城', NULL, 5, '[{\"item\":40,\"star\":3,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9232, 10, 9020, 20, 3, '攻占10座三级城', NULL, 10, '[{\"item\":40,\"star\":4,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9233, 10, 9020, 20, 3, '攻占15座三级城', NULL, 15, '[{\"item\":40,\"star\":4,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9241, 10, 9020, 20, 4, '攻占3座四级城', NULL, 3, '[{\"item\":40,\"star\":5,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9251, 10, 9020, 20, 5, '攻占1座主城', NULL, 1, '[{\"awardId\":850,\"item\":60,\"num\":10}]', b'1');
INSERT INTO `cfg_activity` VALUES (9301, 10, 9030, 24, NULL, '每日消费100元宝', NULL, 100, '[{\"awardId\":10020,\"item\":60,\"num\":10},{\"awardId\":10040,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9302, 10, 9030, 24, NULL, '每日消费300元宝', NULL, 300, '[{\"awardId\":10020,\"item\":60,\"num\":20},{\"awardId\":80,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9303, 10, 9030, 24, NULL, '每日消费600元宝', NULL, 600, '[{\"awardId\":10030,\"item\":60,\"num\":1},{\"awardId\":60,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9304, 10, 9030, 24, NULL, '每日消费1000元宝', NULL, 1000, '[{\"awardId\":10030,\"item\":60,\"num\":1},{\"awardId\":10010,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9305, 10, 9030, 24, NULL, '每日消费1500元宝', NULL, 1500, '[{\"awardId\":10030,\"item\":60,\"num\":1},{\"awardId\":10,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9306, 10, 9030, 24, NULL, '每日消费2000元宝', NULL, 2000, '[{\"awardId\":10030,\"item\":60,\"num\":1},{\"awardId\":570,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9307, 10, 9030, 24, NULL, '每日消费3000元宝', NULL, 3000, '[{\"awardId\":10030,\"item\":60,\"num\":2},{\"awardId\":20,\"item\":60,\"num\":1}]', b'1');
INSERT INTO `cfg_activity` VALUES (9308, 10, 9030, 24, NULL, '每日消费5000元宝', NULL, 5000, '[{\"awardId\":10030,\"item\":60,\"num\":4},{\"awardId\":10030,\"item\":60,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (9309, 10, 9030, 24, NULL, '每日消费7000元宝', NULL, 7000, '[{\"awardId\":10030,\"item\":60,\"num\":4},{\"awardId\":10030,\"item\":60,\"num\":2}]', b'1');
INSERT INTO `cfg_activity` VALUES (9310, 10, 9030, 24, NULL, '每日消费10000元宝', NULL, 10000, '[{\"awardId\":10030,\"item\":60,\"num\":6},{\"awardId\":10030,\"item\":60,\"num\":4}]', b'1');
INSERT INTO `cfg_activity` VALUES (9401, 10, 9040, 25, NULL, '星君宝库', NULL, NULL, NULL, b'1');
COMMIT;

-- ----------------------------
-- Table structure for cfg_activity_rank
-- ----------------------------
DROP TABLE IF EXISTS `cfg_activity_rank`;
CREATE TABLE `cfg_activity_rank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `serial` int(11) DEFAULT NULL,
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
INSERT INTO `cfg_activity_rank` VALUES (10010, 10000, 10000, '充值排行第1名', '[{\"awardId\":10010,\"item\":60,\"num\":10},{\"awardId\":850,\"item\":60,\"num\":6},{\"awardId\":10020,\"item\":60,\"num\":100},{\"item\":20,\"num\":2000000}]', 1, 1, '3000', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10015, 10000, 10000, '充值排行第2名', '[{\"awardId\":10010,\"item\":60,\"num\":8},{\"awardId\":850,\"item\":60,\"num\":4},{\"awardId\":10020,\"item\":60,\"num\":80},{\"item\":20,\"num\":1600000}]', 2, 2, '1000', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10020, 10000, 10000, '充值排行第3名', '[{\"awardId\":10010,\"item\":60,\"num\":6},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 3, 3, '1000', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10025, 10000, 10000, '充值排行第4-10名', '[{\"item\":40,\"star\":4,\"num\":1},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 4, 10, '300', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10030, 10000, 10000, '充值排行第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10035, 10000, 10000, '充值排行第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10040, 10000, 10000, '充值排行第51-100名', '[{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10045, 10000, 10000, '充值排行第101-200名', '[{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10110, 10010, 10010, '富豪榜第1名', '[{\"awardId\":10010,\"item\":60,\"num\":8},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":80},{\"item\":20,\"num\":1600000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10115, 10010, 10010, '富豪榜第2名', '[{\"awardId\":10010,\"item\":60,\"num\":6},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10120, 10010, 10010, '富豪榜第3名', '[{\"item\":40,\"star\":4,\"num\":1},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":1000000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10125, 10010, 10010, '富豪榜第4-10名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10130, 10010, 10010, '富豪榜第11-20名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10135, 10010, 10010, '富豪榜第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10140, 10010, 10010, '富豪榜第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10145, 10010, 10010, '富豪榜第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10210, 10020, 10020, '元宝消耗第1名', '[{\"awardId\":10010,\"item\":60,\"num\":10},{\"awardId\":850,\"item\":60,\"num\":10},{\"awardId\":10,\"item\":50,\"num\":50},{\"awardId\":20,\"item\":50,\"num\":50},{\"awardId\":30,\"item\":50,\"num\":50},{\"awardId\":40,\"item\":50,\"num\":50},{\"awardId\":50,\"item\":50,\"num\":50}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10215, 10020, 10020, '元宝消耗第2名', '[{\"awardId\":10010,\"item\":60,\"num\":8},{\"awardId\":850,\"item\":60,\"num\":8},{\"awardId\":10,\"item\":50,\"num\":30},{\"awardId\":20,\"item\":50,\"num\":30},{\"awardId\":30,\"item\":50,\"num\":30},{\"awardId\":40,\"item\":50,\"num\":30},{\"awardId\":50,\"item\":50,\"num\":30}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10220, 10020, 10020, '元宝消耗第3名', '[{\"awardId\":10010,\"item\":60,\"num\":6},{\"awardId\":850,\"item\":60,\"num\":5},{\"awardId\":10,\"item\":50,\"num\":20},{\"awardId\":20,\"item\":50,\"num\":20},{\"awardId\":30,\"item\":50,\"num\":20},{\"awardId\":40,\"item\":50,\"num\":20},{\"awardId\":50,\"item\":50,\"num\":20}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10225, 10020, 10020, '元宝消耗第4-10名', '[{\"item\":40,\"star\":4,\"num\":1},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10,\"item\":50,\"num\":15},{\"awardId\":20,\"item\":50,\"num\":15},{\"awardId\":30,\"item\":50,\"num\":15},{\"awardId\":40,\"item\":50,\"num\":15},{\"awardId\":50,\"item\":50,\"num\":15}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10230, 10020, 10020, '元宝消耗第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10,\"item\":50,\"num\":10},{\"awardId\":20,\"item\":50,\"num\":10},{\"awardId\":30,\"item\":50,\"num\":10},{\"awardId\":40,\"item\":50,\"num\":10},{\"awardId\":50,\"item\":50,\"num\":10}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10235, 10020, 10020, '元宝消耗第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":5},{\"awardId\":10,\"item\":50,\"num\":8},{\"awardId\":20,\"item\":50,\"num\":8},{\"awardId\":30,\"item\":50,\"num\":8},{\"awardId\":40,\"item\":50,\"num\":8},{\"awardId\":50,\"item\":50,\"num\":8}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10240, 10020, 10020, '元宝消耗第51-100名', '[{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10,\"item\":50,\"num\":5},{\"awardId\":20,\"item\":50,\"num\":5},{\"awardId\":30,\"item\":50,\"num\":5},{\"awardId\":40,\"item\":50,\"num\":5},{\"awardId\":50,\"item\":50,\"num\":5}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10245, 10020, 10020, '元宝消耗第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10,\"item\":50,\"num\":3},{\"awardId\":20,\"item\":50,\"num\":3},{\"awardId\":30,\"item\":50,\"num\":3},{\"awardId\":40,\"item\":50,\"num\":3},{\"awardId\":50,\"item\":50,\"num\":3}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10510, 10050, 10050, '元素消耗第1名', '[{\"awardId\":10010,\"item\":60,\"num\":4},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 1, 1, '500', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10515, 10050, 10050, '元素消耗第2名', '[{\"awardId\":10010,\"item\":60,\"num\":3},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 2, 2, '200', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10520, 10050, 10050, '元素消耗第3名', '[{\"awardId\":10010,\"item\":60,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 3, 3, '200', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10525, 10050, 10050, '元素消耗第4-10名', '[{\"item\":40,\"star\":3,\"num\":3},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 4, 10, '30', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10530, 10050, 10050, '元素消耗第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10535, 10050, 10050, '元素消耗第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10540, 10050, 10050, '元素消耗第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10545, 10050, 10050, '元素消耗第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":5},{\"item\":20,\"num\":100000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10610, 10060, 10060, '胜利宝箱第1名', '[{\"awardId\":10010,\"item\":60,\"num\":4},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10615, 10060, 10060, '胜利宝箱第2名', '[{\"awardId\":10010,\"item\":60,\"num\":3},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10620, 10060, 10060, '胜利宝箱第3名', '[{\"awardId\":10010,\"item\":60,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10625, 10060, 10060, '胜利宝箱第4-10名', '[{\"item\":40,\"star\":3,\"num\":3},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10630, 10060, 10060, '胜利宝箱第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10635, 10060, 10060, '胜利宝箱第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10640, 10060, 10060, '胜利宝箱第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10645, 10060, 10060, '胜利宝箱第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":5},{\"item\":20,\"num\":100000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10710, 10070, 10070, '攻城榜第1名', '[{\"awardId\":10010,\"item\":60,\"num\":10},{\"awardId\":850,\"item\":60,\"num\":6},{\"awardId\":10020,\"item\":60,\"num\":100},{\"item\":20,\"num\":2000000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10715, 10070, 10070, '攻城榜第2名', '[{\"awardId\":10010,\"item\":60,\"num\":8},{\"awardId\":850,\"item\":60,\"num\":4},{\"awardId\":10020,\"item\":60,\"num\":80},{\"item\":20,\"num\":1600000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10720, 10070, 10070, '攻城榜第3名', '[{\"awardId\":10010,\"item\":60,\"num\":6},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10725, 10070, 10070, '攻城榜第4-10名', '[{\"item\":40,\"star\":4,\"num\":1},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10730, 10070, 10070, '攻城榜第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10735, 10070, 10070, '攻城榜第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10740, 10070, 10070, '攻城榜第51-100名', '[{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10745, 10070, 10070, '攻城榜第101-200名', '[{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10810, 10080, 10080, '玩家等级榜第1名', '[{\"awardId\":10010,\"item\":60,\"num\":4},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10815, 10080, 10080, '玩家等级榜第2名', '[{\"awardId\":10010,\"item\":60,\"num\":3},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10820, 10080, 10080, '玩家等级榜第3名', '[{\"awardId\":10010,\"item\":60,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10825, 10080, 10080, '玩家等级榜第4-10名', '[{\"item\":40,\"star\":3,\"num\":3},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10830, 10080, 10080, '玩家等级榜第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10835, 10080, 10080, '玩家等级榜第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10840, 10080, 10080, '玩家等级榜第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10845, 10080, 10080, '玩家等级榜第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":5},{\"item\":20,\"num\":100000}]', 101, 200, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10910, 10090, 10090, '远征榜第1名', '[{\"awardId\":10010,\"item\":60,\"num\":4},{\"awardId\":850,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":60},{\"item\":20,\"num\":1200000}]', 1, 1, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10915, 10090, 10090, '远征榜第2名', '[{\"awardId\":10010,\"item\":60,\"num\":3},{\"awardId\":850,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":40},{\"item\":20,\"num\":800000}]', 2, 2, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10920, 10090, 10090, '远征榜第3名', '[{\"awardId\":10010,\"item\":60,\"num\":2},{\"awardId\":850,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":30},{\"item\":20,\"num\":600000}]', 3, 3, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10925, 10090, 10090, '远征榜第4-10名', '[{\"item\":40,\"star\":3,\"num\":3},{\"awardId\":840,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":20},{\"item\":20,\"num\":400000}]', 4, 10, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10930, 10090, 10090, '远征榜第11-20名', '[{\"item\":40,\"star\":3,\"num\":2},{\"awardId\":840,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":15},{\"item\":20,\"num\":300000}]', 11, 20, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10935, 10090, 10090, '远征榜第21-50名', '[{\"item\":40,\"star\":3,\"num\":1},{\"awardId\":840,\"item\":60,\"num\":1},{\"awardId\":10020,\"item\":60,\"num\":10},{\"item\":20,\"num\":200000}]', 21, 50, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10940, 10090, 10090, '远征榜第51-100名', '[{\"awardId\":830,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":8},{\"item\":20,\"num\":160000}]', 51, 100, '0', b'1');
INSERT INTO `cfg_activity_rank` VALUES (10945, 10090, 10090, '远征榜第101-200名', '[{\"awardId\":830,\"item\":60,\"num\":2},{\"awardId\":10020,\"item\":60,\"num\":5},{\"item\":20,\"num\":100000}]', 101, 200, '0', b'1');
COMMIT;

-- ----------------------------
-- Table structure for cfg_card
-- ----------------------------
DROP TABLE IF EXISTS `cfg_card`;
CREATE TABLE `cfg_card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `type` int(11) NOT NULL,
  `star` int(11) NOT NULL,
  `attack` int(11) NOT NULL,
  `hp` int(11) NOT NULL,
  `zero_skill` int(11) DEFAULT NULL,
  `five_skill` int(11) DEFAULT NULL,
  `ten_skill` int(11) DEFAULT NULL,
  `comment` varchar(100) NOT NULL,
  `group` int(11) DEFAULT NULL,
  `can_get_way` int(2) NOT NULL COMMENT '1限定卡（封神包、财富榜、押押乐、魔王）\r\n2新卡（只能通过节日礼包获得）\r\n3无法正常获得（可通过成就、月签到获得）\r\n-1新卡（暂不能获得）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=537 DEFAULT CHARSET=utf8mb4 COMMENT='卡牌';

-- ----------------------------
-- Records of cfg_card
-- ----------------------------
BEGIN;
INSERT INTO `cfg_card` VALUES (101, '姜子牙', 10, 5, 600, 710, 50, 250, 90, '中国历史上的头衔是杰出政治家、军事家和谋略家。在封神大陆中是封神榜名单的唯一官方权威发言人。', NULL, 0);
INSERT INTO `cfg_card` VALUES (102, '杨戬', 10, 5, 700, 820, 120, 290, 450, '玉泉山玉鼎真人的徒弟，姜子牙的师侄，因擅长七十三变在西歧阵营中长期抢头功。', NULL, 1);
INSERT INTO `cfg_card` VALUES (103, '姬发', 10, 4, 390, 530, 230, NULL, 140, '即周武王，西周的创建者。', NULL, 0);
INSERT INTO `cfg_card` VALUES (104, '雷震子', 10, 4, 490, 450, 170, 40, 10, '姬昌第一百子，因误食转基因产品（仙杏），生出风雷双翅。', NULL, 0);
INSERT INTO `cfg_card` VALUES (105, '李靖', 10, 4, 420, 540, NULL, 240, 200, '山海关总兵，有托塔天王之称。', 30, 0);
INSERT INTO `cfg_card` VALUES (106, '韦护', 10, 4, 440, 550, 20, 420, NULL, '金庭山玉屋洞道行天尊的弟子，辅助西周灭商，肉身成圣，为三教护法。', NULL, 1);
INSERT INTO `cfg_card` VALUES (107, '黄天化', 10, 4, 480, 520, 10, NULL, 20, '黄飞虎长子，曾经用攒心钉斩杀四大天王。', 20, 0);
INSERT INTO `cfg_card` VALUES (108, '周公旦', 10, 4, 370, 500, 280, 70, 330, '正式名字是姬旦，治世能臣，有圣人之称。', NULL, 0);
INSERT INTO `cfg_card` VALUES (109, '金吒', 10, 3, 310, 360, NULL, 240, 270, '李靖长子。师从文殊广法天尊。', 30, 0);
INSERT INTO `cfg_card` VALUES (110, '武吉', 10, 3, 290, 360, 50, 420, NULL, '本是樵夫，后成为姜子牙的弟子，受封武德将军。', NULL, 0);
INSERT INTO `cfg_card` VALUES (111, '黄天祥', 10, 3, 320, 360, 10, NULL, 60, '黄飞虎之子，无法术的超级猛将，拥有唯一以凡人杀败仙人的战绩。', 20, 0);
INSERT INTO `cfg_card` VALUES (112, '彻地夫人', 10, 3, 270, 330, 270, 300, 380, '窦荣之妻，武艺超群，且智谋不凡，封神授月魁星。', NULL, 0);
INSERT INTO `cfg_card` VALUES (113, '姬伯邑考', 10, 3, 240, 310, 280, 300, NULL, '周文王的嫡长子，被封神后，成为玉皇大帝的原型。', NULL, 0);
INSERT INTO `cfg_card` VALUES (114, '南宫适', 10, 3, 260, 330, 20, NULL, 440, '西周开国功臣，高级武官。', NULL, 0);
INSERT INTO `cfg_card` VALUES (115, '金睛兽', 10, 2, 190, 240, 40, NULL, 430, '北崇名将崇黑虎的坐骑。', NULL, 0);
INSERT INTO `cfg_card` VALUES (116, '白鹤仙子', 10, 2, 170, 220, 40, NULL, 70, '选择白鹤作为宠物后果然显得更具品味。', NULL, 0);
INSERT INTO `cfg_card` VALUES (117, '护周上将', 10, 2, 180, 190, 10, NULL, 60, '西歧能统军的士官。', NULL, 0);
INSERT INTO `cfg_card` VALUES (118, '虎贲卫士', 10, 2, 180, 180, 410, NULL, NULL, '西歧步兵中的高级兵种。', NULL, 0);
INSERT INTO `cfg_card` VALUES (119, '散宜生', 10, 2, 160, 200, 70, NULL, 280, '西周开国功臣，辅佐周文王、周武王的谋臣。', NULL, 0);
INSERT INTO `cfg_card` VALUES (120, '昆仑山散仙', 10, 2, 150, 180, 40, NULL, 260, '选择在空气质量极佳的昆仑山修行果然事半功倍。', NULL, 0);
INSERT INTO `cfg_card` VALUES (121, '西岐歌姬', 10, 1, 90, 110, NULL, 130, NULL, '只有商业发达的西歧才能成为高雅艺术发展的土壤。', NULL, 0);
INSERT INTO `cfg_card` VALUES (122, '修行道士', 10, 1, 110, 130, NULL, NULL, 260, '低调奢华有内涵的修行者。', NULL, 0);
INSERT INTO `cfg_card` VALUES (123, '天马', 10, 1, 120, 110, 40, NULL, NULL, '被西歧人驯服后成为神奇的飞行部队。', NULL, 0);
INSERT INTO `cfg_card` VALUES (124, '西岐兵', 10, 1, 100, 130, NULL, NULL, 300, '西歧国普通士兵。', NULL, 0);
INSERT INTO `cfg_card` VALUES (125, '窦荣', 10, 4, 510, 660, 440, 80, 60, '游魂关的总兵官，封神授武曲星。', NULL, 3);
INSERT INTO `cfg_card` VALUES (126, '姬昌', 10, 5, 980, 900, 230, 410, 220, '周文王，西歧的统治者，善长八卦，对封神大陆政治格局不止猜中了开头，还猜中了结果。', NULL, 3);
INSERT INTO `cfg_card` VALUES (127, '恶来', 10, 4, 420, 570, 300, NULL, 480, '商纣王的臣子，蜚廉之子，以勇力而闻名。后世三国典韦有古之恶来之称。', NULL, 3);
INSERT INTO `cfg_card` VALUES (128, '蜚廉', 10, 4, 510, 450, 10, 460, NULL, '恶来之父，也是秦朝人的祖先。', NULL, 3);
INSERT INTO `cfg_card` VALUES (129, '日游神', 10, 4, 450, 530, 280, 250, 450, '她总在白天出现，而且从不接受搭讪。传说碰到她的人会交好运，也有一种说法能遇上她本身就是好运了。', NULL, 2);
INSERT INTO `cfg_card` VALUES (130, '鱼凫', 10, 4, 470, 580, 70, 440, 460, '传说中的西方第二帝，继往开来的领路人，带领西蜀国走向新时代。', NULL, 2);
INSERT INTO `cfg_card` VALUES (131, '黄龙真人', 10, 4, 450, 520, 70, 30, 410, '阐教十二仙之一，性格外向，非常注重阐教尊严。每次大战基本都是提前到场。', NULL, 2);
INSERT INTO `cfg_card` VALUES (132, '道行天尊', 10, 5, 540, 690, 510, 200, 520, '阐教十二仙之一，其刻苦修练的主业是如何让队友更牛掰。', NULL, 3);
INSERT INTO `cfg_card` VALUES (133, '金光仙', 10, 4, 470, 540, 20, 170, 80, '通天教主弟子，原型为金毛犼。在封神之战中战败，被慈航道人抓去当了坐骑。', NULL, 2);
INSERT INTO `cfg_card` VALUES (134, '金箍仙', 10, 4, 460, 550, 260, 120, 240, '截教通天教主座下弟子之一，万仙阵中用金箍套住黄龙真人。', NULL, 3);
INSERT INTO `cfg_card` VALUES (135, '刺客', 10, 1, 130, 100, NULL, 540, NULL, '在一个伸手不见五指的夜晚，我悄悄带走你的脑袋。', NULL, 3);
INSERT INTO `cfg_card` VALUES (136, '锦鲤', 10, 2, 270, 40, NULL, NULL, NULL, '传说跳过那道龙门我们就会变成龙，这都是龙门口那只爱吃鱼的猫放出的风声。', NULL, 3);
INSERT INTO `cfg_card` VALUES (201, '哪吒', 20, 5, 630, 670, 60, 110, 10, '灵珠子转世，李靖三子，封神大陆的现象级明星，后又在西游记等剧走穴客串。', 30, 0);
INSERT INTO `cfg_card` VALUES (202, '句芒', 20, 5, 530, 1000, 170, 70, 270, '中国远古神话中的木神。', NULL, 1);
INSERT INTO `cfg_card` VALUES (203, '桃花星', 20, 4, 350, 620, 50, 70, 160, '即张奎的夫人高兰英，不止貌美如花，而且法术高强。', NULL, 0);
INSERT INTO `cfg_card` VALUES (204, '袁洪', 20, 4, 450, 630, 430, 270, 390, '梅山七杰之首，号为灵圣，是精通八九玄功的神通广大的白猿成精。', 100, 0);
INSERT INTO `cfg_card` VALUES (205, '申公豹', 20, 4, 350, 550, NULL, 160, 100, '与姜子牙同拜于元始天尊门下，人生的主要意义就是与姜子牙抬杠并成为其一生的宿敌。', NULL, 1);
INSERT INTO `cfg_card` VALUES (206, '木吒', 20, 4, 400, 580, 50, 450, 240, '李靖次子。师从普贤真人。', 30, 0);
INSERT INTO `cfg_card` VALUES (207, '游法师', 20, 4, 390, 580, 300, 70, 270, '修练于崆峒山元阳洞。阐教十二上仙之一。', NULL, 0);
INSERT INTO `cfg_card` VALUES (208, '魔礼海', 20, 4, 410, 650, NULL, 170, 120, '魔家四将之一，神通广大，手持玉琵琶，封神授多闻天王。', 60, 0);
INSERT INTO `cfg_card` VALUES (209, '琵琶精', 20, 3, 280, 360, 80, 160, 340, '玉石琵琶化身的妖怪，与狐狸精和九头雉鸡精一同受女娲命令迷魅纣王。', 50, 0);
INSERT INTO `cfg_card` VALUES (210, '杨森', 20, 3, 210, 360, 50, NULL, 250, '截教门人，西海九龙岛练气士，与王魔、高友乾、李兴霸合称“九龙岛四圣”。', 90, 0);
INSERT INTO `cfg_card` VALUES (211, '常昊', 20, 3, 240, 350, NULL, 120, 240, '梅山七怪之一，万年道行的蛇精，法术是可化青烟逃走，口吐毒雾。', 100, 0);
INSERT INTO `cfg_card` VALUES (212, '朱子真', 20, 3, 230, 390, NULL, 200, 20, '梅山七怪之一，野猪精，法术是身后透出一口巨猪直接将敌人吞噬。', 100, 0);
INSERT INTO `cfg_card` VALUES (213, '顺风耳', 20, 3, 220, 330, 450, NULL, 250, '原是棋盘山上的柳鬼，商纣王手下大将高觉，耳听八方，故名顺风耳。', 80, 0);
INSERT INTO `cfg_card` VALUES (214, '千里眼', 20, 3, 210, 350, 50, NULL, 260, '原是棋盘山上的桃精，商纣王手下大将高明，眼观千里，人称千里眼。', 80, 0);
INSERT INTO `cfg_card` VALUES (215, '杨显', 20, 2, 150, 230, 50, NULL, 240, '梅山七怪之一，是个羊怪。绝招是用一道白光把人罩住不能动。', 100, 0);
INSERT INTO `cfg_card` VALUES (216, '戴礼', 20, 2, 160, 220, 20, 430, NULL, '梅山七怪之一，是个狗怪，口吐红珠，百步伤人。', 100, 0);
INSERT INTO `cfg_card` VALUES (217, '金大升', 20, 2, 140, 250, NULL, 270, 280, '梅山七怪之一，野牛精，力大无穷，口吐牛黄烧人 。', 100, 0);
INSERT INTO `cfg_card` VALUES (218, '吴龙', 20, 2, 160, 200, NULL, 200, 180, '梅山七怪之一，蜈蚣精，可发出黑雾迷熏敌人。', 100, 0);
INSERT INTO `cfg_card` VALUES (219, '孙天君', 20, 2, 150, 190, 420, NULL, 80, '十绝阵中化血阵阵主。', 10, 0);
INSERT INTO `cfg_card` VALUES (220, '女娲祭祀', 20, 2, 140, 230, 410, NULL, NULL, '女娲神庙中司掌祭祀的女神。', NULL, 0);
INSERT INTO `cfg_card` VALUES (221, '花翎鸟', 20, 1, 100, 160, 40, NULL, NULL, '碧霄娘娘的坐骑。', NULL, 0);
INSERT INTO `cfg_card` VALUES (222, '白面猿猴', 20, 1, 110, 150, 30, NULL, NULL, '伯邑考进贡给纣王的三宝之一，是个得道千年的白猿，能辩人间妖魅。', NULL, 0);
INSERT INTO `cfg_card` VALUES (223, '地狮', 20, 1, 120, 150, NULL, NULL, 190, '杨森的坐骑，天生具备钻地奇术。', NULL, 0);
INSERT INTO `cfg_card` VALUES (224, '神射手', 20, 1, 110, 110, 50, NULL, NULL, '东鲁国训练射法精准的美女成为卫戍部队的主力。', NULL, 0);
INSERT INTO `cfg_card` VALUES (225, '羽翼仙', 20, 4, 420, 610, 40, 260, NULL, '原型是只大鹏雕，有千年道行，被燃灯道人收服后往灵鹫山修行。', NULL, 3);
INSERT INTO `cfg_card` VALUES (226, '姜桓楚', 20, 5, 880, 1030, 270, 300, 220, '商纣天下八百镇诸侯之首，东伯侯。封神授帝车星。', NULL, 3);
INSERT INTO `cfg_card` VALUES (227, '菡芝仙', 20, 4, 300, 600, 270, NULL, 470, '截教门人，隐居于东海金鳌岛，与十天君、三宵娘娘皆为好友，封神授助风神。', NULL, 3);
INSERT INTO `cfg_card` VALUES (228, '扫把星', 20, 3, 240, 340, 430, 270, 500, '即姜子牙之妻马氏，成为不祥的代名词。', NULL, 2);
INSERT INTO `cfg_card` VALUES (229, '余化', 20, 4, 460, 550, 120, 20, 440, '汜水关副将，人送外号七首将军，可称封神榜上第一神刀。', NULL, 2);
INSERT INTO `cfg_card` VALUES (230, '蚕丛', 20, 4, 410, 660, 70, 80, 410, '传说中的西方大帝，独具慧眼地发现原来蚕丝除了用来绑小动物，还能做丝绸。', NULL, 2);
INSERT INTO `cfg_card` VALUES (231, '灵宝法师', 20, 4, 400, 600, 50, 430, 270, '阐教十二仙之一，愣是没有收过弟子，也许法宝就是他的弟子吧。', NULL, 2);
INSERT INTO `cfg_card` VALUES (232, '白泽', 20, 4, 430, 510, 440, 120, 110, '昆仑圣兽，每次出现都意味着圣人出现。', NULL, 2);
INSERT INTO `cfg_card` VALUES (233, '毕方', 20, 4, 420, 530, 180, 210, 340, '中国古代传说中的火灾之兆。', NULL, 2);
INSERT INTO `cfg_card` VALUES (234, '灵牙仙', 20, 4, 430, 680, 60, 120, 170, '在万仙阵中败给普贤真人，南极仙翁将其打回黄牙老象的原型，赐给普贤真人当坐骑。', NULL, 2);
INSERT INTO `cfg_card` VALUES (235, '毗卢仙', 20, 4, 410, 670, 50, 260, 430, '通天教主门下，实力不明，被西方教主收复。', NULL, 2);
INSERT INTO `cfg_card` VALUES (236, '孟章苍龙', 20, 5, 580, 950, 40, 270, 540, '金銮火凤是最强云台尊者？那是你还不知道我这来自东方的神秘力量。', NULL, 3);
INSERT INTO `cfg_card` VALUES (301, '妲己', 30, 5, 580, 790, 160, 210, 130, '苏氏诸侯苏护长女。纣王欲纳为妃，在到朝歌途中被九尾狐狸精夺取魂魄，成为殷商祸害。', 50, 1);
INSERT INTO `cfg_card` VALUES (302, '赵公明', 30, 5, 620, 700, 280, 200, 230, '财神的本尊，曾统率大军与姜子牙对阵。', NULL, 0);
INSERT INTO `cfg_card` VALUES (303, '云霄', 30, 4, 400, 580, 160, NULL, 130, '赵公明之妹，拜骊山老母为师，性格沉稳，坐骑为青鸾。', 40, 0);
INSERT INTO `cfg_card` VALUES (304, '崇黑虎', 30, 4, 400, 530, NULL, 120, 150, '殷商北伯侯崇侯虎的弟弟，被封曹州侯，宠物为铁嘴神鹰。', NULL, 1);
INSERT INTO `cfg_card` VALUES (305, '邓婵玉', 30, 4, 430, 550, 30, NULL, 130, '邓九公之女，貌美如花，性格刚烈，后嫁土行孙。', NULL, 0);
INSERT INTO `cfg_card` VALUES (306, '龙吉公主', 30, 4, 390, 500, 170, 70, 100, '昊天上帝与瑶池金母之女，洪锦之妻，掌管水的女仙，封神授为红鸾星君。', NULL, 0);
INSERT INTO `cfg_card` VALUES (307, '九天玄女', 30, 4, 420, 620, 40, 410, 370, '精通兵法，是天上掌管各种天书和传授兵法的神。', NULL, 0);
INSERT INTO `cfg_card` VALUES (308, '魔礼红', 30, 4, 420, 610, NULL, 200, 70, '魔家四将之一，手执“混元伞”。封神授广目天王。', 60, 0);
INSERT INTO `cfg_card` VALUES (309, '碧霄', 30, 3, 250, 330, 70, NULL, 130, '赵公明之妹，排行第三，坐骑花翎鸟。', 40, 0);
INSERT INTO `cfg_card` VALUES (310, '琼霄', 30, 3, 240, 350, 70, NULL, 160, '赵公明之妹，排行第二，坐骑鸿鹄。', 40, 0);
INSERT INTO `cfg_card` VALUES (311, '袁天君', 30, 3, 220, 310, NULL, 420, 240, '十绝阵中寒冰阵阵主。', 10, 0);
INSERT INTO `cfg_card` VALUES (312, '姚天君', 30, 3, 230, 290, NULL, 210, 160, '十绝阵中落魂阵阵主，曾将姜子牙的三魂七魄摄去。', 10, 0);
INSERT INTO `cfg_card` VALUES (313, '洪锦', 30, 3, 260, 330, 170, NULL, 320, '殷商三山关总兵，精通奇门遁甲，与龙吉公主结良缘。封神后成为龙德星君。', NULL, 0);
INSERT INTO `cfg_card` VALUES (314, '李兴霸', 30, 3, 230, 320, 40, NULL, 260, '截教门人，西海九龙岛练气士，与王魔、杨森、高友乾合称“九龙岛四圣”。', 90, 0);
INSERT INTO `cfg_card` VALUES (315, '鲸龙', 30, 2, 140, 230, 40, 170, NULL, '每次它的出现都会带来风雨交加，所以总有不明觉厉的百姓像它与真龙搞混。', NULL, 0);
INSERT INTO `cfg_card` VALUES (316, '邑姜', 30, 2, 130, 200, 70, NULL, 240, '姜姓，齐太公吕尚之女。为周朝开国之君周武王之王后，周成王、唐叔虞之母。', NULL, 0);
INSERT INTO `cfg_card` VALUES (317, '神龟', 30, 2, 100, 260, NULL, 120, 270, '据说有好几代国王伺养它试图知道它到底能活到几岁，后来都先它而去了。', NULL, 0);
INSERT INTO `cfg_card` VALUES (318, '鱼人女卫', 30, 2, 160, 200, 410, NULL, NULL, '鱼人是朔北水泽的特殊人种，女卫们信奉团结就是力量。', NULL, 0);
INSERT INTO `cfg_card` VALUES (319, '神鲸', 30, 2, 150, 230, NULL, 270, NULL, '鱼人最早捕食这个物种就发现所有法术都会被厚实的皮弹回，所以称之为神鲸。', NULL, 0);
INSERT INTO `cfg_card` VALUES (320, '鱼人侍女', 30, 2, 120, 180, NULL, NULL, 250, '鱼人是朔北水泽的特殊人种，只有长相温柔的才能吸收为礼仪小姐。', NULL, 0);
INSERT INTO `cfg_card` VALUES (321, '白熊', 30, 1, 100, 160, NULL, 420, NULL, '极北之地才有的庞大物种，主要特点是临死前会很生气。', NULL, 0);
INSERT INTO `cfg_card` VALUES (322, '鱼人女妖', 30, 1, 100, 140, NULL, 70, NULL, '鱼人是朔北水泽的特殊人种，能进化成妖也是一种缘份。', NULL, 0);
INSERT INTO `cfg_card` VALUES (323, '黑虎', 30, 1, 110, 160, NULL, 440, NULL, '种族一般，但颜色变黑后果然很吓人。', NULL, 0);
INSERT INTO `cfg_card` VALUES (324, '小仙女', 30, 1, 60, 90, 260, NULL, 250, '仙女虽小，法术俱全。', NULL, 0);
INSERT INTO `cfg_card` VALUES (325, '崇侯虎', 30, 5, 920, 680, 120, 200, 220, '商纣天下八百镇诸侯之一，北伯侯。', NULL, 3);
INSERT INTO `cfg_card` VALUES (326, '九尾狐王', 30, 4, 380, 520, 270, NULL, 130, '古代神话传说中的奇兽。善变化，蛊惑，修炼人形后常用其婴儿哭泣声引人。', NULL, 3);
INSERT INTO `cfg_card` VALUES (327, '鬼道士', 30, 3, 260, 270, 110, NULL, 160, '很少人知道他，之所以被称为鬼道士，主要是因为他长得跟鬼一样。', NULL, 2);
INSERT INTO `cfg_card` VALUES (328, '玉面银狐', 30, 5, 590, 760, 130, 260, 100, '三千年修得的狐狸精中的极品，无法轻易被杀死。', NULL, 2);
INSERT INTO `cfg_card` VALUES (329, '碧云', 30, 2, 140, 210, NULL, 50, 470, '龙吉公主的徒儿。', NULL, 3);
INSERT INTO `cfg_card` VALUES (330, '厉鬼', 30, 1, 110, 120, NULL, 500, NULL, '她们坚信自己死了战斗力会更强大。', NULL, 3);
INSERT INTO `cfg_card` VALUES (331, '无当圣母', 30, 4, 400, 520, 50, 80, 490, '截教通天教主坐下四位首席大弟子之一，万仙阵出场，来历与去向都十分神秘。', NULL, 2);
INSERT INTO `cfg_card` VALUES (332, '雉鸡精', 30, 2, 160, 200, NULL, 130, NULL, '鸡精中比较高贵的一种，最讨厌别人称她们为野鸡。', NULL, 3);
INSERT INTO `cfg_card` VALUES (333, '韩毒龙', 30, 3, 250, 310, 40, 430, NULL, '道行天尊弟子，从小就学会了高深的擒龙之术，可特么龙在哪呢？', NULL, 1);
INSERT INTO `cfg_card` VALUES (334, '敖广', 30, 4, 440, 550, 70, 450, 300, '东海老王，在认识孙悟空之前，独家掌握着一柱擎天的神器。', NULL, 2);
INSERT INTO `cfg_card` VALUES (335, '敖闰', 30, 4, 420, 490, 30, 50, 410, '西海龙王，因为身怀宝珠，所以管得特别宽。', NULL, 2);
INSERT INTO `cfg_card` VALUES (336, '敖顺', 30, 4, 410, 470, 120, 160, 240, '北海龙王，生性冷冰冰，掌管雪、冰雹、冷冻、冰霜等，难怪没有子嗣。', NULL, 2);
INSERT INTO `cfg_card` VALUES (337, '螭龙', 30, 4, 400, 480, 120, 260, 480, '水精，能避火，龙的家族中层干部。', NULL, 2);
INSERT INTO `cfg_card` VALUES (338, '乌云仙', 30, 4, 490, 520, 120, NULL, 140, '虽然是只金须鳌鱼，武器却是大锤，凶猛得很。', NULL, 2);
INSERT INTO `cfg_card` VALUES (339, '长耳定光仙', 30, 4, 410, 500, 70, 50, 130, '主动背叛截教的一个二代弟子，逃到西方佛教，被准提、接引列为定光欢喜佛。', NULL, 2);
INSERT INTO `cfg_card` VALUES (340, '余元', 30, 4, 390, 450, 210, 440, 200, '余化的师傅，金灵圣母门下，拥有金刚不坏之身，但却是封神里最没脑的代表。', NULL, 2);
INSERT INTO `cfg_card` VALUES (401, '金銮火凤', 40, 5, 610, 780, 40, 180, 110, '凤山的主人，涅槃后能招唤天火毁灭一切。', NULL, 0);
INSERT INTO `cfg_card` VALUES (402, '火灵圣母', 40, 5, 580, 720, 80, 240, 200, '截教关键人物，亲自训练出三千能驾驭三昧火的火龙兵，更兼隐身特技，单挑群战都表现非凡。', NULL, 1);
INSERT INTO `cfg_card` VALUES (403, '金光圣母', 40, 4, 470, 490, 170, 180, 350, '十绝阵中金光阵阵主。', 10, 0);
INSERT INTO `cfg_card` VALUES (404, '杨任', 40, 4, 470, 500, 180, 250, 300, '被纣王挖眼后被道德真君所救，使其眼中长手，手中长眼，关键时刻洞察出钻地的张奎。', NULL, 0);
INSERT INTO `cfg_card` VALUES (405, '胡喜媚', 40, 4, 380, 540, 80, 110, 130, '轩辕三妖中排行第二，千年九头雉鸡精，后以九尾狐狸精的义妹身份入宫，并成为纣王宠妃。', 50, 0);
INSERT INTO `cfg_card` VALUES (406, '王天君', 40, 4, 480, 520, 430, 200, 210, '十绝阵中红水阵阵主。', 10, 1);
INSERT INTO `cfg_card` VALUES (407, '魔礼青', 40, 4, 530, 520, NULL, 180, 200, '魔家四将之一，神通广大，手持青锋宝剑，封神授增长天王。', 60, 0);
INSERT INTO `cfg_card` VALUES (408, '鄂顺', 40, 4, 470, 460, 300, 20, 400, '鄂崇禹长子。封神授北斗星官之贪狼星。', NULL, 0);
INSERT INTO `cfg_card` VALUES (409, '王魔', 40, 3, 310, 280, NULL, 80, 140, '截教门人，西海九龙岛练气士，与杨森、高友乾、李兴霸合称“九龙岛四圣”。', 90, 0);
INSERT INTO `cfg_card` VALUES (410, '张天君', 40, 3, 350, 270, 420, NULL, 160, '十绝阵中红砂阵阵主。', 10, 0);
INSERT INTO `cfg_card` VALUES (411, '苏护', 40, 3, 320, 310, 20, 120, NULL, '北伯侯治下冀州侯。商末大将。苏妲己的父亲。', NULL, 0);
INSERT INTO `cfg_card` VALUES (412, '罗宣', 40, 3, 280, 290, 170, 180, NULL, '火德星君，神通广大，曾火烧西歧城。', NULL, 0);
INSERT INTO `cfg_card` VALUES (413, '郑伦', 40, 3, 270, 320, NULL, 430, 240, '守护寺庙的门神之一，形象威武凶猛，能鼻哼白气制敌。', 70, 0);
INSERT INTO `cfg_card` VALUES (414, '陈奇', 40, 3, 280, 280, NULL, 160, 180, '守护寺庙的门神之一，形象威武凶猛，能口哈黄气擒将。', 70, 0);
INSERT INTO `cfg_card` VALUES (415, '董天君', 40, 2, 220, 190, 40, NULL, 210, '十绝阵中风吼阵阵主。', 10, 0);
INSERT INTO `cfg_card` VALUES (416, '白天君', 40, 2, 220, 190, 430, NULL, 180, '十绝阵中烈焰阵阵主。', 10, 0);
INSERT INTO `cfg_card` VALUES (417, '五云驼', 40, 2, 200, 190, NULL, 170, 70, '截教门下金灵圣母及余元的坐骑。', NULL, 0);
INSERT INTO `cfg_card` VALUES (418, '秦天君', 40, 2, 190, 230, 40, NULL, 500, '十绝阵中天绝阵阵主。', 10, 0);
INSERT INTO `cfg_card` VALUES (419, '花狐貂', 40, 2, 190, 190, 40, 450, NULL, '佳梦关魔礼寿的法宝。宝囊中如同白鼠。飞出可以吃人，最大的悲剧是误食了杨戬。', NULL, 0);
INSERT INTO `cfg_card` VALUES (420, '南鄂巫女', 40, 2, 170, 190, 410, NULL, NULL, '能在理论上权威解读南鄂巫术，但没人见过她们施法。', NULL, 0);
INSERT INTO `cfg_card` VALUES (421, '火龙兵', 40, 1, 130, 100, NULL, 210, NULL, '火灵圣母训练的特种兵，人人都掌握三昧真火。', NULL, 0);
INSERT INTO `cfg_card` VALUES (422, '拜火教徒', 40, 1, 100, 130, NULL, NULL, 180, '南方百姓少有不崇信火的。', NULL, 0);
INSERT INTO `cfg_card` VALUES (423, '赤烟驹', 40, 1, 90, 110, NULL, 170, NULL, '火德星君正神罗宣的坐骑。', NULL, 0);
INSERT INTO `cfg_card` VALUES (424, '鬼兵', 40, 1, 80, 70, NULL, 80, 110, '杀死我们吧，反正我们本来就是鬼。', NULL, 0);
INSERT INTO `cfg_card` VALUES (425, '鄂崇禹', 40, 5, 980, 960, 180, 60, 220, '商纣天下八百镇诸侯之一，南伯侯。总镇南方二百路诸侯，封神授天马星。', NULL, 3);
INSERT INTO `cfg_card` VALUES (426, '巡夜女使', 40, 4, 490, 480, 40, 20, 210, '敢于晚上出来巡夜的杰出女性，不止是胆大还必须有真功夫。', NULL, 2);
INSERT INTO `cfg_card` VALUES (427, '梅伯', 40, 3, 280, 350, NULL, 490, 460, '商朝司徒，赠太师，因冒颜进谏被纣王施以殖醢酷刑。', NULL, 2);
INSERT INTO `cfg_card` VALUES (428, '赤云', 40, 2, 130, 220, NULL, 450, 470, '龙吉公主的徒儿。', NULL, 3);
INSERT INTO `cfg_card` VALUES (429, '星月女卫', 40, 4, 430, 510, 170, 270, 110, '巡夜女使的闺蜜，相比巡夜，她更喜欢驱动星月之光照亮黑夜。', NULL, 2);
INSERT INTO `cfg_card` VALUES (430, '孔雀明王', 40, 5, 720, 720, 270, 240, 510, '即殷商元帅孔宣，身怀五色神光绝技，手下败将有姜子牙、哪吒、杨戬等。', NULL, 3);
INSERT INTO `cfg_card` VALUES (431, '夜游神', 40, 4, 460, 500, 120, 500, 410, '黑夜给了她黑色的面罩，以致没有人见过她神秘而幽怨的真面目。', NULL, 2);
INSERT INTO `cfg_card` VALUES (432, '薛恶虎', 40, 3, 290, 290, 30, 50, NULL, '道行天尊弟子，不要被名字吓到，其实他的外号是好喵。', NULL, 1);
INSERT INTO `cfg_card` VALUES (433, '五夷散人', 40, 4, 480, 500, 280, 270, 410, '都以为他带的落宝金钱是给大家发红包的，其实是来抢红包的。', NULL, 2);
INSERT INTO `cfg_card` VALUES (434, '敖钦', 40, 4, 470, 520, 180, 170, 260, '南海龙王，是条赤龙，控制着火灾、人间二昧真火和闪电。', NULL, 3);
INSERT INTO `cfg_card` VALUES (435, '天花娘娘', 40, 4, 460, 510, 560, 530, NULL, '想不长痘，求我呀。惹我生气，叫你浑身都是痘！', NULL, 3);
INSERT INTO `cfg_card` VALUES (501, '闻仲', 50, 5, 680, 880, 290, 440, 150, '纣王朝中太师，三朝元老大臣，文武双全，威仪并重。', NULL, 1);
INSERT INTO `cfg_card` VALUES (502, '黄飞虎', 50, 5, 600, 800, 20, 140, 230, '殷商武成王，后反出朝歌，封神授泰山神。', 20, 0);
INSERT INTO `cfg_card` VALUES (503, '山神', 50, 4, 500, 650, 60, NULL, 360, '山中之主，常啸聚豪杰，骑虎出巡。', NULL, 0);
INSERT INTO `cfg_card` VALUES (504, '殷郊', 50, 4, 450, 580, 160, NULL, 140, '纣王长子，险被纣王所杀，学得一身法术，带上仙家至宝后又受申公豹蛊惑反投纣王阵营。', NULL, 1);
INSERT INTO `cfg_card` VALUES (505, '邬文化', 50, 4, 500, 580, NULL, 120, 200, '纣王军中一个身高数丈的大汉，使用的一根排扒木作为兵器，封神授力士星。', NULL, 0);
INSERT INTO `cfg_card` VALUES (506, '魔礼寿', 50, 4, 450, 580, 30, NULL, 200, '魔家四将之一，手持双鞭，囊里“紫金花狐貂”，封神授持国天王。', 60, 0);
INSERT INTO `cfg_card` VALUES (507, '土行孙', 50, 4, 430, 520, 190, NULL, 450, '玉虚十二仙之一惧留孙的大弟子，身材矮小，以棍子为武器，擅长地下行走之术。', NULL, 0);
INSERT INTO `cfg_card` VALUES (508, '石矶娘娘', 50, 4, 400, 540, 40, 430, 80, '截教通天教主的徒弟，原形为石头，座骑是青鸾，封神授月游星。', NULL, 0);
INSERT INTO `cfg_card` VALUES (509, '邓九公', 50, 3, 330, 390, NULL, NULL, 230, '成汤三山关总兵，邓婵玉之父。', NULL, 0);
INSERT INTO `cfg_card` VALUES (510, '商容', 50, 3, 250, 320, NULL, 200, 310, '商朝纣王时代的丞相，仁德无缺，因谏纣王撞柱而死。', NULL, 0);
INSERT INTO `cfg_card` VALUES (511, '龙须虎', 50, 3, 270, 360, 120, NULL, 30, '一只既有点像虎又有点像龙的灵物，体型硕大，出手有石。', NULL, 0);
INSERT INTO `cfg_card` VALUES (512, '高友乾', 50, 3, 260, 320, 40, NULL, 80, '截教门人，西海九龙岛练气士，与王魔、杨森、李兴霸合称“九龙岛四圣”。', 90, 0);
INSERT INTO `cfg_card` VALUES (513, '殷洪', 50, 3, 250, 260, 290, NULL, 100, '纣王次子。与其兄殷郊经历相似，受申公豹蛊惑后反回纣王阵营。', NULL, 0);
INSERT INTO `cfg_card` VALUES (514, '赵天君', 50, 3, 250, 330, 170, NULL, 240, '十绝阵中地烈阵阵主。', 10, 0);
INSERT INTO `cfg_card` VALUES (515, '费仲', 50, 2, 150, 190, 280, NULL, NULL, '奉御宣中谏大夫。奸臣。', NULL, 0);
INSERT INTO `cfg_card` VALUES (516, '张桂芳', 50, 2, 170, 230, 300, NULL, 160, '殷商青龙关总兵，武艺过人，同时精通幻术，对纣王忠心耿耿，封神授丧门星。', NULL, 0);
INSERT INTO `cfg_card` VALUES (517, '方弼', 50, 2, 180, 230, NULL, 200, 300, '商朝殷纣王的镇殿将军，封神授太岁部下日值众星之显道神。', 110, 0);
INSERT INTO `cfg_card` VALUES (518, '方相', 50, 2, 180, 270, 210, NULL, 120, '商朝殷纣王的镇殿将军，封神授太岁部下日值众星之开路神。', 110, 0);
INSERT INTO `cfg_card` VALUES (519, '陛犴', 50, 2, 180, 230, NULL, 270, NULL, '传说中的龙九子之一，排行第七。平生好讼，却又有威力。', NULL, 0);
INSERT INTO `cfg_card` VALUES (520, '金鳖岛散仙', 50, 2, 170, 220, 410, NULL, NULL, '岛上修行，练就联攻的技能。', NULL, 0);
INSERT INTO `cfg_card` VALUES (521, '殷商舞女', 50, 1, 80, 100, NULL, 240, NULL, '超凡脱俗的舞技总能让小伙伴们惊呆了。', NULL, 0);
INSERT INTO `cfg_card` VALUES (522, '殷商死士', 50, 1, 130, 160, NULL, NULL, 300, '本为狱囚，经洗脑训练已随时准备好为守护朝歌而死。', NULL, 0);
INSERT INTO `cfg_card` VALUES (523, '花斑豹', 50, 1, 120, 150, NULL, 450, NULL, '特别矫健的身躯使人往往无法捕捉其运动的轨迹。', NULL, 0);
INSERT INTO `cfg_card` VALUES (524, '殷商兵', 50, 1, 110, 140, 20, NULL, NULL, '战斗力强大的中土百战之师。', NULL, 0);
INSERT INTO `cfg_card` VALUES (525, '纣王', 50, 5, 1000, 940, 140, 200, 220, '殷商的最高统治者，通过不断刷新底线以证明自己的强大和绝对权威。', NULL, 3);
INSERT INTO `cfg_card` VALUES (526, '比干', 50, 4, 410, 530, 270, 300, 410, '殷商亚相。被妲己挖心而死。封神授文曲星。', NULL, 2);
INSERT INTO `cfg_card` VALUES (527, '张奎', 50, 5, 560, 810, 30, 190, 460, '殷商渑池县守将，妻子是高兰英。善地行术，两擒杨戬，黄飞虎、崇黑虎、土行孙皆死于其手，封神授七杀星。', NULL, 3);
INSERT INTO `cfg_card` VALUES (528, '乌鸦兵', 50, 1, 120, 90, NULL, 490, NULL, '郑伦手下养3000乌鸦兵，擅长缚敌。', NULL, 3);
INSERT INTO `cfg_card` VALUES (529, '晁田', 50, 3, 270, 350, 410, NULL, NULL, '（纣王侍卫）不要惹我，我兄弟晁雷很厉害的。', NULL, 3);
INSERT INTO `cfg_card` VALUES (530, '晁雷', 50, 3, 250, 360, 30, NULL, 430, '（纣王侍卫）我哥叫晁田，有他罩着我什么都不怕。', NULL, 3);
INSERT INTO `cfg_card` VALUES (531, '尤浑', 50, 2, 160, 180, 430, NULL, NULL, '纣王手下奸臣，与费仲齐名，民间说他尤其浑蛋。', NULL, 3);
INSERT INTO `cfg_card` VALUES (532, '龟灵圣母', 50, 4, 340, 680, 80, 30, 130, '身为截教三大圣母，因为太招蚊子,连精魄都被吸走了，最后竟没混上封神榜。', NULL, 2);
INSERT INTO `cfg_card` VALUES (533, '山魈王', 50, 4, 480, 660, 120, 20, 80, '传说中是山里的独脚鬼怪，喜欢夜间出来吓人。', NULL, 2);
INSERT INTO `cfg_card` VALUES (534, '虬首仙', 50, 4, 520, 560, 440, 20, 10, '通天教主门下狮精，颜值凶猛，攻击力也超强。', NULL, 2);
INSERT INTO `cfg_card` VALUES (535, '吕岳', 50, 4, 420, 550, 430, 550, 490, '九龙岛声名山炼气士，善用瘟疫法宝，被称为是瘟神鼻祖。', NULL, 2);
INSERT INTO `cfg_card` VALUES (536, '叛道童子', 50, 1, 100, 100, NULL, NULL, 530, '她尝试倒练神功，没想到竟领悟封咒秘技。', NULL, 3);
COMMIT;

-- ----------------------------
-- Table structure for cfg_card_group
-- ----------------------------
DROP TABLE IF EXISTS `cfg_card_group`;
CREATE TABLE `cfg_card_group` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `short_name` varchar(10) NOT NULL,
  `memo` varchar(100) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡牌组合';

-- ----------------------------
-- Records of cfg_card_group
-- ----------------------------
BEGIN;
INSERT INTO `cfg_card_group` VALUES (10, '十绝阵', '十绝阵', '两人以上在场，每回合给对方召唤师造成在场十绝阵成员总星级*150的伤害。');
INSERT INTO `cfg_card_group` VALUES (20, '黄家军', '黄家军', '两人以上在场，攻击各加300。');
INSERT INTO `cfg_card_group` VALUES (30, '陈塘家', '陈塘家', '两人以上在场，阵中全体卡牌攻防各加100。');
INSERT INTO `cfg_card_group` VALUES (40, '九曲黄河阵', '黄河阵', '三人同时在场，每回合将己方召唤师血量补满。');
INSERT INTO `cfg_card_group` VALUES (50, '迷魂阵', '迷魂阵', '三人同时在场，对方场上全体卡牌特技失效。');
INSERT INTO `cfg_card_group` VALUES (60, '四大天王', '四大天王', '三人在场，对方所有卡牌攻防永久各减150，四人同时在场，攻防永久各减300。');
INSERT INTO `cfg_card_group` VALUES (70, '哼哈二将', '哼哈二将', '两人同时在场，每回合使对方场上攻防最高的卡牌攻防值永久减半。');
INSERT INTO `cfg_card_group` VALUES (80, '千里眼顺风耳', '二绝', '两人同时在场，对方法术值永久损失1。');
INSERT INTO `cfg_card_group` VALUES (90, '九龙岛四圣', '九龙四圣', '两人在场，随机拉一张卡牌上场。三人以上在场，随机拉卡牌将空位填满。');
INSERT INTO `cfg_card_group` VALUES (100, '梅山七怪', '梅山七怪', '两人在场，每人攻防各加100，每多一人在场攻防再加100。');
INSERT INTO `cfg_card_group` VALUES (110, '左右门神', '左右门神', '两人同时在场，全体上场卡牌防御值各加150。');
INSERT INTO `cfg_card_group` VALUES (120, '四海龙王', '四海龙王', '三个以上在场，每人向对面卡牌施加威风技能。（无视金刚效果，可被回光）');
COMMIT;

-- ----------------------------
-- Table structure for cfg_card_skill
-- ----------------------------
DROP TABLE IF EXISTS `cfg_card_skill`;
CREATE TABLE `cfg_card_skill` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `name_pinyin` varchar(20) NOT NULL,
  `serial` int(11) NOT NULL,
  `value` int(11) NOT NULL COMMENT '法术价值：降临0 法术11 战斗5 被动3',
  `memo` varchar(100) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡牌技能';

-- ----------------------------
-- Records of cfg_card_skill
-- ----------------------------
BEGIN;
INSERT INTO `cfg_card_skill` VALUES (10, '奇袭', 'qixi', 200, 5, '可率先进行攻击，如击退对方卡牌则己方不损防御。');
INSERT INTO `cfg_card_skill` VALUES (20, '突袭', 'tuxi', 260, 5, '直接攻击对方召唤师时攻击力+80%。');
INSERT INTO `cfg_card_skill` VALUES (30, '飞狙', 'feiju', 50, 11, '破除对方随机1张卡牌100点永久防御，每升一阶增长50%的效果。');
INSERT INTO `cfg_card_skill` VALUES (40, '飞行', 'feixing', 51, 3, '有飞行技能的卡牌可以安放于云台位置。');
INSERT INTO `cfg_card_skill` VALUES (50, '拦截', 'lanjie', 130, 11, '破除对方云台位置的卡牌自身星级*(50~100)点永久防御，每升一阶增长50%的效果。');
INSERT INTO `cfg_card_skill` VALUES (60, '穿刺', 'chuanci', 20, 5, '击退对方卡牌后剩余攻击力攻击对方召唤师。');
INSERT INTO `cfg_card_skill` VALUES (70, '治愈', 'zhiyu', 370, 11, '每回合回复己方召唤师至少350点血量，每升一级最高可能回复量+100。');
INSERT INTO `cfg_card_skill` VALUES (80, '吸血', 'xixue', 320, 5, '每次击中对方卡牌后防御永久+60，每升一阶增加50%的效果。');
INSERT INTO `cfg_card_skill` VALUES (90, '封神', 'fengshen', 60, 11, '随机将1张自己坟场的卡牌拉回战场，如战场已满则进入手牌。');
INSERT INTO `cfg_card_skill` VALUES (100, '回魂', 'huihun', 91, 11, '随机将1张自己坟场的卡牌拉回手牌。');
INSERT INTO `cfg_card_skill` VALUES (110, '复活', 'fuhuo', 70, 3, '被击退后有70%的概率回到营地，营地已满则进入手牌。');
INSERT INTO `cfg_card_skill` VALUES (120, '无相', 'wuxiang', 290, 3, '面对属性克制卡牌无视其加成效果。');
INSERT INTO `cfg_card_skill` VALUES (130, '魅惑', 'meihuo', 180, 11, '对方场上随机1张卡牌70%概率掉头攻击自己的召唤师。');
INSERT INTO `cfg_card_skill` VALUES (140, '威风', 'weifeng', 280, 11, '对方场上随机1张卡牌一定概率回到手牌。');
INSERT INTO `cfg_card_skill` VALUES (150, '斥退', 'chitui', 10, 0, '上场时对方场上随机3张卡牌回到手牌。');
INSERT INTO `cfg_card_skill` VALUES (160, '妖术', 'yaoshu', 340, 0, '上场时对方场上随机1张卡牌进坟场。');
INSERT INTO `cfg_card_skill` VALUES (170, '闪电', 'shandian', 210, 11, '破除对方随机1张卡牌至少100点防御，每升一级最高可能破除的防御+50。');
INSERT INTO `cfg_card_skill` VALUES (180, '业火', 'yehuo', 350, 11, '破除对方所有卡牌50~150点防御，每升一阶增长50%的效果。');
INSERT INTO `cfg_card_skill` VALUES (190, '钻地', 'zuandi', 390, 5, '无视自己的对位，直接攻击对方当前防御最弱的卡牌。');
INSERT INTO `cfg_card_skill` VALUES (200, '金刚', 'jingang', 120, 3, '免疫所有非永久效果的负面技能。');
INSERT INTO `cfg_card_skill` VALUES (210, '噬魂', 'shihun', 210, 11, '以自身卡牌星级*150的攻击力攻击对方召唤师。');
INSERT INTO `cfg_card_skill` VALUES (220, '王者', 'wangzhe', 270, 11, '布阵结束后如己方阵型有空位，则随机召唤同元素卡牌填满。');
INSERT INTO `cfg_card_skill` VALUES (230, '主帅', 'zhushuai', 380, 0, '上场时随机使1张卡牌无需消耗法术值上场。');
INSERT INTO `cfg_card_skill` VALUES (240, '枷锁', 'jiasuo', 110, 11, '随机封锁对方1张卡牌一回合。');
INSERT INTO `cfg_card_skill` VALUES (250, '得道', 'dedao', 40, 11, '己方所有卡牌次回合召唤成本-1。');
INSERT INTO `cfg_card_skill` VALUES (260, '修仙', 'xiuxian', 310, 0, '上场时本场战斗自身的法术值永久增加1。');
INSERT INTO `cfg_card_skill` VALUES (270, '回光', 'huiguang', 90, 3, '自身受到法术攻击，则施法者受到同样效果。');
INSERT INTO `cfg_card_skill` VALUES (280, '招财', 'zhaocai', 360, 3, '如上场，战斗所得金钱多80%。');
INSERT INTO `cfg_card_skill` VALUES (290, '混元', 'hunyuan', 100, 0, '上场时本场战斗对方的法术值永久损失2。');
INSERT INTO `cfg_card_skill` VALUES (300, '死士', 'sishi', 230, 3, '被击退后，给敌方召唤师减少相当于自身攻击力两倍的血量。');
INSERT INTO `cfg_card_skill` VALUES (310, '生金', 'shengjin', 201, 11, '首回合，所有金属性卡牌防御力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (320, '生木', 'shengmu', 202, 11, '首回合，所有木属性卡牌防御力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (330, '生水', 'shengshui', 203, 11, '首回合，所有水属性卡牌防御力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (340, '生火', 'shenghuo', 204, 11, '首回合，所有火属性卡牌防御力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (350, '生土', 'shengtu', 205, 11, '首回合，所有土属性卡牌防御力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (360, '强金', 'qiangjin', 191, 11, '首回合，所有金属性卡牌攻击力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (370, '强木', 'qiangmu', 192, 11, '首回合，所有木属性卡牌攻击力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (380, '强水', 'qiangshui', 193, 11, '首回合，所有水属性卡牌攻击力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (390, '强火', 'qianghuo', 194, 11, '首回合，所有火属性卡牌攻击力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (400, '强土', 'qiangtu', 195, 11, '首回合，所有土属性卡牌攻击力上升其本身星级*80，以后每回合星级*30。');
INSERT INTO `cfg_card_skill` VALUES (410, '联攻', 'liangong', 140, 3, '每有一张同元素卡牌在场，则攻防各上升卡牌自身星级*50。');
INSERT INTO `cfg_card_skill` VALUES (420, '死斗', 'sidou', 220, 3, '防守如小于对方卡牌的攻击，则自身攻击翻倍。');
INSERT INTO `cfg_card_skill` VALUES (430, '流毒', 'liudu', 160, 11, '破除自身星级数量的卡牌每张60点永久防御，每升一阶增长50%的效果。');
INSERT INTO `cfg_card_skill` VALUES (440, '嗜血', 'sixue', 240, 5, '每次击中对方卡牌后攻击永久+60，每升一阶增加50%的效果。');
INSERT INTO `cfg_card_skill` VALUES (450, '灵动', 'lingdong', 150, 3, '60%的概率避开对方的攻击。');
INSERT INTO `cfg_card_skill` VALUES (460, '刚毅', 'gangyi', 80, 3, '威风、妖术、魅惑技能对其无效且反弹影响到施法术卡牌。');
INSERT INTO `cfg_card_skill` VALUES (470, '销魂', 'xiaohun', 300, 5, '每次攻击破除对方永久防御。');
INSERT INTO `cfg_card_skill` VALUES (480, '龙息', 'longxi', 170, 5, '攻击对方卡牌的同时，破除其左右卡牌自身攻击50%的防御。');
INSERT INTO `cfg_card_skill` VALUES (490, '死咒', 'sizhou', 250, 3, '被击退后攻击者的攻防永久减半。');
INSERT INTO `cfg_card_skill` VALUES (500, '怨灵', 'yanling', 330, 3, '被击退后，破除对方场上所有卡牌20%~30%防御，每升一阶增加5%的效果。');
INSERT INTO `cfg_card_skill` VALUES (510, '圣火', 'shenghuo', 220, 11, '破除对方所有卡牌150~450点防御，每升一阶增长50%的效果。');
INSERT INTO `cfg_card_skill` VALUES (520, '道法', 'daofa', 30, 11, '只要其在场上，己方场上所有其它卡牌攻防各加500。');
INSERT INTO `cfg_card_skill` VALUES (530, '封咒', 'fengzuo', 65, 0, '永久封禁对位卡牌的所有非上阵技能');
INSERT INTO `cfg_card_skill` VALUES (540, '暴击', 'baoji', 5, 5, '战斗中有70%的概率增加50%的攻击力');
INSERT INTO `cfg_card_skill` VALUES (550, '瘟君', 'wenjun', 285, 0, '上场时对方全体卡牌进入中毒状态，每轮损失80点永久防御。每升1阶增加50%效果。');
INSERT INTO `cfg_card_skill` VALUES (560, '入痘', 'rudou', 560, 11, '每回合使对方一张卡牌进入中毒状态，每轮损失80点永久防御，该状态可累加。每升一阶增加50%效果。');
COMMIT;

-- ----------------------------
-- Table structure for cfg_channel
-- ----------------------------
DROP TABLE IF EXISTS `cfg_channel`;
CREATE TABLE `cfg_channel` (
  `id` int(11) NOT NULL,
  `plat` int(3) NOT NULL,
  `plat_code` varchar(20) NOT NULL,
  `name` char(20) NOT NULL,
  `support_zf_account` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道';

-- ----------------------------
-- Records of cfg_channel
-- ----------------------------
BEGIN;
INSERT INTO `cfg_channel` VALUES (1, 1, '1', '新服务器测试', b'1');
INSERT INTO `cfg_channel` VALUES (10, 10, '10', 'Apple', b'1');
INSERT INTO `cfg_channel` VALUES (20, 20, '20', '91', b'0');
INSERT INTO `cfg_channel` VALUES (30, 30, 'ky', '快用', b'0');
INSERT INTO `cfg_channel` VALUES (40, 40, '40', '神话大富翁', b'1');
INSERT INTO `cfg_channel` VALUES (101, 101, '101', '101', b'1');
INSERT INTO `cfg_channel` VALUES (102, 102, '102', '102', b'1');
INSERT INTO `cfg_channel` VALUES (103, 103, '103', '103', b'1');
INSERT INTO `cfg_channel` VALUES (104, 104, '104', '104', b'1');
INSERT INTO `cfg_channel` VALUES (121, 121, '121', '121', b'1');
INSERT INTO `cfg_channel` VALUES (201, 201, '000023', '360', b'0');
INSERT INTO `cfg_channel` VALUES (202, 202, '000066', '小米', b'0');
INSERT INTO `cfg_channel` VALUES (203, 203, 'tx', '腾讯', b'0');
INSERT INTO `cfg_channel` VALUES (204, 204, '000002', '机锋', b'0');
INSERT INTO `cfg_channel` VALUES (205, 205, '000007', '91', b'0');
INSERT INTO `cfg_channel` VALUES (206, 206, '000215', '百度', b'0');
INSERT INTO `cfg_channel` VALUES (207, 207, '000003', '当乐', b'0');
INSERT INTO `cfg_channel` VALUES (208, 208, '000116', '豌豆荚', b'0');
INSERT INTO `cfg_channel` VALUES (209, 209, '000020', 'oppo', b'0');
INSERT INTO `cfg_channel` VALUES (210, 210, '000005', '安智', b'0');
INSERT INTO `cfg_channel` VALUES (211, 211, '000008', '木蚂蚁', b'0');
INSERT INTO `cfg_channel` VALUES (212, 212, 'lx', '联想', b'1');
INSERT INTO `cfg_channel` VALUES (213, 213, '000009', '应用汇', b'0');
INSERT INTO `cfg_channel` VALUES (214, 214, '110000', '百度云', b'0');
INSERT INTO `cfg_channel` VALUES (215, 215, 'uc', 'UC', b'0');
INSERT INTO `cfg_channel` VALUES (216, 216, '000054', '华为', b'0');
INSERT INTO `cfg_channel` VALUES (217, 217, 'br', '宝软', b'0');
INSERT INTO `cfg_channel` VALUES (218, 218, '000368', '步步高', b'0');
INSERT INTO `cfg_channel` VALUES (219, 219, '000014', '魅族', b'0');
INSERT INTO `cfg_channel` VALUES (220, 220, '000004', 'N多', b'0');
INSERT INTO `cfg_channel` VALUES (221, 221, '160002', '爱贝', b'0');
INSERT INTO `cfg_channel` VALUES (222, 222, 'ls', '乐视', b'1');
INSERT INTO `cfg_channel` VALUES (223, 223, '000800', '搜狗', b'0');
INSERT INTO `cfg_channel` VALUES (224, 224, '000551', 'htc', b'0');
INSERT INTO `cfg_channel` VALUES (225, 225, 'yx', '游讯1', b'1');
INSERT INTO `cfg_channel` VALUES (226, 226, 'yx1', '游讯2', b'1');
INSERT INTO `cfg_channel` VALUES (227, 227, 'yx2', '游讯3', b'1');
INSERT INTO `cfg_channel` VALUES (228, 228, 'yx3', '游讯4', b'1');
INSERT INTO `cfg_channel` VALUES (229, 229, 'yx4', '游讯5', b'1');
INSERT INTO `cfg_channel` VALUES (230, 230, 'yx5', '游讯6', b'1');
INSERT INTO `cfg_channel` VALUES (231, 231, 'gm', '怪猫', b'1');
INSERT INTO `cfg_channel` VALUES (232, 232, '160113', '全民助手', b'0');
INSERT INTO `cfg_channel` VALUES (233, 233, '001145', 'jinli', b'0');
INSERT INTO `cfg_channel` VALUES (234, 234, '160280', '酷派', b'0');
INSERT INTO `cfg_channel` VALUES (235, 235, '160192', '三星', b'0');
INSERT INTO `cfg_channel` VALUES (236, 236, '000108', '4399', b'0');
INSERT INTO `cfg_channel` VALUES (237, 237, 'kuqu', '酷趣', b'0');
INSERT INTO `cfg_channel` VALUES (238, 238, '000986', '乐嗨嗨', b'0');
INSERT INTO `cfg_channel` VALUES (239, 239, 'ypw', '游品味', b'0');
INSERT INTO `cfg_channel` VALUES (240, 240, '160285', '游戏fan（安久）', b'0');
INSERT INTO `cfg_channel` VALUES (241, 241, '160146', 'tt游戏', b'0');
INSERT INTO `cfg_channel` VALUES (242, 242, '000914', '益玩', b'0');
INSERT INTO `cfg_channel` VALUES (243, 243, '001201', '果盘助手', b'0');
INSERT INTO `cfg_channel` VALUES (244, 244, '160442', '3011.cn', b'0');
INSERT INTO `cfg_channel` VALUES (245, 245, '111226', '虫虫游戏', b'0');
INSERT INTO `cfg_channel` VALUES (246, 246, '160115', '夜神', b'0');
INSERT INTO `cfg_channel` VALUES (247, 247, '160499', '牛刀', b'0');
INSERT INTO `cfg_channel` VALUES (248, 248, '160631', '天宇', b'0');
INSERT INTO `cfg_channel` VALUES (249, 249, '160096', '飓风', b'0');
INSERT INTO `cfg_channel` VALUES (1001, 1001, '1001', '1001', b'1');
INSERT INTO `cfg_channel` VALUES (1002, 1002, '1002', '1002', b'1');
INSERT INTO `cfg_channel` VALUES (1003, 1003, '1003', '1003', b'1');
INSERT INTO `cfg_channel` VALUES (1004, 1004, '1004', '1004', b'1');
INSERT INTO `cfg_channel` VALUES (1005, 1005, '1005', '1005', b'1');
INSERT INTO `cfg_channel` VALUES (1006, 1006, '1006', '1006', b'1');
INSERT INTO `cfg_channel` VALUES (1007, 1007, '1007', '1007', b'1');
INSERT INTO `cfg_channel` VALUES (1008, 1008, '1008', '1008', b'1');
INSERT INTO `cfg_channel` VALUES (1009, 1009, '1009', '1009', b'1');
INSERT INTO `cfg_channel` VALUES (1010, 1010, '1010', '1010', b'1');
INSERT INTO `cfg_channel` VALUES (1011, 1011, '1011', '1011', b'1');
INSERT INTO `cfg_channel` VALUES (10001, 10001, '10001', '10001', b'1');
INSERT INTO `cfg_channel` VALUES (10002, 10002, '10002', 'taptap', b'1');
INSERT INTO `cfg_channel` VALUES (1000001, 215, '000255', 'uc', b'0');
COMMIT;

-- ----------------------------
-- Table structure for cfg_city
-- ----------------------------
DROP TABLE IF EXISTS `cfg_city`;
CREATE TABLE `cfg_city` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `address1` int(11) NOT NULL,
  `address2` int(11) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `property` int(11) DEFAULT NULL COMMENT '城市属性',
  `drop_cards` varchar(50) DEFAULT NULL COMMENT '可掉落卡牌',
  `solider_level` int(11) DEFAULT NULL COMMENT '守将等级',
  `soliders` varchar(300) DEFAULT NULL COMMENT '守城卡牌',
  `specials` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市\n一个建筑可能占两格 地址1 地址2';

-- ----------------------------
-- Records of cfg_city
-- ----------------------------
BEGIN;
INSERT INTO `cfg_city` VALUES (117, '仙人洞', 117, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (118, '孟津', 118, 119, 230, 20, '221,220,217,213,207', 25, '204,0;211,0;212,0;215,0;216,0;217,0;218,0;210,0;209,0;214,0;213,0;220,0;222,5;221,5;207,0', '1,1,1,22,22,22,22,16,16,32');
INSERT INTO `cfg_city` VALUES (120, '金鳌岛', 120, 121, 210, 30, '323,321,320,311', 1, '311,0;30,2,0;20,2,0;20,2,0;30,1,0;30,1,0;30,1,0;20,1,0;20,1,0;20,1,0', '1,6,22,22,22,22,12,12,31');
INSERT INTO `cfg_city` VALUES (122, '野地', 122, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (123, '女娲庙', 123, 124, 160, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (125, '森林', 125, NULL, 120, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (126, '徐州', 126, 127, 240, 30, '321,315,309,312,303', 46, '302,5;201,5;30,4,5;30,4,5;30,4,5;30,4,5;20,4,5;20,4,5;20,4,5;20,4,5;30,3,5;30,3,5;331,5;225,5;310,10;309,10;315,10;303,10', '6,6,16,16,16,16,16,22,31,32');
INSERT INTO `cfg_city` VALUES (128, '客栈', 128, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (129, '福地', 129, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (130, '野地', 130, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (217, '福地', 217, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (230, '蓬莱', 230, 330, 230, 10, '123,116,115,109,105', 25, '10,3,0;10,3,0;109,5;10,3,0;214,0;213,0;20,3,0;10,2,0;115,5;20,2,0;20,2,0;10,1,5;123,5;20,1,5;105,0', '1,1,1,12,12,12,12,22,22,32');
INSERT INTO `cfg_city` VALUES (317, '野地', 317, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (417, '白鹿岛', 417, 517, 240, 40, '421,419,409,410,403', 46, '403,5;406,5;312,5;219,5;415,5;311,5;418,5;410,5;514,5;416,5;401,5;40,3,5;20,3,5;20,3,5;227,5;426,5;419,10;409,10', '6,6,12,12,12,22,22,22,31,32');
INSERT INTO `cfg_city` VALUES (430, '曹邑', 430, 530, 210, 50, '522,523,515,512', 1, '50,2,0;50,2,0;20,2,0;20,2,0;50,1,0;50,1,0;50,1,0;20,1,0;20,1,0;20,1,0', '1,1,16,16,16,16,22,22,31');
INSERT INTO `cfg_city` VALUES (617, '芒池', 617, 717, 210, 20, '221,224,220', 1, '220,0;221,0;222,0;223,0;224,0', '1,1,12,12,12,12,16,16,31');
INSERT INTO `cfg_city` VALUES (630, '村庄', 630, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (730, '人方', 730, 830, 220, 50, '522,519,518,512', 14, '514,0;512,0;518,0;50,2,0;50,2,0;20,2,1;20,2,1;20,2,1;519,5;522,5;20,1,5;20,1,5', '6,6,12,22,22,22,16,16,31');
INSERT INTO `cfg_city` VALUES (817, '福临轩', 817, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (917, '村庄', 917, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (918, '野地', 918, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (919, '斟灌', 919, 920, 210, 10, '123,121,119,110', 1, '10,2,1;20,2,0;10,1,0;20,1,0;20,1,0;20,1,0', '1,6,12,12,16,22,12,22,31');
INSERT INTO `cfg_city` VALUES (921, '客栈', 921, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (922, '汜叶', 922, 923, 220, 30, '323,317,315,311', 14, '312,0;311,0;30,2,0;30,2,0;315,0;20,2,1;20,2,1;20,2,1;317,5;30,1,5;323,5;20,1,5', '6,1,12,12,12,16,16,22,31');
INSERT INTO `cfg_city` VALUES (924, '森林', 924, NULL, 120, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (925, '福地', 925, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (926, '游商馆', 926, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (927, '野地', 927, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (928, '九龙岛', 928, 929, 230, 20, '224,219,216,214,203', 25, '210,0;512,0;409,0;314,0;219,0;40,3,0;20,3,0;40,2,0;40,2,0;214,0;216,0;40,1,5;40,1,5;224,5;203,0', '1,1,6,16,16,16,16,22,22,32');
INSERT INTO `cfg_city` VALUES (930, '客栈', 930, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1017, '兖州', 1017, 1117, 240, 20, '222,216,213,214,207', 46, '202,5;20,4,5;20,4,5;20,4,5;20,4,5;20,4,5;20,4,5;20,3,8;20,3,8;20,3,8;20,3,8;20,2,10;20,2,10;313,10;20,3,8;207,5;213,10;214,10', '6,6,16,16,16,16,16,22,31,32');
INSERT INTO `cfg_city` VALUES (1024, '东鲁', 1024, 1124, 250, 20, '211,212,209,204,206,201', 62, '226,10;202,10;201,10;203,10;204,10;205,10;206,10;207,10;208,10;225,10;307,10;313,10;211,10;227,10;209,10;228,10;105,10;502,10;111,10;107,10', '6,6,16,16,22,22,22,31,31,32');
INSERT INTO `cfg_city` VALUES (1030, '野地', 1030, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1130, '黑市', 1130, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1217, '野地', 1217, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1224, '森林', 1224, NULL, 120, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1230, '森林', 1230, NULL, 120, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1317, '斟寻', 1317, 1417, 210, 40, '424,423,417,409', 1, '412,0;409,0;40,2,0;20,2,0;423,0;40,1,0;20,1,1;417,0;20,1,0;424,0', '1,6,12,12,16,22,12,22,31');
INSERT INTO `cfg_city` VALUES (1324, '棋盘山', 1324, 1424, 220, 20, '221,220,217,213', 15, '214,0;213,0;20,3,0;217,0;220,0;20,2,0;20,2,0;20,2,0;20,2,0;20,1,5;20,1,5;20,1,5;20,1,5', '6,1,12,12,16,22,16,22,31');
INSERT INTO `cfg_city` VALUES (1330, '福临轩', 1330, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1430, '游商馆', 1430, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1517, '村庄', 1517, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1518, '福地', 1518, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1519, '云间', 1519, 1520, 230, 40, '421,415,418,410,405', 25, '209,0;40,3,0;40,3,0;40,3,0;20,3,0;20,3,0;20,3,0;40,2,0;40,2,0;418,0;415,0;410,0;421,5;20,1,5;405,0', '1,1,6,12,12,16,22,16,22,32');
INSERT INTO `cfg_city` VALUES (1521, '湖泊', 1521, NULL, 130, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1522, '黑市', 1522, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1523, '游商馆', 1523, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1524, '福地', 1524, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1525, '客栈', 1525, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1526, '仙人洞', 1526, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1527, '野地', 1527, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1528, '有穷', 1528, 1529, 220, 40, '421,420,416,414', 14, '410,0;414,0;416,0;40,2,0;40,2,0;20,2,1;20,2,1;20,2,1;420,5;421,5;20,1,5;20,1,5', '6,1,16,16,22,22,12,16,31');
INSERT INTO `cfg_city` VALUES (1530, '村庄', 1530, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1617, '游商馆', 1617, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1624, '湖泊', 1624, NULL, 130, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1630, '野地', 1630, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1715, '火山', 1715, NULL, 140, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1716, '福地', 1716, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1717, '野地', 1717, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1724, '界碑', 1724, NULL, 40, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1730, '泥沼', 1730, NULL, 150, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1731, '福地', 1731, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1732, '界碑', 1732, NULL, 40, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1815, '界碑', 1815, NULL, 40, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1824, '福地', 1824, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1832, '福地', 1832, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1901, '野地', 1901, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1902, '苗疆', 1902, 1903, 230, 40, '424,420,416,411,404', 25, '404,0;40,3,0;40,3,0;40,3,0;40,3,0;40,3,0;40,3,0;40,2,0;40,2,0;40,2,0;40,2,0;420,5;416,5;40,1,5;411,0', '3,3,5,24,24,24,24,17,17,36');
INSERT INTO `cfg_city` VALUES (1904, '村庄', 1904, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1905, '火山', 1905, NULL, 140, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1906, '扬州', 1906, 1907, 240, 20, '222,215,210,211,203', 46, '202,5;402,5;20,4,5;20,4,5;20,4,5;20,4,5;40,4,5;40,4,5;40,4,5;40,4,5;211,5;210,5;40,3,5;215,5;20,2,10;426,5;427,10;203,5', '5,5,14,14,17,17,24,24,35,36');
INSERT INTO `cfg_city` VALUES (1908, '黑市', 1908, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1909, '游商馆', 1909, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1910, '苍梧', 1910, 1911, 230, 30, '323,317,315,311,303', 25, '30,3,0;30,3,0;30,3,0;30,3,0;40,3,0;40,3,0;40,3,0;315,5;317,5;40,2,0;40,2,0;30,1,5;311,0;40,1,5;303,0', '3,3,5,14,14,14,14,24,24,36');
INSERT INTO `cfg_city` VALUES (1912, '野地', 1912, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1913, '福地', 1913, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1914, '福地', 1914, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1915, '村庄', 1915, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1917, '野地', 1917, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1918, '界牌关', 1918, 1919, 210, 20, '221,224,215,212', 1, '212,0;215,0;20,1,0;50,1,0;50,1,0;50,1,0', '7,7,15,15,15,15,15,20,39');
INSERT INTO `cfg_city` VALUES (1920, '黑市', 1920, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1921, '村庄', 1921, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1922, '渑池', 1922, 1923, 230, 20, '223,219,218,209,204', 25, '203,0;20,3,0;20,3,0;20,3,0;50,3,0;50,3,0;50,3,0;20,2,0;20,2,0;50,2,0;50,2,0;20,1,5;209,5;50,1,5;204,0', '7,7,10,15,15,15,20,20,25,40');
INSERT INTO `cfg_city` VALUES (1924, '客栈', 1924, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1925, '游商馆', 1925, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1926, '东莱', 1926, 1927, 220, 30, '322,320,316,313', 14, '313,0;50,3,0;30,2,0;30,2,0;30,2,0;50,2,1;50,2,1;50,2,1;30,1,5;30,1,5;50,1,5;50,1,5', '10,7,15,15,15,15,20,20,39');
INSERT INTO `cfg_city` VALUES (1928, '泥沼', 1928, NULL, 150, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1929, '福地', 1929, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1930, '野地', 1930, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1932, '野地', 1932, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1933, '村庄', 1933, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1934, '马邑', 1934, 1935, 210, 10, '122,124,117,113', 1, '10,2,0;10,2,0;30,2,0;30,2,0;10,1,0;10,1,0;10,1,0;30,1,0;30,1,0;30,1,0', '4,8,13,13,13,13,18,21,37');
INSERT INTO `cfg_city` VALUES (1936, '福地', 1936, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1937, '北海', 1937, 1938, 230, 50, '521,519,516,513,506', 25, '511,0;50,3,0;50,3,0;50,3,0;30,3,0;30,3,0;30,3,0;50,2,0;50,2,0;30,2,0;30,2,0;513,0;50,1,5;30,1,5;506,0', '4,4,4,13,18,18,18,13,18,38');
INSERT INTO `cfg_city` VALUES (1939, '福临轩', 1939, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1940, '湖泊', 1940, NULL, 130, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1941, '游商馆', 1941, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1942, '青州', 1942, 1943, 240, 30, '323,316,314,313,307', 46, '301,5;302,5;30,4,5;30,4,5;30,4,5;30,4,5;30,4,5;108,5;30,3,8;30,3,8;30,3,8;30,2,10;326,5;328,5;331,5;313,10;314,10;307,5', '8,8,13,13,13,13,13,18,37,38');
INSERT INTO `cfg_city` VALUES (1944, '野地', 1944, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1945, '黑市', 1945, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (1946, '福地', 1946, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2001, '客栈', 2001, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2008, '火山', 2008, NULL, 140, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2014, '沙洲', 2014, 2114, 210, 40, '424,423,420', 1, '420,0;421,0;422,0;423,0;424,0', '3,5,14,14,14,14,24,24,35');
INSERT INTO `cfg_city` VALUES (2017, '泥沼', 2017, NULL, 150, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2030, '佳梦关', 2030, 2130, 240, 40, '422,417,413,414,308', 46, '308,10;208,10;506,10;407,10;419,10;40,4,10;40,4,10;40,4,10;50,4,10;50,4,10;50,4,10;40,3,10;402,5;426,10;50,3,10;527,5;414,10;413,10', '10,10,15,15,15,15,20,25,39,40');
INSERT INTO `cfg_city` VALUES (2033, '客栈', 2033, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2039, '幽州', 2039, 2139, 220, 20, '223,220,217,211', 14, '211,0;30,3,0;20,2,0;20,2,0;20,2,0;30,2,1;30,2,1;30,2,1;20,1,5;20,1,5;30,1,5;30,1,5', '8,8,13,13,13,13,21,21,37');
INSERT INTO `cfg_city` VALUES (2046, '涂山', 2046, 2146, 210, 50, '524,522,517,509', 1, '521,0;522,0;524,0;523,0', '4,4,21,21,21,21,13,13,37');
INSERT INTO `cfg_city` VALUES (2101, '福地', 2101, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2108, '野地', 2108, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2117, '野地', 2117, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2133, '游商馆', 2133, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2201, '夷越', 2201, 2301, 210, 10, '121,122,120,112', 1, '10,2,0;10,2,0;40,2,0;40,2,0;10,1,0;10,1,0;10,1,0;40,1,0;40,1,0;40,1,0', '3,5,17,17,24,24,14,17,35');
INSERT INTO `cfg_city` VALUES (2208, '福地', 2208, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2214, '客栈', 2214, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2217, '豫州', 2217, 2317, 240, 30, '323,317,311,310,305', 46, '501,5;502,5;301,5;30,4,5;30,4,5;30,4,5;30,4,5;50,4,5;50,4,5;50,4,5;50,4,5;50,3,5;50,3,5;331,10;30,3,5;311,5;310,5;305,5', '10,10,15,15,20,20,20,25,39,40');
INSERT INTO `cfg_city` VALUES (2230, '客栈', 2230, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2233, '野地', 2233, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2239, '村庄', 2239, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2246, '湖泊', 2246, NULL, 130, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2308, '河阴', 2308, 2408, 220, 20, '222,219,216,214', 14, '214,0;40,3,0;20,2,0;213,0;20,2,0;40,2,1;40,2,1;40,2,1;20,1,5;20,1,5;40,1,5;40,1,5', '5,5,17,17,17,24,14,24,35');
INSERT INTO `cfg_city` VALUES (2314, '福临轩', 2314, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2330, '游商馆', 2330, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2333, '湖泊', 2333, NULL, 130, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2339, '野地', 2339, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2346, '不死山', 2346, 2446, 220, 30, '324,318,319,314', 14, '309,0;310,0;30,3,0;30,2,0;30,2,0;30,2,0;30,2,0;30,2,0;30,2,0;30,1,5;314,0;30,1,5;30,1,5', '8,8,18,18,18,21,21,21,37');
INSERT INTO `cfg_city` VALUES (2401, '仙人洞', 2401, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2414, '流波山', 2414, 2514, 220, 50, '523,517,516,510', 14, '50,3,0;40,3,0;510,0;50,2,0;50,2,0;40,2,1;40,2,1;40,2,1;50,1,5;50,1,5;40,1,5;40,1,5', '5,5,14,14,24,24,14,17,35');
INSERT INTO `cfg_city` VALUES (2417, '仙人洞', 2417, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2430, '汜水', 2430, 2530, 210, 30, '322,323,319,313', 1, '30,2,0;30,2,0;50,2,0;50,2,0;30,1,0;30,1,0;30,1,0;50,1,0;50,1,0;50,1,0', '7,7,15,15,15,20,15,15,39');
INSERT INTO `cfg_city` VALUES (2433, '并州', 2433, 2533, 220, 10, '122,116,119,113', 15, '113,0;30,3,0;10,2,0;10,2,0;10,2,0;30,2,1;30,2,1;30,2,1;10,1,5;10,1,5;30,1,5;30,1,5', '8,4,13,13,13,13,21,21,37');
INSERT INTO `cfg_city` VALUES (2439, '矿山', 2439, NULL, 110, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2501, '凤山', 2501, 2601, 240, 40, '422,418,411,412,404', 46, '401,5;402,5;40,4,5;40,4,5;40,4,5;40,4,5;40,4,5;40,4,5;40,3,8;40,3,8;40,3,8;418,10;40,2,10;40,2,10;40,2,10;411,10;412,10;404,5', '5,5,17,17,17,17,24,24,35,36');
INSERT INTO `cfg_city` VALUES (2508, '野地', 2508, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2517, '卫畿', 2517, 2617, 210, 10, '124,123,116,114', 1, '114,0;10,2,0;10,2,0;50,2,0;10,1,1;10,1,0;10,1,0;50,1,1;50,1,0;50,1,0', '7,10,15,15,15,15,20,20,39');
INSERT INTO `cfg_city` VALUES (2539, '曹州', 2539, 2639, 250, 30, '310,309,312,308,306,302', 64, '325,10;301,10;302,10;303,10;304,10;305,10;306,10;307,10;308,10;326,10;108,10;112,10;309,10;310,10;327,10;202,10;207,10;508,10;328,10;405,10', '8,8,21,21,21,21,21,37,37,38');
INSERT INTO `cfg_city` VALUES (2546, '客栈', 2546, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2608, '南都', 2608, 2708, 250, 40, '414,409,410,405,407,401', 63, '425,10;401,10;402,10;403,10;404,10;405,10;406,10;407,10;408,10;527,10;204,10;209,10;412,10;413,10;414,10;409,10;426,10;306,10;410,10;208,10', '5,5,14,14,24,24,24,35,35,36');
INSERT INTO `cfg_city` VALUES (2609, '游商馆', 2609, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2610, '河曲', 2610, 2611, 210, 20, '224,222,217,214', 1, '20,2,0;20,2,0;40,2,0;40,2,0;20,1,0;20,1,0;20,1,0;40,1,0;40,1,0;40,1,0', '3,3,17,17,17,17,14,14,35');
INSERT INTO `cfg_city` VALUES (2612, '火山', 2612, NULL, 140, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2613, '野地', 2613, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2614, '福地', 2614, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2615, '火山', 2615, NULL, 140, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2616, '界碑', 2616, NULL, 40, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2630, '村庄', 2630, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2631, '福地', 2631, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2632, '界碑', 2632, NULL, 40, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2633, '福地', 2633, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2634, '野地', 2634, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2635, '矿山', 2635, NULL, 110, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2636, '河上', 2636, 2637, 210, 30, '324,322,318', 1, '50,2,1;30,2,0;50,1,0;30,1,0;30,1,0;509,0', '4,4,18,18,21,21,13,13,37');
INSERT INTO `cfg_city` VALUES (2638, '野地', 2638, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2646, '冥海', 2646, 2746, 230, 30, '324,318,319,314,307', 25, '306,0;30,3,0;30,3,0;314,0;30,3,0;30,3,0;30,3,0;30,2,0;30,2,0;30,2,0;30,2,0;30,1,5;30,1,5;30,1,5;307,0', '4,4,8,18,18,18,18,13,13,38');
INSERT INTO `cfg_city` VALUES (2701, '太一府', 2701, 2801, 180, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2714, '荆州', 2714, 2814, 240, 10, '123,115,112,109,104', 46, '401,5;102,5;10,4,5;10,4,5;10,4,5;10,4,5;40,4,5;40,4,5;40,4,5;40,4,5;109,5;10,3,5;112,5;40,3,5;115,10;104,5;40,2,10;40,2,10', '5,5,14,14,17,17,24,24,35,36');
INSERT INTO `cfg_city` VALUES (2717, '野地', 2717, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2718, '福地', 2718, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2719, '泥沼', 2719, NULL, 150, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2720, '客栈', 2720, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2721, '三山关', 2721, 2722, 230, 50, '522,519,518,512,503', 25, '509,0;305,0;50,3,0;50,3,0;50,3,0;50,3,0;50,3,0;50,2,0;50,2,0;50,2,0;50,2,0;50,1,5;50,1,5;512,5;503,0', '7,7,7,15,15,15,20,20,25,40');
INSERT INTO `cfg_city` VALUES (2723, '鹿台', 2723, 2724, 200, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2725, '朝歌', 2725, 2726, 250, 50, '514,509,513,506,507,502', 68, '525,10;301,10;501,10;502,10;503,10;504,10;505,10;506,10;507,10;508,10;527,10;403,10;408,10;513,10;205,10;405,10;209,10;402,10;302,10;401,10', '10,10,15,15,15,20,25,39,39,40');
INSERT INTO `cfg_city` VALUES (2727, '福临轩', 2727, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2728, '泥沼', 2728, NULL, 150, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2729, '野地', 2729, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2730, '福地', 2730, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2733, '营州', 2733, 2833, 240, 10, '124,120,114,110,107', 46, '101,5;302,5;10,4,5;10,4,5;10,4,5;10,4,5;30,4,5;30,4,5;30,4,5;30,4,5;10,3,5;10,3,5;30,3,5;30,3,5;110,10;114,10;331,5;107,5', '8,8,13,13,13,18,18,18,37,38');
INSERT INTO `cfg_city` VALUES (2739, '野地', 2739, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2808, '泥沼', 2808, NULL, 150, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2817, '有虞', 2817, 2917, 220, 40, '422,417,419,413', 14, '413,0;50,3,0;40,2,0;40,2,0;40,2,0;50,2,1;50,2,1;50,2,1;40,1,5;40,1,5;50,1,5;50,1,5', '10,7,15,15,15,15,20,20,39');
INSERT INTO `cfg_city` VALUES (2824, '仙人洞', 2824, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2830, '轩辕台', 2830, 2930, 220, 50, '524,520,515,511', 15, '511,0;50,3,0;50,3,0;50,2,0;50,2,0;50,2,0;50,2,0;50,2,0;50,2,0;50,1,5;50,1,5;50,1,5;50,1,5', '10,10,15,15,15,15,20,20,39');
INSERT INTO `cfg_city` VALUES (2839, '仙人洞', 2839, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2846, '迷仙洞', 2846, 2946, 190, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2901, '巫沼', 2901, 3001, 210, 50, '523,521,519,510', 1, '511,0;50,2,0;510,0;40,2,0;50,1,1;50,1,0;40,1,1;40,1,0;50,1,0;40,1,0', '3,3,24,24,24,24,14,14,35');
INSERT INTO `cfg_city` VALUES (2908, '水峒', 2908, 3008, 210, 30, '321,324,316,310', 1, '30,2,1;40,2,0;310,0;40,1,0;40,1,0;40,1,0', '3,3,14,14,17,17,14,17,35');
INSERT INTO `cfg_city` VALUES (2914, '野地', 2914, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2924, '鸣条', 2924, 3024, 210, 40, '421,424,418,414', 1, '40,2,0;40,2,0;50,2,0;50,2,0;40,1,0;40,1,0;40,1,0;50,1,0;50,1,0;414,0', '7,10,20,20,20,20,15,15,39');
INSERT INTO `cfg_city` VALUES (2933, '福地', 2933, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (2939, '福地', 2939, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3014, '矿山', 3014, NULL, 110, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3017, '村庄', 3017, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3030, '客栈', 3030, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3033, '卫服', 3033, 3133, 210, 20, '223,221,216,211', 1, '211,0;20,2,0;20,2,0;30,2,0;20,1,1;20,1,0;20,1,0;30,1,1;30,1,0;30,1,0', '4,4,13,13,13,13,18,21,37');
INSERT INTO `cfg_city` VALUES (3039, '崇城', 3039, 3139, 230, 30, '322,320,316,313,305', 25, '313,0;108,0;112,0;30,3,0;30,3,0;30,3,0;30,3,0;30,2,0;30,2,0;30,2,0;30,2,0;30,1,5;30,1,5;30,1,5;305,0', '4,4,4,13,13,13,18,18,18,38');
INSERT INTO `cfg_city` VALUES (3046, '仙人洞', 3046, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3101, '森林', 3101, NULL, 120, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3108, '野地', 3108, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3114, '客栈', 3114, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3117, '游商馆', 3117, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3124, '游商馆', 3124, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3130, '游魂关', 3130, 3230, 230, 10, '124,120,115,114,104', 25, '125,0;112,0;10,3,0;10,3,0;50,3,0;50,3,0;50,3,0;10,2,0;10,2,0;50,2,0;50,2,0;10,1,5;10,1,5;114,5;104,0', '7,7,10,15,15,15,20,20,25,40');
INSERT INTO `cfg_city` VALUES (3146, '村庄', 3146, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3201, '蛮阈', 3201, 3301, 230, 50, '523,517,516,510,505', 25, '510,0;50,3,0;50,3,0;50,3,0;40,3,0;40,3,0;40,3,0;516,5;50,2,0;40,2,0;40,2,0;517,5;50,1,5;40,1,5;505,0', '3,3,3,17,17,17,24,24,24,36');
INSERT INTO `cfg_city` VALUES (3208, '福临轩', 3208, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3214, '壑市', 3214, 3314, 220, 40, '424,420,416,411', 15, '413,0;414,0;411,0;40,2,0;40,2,0;40,2,0;40,2,0;40,2,0;40,2,0;40,1,5;40,1,5;40,1,5;40,1,5', '5,3,14,14,14,17,17,17,35');
INSERT INTO `cfg_city` VALUES (3217, '矿山', 3217, NULL, 110, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3224, '雍州', 3224, 3324, 240, 50, '523,518,511,512,508', 46, '501,5;502,5;50,4,5;50,4,5;50,4,5;50,4,5;50,4,5;50,4,5;50,3,8;527,5;50,3,8;50,2,10;511,10;50,2,10;50,2,10;512,10;508,10;526,5', '10,10,15,15,20,20,25,25,39,40');
INSERT INTO `cfg_city` VALUES (3233, '森林', 3233, NULL, 120, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3239, '野地', 3239, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3246, '冀州', 3246, 3346, 240, 50, '521,516,509,513,505', 46, '411,10;413,10;414,10;301,6;501,5;50,4,5;50,4,5;50,4,5;50,4,5;30,4,5;30,4,5;30,4,5;30,4,5;509,5;513,5;50,3,5;331,5;505,5', '8,8,21,21,21,21,21,13,37,38');
INSERT INTO `cfg_city` VALUES (3308, '福地', 3308, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3317, '福地', 3317, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3330, '福临轩', 3330, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3333, '客栈', 3333, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3339, '客栈', 3339, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3401, '村庄', 3401, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3402, '游商馆', 3402, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3403, '森林', 3403, NULL, 120, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3404, '客栈', 3404, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3405, '野地', 3405, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3406, '常羊', 3406, 3407, 220, 10, '123,118,117,110', 14, '10,3,0;40,3,0;110,0;10,2,0;10,2,0;40,2,1;40,2,1;40,2,1;10,1,5;10,1,5;40,1,5;40,1,5', '5,3,17,17,17,17,14,24,35');
INSERT INTO `cfg_city` VALUES (3408, '泥沼', 3408, NULL, 150, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3409, '仙人洞', 3409, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3410, '火龙岛', 3410, 3411, 230, 40, '423,415,418,412,408', 25, '412,0;209,0;204,0;40,3,0;40,3,0;40,3,0;40,3,0;40,2,0;40,2,0;40,2,0;418,5;415,5;40,1,5;40,1,5;408,0', '3,3,3,14,14,17,17,24,24,36');
INSERT INTO `cfg_city` VALUES (3412, '游商馆', 3412, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3413, '黑市', 3413, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3414, '野地', 3414, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3415, '村庄', 3415, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3417, '野地', 3417, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3418, '青龙关', 3418, 3419, 230, 50, '524,520,515,511,508', 25, '516,0;413,0;414,0;403,0;408,0;50,3,0;50,3,0;50,2,0;50,2,0;50,2,0;50,2,0;50,1,5;50,1,5;511,5;508,0', '7,7,7,15,15,15,20,20,25,40');
INSERT INTO `cfg_city` VALUES (3420, '湖泊', 3420, NULL, 130, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3421, '村庄', 3421, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3422, '火山', 3422, NULL, 140, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3423, '福地', 3423, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3424, '野地', 3424, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3425, '牧野', 3425, 3426, 220, 10, '124,120,115,114', 14, '114,0;50,3,0;10,2,0;10,2,0;10,2,0;50,2,1;50,2,1;50,2,1;10,1,5;10,1,5;50,1,5;50,1,5', '10,7,15,20,20,20,15,20,39');
INSERT INTO `cfg_city` VALUES (3427, '穿云关', 3427, 3428, 210, 50, '524,522,520', 1, '520,0;521,0;522,0;523,0;524,0', '7,10,15,15,15,15,15,20,39');
INSERT INTO `cfg_city` VALUES (3429, '野地', 3429, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3430, '火山', 3430, NULL, 140, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3433, '村庄', 3433, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3434, '涿鹿', 3434, 3435, 230, 20, '222,215,218,210,208', 25, '20,3,0;20,3,0;20,3,0;20,3,0;30,3,0;30,3,0;30,3,0;20,2,0;20,2,0;30,2,0;30,2,0;210,5;218,5;215,5;208,0', '4,4,8,13,13,18,21,18,21,38');
INSERT INTO `cfg_city` VALUES (3436, '游商馆', 3436, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3437, '桓山', 3437, 3438, 220, 40, '423,415,418,412', 14, '40,3,0;30,3,0;40,2,0;40,2,0;40,2,0;30,2,1;30,2,1;30,2,1;40,1,5;40,1,5;30,1,5;412,0', '8,8,13,21,21,21,13,13,37');
INSERT INTO `cfg_city` VALUES (3439, '福地', 3439, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3440, '黑市', 3440, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3441, '游商馆', 3441, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3442, '朔方', 3442, 3443, 210, 40, '422,421,415,413', 1, '40,2,0;40,2,0;30,2,0;30,2,0;40,1,0;40,1,0;40,1,0;30,1,0;30,1,0;30,1,0', '4,8,18,18,18,18,13,13,37');
INSERT INTO `cfg_city` VALUES (3444, '火山', 3444, NULL, 140, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3445, '福临轩', 3445, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3446, '野地', 3446, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3515, '界碑', 3515, NULL, 40, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3524, '黑市', 3524, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3533, '福地', 3533, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3615, '福地', 3615, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3616, '野地', 3616, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3617, '矿山', 3617, NULL, 110, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3624, '界碑', 3624, NULL, 40, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3630, '福地', 3630, NULL, 30, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3631, '矿山', 3631, NULL, 110, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3632, '野地', 3632, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3633, '界碑', 3633, NULL, 40, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3717, '村庄', 3717, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3718, '矿山', 3718, NULL, 110, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3719, '悬圃', 3719, 3720, 210, 10, '121,122,118', 1, '118,0;121,0;122,0;123,0;124,0', '2,9,11,23,23,23,11,11,33');
INSERT INTO `cfg_city` VALUES (3721, '游商馆', 3721, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3722, '陈塘关', 3722, 3723, 240, 20, '223,218,212,209,105', 46, '105,10;201,5;109,10;206,5;10,4,5;10,4,5;10,4,5;10,4,5;20,4,5;20,4,5;20,4,5;20,4,5;125,8;10,3,8;10,3,8;209,8;20,3,8;227,5', '9,9,23,23,23,23,23,19,33,34');
INSERT INTO `cfg_city` VALUES (3724, '野地', 3724, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3725, '绝龙岭', 3725, 3726, 230, 40, '422,417,419,413,403', 25, '413,0;40,3,0;40,3,0;40,3,0;10,3,0;10,3,0;10,3,0;40,2,0;40,2,0;10,2,0;10,2,0;40,1,5;40,1,5;10,1,5;403,0', '2,2,9,11,11,19,23,19,23,34');
INSERT INTO `cfg_city` VALUES (3727, '仙人洞', 3727, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3728, '三津里', 3728, 3729, 220, 30, '321,318,319,310', 14, '30,3,0;10,3,0;310,0;30,2,0;30,2,0;10,2,1;10,2,1;10,2,1;30,1,5;30,1,5;10,1,5;10,1,5', '9,9,11,11,11,11,23,23,33');
INSERT INTO `cfg_city` VALUES (3730, '福临轩', 3730, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3817, '即韦', 3817, 3917, 220, 20, '222,215,218,210', 14, '210,0;10,3,0;20,2,0;20,2,0;20,2,0;10,2,1;10,2,1;10,2,1;20,1,5;20,1,5;10,1,5;10,1,5', '9,2,11,11,19,19,23,23,33');
INSERT INTO `cfg_city` VALUES (3824, '矿山', 3824, NULL, 110, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3830, '村庄', 3830, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3924, '野地', 3924, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (3930, '湖泊', 3930, NULL, 130, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4017, '客栈', 4017, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4024, '客栈', 4024, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4030, '羌西', 4030, 4130, 210, 50, '521,524,518,514', 1, '514,0;50,2,0;10,2,0;10,2,0;50,1,0;50,1,0;50,1,0;10,1,0;10,1,0;10,1,0', '2,9,23,23,23,23,11,11,33');
INSERT INTO `cfg_city` VALUES (4117, '首阳', 4117, 4217, 230, 10, '122,116,119,113,107', 25, '111,0;503,0;510,0;10,3,0;10,3,0;10,3,0;10,3,0;10,2,0;10,2,0;10,2,0;10,2,0;113,5;10,1,5;10,1,5;107,0', '2,2,2,19,19,19,19,23,23,34');
INSERT INTO `cfg_city` VALUES (4124, '游商馆', 4124, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4224, '福地', 4224, NULL, 36, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4230, '黑市', 4230, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4317, '游商馆', 4317, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4318, '泾谷', 4318, 4319, 210, 40, '423,422,416,412', 1, '40,2,1;10,2,0;412,0;10,1,0;10,1,0;10,1,0', '2,2,19,19,19,19,11,11,33');
INSERT INTO `cfg_city` VALUES (4320, '黑市', 4320, NULL, 100, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4321, '丰镐', 4321, 4322, 230, 10, '121,118,117,111,108', 25, '108,0;10,3,0;10,3,0;10,3,0;10,3,0;10,3,0;30,3,0;10,2,0;10,2,0;10,2,0;10,2,0;10,1,5;10,1,5;10,1,5;111,5', '2,2,9,11,19,19,23,19,23,34');
INSERT INTO `cfg_city` VALUES (4323, '庙宇', 4323, 4324, 170, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4325, '西歧', 4325, 4326, 250, 10, '110,112,109,105,103,101', 65, '126,10;101,10;102,10;103,10;104,10;105,10;106,10;107,10;108,10;127,10;503,10;510,10;509,10;305,10;507,10;306,10;201,10;109,10;206,10;128,10', '9,9,11,11,11,11,11,33,33,34');
INSERT INTO `cfg_city` VALUES (4327, '野地', 4327, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4328, '太华', 4328, 4329, 220, 10, '121,118,117,111', 15, '111,0;10,3,0;10,3,0;10,2,0;10,2,0;10,2,0;10,2,0;10,2,0;10,2,0;10,1,5;10,1,5;10,1,5;10,1,5', '9,9,11,11,11,11,23,23,33');
INSERT INTO `cfg_city` VALUES (4330, '游商馆', 4330, NULL, 70, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4417, '森林', 4417, NULL, 120, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4430, '福地', 4430, NULL, 33, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4517, '野地', 4517, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4530, '湖泊', 4530, NULL, 130, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4617, '蜀丘', 4617, 4717, 240, 50, '523,517,510,514,503', 46, '502,5;102,5;50,4,5;50,4,5;50,4,5;50,4,5;10,4,5;10,4,5;10,4,5;10,4,5;50,3,5;50,3,5;10,3,5;10,3,5;50,2,10;50,2,10;127,5;128,5', '9,9,19,19,19,19,19,23,33,34');
INSERT INTO `cfg_city` VALUES (4630, '河西', 4630, 4730, 210, 30, '324,322,317,309', 1, '314,0;30,2,0;30,2,0;10,2,0;30,1,1;30,1,0;30,1,0;10,1,1;10,1,0;10,1,0', '2,2,11,11,11,23,11,23,33');
INSERT INTO `cfg_city` VALUES (4817, '客栈', 4817, NULL, 60, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4830, '村庄', 4830, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4917, '泥沼', 4917, NULL, 150, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (4930, '仙人洞', 4930, NULL, 50, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (5017, '野地', 5017, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (5018, '村庄', 5018, NULL, 10, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (5019, '西亳', 5019, 5020, 220, 50, '521,520,515,514', 14, '50,3,0;10,3,0;514,0;50,2,0;50,2,0;10,2,1;10,2,1;10,2,1;50,1,5;50,1,5;10,1,5;10,1,5', '9,2,19,19,19,19,11,11,33');
INSERT INTO `cfg_city` VALUES (5021, '泥沼', 5021, NULL, 150, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (5022, '临羌', 5022, 5023, 210, 20, '222,223,219,210', 1, '20,2,0;20,2,0;10,2,0;10,2,0;20,1,0;20,1,0;20,1,0;10,1,0;10,1,0;10,1,0', '2,9,23,23,23,23,11,11,33');
INSERT INTO `cfg_city` VALUES (5024, '福临轩', 5024, NULL, 80, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (5025, '凉州', 5025, 5026, 240, 10, '124,119,111,113,108', 46, '101,5;102,5;10,4,5;10,4,5;10,4,5;10,4,5;10,4,5;10,4,5;10,3,8;10,3,8;10,3,8;10,2,10;10,2,10;10,2,10;10,2,10;111,10;113,10;108,5', '9,9,11,11,23,23,23,23,33,34');
INSERT INTO `cfg_city` VALUES (5027, '野地', 5027, NULL, 20, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `cfg_city` VALUES (5028, '昆仑', 5028, 5029, 230, 30, '321,320,315,312,408', 25, '312,3;30,3,0;30,3,0;30,3,0;10,3,0;10,3,0;10,3,0;30,2,0;30,2,0;10,2,0;10,2,0;30,1,5;30,1,5;10,1,5;408,0', '2,2,2,11,11,11,23,19,23,34');
INSERT INTO `cfg_city` VALUES (5030, '客栈', 5030, NULL, 60, NULL, NULL, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for cfg_city_guarder_base
-- ----------------------------
DROP TABLE IF EXISTS `cfg_city_guarder_base`;
CREATE TABLE `cfg_city_guarder_base` (
  `id` int(11) NOT NULL,
  `city_id` int(11) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `level` int(11) NOT NULL,
  `defense` int(11) NOT NULL,
  `soliders` varchar(300) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='守军基础数据\r\n\r\n\r\n守军部队	soliders:   \r\n姜桓楚10;句芒1';

-- ----------------------------
-- Records of cfg_city_guarder_base
-- ----------------------------
BEGIN;
INSERT INTO `cfg_city_guarder_base` VALUES (118, 118, NULL, 25, 3000, '204,0;211,0;212,0;215,0;216,0;217,0;218,0;210,0;209,0;213,0;214,0;220,0;20,1,5;20,1,5;20,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (120, 120, NULL, 1, 1000, '30,2,0;30,2,0;20,2,0;20,2,0;30,1,0;30,1,0;30,1,0;20,1,0;20,1,0;20,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (126, 126, NULL, 46, 4000, '302,5;201,5;30,4,5;30,4,5;30,4,5;30,4,5;20,4,5;20,4,5;20,4,5;20,4,5;30,3,5;30,3,5;20,3,5;20,3,5;30,2,10;30,2,10;20,2,10;20,2,10');
INSERT INTO `cfg_city_guarder_base` VALUES (230, 230, NULL, 25, 3000, '10,3,0;10,3,0;10,3,0;10,3,0;213,0;214,0;20,3,0;10,2,0;10,2,0;20,2,0;20,2,0;10,1,5;10,1,5;20,1,5;20,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (417, 417, NULL, 46, 4000, '403,5;406,5;312,5;219,5;415,5;311,5;418,5;410,5;514,5;416,5;401,5;40,3,5;20,3,5;20,3,5;20,2,10;20,2,10;20,1,10;20,1,10');
INSERT INTO `cfg_city_guarder_base` VALUES (430, 430, NULL, 1, 1000, '50,2,0;50,2,0;20,2,0;20,2,0;50,1,0;50,1,0;50,1,0;20,1,0;20,1,0;20,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (617, 617, NULL, 1, 1000, '220,0;221,0;222,0;223,0;224,0');
INSERT INTO `cfg_city_guarder_base` VALUES (730, 730, NULL, 14, 2000, '514,0;20,3,0;50,2,0;50,2,0;50,2,0;20,2,1;20,2,1;20,2,1;50,1,5;50,1,5;20,1,5;20,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (919, 919, NULL, 1, 1000, '10,2,1;20,2,0;10,1,0;20,1,0;20,1,0;20,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (922, 922, NULL, 14, 2000, '312,0;20,3,0;30,2,0;30,2,0;30,2,0;20,2,1;20,2,1;20,2,1;30,1,5;30,1,5;20,1,5;20,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (928, 928, NULL, 25, 3000, '210,0;512,0;409,0;314,0;30,3,0;40,3,0;20,3,0;40,2,0;40,2,0;20,2,0;20,2,0;40,1,5;40,1,5;20,1,5;20,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (1017, 1017, NULL, 46, 4000, '202,5;20,4,5;20,4,5;20,4,5;20,4,5;20,4,5;20,4,5;20,3,8;20,3,8;20,3,8;20,3,8;20,2,10;20,2,10;20,2,10;20,2,10;20,1,10;20,1,10;20,1,10');
INSERT INTO `cfg_city_guarder_base` VALUES (1024, 1024, NULL, 62, 5000, '226,10;202,10;201,10;203,10;204,10;205,10;206,10;207,10;208,10;220,10;307,10;313,10;211,10;215,10;209,10;106,10;109,10;502,10;111,10;107,10');
INSERT INTO `cfg_city_guarder_base` VALUES (1317, 1317, NULL, 1, 1000, '412,0;40,2,0;40,2,0;20,2,0;40,1,1;40,1,0;20,1,1;20,1,0;20,1,0;40,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (1324, 1324, NULL, 15, 2000, '213,0;214,0;20,3,0;20,2,0;20,2,0;20,2,0;20,2,0;20,2,0;20,2,0;20,1,5;20,1,5;20,1,5;20,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (1519, 1519, NULL, 25, 3000, '209,0;40,3,0;40,3,0;40,3,0;20,3,0;20,3,0;20,3,0;40,2,0;40,2,0;20,2,0;20,2,0;40,1,5;40,1,5;20,1,5;20,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (1528, 1528, NULL, 14, 2000, '410,0;20,3,0;40,2,0;40,2,0;40,2,0;20,2,1;20,2,1;20,2,1;40,1,5;40,1,5;20,1,5;20,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (1902, 1902, NULL, 25, 3000, '404,0;40,3,0;40,3,0;40,3,0;40,3,0;40,3,0;40,3,0;40,2,0;40,2,0;40,2,0;40,2,0;40,1,5;40,1,5;40,1,5;40,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (1906, 1906, NULL, 46, 4000, '202,5;402,5;20,4,5;20,4,5;20,4,5;20,4,5;40,4,5;40,4,5;40,4,5;40,4,5;20,3,5;20,3,5;40,3,5;40,3,5;20,2,10;40,2,10;40,2,10;40,2,10');
INSERT INTO `cfg_city_guarder_base` VALUES (1910, 1910, NULL, 25, 3000, '30,3,0;30,3,0;30,3,0;30,3,0;40,3,0;40,3,0;40,3,0;30,2,0;30,2,0;40,2,0;40,2,0;30,1,5;30,1,5;40,1,5;40,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (1918, 1918, NULL, 1, 1000, '20,2,1;50,2,0;20,1,0;50,1,0;50,1,0;50,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (1922, 1922, NULL, 25, 3000, '203,0;20,3,0;20,3,0;20,3,0;50,3,0;50,3,0;50,3,0;20,2,0;20,2,0;50,2,0;50,2,0;20,1,5;20,1,5;50,1,5;50,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (1926, 1926, NULL, 14, 2000, '30,3,0;50,3,0;30,2,0;30,2,0;30,2,0;50,2,1;50,2,1;50,2,1;30,1,5;30,1,5;50,1,5;50,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (1934, 1934, NULL, 1, 1000, '10,2,0;10,2,0;30,2,0;30,2,0;10,1,0;10,1,0;10,1,0;30,1,0;30,1,0;30,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (1937, 1937, NULL, 25, 3000, '511,0;50,3,0;50,3,0;50,3,0;30,3,0;30,3,0;30,3,0;50,2,0;50,2,0;30,2,0;30,2,0;50,1,5;50,1,5;30,1,5;30,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (1942, 1942, NULL, 46, 4000, '301,5;302,5;30,4,5;30,4,5;30,4,5;30,4,5;30,4,5;30,4,5;30,3,8;30,3,8;30,3,8;30,2,10;30,2,10;30,2,10;30,2,10;30,1,10;30,1,10;30,1,10');
INSERT INTO `cfg_city_guarder_base` VALUES (2014, 2014, NULL, 1, 1000, '420,0;421,0;422,0;423,0;424,0');
INSERT INTO `cfg_city_guarder_base` VALUES (2030, 2030, NULL, 46, 4000, '308,10;208,10;506,10;407,10;419,10;40,4,10;40,4,10;40,4,10;50,4,10;50,4,10;50,4,10;40,3,10;40,3,10;40,3,10;50,3,10;50,3,10;50,3,10;50,3,10');
INSERT INTO `cfg_city_guarder_base` VALUES (2039, 2039, NULL, 14, 2000, '20,3,0;30,3,0;20,2,0;20,2,0;20,2,0;30,2,1;30,2,1;30,2,1;20,1,5;20,1,5;30,1,5;30,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (2046, 2046, NULL, 1, 1000, '521,0;522,0;524,0;523,0');
INSERT INTO `cfg_city_guarder_base` VALUES (2201, 2201, NULL, 1, 1000, '10,2,0;10,2,0;40,2,0;40,2,0;10,1,0;10,1,0;10,1,0;40,1,0;40,1,0;40,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (2217, 2217, NULL, 46, 4000, '501,5;502,5;301,5;30,4,5;30,4,5;30,4,5;30,4,5;50,4,5;50,4,5;50,4,5;50,4,5;50,3,5;50,3,5;50,3,5;30,3,5;30,3,5;30,3,5;50,2,10');
INSERT INTO `cfg_city_guarder_base` VALUES (2308, 2308, NULL, 14, 2000, '20,3,0;40,3,0;20,2,0;20,2,0;20,2,0;40,2,1;40,2,1;40,2,1;20,1,5;20,1,5;40,1,5;40,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (2346, 2346, NULL, 14, 2000, '309,0;310,0;30,3,0;30,2,0;30,2,0;30,2,0;30,2,0;30,2,0;30,2,0;30,1,5;30,1,5;30,1,5;30,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (2414, 2414, NULL, 14, 2000, '50,3,0;40,3,0;50,2,0;50,2,0;50,2,0;40,2,1;40,2,1;40,2,1;50,1,5;50,1,5;40,1,5;40,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (2430, 2430, NULL, 1, 1000, '30,2,0;30,2,0;50,2,0;50,2,0;30,1,0;30,1,0;30,1,0;50,1,0;50,1,0;50,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (2433, 2433, NULL, 15, 2000, '10,3,0;30,3,0;10,2,0;10,2,0;10,2,0;30,2,1;30,2,1;30,2,1;10,1,5;10,1,5;30,1,5;30,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (2501, 2501, NULL, 46, 4000, '401,5;402,5;40,4,5;40,4,5;40,4,5;40,4,5;40,4,5;40,4,5;40,3,8;40,3,8;40,3,8;40,2,10;40,2,10;40,2,10;40,2,10;40,1,10;40,1,10;40,1,10');
INSERT INTO `cfg_city_guarder_base` VALUES (2517, 2517, NULL, 1, 1000, '114,0;10,2,0;10,2,0;50,2,0;10,1,1;10,1,0;10,1,0;50,1,1;50,1,0;50,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (2539, 2539, NULL, 64, 5000, '325,10;301,10;302,10;303,10;304,10;305,10;306,10;307,10;308,10;318,10;108,10;125,10;309,10;310,10;313,10;202,10;207,10;508,10;112,10;405,10');
INSERT INTO `cfg_city_guarder_base` VALUES (2608, 2608, NULL, 63, 5000, '425,10;401,10;402,10;403,10;404,10;405,10;406,10;407,10;408,10;420,10;204,10;209,10;412,10;413,10;414,10;416,10;422,10;306,10;514,10;208,10');
INSERT INTO `cfg_city_guarder_base` VALUES (2610, 2610, NULL, 1, 1000, '20,2,0;20,2,0;40,2,0;40,2,0;20,1,0;20,1,0;20,1,0;40,1,0;40,1,0;40,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (2636, 2636, NULL, 1, 1000, '50,2,1;30,2,0;50,1,0;30,1,0;30,1,0;30,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (2646, 2646, NULL, 25, 3000, '306,0;30,3,0;30,3,0;30,3,0;30,3,0;30,3,0;30,3,0;30,2,0;30,2,0;30,2,0;30,2,0;30,1,5;30,1,5;30,1,5;30,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (2714, 2714, NULL, 46, 4000, '401,5;102,5;10,4,5;10,4,5;10,4,5;10,4,5;40,4,5;40,4,5;40,4,5;40,4,5;10,3,5;10,3,5;40,3,5;40,3,5;10,2,10;10,2,10;40,2,10;40,2,10');
INSERT INTO `cfg_city_guarder_base` VALUES (2721, 2721, NULL, 25, 3000, '509,0;305,0;50,3,0;50,3,0;50,3,0;50,3,0;50,3,0;50,2,0;50,2,0;50,2,0;50,2,0;50,1,5;50,1,5;50,1,5;50,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (2725, 2725, NULL, 68, 5000, '525,10;301,10;501,10;502,10;503,10;504,10;505,10;506,10;507,10;508,10;520,10;403,10;408,10;513,10;205,10;405,10;209,10;402,10;302,10;401,10');
INSERT INTO `cfg_city_guarder_base` VALUES (2733, 2733, NULL, 46, 4000, '101,5;302,5;10,4,5;10,4,5;10,4,5;10,4,5;30,4,5;30,4,5;30,4,5;30,4,5;10,3,5;10,3,5;30,3,5;30,3,5;10,2,10;10,2,10;30,2,10;30,2,10');
INSERT INTO `cfg_city_guarder_base` VALUES (2817, 2817, NULL, 14, 2000, '40,3,0;50,3,0;40,2,0;40,2,0;40,2,0;50,2,1;50,2,1;50,2,1;40,1,5;40,1,5;50,1,5;50,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (2830, 2830, NULL, 15, 2000, '50,3,0;50,3,0;50,3,0;50,2,0;50,2,0;50,2,0;50,2,0;50,2,0;50,2,0;50,1,5;50,1,5;50,1,5;50,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (2901, 2901, NULL, 1, 1000, '511,0;50,2,0;50,2,0;40,2,0;50,1,1;50,1,0;40,1,1;40,1,0;50,1,0;40,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (2908, 2908, NULL, 1, 1000, '30,2,1;40,2,0;30,1,0;40,1,0;40,1,0;40,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (2924, 2924, NULL, 1, 1000, '40,2,0;40,2,0;50,2,0;50,2,0;40,1,0;40,1,0;40,1,0;50,1,0;50,1,0;50,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (3033, 3033, NULL, 1, 1000, '211,0;20,2,0;20,2,0;30,2,0;20,1,1;20,1,0;20,1,0;30,1,1;30,1,0;30,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (3039, 3039, NULL, 25, 3000, '313,0;108,0;112,0;30,3,0;30,3,0;30,3,0;30,3,0;30,2,0;30,2,0;30,2,0;30,2,0;30,1,5;30,1,5;30,1,5;30,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3130, 3130, NULL, 25, 3000, '125,0;112,0;10,3,0;10,3,0;50,3,0;50,3,0;50,3,0;10,2,0;10,2,0;50,2,0;50,2,0;10,1,5;10,1,5;50,1,5;50,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3201, 3201, NULL, 25, 3000, '50,3,0;50,3,0;50,3,0;50,3,0;40,3,0;40,3,0;40,3,0;50,2,0;50,2,0;40,2,0;40,2,0;50,1,5;50,1,5;40,1,5;40,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3214, 3214, NULL, 15, 2000, '413,0;414,0;40,3,0;40,2,0;40,2,0;40,2,0;40,2,0;40,2,0;40,2,0;40,1,5;40,1,5;40,1,5;40,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3224, 3224, NULL, 46, 4000, '501,5;502,5;50,4,5;50,4,5;50,4,5;50,4,5;50,4,5;50,4,5;50,3,8;50,3,8;50,3,8;50,2,10;50,2,10;50,2,10;50,2,10;50,1,10;50,1,10;50,1,10');
INSERT INTO `cfg_city_guarder_base` VALUES (3246, 3246, NULL, 46, 4000, '411,10;413,10;414,10;301,6;501,5;50,4,5;50,4,5;50,4,5;50,4,5;30,4,5;30,4,5;30,4,5;30,4,5;50,3,5;50,3,5;50,3,5;30,3,5;30,3,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3406, 3406, NULL, 14, 2000, '10,3,0;40,3,0;10,2,0;10,2,0;10,2,0;40,2,1;40,2,1;40,2,1;10,1,5;10,1,5;40,1,5;40,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3410, 3410, NULL, 25, 3000, '412,0;209,0;204,0;40,3,0;40,3,0;40,3,0;40,3,0;40,2,0;40,2,0;40,2,0;40,2,0;40,1,5;40,1,5;40,1,5;40,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3418, 3418, NULL, 25, 3000, '516,0;413,0;414,0;403,0;408,0;50,3,0;50,3,0;50,2,0;50,2,0;50,2,0;50,2,0;50,1,5;50,1,5;50,1,5;50,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3425, 3425, NULL, 14, 2000, '10,3,0;50,3,0;10,2,0;10,2,0;10,2,0;50,2,1;50,2,1;50,2,1;10,1,5;10,1,5;50,1,5;50,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3427, 3427, NULL, 1, 1000, '520,0;521,0;522,0;523,0;524,0');
INSERT INTO `cfg_city_guarder_base` VALUES (3434, 3434, NULL, 25, 3000, '20,3,0;20,3,0;20,3,0;20,3,0;30,3,0;30,3,0;30,3,0;20,2,0;20,2,0;30,2,0;30,2,0;20,1,5;20,1,5;30,1,5;30,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3437, 3437, NULL, 14, 2000, '40,3,0;30,3,0;40,2,0;40,2,0;40,2,0;30,2,1;30,2,1;30,2,1;40,1,5;40,1,5;30,1,5;30,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3442, 3442, NULL, 1, 1000, '40,2,0;40,2,0;30,2,0;30,2,0;40,1,0;40,1,0;40,1,0;30,1,0;30,1,0;30,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (3719, 3719, NULL, 1, 1000, '118,0;121,0;122,0;123,0;124,0');
INSERT INTO `cfg_city_guarder_base` VALUES (3722, 3722, NULL, 46, 4000, '105,10;201,5;109,10;206,5;10,4,5;10,4,5;10,4,5;10,4,5;20,4,5;20,4,5;20,4,5;20,4,5;10,3,8;10,3,8;10,3,8;20,3,8;20,3,8;20,3,8');
INSERT INTO `cfg_city_guarder_base` VALUES (3725, 3725, NULL, 25, 3000, '40,3,0;40,3,0;40,3,0;40,3,0;10,3,0;10,3,0;10,3,0;40,2,0;40,2,0;10,2,0;10,2,0;40,1,5;40,1,5;10,1,5;10,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3728, 3728, NULL, 14, 2000, '30,3,0;10,3,0;30,2,0;30,2,0;30,2,0;10,2,1;10,2,1;10,2,1;30,1,5;30,1,5;10,1,5;10,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (3817, 3817, NULL, 14, 2000, '20,3,0;10,3,0;20,2,0;20,2,0;20,2,0;10,2,1;10,2,1;10,2,1;20,1,5;20,1,5;10,1,5;10,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (4030, 4030, NULL, 1, 1000, '50,2,0;50,2,0;10,2,0;10,2,0;50,1,0;50,1,0;50,1,0;10,1,0;10,1,0;10,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (4117, 4117, NULL, 25, 3000, '111,0;503,0;510,0;10,3,0;10,3,0;10,3,0;10,3,0;10,2,0;10,2,0;10,2,0;10,2,0;10,1,5;10,1,5;10,1,5;10,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (4318, 4318, NULL, 1, 1000, '40,2,1;10,2,0;40,1,0;10,1,0;10,1,0;10,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (4321, 4321, NULL, 25, 3000, '108,0;10,3,0;10,3,0;10,3,0;10,3,0;10,3,0;10,3,0;10,2,0;10,2,0;10,2,0;10,2,0;10,1,5;10,1,5;10,1,5;10,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (4325, 4325, NULL, 65, 5000, '126,10;101,10;102,10;103,10;104,10;105,10;106,10;107,10;108,10;118,10;503,10;510,10;509,10;305,10;507,10;306,10;201,10;109,10;206,10;114,10');
INSERT INTO `cfg_city_guarder_base` VALUES (4328, 4328, NULL, 15, 2000, '10,3,0;10,3,0;10,3,0;10,2,0;10,2,0;10,2,0;10,2,0;10,2,0;10,2,0;10,1,5;10,1,5;10,1,5;10,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (4617, 4617, NULL, 46, 4000, '502,5;102,5;50,4,5;50,4,5;50,4,5;50,4,5;10,4,5;10,4,5;10,4,5;10,4,5;50,3,5;50,3,5;10,3,5;10,3,5;50,2,10;50,2,10;10,2,10;10,2,10');
INSERT INTO `cfg_city_guarder_base` VALUES (4630, 4630, NULL, 1, 1000, '314,0;30,2,0;30,2,0;10,2,0;30,1,1;30,1,0;30,1,0;10,1,1;10,1,0;10,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (5019, 5019, NULL, 14, 2000, '50,3,0;10,3,0;50,2,0;50,2,0;50,2,0;10,2,1;10,2,1;10,2,1;50,1,5;50,1,5;10,1,5;10,1,5');
INSERT INTO `cfg_city_guarder_base` VALUES (5022, 5022, NULL, 1, 1000, '20,2,0;20,2,0;10,2,0;10,2,0;20,1,0;20,1,0;20,1,0;10,1,0;10,1,0;10,1,0');
INSERT INTO `cfg_city_guarder_base` VALUES (5025, 5025, NULL, 46, 4000, '101,5;102,5;10,4,5;10,4,5;10,4,5;10,4,5;10,4,5;10,4,5;10,3,8;10,3,8;10,3,8;10,2,10;10,2,10;10,2,10;10,2,10;10,1,10;10,1,10;10,1,10');
INSERT INTO `cfg_city_guarder_base` VALUES (5028, 5028, NULL, 25, 3000, '30,3,0;30,3,0;30,3,0;30,3,0;10,3,0;10,3,0;10,3,0;30,2,0;30,2,0;10,2,0;10,2,0;30,1,5;30,1,5;10,1,5;10,1,5');
COMMIT;

-- ----------------------------
-- Table structure for cfg_city_special
-- ----------------------------
DROP TABLE IF EXISTS `cfg_city_special`;
CREATE TABLE `cfg_city_special` (
  `id` int(11) NOT NULL DEFAULT '0',
  `outside_special` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `inside_special` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='城市交易出售的特产';

-- ----------------------------
-- Records of cfg_city_special
-- ----------------------------
BEGIN;
INSERT INTO `cfg_city_special` VALUES (118, '1,1', '1,22,22,22,22');
INSERT INTO `cfg_city_special` VALUES (120, '1', '6,22,22,22,22');
INSERT INTO `cfg_city_special` VALUES (126, '6,6', '16,16,16,16,16');
INSERT INTO `cfg_city_special` VALUES (230, '1,1', '1,12,12,12,12');
INSERT INTO `cfg_city_special` VALUES (417, '6,6', '12,12,12,22,22');
INSERT INTO `cfg_city_special` VALUES (430, '1', '1,16,16,16,16');
INSERT INTO `cfg_city_special` VALUES (617, '1', '1,12,12,12,12');
INSERT INTO `cfg_city_special` VALUES (730, '6', '6,12,22,22,22');
INSERT INTO `cfg_city_special` VALUES (919, '1', '6,12,12,16,22');
INSERT INTO `cfg_city_special` VALUES (922, '6', '1,12,12,12,16');
INSERT INTO `cfg_city_special` VALUES (928, '1,1', '6,16,16,16,16');
INSERT INTO `cfg_city_special` VALUES (1017, '6,6', '16,16,16,16,16');
INSERT INTO `cfg_city_special` VALUES (1024, '6,6', '16,16,22,22,22');
INSERT INTO `cfg_city_special` VALUES (1317, '1', '6,12,12,16,22');
INSERT INTO `cfg_city_special` VALUES (1324, '6', '1,12,12,16,22');
INSERT INTO `cfg_city_special` VALUES (1519, '1,1', '6,12,12,16,22');
INSERT INTO `cfg_city_special` VALUES (1528, '6', '1,16,16,22,22');
INSERT INTO `cfg_city_special` VALUES (1902, '3,3', '5,24,24,24,24');
INSERT INTO `cfg_city_special` VALUES (1906, '5,5', '14,14,17,17,24');
INSERT INTO `cfg_city_special` VALUES (1910, '3,3', '5,14,14,14,14');
INSERT INTO `cfg_city_special` VALUES (1918, '7', '7,15,15,15,15');
INSERT INTO `cfg_city_special` VALUES (1922, '7,7', '10,15,15,15,20');
INSERT INTO `cfg_city_special` VALUES (1926, '10', '7,15,15,15,15');
INSERT INTO `cfg_city_special` VALUES (1934, '4', '8,13,13,13,13');
INSERT INTO `cfg_city_special` VALUES (1937, '4,4', '4,13,18,18,18');
INSERT INTO `cfg_city_special` VALUES (1942, '8,8', '13,13,13,13,13');
INSERT INTO `cfg_city_special` VALUES (2014, '3', '5,14,14,14,14');
INSERT INTO `cfg_city_special` VALUES (2030, '10,10', '15,15,15,15,20');
INSERT INTO `cfg_city_special` VALUES (2039, '8', '8,13,13,13,13');
INSERT INTO `cfg_city_special` VALUES (2046, '4', '4,21,21,21,21');
INSERT INTO `cfg_city_special` VALUES (2201, '3', '5,17,17,24,24');
INSERT INTO `cfg_city_special` VALUES (2217, '10,10', '15,15,20,20,20');
INSERT INTO `cfg_city_special` VALUES (2308, '5', '5,17,17,17,24');
INSERT INTO `cfg_city_special` VALUES (2346, '8', '8,18,18,18,21');
INSERT INTO `cfg_city_special` VALUES (2414, '5', '5,14,14,24,24');
INSERT INTO `cfg_city_special` VALUES (2430, '7', '7,15,15,15,20');
INSERT INTO `cfg_city_special` VALUES (2433, '8', '4,13,13,13,13');
INSERT INTO `cfg_city_special` VALUES (2501, '5,5', '17,17,17,17,24');
INSERT INTO `cfg_city_special` VALUES (2517, '7', '10,15,15,15,15');
INSERT INTO `cfg_city_special` VALUES (2539, '8,8', '21,21,21,21,21');
INSERT INTO `cfg_city_special` VALUES (2608, '5,5', '14,14,24,24,24');
INSERT INTO `cfg_city_special` VALUES (2610, '3', '3,17,17,17,17');
INSERT INTO `cfg_city_special` VALUES (2636, '4', '4,18,18,21,21');
INSERT INTO `cfg_city_special` VALUES (2646, '4,4', '8,18,18,18,18');
INSERT INTO `cfg_city_special` VALUES (2714, '5,5', '14,14,17,17,24');
INSERT INTO `cfg_city_special` VALUES (2721, '7,7', '7,15,15,15,20');
INSERT INTO `cfg_city_special` VALUES (2725, '10,10', '15,15,15,20,25');
INSERT INTO `cfg_city_special` VALUES (2733, '8,8', '13,13,13,18,18');
INSERT INTO `cfg_city_special` VALUES (2817, '10', '7,15,15,15,15');
INSERT INTO `cfg_city_special` VALUES (2830, '10', '10,15,15,15,15');
INSERT INTO `cfg_city_special` VALUES (2901, '3', '3,24,24,24,24');
INSERT INTO `cfg_city_special` VALUES (2908, '3', '3,14,14,17,17');
INSERT INTO `cfg_city_special` VALUES (2924, '7', '10,20,20,20,20');
INSERT INTO `cfg_city_special` VALUES (3033, '4', '4,13,13,13,13');
INSERT INTO `cfg_city_special` VALUES (3039, '4,4', '4,13,13,13,18');
INSERT INTO `cfg_city_special` VALUES (3130, '7,7', '10,15,15,15,20');
INSERT INTO `cfg_city_special` VALUES (3201, '3,3', '3,17,17,17,24');
INSERT INTO `cfg_city_special` VALUES (3214, '5', '3,14,14,14,17');
INSERT INTO `cfg_city_special` VALUES (3224, '10,10', '15,15,20,20,25');
INSERT INTO `cfg_city_special` VALUES (3246, '8,8', '21,21,21,21,21');
INSERT INTO `cfg_city_special` VALUES (3406, '5', '3,17,17,17,17');
INSERT INTO `cfg_city_special` VALUES (3410, '3,3', '3,14,14,17,17');
INSERT INTO `cfg_city_special` VALUES (3418, '7,7', '7,15,15,15,20');
INSERT INTO `cfg_city_special` VALUES (3425, '10', '7,15,20,20,20');
INSERT INTO `cfg_city_special` VALUES (3427, '7', '10,15,15,15,15');
INSERT INTO `cfg_city_special` VALUES (3434, '4,4', '8,13,13,18,21');
INSERT INTO `cfg_city_special` VALUES (3437, '8', '8,13,21,21,21');
INSERT INTO `cfg_city_special` VALUES (3442, '4', '8,18,18,18,18');
INSERT INTO `cfg_city_special` VALUES (3719, '2', '9,11,23,23,23');
INSERT INTO `cfg_city_special` VALUES (3722, '9,9', '23,23,23,23,23');
INSERT INTO `cfg_city_special` VALUES (3725, '2,2', '9,11,11,19,23');
INSERT INTO `cfg_city_special` VALUES (3728, '9', '9,11,11,11,11');
INSERT INTO `cfg_city_special` VALUES (3817, '9', '2,11,11,19,19');
INSERT INTO `cfg_city_special` VALUES (4030, '2', '9,23,23,23,23');
INSERT INTO `cfg_city_special` VALUES (4117, '2,2', '2,19,19,19,19');
INSERT INTO `cfg_city_special` VALUES (4318, '2', '2,19,19,19,19');
INSERT INTO `cfg_city_special` VALUES (4321, '2,2', '9,11,19,19,23');
INSERT INTO `cfg_city_special` VALUES (4325, '9,9', '11,11,11,11,11');
INSERT INTO `cfg_city_special` VALUES (4328, '9', '9,11,11,11,11');
INSERT INTO `cfg_city_special` VALUES (4617, '9,9', '19,19,19,19,19');
INSERT INTO `cfg_city_special` VALUES (4630, '2', '2,11,11,11,23');
INSERT INTO `cfg_city_special` VALUES (5019, '9', '2,19,19,19,19');
INSERT INTO `cfg_city_special` VALUES (5022, '2', '9,23,23,23,23');
INSERT INTO `cfg_city_special` VALUES (5025, '9,9', '11,11,23,23,23');
INSERT INTO `cfg_city_special` VALUES (5028, '2,2', '2,11,11,11,23');
COMMIT;

-- ----------------------------
-- Table structure for cfg_exchange_good
-- ----------------------------
DROP TABLE IF EXISTS `cfg_exchange_good`;
CREATE TABLE `cfg_exchange_good` (
  `id` int(11) NOT NULL,
  `serial` int(11) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `good_id` int(11) DEFAULT NULL,
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `unit` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `num` int(11) NOT NULL,
  `way` int(11) NOT NULL,
  `is_valid` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of cfg_exchange_good
-- ----------------------------
BEGIN;
INSERT INTO `cfg_exchange_good` VALUES (810, 810, 60, 810, '一星灵石', 2, 30, 1, 2, b'1');
INSERT INTO `cfg_exchange_good` VALUES (820, 820, 60, 820, '二星灵石', 2, 100, 1, 2, b'1');
INSERT INTO `cfg_exchange_good` VALUES (830, 830, 60, 830, '三星灵石', 2, 300, 1, 2, b'1');
INSERT INTO `cfg_exchange_good` VALUES (840, 840, 60, 840, '四星灵石', 2, 1000, 1, 2, b'1');
INSERT INTO `cfg_exchange_good` VALUES (850, 850, 60, 850, '五星灵石', 2, 3000, 1, 2, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2010, 10, 50, 10, '金元素', 1, 1000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2020, 20, 50, 20, '木元素', 1, 1000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2030, 30, 50, 30, '水元素', 1, 1000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2040, 50, 50, 40, '火元素', 1, 1000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2050, 50, 50, 50, '土元素', 1, 1000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2070, 70, 20, NULL, '铜钱', 1, 1000, 10000, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2329, 103, 40, 328, '碧云', 1, 300000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2330, 102, 40, 330, '厉鬼', 1, 150000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2332, 104, 40, 332, '雉鸡精', 1, 300000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2426, 107, 40, 426, '巡夜女使', 1, 1200000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2430, 108, 40, 430, '孔雀明王', 1, 2400000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2528, 101, 40, 528, '乌鸦兵', 1, 150000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2529, 105, 40, 529, '晁田', 1, 600000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (2530, 106, 40, 530, '晁雷', 1, 600000, 1, 1, b'1');
INSERT INTO `cfg_exchange_good` VALUES (3010, 10, 50, 10, '金元素', 3, 1, 1, 3, b'1');
INSERT INTO `cfg_exchange_good` VALUES (3020, 20, 50, 20, '木元素', 3, 1, 1, 3, b'1');
INSERT INTO `cfg_exchange_good` VALUES (3030, 30, 50, 30, '水元素', 3, 1, 1, 3, b'1');
INSERT INTO `cfg_exchange_good` VALUES (3040, 40, 50, 40, '火元素', 3, 1, 1, 3, b'1');
INSERT INTO `cfg_exchange_good` VALUES (3050, 50, 50, 50, '土元素', 3, 1, 1, 3, b'1');
INSERT INTO `cfg_exchange_good` VALUES (3060, 60, 60, 10030, '混沌仙石', 3, 30, 1, 3, b'1');
COMMIT;

-- ----------------------------
-- Table structure for cfg_mall
-- ----------------------------
DROP TABLE IF EXISTS `cfg_mall`;
CREATE TABLE `cfg_mall` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `goods_id` int(11) NOT NULL,
  `serial` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `unit` int(11) NOT NULL DEFAULT '1',
  `limit` int(11) NOT NULL DEFAULT '0',
  `peroid` bigint(20) NOT NULL DEFAULT '0',
  `status` bit(1) NOT NULL DEFAULT b'1' COMMENT '1-产品有效；0-无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1328 DEFAULT CHARSET=utf8mb4 COMMENT='商城\ngoods_id：指向cfg_treasure的ID\r\ntype: 10道具 20神秘 30卡包 40特惠';

-- ----------------------------
-- Records of cfg_mall
-- ----------------------------
BEGIN;
INSERT INTO `cfg_mall` VALUES (10, 20, 10, 10, 380, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (20, 20, 20, 20, 580, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (60, 10, 60, 60, 180, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (70, 0, 70, 70, 20, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (80, 20, 80, 80, 80, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (90, 10, 90, 90, 65, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (110, 20, 110, 110, 70, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (120, 10, 120, 120, 70, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (130, 10, 130, 130, 50, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (150, 10, 150, 150, 20, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (160, 10, 160, 160, 10, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (165, 10, 50, 165, 180, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (170, 0, 170, 170, 5000, 2, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (180, 20, 180, 180, 50000, 2, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (190, 20, 190, 190, 8, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (205, 0, 205, 205, 20, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (210, 20, 210, 210, 110, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (220, 20, 220, 220, 125, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (230, 20, 230, 230, 80, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (240, 20, 240, 240, 75, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (260, 20, 260, 260, 100, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (270, 20, 270, 270, 55, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (300, 20, 300, 300, 100, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (310, 20, 310, 310, 95, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (350, 20, 350, 350, 60, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (400, 20, 400, 400, 30, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (420, 20, 420, 420, 55, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (510, 10, 510, 3, 50, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (520, 10, 520, 1, 250, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (610, 30, 610, 5, 188, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (620, 30, 620, 7, 188, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (630, 30, 630, 9, 188, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (640, 30, 640, 11, 188, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (650, 30, 650, 13, 188, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (700, 30, 700, 15, 188, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (710, 30, 710, 17, 1692, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (910, 30, 910, 19, 1692, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (920, 30, 920, 21, 1692, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (930, 30, 930, 23, 1692, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (940, 30, 940, 25, 1692, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (950, 30, 950, 27, 1692, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1010, 40, 1010, 1030, 30000, 2, 1, 1, b'1');
INSERT INTO `cfg_mall` VALUES (1020, 40, 1020, 1040, 110, 1, 2, 1, b'1');
INSERT INTO `cfg_mall` VALUES (1030, 40, 1030, 1050, 50, 1, 2, 1, b'1');
INSERT INTO `cfg_mall` VALUES (1110, 40, 1110, 1060, 120, 1, 3, 7, b'1');
INSERT INTO `cfg_mall` VALUES (1120, 40, 1120, 1070, 240, 1, 2, 7, b'1');
INSERT INTO `cfg_mall` VALUES (1130, 40, 1130, 1080, 18, 3, 3, 7, b'1');
INSERT INTO `cfg_mall` VALUES (1210, 40, 1210, 1010, 6, 3, 1, -7, b'1');
INSERT INTO `cfg_mall` VALUES (1220, 40, 1220, 1020, 2560, 1, 1, -7, b'1');
INSERT INTO `cfg_mall` VALUES (1281, 50, 1281, 1281, 1, 3, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1282, 50, 1282, 1282, 6, 3, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1283, 50, 1283, 1283, 12, 3, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1314, 20, 840, 840, 200, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1315, 20, 850, 850, 600, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1316, 20, 10010, 10010, 400, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1318, 10, 10030, 10030, 200, 1, 0, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1319, 20, 30, 30, 180, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1320, 20, 540, 540, 50, 1, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1321, 20, 550, 550, 200000, 2, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1322, 20, 70, 70, 100000, 2, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1323, 20, 360, 360, 200000, 2, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1324, 20, 370, 370, 100000, 2, 1, 0, b'1');
INSERT INTO `cfg_mall` VALUES (1327, 10, 10040, 10040, 50, 1, 0, 0, b'1');
COMMIT;

-- ----------------------------
-- Table structure for cfg_product
-- ----------------------------
DROP TABLE IF EXISTS `cfg_product`;
CREATE TABLE `cfg_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `serial` int(11) unsigned NOT NULL,
  `group_id` int(3) unsigned NOT NULL,
  `inner_id` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `quantity` int(11) unsigned NOT NULL,
  `price` int(11) unsigned NOT NULL,
  `extra_num` int(11) unsigned NOT NULL COMMENT '额外的送值',
  `is_available` bit(1) NOT NULL,
  `recommend` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21145 DEFAULT CHARSET=utf8mb4 COMMENT=' 产品类别  黄金 960   白银950(和在道具枚举里面的值一致 )\r\n平台商店 目前就这两个  appl';

-- ----------------------------
-- Records of cfg_product
-- ----------------------------
BEGIN;
INSERT INTO `cfg_product` VALUES (51, 8, 0, 'wx3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (52, 7, 0, 'wx1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (53, 6, 0, 'wx980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (54, 5, 0, 'wx680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (55, 4, 0, 'wx180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (56, 3, 0, 'wx60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (57, 9, 0, 'wx6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (58, 2, 0, 'wxTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (59, 1, 0, 'wxyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (101, 8, 201, '201yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (102, 7, 201, '201yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (103, 6, 201, '201yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (104, 5, 201, '201yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (105, 4, 201, '201yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (106, 3, 201, '201yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (107, 9, 201, '201zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (108, 2, 201, '201zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (109, 1, 201, '201zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (201, 8, 202, '202yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (202, 7, 202, '202yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (203, 6, 202, '202yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (204, 5, 202, '202yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (205, 4, 202, '202yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (206, 3, 202, '202yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (207, 9, 202, '202zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (208, 2, 202, '202zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (209, 1, 202, '202zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (301, 8, 203, '203yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (302, 7, 203, '203yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (303, 6, 203, '203yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (304, 5, 203, '203yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (305, 4, 203, '203yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (306, 3, 203, '203yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (307, 9, 203, '203zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (308, 2, 203, '203zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (309, 1, 203, '203zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (401, 8, 204, '204yxyuanbao3280', '3280元宝', 3280, 328, 500, b'0', b'0');
INSERT INTO `cfg_product` VALUES (402, 7, 204, '204yxyuanbao1980', '1980元宝', 1980, 198, 250, b'0', b'1');
INSERT INTO `cfg_product` VALUES (403, 6, 204, '204yxyuanbao980', '980元宝', 980, 98, 100, b'0', b'0');
INSERT INTO `cfg_product` VALUES (404, 5, 204, '204yxyuanbao680', '680元宝', 680, 68, 50, b'0', b'1');
INSERT INTO `cfg_product` VALUES (405, 4, 204, '204yxyuanbao180', '180元宝', 180, 18, 10, b'0', b'0');
INSERT INTO `cfg_product` VALUES (406, 3, 204, '204yxyuanbao60', '60元宝', 60, 6, 0, b'0', b'0');
INSERT INTO `cfg_product` VALUES (407, 9, 204, '204zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'0', b'1');
INSERT INTO `cfg_product` VALUES (408, 2, 204, '204zfSTF', '300元宝速战卡', 300, 30, 0, b'0', b'1');
INSERT INTO `cfg_product` VALUES (409, 1, 204, '204zfyueka', '300元宝月卡', 300, 30, 0, b'0', b'1');
INSERT INTO `cfg_product` VALUES (501, 8, 205, '205yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (502, 7, 205, '205yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (503, 6, 205, '205yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (504, 5, 205, '205yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (505, 4, 205, '205yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (506, 3, 205, '205yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (507, 9, 205, '205zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (508, 2, 205, '205zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (509, 1, 205, '205zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (601, 8, 206, '206yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (602, 7, 206, '206yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (603, 6, 206, '206yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (604, 5, 206, '206yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (605, 4, 206, '206yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (606, 3, 206, '206yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (607, 9, 206, '206zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (608, 2, 206, '206zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (609, 1, 206, '206zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (701, 8, 207, '207yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (702, 7, 207, '207yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (703, 6, 207, '207yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (704, 5, 207, '207yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (705, 4, 207, '207yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (706, 3, 207, '207yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (707, 9, 207, '207zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (708, 2, 207, '207zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (709, 1, 207, '207zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (801, 8, 208, '208yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (802, 7, 208, '208yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (803, 6, 208, '208yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (804, 5, 208, '208yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (805, 4, 208, '208yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (806, 3, 208, '208yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (807, 9, 208, '208zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (808, 2, 208, '208zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (809, 1, 208, '208zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (901, 8, 209, '209yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (902, 7, 209, '209yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (903, 6, 209, '209yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (904, 5, 209, '209yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (905, 4, 209, '209yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (906, 3, 209, '209yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (907, 9, 209, '209zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (908, 2, 209, '209zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (909, 1, 209, '209zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1001, 8, 210, '210yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1002, 7, 210, '210yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1003, 6, 210, '210yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1004, 5, 210, '210yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1005, 4, 210, '210yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1006, 3, 210, '210yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1007, 9, 210, '210zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1008, 2, 210, '210zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1009, 1, 210, '210zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1101, 8, 211, '211yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1102, 7, 211, '211yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1103, 6, 211, '211yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1104, 5, 211, '211yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1105, 4, 211, '211yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1106, 3, 211, '211yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1107, 9, 211, '211zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1108, 2, 211, '211zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1109, 1, 211, '211zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1201, 8, 212, '212yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1202, 7, 212, '212yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1203, 6, 212, '212yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1204, 5, 212, '212yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1205, 4, 212, '212yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1206, 3, 212, '212yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1207, 9, 212, '212zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1208, 2, 212, '212zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1209, 1, 212, '212zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1301, 8, 213, '213yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1302, 7, 213, '213yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1303, 6, 213, '213yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1304, 5, 213, '213yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1305, 4, 213, '213yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1306, 3, 213, '213yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1307, 9, 213, '213zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1308, 2, 213, '213zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1309, 1, 213, '213zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1401, 8, 214, '214yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1402, 7, 214, '214yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1403, 6, 214, '214yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1404, 5, 214, '214yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1405, 4, 214, '214yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1406, 3, 214, '214yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1407, 9, 214, '214zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1408, 2, 214, '214zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1409, 1, 214, '214zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1501, 8, 215, '215yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1502, 7, 215, '215yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1503, 6, 215, '215yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1504, 5, 215, '215yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1505, 4, 215, '215yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1506, 3, 215, '215yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1507, 9, 215, '215zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1508, 2, 215, '215zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1509, 1, 215, '215zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1601, 8, 216, '216yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1602, 7, 216, '216yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1603, 6, 216, '216yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1604, 5, 216, '216yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1605, 4, 216, '216yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1606, 3, 216, '216yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1607, 9, 216, '216zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1608, 2, 216, '216zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1609, 1, 216, '216zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1701, 8, 217, '217yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1702, 7, 217, '217yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1703, 6, 217, '217yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1704, 5, 217, '217yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1705, 4, 217, '217yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1706, 3, 217, '217yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1707, 9, 217, '217zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1708, 2, 217, '217fSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1709, 1, 217, '217zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1801, 8, 218, '218yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1802, 7, 218, '218yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1803, 6, 218, '218yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1804, 5, 218, '218yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1805, 4, 218, '218yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1806, 3, 218, '218yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1807, 9, 218, '218zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1808, 2, 218, '218zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1809, 1, 218, '218zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1901, 8, 219, '219yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1902, 7, 219, '219yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1903, 6, 219, '219yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1904, 5, 219, '219yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1905, 4, 219, '219yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1906, 3, 219, '219yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (1907, 9, 219, '219zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1908, 2, 219, '219zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (1909, 1, 219, '219zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2001, 8, 220, '220yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2002, 7, 220, '220yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2003, 6, 220, '220yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2004, 5, 220, '220yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2005, 4, 220, '220yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2006, 3, 220, '220yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2007, 9, 220, '220zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2008, 2, 220, '220zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2009, 1, 220, '220zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2101, 8, 221, '221yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2102, 7, 221, '221yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2103, 6, 221, '221yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2104, 5, 221, '221yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2105, 4, 221, '221yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2106, 3, 221, '221yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2107, 9, 221, '221zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2108, 2, 221, '221zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2109, 1, 221, '221zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2201, 8, 222, '222yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2202, 7, 222, '222yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2203, 6, 222, '222yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2204, 5, 222, '222yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2205, 4, 222, '222yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2206, 3, 222, '222yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2207, 9, 222, '222zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2208, 2, 222, '222zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2209, 1, 222, '222zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2301, 8, 223, '223yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2302, 7, 223, '223yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2303, 6, 223, '223yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2304, 5, 223, '223yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2305, 4, 223, '223yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2306, 3, 223, '223yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2307, 9, 223, '223zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2308, 2, 223, '223zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2309, 1, 223, '223zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2401, 8, 224, '224yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2402, 7, 224, '224yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2403, 6, 224, '224yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2404, 5, 224, '224yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2405, 4, 224, '224yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2406, 3, 224, '224yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2407, 9, 224, '224zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2408, 2, 224, '224zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2409, 1, 224, '224zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2501, 8, 225, '225yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2502, 7, 225, '225yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2503, 6, 225, '225yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2504, 5, 225, '225yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2505, 4, 225, '225yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2506, 3, 225, '225yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2507, 9, 225, '225zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2508, 2, 225, '225zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2509, 1, 225, '225zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2601, 8, 226, '226yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2602, 7, 226, '226yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2603, 6, 226, '226yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2604, 5, 226, '226yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2605, 4, 226, '226yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2606, 3, 226, '226yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2607, 9, 226, '226zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2608, 2, 226, '226zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2609, 1, 226, '226zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2701, 8, 227, '226yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2702, 7, 227, '226yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2703, 6, 227, '226yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2704, 5, 227, '226yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2705, 4, 227, '226yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2706, 3, 227, '226yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2707, 9, 227, '226zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2708, 2, 227, '226zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2709, 1, 227, '226zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2801, 8, 228, '226yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2802, 7, 228, '226yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2803, 6, 228, '226yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2804, 5, 228, '226yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2805, 4, 228, '226yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2806, 3, 228, '226yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2807, 9, 228, '226zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2808, 2, 228, '226zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2809, 1, 228, '226zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2901, 8, 229, '226yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2902, 7, 229, '226yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2903, 6, 229, '226yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2904, 5, 229, '226yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2905, 4, 229, '226yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2906, 3, 229, '226yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (2907, 9, 229, '226zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2908, 2, 229, '226zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (2909, 1, 229, '226zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3001, 8, 230, '226yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3002, 7, 230, '226yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3003, 6, 230, '226yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3004, 5, 230, '226yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3005, 4, 230, '226yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3006, 3, 230, '226yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3007, 9, 230, '226zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3008, 2, 230, '226zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3009, 1, 230, '226zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3101, 8, 231, '226yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3102, 7, 231, '226yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3103, 6, 231, '226yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3104, 5, 231, '226yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3105, 4, 231, '226yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3106, 3, 231, '226yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3107, 9, 231, '226zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3108, 2, 231, '226zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3109, 1, 231, '226zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3201, 8, 232, 'yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3202, 7, 232, 'yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3203, 6, 232, 'yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3204, 5, 232, 'yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3205, 4, 232, 'yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3206, 3, 232, 'yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3207, 9, 232, 'zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3208, 2, 232, 'zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3209, 1, 232, 'zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3301, 8, 233, '233yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3302, 7, 233, '233yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3303, 6, 233, '233yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3304, 5, 233, '233yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3305, 4, 233, '233yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3306, 3, 233, '233yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3307, 9, 233, '233zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3308, 2, 233, '233zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3309, 1, 233, '233zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3401, 8, 234, '6', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3402, 7, 234, '5', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3403, 6, 234, '4', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3404, 5, 234, '3', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3405, 4, 234, '2', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3406, 3, 234, '1', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (3407, 9, 234, '7', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3408, 2, 234, '9', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (3409, 1, 234, '8', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (11001, 8, 1001, '1001yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (11002, 7, 1001, '1001yxyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (11003, 6, 1001, '1001yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (11004, 5, 1001, '1001yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (11005, 4, 1001, '1001yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (11006, 3, 1001, '1001yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (11007, 9, 1001, '1001zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (11008, 2, 1001, '1001zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (11009, 1, 1001, '1001zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (12001, 8, 1002, '1002yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (12002, 7, 1002, '1002xyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (12003, 6, 1002, '1002yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (12004, 5, 1002, '1002yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (12005, 4, 1002, '1002yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (12006, 3, 1002, '1002yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (12007, 9, 1002, '1002zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (12008, 2, 1002, '1002zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (12009, 1, 1002, '1002zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (13001, 8, 1003, '1003yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (13002, 7, 1003, '1003xyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (13003, 6, 1003, '1003yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (13004, 5, 1003, '1003yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (13005, 4, 1003, '1003yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (13006, 3, 1003, '1003yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (13007, 9, 1003, '1003zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (13008, 2, 1003, '1003zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (13009, 1, 1003, '1003zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (14001, 8, 1004, '1004yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (14002, 7, 1004, '1004xyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (14003, 6, 1004, '1004yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (14004, 5, 1004, '1004yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (14005, 4, 1004, '1004yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (14006, 3, 1004, '1004yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (14007, 9, 1004, '1004zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (14008, 2, 1004, '1004zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (14009, 1, 1004, '1004zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (15001, 8, 1005, '1005yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (15002, 7, 1005, '1005xyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (15003, 6, 1005, '1005yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (15004, 5, 1005, '1005yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (15005, 4, 1005, '1005yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (15006, 3, 1005, '1005yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (15007, 9, 1005, '1005zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (15008, 2, 1005, '1005zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (15009, 1, 1005, '1005zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (16001, 8, 1006, '1006yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (16002, 7, 1006, '1006xyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (16003, 6, 1006, '1006yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (16004, 5, 1006, '1006yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (16005, 4, 1006, '1006yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (16006, 3, 1006, '1006yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (16007, 9, 1006, '1006zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (16008, 2, 1006, '1006zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (16009, 1, 1006, '1006zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (17001, 8, 1007, '1007yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (17002, 7, 1007, '1007xyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (17003, 6, 1007, '1007yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (17004, 5, 1007, '1007yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (17005, 4, 1007, '1007yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (17006, 3, 1007, '1007yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (17007, 9, 1007, '1007zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (17008, 2, 1007, '1007zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (17009, 1, 1007, '1007zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (18006, 3, 1008, 'yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (18007, 9, 1008, 'zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (18008, 2, 1008, 'zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (18009, 1, 1008, 'zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (19001, 8, 1009, '1007yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (19002, 7, 1009, '1007xyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (19003, 6, 1009, '1007yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (19004, 5, 1009, '1007yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (19005, 4, 1009, '1007yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (19006, 3, 1009, '1007yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (19007, 9, 1009, '1007zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (19008, 2, 1009, '1007zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (19009, 1, 1009, '1007zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (20001, 8, 1010, '1007yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (20002, 7, 1010, '1007xyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (20003, 6, 1010, '1007yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (20004, 5, 1010, '1007yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (20005, 4, 1010, '1007yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (20006, 3, 1010, '1007yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (20007, 9, 1010, '1007zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (20008, 2, 1010, '1007zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (20009, 1, 1010, '1007zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21001, 8, 1011, '1007yxyuanbao3280', '3280元宝', 3280, 328, 500, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21002, 7, 1011, '1007xyuanbao1980', '1980元宝', 1980, 198, 250, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21003, 6, 1011, '1007yxyuanbao980', '980元宝', 980, 98, 100, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21004, 5, 1011, '1007yxyuanbao680', '680元宝', 680, 68, 50, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21005, 4, 1011, '1007yxyuanbao180', '180元宝', 180, 18, 10, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21006, 3, 1011, '1007yxyuanbao60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21007, 9, 1011, '1007zfyuanbao6480', '6480元宝', 6480, 648, 1200, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21008, 2, 1011, '1007zfSTF', '300元宝速战卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21009, 1, 1011, '1007zfyueka', '300元宝月卡', 300, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21010, 8, 235, '6', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21011, 7, 235, '5', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21012, 6, 235, '4', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21013, 5, 235, '3', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21014, 4, 235, '2', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21015, 3, 235, '1', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21016, 9, 235, '7', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21017, 2, 235, '8', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21018, 1, 235, '9', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21019, 8, 236, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21020, 7, 236, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21021, 6, 236, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21022, 5, 236, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21023, 4, 236, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21024, 3, 236, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21025, 9, 236, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21026, 2, 236, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21027, 1, 236, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21028, 8, 237, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21029, 7, 237, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21030, 6, 237, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21031, 5, 237, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21032, 4, 237, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21033, 3, 237, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21034, 9, 237, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21035, 2, 237, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21036, 1, 237, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21037, 8, 238, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21038, 7, 238, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21039, 6, 238, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21040, 5, 238, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21041, 4, 238, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21042, 3, 238, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21043, 9, 238, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21044, 2, 238, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21045, 1, 238, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21046, 8, 239, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21047, 7, 239, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21048, 6, 239, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21049, 5, 239, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21050, 4, 239, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21051, 3, 239, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21052, 9, 239, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21053, 2, 239, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21054, 1, 239, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21055, 8, 240, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21056, 7, 240, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21057, 6, 240, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21058, 5, 240, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21059, 4, 240, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21060, 3, 240, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21061, 9, 240, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21062, 2, 240, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21063, 1, 240, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21064, 8, 241, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21065, 7, 241, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21066, 6, 241, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21067, 5, 241, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21068, 4, 241, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21069, 3, 241, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21070, 9, 241, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21071, 2, 241, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21072, 1, 241, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21073, 8, 242, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21074, 7, 242, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21075, 6, 242, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21076, 5, 242, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21077, 4, 242, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21078, 3, 242, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21079, 9, 242, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21080, 2, 242, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21081, 1, 242, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21082, 8, 243, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21083, 7, 243, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21084, 6, 243, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21085, 5, 243, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21086, 4, 243, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21087, 3, 243, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21088, 9, 243, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21089, 2, 243, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21090, 1, 243, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21091, 8, 244, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21092, 7, 244, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21093, 6, 244, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21094, 5, 244, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21095, 4, 244, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21096, 3, 244, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21097, 9, 244, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21098, 2, 244, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21099, 1, 244, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21100, 8, 245, '121855', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21101, 7, 245, '121854', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21102, 6, 245, '121853', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21103, 5, 245, '121852', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21104, 4, 245, '121851', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21105, 3, 245, '121850', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21106, 9, 245, '121856', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21107, 2, 245, '121857', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21108, 1, 245, '121858', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21109, 8, 246, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21110, 7, 246, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21111, 6, 246, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21112, 5, 246, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21113, 4, 246, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21114, 3, 246, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21115, 9, 246, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21116, 2, 246, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21117, 1, 246, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21118, 8, 247, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21119, 7, 247, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21120, 6, 247, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21121, 5, 247, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21122, 4, 247, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21123, 3, 247, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21124, 9, 247, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21125, 2, 247, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21126, 1, 247, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21127, 8, 248, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21128, 7, 248, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21129, 6, 248, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21130, 5, 248, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21131, 4, 248, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21132, 3, 248, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21133, 9, 248, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21134, 2, 248, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21135, 1, 248, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21136, 8, 249, 'god3280', '3280元宝', 3280, 328, 600, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21137, 7, 249, 'god1980', '1980元宝', 1980, 198, 300, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21138, 6, 249, 'god980', '980元宝', 980, 98, 120, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21139, 5, 249, 'god680', '680元宝', 680, 68, 60, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21140, 4, 249, 'god180', '180元宝', 180, 18, 12, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21141, 3, 249, 'god60', '60元宝', 60, 6, 0, b'1', b'0');
INSERT INTO `cfg_product` VALUES (21142, 9, 249, 'god6480', '6480元宝', 6480, 648, 1440, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21143, 2, 249, 'godTF', '360元宝速战卡', 360, 30, 0, b'1', b'1');
INSERT INTO `cfg_product` VALUES (21144, 1, 249, 'godyueka', '360元宝月卡', 360, 30, 0, b'1', b'1');
COMMIT;

-- ----------------------------
-- Table structure for cfg_road
-- ----------------------------
DROP TABLE IF EXISTS `cfg_road`;
CREATE TABLE `cfg_road` (
  `id` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `way` char(4) NOT NULL,
  `country` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=' way 前后左右 有路用1 ，没路0    1111  表示都有路';

-- ----------------------------
-- Records of cfg_road
-- ----------------------------
BEGIN;
INSERT INTO `cfg_road` VALUES (117, 1, 17, '1100', 20);
INSERT INTO `cfg_road` VALUES (118, 1, 18, '1001', 20);
INSERT INTO `cfg_road` VALUES (119, 1, 19, '1001', 20);
INSERT INTO `cfg_road` VALUES (120, 1, 20, '1001', 20);
INSERT INTO `cfg_road` VALUES (121, 1, 21, '1001', 20);
INSERT INTO `cfg_road` VALUES (122, 1, 22, '1001', 20);
INSERT INTO `cfg_road` VALUES (123, 1, 23, '1001', 20);
INSERT INTO `cfg_road` VALUES (124, 1, 24, '1001', 20);
INSERT INTO `cfg_road` VALUES (125, 1, 25, '1001', 20);
INSERT INTO `cfg_road` VALUES (126, 1, 26, '1001', 20);
INSERT INTO `cfg_road` VALUES (127, 1, 27, '1001', 20);
INSERT INTO `cfg_road` VALUES (128, 1, 28, '1001', 20);
INSERT INTO `cfg_road` VALUES (129, 1, 29, '1001', 20);
INSERT INTO `cfg_road` VALUES (130, 1, 30, '0101', 20);
INSERT INTO `cfg_road` VALUES (217, 2, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (230, 2, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (317, 3, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (330, 3, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (417, 4, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (430, 4, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (517, 5, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (530, 5, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (617, 6, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (630, 6, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (717, 7, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (730, 7, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (817, 8, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (830, 8, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (917, 9, 17, '1110', 20);
INSERT INTO `cfg_road` VALUES (918, 9, 18, '1001', 20);
INSERT INTO `cfg_road` VALUES (919, 9, 19, '1001', 20);
INSERT INTO `cfg_road` VALUES (920, 9, 20, '1001', 20);
INSERT INTO `cfg_road` VALUES (921, 9, 21, '1001', 20);
INSERT INTO `cfg_road` VALUES (922, 9, 22, '1001', 20);
INSERT INTO `cfg_road` VALUES (923, 9, 23, '1001', 20);
INSERT INTO `cfg_road` VALUES (924, 9, 24, '1101', 20);
INSERT INTO `cfg_road` VALUES (925, 9, 25, '1001', 20);
INSERT INTO `cfg_road` VALUES (926, 9, 26, '1001', 20);
INSERT INTO `cfg_road` VALUES (927, 9, 27, '1001', 20);
INSERT INTO `cfg_road` VALUES (928, 9, 28, '1001', 20);
INSERT INTO `cfg_road` VALUES (929, 9, 29, '1001', 20);
INSERT INTO `cfg_road` VALUES (930, 9, 30, '0111', 20);
INSERT INTO `cfg_road` VALUES (1017, 10, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (1024, 10, 24, '0110', 20);
INSERT INTO `cfg_road` VALUES (1030, 10, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (1117, 11, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (1124, 11, 24, '0110', 20);
INSERT INTO `cfg_road` VALUES (1130, 11, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (1217, 12, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (1224, 12, 24, '0110', 20);
INSERT INTO `cfg_road` VALUES (1230, 12, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (1317, 13, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (1324, 13, 24, '0110', 20);
INSERT INTO `cfg_road` VALUES (1330, 13, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (1417, 14, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (1424, 14, 24, '0110', 20);
INSERT INTO `cfg_road` VALUES (1430, 14, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (1517, 15, 17, '1110', 20);
INSERT INTO `cfg_road` VALUES (1518, 15, 18, '1001', 20);
INSERT INTO `cfg_road` VALUES (1519, 15, 19, '1001', 20);
INSERT INTO `cfg_road` VALUES (1520, 15, 20, '1001', 20);
INSERT INTO `cfg_road` VALUES (1521, 15, 21, '1001', 20);
INSERT INTO `cfg_road` VALUES (1522, 15, 22, '1001', 20);
INSERT INTO `cfg_road` VALUES (1523, 15, 23, '1001', 20);
INSERT INTO `cfg_road` VALUES (1524, 15, 24, '1111', 20);
INSERT INTO `cfg_road` VALUES (1525, 15, 25, '1001', 20);
INSERT INTO `cfg_road` VALUES (1526, 15, 26, '1001', 20);
INSERT INTO `cfg_road` VALUES (1527, 15, 27, '1001', 20);
INSERT INTO `cfg_road` VALUES (1528, 15, 28, '1001', 20);
INSERT INTO `cfg_road` VALUES (1529, 15, 29, '1001', 20);
INSERT INTO `cfg_road` VALUES (1530, 15, 30, '0111', 20);
INSERT INTO `cfg_road` VALUES (1617, 16, 17, '0110', 20);
INSERT INTO `cfg_road` VALUES (1624, 16, 24, '0110', 20);
INSERT INTO `cfg_road` VALUES (1630, 16, 30, '0110', 20);
INSERT INTO `cfg_road` VALUES (1715, 17, 15, '1100', 20);
INSERT INTO `cfg_road` VALUES (1716, 17, 16, '1001', 20);
INSERT INTO `cfg_road` VALUES (1717, 17, 17, '0011', 20);
INSERT INTO `cfg_road` VALUES (1724, 17, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (1730, 17, 30, '1010', 20);
INSERT INTO `cfg_road` VALUES (1731, 17, 31, '1001', 20);
INSERT INTO `cfg_road` VALUES (1732, 17, 32, '0101', 30);
INSERT INTO `cfg_road` VALUES (1815, 18, 15, '0110', 20);
INSERT INTO `cfg_road` VALUES (1824, 18, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (1832, 18, 32, '0110', 30);
INSERT INTO `cfg_road` VALUES (1901, 19, 1, '1100', 40);
INSERT INTO `cfg_road` VALUES (1902, 19, 2, '1001', 40);
INSERT INTO `cfg_road` VALUES (1903, 19, 3, '1001', 40);
INSERT INTO `cfg_road` VALUES (1904, 19, 4, '1001', 40);
INSERT INTO `cfg_road` VALUES (1905, 19, 5, '1001', 40);
INSERT INTO `cfg_road` VALUES (1906, 19, 6, '1001', 40);
INSERT INTO `cfg_road` VALUES (1907, 19, 7, '1001', 40);
INSERT INTO `cfg_road` VALUES (1908, 19, 8, '1101', 40);
INSERT INTO `cfg_road` VALUES (1909, 19, 9, '1001', 40);
INSERT INTO `cfg_road` VALUES (1910, 19, 10, '1001', 40);
INSERT INTO `cfg_road` VALUES (1911, 19, 11, '1001', 40);
INSERT INTO `cfg_road` VALUES (1912, 19, 12, '1001', 40);
INSERT INTO `cfg_road` VALUES (1913, 19, 13, '1001', 40);
INSERT INTO `cfg_road` VALUES (1914, 19, 14, '1101', 40);
INSERT INTO `cfg_road` VALUES (1915, 19, 15, '0011', 40);
INSERT INTO `cfg_road` VALUES (1917, 19, 17, '1100', 50);
INSERT INTO `cfg_road` VALUES (1918, 19, 18, '1001', 50);
INSERT INTO `cfg_road` VALUES (1919, 19, 19, '1001', 50);
INSERT INTO `cfg_road` VALUES (1920, 19, 20, '1001', 50);
INSERT INTO `cfg_road` VALUES (1921, 19, 21, '1001', 50);
INSERT INTO `cfg_road` VALUES (1922, 19, 22, '1001', 50);
INSERT INTO `cfg_road` VALUES (1923, 19, 23, '1001', 50);
INSERT INTO `cfg_road` VALUES (1924, 19, 24, '1011', 50);
INSERT INTO `cfg_road` VALUES (1925, 19, 25, '1001', 50);
INSERT INTO `cfg_road` VALUES (1926, 19, 26, '1001', 50);
INSERT INTO `cfg_road` VALUES (1927, 19, 27, '1001', 50);
INSERT INTO `cfg_road` VALUES (1928, 19, 28, '1001', 50);
INSERT INTO `cfg_road` VALUES (1929, 19, 29, '1001', 50);
INSERT INTO `cfg_road` VALUES (1930, 19, 30, '0101', 50);
INSERT INTO `cfg_road` VALUES (1932, 19, 32, '1010', 30);
INSERT INTO `cfg_road` VALUES (1933, 19, 33, '1101', 30);
INSERT INTO `cfg_road` VALUES (1934, 19, 34, '1001', 30);
INSERT INTO `cfg_road` VALUES (1935, 19, 35, '1001', 30);
INSERT INTO `cfg_road` VALUES (1936, 19, 36, '1001', 30);
INSERT INTO `cfg_road` VALUES (1937, 19, 37, '1001', 30);
INSERT INTO `cfg_road` VALUES (1938, 19, 38, '1001', 30);
INSERT INTO `cfg_road` VALUES (1939, 19, 39, '1101', 30);
INSERT INTO `cfg_road` VALUES (1940, 19, 40, '1001', 30);
INSERT INTO `cfg_road` VALUES (1941, 19, 41, '1001', 30);
INSERT INTO `cfg_road` VALUES (1942, 19, 42, '1001', 30);
INSERT INTO `cfg_road` VALUES (1943, 19, 43, '1001', 30);
INSERT INTO `cfg_road` VALUES (1944, 19, 44, '1001', 30);
INSERT INTO `cfg_road` VALUES (1945, 19, 45, '1001', 30);
INSERT INTO `cfg_road` VALUES (1946, 19, 46, '0101', 30);
INSERT INTO `cfg_road` VALUES (2001, 20, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2008, 20, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (2014, 20, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (2017, 20, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (2030, 20, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (2033, 20, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (2039, 20, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (2046, 20, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (2101, 21, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2108, 21, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (2114, 21, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (2117, 21, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (2130, 21, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (2133, 21, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (2139, 21, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (2146, 21, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (2201, 22, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2208, 22, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (2214, 22, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (2217, 22, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (2230, 22, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (2233, 22, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (2239, 22, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (2246, 22, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (2301, 23, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2308, 23, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (2314, 23, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (2317, 23, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (2330, 23, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (2333, 23, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (2339, 23, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (2346, 23, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (2401, 24, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2408, 24, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (2414, 24, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (2417, 24, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (2430, 24, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (2433, 24, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (2439, 24, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (2446, 24, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (2501, 25, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2508, 25, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (2514, 25, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (2517, 25, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (2530, 25, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (2533, 25, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (2539, 25, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (2546, 25, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (2601, 26, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2608, 26, 8, '1110', 40);
INSERT INTO `cfg_road` VALUES (2609, 26, 9, '1001', 40);
INSERT INTO `cfg_road` VALUES (2610, 26, 10, '1001', 40);
INSERT INTO `cfg_road` VALUES (2611, 26, 11, '1001', 40);
INSERT INTO `cfg_road` VALUES (2612, 26, 12, '1001', 40);
INSERT INTO `cfg_road` VALUES (2613, 26, 13, '1001', 40);
INSERT INTO `cfg_road` VALUES (2614, 26, 14, '1111', 40);
INSERT INTO `cfg_road` VALUES (2615, 26, 15, '1001', 40);
INSERT INTO `cfg_road` VALUES (2616, 26, 16, '1001', 50);
INSERT INTO `cfg_road` VALUES (2617, 26, 17, '0111', 50);
INSERT INTO `cfg_road` VALUES (2630, 26, 30, '1110', 50);
INSERT INTO `cfg_road` VALUES (2631, 26, 31, '1001', 50);
INSERT INTO `cfg_road` VALUES (2632, 26, 32, '1001', 50);
INSERT INTO `cfg_road` VALUES (2633, 26, 33, '1111', 30);
INSERT INTO `cfg_road` VALUES (2634, 26, 34, '1001', 30);
INSERT INTO `cfg_road` VALUES (2635, 26, 35, '1001', 30);
INSERT INTO `cfg_road` VALUES (2636, 26, 36, '1001', 30);
INSERT INTO `cfg_road` VALUES (2637, 26, 37, '1001', 30);
INSERT INTO `cfg_road` VALUES (2638, 26, 38, '1001', 30);
INSERT INTO `cfg_road` VALUES (2639, 26, 39, '0111', 30);
INSERT INTO `cfg_road` VALUES (2646, 26, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (2701, 27, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2708, 27, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (2714, 27, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (2717, 27, 17, '1110', 50);
INSERT INTO `cfg_road` VALUES (2718, 27, 18, '1001', 50);
INSERT INTO `cfg_road` VALUES (2719, 27, 19, '1001', 50);
INSERT INTO `cfg_road` VALUES (2720, 27, 20, '1001', 50);
INSERT INTO `cfg_road` VALUES (2721, 27, 21, '1001', 50);
INSERT INTO `cfg_road` VALUES (2722, 27, 22, '1001', 50);
INSERT INTO `cfg_road` VALUES (2723, 27, 23, '1001', 50);
INSERT INTO `cfg_road` VALUES (2724, 27, 24, '1101', 50);
INSERT INTO `cfg_road` VALUES (2725, 27, 25, '1001', 50);
INSERT INTO `cfg_road` VALUES (2726, 27, 26, '1001', 50);
INSERT INTO `cfg_road` VALUES (2727, 27, 27, '1001', 50);
INSERT INTO `cfg_road` VALUES (2728, 27, 28, '1001', 50);
INSERT INTO `cfg_road` VALUES (2729, 27, 29, '1001', 50);
INSERT INTO `cfg_road` VALUES (2730, 27, 30, '0111', 50);
INSERT INTO `cfg_road` VALUES (2733, 27, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (2739, 27, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (2746, 27, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (2801, 28, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2808, 28, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (2814, 28, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (2817, 28, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (2824, 28, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (2830, 28, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (2833, 28, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (2839, 28, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (2846, 28, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (2901, 29, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (2908, 29, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (2914, 29, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (2917, 29, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (2924, 29, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (2930, 29, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (2933, 29, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (2939, 29, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (2946, 29, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (3001, 30, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (3008, 30, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (3014, 30, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (3017, 30, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (3024, 30, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (3030, 30, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (3033, 30, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (3039, 30, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (3046, 30, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (3101, 31, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (3108, 31, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (3114, 31, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (3117, 31, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (3124, 31, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (3130, 31, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (3133, 31, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (3139, 31, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (3146, 31, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (3201, 32, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (3208, 32, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (3214, 32, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (3217, 32, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (3224, 32, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (3230, 32, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (3233, 32, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (3239, 32, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (3246, 32, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (3301, 33, 1, '0110', 40);
INSERT INTO `cfg_road` VALUES (3308, 33, 8, '0110', 40);
INSERT INTO `cfg_road` VALUES (3314, 33, 14, '0110', 40);
INSERT INTO `cfg_road` VALUES (3317, 33, 17, '0110', 50);
INSERT INTO `cfg_road` VALUES (3324, 33, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (3330, 33, 30, '0110', 50);
INSERT INTO `cfg_road` VALUES (3333, 33, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (3339, 33, 39, '0110', 30);
INSERT INTO `cfg_road` VALUES (3346, 33, 46, '0110', 30);
INSERT INTO `cfg_road` VALUES (3401, 34, 1, '1010', 40);
INSERT INTO `cfg_road` VALUES (3402, 34, 2, '1001', 40);
INSERT INTO `cfg_road` VALUES (3403, 34, 3, '1001', 40);
INSERT INTO `cfg_road` VALUES (3404, 34, 4, '1001', 40);
INSERT INTO `cfg_road` VALUES (3405, 34, 5, '1001', 40);
INSERT INTO `cfg_road` VALUES (3406, 34, 6, '1001', 40);
INSERT INTO `cfg_road` VALUES (3407, 34, 7, '1001', 40);
INSERT INTO `cfg_road` VALUES (3408, 34, 8, '1011', 40);
INSERT INTO `cfg_road` VALUES (3409, 34, 9, '1001', 40);
INSERT INTO `cfg_road` VALUES (3410, 34, 10, '1001', 40);
INSERT INTO `cfg_road` VALUES (3411, 34, 11, '1001', 40);
INSERT INTO `cfg_road` VALUES (3412, 34, 12, '1001', 40);
INSERT INTO `cfg_road` VALUES (3413, 34, 13, '1001', 40);
INSERT INTO `cfg_road` VALUES (3414, 34, 14, '1011', 40);
INSERT INTO `cfg_road` VALUES (3415, 34, 15, '0101', 40);
INSERT INTO `cfg_road` VALUES (3417, 34, 17, '1010', 50);
INSERT INTO `cfg_road` VALUES (3418, 34, 18, '1001', 50);
INSERT INTO `cfg_road` VALUES (3419, 34, 19, '1001', 50);
INSERT INTO `cfg_road` VALUES (3420, 34, 20, '1001', 50);
INSERT INTO `cfg_road` VALUES (3421, 34, 21, '1001', 50);
INSERT INTO `cfg_road` VALUES (3422, 34, 22, '1001', 50);
INSERT INTO `cfg_road` VALUES (3423, 34, 23, '1001', 50);
INSERT INTO `cfg_road` VALUES (3424, 34, 24, '1111', 50);
INSERT INTO `cfg_road` VALUES (3425, 34, 25, '1001', 50);
INSERT INTO `cfg_road` VALUES (3426, 34, 26, '1001', 50);
INSERT INTO `cfg_road` VALUES (3427, 34, 27, '1001', 50);
INSERT INTO `cfg_road` VALUES (3428, 34, 28, '1001', 50);
INSERT INTO `cfg_road` VALUES (3429, 34, 29, '1001', 50);
INSERT INTO `cfg_road` VALUES (3430, 34, 30, '0011', 50);
INSERT INTO `cfg_road` VALUES (3433, 34, 33, '1110', 30);
INSERT INTO `cfg_road` VALUES (3434, 34, 34, '1001', 30);
INSERT INTO `cfg_road` VALUES (3435, 34, 35, '1001', 30);
INSERT INTO `cfg_road` VALUES (3436, 34, 36, '1001', 30);
INSERT INTO `cfg_road` VALUES (3437, 34, 37, '1001', 30);
INSERT INTO `cfg_road` VALUES (3438, 34, 38, '1001', 30);
INSERT INTO `cfg_road` VALUES (3439, 34, 39, '1011', 30);
INSERT INTO `cfg_road` VALUES (3440, 34, 40, '1001', 30);
INSERT INTO `cfg_road` VALUES (3441, 34, 41, '1001', 30);
INSERT INTO `cfg_road` VALUES (3442, 34, 42, '1001', 30);
INSERT INTO `cfg_road` VALUES (3443, 34, 43, '1001', 30);
INSERT INTO `cfg_road` VALUES (3444, 34, 44, '1001', 30);
INSERT INTO `cfg_road` VALUES (3445, 34, 45, '1001', 30);
INSERT INTO `cfg_road` VALUES (3446, 34, 46, '0011', 30);
INSERT INTO `cfg_road` VALUES (3515, 35, 15, '0110', 40);
INSERT INTO `cfg_road` VALUES (3524, 35, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (3533, 35, 33, '0110', 30);
INSERT INTO `cfg_road` VALUES (3615, 36, 15, '1010', 10);
INSERT INTO `cfg_road` VALUES (3616, 36, 16, '1001', 10);
INSERT INTO `cfg_road` VALUES (3617, 36, 17, '0101', 10);
INSERT INTO `cfg_road` VALUES (3624, 36, 24, '0110', 50);
INSERT INTO `cfg_road` VALUES (3630, 36, 30, '1100', 10);
INSERT INTO `cfg_road` VALUES (3631, 36, 31, '1001', 10);
INSERT INTO `cfg_road` VALUES (3632, 36, 32, '1001', 10);
INSERT INTO `cfg_road` VALUES (3633, 36, 33, '0011', 10);
INSERT INTO `cfg_road` VALUES (3717, 37, 17, '1110', 10);
INSERT INTO `cfg_road` VALUES (3718, 37, 18, '1001', 10);
INSERT INTO `cfg_road` VALUES (3719, 37, 19, '1001', 10);
INSERT INTO `cfg_road` VALUES (3720, 37, 20, '1001', 10);
INSERT INTO `cfg_road` VALUES (3721, 37, 21, '1001', 10);
INSERT INTO `cfg_road` VALUES (3722, 37, 22, '1001', 10);
INSERT INTO `cfg_road` VALUES (3723, 37, 23, '1001', 10);
INSERT INTO `cfg_road` VALUES (3724, 37, 24, '1111', 10);
INSERT INTO `cfg_road` VALUES (3725, 37, 25, '1001', 10);
INSERT INTO `cfg_road` VALUES (3726, 37, 26, '1001', 10);
INSERT INTO `cfg_road` VALUES (3727, 37, 27, '1001', 10);
INSERT INTO `cfg_road` VALUES (3728, 37, 28, '1001', 10);
INSERT INTO `cfg_road` VALUES (3729, 37, 29, '1001', 10);
INSERT INTO `cfg_road` VALUES (3730, 37, 30, '0111', 10);
INSERT INTO `cfg_road` VALUES (3817, 38, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (3824, 38, 24, '0110', 10);
INSERT INTO `cfg_road` VALUES (3830, 38, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (3917, 39, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (3924, 39, 24, '0110', 10);
INSERT INTO `cfg_road` VALUES (3930, 39, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (4017, 40, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (4024, 40, 24, '0110', 10);
INSERT INTO `cfg_road` VALUES (4030, 40, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (4117, 41, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (4124, 41, 24, '0110', 10);
INSERT INTO `cfg_road` VALUES (4130, 41, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (4217, 42, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (4224, 42, 24, '0110', 10);
INSERT INTO `cfg_road` VALUES (4230, 42, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (4317, 43, 17, '1110', 10);
INSERT INTO `cfg_road` VALUES (4318, 43, 18, '1001', 10);
INSERT INTO `cfg_road` VALUES (4319, 43, 19, '1001', 10);
INSERT INTO `cfg_road` VALUES (4320, 43, 20, '1001', 10);
INSERT INTO `cfg_road` VALUES (4321, 43, 21, '1001', 10);
INSERT INTO `cfg_road` VALUES (4322, 43, 22, '1001', 10);
INSERT INTO `cfg_road` VALUES (4323, 43, 23, '1001', 10);
INSERT INTO `cfg_road` VALUES (4324, 43, 24, '1011', 10);
INSERT INTO `cfg_road` VALUES (4325, 43, 25, '1001', 10);
INSERT INTO `cfg_road` VALUES (4326, 43, 26, '1001', 10);
INSERT INTO `cfg_road` VALUES (4327, 43, 27, '1001', 10);
INSERT INTO `cfg_road` VALUES (4328, 43, 28, '1001', 10);
INSERT INTO `cfg_road` VALUES (4329, 43, 29, '1001', 10);
INSERT INTO `cfg_road` VALUES (4330, 43, 30, '0111', 10);
INSERT INTO `cfg_road` VALUES (4417, 44, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (4430, 44, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (4517, 45, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (4530, 45, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (4617, 46, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (4630, 46, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (4717, 47, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (4730, 47, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (4817, 48, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (4830, 48, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (4917, 49, 17, '0110', 10);
INSERT INTO `cfg_road` VALUES (4930, 49, 30, '0110', 10);
INSERT INTO `cfg_road` VALUES (5017, 50, 17, '1010', 10);
INSERT INTO `cfg_road` VALUES (5018, 50, 18, '1001', 10);
INSERT INTO `cfg_road` VALUES (5019, 50, 19, '1001', 10);
INSERT INTO `cfg_road` VALUES (5020, 50, 20, '1001', 10);
INSERT INTO `cfg_road` VALUES (5021, 50, 21, '1001', 10);
INSERT INTO `cfg_road` VALUES (5022, 50, 22, '1001', 10);
INSERT INTO `cfg_road` VALUES (5023, 50, 23, '1001', 10);
INSERT INTO `cfg_road` VALUES (5024, 50, 24, '1001', 10);
INSERT INTO `cfg_road` VALUES (5025, 50, 25, '1001', 10);
INSERT INTO `cfg_road` VALUES (5026, 50, 26, '1001', 10);
INSERT INTO `cfg_road` VALUES (5027, 50, 27, '1001', 10);
INSERT INTO `cfg_road` VALUES (5028, 50, 28, '1001', 10);
INSERT INTO `cfg_road` VALUES (5029, 50, 29, '1001', 10);
INSERT INTO `cfg_road` VALUES (5030, 50, 30, '0011', 10);
COMMIT;

-- ----------------------------
-- Table structure for cfg_server
-- ----------------------------
DROP TABLE IF EXISTS `cfg_server`;
CREATE TABLE `cfg_server` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `merge_sid` int(11) NOT NULL DEFAULT '0' COMMENT '生效的区服ID',
  `group_id` int(3) NOT NULL COMMENT '服务器群组ID',
  `serial` int(11) DEFAULT NULL COMMENT '排序',
  `name` varchar(50) CHARACTER SET gbk NOT NULL COMMENT '名称',
  `ip` varchar(50) CHARACTER SET gbk NOT NULL COMMENT '服务器ip',
  `port` int(11) NOT NULL COMMENT '服务器端口',
  `extension` varchar(50) CHARACTER SET gbk NOT NULL COMMENT '应用扩展名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `begin_time` datetime DEFAULT NULL COMMENT '开服时间',
  `mt_begin_time` datetime DEFAULT NULL COMMENT '开始维护时间',
  `mt_end_time` datetime DEFAULT NULL COMMENT '结束维护时间',
  `close_time` datetime DEFAULT NULL COMMENT '关闭时间',
  `status` int(11) NOT NULL COMMENT '未开服0，预告中10，运行中20，维护中30，已关闭40',
  `if_recomended` bit(1) DEFAULT NULL COMMENT '是否推荐标志',
  `online_num` int(11) DEFAULT NULL COMMENT '在线人数',
  `conn_string` varchar(255) CHARACTER SET gbk DEFAULT NULL,
  `memo` varchar(1000) CHARACTER SET gbk DEFAULT NULL,
  `statement` int(4) NOT NULL DEFAULT '20' COMMENT '10新服、20流畅、30饱和',
  `row_update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='区服列表';

-- ----------------------------
-- Records of cfg_server
-- ----------------------------
BEGIN;
INSERT INTO `cfg_server` VALUES (98, 98, 20, 1404111800, '98区', '127.0.0.1', 8098, 'godLogic', '2019-03-31 00:00:00', '2019-03-31 00:00:00', '2019-03-31 00:00:00', NULL, NULL, -1, NULL, 176, 'jdbc:mysql://127.0.0.1:3306/godserver_98?user=root&password=123456&allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false', '', 20, '2019-03-31 16:58:53');
INSERT INTO `cfg_server` VALUES (99, 99, 20, 1404111800, '99区', '127.0.0.1', 8088, 'nas1', '2019-03-31 00:00:00', '2019-03-31 00:00:00', '2019-03-31 00:00:00', NULL, NULL, -1, NULL, 176, 'jdbc:mysql://127.0.0.1:3306/godserver_99?user=root&password=123456&allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false', '', 20, '2019-03-31 16:58:53');
COMMIT;

-- ----------------------------
-- Table structure for cfg_server_group
-- ----------------------------
DROP TABLE IF EXISTS `cfg_server_group`;
CREATE TABLE `cfg_server_group` (
  `id` int(11) NOT NULL COMMENT '群组ID',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `seq` int(1) NOT NULL DEFAULT '0' COMMENT '排序号',
  `app_product_id` int(11) NOT NULL DEFAULT '1' COMMENT 'app产品组ID',
  `wechat_product_id` int(11) NOT NULL DEFAULT '2' COMMENT '微信产品组ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='服务器群组';

-- ----------------------------
-- Records of cfg_server_group
-- ----------------------------
BEGIN;
INSERT INTO `cfg_server_group` VALUES (10, '苹果(自营)', 20, 1, 2);
INSERT INTO `cfg_server_group` VALUES (16, '买量(自营)', 10, 1, 2);
INSERT INTO `cfg_server_group` VALUES (20, '安卓(自营)', 1, 1, 2);
INSERT INTO `cfg_server_group` VALUES (100, '龙游专服', 30, 1, 2);
INSERT INTO `cfg_server_group` VALUES (110, '猫耳专服', 40, 1, 2);
COMMIT;

-- ----------------------------
-- Table structure for cfg_special
-- ----------------------------
DROP TABLE IF EXISTS `cfg_special`;
CREATE TABLE `cfg_special` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `type` int(11) NOT NULL DEFAULT '10' COMMENT '特产类型：10普通；20高级；30顶级',
  `price` int(11) unsigned NOT NULL COMMENT '买进的价格',
  `min_price` int(11) unsigned NOT NULL,
  `max_price` int(11) unsigned DEFAULT NULL,
  `country` int(2) unsigned NOT NULL,
  `comment` varchar(200) NOT NULL,
  `selling_cities` varchar(100) NOT NULL COMMENT '特产出售城池',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COMMENT='特产';

-- ----------------------------
-- Records of cfg_special
-- ----------------------------
BEGIN;
INSERT INTO `cfg_special` VALUES (1, '盐', 10, 500, 2200, 3050, 20, '纯正海盐，一级过滤，吃起来口感绵密又不会太咸，据说能防抗顽疾，每天吃一点还长个子呢。', '120,1317,919,928,1519,430,617,118,230,1528,1324,922');
INSERT INTO `cfg_special` VALUES (2, '面', 10, 600, 2500, 3500, 10, '姜子牙亲自推销，马夫人亲手磨制，入口即化，吃出家乡的味道。切不可生吃哦。', '5019,3817,5022,4030,3719,4321,3725,4117,5028,4318,4630');
INSERT INTO `cfg_special` VALUES (3, '米', 10, 1000, 3100, 4000, 40, '出产时个个饱满，煮熟后粒粒香醇，满足您健康与味觉的需求。吃什么不如吃健康，送什么不如送大米。', '3406,3214,2201,2014,1902,1910,3201,3410,2901,2610,2908');
INSERT INTO `cfg_special` VALUES (4, '亚麻', 10, 1500, 3800, 4550, 30, '这就是传说中最结实的布料，怎么撕怎么不烂，怎么穿怎么舒爽，平民消费，贵族享受，经久耐磨，经济实用，做衣服一件能顶两件穿。', '2433,3442,1934,2646,3434,1937,3039,2046,2636,3033');
INSERT INTO `cfg_special` VALUES (5, '草药', 10, 1750, 4250, 4900, 40, '神农氏最爱吃的健康食品。无论是您觉得体虚、犯困，还是易疲劳，不管是内伤外伤，还是跌打损伤，一抹见效，一吃即好。', '2308,2501,2608,2414,1906,2714,3406,3214,2201,2014,1902,1910');
INSERT INTO `cfg_special` VALUES (6, '鱼', 10, 2000, 4700, 5000, 20, '纯正野生深海鱼，去头去内脏就能吃，鲜香嫩滑，吃起来感觉就像畅游在广阔的海洋，大海的神秘尽收心底。现已加入豪华午餐。', '1024,126,1017,730,417,1528,1324,922,120,1317,919,928,1519');
INSERT INTO `cfg_special` VALUES (7, '石料', 10, 2100, 5000, 5500, 50, '这可不是一般的建筑材料，采自灵山的圣石，建房子特好用。饥荒的时候还能扒一块来吃，嘎嘣脆，鸡肉味。', '3418,2721,2430,1918,3130,1922,2924,2517,3427,3425,2817,1926');
INSERT INTO `cfg_special` VALUES (8, '鲜肉', 10, 3000, 6100, 6500, 30, '上好野生山猪肉，肉质鲜美，滑而不腻，嫩而不烂，吃了它就是吃健康，吃了它就是吃幸福，吃了它就能感觉大地的丰美，吃了它就能听见远山的呼唤。', '2539,3246,2346,3437,2733,2039,1942,2433,3442,1934,2646,3434');
INSERT INTO `cfg_special` VALUES (9, '猫', 10, 3300, 6600, 7000, 10, '传说中喵星人塑像，西岐地区很流行的装饰品。喵星人是来自遥远宇宙另一端的神秘物种，他们的语言就是“喵”，很好听吧？', '3722,4617,5025,3728,4328,4325,5019,3817,5022,4030,3719,4321,3725');
INSERT INTO `cfg_special` VALUES (10, '紫色陶瓶', 10, 4000, 7500, 8400, 50, '此款花瓶做工精细、造型优雅，而且上了罕见紫色釉，实数良品，是中国古代劳动人民智慧的结晶。仅卖8000，你值得拥有。', '3130,1922,2924,2517,3427,3425,2817,1926,3224,2217,2725,2030,2830');
INSERT INTO `cfg_special` VALUES (11, '龟甲', 20, 13000, 9000, 17000, 10, '千年龟甲，细心雕琢，上面刻有神奇的雕文，有时还会发光，放着可以辟邪，用了可以占卜命运。你问我怎么用？我只是一个卖东西的人。', '5025,3728,4328,4325,5019,3817,5022,4030,3719,4321,3725,5028,4318,4630');
INSERT INTO `cfg_special` VALUES (12, '肚兜', 20, 12500, 10000, 14000, 20, '封神大陆最时尚的女性内衣，不失古典美，性感撩人的曲线、傲人的身材就是这么穿出来的。许多明星都喜欢偷穿，比如哪咤。', '730,417,1528,1324,922,120,1317,919,1519,617,230');
INSERT INTO `cfg_special` VALUES (13, '瓷器', 20, 16000, 11000, 15500, 30, '这虽然看上去就是一款瓷碗，但它绝对不是一块简单的瓷碗，这可是是鱼人最喜欢的款式，整天带在身上，除了盛饭，还能砸人。', '3246,3437,2733,2039,1942,2433,3442,1934,2646,3434,1937,3039,2046,2636,3033');
INSERT INTO `cfg_special` VALUES (14, '玉如意', 20, 15500, 13000, 17000, 40, '此款产品雕工精美，质地圆润。当您有什么不顺心的事，请喊“如意如意顺我心意，快快显灵”。据说有意想不到的事情发生。', '2308,2608,2414,1906,2714,3406,3214,2201,2014,1910,3410,2901,2610,2908');
INSERT INTO `cfg_special` VALUES (15, '宫灯', 20, 15000, 15000, 19500, 50, '据说是摘星台里今年最流行的宫灯款式，造型简洁，既美观，又实用，有种超越了这个时代的艺术感。', '3418,2721,2430,1918,3130,1922,2924,2517,3427,3425,2817,1926,3224,2217,2725,2030,2830');
INSERT INTO `cfg_special` VALUES (16, '青铜器', 20, 14000, 17000, 20000, 20, '造型别致，实用性强的高端青铜产品，您可以拿来煲汤，煮饭，炒菜，关键还不粘底。限时抢购中，前五十名预定的客人，还送高级厨具一套。', '1024,126,1017,730,1528,1324,922,1317,919,928,1519,430,617,118');
INSERT INTO `cfg_special` VALUES (17, '麝香', 20, 13500, 19000, 24000, 40, '优质麝香香囊，造型雅致，香的香味浓郁，经久不散，闻起来清新健肺去烦躁，连你家宠物会喜欢，除了麝。', '2308,2501,2414,1906,2714,3406,3214,2201,1902,3201,3410,2610,2908');
INSERT INTO `cfg_special` VALUES (18, '狐裘', 20, 14500, 21000, 26000, 30, '新款妲己款狐裘，据说是妲己娘娘最爱穿的时尚服饰，她穿起来有种浑然一体的感觉，更离奇的三伏天也能穿。', '2346,2733,1942,3442,1934,2646,3434,1937,3039,2636,3033');
INSERT INTO `cfg_special` VALUES (19, '丝绸', 20, 12000, 23000, 29000, 10, '殷商最流行的紫色丝绸布料，上面穿有银丝线，制成衣，穿起来光滑、贴身、柔软还有弹性，尽显高贵身份。', '3722,4617,5019,3817,4321,3725,4117,5028,4318');
INSERT INTO `cfg_special` VALUES (20, '纣王扳指', 20, 20000, 15000, NULL, 50, '上乘田黄石经过大师加工的优质扳指，据说纣王用过它，上面还留有帝王之气，戴上它您无论身材都会像老大。', '3418,2721,2430,1918,3130,1922,2924,2517,3427,3425,2817,1926,3224,2217,2725,2030,2830');
INSERT INTO `cfg_special` VALUES (21, '水晶', 20, 16500, 27000, 35000, 30, '鱼人的眼泪经过一段时间的风化，形成的水晶，晶莹剔透，蕴含着水的力量，据说还代表着人与人之间纯洁的爱情。', '2539,3246,2346,3437,2039,2433,1934,3434,2046,2636,3033');
INSERT INTO `cfg_special` VALUES (22, '鹿角', 20, 17000, 30000, 38000, 20, '深山五色灵鹿，集仙灵之气凝结而成的鹿角，东鲁国几代领主权力的象征。现在流落民间只有我这儿有，客人，您真是好运啊。', '1024,126,1017,730,417,1528,1324,922,120,1317,919,928,1519,430,118,230');
INSERT INTO `cfg_special` VALUES (23, '玛瑙', 20, 17500, 34000, 40000, 10, '采自昆仑山沾满仙气的玛瑙石，表面有纯天然仙女纹路，开光后还有幸运加持！只要40000！一点都不贵！', '3722,4617,5025,3728,4328,3817,5022,4030,3719,4321,3725,4117,5028,4630');
INSERT INTO `cfg_special` VALUES (24, '翡翠', 20, 18000, 40000, 52000, 40, '南鄂国贵族专用的翡翠，拜火教高级人士的象征。买回去挂家里，祛邪保平安，买回去带身上，显得您就是那么的高端大气上档次。', '2308,2501,2608,2414,1906,2714,3406,2201,2014,1902,1910,3201,3410,2901');
INSERT INTO `cfg_special` VALUES (25, '真·纣王扳指', 20, 30000, 70000, NULL, 50, '坊间流传的纣王扳指都是假货，只有这个才是真的，上面还能闻到纣王的体香。纣王本人亲自代言，您看这是他的亲笔认证证书。', '3418,2721,3130,1922,3224,2217,2725,2030');
INSERT INTO `cfg_special` VALUES (31, '海珀', 30, 26000, 28000, 51000, 20, '在那山的那边海的那边有一群水精灵，她们活泼又聪明，但哭起来很要命。传说海珀是她们眼珠所化，有懂行的人会高价收购，谁也不知道这些东西的神秘用途。', '1024,126,1017,730,417,1528,1324,922,120,1317,919,430,617');
INSERT INTO `cfg_special` VALUES (32, '龙珠', 30, 54000, 160000, NULL, 20, '传说集齐7个，就能召唤真龙，然后，它就消失了……所以还不如卖钱划算。', '1024,126,1017,417,928,1519,118,230');
INSERT INTO `cfg_special` VALUES (33, '金钱卦', 30, 28000, 10000, 38000, 10, '看似普普通通的金钱，懂行的人却能拿它来卜算吉凶。所以呢只要吉人天相，一定能够卖个好价钱。', '3722,4617,5025,3728,4328,4325,5019,3817,5022,4030,3719,4318,4630');
INSERT INTO `cfg_special` VALUES (34, '金鼎', 30, 50000, 92000, 110000, 10, '没有强大的经济实力，非凡的铸造工艺，谁能把这种高大上的货色做得如此金灿灿，亮瞎你的眼。官邸中摆上一个，宠姬们再也不愁没镜子照了。', '3722,4617,5025,4325,4321,3725,4117,5028');
INSERT INTO `cfg_special` VALUES (35, '火晶', 30, 28000, 48000, 56000, 40, '火候必须刚刚好，才能炼出这么璀璨的红色珍宝。南方贵族特别喜欢这种宝贝，给它们起了个十分大气的名字：凤凰之粪便。', '2308,2501,2608,2414,1906,2714,3406,3214,2201,2014,2901,2610,2908');
INSERT INTO `cfg_special` VALUES (36, '黄钟', 30, 38000, 68000, 81000, 40, '发家致富的人，要不再玩点高雅艺术，很容易被人看作土豪。只有听得懂黄钟大吕，你才是真正的贵族世胄。', '2501,2608,1906,2714,1902,1910,3201,3410');
INSERT INTO `cfg_special` VALUES (37, '香料', 30, 30000, 60000, 72000, 30, '你是闻香而来吧。有钱也买不到的进口产品，随便撒上点，白面都能吃出炸鸡味。', '2539,3246,2346,3437,2733,2039,1942,2433,3442,1934,2046,2636,3033');
INSERT INTO `cfg_special` VALUES (38, '神仙酒', 30, 32000, 70000, 80000, 30, '千年传承，百年老窑，皇家特供，一口醉倒！神仙曰：喝过都说好！', '2539,3246,2733,1942,2646,3434,1937,3039');
INSERT INTO `cfg_special` VALUES (39, '御贡神丹', 30, 26000, 50000, 54000, 50, '贡品中的至尊，不用广告，直接看疗效。至于效果怎么样，看看妲己多滋润就知道了。', '2430,1918,2924,2517,3427,3425,2817,1926,3224,2217,2725,2030,2830');
INSERT INTO `cfg_special` VALUES (40, '纣王戈', 30, 60000, 88888, NULL, 50, '据说纣王陵还没修完，就有盗墓贼把里面的纣王自作用戈给挖出来的。你问咋见到那边多把，因为很多假的呗，你可要看好，这把才是真的，不信看看定价。', '3418,2721,3130,1922,3224,2217,2725,2030');
COMMIT;

-- ----------------------------
-- Table structure for cfg_task
-- ----------------------------
DROP TABLE IF EXISTS `cfg_task`;
CREATE TABLE `cfg_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` int(11) unsigned NOT NULL,
  `value` int(11) DEFAULT NULL,
  `award` varchar(200) DEFAULT NULL,
  `is_valid` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10131 DEFAULT CHARSET=utf8mb4 COMMENT='任务';

-- ----------------------------
-- Records of cfg_task
-- ----------------------------
BEGIN;
INSERT INTO `cfg_task` VALUES (10, '完成新手引导', 10, 1, '[{\"num\":50,\"item\":10},{\"num\":20000,\"item\":20}]', b'1');
INSERT INTO `cfg_task` VALUES (20, '在【城市交易】中卖掉1件特产并赚钱', 10, 1, '[{\"num\":10000,\"item\":20}]', b'1');
INSERT INTO `cfg_task` VALUES (30, '攻下2座一级城', 10, 2, '[{\"num\":3,\"item\":50}]', b'1');
INSERT INTO `cfg_task` VALUES (40, '在【神将】，【城内-炼丹房】或【鹿台】将任意1张卡牌升1级', 10, 1, '[{\"num\":20000,\"item\":20}]', b'1');
INSERT INTO `cfg_task` VALUES (50, '打赢3场野怪', 10, 3, '[{\"num\":20,\"item\":10}]', b'1');
INSERT INTO `cfg_task` VALUES (60, '在菜单【神将】中编组10张卡牌', 10, 1, '[{\"num\":20000,\"item\":20}]', b'1');
INSERT INTO `cfg_task` VALUES (70, '在【城市】【交易】中卖掉10件特产', 10, 10, '[{\"num\":30000,\"item\":20}]', b'1');
INSERT INTO `cfg_task` VALUES (80, '通过菜单【设置】->【好友】添加一个好友', 10, 1, '[{\"num\":20,\"item\":10}]', b'1');
INSERT INTO `cfg_task` VALUES (90, '帮好友打一次怪', 10, 1, '[{\"num\":1,\"awardId\":10,\"item\":50},{\"num\":1,\"awardId\":20,\"item\":50},{\"num\":1,\"awardId\":30,\"item\":50},{\"num\":1,\"awardId\":40,\"item\":50},{\"num\":1,\"awardId\":50,\"item\":50}]', b'1');
INSERT INTO `cfg_task` VALUES (100, '在【神将】【城内】【炼丹房】或【鹿台】将任意3张卡牌升到3级', 10, 3, '[{\"num\":20000,\"item\":20}]', b'1');
INSERT INTO `cfg_task` VALUES (110, '打下一座二级城', 10, 1, '[{\"num\":1,\"star\":3,\"item\":40},{\"num\":188,\"item\":10}]', b'1');
INSERT INTO `cfg_task` VALUES (210, '去【商城】任意开一个【卡包】', 20, 1, '[{\"num\":1,\"awardId\":90,\"item\":50}]', b'1');
INSERT INTO `cfg_task` VALUES (220, '通过【法宝】使用【定风珠】', 20, 1, '[{\"num\":1,\"awardId\":160,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (230, '在【法宝】中开启【漫步靴】并通过1个路口', 20, 1, '[{\"num\":1,\"awardId\":160,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (240, '通过【城池】【交易】特产盈利12万', 20, 120000, '[{\"num\":1,\"awardId\":80,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (250, '到【城内】将一座【炼宝炉】升1级', 20, 1, '[{\"num\":1,\"awardId\":340,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (260, '在战斗中使用【绝仙剑】', 20, 1, '[{\"num\":1,\"awardId\":340,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (270, '赢得3场练兵', 20, 3, '[{\"num\":1,\"awardId\":250,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (280, '在【封神台】中取得3场胜利', 20, 3, '[{\"num\":1,\"awardId\":205,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (290, '打下3座二级城', 20, 3, '[{\"num\":1,\"awardId\":6,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (300, '打下1座三级城', 20, 1, '[{\"num\":1,\"star\":4,\"item\":40}]', b'1');
INSERT INTO `cfg_task` VALUES (1100, '累计攻下%d座城池', 50, NULL, NULL, b'1');
INSERT INTO `cfg_task` VALUES (1200, '累计%d座城城内所有建筑升到5级', 50, NULL, NULL, b'1');
INSERT INTO `cfg_task` VALUES (1300, '累计收集%d张卡牌', 50, NULL, NULL, b'1');
INSERT INTO `cfg_task` VALUES (10010, '累计铜钱收益lv*2万', 30, NULL, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10011, '在城市交易中卖掉max(20,lv/5*4)个特产', 30, NULL, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10012, '到游商馆买入20件特产', 30, 20, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10013, '到黑市购买5件法宝', 30, 5, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10014, '到商城购买一次物品', 30, 1, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10015, '到福地收获60元宝', 30, 60, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10016, '在路边捡到1个百宝箱', 30, 1, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10017, '经过1次界碑', 30, 1, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10018, '聚贤庄购买**张卡牌(20级前3次，40级前8次,40级后15)', 30, NULL, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10019, '炼丹房收取**份经验(20级前3次，40级前8次,40级后15)', 30, NULL, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10020, '矿场收取**个元素(20级前5次，40级前16次,40级后60)', 30, NULL, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10021, '炼丹炉收取一个法宝或万能灵石', 30, 1, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10022, '魔王降临时打魔王超过10次', 30, 10, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10023, '练兵胜利**次(20级前3次，20级后5次)', 30, NULL, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10024, '打野怪胜利**次(20级前5次，20级后8次)', 30, NULL, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10025, '帮好友打赢3个怪', 30, 3, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10026, '在封神台取得3场胜利', 30, 3, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10027, '到文王庙求签1次', 30, 1, '[{\"num\"600,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10028, '到女娲庙捐赠1次', 30, 1, '[{\"num\"600,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10029, '到迷仙洞探险1次', 30, 1, '[{\"num\"600,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10030, '到太一府进贡特产1次', 30, 1, '[{\"num\"600,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10031, '到鹿台升级卡牌1次', 30, 1, '[{\"num\"600,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10032, '消耗max(1,lv/10)*10个元素升级卡牌', 30, NULL, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10033, '使用max(1,lv/10)次地图法宝', 30, NULL, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10034, '在战斗中使用战斗法宝且获胜', 30, 1, '[{\"num\"800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10035, '钱庄领取**份税收(20级前3次，40级前8次，,40级后15)', 30, NULL, '[{\"num\"400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10036, '挑战一次诛仙阵', 30, 1, '[{\"num\"800,\"item\":80}]', b'0');
INSERT INTO `cfg_task` VALUES (10110, '完成1个每日任务', 40, 1, NULL, b'1');
INSERT INTO `cfg_task` VALUES (10120, '完成3个每日任务', 40, 3, NULL, b'1');
INSERT INTO `cfg_task` VALUES (10130, '完成5个每日任务', 40, 5, NULL, b'1');
COMMIT;

-- ----------------------------
-- Table structure for cfg_treasure
-- ----------------------------
DROP TABLE IF EXISTS `cfg_treasure`;
CREATE TABLE `cfg_treasure` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `type` int(11) NOT NULL,
  `star` int(11) DEFAULT NULL,
  `black_price` int(11) DEFAULT NULL,
  `black_unit` int(11) DEFAULT NULL,
  `can_product` bit(1) NOT NULL,
  `memo` varchar(100) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10041 DEFAULT CHARSET=utf8mb4 COMMENT='法宝\ntype：10地图法宝 20战斗法宝 30卡包/宝箱 40灵石 50不在包裹显示的道具';

-- ----------------------------
-- Records of cfg_treasure
-- ----------------------------
BEGIN;
INSERT INTO `cfg_treasure` VALUES (10, '财神珠', 10, 5, NULL, NULL, b'1', '接下来200步内卖出特产溢价100%。');
INSERT INTO `cfg_treasure` VALUES (20, '乾坤图', 10, 5, NULL, NULL, b'0', '直接在可见范围中属于自己的城池多建一套建筑。');
INSERT INTO `cfg_treasure` VALUES (30, '落宝金钱', 10, 5, NULL, NULL, b'1', '接下来三场战斗，胜利则掉落随机法宝。');
INSERT INTO `cfg_treasure` VALUES (50, '捆仙绳', 10, 4, NULL, NULL, b'0', '免费绑走客栈或聚贤庄内出现的卡牌。');
INSERT INTO `cfg_treasure` VALUES (60, '山河社稷图', 10, 4, NULL, NULL, b'1', '到全地图任意地点。不能到有悬浮物的地点。');
INSERT INTO `cfg_treasure` VALUES (70, '五彩石', 10, 4, NULL, NULL, b'0', '打怪等待时间清零。');
INSERT INTO `cfg_treasure` VALUES (80, '玉麒麟', 10, 4, NULL, NULL, b'0', '加10个行动点。');
INSERT INTO `cfg_treasure` VALUES (90, '定风珠', 10, 3, 18000, 2, b'1', '原地停留一回合。');
INSERT INTO `cfg_treasure` VALUES (110, '风火轮', 10, 3, 21000, 2, b'1', '到地图可见范围内的任意地点。不能到有悬浮物的地点。');
INSERT INTO `cfg_treasure` VALUES (120, '七香车', 10, 3, 21000, 2, b'1', '可控制下一次行动行走步数。');
INSERT INTO `cfg_treasure` VALUES (130, '青鸾', 10, 2, 11000, 2, b'1', '接下来10次行动可用三个骰子。');
INSERT INTO `cfg_treasure` VALUES (150, '四不像', 10, 2, 4000, 2, b'1', '接下来10次行动可用两个骰子。');
INSERT INTO `cfg_treasure` VALUES (160, '漫步靴', 10, 1, 3200, 2, b'1', '下一个路口可选择行走方向。');
INSERT INTO `cfg_treasure` VALUES (170, '醒酒毡', 10, 1, 3000, 2, b'1', '重新战斗。');
INSERT INTO `cfg_treasure` VALUES (180, '送神符', 10, 1, 2000, 2, b'1', '送走附在身上的各类神仙。');
INSERT INTO `cfg_treasure` VALUES (190, '回马枪', 10, 1, 2000, 2, b'1', '改变行进方向。');
INSERT INTO `cfg_treasure` VALUES (205, '杏黄旗', 10, 1, 3000, 2, b'1', '增加竞技场挑战次数1次。');
INSERT INTO `cfg_treasure` VALUES (210, '番天印', 20, 5, NULL, NULL, b'0', '直接选定敌方一张卡牌送入坟场，无视其金刚效果。');
INSERT INTO `cfg_treasure` VALUES (220, '打神鞭', 20, 5, NULL, NULL, b'1', '封杀敌方所有卡牌所有特技一回合。');
INSERT INTO `cfg_treasure` VALUES (230, '阴阳镜', 20, 5, NULL, NULL, b'1', '拉己方一张坟场中的卡牌回战场。');
INSERT INTO `cfg_treasure` VALUES (240, '太极图', 20, 5, NULL, NULL, b'0', '控制敌方一张卡牌一回合。');
INSERT INTO `cfg_treasure` VALUES (250, '诛仙剑', 20, 5, NULL, NULL, b'1', '装备卡牌攻防+500。');
INSERT INTO `cfg_treasure` VALUES (260, '九龙神火罩', 20, 4, NULL, NULL, b'1', '封杀敌方一张卡牌，使其不能攻击也不能阻挡。');
INSERT INTO `cfg_treasure` VALUES (270, '莫邪宝剑', 20, 4, NULL, NULL, b'0', '装备卡牌攻防+380。');
INSERT INTO `cfg_treasure` VALUES (280, '招魂幡', 20, 4, NULL, NULL, b'1', '每回合召唤一鬼兵进场。');
INSERT INTO `cfg_treasure` VALUES (290, '戮仙剑', 20, 4, NULL, NULL, b'1', '装备卡牌攻击+500且获得特技嗜血。');
INSERT INTO `cfg_treasure` VALUES (300, '攒心钉', 20, 3, 20000, 2, b'1', '每回合狙击敌方生命值最低者，使其损失250血，直到战斗结束。');
INSERT INTO `cfg_treasure` VALUES (310, '乾坤尺', 20, 3, NULL, NULL, b'1', '装备卡牌攻击+400且获得特技钻地。');
INSERT INTO `cfg_treasure` VALUES (320, '紫金钵盂', 20, 3, NULL, NULL, b'1', '使用回合己方所有卡牌均可上云台。');
INSERT INTO `cfg_treasure` VALUES (330, '紫绫仙衣', 20, 3, 15000, 2, b'1', '己方所有卡牌防守+100。');
INSERT INTO `cfg_treasure` VALUES (340, '绝仙剑', 20, 3, NULL, NULL, b'1', '装备卡牌攻防各+250且获得特技穿刺。');
INSERT INTO `cfg_treasure` VALUES (350, '乾坤弓', 20, 2, 13000, 2, b'1', '给敌方血槽造成450~800点伤害，一次战斗限三次。');
INSERT INTO `cfg_treasure` VALUES (360, '震天箭', 20, 2, 12000, 2, b'1', '给敌方血槽造成500点伤害，一次战斗限三次。');
INSERT INTO `cfg_treasure` VALUES (370, '红葫芦', 20, 1, 9000, 2, b'1', '补己方召唤师600点血，一次战斗限五次。');
INSERT INTO `cfg_treasure` VALUES (380, '金葫芦', 20, 2, 12000, 2, b'1', '使用回合己方所有卡牌不会陷入异常状态。');
INSERT INTO `cfg_treasure` VALUES (390, '陷仙剑', 20, 2, NULL, NULL, b'1', '装备卡牌攻防各+200。');
INSERT INTO `cfg_treasure` VALUES (400, '混元金斗', 20, 1, NULL, NULL, b'1', '当前回合营地所有卡牌召唤成本-1，一场战斗限使用3次。');
INSERT INTO `cfg_treasure` VALUES (410, '青云剑', 20, 1, 5000, 2, b'1', '装备卡牌攻防各+100。');
INSERT INTO `cfg_treasure` VALUES (420, '定神丹', 20, 2, 9000, 2, b'1', '使用回合己方所有卡牌不会受威风、斥退、妖术的影响。');
INSERT INTO `cfg_treasure` VALUES (430, '扫霞衣', 20, 4, NULL, NULL, b'1', '装备卡牌防守+500。');
INSERT INTO `cfg_treasure` VALUES (440, '五火神焰扇', 20, 4, NULL, NULL, b'1', '给对方全体卡牌150~500点业火伤害（无视金刚和云台位）。');
INSERT INTO `cfg_treasure` VALUES (450, '柴胡草', 20, 2, NULL, NULL, b'1', '去掉一张卡牌所受的永久伤害及异常状态。');
INSERT INTO `cfg_treasure` VALUES (460, '如意乾坤袋', 20, 1, NULL, NULL, b'1', '可将当前营地中的卡牌全部换掉。');
INSERT INTO `cfg_treasure` VALUES (470, '太极符印', 20, 2, NULL, NULL, b'1', '装备后卡牌在战斗中获得无相技能。');
INSERT INTO `cfg_treasure` VALUES (480, '火龙标', 20, 2, NULL, NULL, b'1', '给予对方一张卡牌150点血的永久伤害。一次战斗限使用3次。');
INSERT INTO `cfg_treasure` VALUES (490, '百毒痘', 20, 3, NULL, NULL, b'1', '使对方一张卡牌中毒，每回合造成100点血的永久伤害。');
INSERT INTO `cfg_treasure` VALUES (510, '宝箱', 30, NULL, NULL, NULL, b'0', '可随机获得一件物品。');
INSERT INTO `cfg_treasure` VALUES (520, '百宝箱', 30, NULL, NULL, NULL, b'0', '可随机获得多件物品。');
INSERT INTO `cfg_treasure` VALUES (530, '宝袋', 30, NULL, NULL, NULL, b'0', '');
INSERT INTO `cfg_treasure` VALUES (540, '5万铜钱袋', 30, NULL, NULL, NULL, b'0', '');
INSERT INTO `cfg_treasure` VALUES (550, '10元素袋', 30, NULL, NULL, NULL, b'0', '');
INSERT INTO `cfg_treasure` VALUES (560, '体力包', 30, NULL, NULL, NULL, b'0', '');
INSERT INTO `cfg_treasure` VALUES (570, '进阶宝袋', 30, NULL, NULL, NULL, b'0', '');
INSERT INTO `cfg_treasure` VALUES (610, '金卡包', 30, NULL, NULL, NULL, b'0', '可随机获得1张金属性三星或以上卡牌，有机会获得特殊卡牌杨戬和韦护。');
INSERT INTO `cfg_treasure` VALUES (620, '木卡包', 30, NULL, NULL, NULL, b'0', '可随机获得1张木属性三星或以上卡牌，有机会获得特殊卡牌句芒和申公豹。');
INSERT INTO `cfg_treasure` VALUES (630, '水卡包', 30, NULL, NULL, NULL, b'0', '可随机获得1张水属性三星或以上卡牌，有机会获得特殊卡牌妲己和崇黑虎。');
INSERT INTO `cfg_treasure` VALUES (640, '火卡包', 30, NULL, NULL, NULL, b'0', '可随机获得1张火属性三星或以上卡牌，有机会获得特殊卡牌火灵圣母和王天君。');
INSERT INTO `cfg_treasure` VALUES (650, '土卡包', 30, NULL, NULL, NULL, b'0', '可随机获得1张土属性三星或以上卡牌，有机会获得特殊卡牌闻仲和殷郊。');
INSERT INTO `cfg_treasure` VALUES (700, '限时礼包', 30, NULL, NULL, NULL, b'0', '可随机获得1张全属性三星以上卡牌或三星以上灵石，有较高概率获得新卡牌。');
INSERT INTO `cfg_treasure` VALUES (710, '限时礼包x10', 30, NULL, NULL, NULL, b'0', '可随机获得全属性三星以上卡牌或三星以上灵石，有较高概率获得新卡牌。');
INSERT INTO `cfg_treasure` VALUES (810, '一星灵石', 40, NULL, NULL, NULL, b'1', '可代替任何一星卡牌灵石用来进阶卡牌，也可用来升级卡牌。');
INSERT INTO `cfg_treasure` VALUES (820, '二星灵石', 40, NULL, NULL, NULL, b'1', '可代替任何二星卡牌灵石用来进阶卡牌。');
INSERT INTO `cfg_treasure` VALUES (830, '三星灵石', 40, NULL, NULL, NULL, b'1', '可代替任何三星卡牌灵石用来进阶卡牌。');
INSERT INTO `cfg_treasure` VALUES (840, '四星灵石', 40, NULL, NULL, NULL, b'1', '可代替任何四星卡牌灵石用来进阶卡牌。');
INSERT INTO `cfg_treasure` VALUES (850, '五星灵石', 40, NULL, NULL, NULL, b'1', '可代替任何五星卡牌灵石用来进阶卡牌。');
INSERT INTO `cfg_treasure` VALUES (910, '金卡包x10', 30, NULL, NULL, NULL, b'0', '可随机获得金属性三星以上卡牌，有机会获得特殊卡牌杨戬和韦护。');
INSERT INTO `cfg_treasure` VALUES (920, '木卡包x10', 30, NULL, NULL, NULL, b'0', '可随机获得木属性三星以上卡牌，有机会获得特殊卡牌句芒和申公豹。');
INSERT INTO `cfg_treasure` VALUES (930, '水卡包x10', 30, NULL, NULL, NULL, b'0', '可随机获得水属性三星以上卡牌，有机会获得特殊卡牌妲己和崇黑虎。');
INSERT INTO `cfg_treasure` VALUES (940, '火卡包x10', 30, NULL, NULL, NULL, b'0', '可随机获得火属性三星以上卡牌，有机会获得特殊卡牌火灵圣母和王天君。');
INSERT INTO `cfg_treasure` VALUES (950, '土卡包x10', 30, NULL, NULL, NULL, b'0', '可随机获得土属性三星以上卡牌，有机会获得特殊卡牌闻仲和殷郊。');
INSERT INTO `cfg_treasure` VALUES (10010, '聚仙旗', 50, NULL, NULL, NULL, b'0', '');
INSERT INTO `cfg_treasure` VALUES (10020, '神沙', 50, NULL, NULL, NULL, b'0', '');
INSERT INTO `cfg_treasure` VALUES (10030, '混沌仙石', 50, NULL, NULL, NULL, b'0', '');
INSERT INTO `cfg_treasure` VALUES (10040, '诛仙令', 20, 3, NULL, NULL, b'1', '');
COMMIT;

-- ----------------------------
-- Table structure for cfg_yd_event
-- ----------------------------
DROP TABLE IF EXISTS `cfg_yd_event`;
CREATE TABLE `cfg_yd_event` (
  `id` int(11) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '事件名称',
  `type` int(11) NOT NULL DEFAULT '10' COMMENT '正面10、0中性、-10负面',
  `probability` int(11) NOT NULL COMMENT '野地事件概率',
  `memo` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随机事件概率';

-- ----------------------------
-- Records of cfg_yd_event
-- ----------------------------
BEGIN;
INSERT INTO `cfg_yd_event` VALUES (1, '无事，打个盹', 0, 27, NULL);
INSERT INTO `cfg_yd_event` VALUES (2, '鉴于您深得民心，【XX】的富户决定再次向您上缴**份基本税收（24000~18万）', 10, 3, '5～8份');
INSERT INTO `cfg_yd_event` VALUES (3, '【XX】人性化的管理制度初见成效，矿场多生产**份【XX元素】进献', 10, 5, '1份');
INSERT INTO `cfg_yd_event` VALUES (4, '路边有只小动物很可怜的样子，正要抱走收养，却发现它变成传说中大伙梦寐以求的【四不像】', 10, 4, NULL);
INSERT INTO `cfg_yd_event` VALUES (5, '运气来了挡也挡不住啊，用弹弓打鸟竟然捕获了临阵逃跑的至宝：【青鸾】', 10, 3, NULL);
INSERT INTO `cfg_yd_event` VALUES (6, '低头走路有惊喜，捡到**铜钱', 10, 7, 'lv*500');
INSERT INTO `cfg_yd_event` VALUES (7, '看妲己画像入神，被大兵抓住，需缴纳**保证金', -10, 7, 'lv*500［送子观音可避免］');
INSERT INTO `cfg_yd_event` VALUES (8, '忽悠路边小白，按市场最高价卖掉身上所有特产', 10, 3, NULL);
INSERT INTO `cfg_yd_event` VALUES (9, '送迷路的老奶奶回家，赠送你**件特产。', 10, 5, '前20级,1个高级，前60级1~2个高级，60级以上2个高级（获得时显示价值）');
INSERT INTO `cfg_yd_event` VALUES (10, '仙人指路，还赠送了个【****】', 10, 5, '（35，35，27，3，0）');
INSERT INTO `cfg_yd_event` VALUES (11, '吃了店老板开的小灶，腰不疼了，腿不酸了，今天一气能多走了3步', 10, 5, NULL);
INSERT INTO `cfg_yd_event` VALUES (12, '被路边大妈忽悠，按市场买入价卖出身上所有特产', -10, 3, '按特产最高比率');
INSERT INTO `cfg_yd_event` VALUES (13, '小偷光顾，损失**件特产。', -10, 5, '随机特产，前20级,1个，前60级1~2个，60级以上2个［送子观音可避免］');
INSERT INTO `cfg_yd_event` VALUES (14, '此路是有开，此树是我栽……强盗从此过，留下**铜钱方得放行', -10, 3, 'lv*1200［送子观音可避免］');
INSERT INTO `cfg_yd_event` VALUES (15, '偶遇土豪以**倍收购【**】。您出售了**件。', 10, 5, '随机1种普通特产4倍基本价特卖或1种高级特产2倍基本价卖。无事财神珠效果');
INSERT INTO `cfg_yd_event` VALUES (16, '从神仙老爷那获得了个幸运号，在猜猜猜中获得***铜钱', 10, 7, '（10,60）*360');
INSERT INTO `cfg_yd_event` VALUES (17, '传送', 0, 3, '到区域外任意地点，但不触发该地点的玩法');
COMMIT;

-- ----------------------------
-- Table structure for cfg_yg_cards
-- ----------------------------
DROP TABLE IF EXISTS `cfg_yg_cards`;
CREATE TABLE `cfg_yg_cards` (
  `id` int(11) NOT NULL COMMENT '同时表示野怪召唤师等级',
  `cards` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '野怪卡组',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='野怪卡组';

-- ----------------------------
-- Records of cfg_yg_cards
-- ----------------------------
BEGIN;
INSERT INTO `cfg_yg_cards` VALUES (1, '1,0;1,0;1,0');
INSERT INTO `cfg_yg_cards` VALUES (2, '1,0;1,0;1,0;1,0');
INSERT INTO `cfg_yg_cards` VALUES (3, '2,0;1,0;1,0;1,0;1,0');
INSERT INTO `cfg_yg_cards` VALUES (4, '2,0;2,0;1,0;1,0;1,0;1,0');
INSERT INTO `cfg_yg_cards` VALUES (5, '2,0;2,0;2,0;1,0;1,0;1,0;1,0');
INSERT INTO `cfg_yg_cards` VALUES (6, '2,0;2,0;2,0;2,0;1,0;1,0;1,0;1,0');
INSERT INTO `cfg_yg_cards` VALUES (7, '2,0;2,0;2,0;2,0;2,0;1,1;1,1;1,1;1,1');
INSERT INTO `cfg_yg_cards` VALUES (8, '2,0;2,0;2,0;2,0;2,0;2,0;1,1;1,1;1,1;1,1');
INSERT INTO `cfg_yg_cards` VALUES (9, '3,0;2,0;2,0;2,0;2,0;2,0;2,0;1,1;1,1;1,1;1,1');
INSERT INTO `cfg_yg_cards` VALUES (10, '3,0;3,0;2,0;2,0;2,0;2,0;2,0;2,0;1,1;1,1;1,1;1,1');
INSERT INTO `cfg_yg_cards` VALUES (11, '3,0;3,0;3,0;2,1;2,1;2,1;2,1;2,1;1,2;1,2;1,2;1,2');
INSERT INTO `cfg_yg_cards` VALUES (12, '3,0;3,0;3,0;3,0;2,1;2,1;2,1;2,1;1,2;1,2;1,2;1,2');
INSERT INTO `cfg_yg_cards` VALUES (13, '3,1;3,1;3,1;3,1;2,2;2,2;2,2;2,2;1,3;1,3;1,3;1,3');
INSERT INTO `cfg_yg_cards` VALUES (14, '3,1;3,1;3,1;3,1;3,1;2,2;2,2;2,2;2,2;1,3;1,3;1,3');
INSERT INTO `cfg_yg_cards` VALUES (15, '3,1;3,1;3,1;3,1;3,1;3,1;2,2;2,2;2,2;2,2;1,3;1,3;1,3');
INSERT INTO `cfg_yg_cards` VALUES (16, '3,2;3,2;3,2;3,2;3,2;3,2;2,3;2,3;2,3;2,3;1,4;1,4;1,4');
INSERT INTO `cfg_yg_cards` VALUES (17, '3,2;3,2;3,2;3,2;3,2;3,2;2,3;2,3;2,3;2,3;1,5;1,5;1,5');
INSERT INTO `cfg_yg_cards` VALUES (18, '3,2;3,2;3,2;3,2;3,2;3,2;2,4;2,4;2,4;2,4;1,5;1,5;1,5');
INSERT INTO `cfg_yg_cards` VALUES (19, '3,3;3,3;3,3;3,3;0,3,3;0,3,3;0,3,3;2,4;2,4;2,4;1,5;1,5;1,5');
INSERT INTO `cfg_yg_cards` VALUES (20, '4,0;3,3;3,3;3,3;3,3;3,3;3,3;2,5;2,5;2,5;2,5;1,6;1,6;1,6');
INSERT INTO `cfg_yg_cards` VALUES (21, '4,0;4,0;3,3;3,3;3,3;3,3;3,3;3,3;2,5;2,5;2,5;2,5;1,6;1,6');
INSERT INTO `cfg_yg_cards` VALUES (22, '4,1;4,1;3,4;3,4;3,4;3,4;3,4;3,4;2,6;2,6;2,6;2,6;1,7;1,7');
INSERT INTO `cfg_yg_cards` VALUES (23, '4,1;4,1;3,5;3,5;3,5;3,5;3,5;3,5;2,6;2,6;2,6;2,6;1,7;1,7');
INSERT INTO `cfg_yg_cards` VALUES (24, '4,2;4,2;3,5;3,5;3,5;3,5;0,3,5;0,3,5;0,3,5;2,7;2,7;2,7;1,8;1,8');
INSERT INTO `cfg_yg_cards` VALUES (25, '4,2;4,2;4,2;3,5;3,5;3,5;3,5;3,5;3,5;2,7;2,7;2,7;2,7;1,8;1,8');
INSERT INTO `cfg_yg_cards` VALUES (26, '4,3;4,3;4,3;3,6;3,6;3,6;3,6;3,6;3,6;2,8;2,8;2,8;2,8;1,9;1,9');
INSERT INTO `cfg_yg_cards` VALUES (27, '4,3;4,3;4,3;3,6;3,6;3,6;3,6;3,6;3,6;2,9;2,9;2,9;2,9;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (28, '4,4;4,4;4,4;3,6;3,6;3,6;3,6;3,6;3,6;2,10;2,10;2,10;2,10;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (29, '4,4;4,4;0,4,4;3,6;3,6;3,6;3,6;0,3,6;0,3,6;0,3,6;2,10;2,10;2,10;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (30, '4,4;4,4;0,4,4;3,6;3,6;3,6;3,6;0,3,6;0,3,6;0,3,6;0,3,6;2,10;2,10;2,10;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (31, '4,4;4,4;4,4;4,4;3,6;3,6;3,6;3,6;3,6;3,6;2,10;2,10;2,10;2,10;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (32, '4,4;4,4;4,4;4,4;3,7;3,7;3,7;3,7;3,7;3,7;2,10;2,10;2,10;2,10;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (33, '4,4;4,4;4,4;4,4;3,7;3,7;3,7;3,7;0,3,7;0,3,7;0,3,7;2,10;2,10;2,10;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (34, '4,4;4,4;4,4;4,4;3,8;3,8;3,8;3,8;0,3,8;0,3,8;0,3,8;0,3,8;2,10;2,10;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (35, '4,4;4,4;4,4;4,4;4,4;3,8;3,8;3,8;3,8;0,3,8;0,3,8;0,3,8;0,3,8;2,10;2,10;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (36, '4,5;4,5;4,5;4,5;4,5;3,8;3,8;3,8;3,8;0,3,8;0,3,8;0,3,8;0,3,8;2,10;2,10;1,10;1,10');
INSERT INTO `cfg_yg_cards` VALUES (37, '4,5;4,5;4,5;4,5;4,5;3,8;3,8;3,8;3,8;3,8;0,3,8;0,3,8;0,3,8;0,3,8;0,3,8;2,10;2,10');
INSERT INTO `cfg_yg_cards` VALUES (38, '4,5;4,5;4,5;4,5;4,5;3,9;3,9;3,9;3,9;3,9;0,3,9;0,3,9;0,3,9;0,3,9;0,3,9;2,10;2,10');
INSERT INTO `cfg_yg_cards` VALUES (39, '4,5;4,5;4,5;0,4,5;0,4,5;0,4,5;3,9;3,9;3,9;3,9;3,9;0,3,9;0,3,9;0,3,9;0,3,9;2,10;2,10');
INSERT INTO `cfg_yg_cards` VALUES (40, '4,5;4,5;4,5;4,5;4,5;4,5;3,9;3,9;3,9;3,9;3,9;0,3,9;0,3,9;0,3,9;0,3,9;0,3,9;2,10;2,10');
INSERT INTO `cfg_yg_cards` VALUES (41, '4,6;4,6;4,6;4,6;4,6;4,6;3,9;3,9;3,9;3,9;3,9;0,3,9;0,3,9;0,3,9;0,3,9;0,3,9;2,10;2,10');
INSERT INTO `cfg_yg_cards` VALUES (42, '4,6;4,6;4,6;4,6;4,6;4,6;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10;2,10;2,10');
INSERT INTO `cfg_yg_cards` VALUES (43, '4,6;4,6;4,6;4,6;4,6;4,6;3,10;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (44, '4,6;4,6;4,6;4,6;4,6;0,4,6;0,4,6;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (45, '5,0;4,6;4,6;4,6;4,6;4,6;4,6;3,10;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (46, '5,0;4,6;4,6;4,6;4,6;4,6;0,4,6;0,4,6;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (47, '5,0;4,6;4,6;4,6;4,6;4,6;0,4,6;0,4,6;0,4,6;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (48, '5,0;4,7;4,7;4,7;4,7;4,7;0,4,7;0,4,7;0,4,7;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (49, '5,0;5,0;4,7;4,7;4,7;4,7;4,7;0,4,7;0,4,7;0,4,7;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (50, '5,1;5,1;4,7;4,7;4,7;4,7;4,7;0,4,7;0,4,7;0,4,7;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (51, '5,2;5,2;4,7;4,7;4,7;4,7;4,7;0,4,7;0,4,7;0,4,7;3,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (52, '5,2;5,2;4,7;4,7;4,7;4,7;4,7;4,7;0,4,7;0,4,7;0,4,7;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (53, '5,2;5,2;4,8;4,8;4,8;4,8;4,8;0,4,8;0,4,8;0,4,8;0,4,8;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (54, '5,2;5,2;4,8;4,8;4,8;4,8;4,8;0,4,8;0,4,8;0,4,8;0,4,8;0,4,8;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (55, '5,3;5,3;4,8;4,8;4,8;4,8;4,8;0,4,8;0,4,8;0,4,8;0,4,8;0,4,8;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (56, '5,3;5,3;4,9;4,9;4,9;4,9;4,9;0,4,9;0,4,9;0,4,9;0,4,9;0,4,9;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (57, '5,4;5,4;4,9;4,9;4,9;4,9;4,9;0,4,9;0,4,9;0,4,9;0,4,9;0,4,9;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (58, '5,5;5,5;4,9;4,9;4,9;4,9;4,9;0,4,9;0,4,9;0,4,9;0,4,9;0,4,9;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (59, '5,5;5,5;0,5,5;4,9;4,9;4,9;4,9;0,4,9;0,4,9;0,4,9;0,4,9;0,4,9;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (60, '5,5;5,5;0,5,5;0,5,5;4,9;4,9;4,9;4,9;0,4,9;0,4,9;0,4,9;0,4,9;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (61, '5,5;5,5;0,5,5;0,5,5;4,10;4,10;4,10;4,10;0,4,10;0,4,10;0,4,10;0,4,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (62, '5,6;5,6;0,5,6;0,5,6;4,10;4,10;4,10;4,10;0,4,10;0,4,10;0,4,10;0,4,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (63, '5,7;5,7;0,5,7;0,5,7;4,10;4,10;4,10;4,10;0,4,10;0,4,10;0,4,10;0,4,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (64, '5,8;5,8;0,5,8;0,5,8;4,10;4,10;4,10;4,10;0,4,10;0,4,10;0,4,10;0,4,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (65, '5,9;5,9;0,5,9;0,5,9;4,10;4,10;4,10;4,10;0,4,10;0,4,10;0,4,10;0,4,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (66, '5,10;5,10;0,5,10;0,5,10;4,10;4,10;4,10;4,10;0,4,10;0,4,10;0,4,10;0,4,10;3,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (67, '5,10;5,10;0,5,10;0,5,10;0,5,10;219,10;311,10;312,10;406,10;410,10;415,10;416,10;418,10;514,10;403,10;4,10;3,10;3,10;3,10;3,10');
INSERT INTO `cfg_yg_cards` VALUES (68, '5,10;5,10;5,10;0,5,10;0,5,10;0,5,10;4,10;4,10;4,10;4,10;0,4,10;0,4,10;0,4,10;0,4,10;3,10;3,10;3,10;0,3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (69, '5,10;5,10;5,10;0,5,10;0,5,10;0,5,10;0,5,10;4,10;4,10;4,10;4,10;0,4,10;0,4,10;0,4,10;0,4,10;0,4,10;3,10;3,10;0,3,10;0,3,10');
INSERT INTO `cfg_yg_cards` VALUES (70, '525,10;301,10;501,10;502,10;101,10;504,10;103,10;325,10;303,10;203,10;205,10;306,10;513,10;310,10;312,10;410,10;209,10;327,10;328,10;409,10');
INSERT INTO `cfg_yg_cards` VALUES (71, '525,10;325,10;401,10;302,10;505,10;406,10;407,10;506,10;308,10;208,10;105,10;212,10;510,10;5,10;5,10;0,5,10;4,10;4,10;4,10;4,10');
INSERT INTO `cfg_yg_cards` VALUES (72, '0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;0,5,10;4,10;4,10;4,10;4,10');
COMMIT;

-- ----------------------------
-- Table structure for ins_game_data
-- ----------------------------
DROP TABLE IF EXISTS `ins_game_data`;
CREATE TABLE `ins_game_data` (
  `data_id` bigint(20) unsigned NOT NULL COMMENT '数据ID',
  `data_type` varchar(20) NOT NULL COMMENT '数据类型',
  `data_json` json NOT NULL COMMENT '资源JSON',
  PRIMARY KEY (`data_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全服每日相关数据';

-- ----------------------------
-- Table structure for ins_game_day_data
-- ----------------------------
DROP TABLE IF EXISTS `ins_game_day_data`;
CREATE TABLE `ins_game_day_data` (
  `data_id` bigint(20) unsigned NOT NULL COMMENT '数据ID',
  `date_int` int(11) unsigned NOT NULL COMMENT '日期的数字类型',
  `data_type` varchar(20) NOT NULL COMMENT '数据类型',
  `data_json` json NOT NULL COMMENT '资源JSON',
  PRIMARY KEY (`data_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全服每日相关数据';

-- ----------------------------
-- Records of ins_game_day_data
-- ----------------------------
BEGIN;
INSERT INTO `ins_game_day_data` VALUES (1904071001, 20190407, 'flxresult', '{\"id\": 1904071001, \"sgNum\": 17, \"dateInt\": 20190407, \"ysgBet1\": 30, \"ysgBet2\": 10, \"ysgBet3\": 30, \"ysgCard\": 508, \"cardName\": \"石矶娘娘\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"30、10、30\"}');
INSERT INTO `ins_game_day_data` VALUES (1904081001, 20190408, 'flxresult', '{\"id\": 1904081001, \"sgNum\": 35, \"dateInt\": 20190408, \"ysgBet1\": 20, \"ysgBet2\": 30, \"ysgBet3\": 10, \"ysgCard\": 405, \"cardName\": \"胡喜媚\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"20、30、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1904091001, 20190409, 'flxresult', '{\"id\": 1904091001, \"sgNum\": 3, \"dateInt\": 20190409, \"ysgBet1\": 20, \"ysgBet2\": 30, \"ysgBet3\": 50, \"ysgCard\": 407, \"cardName\": \"魔礼青\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"20、30、50\"}');
INSERT INTO `ins_game_day_data` VALUES (1904101001, 20190410, 'flxresult', '{\"id\": 1904101001, \"sgNum\": 20, \"dateInt\": 20190410, \"ysgBet1\": 40, \"ysgBet2\": 40, \"ysgBet3\": 10, \"ysgCard\": 303, \"cardName\": \"云霄\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"40、40、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1904111001, 20190411, 'flxresult', '{\"id\": 1904111001, \"sgNum\": 36, \"dateInt\": 20190411, \"ysgBet1\": 20, \"ysgBet2\": 50, \"ysgBet3\": 30, \"ysgCard\": 402, \"cardName\": \"火灵圣母\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"20、50、30\"}');
INSERT INTO `ins_game_day_data` VALUES (1904121001, 20190412, 'flxresult', '{\"id\": 1904121001, \"sgNum\": 20, \"dateInt\": 20190412, \"ysgBet1\": 30, \"ysgBet2\": 30, \"ysgBet3\": 10, \"ysgCard\": 208, \"cardName\": \"魔礼海\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"30、30、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1904131001, 20190413, 'flxresult', '{\"id\": 1904131001, \"sgNum\": 5, \"dateInt\": 20190413, \"ysgBet1\": 30, \"ysgBet2\": 20, \"ysgBet3\": 20, \"ysgCard\": 304, \"cardName\": \"崇黑虎\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"30、20、20\"}');
INSERT INTO `ins_game_day_data` VALUES (1904141001, 20190414, 'flxresult', '{\"id\": 1904141001, \"sgNum\": 30, \"dateInt\": 20190414, \"ysgBet1\": 50, \"ysgBet2\": 10, \"ysgBet3\": 50, \"ysgCard\": 508, \"cardName\": \"石矶娘娘\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"50、10、50\"}');
INSERT INTO `ins_game_day_data` VALUES (1904151001, 20190415, 'flxresult', '{\"id\": 1904151001, \"sgNum\": 7, \"dateInt\": 20190415, \"ysgBet1\": 50, \"ysgBet2\": 10, \"ysgBet3\": 40, \"ysgCard\": 308, \"cardName\": \"魔礼红\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"50、10、40\"}');
INSERT INTO `ins_game_day_data` VALUES (1904161001, 20190416, 'flxresult', '{\"id\": 1904161001, \"sgNum\": 34, \"dateInt\": 20190416, \"ysgBet1\": 10, \"ysgBet2\": 50, \"ysgBet3\": 40, \"ysgCard\": 105, \"cardName\": \"李靖\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"10、50、40\"}');
INSERT INTO `ins_game_day_data` VALUES (1904171001, 20190417, 'flxresult', '{\"id\": 1904171001, \"sgNum\": 25, \"dateInt\": 20190417, \"ysgBet1\": 30, \"ysgBet2\": 10, \"ysgBet3\": 10, \"ysgCard\": 106, \"cardName\": \"韦护\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"30、10、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1904181001, 20190418, 'flxresult', '{\"id\": 1904181001, \"sgNum\": 5, \"dateInt\": 20190418, \"ysgBet1\": 50, \"ysgBet2\": 30, \"ysgBet3\": 10, \"ysgCard\": 205, \"cardName\": \"申公豹\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"50、30、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1904191001, 20190419, 'flxresult', '{\"id\": 1904191001, \"sgNum\": 14, \"dateInt\": 20190419, \"ysgBet1\": 20, \"ysgBet2\": 30, \"ysgBet3\": 10, \"ysgCard\": 304, \"cardName\": \"崇黑虎\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"20、30、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1904201001, 20190420, 'flxresult', '{\"id\": 1904201001, \"sgNum\": 8, \"dateInt\": 20190420, \"ysgBet1\": 20, \"ysgBet2\": 30, \"ysgBet3\": 20, \"ysgCard\": 505, \"cardName\": \"邬文化\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"20、30、20\"}');
INSERT INTO `ins_game_day_data` VALUES (1904211001, 20190421, 'flxresult', '{\"id\": 1904211001, \"sgNum\": 34, \"dateInt\": 20190421, \"ysgBet1\": 30, \"ysgBet2\": 40, \"ysgBet3\": 20, \"ysgCard\": 501, \"cardName\": \"闻仲\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"30、40、20\"}');
INSERT INTO `ins_game_day_data` VALUES (1904221001, 20190422, 'flxresult', '{\"id\": 1904221001, \"sgNum\": 2, \"dateInt\": 20190422, \"ysgBet1\": 40, \"ysgBet2\": 10, \"ysgBet3\": 40, \"ysgCard\": 104, \"cardName\": \"雷震子\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"40、10、40\"}');
INSERT INTO `ins_game_day_data` VALUES (1904231001, 20190423, 'flxresult', '{\"id\": 1904231001, \"sgNum\": 4, \"dateInt\": 20190423, \"ysgBet1\": 20, \"ysgBet2\": 30, \"ysgBet3\": 20, \"ysgCard\": 303, \"cardName\": \"云霄\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"20、30、20\"}');
INSERT INTO `ins_game_day_data` VALUES (1904241001, 20190424, 'flxresult', '{\"id\": 1904241001, \"sgNum\": 10, \"dateInt\": 20190424, \"ysgBet1\": 40, \"ysgBet2\": 10, \"ysgBet3\": 10, \"ysgCard\": 203, \"cardName\": \"桃花星\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"40、10、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1904251001, 20190425, 'flxresult', '{\"id\": 1904251001, \"sgNum\": 23, \"dateInt\": 20190425, \"ysgBet1\": 20, \"ysgBet2\": 30, \"ysgBet3\": 40, \"ysgCard\": 307, \"cardName\": \"九天玄女\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"20、30、40\"}');
INSERT INTO `ins_game_day_data` VALUES (1904261001, 20190426, 'flxresult', '{\"id\": 1904261001, \"sgNum\": 21, \"dateInt\": 20190426, \"ysgBet1\": 20, \"ysgBet2\": 10, \"ysgBet3\": 10, \"ysgCard\": 403, \"cardName\": \"金光圣母\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"20、10、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1904271001, 20190427, 'flxresult', '{\"id\": 1904271001, \"sgNum\": 1, \"dateInt\": 20190427, \"ysgBet1\": 30, \"ysgBet2\": 10, \"ysgBet3\": 10, \"ysgCard\": 108, \"cardName\": \"周公旦\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"30、10、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1904281001, 20190428, 'flxresult', '{\"id\": 1904281001, \"sgNum\": 16, \"dateInt\": 20190428, \"ysgBet1\": 50, \"ysgBet2\": 20, \"ysgBet3\": 40, \"ysgCard\": 104, \"cardName\": \"雷震子\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"50、20、40\"}');
INSERT INTO `ins_game_day_data` VALUES (1904291001, 20190429, 'flxresult', '{\"id\": 1904291001, \"sgNum\": 23, \"dateInt\": 20190429, \"ysgBet1\": 50, \"ysgBet2\": 40, \"ysgBet3\": 20, \"ysgCard\": 104, \"cardName\": \"雷震子\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"50、40、20\"}');
INSERT INTO `ins_game_day_data` VALUES (1904301001, 20190430, 'flxresult', '{\"id\": 1904301001, \"sgNum\": 1, \"dateInt\": 20190430, \"ysgBet1\": 40, \"ysgBet2\": 50, \"ysgBet3\": 30, \"ysgCard\": 507, \"cardName\": \"土行孙\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"40、50、30\"}');
INSERT INTO `ins_game_day_data` VALUES (1905011001, 20190501, 'flxresult', '{\"id\": 1905011001, \"sgNum\": 31, \"dateInt\": 20190501, \"ysgBet1\": 10, \"ysgBet2\": 10, \"ysgBet3\": 50, \"ysgCard\": 202, \"cardName\": \"句芒\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"10、10、50\"}');
INSERT INTO `ins_game_day_data` VALUES (1905021001, 20190502, 'flxresult', '{\"id\": 1905021001, \"sgNum\": 30, \"dateInt\": 20190502, \"ysgBet1\": 50, \"ysgBet2\": 30, \"ysgBet3\": 10, \"ysgCard\": 206, \"cardName\": \"木吒\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"50、30、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1905031001, 20190503, 'flxresult', '{\"id\": 1905031001, \"sgNum\": 27, \"dateInt\": 20190503, \"ysgBet1\": 30, \"ysgBet2\": 40, \"ysgBet3\": 40, \"ysgCard\": 405, \"cardName\": \"胡喜媚\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"30、40、40\"}');
INSERT INTO `ins_game_day_data` VALUES (1905041001, 20190504, 'flxresult', '{\"id\": 1905041001, \"sgNum\": 15, \"dateInt\": 20190504, \"ysgBet1\": 10, \"ysgBet2\": 20, \"ysgBet3\": 10, \"ysgCard\": 107, \"cardName\": \"黄天化\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"10、20、10\"}');
INSERT INTO `ins_game_day_data` VALUES (1905051001, 20190505, 'flxresult', '{\"id\": 1905051001, \"sgNum\": 18, \"dateInt\": 20190505, \"ysgBet1\": 50, \"ysgBet2\": 10, \"ysgBet3\": 40, \"ysgCard\": 506, \"cardName\": \"魔礼寿\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"50、10、40\"}');
INSERT INTO `ins_game_day_data` VALUES (1905061001, 20190506, 'flxresult', '{\"id\": 1905061001, \"sgNum\": 2, \"dateInt\": 20190506, \"ysgBet1\": 40, \"ysgBet2\": 30, \"ysgBet3\": 50, \"ysgCard\": 203, \"cardName\": \"桃花星\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"40、30、50\"}');
INSERT INTO `ins_game_day_data` VALUES (1905071001, 20190507, 'flxresult', '{\"id\": 1905071001, \"sgNum\": 8, \"dateInt\": 20190507, \"ysgBet1\": 20, \"ysgBet2\": 30, \"ysgBet3\": 40, \"ysgCard\": 505, \"cardName\": \"邬文化\", \"dataType\": \"FLXRESULT\", \"ysgEleNames\": \"20、30、40\"}');
COMMIT;

-- ----------------------------
-- Table structure for ins_role_info
-- ----------------------------
DROP TABLE IF EXISTS `ins_role_info`;
CREATE TABLE `ins_role_info` (
  `uid` bigint(20) unsigned NOT NULL COMMENT '区服玩家id。对应game_user.id',
  `username` varchar(128) COLLATE utf8_unicode_ci NOT NULL COMMENT '账号',
  `nickname` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '区服玩家昵称。对应game_user.nickname',
  `level` int(11) unsigned NOT NULL DEFAULT '1' COMMENT '等级',
  `origin_sid` int(11) unsigned NOT NULL COMMENT '注册时候的区服ID',
  `sid` int(11) unsigned NOT NULL COMMENT '当前所属区服ID',
  `cid` int(11) unsigned NOT NULL COMMENT '渠道ID。对应base_plat.id。',
  `invi_code` varchar(16) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '邀请码',
  `reg_date` int(8) unsigned NOT NULL COMMENT '注册日期',
  `reg_Ip` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '注册ip',
  `reg_device` varchar(64) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '注册时候的设备标识',
  `server_name` varchar(16) COLLATE utf8_unicode_ci NOT NULL COMMENT '区服名称',
  `last_login_date` int(8) unsigned NOT NULL COMMENT '上次登录日期',
  `row_update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '行更新时间',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='角色信息表';

-- ----------------------------
-- Records of ins_role_info
-- ----------------------------
BEGIN;
INSERT INTO `ins_role_info` VALUES (190403009800003, 'zftest8', '季飞运', 1, 98, 98, 0, 'qmt949', 20190403, '192.168.31.78', '', '98区', 20190403, '2019-04-03 23:24:25');
COMMIT;

-- ----------------------------
-- Table structure for QRTZ_BLOB_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for QRTZ_CALENDARS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for QRTZ_CRON_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_CRON_TRIGGERS
-- ----------------------------
BEGIN;
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_1', 'DEFAULT', '15 0/1 * * * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_11', 'DEFAULT', '0 10 1 * * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_2', 'DEFAULT', '0 0/1 * * * ? *', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_21', 'DEFAULT', '10 30 12,20 * * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_22', 'DEFAULT', '40 4/20 * * * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_23', 'DEFAULT', '10 5/20 * * * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_24', 'DEFAULT', '0 10 14 20 * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_25', 'DEFAULT', '0 0 14 20 * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_3', 'DEFAULT', '30 0/1 * * * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_5', 'DEFAULT', '0 0 15 1/7 * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_6', 'DEFAULT', '0 0 15 * * ? ', 'Asia/Shanghai');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_9', 'DEFAULT', '2 1 0 * * ? ', 'Asia/Shanghai');
COMMIT;

-- ----------------------------
-- Table structure for QRTZ_FIRED_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_FIRED_TRIGGERS
-- ----------------------------
BEGIN;
INSERT INTO `QRTZ_FIRED_TRIGGERS` VALUES ('bbw-god-game-logic', 'xy15546502352101554650235158', 'TASK_2', 'DEFAULT', 'xy1554650235210', 1554650310018, 1554650340000, 5, 'ACQUIRED', NULL, NULL, '0', '0');
COMMIT;

-- ----------------------------
-- Table structure for QRTZ_JOB_DETAILS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_JOB_DETAILS
-- ----------------------------
BEGIN;
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_1', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000F67616D6555736572546F44424A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F313520302F31202A202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000001740005646F4A6F627400013074002BE4BF9DE5AD9847616D6555736572E6AF8FE58886E9929FE79A84E7ACAC3135E7A792E689A7E8A18CE38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_11', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000B666C7841776172644A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000D302031302031202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000000B740005646F4A6F6274000131740025E7A68FE4B8B4E8BDA9E5BC80E5A596E38082E6AF8FE5A4A9303A3130E58886E689A7E8A18C737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_2', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000F7573657244617461546F44424A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F3020302F31202A202A202A203F202A7372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000002740005646F4A6F6274000130740018E6AF8FE58886E9929FE68C81E4B985E58C96E695B0E68DAE737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_21', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000C6D616F7541776172644A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874001231302033302031322C3230202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000015740005646F4A6F627400013174003AE9AD94E78E8BE5A596E58AB1E58F91E694BEE38082E6AF8FE5A4A931323A33303A3130E5928C32303A33303A3130E7A792E689A7E8A18CE38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_22', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B7870740013667374506F696E74496E6372656173654A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE0078740010343020342F3230202A202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000016740005646F4A6F627400013174004BE5B081E7A59EE58FB0E7A7AFE58886E5A29EE995BFE38082E4BB8EE7ACAC34E58886E9929F3430E7A792E5BC80E5A78BEFBC8CE6AF8F3230E58886E9929FE689A7E8A18C31E6ACA1E38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_23', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000F7370656369616C50726963654A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE0078740010313020352F3230202A202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000017740005646F4A6F6274000130740042E789B9E4BAA7E6B6A8E4BBB7E38082E4BB8EE7ACAC35E58886E9929F3130E7A792E5BC80E5A78BEFBC8CE6AF8F3230E58886E9929FE689A7E8A18C31E6ACA1E38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_24', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B7870740013616374697669747947656E65726174654A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F30203130203134203230202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000018740005646F4A6F627400013174002AE6B4BBE58AA8E5AE9EE4BE8BE7949FE688902CE6AF8FE69C883230E58FB72031343A3130E689A7E8A18C737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_25', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B7870740017616374697669747952616E6B47656E65726174654A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000E302030203134203230202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000019740005646F4A6F627400013174002AE586B2E6A69CE5AE9EE4BE8BE7949FE688902CE6AF8FE69C883230E58FB72031343A3030E689A7E8A18C737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_3', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074001173657276657244617461546F44424A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F333020302F31202A202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000003740005646F4A6F627400013074001EE6AF8FE58886E9929FE7ACAC3330E7A792E689A7E8A18C31E6ACA1E38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_5', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000E70726570617265446174614A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F30203020313520312F37202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000005740005646F4A6F627400013174001BE68F90E5898DE7949FE68890E9858DE7BDAEE695B0E68DAEE38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_6', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000B6865616C7468436865636B7372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000D302030203135202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000006740005646F4A6F627400013174001AE6AF8FE5A4A93135E782B9E581A5E5BAB7E6A380E69FA5E38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('bbw-god-game-logic', 'TASK_9', 'DEFAULT', NULL, 'com.bbw.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074001973656E64416374697669747952616E6B4177617264734A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000C3220312030202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000009740005646F4A6F6274000131740025E586B2E6A69CE5A596E58AB1E38082E6AF8FE5A4A9303A30313A3032E689A7E8A18CE38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
COMMIT;

-- ----------------------------
-- Table structure for QRTZ_LOCKS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_LOCKS
-- ----------------------------
BEGIN;
INSERT INTO `QRTZ_LOCKS` VALUES ('bbw-god-game-logic', 'STATE_ACCESS');
INSERT INTO `QRTZ_LOCKS` VALUES ('bbw-god-game-logic', 'TRIGGER_ACCESS');
COMMIT;

-- ----------------------------
-- Table structure for QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for QRTZ_SCHEDULER_STATE
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_SCHEDULER_STATE
-- ----------------------------
BEGIN;
INSERT INTO `QRTZ_SCHEDULER_STATE` VALUES ('bbw-god-game-logic', 'xy1554650235210', 1554650327745, 20000);
COMMIT;

-- ----------------------------
-- Table structure for QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for QRTZ_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_TRIGGERS
-- ----------------------------
BEGIN;
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_1', 'DEFAULT', 'TASK_1', 'DEFAULT', NULL, 1554045615000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000F67616D6555736572546F44424A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F313520302F31202A202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000001740005646F4A6F627400013074002BE4BF9DE5AD9847616D6555736572E6AF8FE58886E9929FE79A84E7ACAC3135E7A792E689A7E8A18CE38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_11', 'DEFAULT', 'TASK_11', 'DEFAULT', NULL, 1554052200000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000B666C7841776172644A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000D302031302031202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000000B740005646F4A6F6274000131740025E7A68FE4B8B4E8BDA9E5BC80E5A596E38082E6AF8FE5A4A9303A3130E58886E689A7E8A18C737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_2', 'DEFAULT', 'TASK_2', 'DEFAULT', NULL, 1554045600000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000F7573657244617461546F44424A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F3020302F31202A202A202A203F202A7372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000002740005646F4A6F6274000130740018E6AF8FE58886E9929FE68C81E4B985E58C96E695B0E68DAE737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_21', 'DEFAULT', 'TASK_21', 'DEFAULT', NULL, 1554093010000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000C6D616F7541776172644A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874001231302033302031322C3230202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000015740005646F4A6F627400013174003AE9AD94E78E8BE5A596E58AB1E58F91E694BEE38082E6AF8FE5A4A931323A33303A3130E5928C32303A33303A3130E7A792E689A7E8A18CE38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_22', 'DEFAULT', 'TASK_22', 'DEFAULT', NULL, 1554045880000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B7870740013667374506F696E74496E6372656173654A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE0078740010343020342F3230202A202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000016740005646F4A6F627400013174004BE5B081E7A59EE58FB0E7A7AFE58886E5A29EE995BFE38082E4BB8EE7ACAC34E58886E9929F3430E7A792E5BC80E5A78BEFBC8CE6AF8F3230E58886E9929FE689A7E8A18C31E6ACA1E38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_23', 'DEFAULT', 'TASK_23', 'DEFAULT', NULL, 1554045910000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000F7370656369616C50726963654A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE0078740010313020352F3230202A202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000017740005646F4A6F6274000130740042E789B9E4BAA7E6B6A8E4BBB7E38082E4BB8EE7ACAC35E58886E9929F3130E7A792E5BC80E5A78BEFBC8CE6AF8F3230E58886E9929FE689A7E8A18C31E6ACA1E38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_24', 'DEFAULT', 'TASK_24', 'DEFAULT', NULL, 1555740600000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B7870740013616374697669747947656E65726174654A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F30203130203134203230202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000018740005646F4A6F627400013174002AE6B4BBE58AA8E5AE9EE4BE8BE7949FE688902CE6AF8FE69C883230E58FB72031343A3130E689A7E8A18C737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_25', 'DEFAULT', 'TASK_25', 'DEFAULT', NULL, 1555740000000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B7870740017616374697669747952616E6B47656E65726174654A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000E302030203134203230202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000019740005646F4A6F627400013174002AE586B2E6A69CE5AE9EE4BE8BE7949FE688902CE6AF8FE69C883230E58FB72031343A3030E689A7E8A18C737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_3', 'DEFAULT', 'TASK_3', 'DEFAULT', NULL, 1554045570000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074001173657276657244617461546F44424A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F333020302F31202A202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000003740005646F4A6F627400013074001EE6AF8FE58886E9929FE7ACAC3330E7A792E689A7E8A18C31E6ACA1E38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_5', 'DEFAULT', 'TASK_5', 'DEFAULT', NULL, 1554102000000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000E70726570617265446174614A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000F30203020313520312F37202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000005740005646F4A6F627400013174001BE68F90E5898DE7949FE68890E9858DE7BDAEE695B0E68DAEE38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_6', 'DEFAULT', 'TASK_6', 'DEFAULT', NULL, 1554102000000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074000B6865616C7468436865636B7372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000D302030203135202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000006740005646F4A6F627400013174001AE6AF8FE5A4A93135E782B9E581A5E5BAB7E6A380E69FA5E38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('bbw-god-game-logic', 'TASK_9', 'DEFAULT', 'TASK_9', 'DEFAULT', NULL, 1554048062000, -1, 5, 'WAITING', 'CRON', 1554045569000, 0, NULL, 2, 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720024636F6D2E6262772E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074001973656E64416374697669747952616E6B4177617264734A6F627372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000169D70DCE007874000C3220312030202A202A203F207372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000009740005646F4A6F6274000131740025E586B2E6A69CE5A596E58AB1E38082E6AF8FE5A4A9303A30313A3032E689A7E8A18CE38082737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
COMMIT;

-- ----------------------------
-- Table structure for receipt
-- ----------------------------
DROP TABLE IF EXISTS `receipt`;
CREATE TABLE `receipt` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `game_user_id` bigint(11) DEFAULT NULL,
  `uuid` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `transaction_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `purchase_date` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '3.产品有误， 或者该产品已经失效 2.重复收据  1已经正常下放 0未处理的状态',
  `original_info` varchar(1000) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `way` int(2) DEFAULT NULL COMMENT '10补发订单\r\n20测试订单',
  PRIMARY KEY (`id`),
  KEY `game_user_id` (`game_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='\r\nstatus  0 未处理  1超过次数限制 2.产品内部编号有误 3.产品类别有误（暂时只有黄金 白银）';

-- ----------------------------
-- Table structure for runtime_var
-- ----------------------------
DROP TABLE IF EXISTS `runtime_var`;
CREATE TABLE `runtime_var` (
  `name` varchar(255) NOT NULL COMMENT '参数名',
  `value` varchar(255) DEFAULT NULL COMMENT '参数值',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运行时参数';

-- ----------------------------
-- Table structure for schedule_job
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job`;
CREATE TABLE `schedule_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `method_name` varchar(100) DEFAULT NULL COMMENT '方法名',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  `status` tinyint(4) DEFAULT NULL COMMENT '任务状态  0：正常  1：暂停',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='定时任务';

-- ----------------------------
-- Records of schedule_job
-- ----------------------------
BEGIN;
INSERT INTO `schedule_job` VALUES (1, 'gameUserToDBJob', 'doJob', '0', '15 0/1 * * * ? ', 0, '保存GameUser每分钟的第15秒执行。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (2, 'userDataToDBJob', 'doJob', '0', '0 0/1 * * * ? *', 0, '每分钟持久化数据', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (3, 'serverDataToDBJob', 'doJob', '0', '30 0/1 * * * ? ', 0, '每分钟第30秒执行1次。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (5, 'prepareDataJob', 'doJob', '1', '0 0 15 1/7 * ? ', 0, '提前生成配置数据。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (6, 'healthCheck', 'doJob', '1', '0 0 15 * * ? ', 0, '每天15点健康检查。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (9, 'sendActivityRankAwardsJob', 'doJob', '1', '2 1 0 * * ? ', 0, '冲榜奖励。每天0:01:02执行。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (11, 'flxAwardJob', 'doJob', '1', '0 10 1 * * ? ', 0, '福临轩开奖。每天0:10分执行', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (21, 'maouAwardJob', 'doJob', '1', '10 30 12,20 * * ? ', 0, '魔王奖励发放。每天12:30:10和20:30:10秒执行。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (22, 'fstPointIncreaseJob', 'doJob', '1', '40 4/20 * * * ? ', 0, '封神台积分增长。从第4分钟40秒开始，每20分钟执行1次。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (23, 'specialPriceJob', 'doJob', '0', '10 5/20 * * * ? ', 0, '特产涨价。从第5分钟10秒开始，每20分钟执行1次。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (24, 'activityGenerateJob', 'doJob', '1', '0 10 14 20 * ? ', 0, '活动实例生成,每月20号 14:10执行', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (25, 'activityRankGenerateJob', 'doJob', '1', '0 0 14 20 * ? ', 0, '冲榜实例生成,每月20号 14:00执行', '2019-03-31 23:00:00');
COMMIT;

-- ----------------------------
-- Table structure for schedule_job_log
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job_log`;
CREATE TABLE `schedule_job_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志id',
  `job_id` bigint(20) NOT NULL COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `method_name` varchar(100) DEFAULT NULL COMMENT '方法名',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `status` tinyint(4) NOT NULL COMMENT '任务状态    0：成功    1：失败',
  `error` varchar(2000) DEFAULT NULL COMMENT '失败信息',
  `times` int(11) NOT NULL COMMENT '耗时(单位：毫秒)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `job_id` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=414 DEFAULT CHARSET=utf8 COMMENT='定时任务日志';

-- ----------------------------
-- Records of schedule_job_log
-- ----------------------------
BEGIN;
INSERT INTO `schedule_job_log` VALUES (1, 22, 'fstPointIncreaseJob', 'doJob', '1', 0, NULL, 144, '2019-04-03 10:24:40');
INSERT INTO `schedule_job_log` VALUES (2, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 182, '2019-04-03 10:25:00');
INSERT INTO `schedule_job_log` VALUES (3, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-03 10:57:00');
INSERT INTO `schedule_job_log` VALUES (4, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 197, '2019-04-03 10:57:15');
INSERT INTO `schedule_job_log` VALUES (5, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-03 10:57:30');
INSERT INTO `schedule_job_log` VALUES (6, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-03 10:58:00');
INSERT INTO `schedule_job_log` VALUES (7, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 28, '2019-04-03 10:58:15');
INSERT INTO `schedule_job_log` VALUES (8, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-03 10:58:30');
INSERT INTO `schedule_job_log` VALUES (9, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-03 10:59:00');
INSERT INTO `schedule_job_log` VALUES (10, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-03 10:59:15');
INSERT INTO `schedule_job_log` VALUES (11, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 12, '2019-04-03 10:59:30');
INSERT INTO `schedule_job_log` VALUES (12, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 104, '2019-04-03 11:00:00');
INSERT INTO `schedule_job_log` VALUES (13, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 31, '2019-04-03 11:00:15');
INSERT INTO `schedule_job_log` VALUES (14, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 37, '2019-04-03 11:00:30');
INSERT INTO `schedule_job_log` VALUES (15, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 414, '2019-04-03 23:51:30');
INSERT INTO `schedule_job_log` VALUES (16, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-03 23:55:30');
INSERT INTO `schedule_job_log` VALUES (17, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 162, '2019-04-04 00:34:15');
INSERT INTO `schedule_job_log` VALUES (18, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 171, '2019-04-04 00:34:30');
INSERT INTO `schedule_job_log` VALUES (19, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-04 00:35:00');
INSERT INTO `schedule_job_log` VALUES (20, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 79, '2019-04-04 00:35:15');
INSERT INTO `schedule_job_log` VALUES (21, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-04 00:35:30');
INSERT INTO `schedule_job_log` VALUES (22, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-04 00:36:00');
INSERT INTO `schedule_job_log` VALUES (23, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-04 00:36:15');
INSERT INTO `schedule_job_log` VALUES (24, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 174, '2019-04-04 00:36:30');
INSERT INTO `schedule_job_log` VALUES (25, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-04 00:37:00');
INSERT INTO `schedule_job_log` VALUES (26, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 22, '2019-04-04 00:37:15');
INSERT INTO `schedule_job_log` VALUES (27, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-04 00:37:30');
INSERT INTO `schedule_job_log` VALUES (28, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-04 00:38:00');
INSERT INTO `schedule_job_log` VALUES (29, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-04 00:38:15');
INSERT INTO `schedule_job_log` VALUES (30, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 57, '2019-04-04 00:38:30');
INSERT INTO `schedule_job_log` VALUES (31, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 127, '2019-04-04 00:48:15');
INSERT INTO `schedule_job_log` VALUES (32, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-04 00:48:30');
INSERT INTO `schedule_job_log` VALUES (33, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 26, '2019-04-04 00:49:00');
INSERT INTO `schedule_job_log` VALUES (34, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 27, '2019-04-04 00:49:15');
INSERT INTO `schedule_job_log` VALUES (35, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 33, '2019-04-04 00:57:00');
INSERT INTO `schedule_job_log` VALUES (36, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 164, '2019-04-04 00:57:15');
INSERT INTO `schedule_job_log` VALUES (37, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 263, '2019-04-04 00:57:30');
INSERT INTO `schedule_job_log` VALUES (38, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 66, '2019-04-04 00:58:00');
INSERT INTO `schedule_job_log` VALUES (39, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-04 00:58:15');
INSERT INTO `schedule_job_log` VALUES (40, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-04 00:58:30');
INSERT INTO `schedule_job_log` VALUES (41, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-04 00:59:00');
INSERT INTO `schedule_job_log` VALUES (42, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 22, '2019-04-04 00:59:15');
INSERT INTO `schedule_job_log` VALUES (43, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 75, '2019-04-04 00:59:30');
INSERT INTO `schedule_job_log` VALUES (44, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-04 01:00:00');
INSERT INTO `schedule_job_log` VALUES (45, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-04 01:00:15');
INSERT INTO `schedule_job_log` VALUES (46, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 12, '2019-04-04 01:00:30');
INSERT INTO `schedule_job_log` VALUES (47, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-04 01:01:00');
INSERT INTO `schedule_job_log` VALUES (48, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-04 01:01:15');
INSERT INTO `schedule_job_log` VALUES (49, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-04 01:16:00');
INSERT INTO `schedule_job_log` VALUES (50, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 140, '2019-04-04 01:16:15');
INSERT INTO `schedule_job_log` VALUES (51, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 158, '2019-04-04 01:16:30');
INSERT INTO `schedule_job_log` VALUES (52, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 36, '2019-04-04 08:48:00');
INSERT INTO `schedule_job_log` VALUES (53, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 145, '2019-04-04 08:48:15');
INSERT INTO `schedule_job_log` VALUES (54, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-04 08:48:30');
INSERT INTO `schedule_job_log` VALUES (55, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 27, '2019-04-04 08:49:00');
INSERT INTO `schedule_job_log` VALUES (56, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 28, '2019-04-04 08:49:15');
INSERT INTO `schedule_job_log` VALUES (57, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 492, '2019-04-04 08:49:30');
INSERT INTO `schedule_job_log` VALUES (58, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-04 08:50:00');
INSERT INTO `schedule_job_log` VALUES (59, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 41, '2019-04-04 08:50:15');
INSERT INTO `schedule_job_log` VALUES (60, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-04 08:50:30');
INSERT INTO `schedule_job_log` VALUES (61, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-04 08:51:00');
INSERT INTO `schedule_job_log` VALUES (62, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 22, '2019-04-04 08:51:15');
INSERT INTO `schedule_job_log` VALUES (63, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 56, '2019-04-04 08:51:30');
INSERT INTO `schedule_job_log` VALUES (64, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-04 08:52:00');
INSERT INTO `schedule_job_log` VALUES (65, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 26, '2019-04-04 08:52:15');
INSERT INTO `schedule_job_log` VALUES (66, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 12, '2019-04-04 08:52:30');
INSERT INTO `schedule_job_log` VALUES (67, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-04 08:53:00');
INSERT INTO `schedule_job_log` VALUES (68, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-04 08:53:15');
INSERT INTO `schedule_job_log` VALUES (69, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 73, '2019-04-04 08:53:30');
INSERT INTO `schedule_job_log` VALUES (70, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 134, '2019-04-04 09:07:15');
INSERT INTO `schedule_job_log` VALUES (71, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 26, '2019-04-04 09:07:30');
INSERT INTO `schedule_job_log` VALUES (72, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 30, '2019-04-04 09:08:00');
INSERT INTO `schedule_job_log` VALUES (73, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 31, '2019-04-04 09:08:15');
INSERT INTO `schedule_job_log` VALUES (74, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 184, '2019-04-04 09:08:30');
INSERT INTO `schedule_job_log` VALUES (75, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 31, '2019-04-04 09:09:00');
INSERT INTO `schedule_job_log` VALUES (76, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 92, '2019-04-04 09:09:15');
INSERT INTO `schedule_job_log` VALUES (77, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-04 09:09:30');
INSERT INTO `schedule_job_log` VALUES (78, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-04 09:10:00');
INSERT INTO `schedule_job_log` VALUES (79, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-04 09:10:15');
INSERT INTO `schedule_job_log` VALUES (80, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 63, '2019-04-04 09:10:30');
INSERT INTO `schedule_job_log` VALUES (81, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-04 09:11:00');
INSERT INTO `schedule_job_log` VALUES (82, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-04 09:11:15');
INSERT INTO `schedule_job_log` VALUES (83, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-04 09:11:30');
INSERT INTO `schedule_job_log` VALUES (84, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-04 09:12:00');
INSERT INTO `schedule_job_log` VALUES (85, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-04 09:12:15');
INSERT INTO `schedule_job_log` VALUES (86, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 69, '2019-04-04 09:12:30');
INSERT INTO `schedule_job_log` VALUES (87, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-04 09:13:00');
INSERT INTO `schedule_job_log` VALUES (88, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-04 09:13:15');
INSERT INTO `schedule_job_log` VALUES (89, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-04 09:13:30');
INSERT INTO `schedule_job_log` VALUES (90, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-04 09:14:00');
INSERT INTO `schedule_job_log` VALUES (91, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-04 09:14:15');
INSERT INTO `schedule_job_log` VALUES (92, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 32, '2019-04-04 09:14:30');
INSERT INTO `schedule_job_log` VALUES (93, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-04 09:15:00');
INSERT INTO `schedule_job_log` VALUES (94, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-04 09:19:00');
INSERT INTO `schedule_job_log` VALUES (95, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 112, '2019-04-04 09:19:15');
INSERT INTO `schedule_job_log` VALUES (96, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-04 09:19:30');
INSERT INTO `schedule_job_log` VALUES (97, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-04 09:20:00');
INSERT INTO `schedule_job_log` VALUES (98, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-04 09:20:15');
INSERT INTO `schedule_job_log` VALUES (99, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 149, '2019-04-04 09:20:30');
INSERT INTO `schedule_job_log` VALUES (100, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-04 09:21:00');
INSERT INTO `schedule_job_log` VALUES (101, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-04 09:21:15');
INSERT INTO `schedule_job_log` VALUES (102, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 105, '2019-04-04 11:34:15');
INSERT INTO `schedule_job_log` VALUES (103, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 145, '2019-04-04 11:34:30');
INSERT INTO `schedule_job_log` VALUES (104, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-04 11:35:00');
INSERT INTO `schedule_job_log` VALUES (105, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 28, '2019-04-04 11:35:15');
INSERT INTO `schedule_job_log` VALUES (106, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 66, '2019-04-04 11:35:30');
INSERT INTO `schedule_job_log` VALUES (107, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-04 11:36:00');
INSERT INTO `schedule_job_log` VALUES (108, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-04 11:36:15');
INSERT INTO `schedule_job_log` VALUES (109, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 31, '2019-04-04 11:36:30');
INSERT INTO `schedule_job_log` VALUES (110, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 226, '2019-04-04 20:48:15');
INSERT INTO `schedule_job_log` VALUES (111, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 184, '2019-04-04 20:48:30');
INSERT INTO `schedule_job_log` VALUES (112, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 251, '2019-04-04 22:27:30');
INSERT INTO `schedule_job_log` VALUES (113, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-04 22:28:00');
INSERT INTO `schedule_job_log` VALUES (114, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 83, '2019-04-04 22:28:15');
INSERT INTO `schedule_job_log` VALUES (115, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 110, '2019-04-04 23:18:15');
INSERT INTO `schedule_job_log` VALUES (116, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 142, '2019-04-04 23:18:30');
INSERT INTO `schedule_job_log` VALUES (117, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-04 23:19:00');
INSERT INTO `schedule_job_log` VALUES (118, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-04 23:19:15');
INSERT INTO `schedule_job_log` VALUES (119, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 48, '2019-04-04 23:19:30');
INSERT INTO `schedule_job_log` VALUES (120, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-04 23:20:00');
INSERT INTO `schedule_job_log` VALUES (121, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 30, '2019-04-04 23:20:15');
INSERT INTO `schedule_job_log` VALUES (122, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 73, '2019-04-04 23:20:30');
INSERT INTO `schedule_job_log` VALUES (123, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 255, '2019-04-05 04:03:30');
INSERT INTO `schedule_job_log` VALUES (124, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 108, '2019-04-05 04:07:15');
INSERT INTO `schedule_job_log` VALUES (125, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 248, '2019-04-05 04:07:30');
INSERT INTO `schedule_job_log` VALUES (126, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-05 04:08:00');
INSERT INTO `schedule_job_log` VALUES (127, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-05 04:08:15');
INSERT INTO `schedule_job_log` VALUES (128, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 30, '2019-04-05 04:08:30');
INSERT INTO `schedule_job_log` VALUES (129, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-05 04:09:00');
INSERT INTO `schedule_job_log` VALUES (130, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-05 04:09:15');
INSERT INTO `schedule_job_log` VALUES (131, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-05 04:09:30');
INSERT INTO `schedule_job_log` VALUES (132, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 56, '2019-04-05 04:13:00');
INSERT INTO `schedule_job_log` VALUES (133, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-05 04:13:15');
INSERT INTO `schedule_job_log` VALUES (134, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 33, '2019-04-05 04:16:00');
INSERT INTO `schedule_job_log` VALUES (135, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-05 04:16:15');
INSERT INTO `schedule_job_log` VALUES (136, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 39, '2019-04-05 04:20:00');
INSERT INTO `schedule_job_log` VALUES (137, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-05 04:20:15');
INSERT INTO `schedule_job_log` VALUES (138, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 829, '2019-04-05 04:20:30');
INSERT INTO `schedule_job_log` VALUES (139, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 40, '2019-04-05 04:21:00');
INSERT INTO `schedule_job_log` VALUES (140, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-05 04:21:15');
INSERT INTO `schedule_job_log` VALUES (141, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-05 04:21:30');
INSERT INTO `schedule_job_log` VALUES (142, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-05 04:22:00');
INSERT INTO `schedule_job_log` VALUES (143, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-05 04:22:15');
INSERT INTO `schedule_job_log` VALUES (144, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-05 04:22:30');
INSERT INTO `schedule_job_log` VALUES (145, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 59, '2019-04-05 04:23:00');
INSERT INTO `schedule_job_log` VALUES (146, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-05 04:23:15');
INSERT INTO `schedule_job_log` VALUES (147, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-05 04:23:30');
INSERT INTO `schedule_job_log` VALUES (148, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-05 04:24:00');
INSERT INTO `schedule_job_log` VALUES (149, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-05 04:24:15');
INSERT INTO `schedule_job_log` VALUES (150, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-05 04:24:30');
INSERT INTO `schedule_job_log` VALUES (151, 22, 'fstPointIncreaseJob', 'doJob', '1', 0, NULL, 66, '2019-04-05 04:24:40');
INSERT INTO `schedule_job_log` VALUES (152, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-05 04:25:00');
INSERT INTO `schedule_job_log` VALUES (153, 23, 'specialPriceJob', 'doJob', '0', 0, NULL, 19, '2019-04-05 04:25:10');
INSERT INTO `schedule_job_log` VALUES (154, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 77, '2019-04-05 04:25:15');
INSERT INTO `schedule_job_log` VALUES (155, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-05 04:25:30');
INSERT INTO `schedule_job_log` VALUES (156, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 44, '2019-04-05 04:26:00');
INSERT INTO `schedule_job_log` VALUES (157, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 11, '2019-04-05 04:26:15');
INSERT INTO `schedule_job_log` VALUES (158, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-05 04:26:30');
INSERT INTO `schedule_job_log` VALUES (159, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 33, '2019-04-05 04:27:00');
INSERT INTO `schedule_job_log` VALUES (160, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-05 04:27:15');
INSERT INTO `schedule_job_log` VALUES (161, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-05 04:27:30');
INSERT INTO `schedule_job_log` VALUES (162, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 59, '2019-04-05 04:28:00');
INSERT INTO `schedule_job_log` VALUES (163, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 8, '2019-04-05 04:28:15');
INSERT INTO `schedule_job_log` VALUES (164, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 9, '2019-04-05 04:28:30');
INSERT INTO `schedule_job_log` VALUES (165, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-05 04:29:00');
INSERT INTO `schedule_job_log` VALUES (166, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-05 04:29:15');
INSERT INTO `schedule_job_log` VALUES (167, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 126, '2019-04-05 04:34:00');
INSERT INTO `schedule_job_log` VALUES (168, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 96, '2019-04-05 04:34:15');
INSERT INTO `schedule_job_log` VALUES (169, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 164, '2019-04-05 05:36:15');
INSERT INTO `schedule_job_log` VALUES (170, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-05 05:36:30');
INSERT INTO `schedule_job_log` VALUES (171, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-05 05:37:30');
INSERT INTO `schedule_job_log` VALUES (172, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-05 05:38:00');
INSERT INTO `schedule_job_log` VALUES (173, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 102, '2019-04-05 05:38:15');
INSERT INTO `schedule_job_log` VALUES (174, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-05 05:38:30');
INSERT INTO `schedule_job_log` VALUES (175, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 82, '2019-04-05 05:40:00');
INSERT INTO `schedule_job_log` VALUES (176, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 64, '2019-04-05 05:40:15');
INSERT INTO `schedule_job_log` VALUES (177, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-05 05:40:30');
INSERT INTO `schedule_job_log` VALUES (178, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 132, '2019-04-05 05:57:00');
INSERT INTO `schedule_job_log` VALUES (179, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-05 05:58:00');
INSERT INTO `schedule_job_log` VALUES (180, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 135, '2019-04-05 05:58:15');
INSERT INTO `schedule_job_log` VALUES (181, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 22, '2019-04-05 05:58:30');
INSERT INTO `schedule_job_log` VALUES (182, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 160, '2019-04-07 04:57:00');
INSERT INTO `schedule_job_log` VALUES (183, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 208, '2019-04-07 04:57:15');
INSERT INTO `schedule_job_log` VALUES (184, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 575, '2019-04-07 04:57:30');
INSERT INTO `schedule_job_log` VALUES (185, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 42, '2019-04-07 04:58:00');
INSERT INTO `schedule_job_log` VALUES (186, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 69, '2019-04-07 04:58:15');
INSERT INTO `schedule_job_log` VALUES (187, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 104, '2019-04-07 04:58:30');
INSERT INTO `schedule_job_log` VALUES (188, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 35, '2019-04-07 04:59:00');
INSERT INTO `schedule_job_log` VALUES (189, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 41, '2019-04-07 04:59:15');
INSERT INTO `schedule_job_log` VALUES (190, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 37, '2019-04-07 04:59:30');
INSERT INTO `schedule_job_log` VALUES (191, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 31, '2019-04-07 05:00:00');
INSERT INTO `schedule_job_log` VALUES (192, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 42, '2019-04-07 05:00:15');
INSERT INTO `schedule_job_log` VALUES (193, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 39, '2019-04-07 05:00:30');
INSERT INTO `schedule_job_log` VALUES (194, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 52, '2019-04-07 05:01:00');
INSERT INTO `schedule_job_log` VALUES (195, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-07 05:01:15');
INSERT INTO `schedule_job_log` VALUES (196, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 31, '2019-04-07 05:01:30');
INSERT INTO `schedule_job_log` VALUES (197, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 192, '2019-04-07 05:02:00');
INSERT INTO `schedule_job_log` VALUES (198, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 55, '2019-04-07 05:02:15');
INSERT INTO `schedule_job_log` VALUES (199, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 37, '2019-04-07 05:02:30');
INSERT INTO `schedule_job_log` VALUES (200, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 40, '2019-04-07 05:03:00');
INSERT INTO `schedule_job_log` VALUES (201, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 27, '2019-04-07 05:03:15');
INSERT INTO `schedule_job_log` VALUES (202, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 43, '2019-04-07 05:03:30');
INSERT INTO `schedule_job_log` VALUES (203, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 05:04:00');
INSERT INTO `schedule_job_log` VALUES (204, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 32, '2019-04-07 05:04:15');
INSERT INTO `schedule_job_log` VALUES (205, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 47, '2019-04-07 05:04:30');
INSERT INTO `schedule_job_log` VALUES (206, 22, 'fstPointIncreaseJob', 'doJob', '1', 0, NULL, 15, '2019-04-07 05:04:40');
INSERT INTO `schedule_job_log` VALUES (207, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 35, '2019-04-07 05:05:00');
INSERT INTO `schedule_job_log` VALUES (208, 23, 'specialPriceJob', 'doJob', '0', 0, NULL, 10, '2019-04-07 05:05:10');
INSERT INTO `schedule_job_log` VALUES (209, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 60, '2019-04-07 05:05:15');
INSERT INTO `schedule_job_log` VALUES (210, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 49, '2019-04-07 05:05:30');
INSERT INTO `schedule_job_log` VALUES (211, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 05:06:00');
INSERT INTO `schedule_job_log` VALUES (212, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 49, '2019-04-07 05:06:15');
INSERT INTO `schedule_job_log` VALUES (213, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-07 05:06:30');
INSERT INTO `schedule_job_log` VALUES (214, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 32, '2019-04-07 05:07:00');
INSERT INTO `schedule_job_log` VALUES (215, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 44, '2019-04-07 05:07:15');
INSERT INTO `schedule_job_log` VALUES (216, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 149, '2019-04-07 05:07:30');
INSERT INTO `schedule_job_log` VALUES (217, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 34, '2019-04-07 05:08:00');
INSERT INTO `schedule_job_log` VALUES (218, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 43, '2019-04-07 05:08:15');
INSERT INTO `schedule_job_log` VALUES (219, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 22, '2019-04-07 05:08:30');
INSERT INTO `schedule_job_log` VALUES (220, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 56, '2019-04-07 05:09:00');
INSERT INTO `schedule_job_log` VALUES (221, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-07 05:09:15');
INSERT INTO `schedule_job_log` VALUES (222, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-07 05:09:30');
INSERT INTO `schedule_job_log` VALUES (223, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 27, '2019-04-07 05:10:00');
INSERT INTO `schedule_job_log` VALUES (224, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 41, '2019-04-07 05:10:15');
INSERT INTO `schedule_job_log` VALUES (225, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 05:10:30');
INSERT INTO `schedule_job_log` VALUES (226, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 27, '2019-04-07 05:11:00');
INSERT INTO `schedule_job_log` VALUES (227, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-07 05:11:15');
INSERT INTO `schedule_job_log` VALUES (228, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-07 05:11:30');
INSERT INTO `schedule_job_log` VALUES (229, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 05:12:00');
INSERT INTO `schedule_job_log` VALUES (230, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 60, '2019-04-07 05:12:15');
INSERT INTO `schedule_job_log` VALUES (231, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 05:12:30');
INSERT INTO `schedule_job_log` VALUES (232, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-07 05:13:00');
INSERT INTO `schedule_job_log` VALUES (233, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 55, '2019-04-07 05:13:15');
INSERT INTO `schedule_job_log` VALUES (234, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 05:13:30');
INSERT INTO `schedule_job_log` VALUES (235, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 11, '2019-04-07 05:14:00');
INSERT INTO `schedule_job_log` VALUES (236, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 34, '2019-04-07 05:14:15');
INSERT INTO `schedule_job_log` VALUES (237, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-07 05:14:30');
INSERT INTO `schedule_job_log` VALUES (238, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 27, '2019-04-07 05:15:00');
INSERT INTO `schedule_job_log` VALUES (239, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 05:15:15');
INSERT INTO `schedule_job_log` VALUES (240, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-07 05:15:30');
INSERT INTO `schedule_job_log` VALUES (241, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 32, '2019-04-07 05:16:00');
INSERT INTO `schedule_job_log` VALUES (242, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 63, '2019-04-07 05:16:15');
INSERT INTO `schedule_job_log` VALUES (243, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-07 05:16:30');
INSERT INTO `schedule_job_log` VALUES (244, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 51, '2019-04-07 05:17:00');
INSERT INTO `schedule_job_log` VALUES (245, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 30, '2019-04-07 05:17:15');
INSERT INTO `schedule_job_log` VALUES (246, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 35, '2019-04-07 05:31:30');
INSERT INTO `schedule_job_log` VALUES (247, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 86, '2019-04-07 05:32:00');
INSERT INTO `schedule_job_log` VALUES (248, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 351, '2019-04-07 05:32:15');
INSERT INTO `schedule_job_log` VALUES (249, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-07 05:32:30');
INSERT INTO `schedule_job_log` VALUES (250, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 91, '2019-04-07 05:35:00');
INSERT INTO `schedule_job_log` VALUES (251, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 146, '2019-04-07 05:35:15');
INSERT INTO `schedule_job_log` VALUES (252, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 44, '2019-04-07 05:35:30');
INSERT INTO `schedule_job_log` VALUES (253, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 54, '2019-04-07 05:36:00');
INSERT INTO `schedule_job_log` VALUES (254, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 61, '2019-04-07 05:36:15');
INSERT INTO `schedule_job_log` VALUES (255, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 36, '2019-04-07 05:36:30');
INSERT INTO `schedule_job_log` VALUES (256, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 31, '2019-04-07 05:37:00');
INSERT INTO `schedule_job_log` VALUES (257, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 68, '2019-04-07 05:41:00');
INSERT INTO `schedule_job_log` VALUES (258, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 247, '2019-04-07 05:41:15');
INSERT INTO `schedule_job_log` VALUES (259, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 35, '2019-04-07 05:41:30');
INSERT INTO `schedule_job_log` VALUES (260, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 30, '2019-04-07 05:42:00');
INSERT INTO `schedule_job_log` VALUES (261, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 67, '2019-04-07 05:42:15');
INSERT INTO `schedule_job_log` VALUES (262, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 51, '2019-04-07 05:42:30');
INSERT INTO `schedule_job_log` VALUES (263, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 54, '2019-04-07 05:44:00');
INSERT INTO `schedule_job_log` VALUES (264, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 162, '2019-04-07 05:44:15');
INSERT INTO `schedule_job_log` VALUES (265, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 102, '2019-04-07 05:44:30');
INSERT INTO `schedule_job_log` VALUES (266, 22, 'fstPointIncreaseJob', 'doJob', '1', 0, NULL, 49, '2019-04-07 05:44:40');
INSERT INTO `schedule_job_log` VALUES (267, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 51, '2019-04-07 05:45:00');
INSERT INTO `schedule_job_log` VALUES (268, 23, 'specialPriceJob', 'doJob', '0', 0, NULL, 12, '2019-04-07 05:45:10');
INSERT INTO `schedule_job_log` VALUES (269, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 34, '2019-04-07 05:45:15');
INSERT INTO `schedule_job_log` VALUES (270, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 122, '2019-04-07 05:45:30');
INSERT INTO `schedule_job_log` VALUES (271, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 54, '2019-04-07 05:48:30');
INSERT INTO `schedule_job_log` VALUES (272, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 35, '2019-04-07 05:49:00');
INSERT INTO `schedule_job_log` VALUES (273, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 193, '2019-04-07 05:49:15');
INSERT INTO `schedule_job_log` VALUES (274, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 65, '2019-04-07 05:49:30');
INSERT INTO `schedule_job_log` VALUES (275, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-07 05:50:00');
INSERT INTO `schedule_job_log` VALUES (276, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 90, '2019-04-07 05:50:15');
INSERT INTO `schedule_job_log` VALUES (277, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 05:50:30');
INSERT INTO `schedule_job_log` VALUES (278, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 85, '2019-04-07 05:51:00');
INSERT INTO `schedule_job_log` VALUES (279, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 56, '2019-04-07 05:51:15');
INSERT INTO `schedule_job_log` VALUES (280, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 209, '2019-04-07 05:51:30');
INSERT INTO `schedule_job_log` VALUES (281, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 35, '2019-04-07 05:52:00');
INSERT INTO `schedule_job_log` VALUES (282, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 26, '2019-04-07 05:52:15');
INSERT INTO `schedule_job_log` VALUES (283, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 86, '2019-04-07 05:54:00');
INSERT INTO `schedule_job_log` VALUES (284, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 289, '2019-04-07 05:54:15');
INSERT INTO `schedule_job_log` VALUES (285, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 120, '2019-04-07 05:54:30');
INSERT INTO `schedule_job_log` VALUES (286, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 53, '2019-04-07 05:55:00');
INSERT INTO `schedule_job_log` VALUES (287, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 22, '2019-04-07 08:21:00');
INSERT INTO `schedule_job_log` VALUES (288, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 104, '2019-04-07 08:21:15');
INSERT INTO `schedule_job_log` VALUES (289, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-07 08:21:30');
INSERT INTO `schedule_job_log` VALUES (290, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-07 08:22:00');
INSERT INTO `schedule_job_log` VALUES (291, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-07 08:22:15');
INSERT INTO `schedule_job_log` VALUES (292, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 111, '2019-04-07 08:24:15');
INSERT INTO `schedule_job_log` VALUES (293, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 08:24:30');
INSERT INTO `schedule_job_log` VALUES (294, 22, 'fstPointIncreaseJob', 'doJob', '1', 0, NULL, 49, '2019-04-07 08:24:40');
INSERT INTO `schedule_job_log` VALUES (295, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 08:25:00');
INSERT INTO `schedule_job_log` VALUES (296, 23, 'specialPriceJob', 'doJob', '0', 0, NULL, 13, '2019-04-07 08:25:10');
INSERT INTO `schedule_job_log` VALUES (297, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-07 08:25:15');
INSERT INTO `schedule_job_log` VALUES (298, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 08:25:30');
INSERT INTO `schedule_job_log` VALUES (299, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 08:26:00');
INSERT INTO `schedule_job_log` VALUES (300, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 08:26:15');
INSERT INTO `schedule_job_log` VALUES (301, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 08:26:30');
INSERT INTO `schedule_job_log` VALUES (302, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 08:27:00');
INSERT INTO `schedule_job_log` VALUES (303, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 08:27:15');
INSERT INTO `schedule_job_log` VALUES (304, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 25, '2019-04-07 08:27:30');
INSERT INTO `schedule_job_log` VALUES (305, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-07 08:28:00');
INSERT INTO `schedule_job_log` VALUES (306, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 08:28:15');
INSERT INTO `schedule_job_log` VALUES (307, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-07 08:28:30');
INSERT INTO `schedule_job_log` VALUES (308, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 08:29:00');
INSERT INTO `schedule_job_log` VALUES (309, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 08:29:15');
INSERT INTO `schedule_job_log` VALUES (310, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 08:29:30');
INSERT INTO `schedule_job_log` VALUES (311, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-07 08:30:00');
INSERT INTO `schedule_job_log` VALUES (312, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 08:30:15');
INSERT INTO `schedule_job_log` VALUES (313, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 08:30:30');
INSERT INTO `schedule_job_log` VALUES (314, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-07 08:31:00');
INSERT INTO `schedule_job_log` VALUES (315, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 08:31:15');
INSERT INTO `schedule_job_log` VALUES (316, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 08:31:30');
INSERT INTO `schedule_job_log` VALUES (317, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 11, '2019-04-07 08:32:00');
INSERT INTO `schedule_job_log` VALUES (318, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-07 08:35:00');
INSERT INTO `schedule_job_log` VALUES (319, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 84, '2019-04-07 08:35:15');
INSERT INTO `schedule_job_log` VALUES (320, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 08:35:30');
INSERT INTO `schedule_job_log` VALUES (321, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-07 08:36:00');
INSERT INTO `schedule_job_log` VALUES (322, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 27, '2019-04-07 08:36:15');
INSERT INTO `schedule_job_log` VALUES (323, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 08:36:30');
INSERT INTO `schedule_job_log` VALUES (324, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 73, '2019-04-07 08:39:15');
INSERT INTO `schedule_job_log` VALUES (325, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 08:39:30');
INSERT INTO `schedule_job_log` VALUES (326, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 22, '2019-04-07 08:40:00');
INSERT INTO `schedule_job_log` VALUES (327, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 08:40:15');
INSERT INTO `schedule_job_log` VALUES (328, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 12, '2019-04-07 08:40:30');
INSERT INTO `schedule_job_log` VALUES (329, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-07 08:41:00');
INSERT INTO `schedule_job_log` VALUES (330, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 08:41:15');
INSERT INTO `schedule_job_log` VALUES (331, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-07 08:41:30');
INSERT INTO `schedule_job_log` VALUES (332, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-07 08:42:00');
INSERT INTO `schedule_job_log` VALUES (333, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 08:42:15');
INSERT INTO `schedule_job_log` VALUES (334, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 08:42:30');
INSERT INTO `schedule_job_log` VALUES (335, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-07 08:43:00');
INSERT INTO `schedule_job_log` VALUES (336, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 08:43:15');
INSERT INTO `schedule_job_log` VALUES (337, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 9, '2019-04-07 08:43:30');
INSERT INTO `schedule_job_log` VALUES (338, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-07 08:44:00');
INSERT INTO `schedule_job_log` VALUES (339, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 22, '2019-04-07 08:44:15');
INSERT INTO `schedule_job_log` VALUES (340, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 10, '2019-04-07 08:44:30');
INSERT INTO `schedule_job_log` VALUES (341, 22, 'fstPointIncreaseJob', 'doJob', '1', 0, NULL, 21, '2019-04-07 08:44:40');
INSERT INTO `schedule_job_log` VALUES (342, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 08:45:00');
INSERT INTO `schedule_job_log` VALUES (343, 23, 'specialPriceJob', 'doJob', '0', 0, NULL, 5, '2019-04-07 08:45:10');
INSERT INTO `schedule_job_log` VALUES (344, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-07 08:45:15');
INSERT INTO `schedule_job_log` VALUES (345, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 11, '2019-04-07 08:45:30');
INSERT INTO `schedule_job_log` VALUES (346, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-07 08:46:00');
INSERT INTO `schedule_job_log` VALUES (347, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 08:46:15');
INSERT INTO `schedule_job_log` VALUES (348, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 12, '2019-04-07 08:46:30');
INSERT INTO `schedule_job_log` VALUES (349, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 08:47:00');
INSERT INTO `schedule_job_log` VALUES (350, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 12, '2019-04-07 08:47:15');
INSERT INTO `schedule_job_log` VALUES (351, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 11, '2019-04-07 08:47:30');
INSERT INTO `schedule_job_log` VALUES (352, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 9, '2019-04-07 08:48:00');
INSERT INTO `schedule_job_log` VALUES (353, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 08:48:15');
INSERT INTO `schedule_job_log` VALUES (354, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 08:48:30');
INSERT INTO `schedule_job_log` VALUES (355, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 08:50:30');
INSERT INTO `schedule_job_log` VALUES (356, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-07 08:51:00');
INSERT INTO `schedule_job_log` VALUES (357, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 128, '2019-04-07 08:51:15');
INSERT INTO `schedule_job_log` VALUES (358, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 08:51:30');
INSERT INTO `schedule_job_log` VALUES (359, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-07 08:52:00');
INSERT INTO `schedule_job_log` VALUES (360, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-07 08:52:15');
INSERT INTO `schedule_job_log` VALUES (361, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 08:52:30');
INSERT INTO `schedule_job_log` VALUES (362, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 150, '2019-04-07 08:55:15');
INSERT INTO `schedule_job_log` VALUES (363, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 08:55:30');
INSERT INTO `schedule_job_log` VALUES (364, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 08:56:00');
INSERT INTO `schedule_job_log` VALUES (365, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 31, '2019-04-07 08:56:15');
INSERT INTO `schedule_job_log` VALUES (366, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 08:56:30');
INSERT INTO `schedule_job_log` VALUES (367, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 08:57:00');
INSERT INTO `schedule_job_log` VALUES (368, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 08:57:15');
INSERT INTO `schedule_job_log` VALUES (369, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 08:57:30');
INSERT INTO `schedule_job_log` VALUES (370, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 08:58:00');
INSERT INTO `schedule_job_log` VALUES (371, 22, 'fstPointIncreaseJob', 'doJob', '1', 0, NULL, 31, '2019-04-07 09:04:40');
INSERT INTO `schedule_job_log` VALUES (372, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 34, '2019-04-07 09:05:00');
INSERT INTO `schedule_job_log` VALUES (373, 23, 'specialPriceJob', 'doJob', '0', 0, NULL, 8, '2019-04-07 09:05:10');
INSERT INTO `schedule_job_log` VALUES (374, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 111, '2019-04-07 09:05:15');
INSERT INTO `schedule_job_log` VALUES (375, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 09:05:30');
INSERT INTO `schedule_job_log` VALUES (376, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 09:06:00');
INSERT INTO `schedule_job_log` VALUES (377, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 09:06:15');
INSERT INTO `schedule_job_log` VALUES (378, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 09:06:30');
INSERT INTO `schedule_job_log` VALUES (379, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-07 09:07:00');
INSERT INTO `schedule_job_log` VALUES (380, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 09:07:15');
INSERT INTO `schedule_job_log` VALUES (381, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 14, '2019-04-07 09:07:30');
INSERT INTO `schedule_job_log` VALUES (382, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 09:08:00');
INSERT INTO `schedule_job_log` VALUES (383, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 09:08:15');
INSERT INTO `schedule_job_log` VALUES (384, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 09:08:30');
INSERT INTO `schedule_job_log` VALUES (385, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 13, '2019-04-07 09:09:00');
INSERT INTO `schedule_job_log` VALUES (386, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 09:09:15');
INSERT INTO `schedule_job_log` VALUES (387, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 09:09:30');
INSERT INTO `schedule_job_log` VALUES (388, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 09:10:00');
INSERT INTO `schedule_job_log` VALUES (389, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 09:10:15');
INSERT INTO `schedule_job_log` VALUES (390, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 09:10:30');
INSERT INTO `schedule_job_log` VALUES (391, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 09:11:00');
INSERT INTO `schedule_job_log` VALUES (392, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-07 09:11:15');
INSERT INTO `schedule_job_log` VALUES (393, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 15, '2019-04-07 09:11:30');
INSERT INTO `schedule_job_log` VALUES (394, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 09:12:00');
INSERT INTO `schedule_job_log` VALUES (395, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 09:12:15');
INSERT INTO `schedule_job_log` VALUES (396, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 09:12:30');
INSERT INTO `schedule_job_log` VALUES (397, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 29, '2019-04-07 09:21:30');
INSERT INTO `schedule_job_log` VALUES (398, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-07 09:22:00');
INSERT INTO `schedule_job_log` VALUES (399, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 132, '2019-04-07 09:22:15');
INSERT INTO `schedule_job_log` VALUES (400, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 20, '2019-04-07 09:22:30');
INSERT INTO `schedule_job_log` VALUES (401, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 17, '2019-04-07 09:23:00');
INSERT INTO `schedule_job_log` VALUES (402, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 28, '2019-04-07 09:23:15');
INSERT INTO `schedule_job_log` VALUES (403, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 09:23:30');
INSERT INTO `schedule_job_log` VALUES (404, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 23, '2019-04-07 09:24:00');
INSERT INTO `schedule_job_log` VALUES (405, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 19, '2019-04-07 09:24:15');
INSERT INTO `schedule_job_log` VALUES (406, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 18, '2019-04-07 09:24:30');
INSERT INTO `schedule_job_log` VALUES (407, 22, 'fstPointIncreaseJob', 'doJob', '1', 0, NULL, 23, '2019-04-07 09:24:40');
INSERT INTO `schedule_job_log` VALUES (408, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 21, '2019-04-07 09:25:00');
INSERT INTO `schedule_job_log` VALUES (409, 23, 'specialPriceJob', 'doJob', '0', 0, NULL, 9, '2019-04-07 09:25:10');
INSERT INTO `schedule_job_log` VALUES (410, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 16, '2019-04-07 09:25:15');
INSERT INTO `schedule_job_log` VALUES (411, 2, 'userDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-07 10:18:00');
INSERT INTO `schedule_job_log` VALUES (412, 1, 'gameUserToDBJob', 'doJob', '0', 0, NULL, 93, '2019-04-07 10:18:15');
INSERT INTO `schedule_job_log` VALUES (413, 3, 'serverDataToDBJob', 'doJob', '0', 0, NULL, 24, '2019-04-07 10:18:30');
COMMIT;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `param_key` varchar(50) DEFAULT NULL COMMENT 'key',
  `param_value` varchar(2000) DEFAULT NULL COMMENT 'value',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态   0：隐藏   1：显示',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `param_key` (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置信息表';

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级部门ID，一级部门为0',
  `name` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '部门名称',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '是否删除  -1：已删除  0：正常',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='部门管理';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` VALUES (1, 0, '竹风软件', 0, 0);
INSERT INTO `sys_dept` VALUES (2, 1, '技术部', 1, 0);
INSERT INTO `sys_dept` VALUES (3, 1, '运营部', 2, 0);
INSERT INTO `sys_dept` VALUES (4, 3, '技术部', 0, -1);
INSERT INTO `sys_dept` VALUES (5, 3, '销售部', 1, -1);
INSERT INTO `sys_dept` VALUES (6, 0, '专服渠道', 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '字典名称',
  `type` varchar(100) NOT NULL COMMENT '字典类型',
  `code` varchar(100) NOT NULL COMMENT '字典码',
  `value` varchar(1000) NOT NULL COMMENT '字典值',
  `order_num` int(11) DEFAULT '0' COMMENT '排序',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '删除标记  -1：已删除  0：正常',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type` (`type`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典表';

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) DEFAULT NULL COMMENT '请求参数',
  `time` bigint(20) NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='系统日志';

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int(11) DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8 COMMENT='菜单管理';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` VALUES (1, 0, '系统管理', NULL, NULL, 0, 'fa fa-cog', 10);
INSERT INTO `sys_menu` VALUES (2, 1, '管理员管理', 'modules/sys/user.html', NULL, 1, 'fa fa-user', 1);
INSERT INTO `sys_menu` VALUES (3, 1, '角色管理', 'modules/sys/role.html', NULL, 1, 'fa fa-user-secret', 2);
INSERT INTO `sys_menu` VALUES (4, 1, '菜单管理', 'modules/sys/menu.html', NULL, 1, 'fa fa-th-list', 3);
INSERT INTO `sys_menu` VALUES (5, 1, 'SQL监控', 'druid/sql.html', NULL, 1, 'fa fa-bug', 4);
INSERT INTO `sys_menu` VALUES (6, 1, '定时任务', 'modules/job/schedule.html', NULL, 1, 'fa fa-tasks', 5);
INSERT INTO `sys_menu` VALUES (7, 6, '查看', NULL, 'sys:schedule:list,sys:schedule:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (8, 6, '新增', NULL, 'sys:schedule:save', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (9, 6, '修改', NULL, 'sys:schedule:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (10, 6, '删除', NULL, 'sys:schedule:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (11, 6, '暂停', NULL, 'sys:schedule:pause', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (12, 6, '恢复', NULL, 'sys:schedule:resume', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (13, 6, '立即执行', NULL, 'sys:schedule:run', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (14, 6, '日志列表', NULL, 'sys:schedule:log', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (15, 2, '查看', NULL, 'sys:user:list,sys:user:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (16, 2, '新增', NULL, 'sys:user:save,sys:role:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (17, 2, '修改', NULL, 'sys:user:update,sys:role:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (18, 2, '删除', NULL, 'sys:user:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (19, 3, '查看', NULL, 'sys:role:list,sys:role:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (20, 3, '新增', NULL, 'sys:role:save,sys:menu:perms', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (21, 3, '修改', NULL, 'sys:role:update,sys:menu:perms', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (22, 3, '删除', NULL, 'sys:role:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (23, 4, '查看', NULL, 'sys:menu:list,sys:menu:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (24, 4, '新增', NULL, 'sys:menu:save,sys:menu:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (25, 4, '修改', NULL, 'sys:menu:update,sys:menu:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (26, 4, '删除', NULL, 'sys:menu:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (27, 1, '参数管理', 'modules/sys/config.html', 'sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete', 1, 'fa fa-sun-o', 6);
INSERT INTO `sys_menu` VALUES (29, 1, '系统日志', 'modules/sys/log.html', 'sys:log:list', 1, 'fa fa-file-text-o', 7);
INSERT INTO `sys_menu` VALUES (30, 1, '文件上传', 'modules/oss/oss.html', 'sys:oss:all', 1, 'fa fa-file-image-o', 6);
INSERT INTO `sys_menu` VALUES (31, 1, '部门管理', 'modules/sys/dept.html', NULL, 1, 'fa fa-file-code-o', 1);
INSERT INTO `sys_menu` VALUES (32, 31, '查看', NULL, 'sys:dept:list,sys:dept:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (33, 31, '新增', NULL, 'sys:dept:save,sys:dept:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (34, 31, '修改', NULL, 'sys:dept:update,sys:dept:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (35, 31, '删除', NULL, 'sys:dept:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (36, 1, '字典管理', 'modules/sys/dict.html', NULL, 1, 'fa fa-bookmark-o', 6);
INSERT INTO `sys_menu` VALUES (37, 36, '查看', NULL, 'sys:dict:list,sys:dict:info', 2, NULL, 6);
INSERT INTO `sys_menu` VALUES (38, 36, '新增', NULL, 'sys:dict:save', 2, NULL, 6);
INSERT INTO `sys_menu` VALUES (39, 36, '修改', NULL, 'sys:dict:update', 2, NULL, 6);
INSERT INTO `sys_menu` VALUES (40, 36, '删除', NULL, 'sys:dict:delete', 2, NULL, 6);
INSERT INTO `sys_menu` VALUES (41, 0, '玩家信息管理', NULL, NULL, 0, 'fa fa-user', 1);
INSERT INTO `sys_menu` VALUES (42, 41, '玩家信息', 'modules/player/player.html', NULL, 1, NULL, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT 'URL地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='文件上传';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='角色';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` VALUES (1, '客服', NULL, 3, '2018-11-16 09:43:06');
INSERT INTO `sys_role` VALUES (2, '运营日常管理', NULL, 3, '2018-11-29 10:19:17');
INSERT INTO `sys_role` VALUES (3, '渠道运营', NULL, 1, '2019-01-09 16:22:12');
INSERT INTO `sys_role` VALUES (5, '管理员', NULL, 1, '2019-02-22 09:15:09');
INSERT INTO `sys_role` VALUES (6, '三星', NULL, 1, '2019-02-26 10:25:03');
INSERT INTO `sys_role` VALUES (7, '专服运营', NULL, 6, '2019-03-06 13:51:01');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='角色与部门对应关系';

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_dept` VALUES (22, 2, 1);
INSERT INTO `sys_role_dept` VALUES (23, 2, 2);
INSERT INTO `sys_role_dept` VALUES (24, 2, 3);
INSERT INTO `sys_role_dept` VALUES (25, 5, 1);
INSERT INTO `sys_role_dept` VALUES (26, 5, 2);
INSERT INTO `sys_role_dept` VALUES (27, 5, 3);
INSERT INTO `sys_role_dept` VALUES (30, 3, 1);
INSERT INTO `sys_role_dept` VALUES (31, 1, 3);
INSERT INTO `sys_role_dept` VALUES (32, 7, 6);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色与菜单对应关系';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '盐',
  `email` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '手机号',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='系统用户';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 'admin', '8070ab664ab54eef99f8dcbef6228f85d44133e46958470bf19959bdde85396f', 'YzcmCZNvbXocrsz9dm8e', 'lsj@bamboowind.com', '18965615787', 1, 1, '2016-11-11 11:11:11');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户与角色对应关系';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` VALUES (5, 4, 3);
INSERT INTO `sys_user_role` VALUES (6, 5, 2);
INSERT INTO `sys_user_role` VALUES (7, 6, 2);
INSERT INTO `sys_user_role` VALUES (10, 7, 2);
INSERT INTO `sys_user_role` VALUES (11, 2, 2);
INSERT INTO `sys_user_role` VALUES (16, 3, 5);
INSERT INTO `sys_user_role` VALUES (17, 8, 6);
INSERT INTO `sys_user_role` VALUES (20, 9, 7);
COMMIT;

-- ----------------------------
-- Table structure for tb_token
-- ----------------------------
DROP TABLE IF EXISTS `tb_token`;
CREATE TABLE `tb_token` (
  `user_id` bigint(20) NOT NULL,
  `token` varchar(100) NOT NULL COMMENT 'token',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
