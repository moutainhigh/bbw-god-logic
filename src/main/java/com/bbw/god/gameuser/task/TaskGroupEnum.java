package com.bbw.god.gameuser.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年11月21日 下午5:30:14
 * 类说明  获取等级对应的任务列表起始Id
 */
@Getter
@AllArgsConstructor
public enum TaskGroupEnum {
    TASK_MAIN("主线任务", 500),
    TASK_NEWBIE("新手任务", 10000),
    TASK_1_9("10级以下每日任务", 24000),
    TASK_10_14("10-14级每日任务", 21000),
    TASK_15_19("15-19级每日任务", 25000),
    TASK_20_39("20-39级每日任务", 22000),
    TASK_40_999("40级以上任务", 23000),
    HERO_BACK("英雄回归每日任务", 60000),
    SXDH_SEASON_TASK("神仙大会赛季挑战", 70000),
    DFDJ_SEASON_TASK("巅峰对决赛季挑战", 80000),
    GOD_TRAINING("上仙试炼", 90000),
    CUN_ZHUANG_TASK("村庄任务", 100000),
    WAN_SHENG_JIE_TASK("万圣节限时任务", 110000),
    THANKS_GIVING_TASK("感恩节限时任务", 120000),
    NEW_YEAR_AND_CHRISTMAS_TASK("双旦限时任务", 130000),
    CELEBRATION_INVITATION_TASK("庆典邀约限时任务", 131000),
    SPRING_FESTIVAL_TASK("春节限时任务", 140000),
    BUSINESS_GANG_SPECIALTY_SHIPPING_TASK("商帮特产运送任务", 150000),
    BUSINESS_GANG_WEEKLY_TASK("商帮每周任务", 160000),
    BUSINESS_GANG_DISPATCH_TASK("商帮派遣任务", 170000),
    BIG_DOG_PLAN("大仙计划", 180000),
    BROCADE_GIFT_DAILY_TASK("锦礼每日任务", 190000),
    QING_MING_TASK("清明限时任务", 200000),
    DRAGON_BOAT_FESTIVAL_TASK("端午节限时任务", 210000),
    BUSINESS_GANG_YINGJIE_TASK("商帮英杰任务", 220000),
    HALLOWEEN_RESTAURANT_LIMIT_TASK("万圣餐厅限时任务", 230000),
    GUESS_DAILY_TASK("竞猜每日任务", 240000),
    PAI_LI_FAWN_51("派礼小鹿-51", 250000),


    ;


    private final String name;
    private final int value;

    public static TaskGroupEnum fromValue(int value) {
        for (TaskGroupEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public static TaskGroupEnum fromLv(int lv) {
        if (lv < 10) {
            return TASK_1_9;
        }
        if (lv < 15) {
            return TASK_10_14;
        }
        if (lv < 20) {
            return TASK_15_19;
        }

        if (lv < 40) {
            return TASK_20_39;
        }
        return TASK_40_999;
    }
}
