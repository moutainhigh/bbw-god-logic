package com.bbw.god.activity.holiday.lottery.service.bocake;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 王中王等级枚举
 *
 * @author: huanghb
 * @date: 2022/1/11 10:10
 */
@Getter
@AllArgsConstructor
public enum WangZhongWangLevelEnum {
    FIRST("一等奖", 110),
    SECOND("二等奖", 120),
    THIRD("三等奖", 130),
    FOURTH("四等奖", 140),
    PARTICIPATE("参与奖", 150),
    ;
    private final String name;
    private final Integer value;

    protected static WangZhongWangLevelEnum getLevel(String number, List<String> first, List<String> second, List<String> third, List<String> fourth) {
        if (first.contains(number)) {
            return WangZhongWangLevelEnum.FIRST;
        }
        if (second.contains(number)) {
            return WangZhongWangLevelEnum.SECOND;
        }
        if (third.contains(number)) {
            return WangZhongWangLevelEnum.THIRD;
        }
        if (fourth.contains(number)) {
            return WangZhongWangLevelEnum.FOURTH;
        }
        return WangZhongWangLevelEnum.PARTICIPATE;
    }
}