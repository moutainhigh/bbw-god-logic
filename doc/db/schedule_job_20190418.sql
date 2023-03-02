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

 Date: 18/04/2019 14:31:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
INSERT INTO `schedule_job` VALUES (5, 'prepareDataJob', 'doJob', '1', '0 0 15 1/7 * ? ', 0, '提前生成配置数据。每周三15点', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (6, 'healthCheck', 'doJob', '1', '0 0 9 * * ? ', 0, '每天9点健康检查。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (9, 'sendActivityRankAwardsJob', 'doJob', '1', '2 1 0 * * ? ', 0, '冲榜奖励。每天0:01:02执行。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (11, 'flxAwardJob', 'doJob', '1', '0 11 0 * * ? ', 0, '福临轩开奖。每天0:11分执行', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (21, 'maouAwardJob', 'doJob', '1', '10 30 12,20 * * ? ', 0, '魔王奖励发放。每天12:30:10和20:30:10秒执行。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (22, 'fstPointIncreaseJob', 'doJob', '0', '40 4/20 * * * ? ', 0, '封神台积分增长。从第4分钟40秒开始，每20分钟执行1次。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (23, 'specialPriceJob', 'doJob', '0', '10 5/20 * * * ? ', 0, '特产涨价。从第5分钟10秒开始，每20分钟执行1次。', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (24, 'activityGenerateJob', 'doJob', '1', '0 10 14 20 * ? ', 0, '活动实例生成,每月20号 14:10执行', '2019-03-31 23:00:00');
INSERT INTO `schedule_job` VALUES (25, 'activityRankGenerateJob', 'doJob', '1', '0 0 14 20 * ? ', 0, '冲榜实例生成,每月20号 14:00执行', '2019-03-31 23:00:00');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
