-- 1.活动总览 13670
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100959, 52, 13670, 10, 9, NULL, '节日总览', '元宵', NULL, NULL, b'1');
-- 2.节日登入 13680
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100960, 52, 13680, 10, 20, NULL, '节日签到第一天', '元宵', 1, '[{\"item\":60,\"awardId\":50206,\"num\":1},{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":60,\"awardId\":80,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100961, 52, 13680, 10, 20, NULL, '节日签到第二天', '元宵', 2, '[{\"item\":60,\"awardId\":50206,\"num\":1},{\"item\":60,\"awardId\":10110,\"num\":1},{\"item\":60,\"awardId\":130,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100962, 52, 13680, 10, 20, NULL, '节日签到第三天', '元宵', 3, '[{\"item\":60,\"awardId\":50206,\"num\":1},{\"item\":60,\"awardId\":10,\"num\":1},{\"item\":60,\"awardId\":120,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100963, 52, 13680, 10, 20, NULL, '节日签到第四天', '元宵', 4, '[{\"item\":60,\"awardId\":50206,\"num\":1},{\"item\":60,\"awardId\":30,\"num\":1},{\"item\":60,\"awardId\":110,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100964, 52, 13680, 10, 20, NULL, '节日签到第五天', '元宵', 5, '[{\"item\":60,\"awardId\":50206,\"num\":1},{\"item\":60,\"awardId\":60,\"num\":1},{\"item\":60,\"awardId\":220,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100965, 52, 13680, 10, 20, NULL, '节日签到第六天', '元宵', 6, '[{\"item\":60,\"awardId\":50206,\"num\":1},{\"item\":60,\"awardId\":11090,\"num\":2},{\"item\":60,\"awardId\":150,\"num\":1}]', b'1');
-- 隐藏`status` = b'0'
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100966, 52, 13680, 10, 20, NULL, '节日签到第七天', '元宵', 7, '[{\"item\":60,\"awardId\":50206,\"num\":1},{\"item\":60,\"awardId\":20,\"num\":1},{\"item\":60,\"awardId\":230,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100967, 52, 13680, 10, 20, NULL, '节日签到第八天', '元宵', 8, '[{\"item\":60,\"awardId\":50455,\"num\":3},{\"item\":60,\"awardId\":10030,\"num\":3},{\"item\":60,\"awardId\":260,\"num\":1}]', b'0');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100968, 52, 13680, 10, 20, NULL, '节日签到第九天', '元宵', 9, '[{\"item\":60,\"awardId\":50455,\"num\":3},{\"item\":60,\"awardId\":10180,\"num\":5},{\"item\":60,\"awardId\":240,\"num\":1}]', b'0');
-- 隐藏`status` = b''0''

-- 天灯工坊 13180
UPDATE cfg_activity SET `parent_type` = 52, `type` = 13180, `scope` = 10, `serial` = 13180, `series` = NULL, `name` = '天灯工坊', `detail` = '元宵', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 100853;
-- 材料商店 13340
UPDATE cfg_activity SET `parent_type` = 52, `type` = 13340, `scope` = 10, `serial` = 13340, `series` = NULL, `name` = '节日-材料商店', `detail` = '元宵', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 100901;
-- 祈福天灯13190
UPDATE cfg_activity SET `parent_type` = 52, `type` = 13190, `scope` = 10, `serial` = 13190, `series` = NULL, `name` = '祈福天灯', `detail` = '元宵', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 100854;
-- 寻藏宝图 13200
UPDATE cfg_activity SET `parent_type` = 52, `type` = 13200, `scope` = 10, `serial` = 13200, `series` = NULL, `name` = '寻藏宝图', `detail` = '元宵', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 100855;
-- 元宵锦礼 13210
UPDATE cfg_activity SET `parent_type` = 52, `type` = 13210, `scope` = 10, `serial` = 13210, `series` = NULL, `name` = '元宵锦礼', `detail` = '元宵', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 100856;
-- 元宵锦礼定时器
UPDATE schedule_job SET `bean_name` = 'holidayLanternGiftsAwardsJob', `method_name` = 'doJob', `params` = '0', `cron_expression` = '0 1 17,18,19,20,21 02,04,06 2 ? 2023', `status` = 0, `remark` = '锦礼活动开奖。', `create_time` = '2022-03-17 14:00:00' WHERE `job_id` = 70;
-- 玉兔纳福 13170
UPDATE cfg_activity SET `parent_type` = 52, `type` = 13170, `scope` = 10, `serial` = 13170, `series` = NULL, `name` = '金虎纳福', `detail` = '元宵', `need_value` = NULL, `awards` = NULL, `status` = b'1' WHERE `id` = 100852;
-- 玉兔纳福定时器
UPDATE schedule_job SET `bean_name` = 'holdaiyLotteryJHNFAwardsJob', `method_name` = 'doJob', `params` = '0', `cron_expression` = '55 59 7 07 02 ?', `status` = 0, `remark` = '2023年2月07号早上7点59分59秒执行王中王抽奖', `create_time` = '2022-01-18 14:24:46' WHERE `job_id` = 69;

-- 节日礼包 13560
UPDATE cfg_activity SET `parent_type` = 52, `type` = 13560, `scope` = 10, `serial` = 0, `series` = NULL, `name` = '节日礼包', `detail` = '元宵', `need_value` = 6, `awards` = '[{\"num\":1,\"awardId\":11060,\"item\":60},{\"num\":10,\"awardId\":11090,\"item\":60},{\"num\":10,\"awardId\":11010,\"item\":60},{\"num\":3,\"awardId\":10030,\"item\":60},{\"num\":2,\"awardId\":10110,\"item\":60},{\"num\":1,\"awardId\":50158,\"item\":60}]', `status` = b'1' WHERE `id` = 100940;


-- 充值特惠 13690,13700
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100969, 52, 13690, 10, 0, NULL, '节日每日每充值10元', '元宵', 10, '[{\"item\":60,\"awardId\":50494,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100970, 52, 13700, 10, 0, NULL, '今日累计充值6元', '元宵', 6, '[{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":60,\"awardId\":10110,\"num\":1}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100971, 52, 13700, 10, 0, NULL, '今日累计充值30元', '元宵', 30, '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100972, 52, 13700, 10, 0, NULL, '今日累计充值68元', '元宵', 68, '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100973, 52, 13700, 10, 0, NULL, '今日累计充值98元', '元宵', 98, '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100974, 52, 13700, 10, 0, NULL, '今日累计充值198元', '元宵', 198, '[{\"item\":10,\"num\":588},{\"item\":60,\"awardId\":10180,\"num\":6}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100975, 52, 13700, 10, 0, NULL, '今日累计充值328元', '元宵', 328, '[{\"item\":10,\"num\":588},{\"item\":60,\"awardId\":10180,\"num\":6}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100976, 52, 13700, 10, 0, NULL, '今日累计充值648元', '元宵', 648, '[{\"item\":10,\"num\":1588},{\"item\":60,\"awardId\":10180,\"num\":18}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100977, 52, 13700, 10, 0, NULL, '今日累计充值1000元', '元宵', 1000, '[{\"item\":10,\"num\":1988},{\"item\":60,\"awardId\":10180,\"num\":24}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100978, 52, 13700, 10, 0, NULL, '今日累计充值1500元', '元宵', 1500, '[{\"item\":1,\"num\":200},{\"item\":60,\"awardId\":10180,\"num\":34}]', b'1');
INSERT INTO cfg_activity (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`, `awards`, `status`) VALUES (100979, 52, 13700, 10, 0, NULL, '今日累计充值2000元', '元宵', 2000, '[{\"item\":1,\"num\":300},{\"item\":60,\"awardId\":10180,\"num\":34}]', b'1');
