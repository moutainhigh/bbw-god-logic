package com.bbw.god.game.dfdj.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 段位
 *
 * @author suhq
 * @date 2019-07-16 09:25:38
 */
@Getter
@AllArgsConstructor
public enum DfdjSegment {

    //	INIT("初始段位", 0),
    ONE("段位1", 10),
    TWO("段位2", 20),
    THREE("段位3", 30),
    FOUR("段位4", 40),
    FIVE("段位5", 50),
    SIX("段位6", 60),
    SEVEN("段位7", 70);

    private final String name;
    private final int value;

    public static DfdjSegment fromValue(int value) {
        for (DfdjSegment item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
