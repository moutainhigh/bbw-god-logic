package com.bbw.god.activity.holiday.lottery;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 节日抽奖类型
 * @date 2020/9/21 9:08
 **/
@Getter
@AllArgsConstructor
public enum HolidayLotteryType {
    QMRG("驱魔荣光|雨露均沾", 10),
    ZQBB("中秋博饼", 20),
    WQCY("五气朝元", 30),
    FCJB("丰财聚宝", 40),
    JFNF("金虎纳福", 50),
    ;
    private final String name;
    private final Integer value;
}
