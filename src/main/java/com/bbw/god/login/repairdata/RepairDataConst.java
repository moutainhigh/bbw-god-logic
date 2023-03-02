package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;

import java.util.Date;

/**
 * @author suchaobin
 * @description 静态常量
 * @date 2020/7/7 16:15
 **/
public class RepairDataConst {
    // 迁移封神台、诛仙阵积分、仙豆
    public static final Date MIGRATE_DATE = DateUtil.fromDateTimeString("2020-03-19 15:20:00");
    // 修复成就积分
    public static final Date REISSUE_SCORE_DATE = DateUtil.fromDateTimeString("2022-01-27 14:40:00");
    // 修正userAchievementInfo数据，保证初始化时就有且仅有一个对应的数据
    public static final Date RESET_INFO_DATE = DateUtil.fromDateTimeString("2020-05-30 12:40:00");
    // 碧游宫的全-龙息改为全-封咒，已达成的跳过
    public static final Date SKILL_CHANGE_DATE = DateUtil.fromDateTimeString("2020-04-03 16:25:00");
    public static final Date SKILL_CHANGE_07V2 = DateUtil.fromDateTimeString("2020-08-03 16:30:00");
    // 重置卡池，本次只重置解锁状态和解锁时间
    public static final Date RESET_CARD_SHOP_DATE = DateUtil.fromDateTimeString("2019-12-03 14:31:00");
    // 重置商会
    public static final Date RESET_COC_DATE = DateUtil.fromDateTimeString("2020-03-05 14:05:00");
    // 重置商会任务
    public static final Date RESET_COC_TASK_DATE = DateUtil.fromDateTimeString("2020-03-06 02:10:00");
    // 重置每日任务
    public static final Date RESET_DAILY_TASK_DATE = DateUtil.fromDateTimeString("2019-12-03 14:31:00");
    // 修复体力
    public final static Date REPAIR_DICE_DATE = DateUtil.fromDateTimeString("2019-06-20 10:10:00");
    // 修复头像
    public static final Date REPAIR_HEAD_DATE = DateUtil.fromDateTimeString("2019-09-25 10:10:00");
    // 重置月礼包
    public static final Date RESET_MONTH_BAG_DATE = DateUtil.fromDateTimeString("2019-12-12 14:31:00");
    // 重置周礼包
    public static final Date RESET_WEEK_BAG_DATE = DateUtil.fromDateTimeString("2019-12-12 14:31:00");
    // 3倍返利更新后进度修复
    public static final Date REPAIR_MULTIPLE_REBATE_DATE = DateUtil.fromDateTimeString("2019-11-14 14:40:00");
    // 修复推送数据
    public static final Date REPAIR_PUSH_DATE = DateUtil.fromDateTimeString("2020-04-03 18:10:00");
    // 使用人民币作为活动单位
    public static final Date REPAIR_RMB_PROGRESS_DATE = DateUtil.fromDateTimeString("2019-10-31 14:40:00");
    // 重置七日之约
    public static final Date RESET_SEVEN_LOGIN_DATE = DateUtil.fromDateTimeString("2019-12-03 14:31:00");
    // 统计初始化时间
    public static final Date STATISTIC_INIT_TIME = DateUtil.fromDateTimeString("2020-04-29 09:35:00");
    // 充值统计初始化时间
    public static final Date RECHARGE_STATISTIC_INIT_TIME = DateUtil.fromDateTimeString("2020-10-10 00:00:00");
    // 重新初始化城池和卡牌统计数据
    public static final Date REINIT_CITY_CARD_STATISTIC_TIME = DateUtil.fromDateTimeString("2099-09-17 15:00:00");
    // 神仙大会3.0仙豆转时限
    public static final Date SXDH3_0 = DateUtil.fromDateTimeString("2020-04-29 11:10:00");
    // 迁移神仙大会仙豆
    public static final Date MIGRATE_SXDH_BEAN = DateUtil.fromDateTimeString("2020-03-19 15:20:00");
    // 迁移神仙大会门票
    public static final Date MIGRATE_SXDH_TICKET_TIME = DateUtil.fromDateTimeString("2020-07-17 12:00:00");
    // 将哼哈二将的奖励提前发放，保证后面更新后不需要处理玩家奖励问题
    public static final Date FIRST_AWARD_SEND_08V1 = DateUtil.fromDateTimeString("2020-08-18 12:00:00");
    // 旧节日兑换记录删除
    public static final Date CLEAN_HOLIDAY_MALL_RECORD_TIME = DateUtil.fromDateTimeString("2020-11-17 10:00:00");
    // 修复节日每充值10元状态异常
    public static final Date REPAIR_PER_ACCR_TIME = DateUtil.fromDateTimeString("2022-04-28 12:00:00");
    // 清除UserHolidayLottery数据
    public static final Date CLEAN_USER_HOLIDAY_LOTTERY = DateUtil.fromDateTimeString("2020-10-01 12:00:00");
    // 修复主线任务进度
    public static final Date REPAIR_MAIN_TASK_VALUE = DateUtil.fromDateTimeString("2020-09-29 22:00:00");
    // 源晶转化
    public static final Date YUAN_JING_CAST_TIME = DateUtil.fromDateTimeString("2020-11-03 16:10:00");
    // 修复成就状态
    public static final Date REPAIR_ACHIEVEMENT_STATUS_TIME = DateUtil.fromDateTimeString("2099-01-01 00:00:00");
    // 修复成就
    public static final Date REPAIR_2107V2_TIME = DateUtil.fromDateTimeString("2021-08-20 14:30:00");
    // 重新初始化抽卡统计
    public static final Date REINIT_DRAW_CARD_STATISTIC_TIME = DateUtil.fromDateTimeString("2020-11-26 12:00:00");
    // 抵用券过期
    public static final Date VOUCHER_EXPIRE_TIME = DateUtil.fromDateTimeString("2099-12-01 00:00:00");
    // 修复卡池
    public static final Date REPAIR_CARD_POOL = DateUtil.fromDateTimeString("2099-12-01 18:00:00");
    // 重置新手任务
    public static final Date RESET_NEWER_TASK_TIME = DateUtil.fromDateTimeString("2021-01-15 09:30:00");
    // 补发七日之约奖励
    public static final Date REISSUE_SEVEN_LOGIN_AWARD_TIME = DateUtil.fromDateTimeString("2020-12-19 18:00:00");
    // 新手任务奖励修改
    public static final Date REPAIR_GROW_TASK_TIME = DateUtil.fromDateTimeString("2020-12-24 23:59:59");
    // 碧游宫秘传统计
    public static final Date REINIT_BIYOU_STATISTIC_TIME = DateUtil.fromDateTimeString("2021-04-29 09:40:00");
    // 主角卡装备统计
    public static final Date REINIT_EQUIPMENT_STATISTIC_TIME = DateUtil.fromDateTimeString("2021-06-10 15:30:00");
    // 开启梦魇世界
    public static final Date SEND_NIGHTMARE_AWARD = DateUtil.fromDateTimeString("2021-09-02 14:35:00");
    // 重新计算梦魇难度
    public static final Date REST_NIGHTMARE_SETTLE = DateUtil.fromDateTimeString("2021-09-02 14:35:00");
    // 重新初始化梦魇城池统计
    public static final Date REINIT_NIGHTMARE_STATISTIC_TIME = DateUtil.fromDateTimeString("2021-09-02 14:35:00");
    // 上仙试炼修复
    public static final Date GOD_TRAINING_REPAIR_TIME = DateUtil.fromDateTimeString("2021-02-09 09:40:00");
    // 封神台统计
    public static final Date REINIT_FST_STATISTIC_TIME = DateUtil.fromDateTimeString("2021-07-14 14:40:00");
    //迁移主角卡数据
    public static final Date LEADER_CARD_SKILLS_MIGRATE = DateUtil.fromDateTimeString("2021-10-27 14:40:00");
    //修复玩家卡组数据
    public static final Date REPAIR_CARD_GROUP = DateUtil.fromDateTimeString("2021-12-10 09:40:00");
    //发放商会关闭补偿奖励
    public static final Date COC_CLOSE_AWARD_SEND = DateUtil.fromDateTimeString("2022-01-27 14:40:00");
    //妖族数据修复
    public static final Date YAO_ZU_DATA_REPAIR = DateUtil.fromDateTimeString("2022-01-27 14:40:00");
    //主角卡技能修复
    public static final Date LEADER_CARD_LV_REPAIR_TIME = DateUtil.fromDateTimeString("2022-01-27 14:40:00");
    //法坛统计数据修复
    public static final Date FA_TAN_STATISTIC_REPAIR = DateUtil.fromDateTimeString("2022-03-03 20:30:00");
    //2203v2版本新增成就进度更新
    public static final Date REPAIR_2203V2_TIME = DateUtil.fromDateTimeString("2022-03-31 15:10:00");
    //群星册新增成就进度更新
    public static final Date REPAIR_FLOCKSTAR_BOOK_TIME = DateUtil.fromDateTimeString("2022-05-17 06:00:00");
    // 重置限时卡池许愿值（重新开活动时需要）
    public static final Date RESET_CARD_SHOP_WISH = DateUtil.fromDateTimeString("2023-01-22 00:00:00");
    // 修复五行神将礼包保底次数
    public static final Date RESET_WX_SHENG_RECORD = DateUtil.fromDateTimeString("2022-05-19 10:25:00");
    /** 重置元宝礼包记录 */
    public static final Date RESET_GOLD_GIFT_RECORD = DateUtil.fromDateTimeString("2022-06-20 16:01:00");
    /** 修复技能组 */
    public static final Date REPAIR_SKILL_GROUP = DateUtil.fromDateTimeString("2022-09-05 18:45:00");
    /** 初始化数据 */
    public static final Date REPAIR_INIT_DATA = DateUtil.fromDateTimeString("2022-11-10 18:00:00");
    /** 重置玩家诛仙阵 */
    public static final Date RESET_USER_ZXZ = DateUtil.fromDateTimeString("2023-02-17 20:00:00");
    /** 重置限时卡池许愿卡（更换许愿卡时需要） */
    public static final Date RESET_LIMIT_CARD_SHOP = DateUtil.fromDateTimeString("2023-01-22 00:00:00");

}
