package com.bbw.god.game.zxz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 诛仙阵四圣挑战野怪种类枚举
 * @author: hzf
 * @create: 2022-12-29 09:44
 **/
@AllArgsConstructor
@Getter
public enum ZxzFourSaintsDefenderKindEnum {
    KIND_10(10,"眷属野怪"),
    KIND_20(20,"圣兽野怪"),

    ;

    private int kind;
    private String describe;


    public static ZxzFourSaintsDefenderKindEnum fromZxzKind(int kind) {
        for (ZxzFourSaintsDefenderKindEnum way:values()) {
            if (way.getKind() == kind) {
                return way;
            }
        }
        return null;
    }

}
