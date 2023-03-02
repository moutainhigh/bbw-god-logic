package com.bbw.god.gameuser.card.equipment.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 至宝枚举
 *
 * @author: huanghb
 * @date: 2022/9/17 14:22
 */
@Getter
@AllArgsConstructor
public enum ZhiBaoEnum {
    NONE("无", 0),
    FA_QI("法器", 10),

    LING_BAO("灵宝", 20);

    private final String name;
    private final int value;


    public static ZhiBaoEnum fromValue(int value) {
        for (ZhiBaoEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

}
