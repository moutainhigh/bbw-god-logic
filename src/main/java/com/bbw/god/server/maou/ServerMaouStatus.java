package com.bbw.god.server.maou;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suhq
 * @description: 魔王状态
 * @date 2019-12-20 15:14
 **/
@Getter
@AllArgsConstructor
public enum ServerMaouStatus {

    PEACE("平安期", 0),
    ASSEMBLY("集结期", 10),
    ATTACKING("攻打中", 20),
    KILLED("已击杀", 30),
    LEAVE("已逃走", 40),
    OVER("魔王已结束", 50),
    ;

    private String name;
    private int value;

    public static ServerMaouStatus fromValue(int value) {
        for (ServerMaouStatus item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
