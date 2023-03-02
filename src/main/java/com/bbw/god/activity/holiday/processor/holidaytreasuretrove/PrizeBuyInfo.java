package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import lombok.Data;

import java.io.Serializable;

/**
 * 藏宝秘境奖励购买信息
 *
 * @author: huanghb
 * @date: 2022/7/7 9:52
 */
@Data
public class PrizeBuyInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 法宝id */
    private int id;
    /** 购买次数 */
    private int buyTimes;
}
