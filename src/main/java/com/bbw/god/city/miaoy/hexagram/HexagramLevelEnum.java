package com.bbw.god.city.miaoy.hexagram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 获取卦象级别
 * @author liuwenbin
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum HexagramLevelEnum {
    UP_UP(1, "上上卦"),
    MID_UP(2,"中上卦"),
    MID_MID(3,"中中卦"),
    MID_DOWN(4,"中下卦"),
    DOWN_DOWN(5,"下下卦");
    private int level;
    private String memo;
}
