package com.bbw.god.server.maou.bossmaou;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suhq
 * @description: 魔王种类
 * @date 2019-12-20 15:14
 **/
@Getter
@AllArgsConstructor
public enum BossMaouLevel {

    HunDML("混沌魔灵", 10),
    ShenYMZ("深渊魔尊", 20),
    ;

    private String name;
    private int value;

    public static BossMaouLevel fromValue(int value) {
        for (BossMaouLevel item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
