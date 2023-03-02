package com.bbw.god.game.maou.Enum;

import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 祛暑道具枚举
 *
 * @author: huanghb
 * @date: 2022/7/5 17:46
 */
@Getter
@AllArgsConstructor
public enum AntiHeatPropsTypeEnum {
    SMALL_FOLDING_FAN("小扇子", 20, TreasureEnum.SMALL_FOLDING_FAN.getValue()),
    WATER_PISTOL("喷水手枪", 50, TreasureEnum.WATER_PISTOL.getValue()),
    COOLING_ICE_PACK("降温冰袋", 120, TreasureEnum.COOLING_ICE_PACK.getValue()),
    BALLOON_WATER_EGG("气球水蛋", 300, TreasureEnum.BALLOON_WATER_EGG.getValue()),
    ;
    /** 平安符名称 */
    private final String name;
    /** 平安符伤害值 */
    private final int hurtValue;
    /** 平安符名称法宝ID */
    private final int treasureId;

    public static AntiHeatPropsTypeEnum fromTreasureId(int treasureId) {
        for (AntiHeatPropsTypeEnum item : values()) {
            if (item.getTreasureId() == treasureId) {
                return item;
            }
        }
        return null;
    }
}
