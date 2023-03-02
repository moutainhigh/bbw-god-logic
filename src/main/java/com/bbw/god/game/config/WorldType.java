package com.bbw.god.game.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 世界类型枚举
 * @date 2020/9/16 13:58
 **/
@Getter
@AllArgsConstructor
public enum WorldType {
    NORMAL("普通世界", 10),
    NIGHTMARE("梦魇世界", 20),
    TRANSMIGRATION("轮回世界", 30),
    ;
    private String name;
    private int value;

    public static WorldType fromValue(int value) {
        for (WorldType item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
