package com.bbw.god.activity.holiday.processor.holidaycelebration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全服庆典积分枚举
 *
 * @author: huanghb
 * @date: 2021/12/16 21:35
 */
@Getter
@AllArgsConstructor
public enum CelebrationPointsEnum {
    ERSHIW_CELEBRATION_POINTS("6千庆典积分", 6000, 100839),
    WUSHIW_CELEBRATION_POINTS("1万5千庆典积分", 15000, 100840),
    YIBAIW_CELEBRATION_POINTS("3万庆典积分", 30000, 100841),
    YIBAIWSW_CELEBRATION_POINTS("4万5千庆典积分", 45000, 100842),
    ERBAIW_CELEBRATION_POINTS("6万庆典积分", 60000, 100843),
    SANBAIW_CELEBRATION_POINTS("9万庆典积分", 90000, 100844),
    SIBAIW_CELEBRATION_POINTS("12万庆典积分", 120000, 100845);

    private final String name;
    private final int targetProgress;
    private final int activityId;

    public static CelebrationPointsEnum fromValue(int value) {
        for (CelebrationPointsEnum item : values()) {
            if (item.getActivityId() == value) {
                return item;
            }
        }
        return null;
    }
}
