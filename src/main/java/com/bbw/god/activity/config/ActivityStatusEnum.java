package com.bbw.god.activity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动状态
 *
 * @author suhq
 * @date 2019年3月3日 下午11:20:32
 */
@Deprecated
@Getter
@AllArgsConstructor
public enum ActivityStatusEnum {

    ENABLE_REPLENISH0("可补领", -4),
    READY_REPLENISH0("待补领", -3),
    TIME_OUT0("已过期", -2),
    UNAWARD0("不能领取", -1),
    ENABLE_AWARD0("未领取", 0),
    AWARDED0("已领取", 1);

    private String name;
    private int value;

    public static ActivityStatusEnum fromValue(int value) {
        for (ActivityStatusEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
