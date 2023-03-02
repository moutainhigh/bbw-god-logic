-- 活动总览  13420
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100918, 110, 13420, 10, 9, NULL, '活动总览（系列活动）', '世界杯', NULL, NULL, b'1');
-- 超级16强 13430
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100919, 110, 13430, 10, 9, NULL, '超级16强', '世界杯', NULL, NULL, b'1');
-- 决战8强 13440
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100920, 110, 13440, 10, 9, NULL, '决战8强', '世界杯', NULL, NULL, b'1');
-- 我是预言家 13450
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100921, 110, 13450, 10, 9, NULL, '我是预言家', '世界杯', NULL, NULL, b'1');
-- 我是竞猜王 13460
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100922, 110, 13460, 10, 9, NULL, '我是竞猜王', '世界杯', NULL, NULL, b'1');
-- 竞猜任务 13470
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100923, 110, 13470, 10, 9, NULL, '竞猜任务', '世界杯', NULL, NULL, b'1');
-- 竞猜商店 13480
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100924, 110, 13480, 10, 9, NULL, '竞猜商店', '世界杯', NULL, NULL, b'1');
-- 竞猜跨服冲榜11170 日榜
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111400, 11170, 10, 11170, '竞猜榜日榜第1名', '[{\"awardId\":11410,\"item\":60,\"num\":1},{\"awardId\":10180,\"item\":60,\"num\":35},{\"awardId\":10020,\"item\":60,\"num\":30}]', 1, 1, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111401, 11170, 10, 11170, '竞猜榜日榜第2名', '[{\"awardId\":11060,\"item\":60,\"num\":1},{\"awardId\":10180,\"item\":60,\"num\":25},{\"awardId\":10020,\"item\":60,\"num\":20}]', 2, 2, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111402, 11170, 10, 11170, '竞猜榜日榜第3名', '[{\"awardId\":10180,\"item\":60,\"num\":20},{\"awardId\":10020,\"item\":60,\"num\":15}]', 3, 3, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111403, 11170, 10, 11170, '竞猜榜日榜第4-6名', '[{\"awardId\":10180,\"item\":60,\"num\":15},{\"awardId\":10020,\"item\":60,\"num\":10}]', 4, 6, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111404, 11170, 10, 11170, '竞猜榜日榜第7-10名', '[{\"awardId\":10180,\"item\":60,\"num\":10},{\"awardId\":10020,\"item\":60,\"num\":8}]', 7, 10, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111405, 11170, 10, 11170, '竞猜榜日榜第11-20名', '[{\"awardId\":10180,\"item\":60,\"num\":7},{\"awardId\":10020,\"item\":60,\"num\":6}]', 11, 20, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111406, 11170, 10, 11170, '竞猜榜日榜第21-50名', '[{\"awardId\":10180,\"item\":60,\"num\":5},{\"awardId\":10020,\"item\":60,\"num\":4}]', 21, 50, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111407, 11170, 10, 11170, '竞猜榜日榜参与奖', '[{\"awardId\":10180,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":1}]', 51, 99999, '0', b'1');
-- 竞猜跨服冲榜11180 总榜
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111408, 11180, 10, 11180, '竞猜榜总榜第1名', '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":350},{\"awardId\":50129,\"item\":60,\"num\":20},{\"awardId\":50132,\"item\":60,\"num\":3}]', 1, 1, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111409, 11180, 10, 11180, '竞猜榜总榜第2名', '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":250},{\"awardId\":50129,\"item\":60,\"num\":15},{\"awardId\":50132,\"item\":60,\"num\":1}]', 2, 2, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111410, 11180, 10, 11180, '竞猜榜总榜第3名', '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":200},{\"awardId\":50129,\"item\":60,\"num\":10},{\"awardId\":50131,\"item\":60,\"num\":2}]', 3, 3, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111411, 11180, 10, 11180, '竞猜榜总榜第4-6名', '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":150},{\"awardId\":50129,\"item\":60,\"num\":8},{\"awardId\":50131,\"item\":60,\"num\":1}]', 4, 6, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111412, 11180, 10, 11180, '竞猜榜总榜第7-10名', '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":120},{\"awardId\":50129,\"item\":60,\"num\":5},{\"awardId\":50130,\"item\":60,\"num\":2}]', 7, 10, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111413, 11180, 10, 11180, '竞猜榜总榜第11-20名', '[{\"awardId\":50221,\"item\":60,\"num\":100},{\"awardId\":11010,\"item\":60,\"num\":90},{\"awardId\":50129,\"item\":60,\"num\":2},{\"awardId\":50130,\"item\":60,\"num\":1}]', 11, 20, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111414, 11180, 10, 11180, '竞猜榜总榜第21-50名', '[{\"awardId\":50221,\"item\":60,\"num\":60},{\"awardId\":11010,\"item\":60,\"num\":60}]', 21, 50, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111415, 11180, 10, 11180, '竞猜榜总榜第51-100名', '[{\"awardId\":50221,\"item\":60,\"num\":40},{\"awardId\":11010,\"item\":60,\"num\":30}]', 51, 100, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111416, 11180, 10, 11180, '竞猜榜总榜第101-200名', '[{\"awardId\":50221,\"item\":60,\"num\":20},{\"awardId\":11010,\"item\":60,\"num\":15}]', 101, 200, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111417, 11180, 10, 11180, '竞猜榜总榜参与奖', '[{\"awardId\":50221,\"item\":60,\"num\":10},{\"awardId\":11010,\"item\":60,\"num\":5}]', 201, 99999, '0', b'1');



-- 竞猜跨服冲榜11170 日榜
UPDATE cfg_activity_rank SET `serial` = 11170, `scope` = 10, `type` = 11170, `name` = '竞猜榜日榜第1名', `min_rank` = 1, `max_rank` = 1, `min_value` = '0',  `awards` = '[{\"awardId\":11410,\"item\":60,\"num\":1},{\"awardId\":10180,\"item\":60,\"num\":35},{\"awardId\":10020,\"item\":60,\"num\":30}]', `status` = b'1' WHERE `id` = 111400;

UPDATE cfg_activity_rank SET `serial` = 11170, `scope` = 10, `type` = 11170, `name` = '竞猜榜日榜第2名', `min_rank` = 2, `max_rank` = 2, `min_value` = '0',  `awards` = '[{\"awardId\":11060,\"item\":60,\"num\":1},{\"awardId\":10180,\"item\":60,\"num\":25},{\"awardId\":10020,\"item\":60,\"num\":20}]', `status` = b'1' WHERE `id` = 111401;

UPDATE cfg_activity_rank SET `serial` = 11170, `scope` = 10, `type` = 11170, `name` = '竞猜榜日榜第3名', `min_rank` = 3, `max_rank` = 3, `min_value` = '0',  `awards` = '[{\"awardId\":10180,\"item\":60,\"num\":20},{\"awardId\":10020,\"item\":60,\"num\":15}]', `status` = b'1' WHERE `id` = 111402;


UPDATE cfg_activity_rank SET `serial` = 11170, `scope` = 10, `type` = 11170, `name` = '竞猜榜日榜第4-6名', `min_rank` = 4, `max_rank` = 6, `min_value` = '0',  `awards` = '[{\"awardId\":10180,\"item\":60,\"num\":15},{\"awardId\":10020,\"item\":60,\"num\":10}]', `status` = b'1' WHERE `id` = 111403;


UPDATE cfg_activity_rank SET `serial` = 11170, `scope` = 10, `type` = 11170, `name` = '竞猜榜日榜第7-10名', `min_rank` = 7, `max_rank` = 10, `min_value` = '0',  `awards` = '[{\"awardId\":10180,\"item\":60,\"num\":10},{\"awardId\":10020,\"item\":60,\"num\":8}]', `status` = b'1' WHERE `id` = 111404;


UPDATE cfg_activity_rank SET `serial` = 11170, `scope` = 10, `type` = 11170, `name` = '竞猜榜日榜第11-20名', `min_rank` = 11, `max_rank` = 20, `min_value` = '0',  `awards` = '[{\"awardId\":10180,\"item\":60,\"num\":7},{\"awardId\":10020,\"item\":60,\"num\":6}]', `status` = b'1' WHERE `id` = 111405;

UPDATE cfg_activity_rank SET `serial` = 11170, `scope` = 10, `type` = 11170, `name` = '竞猜榜日榜第21-50名', `min_rank` = 11, `max_rank` = 20, `min_value` = '0',  `awards` = '[{\"awardId\":10180,\"item\":60,\"num\":5},{\"awardId\":10020,\"item\":60,\"num\":4}]', `status` = b'1' WHERE `id` = 111406;

UPDATE cfg_activity_rank SET `serial` = 11170, `scope` = 10, `type` = 11170, `name` = '竞猜榜日榜第21-50名', `min_rank` = 51, `max_rank` = 99999, `min_value` = '0',  `awards` = '[{\"awardId\":10180,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":1}]', `status` = b'1' WHERE `id` = 111407;

-- 竞猜跨服冲榜11180 总榜
UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜第1名', `min_rank` = 1, `max_rank` = 1, `min_value` = '0',  `awards` = '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":350},{\"awardId\":50129,\"item\":60,\"num\":20},{\"awardId\":50132,\"item\":60,\"num\":3}]', `status` = b'1' WHERE `id` = 111408;

UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜第2名', `min_rank` = 2, `max_rank` = 2, `min_value` = '0',  `awards` = '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":250},{\"awardId\":50129,\"item\":60,\"num\":15},{\"awardId\":50132,\"item\":60,\"num\":1}]', `status` = b'1' WHERE `id` = 111409;

UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜第3名', `min_rank` = 3, `max_rank` = 3, `min_value` = '0',  `awards` = '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":200},{\"awardId\":50129,\"item\":60,\"num\":10},{\"awardId\":50131,\"item\":60,\"num\":2}]', `status` = b'1' WHERE `id` = 111410;

UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜第4-6名', `min_rank` = 4, `max_rank` = 6, `min_value` = '0',  `awards` = '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":150},{\"awardId\":50129,\"item\":60,\"num\":8},{\"awardId\":50131,\"item\":60,\"num\":1}]', `status` = b'1' WHERE `id` = 111411;

UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜第7-10名', `min_rank` = 7, `max_rank` = 10, `min_value` = '0',  `awards` = '[{\"awardId\":50432,\"item\":60,\"num\":1},{\"awardId\":11010,\"item\":60,\"num\":120},{\"awardId\":50129,\"item\":60,\"num\":5},{\"awardId\":50130,\"item\":60,\"num\":2}]', `status` = b'1' WHERE `id` = 111412;

UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜第11-20名', `min_rank` = 11, `max_rank` = 20, `min_value` = '0',  `awards` = '[{\"awardId\":50221,\"item\":60,\"num\":100},{\"awardId\":11010,\"item\":60,\"num\":90},{\"awardId\":50129,\"item\":60,\"num\":2},{\"awardId\":50130,\"item\":60,\"num\":1}]', `status` = b'1' WHERE `id` = 111413;

UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜第21-50名', `min_rank` = 21, `max_rank` = 50, `min_value` = '0',  `awards` = '[{\"awardId\":50221,\"item\":60,\"num\":60},{\"awardId\":11010,\"item\":60,\"num\":60}]', `status` = b'1' WHERE `id` = 111414;

UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜第51-100名', `min_rank` = 51, `max_rank` = 100, `min_value` = '0',  `awards` = '[{\"awardId\":50221,\"item\":60,\"num\":40},{\"awardId\":11010,\"item\":60,\"num\":30}]', `status` = b'1' WHERE `id` = 111415;

UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜第101-200名', `min_rank` = 101, `max_rank` = 200, `min_value` = '0',  `awards` = '[{\"awardId\":50221,\"item\":60,\"num\":20},{\"awardId\":11010,\"item\":60,\"num\":15}]', `status` = b'1' WHERE `id` = 111416;

UPDATE cfg_activity_rank SET `serial` = 11180, `scope` = 10, `type` = 11180, `name` = '竞猜榜总榜参与奖', `min_rank` = 201, `max_rank` = 99999, `min_value` = '0',  `awards` = '[{\"awardId\":50221,\"item\":60,\"num\":10},{\"awardId\":11010,\"item\":60,\"num\":5}]', `status` = b'1' WHERE `id` = 111417;













-- 世界杯定时发送奖励
INSERT INTO `schedule_job` VALUES (null, 'userSuper16AwardsJob', 'job', '', '0 0 16 4 12 ? ', 0, '世界杯-超级16强定时发送奖励', '2022-11-16 08:55:14');
INSERT INTO `schedule_job` VALUES (null, 'userDroiyan8AwardsJob', 'job', NULL, '0 0 16 8 12 ? ', 0, '世界杯-决战8强定时发送奖励', '2022-11-16 08:55:12');
INSERT INTO `schedule_job` VALUES (null, 'userProphetAwardsJob', 'job', NULL, '0 0 16 19 12 ? ', 0, '世界杯-我是预言家定时发送奖励', '2022-11-16 08:55:10');
INSERT INTO `schedule_job` VALUES (null, 'userQuizKingAwardsJob', 'job', NULL, '0 20 16 21,22,23,24,25,26,27,28,29,30,31 11 ? 2022-2023', 0, '世界杯-我是竞猜王定时发送奖励', '2022-11-16 08:55:05');
INSERT INTO `schedule_job` VALUES (null, 'userQuizKingAwardsJob', 'job', NULL, '0 20 16 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,18,19 12 ? 2022-2023', 0, '世界杯-我是竞猜王定时发送奖励', '2022-11-16 08:55:07');



