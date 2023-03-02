package com.bbw.god.gameuser.card.equipment.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仙诀类型枚举
 *
 * @author: huanghb
 * @date: 2022/9/17 14:22
 */
@Getter
@AllArgsConstructor
public enum XianJueTypeEnum {
    YU_QI_JUE("御器决", 10),
    KONG_BAO_SHU("控宝术", 20);
    private final String name;
    private final int value;


    public static XianJueTypeEnum fromValue(int value) {
        for (XianJueTypeEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

}
