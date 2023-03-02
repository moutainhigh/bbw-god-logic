package com.bbw.god.gameuser.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务类型
 *
 * @author suhq
 * @date 2019年2月21日 上午11:18:46
 */
@Getter
@AllArgsConstructor
public enum TaskTypeEnum {

    MAP_TIP("地图红点", 0),
    NEWER_TASK("新手任务", 10),
    JINJIE_TASK("进阶任务", 20),
    DAILY_TASK("每日任务", 30),
    DAILY_BOX_TASK("每日任务箱子", 40),
    MAIN_TASK("主线任务", 50),
    HERO_BACK_TASK("回归每日任务", 60),
    HERO_BACK_BOX_TASK("回归每日宝箱", 70),
    SXDH_SEASON_TASK("神仙大会赛季挑战", 80),
    DFDJ_SEASON_TASK("巅峰对决赛季挑战", 90),
    GOD_TRAINING_TASK("试炼任务", 100),
    /** 客户端获取村庄任务列表传 110 */
    TIME_LIMIT_NORMAL("时效-常规", 110),
    TIME_LIMIT_DISPATCH_TASK("时效-派遣任务", 120),
    TIME_LIMIT_FIGHT_TASK("时效-特殊战斗任务", 130),
    /** 客户端传参数使用 */
    WAN_SHENG_JIE_TASK("万圣节任务", 140),
    /** 客户端传参数使用 */
    THANKS_GIVING_TASK("感恩节限时任务", 150),
    NEW_YEAR_AND_CHRISTMAS_TASK("双旦村庄限时任务", 160),
    CELEBRATION_INVITATION_TASK("庆典邀约限时任务", 170),
    SPRING_FESTIVAL_TASK("春节限时任务", 180),
    BUSINESS_GANG_DISPATCH_TASK("商帮派遣任务", 190),
    BUSINESS_GANG_SHIPPING_TASK("商帮运送任务", 200),
    BUSINESS_GANG_WEEKLY_TASK("商帮周常任务", 210),
    BIG_GOD_PLAN("大仙计划", 220),
    ANNUAL_GIFT_DAILY_TASK("锦礼每日任务", 230),
    QING_MING_TASK("清明限时任务", 240),
    DRAGON_BOAT_FESTIVAL_TASK("端午限时任务", 250),
    BUSINESS_GANG_YINGJIE_TASK("商帮英杰任务", 260),
    HALLOWEEN_RESTAURANT_LIMIT_TASK("万圣餐厅限时任务", 270),
    GUESS_DAILY_TASK("竞猜每日任务", 280),
    PAI_LI_FAWN_51("派礼小鹿-51", 290),

    ;

    private final String name;
    private final int value;

    public static TaskTypeEnum fromValue(int value) {
        for (TaskTypeEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
