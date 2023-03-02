package com.bbw.god.game.maou.Enum;

import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 鞭炮类型枚举
 *
 * @author: suhq
 * @date: 2022/1/7 9:57 上午
 */
@Getter
@AllArgsConstructor
public enum FirecrackerTypeEnum {
    FAIRY_STICK("仙女棒", 20, TreasureEnum.FAIRY_STICK.getValue()),
    CTH("窜天猴", 50, TreasureEnum.CTH.getValue()),
    ER_TJ("二踢脚", 120, TreasureEnum.ETJ.getValue()),
    DD_RED("大地红", 300, TreasureEnum.DD_RED.getValue()),
    ;
    /** 鞭炮名称 */
    private final String name;
    /** 鞭炮伤害值 */
    private final int hurtValue;
    /** 鞭炮法宝ID */
    private final int treasureId;

    public static FirecrackerTypeEnum fromTreasureId(int treasureId) {
        for (FirecrackerTypeEnum item : values()) {
            if (item.getTreasureId() == treasureId) {
                return item;
            }
        }
        return null;
    }
}
