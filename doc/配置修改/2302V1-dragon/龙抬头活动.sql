-- 活动总览  10130
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10130, `scope` = 10, `serial` = 9, `series` = NULL, `name` =  '节日总览 ', `detail` =  '龙抬头', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 9955;
-- 节日登入 10020
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10020, `scope` = 10, `serial` = 20, `series` = NULL, `name` =  '节日签到第一天 ', `detail` =  '龙抬头', `need_value` = 1, `awards` =  '[{\"item\":60,\"awardId\":50497,\"num\":1},{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":60,\"awardId\":80,\"num\":1}] ', `status` =  b'1' WHERE `id` = 9904;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10020, `scope` = 10, `serial` = 20, `series` = NULL, `name` =  '节日签到第二天 ', `detail` =  '龙抬头', `need_value` = 2, `awards` =  '[{\"item\":60,\"awardId\":50497,\"num\":1},{\"item\":60,\"awardId\":10110,\"num\":1},{\"item\":60,\"awardId\":130,\"num\":1}] ', `status` =  b'1' WHERE `id` = 9905;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10020, `scope` = 10, `serial` = 20, `series` = NULL, `name` =  '节日签到第三天 ', `detail` =  '龙抬头', `need_value` = 3, `awards` =  '[{\"item\":60,\"awardId\":50497,\"num\":1},{\"item\":60,\"awardId\":10,\"num\":1},{\"item\":60,\"awardId\":120,\"num\":1}] ', `status` =  b'1' WHERE `id` = 9906;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10020, `scope` = 10, `serial` = 20, `series` = NULL, `name` =  '节日签到第四天 ', `detail` =  '龙抬头', `need_value` = 4, `awards` =  '[{\"item\":60,\"awardId\":50497,\"num\":1},{\"item\":60,\"awardId\":30,\"num\":1},{\"item\":60,\"awardId\":110,\"num\":1}] ', `status` =  b'1' WHERE `id` = 9907;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10020, `scope` = 10, `serial` = 20, `series` = NULL, `name` =  '节日签到第五天 ', `detail` =  '龙抬头', `need_value` = 5, `awards` =  '[{\"item\":60,\"awardId\":50497,\"num\":1},{\"item\":60,\"awardId\":60,\"num\":1},{\"item\":60,\"awardId\":220,\"num\":1}] ', `status` =  b'1' WHERE `id` = 9908;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10020, `scope` = 10, `serial` = 20, `series` = NULL, `name` =  '节日签到第六天 ', `detail` =  '龙抬头', `need_value` = 6, `awards` =  '[{\"item\":60,\"awardId\":50497,\"num\":1},{\"item\":60,\"awardId\":11090,\"num\":2},{\"item\":60,\"awardId\":150,\"num\":1}] ', `status` =  b'1' WHERE `id` = 9909;
-- 隐藏`status` = b '0 '
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10020, `scope` = 10, `serial` = 20, `series` = NULL, `name` =  '节日签到第七天 ', `detail` =  '龙抬头', `need_value` = 7, `awards` =  '[{\"item\":60,\"awardId\":50174,\"num\":3},{\"item\":60,\"awardId\":20,\"num\":1},{\"item\":60,\"awardId\":230,\"num\":1}] ', `status` =  b'0' WHERE `id` = 9910;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10020, `scope` = 10, `serial` = 20, `series` = NULL, `name` =  '节日签到第八天 ', `detail` =  '龙抬头', `need_value` = 8, `awards` =  '[{\"item\":60,\"awardId\":50455,\"num\":3},{\"item\":60,\"awardId\":10030,\"num\":3},{\"item\":60,\"awardId\":260,\"num\":1}] ', `status` = b'0' WHERE `id` = 9911;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10020, `scope` = 10, `serial` = 20, `series` = NULL, `name` =  '节日签到第九天 ', `detail` =  '龙抬头', `need_value` = 9, `awards` =  '[{\"item\":60,\"awardId\":50455,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":5},{\"item\":60,\"awardId\":240,\"num\":1}] ', `status` = b'0' WHERE `id` = 9912;
-- 劳动光荣(幸运翻倍) 13330
UPDATE cfg_activity SET `parent_type` = 50, `type` = 13330, `scope` = 10, `serial` = 13330, `series` = NULL, `name` = '感恩同行', `detail` = '龙抬头', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 100900;
-- 生肖对碰 13710
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100980, 50, 13710, 10, 0, NULL, '生肖对碰', '龙抬头', NULL, NULL, b'1');
-- 逍遥碰杯 13730
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100987, 50, 13730, 10, 0, NULL, '逍遥碰杯', '龙抬头', NULL, NULL, b'1');
-- 感恩商店（瑞兔商店） 13060
UPDATE cfg_activity SET `parent_type` = 50, `type` = 13060, `scope` = 10, `serial` = 13060, `series` = NULL, `name` = '黑五特惠', `detail` = '龙抬头', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 100835;
-- 酒逢知己 13720
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100981, 50, 13720, 10, 5, NULL, '累计获赠逍遥酿达100', '龙抬头', 100, '[{\"awardId\":11010,\"item\":60,\"num\":50}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100982, 50, 13720, 10, 5, NULL, '累计获赠逍遥酿达300', '龙抬头', 300, '[{\"awardId\":11600,\"item\":60,\"num\":50}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100983, 50, 13720, 10, 5, NULL, '累计获赠逍遥酿达500', '龙抬头', 500, '[{\"awardId\":11400,\"item\":60,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100984, 50, 13720, 10, 5, NULL, '累计获赠逍遥酿达700', '龙抬头', 700, '[{\"awardId\":50011,\"item\":60,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100985, 50, 13720, 10, 5, NULL, '累计获赠逍遥酿达900', '龙抬头', 900, '[{\"awardId\":11065,\"item\":60,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100986, 50, 13720, 10, 5, NULL, '累计获赠逍遥酿达1200', '龙抬头', 1200, '[{\"awardId\":50500,\"item\":60,\"num\":1}]', b'1');
-- 7.节日礼包 13560
UPDATE cfg_activity SET `parent_type` = 50, `type` = 13560, `scope` = 10, `serial` = 0, `series` = NULL, `name` = '节日礼包', `detail` = '瑞兔迎新', `need_value` = 6, `awards` = '[{\"num\":1,\"awardId\":11060,\"item\":60},{\"num\":10,\"awardId\":11090,\"item\":60},{\"num\":10,\"awardId\":11010,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60},{\"num\":2,\"awardId\":10110,\"item\":60},{\"num\":1,\"awardId\":50158,\"item\":60}]', `status` = b'1' WHERE `id` = 100940;

-- 充值特惠 10140,10030
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10140, `scope` = 10, `serial` = 110, `series` = NULL, `name` =  '节日每日每充值10元 ', `detail` =  '龙抬头', `need_value` = 10, `awards` =  '[{\"item\":60,\"awardId\":50498,\"num\":1}] ', `status` = b'1' WHERE `id` = 9914;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值6元 ', `detail` =  '龙抬头', `need_value` = 6, `awards` =  '[{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":60,\"awardId\":10110,\"num\":1}] ', `status` =  b'1' WHERE `id` = 9915;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值30元 ', `detail` =  '龙抬头', `need_value` = 30, `awards` =  '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}] ', `status` =  b'1' WHERE `id` = 9916;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值68元 ', `detail` =  '龙抬头', `need_value` = 68, `awards` =  '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}] ', `status` =  b'1' WHERE `id` = 9917;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值98元 ', `detail` =  '龙抬头', `need_value` = 98, `awards` =  '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}] ', `status` =  b'1' WHERE `id` = 9918;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值198元 ', `detail` =  '龙抬头', `need_value` = 198, `awards` =  '[{\"item\":10,\"num\":588},{\"item\":60,\"awardId\":10180,\"num\":6}] ', `status` =  b'1' WHERE `id` = 9919;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值328元 ', `detail` =  '龙抬头', `need_value` = 328, `awards` =  '[{\"item\":10,\"num\":588},{\"item\":60,\"awardId\":10180,\"num\":6}] ', `status` =  b'1' WHERE `id` = 9920;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值648元 ', `detail` =  '龙抬头', `need_value` = 648, `awards` =  '[{\"item\":10,\"num\":1588},{\"item\":60,\"awardId\":10180,\"num\":18}] ', `status` =  b'1' WHERE `id` = 9921;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值1000元 ', `detail` =  '龙抬头', `need_value` = 1000, `awards` =  '[{\"item\":10,\"num\":1988},{\"item\":60,\"awardId\":10180,\"num\":24}] ', `status` =  b'1' WHERE `id` = 9922;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值1500元 ', `detail` =  '龙抬头', `need_value` = 1500, `awards` =  '[{\"item\":1,\"num\":200},{\"item\":60,\"awardId\":10180,\"num\":34}] ', `status` =  b'1' WHERE `id` = 9923;
UPDATE cfg_activity SET `parent_type` = 50, `type` = 10030, `scope` = 10, `serial` = 50, `series` = NULL, `name` =  '今日累计充值2000元 ', `detail` =  '龙抬头', `need_value` = 2000, `awards` =  '[{\"item\":1,\"num\":300},{\"item\":60,\"awardId\":10180,\"num\":34}] ', `status` =  b'1' WHERE `id` = 9924;

-- 花赋予神 13310
UPDATE cfg_activity SET `parent_type` = 50, `type` = 13310, `scope` = 10, `serial` = 13310, `series` = NULL, `name` = '花赋予神', `detail` = '龙抬头', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 100898;
-- 逍遥跨服冲榜11190 日榜
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111420, 11190, 10, 11190, '逍遥榜日榜第1名', '[{\"awardId\":11410,\"item\":60,\"num\":1},{\"awardId\":10180,\"item\":60,\"num\":35},{\"awardId\":10020,\"item\":60,\"num\":30}]', 1, 1, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111421, 11190, 10, 11190, '逍遥榜日榜第2名', '[{\"awardId\":11060,\"item\":60,\"num\":1},{\"awardId\":10180,\"item\":60,\"num\":25},{\"awardId\":10020,\"item\":60,\"num\":20}]', 2, 2, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111422, 11190, 10, 11190, '逍遥榜日榜第3名', '[{\"awardId\":10180,\"item\":60,\"num\":20},{\"awardId\":10020,\"item\":60,\"num\":15}]', 3, 3, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111423, 11190, 10, 11190, '逍遥榜日榜第4-6名', '[{\"awardId\":10180,\"item\":60,\"num\":15},{\"awardId\":10020,\"item\":60,\"num\":10}]', 4, 6, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111424, 11190, 10, 11190, '逍遥榜日榜第7-10名', '[{\"awardId\":10180,\"item\":60,\"num\":10},{\"awardId\":10020,\"item\":60,\"num\":8}]', 7, 10, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111425, 11190, 10, 11190, '逍遥榜日榜第11-20名', '[{\"awardId\":10180,\"item\":60,\"num\":7},{\"awardId\":10020,\"item\":60,\"num\":6}]', 11, 20, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111426, 11190, 10, 11190, '逍遥榜日榜第21-50名', '[{\"awardId\":10180,\"item\":60,\"num\":5},{\"awardId\":10020,\"item\":60,\"num\":4}]', 21, 50, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111427, 11190, 10, 11190, '逍遥榜日榜参与奖', '[{\"awardId\":10180,\"item\":60,\"num\":3},{\"awardId\":10020,\"item\":60,\"num\":1}]', 51, 99999, '0', b'1');
-- 逍遥跨服冲榜11200 总榜
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111431, 11200, 10, 11200, '逍遥榜总榜第1名', '[{\"awardId\":50221,\"item\":60,\"num\":200},{\"awardId\":11010,\"item\":60,\"num\":350},{\"awardId\":50129,\"item\":60,\"num\":20},{\"awardId\":50132,\"item\":60,\"num\":3}]', 1, 1, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111432, 11200, 10, 11200, '逍遥榜总榜第2名', '[{\"awardId\":50221,\"item\":60,\"num\":200},{\"awardId\":11010,\"item\":60,\"num\":250},{\"awardId\":50129,\"item\":60,\"num\":15},{\"awardId\":50132,\"item\":60,\"num\":1}]', 2, 2, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111433, 11200, 10, 11200, '逍遥榜总榜第3名', '[{\"awardId\":50221,\"item\":60,\"num\":200},{\"awardId\":11010,\"item\":60,\"num\":200},{\"awardId\":50129,\"item\":60,\"num\":10},{\"awardId\":50131,\"item\":60,\"num\":2}]', 3, 3, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111434, 11200, 10, 11200, '逍遥榜总榜第4-6名', '[{\"awardId\":50221,\"item\":60,\"num\":200},{\"awardId\":11010,\"item\":60,\"num\":150},{\"awardId\":50129,\"item\":60,\"num\":8},{\"awardId\":50131,\"item\":60,\"num\":1}]', 4, 6, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111435, 11200, 10, 11200, '逍遥榜总榜第7-10名', '[{\"awardId\":50221,\"item\":60,\"num\":200},{\"awardId\":11010,\"item\":60,\"num\":120},{\"awardId\":50129,\"item\":60,\"num\":5},{\"awardId\":50130,\"item\":60,\"num\":2}]', 7, 10, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111436, 11200, 10, 11200, '逍遥榜总榜第11-20名', '[{\"awardId\":50221,\"item\":60,\"num\":100},{\"awardId\":11010,\"item\":60,\"num\":90},{\"awardId\":50129,\"item\":60,\"num\":2},{\"awardId\":50130,\"item\":60,\"num\":1}]', 11, 20, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111437, 11200, 10, 11200, '逍遥榜总榜第21-50名', '[{\"awardId\":50221,\"item\":60,\"num\":60},{\"awardId\":11010,\"item\":60,\"num\":60}]', 21, 50, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111438, 11200, 10, 11200, '逍遥榜总榜第51-100名', '[{\"awardId\":50221,\"item\":60,\"num\":40},{\"awardId\":11010,\"item\":60,\"num\":30}]', 51, 100, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111439, 11200, 10, 11200, '逍遥榜总榜第101-200名', '[{\"awardId\":50221,\"item\":60,\"num\":20},{\"awardId\":11010,\"item\":60,\"num\":15}]', 101, 200, '0', b'1');
INSERT INTO cfg_activity_rank (`id`, `serial`, `scope`, `type`, `name`, `awards`, `min_rank`, `max_rank`, `min_value`, `status`) VALUES (111440, 11200, 10, 11200, '逍遥榜总榜参与奖', '[{\"awardId\":50221,\"item\":60,\"num\":10},{\"awardId\":11010,\"item\":60,\"num\":5}]', 201, 99999, '0', b'1');
