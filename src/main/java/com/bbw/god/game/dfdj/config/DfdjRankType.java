package com.bbw.god.game.dfdj.config;

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
public enum DfdjRankType {
    RANK("赛季", 30),
    MIDDLE_RANK("季中", 31),
    PHASE_RANK("赛季阶段排行", 32),
    LAST_PHASE_RANK("赛季前一个阶段排行", 33),
    ;

    private final String name;
    private final int value;

    public static DfdjRankType fromValue(int value) {
        for (DfdjRankType item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
