package com.bbw.god.activity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 可选择奖励状态枚举
 *
 * @author: huanghb
 * @date: 2022/1/4 9:24
 */
@Getter
@AllArgsConstructor
public enum ActivityAbleChooseAwardStatusEnum {
    UNSELECTED("未选择", 0),
    SELECTED("已选", 1);
    private final String name;
    private final int value;

    public static ActivityAbleChooseAwardStatusEnum fromValue(int value) {
        for (ActivityAbleChooseAwardStatusEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
