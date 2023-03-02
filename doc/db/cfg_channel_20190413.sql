/*
 Navicat Premium Data Transfer

 Source Server         : 阿里云_godAdmin
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : rm-wz9bv437rb04kz00n6o.mysql.rds.aliyuncs.com:3306
 Source Schema         : god_game

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 13/04/2019 18:50:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cfg_channel
-- ----------------------------
DROP TABLE IF EXISTS `cfg_channel`;
CREATE TABLE `cfg_channel` (
  `id` int(11) NOT NULL COMMENT '渠道ID。',
  `plat` int(3) NOT NULL COMMENT '渠道标识。服务端使用。',
  `plat_code` varchar(20) NOT NULL COMMENT '渠道编号，客户端使用。',
  `name` char(20) NOT NULL COMMENT '渠道名',
  `support_zf_account` bit(1) NOT NULL,
  `server_group` int(11) NOT NULL DEFAULT '10' COMMENT '服务器组ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道';

-- ----------------------------
-- Records of cfg_channel
-- ----------------------------
BEGIN;
INSERT INTO `cfg_channel` VALUES (0, 0, 'wxmp', '微信公众号', b'1', 0);
INSERT INTO `cfg_channel` VALUES (1, 1, '1', '新服务端测试专用', b'1', 1);
INSERT INTO `cfg_channel` VALUES (10, 10, '10', 'Apple', b'1', 10);
INSERT INTO `cfg_channel` VALUES (11, 11, '11', 'IOS官方完整包', b'1', 10);
INSERT INTO `cfg_channel` VALUES (20, 20, '20', '91', b'1', 10);
INSERT INTO `cfg_channel` VALUES (30, 30, 'ky', '快用', b'1', 10);
INSERT INTO `cfg_channel` VALUES (40, 40, '40', '神话大富翁', b'1', 10);
INSERT INTO `cfg_channel` VALUES (50, 50, '50', '神仙富翁', b'1', 10);
INSERT INTO `cfg_channel` VALUES (60, 60, '60', '富甲封神传(企业版)', b'1', 16);
INSERT INTO `cfg_channel` VALUES (101, 101, '101', '101', b'1', 10);
INSERT INTO `cfg_channel` VALUES (102, 102, '102102', '102', b'1', 10);
INSERT INTO `cfg_channel` VALUES (103, 103, '103', '103', b'1', 10);
INSERT INTO `cfg_channel` VALUES (104, 104, '104', '104', b'1', 10);
INSERT INTO `cfg_channel` VALUES (121, 121, '121', '富甲神魔榜', b'1', 10);
INSERT INTO `cfg_channel` VALUES (122, 122, '122', '2018富甲神魔榜', b'1', 10);
INSERT INTO `cfg_channel` VALUES (201, 201, '000023', '360', b'0', 20);
INSERT INTO `cfg_channel` VALUES (202, 202, '000066', '小米', b'0', 20);
INSERT INTO `cfg_channel` VALUES (203, 203, 'tx', '腾讯', b'0', 20);
INSERT INTO `cfg_channel` VALUES (204, 204, '000002', '机锋', b'0', 20);
INSERT INTO `cfg_channel` VALUES (205, 205, '000007', '91', b'0', 20);
INSERT INTO `cfg_channel` VALUES (206, 206, '000215', '百度多酷', b'0', 20);
INSERT INTO `cfg_channel` VALUES (207, 207, '000003', '当乐', b'0', 20);
INSERT INTO `cfg_channel` VALUES (208, 208, '000116', '豌豆荚', b'0', 20);
INSERT INTO `cfg_channel` VALUES (209, 209, '000020', 'oppo', b'0', 20);
INSERT INTO `cfg_channel` VALUES (210, 210, 'az', '安智', b'0', 20);
INSERT INTO `cfg_channel` VALUES (211, 211, '000008', '木蚂蚁', b'0', 20);
INSERT INTO `cfg_channel` VALUES (212, 212, 'lx', '联想', b'0', 20);
INSERT INTO `cfg_channel` VALUES (213, 213, '000009', '应用汇', b'0', 20);
INSERT INTO `cfg_channel` VALUES (214, 214, '110000', '百度云', b'0', 20);
INSERT INTO `cfg_channel` VALUES (215, 215, '000255', 'UC', b'0', 20);
INSERT INTO `cfg_channel` VALUES (216, 216, '000054', '华为', b'0', 20);
INSERT INTO `cfg_channel` VALUES (217, 217, 'br', '宝软', b'0', 20);
INSERT INTO `cfg_channel` VALUES (218, 218, '000368', '步步高', b'0', 20);
INSERT INTO `cfg_channel` VALUES (219, 219, '000014', '魅族', b'0', 20);
INSERT INTO `cfg_channel` VALUES (220, 220, '000004', 'N多', b'0', 20);
INSERT INTO `cfg_channel` VALUES (221, 221, '160002', '爱贝', b'0', 20);
INSERT INTO `cfg_channel` VALUES (222, 222, 'ls', '乐视', b'1', 20);
INSERT INTO `cfg_channel` VALUES (223, 223, '000800', '搜狗', b'0', 20);
INSERT INTO `cfg_channel` VALUES (224, 224, '000551', 'htc', b'0', 20);
INSERT INTO `cfg_channel` VALUES (225, 225, 'yx', '游讯1', b'1', 20);
INSERT INTO `cfg_channel` VALUES (226, 226, 'yx1', '游讯2', b'1', 20);
INSERT INTO `cfg_channel` VALUES (227, 227, 'yx2', '游讯3', b'1', 20);
INSERT INTO `cfg_channel` VALUES (228, 228, 'yx3', '游讯4', b'1', 20);
INSERT INTO `cfg_channel` VALUES (229, 229, 'yx4', '游讯5', b'1', 20);
INSERT INTO `cfg_channel` VALUES (230, 230, 'yx5', '游讯6', b'1', 20);
INSERT INTO `cfg_channel` VALUES (231, 231, 'gm', '怪猫', b'0', 20);
INSERT INTO `cfg_channel` VALUES (232, 232, '160113', '全民助手', b'0', 20);
INSERT INTO `cfg_channel` VALUES (233, 233, '001145', '金立', b'0', 20);
INSERT INTO `cfg_channel` VALUES (234, 234, '160280', '酷派', b'0', 20);
INSERT INTO `cfg_channel` VALUES (235, 235, '160192', '三星', b'0', 20);
INSERT INTO `cfg_channel` VALUES (236, 236, '000108', '4399', b'0', 20);
INSERT INTO `cfg_channel` VALUES (237, 237, 'kuqu', '酷趣', b'0', 20);
INSERT INTO `cfg_channel` VALUES (238, 238, '000986', '乐嗨嗨', b'0', 20);
INSERT INTO `cfg_channel` VALUES (239, 239, 'ypw', '游品味', b'0', 20);
INSERT INTO `cfg_channel` VALUES (240, 240, '160285', '游戏fan（安久）', b'0', 20);
INSERT INTO `cfg_channel` VALUES (241, 241, '160146', 'tt游戏', b'0', 20);
INSERT INTO `cfg_channel` VALUES (242, 242, '000914', '益玩', b'0', 20);
INSERT INTO `cfg_channel` VALUES (243, 243, '001201', '果盘助手', b'0', 20);
INSERT INTO `cfg_channel` VALUES (244, 244, '160442', '3011.cn', b'0', 20);
INSERT INTO `cfg_channel` VALUES (245, 245, '111226', '虫虫游戏', b'0', 20);
INSERT INTO `cfg_channel` VALUES (246, 246, '160115', '夜神', b'0', 20);
INSERT INTO `cfg_channel` VALUES (247, 247, '160499', '牛刀', b'0', 20);
INSERT INTO `cfg_channel` VALUES (248, 248, '160631', '天宇', b'0', 20);
INSERT INTO `cfg_channel` VALUES (249, 249, '160096', '飓风', b'0', 20);
INSERT INTO `cfg_channel` VALUES (1001, 1001, '1001', '1001', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1002, 1002, '1002', '1002', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1003, 1003, '1003', '1003', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1004, 1004, '1004', '1004', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1005, 1005, '1005', '1005', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1006, 1006, '1006', '1006', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1007, 1007, '1007', '1007', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1008, 1008, '1008', '1008', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1009, 1009, '1009', '1009', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1010, 1010, '1010', '1010', b'1', 20);
INSERT INTO `cfg_channel` VALUES (1011, 1011, '1011', '1011', b'1', 20);
INSERT INTO `cfg_channel` VALUES (10001, 10001, '10001', '10001', b'1', 16);
INSERT INTO `cfg_channel` VALUES (10002, 10002, '10002', 'taptap', b'1', 16);
INSERT INTO `cfg_channel` VALUES (10003, 10003, '10003', '10003', b'1', 16);
INSERT INTO `cfg_channel` VALUES (10004, 10004, '10004', '悟饭', b'1', 16);
INSERT INTO `cfg_channel` VALUES (10005, 10005, '10005', '6kw', b'0', 16);
INSERT INTO `cfg_channel` VALUES (10006, 10006, '10006', '趣玩', b'0', 16);
INSERT INTO `cfg_channel` VALUES (20001, 20001, '20001', '小米-暗黑封神榜(游动)', b'0', 16);
INSERT INTO `cfg_channel` VALUES (22000, 22000, '413', '奇点-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (23000, 23000, '801', '酷畅-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (24000, 24000, '24000', '好游快爆-富甲封神传', b'0', 16);
INSERT INTO `cfg_channel` VALUES (27000, 27000, '27000', '亲娱-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (28000, 28000, '28000', '同步推-富甲封神传', b'0', 16);
INSERT INTO `cfg_channel` VALUES (29000, 29000, '381', '57K-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (31000, 31000, '31000', '小米-神魔幻想(游动)', b'0', 16);
INSERT INTO `cfg_channel` VALUES (32000, 32000, '32000', '小米-神魔召唤师(游动)', b'0', 16);
INSERT INTO `cfg_channel` VALUES (33000, 33000, '664', '天宇-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (34000, 34000, '1030', '九妖游戏-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (35000, 35000, '33', '爱奇艺-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (36000, 36000, '639', '美图-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (37000, 37000, '12', '360-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (38000, 38000, '634', '乐嗨嗨-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (39000, 39000, '15', '小米-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (40000, 40000, '306', '游戏fan-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (41000, 41000, '90', 'tt游戏-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (42000, 42000, '4', '当乐-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (43000, 43000, '14', '百度-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (44000, 44000, '445', '三星-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (45000, 45000, '22', '益玩-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (46000, 46000, '52', '果盘-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (47000, 47000, '27', '4399-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (48000, 48000, '28', '搜狗-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (49000, 49000, '70', '魅族-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (50000, 50000, '131', 'htc-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (51000, 51000, '6', '应用汇-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (52000, 52000, '633', '3011.cn-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (53000, 53000, '159', '夜神-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (54000, 54000, '480', '飓风-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (55000, 55000, '483', '牛刀-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (56000, 56000, '43', '酷派-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (57000, 57000, '102', '虫虫-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (58000, 58000, '476', '捞月狗-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (59000, 59000, '262', '海信-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (60000, 60000, '814', '银联-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (61000, 61000, '133', '游戏群-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (62000, 62000, '146', '乐游-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (63000, 63000, '324', '遥点-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (64000, 64000, '363', '蓝叠-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (65000, 65000, '577', '233手游-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (66000, 66000, '735', '雷神游戏-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (67000, 67000, '1038', '高富帅手游-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (68000, 68000, '886', '偷星猫-富甲封神传', b'0', 20);
INSERT INTO `cfg_channel` VALUES (69000, 69000, '69000', '真特网络-富甲封神传', b'0', 16);
INSERT INTO `cfg_channel` VALUES (70000, 70000, '70000', '壁虎网络-神仙大富翁', b'0', 16);
INSERT INTO `cfg_channel` VALUES (71000, 71000, '71000', '龙游-富甲封神传', b'0', 100);
INSERT INTO `cfg_channel` VALUES (72000, 72000, '72000', '城东微信', b'1', 16);
INSERT INTO `cfg_channel` VALUES (73000, 73000, '73000', '台商qq', b'1', 16);
INSERT INTO `cfg_channel` VALUES (74000, 74000, '74000', '猫耳-富甲封神传', b'0', 110);
INSERT INTO `cfg_channel` VALUES (75000, 75000, '31', '31富甲封神传', b'1', 16);
INSERT INTO `cfg_channel` VALUES (76000, 76000, '19', '富甲封神传(IOS正式版)', b'1', 16);
INSERT INTO `cfg_channel` VALUES (1000001, 2150000, '0002550==', 'UC(无用)', b'0', 20);
INSERT INTO `cfg_channel` VALUES (99000000, 99000000, '99000000', '直充产品', b'1', 16);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
