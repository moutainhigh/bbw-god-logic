package com.bbw.god.activity.holiday.processor.HolidayGroceryShop.rd;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回盲盒奖励
 * @author: hzf
 * @create: 2022-12-12 08:49
 **/
@Data
public class RdBlindBoxAward implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 盲盒位置 */
    private Integer pos;
    /**道具类型 */
    private Integer item;
    /**奖励id */
    private Integer awardId;
    /**奖励数量  */
    private Integer num;
    /**状态 1：已领取,0:未领取*/
    private Integer status;


}
