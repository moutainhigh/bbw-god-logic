package com.bbw.god.game.zxz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关卡枚举
 * @author: hzf
 * @create: 2022-09-17 16:09
 **/
@AllArgsConstructor
@Getter
public enum ZxzDefenderEnum {
    DEFENDER_1(1,"普通野怪1"),
    DEFENDER_2(2,"普通野怪2"),
    DEFENDER_3(3,"精英野怪3"),
    DEFENDER_4(4,"普通野怪4"),
    DEFENDER_5(5,"普通野怪5"),
    DEFENDER_6(6,"首领野怪6"),

    ;

    private int defenderId;
    private String describe;


    public static ZxzDefenderEnum fromZxzKind(int defenderId) {
        for (ZxzDefenderEnum way:values()) {
            if (way.getDefenderId() == defenderId) {
                return way;
            }
        }
        return null;
    }

}
