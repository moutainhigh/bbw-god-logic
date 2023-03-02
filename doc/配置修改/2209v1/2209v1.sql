-- 活动总览  10130
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10130,
    `scope`       = 10,
    `serial`      = 9,
    `series`      = NULL,
    `name`        = '节日总览',
    `detail`      = '国庆节',
    `need_value`  = NULL,
    `awards`      = NULL,
    `status`      = b'1'
WHERE `id` = 9955;
-- 节日登入 10020
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10020,
    `scope`       = 10,
    `serial`      = 20,
    `series`      = NULL,
    `name`        = '节日签到第一天',
    `detail`      = '国庆节',
    `need_value`  = 1,
    `awards`      = '[{\"item\":60,\"awardId\":50402,\"num\":2},{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":60,\"awardId\":80,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9904;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10020,
    `scope`       = 10,
    `serial`      = 20,
    `series`      = NULL,
    `name`        = '节日签到第二天',
    `detail`      = '国庆节',
    `need_value`  = 2,
    `awards`      = '[{\"item\":60,\"awardId\":50402,\"num\":2},{\"item\":60,\"awardId\":10110,\"num\":1},{\"item\":60,\"awardId\":130,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9905;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10020,
    `scope`       = 10,
    `serial`      = 20,
    `series`      = NULL,
    `name`        = '节日签到第三天',
    `detail`      = '国庆节',
    `need_value`  = 3,
    `awards`      = '[{\"item\":60,\"awardId\":50403,\"num\":2},{\"item\":60,\"awardId\":10,\"num\":1},{\"item\":60,\"awardId\":120,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9906;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10020,
    `scope`       = 10,
    `serial`      = 20,
    `series`      = NULL,
    `name`        = '节日签到第四天',
    `detail`      = '国庆节',
    `need_value`  = 4,
    `awards`      = '[{\"item\":60,\"awardId\":50403,\"num\":2},{\"item\":60,\"awardId\":30,\"num\":1},{\"item\":60,\"awardId\":110,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9907;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10020,
    `scope`       = 10,
    `serial`      = 20,
    `series`      = NULL,
    `name`        = '节日签到第五天',
    `detail`      = '国庆节',
    `need_value`  = 5,
    `awards`      = '[{\"item\":60,\"awardId\":50404,\"num\":2},{\"item\":60,\"awardId\":60,\"num\":1},{\"item\":60,\"awardId\":220,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9908;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10020,
    `scope`       = 10,
    `serial`      = 20,
    `series`      = NULL,
    `name`        = '节日签到第六天',
    `detail`      = '国庆节',
    `need_value`  = 6,
    `awards`      = '[{\"item\":60,\"awardId\":50404,\"num\":2},{\"item\":60,\"awardId\":11090,\"num\":2},{\"item\":60,\"awardId\":150,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9909;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10020,
    `scope`       = 10,
    `serial`      = 20,
    `series`      = NULL,
    `name`        = '节日签到第七天',
    `detail`      = '国庆节',
    `need_value`  = 7,
    `awards`      = '[{\"item\":60,\"awardId\":50405,\"num\":2},{\"item\":60,\"awardId\":20,\"num\":1},{\"item\":60,\"awardId\":230,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9910;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10020,
    `scope`       = 10,
    `serial`      = 20,
    `series`      = NULL,
    `name`        = '节日签到第八天',
    `detail`      = '国庆节',
    `need_value`  = 8,
    `awards`      = '[{\"item\":60,\"awardId\":50405,\"num\":2},{\"item\":60,\"awardId\":10030,\"num\":3},{\"item\":60,\"awardId\":260,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9911;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10020,
    `scope`       = 10,
    `serial`      = 20,
    `series`      = NULL,
    `name`        = '节日签到第九天',
    `detail`      = '国庆节',
    `need_value`  = 9,
    `awards`      = '[{\"item\":60,\"awardId\":50405,\"num\":2},{\"item\":60,\"awardId\":10180,\"num\":5},{\"item\":60,\"awardId\":240,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9912;
#充值特惠 10140,10030
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10140,
    `scope`       = 10,
    `serial`      = 110,
    `series`      = NULL,
    `name`        = '节日每日每充值10元',
    `detail`      = '国庆节',
    `need_value`  = 10,
    `awards`      = '[{\"item\":60,\"awardId\":50407,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9914;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值6元',
    `detail`      = '国庆节',
    `need_value`  = 6,
    `awards`      = '[{\"item\":60,\"awardId\":530,\"num\":1},{\"item\":60,\"awardId\":10110,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9915;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值30元',
    `detail`      = '国庆节',
    `need_value`  = 30,
    `awards`      = '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}]',
    `status`      = b'1'
WHERE `id` = 9916;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值68元',
    `detail`      = '国庆节',
    `need_value`  = 68,
    `awards`      = '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}]',
    `status`      = b'1'
WHERE `id` = 9917;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值98元',
    `detail`      = '国庆节',
    `need_value`  = 98,
    `awards`      = '[{\"item\":10,\"num\":158},{\"item\":60,\"awardId\":10180,\"num\":2}]',
    `status`      = b'1'
WHERE `id` = 9918;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值198元',
    `detail`      = '国庆节',
    `need_value`  = 198,
    `awards`      = '[{\"item\":10,\"num\":588},{\"item\":60,\"awardId\":10180,\"num\":6}]',
    `status`      = b'1'
WHERE `id` = 9919;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值328元',
    `detail`      = '国庆节',
    `need_value`  = 328,
    `awards`      = '[{\"item\":10,\"num\":588},{\"item\":60,\"awardId\":10180,\"num\":6}]',
    `status`      = b'1'
WHERE `id` = 9920;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值648元',
    `detail`      = '国庆节',
    `need_value`  = 648,
    `awards`      = '[{\"item\":10,\"num\":1588},{\"item\":60,\"awardId\":10180,\"num\":18}]',
    `status`      = b'1'
WHERE `id` = 9921;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值1000元',
    `detail`      = '国庆节',
    `need_value`  = 1000,
    `awards`      = '[{\"item\":10,\"num\":1988},{\"item\":60,\"awardId\":10180,\"num\":24}]',
    `status`      = b'1'
WHERE `id` = 9922;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值1500元',
    `detail`      = '国庆节',
    `need_value`  = 1500,
    `awards`      = '[{\"item\":1,\"num\":200},{\"item\":60,\"awardId\":10180,\"num\":34}]',
    `status`      = b'1'
WHERE `id` = 9923;
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10030,
    `scope`       = 10,
    `serial`      = 50,
    `series`      = NULL,
    `name`        = '今日累计充值2000元',
    `detail`      = '国庆节',
    `need_value`  = 2000,
    `awards`      = '[{\"item\":1,\"num\":300},{\"item\":60,\"awardId\":10180,\"num\":34}]',
    `status`      = b'1'
WHERE `id` = 9924;

-- 舞动中国 10180
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 10180,
    `scope`       = 10,
    `serial`      = 10180,
    `series`      = NULL,
    `name`        = '舞动中过(端午赛舟)',
    `detail`      = '国庆节',
    `need_value`  = NULL,
    `awards`      = NULL,
    `status`      = b'1'
WHERE `id` = 100897;
-- 锦鲤祈愿 13370
INSERT INTO `cfg_activity` (`id`, `parent_type`, `type`, `scope`, `serial`, `series`, `name`, `detail`, `need_value`,
                            `awards`, `status`)
VALUES (100903, 50, 13370, 10, 13370, NULL, '锦鲤祈愿', '国庆节', NULL, NULL, b'1');
-- 国庆商店 13060
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 13060,
    `scope`       = 10,
    `serial`      = 13060,
    `series`      = NULL,
    `name`        = '黑五特惠',
    `detail`      = '国庆节',
    `need_value`  = NULL,
    `awards`      = NULL,
    `status`      = b'1'
WHERE `id` = 100835;
-- 花赋予神 13310
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 13310,
    `scope`       = 10,
    `serial`      = 13310,
    `series`      = NULL,
    `name`        = '花赋予神',
    `detail`      ='国庆节',
    `need_value`  = NULL,
    `awards`      = NULL,
    `status`      = b'1'
WHERE `id` = 100898;
-- 许愿宝图 13200
UPDATE `cfg_activity`
SET `parent_type` = 50,
    `type`        = 13200,
    `scope`       = 10,
    `serial`      = 13200,
    `series`      = NULL,
    `name`        = '寻藏宝图',
    `detail`      = '国庆节',
    `need_value`  = NULL,
    `awards`      = '[{\"item\":60,\"awardId\":11280,\"num\":1},{\"item\":120,\"awardId\":0,\"num\":1}]',
    `status`      = b'1'
WHERE `id` = 9000001;

