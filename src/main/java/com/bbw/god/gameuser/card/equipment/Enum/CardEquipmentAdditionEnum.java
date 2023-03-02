package com.bbw.god.gameuser.card.equipment.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 装备加成枚举
 *
 * @author: huanghb
 * @date: 2022/9/17 14:22
 */
@Getter
@AllArgsConstructor
public enum CardEquipmentAdditionEnum {

    ATTACK("攻击", 10),
    DEFENSE("防御", 20),
    STRENGTH_RATE("强度加成", 30),
    TENACITY_RATE("韧度加成", 40),
    STRENGTH("强度", 50),
    TENACITY("韧度", 60),
    ;

    private final String name;
    private final int value;


    public static CardEquipmentAdditionEnum fromValue(int value) {
        for (CardEquipmentAdditionEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

}
