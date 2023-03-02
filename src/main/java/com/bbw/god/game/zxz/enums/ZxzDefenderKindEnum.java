package com.bbw.god.game.zxz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 诛仙阵野怪种类枚举
 * @author: hzf
 * @create: 2022-09-17 16:09
 **/
@AllArgsConstructor
@Getter
public enum ZxzDefenderKindEnum {
    KIND_10(10,"普通野怪"),
    KIND_20(20,"精英野怪"),
    KIND_30(30,"首领野怪"),

    ;

    private int kind;
    private String describe;


    public static ZxzDefenderKindEnum fromZxzKind(int kind) {
        for (ZxzDefenderKindEnum way:values()) {
            if (way.getKind() == kind) {
                return way;
            }
        }
        return null;
    }

}
