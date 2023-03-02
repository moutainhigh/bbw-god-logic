-- 1.活动总览  13620
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100926, 51, 13620, 10, 9, NULL, '节日总览', '圣诞节', NULL, NULL, b'1');
-- 2.节日登入 13510
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100927, 51, 13510, 10, 20, NULL, '节日签到第一天', '圣诞节', 1, '[{\"item\":60,\"awardId\":50460,\"num\":3},{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":60,\"awardId\":80,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100928, 51, 13510, 10, 20, NULL, '节日签到第二天', '圣诞节', 2, '[{\"item\":60,\"awardId\":50460,\"num\":3},{\"item\":60,\"awardId\":10110,\"num\":1},{\"item\":60,\"awardId\":130,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100929, 51, 13510, 10, 20, NULL, '节日签到第三天', '圣诞节', 3, '[{\"item\":60,\"awardId\":50460,\"num\":3},{\"item\":60,\"awardId\":10,\"num\":1},{\"item\":60,\"awardId\":120,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100930, 51, 13510, 10, 20, NULL, '节日签到第四天', '圣诞节', 4, '[{\"item\":60,\"awardId\":50460,\"num\":3},{\"item\":60,\"awardId\":30,\"num\":1},{\"item\":60,\"awardId\":110,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100931, 51, 13510, 10, 20, NULL, '节日签到第五天', '圣诞节', 5, '[{\"item\":60,\"awardId\":50460,\"num\":3},{\"item\":60,\"awardId\":60,\"num\":1},{\"item\":60,\"awardId\":220,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100932, 51, 13510, 10, 20, NULL, '节日签到第六天', '圣诞节', 6, '[{\"item\":60,\"awardId\":50460,\"num\":3},{\"item\":60,\"awardId\":11090,\"num\":2},{\"item\":60,\"awardId\":150,\"num\":1}]', b'1');
-- 隐藏`status` = b'0'
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100933, 51, 13510, 10, 20, NULL, '节日签到第七天', '圣诞节', 7, '[{\"item\":60,\"awardId\":50455,\"num\":3},{\"item\":60,\"awardId\":20,\"num\":1},{\"item\":60,\"awardId\":230,\"num\":1}]', b'0');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100934, 51, 13510, 10, 20, NULL, '节日签到第八天', '圣诞节', 8, '[{\"item\":60,\"awardId\":50455,\"num\":3},{\"item\":60,\"awardId\":10030,\"num\":3},{\"item\":60,\"awardId\":260,\"num\":1}]', b'0');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100935, 51, 13510, 10, 20, NULL, '节日签到第九天', '圣诞节', 9, '[{\"item\":60,\"awardId\":50455,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":5},{\"item\":60,\"awardId\":240,\"num\":1}]', b'0');

-- 3.派礼小鹿 13520
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100936, 51, 13520, 10, 13330, NULL, '派礼小鹿', '圣诞节', NULL, NULL, b'1');
-- 4.魔法女巫 13530
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100937, 51, 13530, 10, 13330, NULL, '魔法女巫', '圣诞节', NULL, NULL, b'1');
-- 5.圣诞心愿 13540
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100938, 51, 13540, 10, 13330, NULL, '圣诞心愿', '圣诞节', NULL, NULL, b'1');
-- 6.杂货小铺 13550
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100939, 51, 13550, 10, 0, NULL, '杂货小铺', '圣诞节', NULL, NULL, b'1');
-- 7.节日礼包 13560
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100940, 51, 13560, 10, 0, NULL, '节日礼包', '圣诞节', 6, '[{\"num\":1,\"awardId\":11060,\"item\":60},{\"num\":10,\"awardId\":11090,\"item\":60},{\"num\":10,\"awardId\":11010,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60},{\"num\":2,\"awardId\":10110,\"item\":60},{\"num\":1,\"awardId\":50158,\"item\":60}]', b'1');
-- 8.1丰财聚宝 13570
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100941, 51, 13570, 10, 0, NULL, '丰财聚宝', '圣诞节', NULL, NULL, b'1');
-- 8.2限时礼包 13580
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (9000002, 51, 13580, 10, 0, NULL, '限时礼包', '圣诞节', NULL, '[{\"item\":60,\"awardId\":11216,\"num\":1},{\"item\":120,\"awardId\":0,\"num\":1}]', b'1');
-- 9.虎年招募 13590
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100943, 51, 13590, 10, 0, NULL, '虎年招募', '圣诞节', NULL, NULL, b'1');

-- 10充值特惠 13600,13610
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100944, 51, 13600, 10, 0, NULL, '节日每日每充值10元', '圣诞节', 10, '[{\"item\":60,\"awardId\":11216,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100945, 51, 13610, 10, 0, NULL, '今日累计充值6元', '圣诞节', 6, '[{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":60,\"awardId\":10110,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100946, 51, 13610, 10, 0, NULL, '今日累计充值30元', '圣诞节', 30, '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100947, 51, 13610, 10, 0, NULL, '今日累计充值68元', '圣诞节', 68, '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100948, 51, 13610, 10, 0, NULL, '今日累计充值98元', '圣诞节', 98, '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100949, 51, 13610, 10, 0, NULL, '今日累计充值198元', '圣诞节', 198, '[{\"item\":10,\"num\":588},{\"item\":60,\"awardId\":10180,\"num\":6}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100950, 51, 13610, 10, 0, NULL, '今日累计充值328元', '圣诞节', 328, '[{\"item\":10,\"num\":588},{\"item\":60,\"awardId\":10180,\"num\":6}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100951, 51, 13610, 10, 0, NULL, '今日累计充值648元', '圣诞节', 648, '[{\"item\":10,\"num\":1588},{\"item\":60,\"awardId\":10180,\"num\":18}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100952, 51, 13610, 10, 0, NULL, '今日累计充值1000元', '圣诞节', 1000, '[{\"item\":10,\"num\":1988},{\"item\":60,\"awardId\":10180,\"num\":24}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100953, 51, 13610, 10, 0, NULL, '今日累计充值1500元', '圣诞节', 1500, '[{\"item\":1,\"num\":200},{\"item\":60,\"awardId\":10180,\"num\":34}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100954, 51, 13610, 10, 0, NULL, '今日累计充值2000元', '圣诞节', 2000, '[{\"item\":1,\"num\":300},{\"item\":60,\"awardId\":10180,\"num\":34}]', b'1');


