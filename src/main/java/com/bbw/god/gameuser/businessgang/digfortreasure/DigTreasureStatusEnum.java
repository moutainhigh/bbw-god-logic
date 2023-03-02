package com.bbw.god.gameuser.businessgang.digfortreasure;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 挖宝状态枚举;
 *
 * @author: huanghb
 * @date: 2022/1/18 14:47
 */
@Getter
@AllArgsConstructor
public enum DigTreasureStatusEnum {
    CAN_DIG_TREASURE("可以挖宝", 0),
    DUG_TREASURE("已挖宝", 1);
    private final String name;
    /** 挖宝状态 */
    private final int digTreasureStatus;

    public static DigTreasureStatusEnum fromValue(int value) {
        for (DigTreasureStatusEnum resultLevel : values()) {
            if (resultLevel.getDigTreasureStatus() == value) {
                return resultLevel;
            }
        }
        return null;
    }
}
