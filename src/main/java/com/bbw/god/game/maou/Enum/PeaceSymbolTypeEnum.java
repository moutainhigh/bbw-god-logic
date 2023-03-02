package com.bbw.god.game.maou.Enum;

import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 平安符类型枚举
 *
 * @author: suhq
 * @date: 2022/1/7 9:57 上午
 */
@Getter
@AllArgsConstructor
public enum PeaceSymbolTypeEnum {
    FU_BDBQ("符•百毒不侵", 20, TreasureEnum.FU_BDBQ.getValue()),
    FU_ZXTS("符•诸邪退散", 50, TreasureEnum.FU_ZXTS.getValue()),
    FU_APCHY("符•爱拼才会赢", 120, TreasureEnum.FU_APCHY.getValue()),
    FU_PAWDZ("符•平安无代志", 300, TreasureEnum.FU_PAWDZ.getValue()),
    ;
    /** 平安符名称 */
    private final String name;
    /** 平安符伤害值 */
    private final int hurtValue;
    /** 平安符名称法宝ID */
    private final int treasureId;

    public static PeaceSymbolTypeEnum fromTreasureId(int treasureId) {
        for (PeaceSymbolTypeEnum item : values()) {
            if (item.getTreasureId() == treasureId) {
                return item;
            }
        }
        return null;
    }
}
