package com.bbw.god.activity.holiday.lottery;

import lombok.Data;

/**
 * @author suchaobin
 * @description 节日抽奖参数
 * @date 2020/12/22 15:17
 **/
@Data
public class HolidayLotteryParam {
    // 类型
    private Integer type;
    /**
     * 五气朝元的珠子排序集合
     */
    private String result;
    /**
     * 锁住的奖励
     */
    private String lockAwardIds;
    /**
     * 抽奖次数 默认单抽，10连抽需要传10
     */
    private Integer drawTimes=1;
}
