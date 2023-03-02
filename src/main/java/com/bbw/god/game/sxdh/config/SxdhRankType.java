package com.bbw.god.game.sxdh.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 榜单类型
 *
 * @author suhq
 * @date 2019-06-21 11:33:21
 */
@Getter
@AllArgsConstructor
public enum SxdhRankType {

    //    TODAY_RANK("今日", 10),
//    YESTERDAY_RANK("昨日", 20),
    RANK("赛季", 30),
    MIDDLE_RANK("季中", 31),
    PHASE_RANK("赛季阶段排行", 32),
    LAST_PHASE_RANK("赛季前一个阶段排行", 33),
    ;

    private String name;
    private int value;

    public static SxdhRankType fromValue(int value) {
        for (SxdhRankType item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
