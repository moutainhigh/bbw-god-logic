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

 Date: 10/04/2019 18:34:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
INSERT INTO `cfg_task` VALUES (210, '去【商城】任意开一个【卡包】', 20, 1, '[{\"num\":1,\"awardId\":90,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (220, '通过【法宝】使用【定风珠】', 20, 1, '[{\"num\":1,\"awardId\":160,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (230, '在【法宝】中开启【漫步靴】并通过1个路口', 20, 1, '[{\"num\":1,\"awardId\":160,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (240, '通过【城池】【交易】特产盈利12万', 20, 120000, '[{\"num\":1,\"awardId\":80,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (250, '到【城内】将一座【炼宝炉】升1级', 20, 1, '[{\"num\":1,\"awardId\":340,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (260, '在战斗中使用【绝仙剑】', 20, 1, '[{\"num\":1,\"awardId\":340,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (270, '赢得3场练兵', 20, 3, '[{\"num\":1,\"awardId\":250,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (280, '在【封神台】中取得3场胜利', 20, 3, '[{\"num\":1,\"awardId\":205,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (290, '打下3座二级城', 20, 3, '[{\"num\":1,\"awardId\":60,\"item\":60}]', b'1');
INSERT INTO `cfg_task` VALUES (300, '打下1座三级城', 20, 1, '[{\"num\":1,\"star\":4,\"item\":40}]', b'1');
INSERT INTO `cfg_task` VALUES (1100, '累计攻下%d座城池', 50, NULL, NULL, b'1');
INSERT INTO `cfg_task` VALUES (1200, '累计%d座城城内所有建筑升到5级', 50, NULL, NULL, b'1');
INSERT INTO `cfg_task` VALUES (1300, '累计收集%d张卡牌', 50, NULL, NULL, b'1');
INSERT INTO `cfg_task` VALUES (10010, '累计铜钱收益lv*2万', 30, NULL, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10011, '在城市交易中卖掉max(20,lv/5*4)个特产', 30, NULL, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10012, '到游商馆买入20件特产', 30, 20, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10013, '到黑市购买5件法宝', 30, 5, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10014, '到商城购买一次物品', 30, 1, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10015, '到福地收获60元宝', 30, 60, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10016, '在路边捡到1个百宝箱', 30, 1, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10017, '经过1次界碑', 30, 1, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10018, '聚贤庄购买**张卡牌(20级前3次，40级前8次,40级后15)', 30, NULL, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10019, '炼丹房收取**份经验(20级前3次，40级前8次,40级后15)', 30, NULL, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10020, '矿场收取**个元素(20级前5次，40级前16次,40级后60)', 30, NULL, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10021, '炼丹炉收取一个法宝或万能灵石', 30, 1, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10022, '魔王降临时打魔王超过10次', 30, 10, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10023, '练兵胜利**次(20级前3次，20级后5次)', 30, NULL, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10024, '打野怪胜利**次(20级前5次，20级后8次)', 30, NULL, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10025, '帮好友打赢3个怪', 30, 3, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10026, '在封神台取得3场胜利', 30, 3, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10027, '到文王庙求签1次', 30, 1, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10028, '到女娲庙捐赠1次', 30, 1, '[{\"num\":600,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10029, '到迷仙洞探险1次', 30, 1, '[{\"num\":600,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10030, '到太一府进贡特产1次', 30, 1, '[{\"num\":600,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10031, '到鹿台升级卡牌1次', 30, 1, '[{\"num\":600,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10032, '消耗max(1,lv/10)*10个元素升级卡牌', 30, NULL, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10033, '使用max(1,lv/10)次地图法宝', 30, NULL, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10034, '在战斗中使用战斗法宝且获胜', 30, 1, '[{\"num\":800,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10035, '钱庄领取**份税收(20级前3次，40级前8次，,40级后15)', 30, NULL, '[{\"num\":400,\"item\":80}]', b'1');
INSERT INTO `cfg_task` VALUES (10036, '挑战一次诛仙阵', 30, 1, '[{\"num\":800,\"item\":80}]', b'0');
INSERT INTO `cfg_task` VALUES (10110, '完成1个每日任务', 40, 1, NULL, b'1');
INSERT INTO `cfg_task` VALUES (10120, '完成3个每日任务', 40, 3, NULL, b'1');
INSERT INTO `cfg_task` VALUES (10130, '完成5个每日任务', 40, 5, NULL, b'1');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
