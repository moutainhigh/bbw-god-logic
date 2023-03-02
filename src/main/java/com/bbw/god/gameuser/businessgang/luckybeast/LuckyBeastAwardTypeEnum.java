package com.bbw.god.gameuser.businessgang.luckybeast;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 招财兽奖励类别枚举;
 *
 * @author: huanghb
 * @date: 2022/1/18 14:47
 */
@Getter
@AllArgsConstructor
public enum LuckyBeastAwardTypeEnum {
    TQ("铜钱", 0),
    BY("元宝", 1),
    RYTB("荣耀铜币", 2),
    ;
    private final String name;
    private final Integer type;

    public static LuckyBeastAwardTypeEnum fromValue(int value) {
        for (LuckyBeastAwardTypeEnum resultLevel : values()) {
            if (resultLevel.getType() == value) {
                return resultLevel;
            }
        }
        return null;
    }
}
