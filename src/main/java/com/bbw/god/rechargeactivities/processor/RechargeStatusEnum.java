package com.bbw.god.rechargeactivities.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author：lwb
 * @date: 2020/12/30 15:25
 * @version: 1.0
 */
@Getter
@AllArgsConstructor
public enum RechargeStatusEnum {
    /** 不可以购买 */
    DONE(-1),
    /** 可以购买 */
    CAN_BUY(0),
    /** 可以领奖 */
    CAN_GAIN_AWARD(1)
    ;
    private int status;
}
