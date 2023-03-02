package com.bbw.god.game.maou.Enum;

import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 驯鹿食物类型枚举
 *
 * @author: suhq
 * @date: 2022/1/7 9:57 上午
 */
@Getter
@AllArgsConstructor
public enum ReindeerFoodTypeEnum {
    MUSHROOM("蘑菇", 20, TreasureEnum.MUSHROOM.getValue()),
    LITMUS("石蕊", 50, TreasureEnum.LITMUS.getValue()),
    DEER_GRASS("鹿草", 120, TreasureEnum.DEER_GRASS.getValue()),
    BERRIES("浆果", 300, TreasureEnum.BERRIES.getValue()),
    ;
    /** 驯鹿食物名称 */
    private final String name;
    /** 驯鹿食物伤害值 */
    private final int hurtValue;
    /** 驯鹿食物法宝ID */
    private final int treasureId;

    public static ReindeerFoodTypeEnum fromTreasureId(int treasureId) {
        for (ReindeerFoodTypeEnum item : values()) {
            if (item.getTreasureId() == treasureId) {
                return item;
            }
        }
        return null;
    }
}
