package com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 藏宝图等级枚举
 *
 * @author: huanghb
 * @date: 2021/12/16 21:35
 */
@Getter
@AllArgsConstructor
public enum TreasureTroveMapLevelEnum {
    LOW_LEVEL("低等级", 1, 0),
    MIDDLE_LEVEL("中等级", 2, 1),
    HIGH_LEVEL("高等级", 3, 2);

    private final String name;
    /** 藏宝图等级 */
    private final int level;
    /** 藏宝图等级下标 */
    private final int levelIndex;


    public static TreasureTroveMapLevelEnum fromValue(int level) {
        for (TreasureTroveMapLevelEnum item : values()) {
            if (item.getLevel() == level) {
                return item;
            }
        }
        return null;
    }
}
