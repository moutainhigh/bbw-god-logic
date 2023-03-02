package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 藏宝秘境奖池枚举
 *
 * @author: huanghb
 * @date: 2021/12/16 21:35
 */
@Getter
@AllArgsConstructor
public enum TreasureTroveEnum {
    ORDINARY_PRIZES("普通奖池", 9),
    GRAND_PRIZES("大奖池", 3);
    private final String name;
    private final int awardNum;
}
