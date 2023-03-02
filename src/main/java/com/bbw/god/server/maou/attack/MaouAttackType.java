package com.bbw.god.server.maou.attack;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suhq
 * @description: 魔王攻击类型
 * @date 2019-12-20 15:14
 **/
@Getter
@AllArgsConstructor
public enum MaouAttackType {

    COMMON_ATTACK("常规攻击", 0),
    ONE_ATTACKS_WITH_GOLD("元宝1倍攻击", 1),
    DOUBLE_ATTACKS_WITH_GOLD("元宝多倍攻击", 2),
    ;

    private String name;
    private int value;

    public static MaouAttackType fromValue(int value) {
        for (MaouAttackType item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
