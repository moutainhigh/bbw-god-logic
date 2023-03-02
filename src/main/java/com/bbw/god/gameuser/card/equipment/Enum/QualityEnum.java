package com.bbw.god.gameuser.card.equipment.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 品质枚举
 *
 * @author: huanghb
 * @date: 2022/9/15 9:57
 */
@Getter
@AllArgsConstructor
public enum QualityEnum {
    NONE("无", 0),

    FAN_PIN("凡品", 10),

    MIDDLE_GRADE("中品", 20),

    TOP_GRADE("上品", 30),

    BOUTIQUE("精品", 40),

    BEST_QUALITY("极品", 50),

    FAIRY("仙品", 60);

    private final String name;
    private final int value;
}
