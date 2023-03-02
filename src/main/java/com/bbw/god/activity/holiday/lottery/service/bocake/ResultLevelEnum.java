package com.bbw.god.activity.holiday.lottery.service.bocake;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抽奖结果（结果级别）枚举
 *
 * @author: huanghb
 * @date: 2022/1/11 10:38
 */
@Getter
@AllArgsConstructor
public enum ResultLevelEnum {
    ZY("状元", 10),
    BY("榜眼", 20),
    TH("探花", 30),
    JS("进士", 40),
    JR("举人", 50),
    XC("秀才", 60),
    PARTICIPATE("参与奖", 70),
    ;
    private final String name;
    private final Integer value;

    public static ResultLevelEnum fromValue(int value) {
        for (ResultLevelEnum resultLevel : values()) {
            if (resultLevel.getValue() == value) {
                return resultLevel;
            }
        }
        return null;
    }
}
