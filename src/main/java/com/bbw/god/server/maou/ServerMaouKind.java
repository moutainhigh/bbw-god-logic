package com.bbw.god.server.maou;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suhq
 * @description: 魔王种类
 * @date 2019-12-20 15:14
 **/
@Getter
@AllArgsConstructor
public enum ServerMaouKind {

    ALONE_MAOU("独战魔王", 10),
    BOSS_MAOU("魔王降临", 20),
    ;

    private String name;
    private int value;

    public static ServerMaouKind fromValue(int value) {
        for (ServerMaouKind item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
