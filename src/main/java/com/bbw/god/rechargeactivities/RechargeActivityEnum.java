package com.bbw.god.rechargeactivities;

import com.bbw.exception.ExceptionForClientTip;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author lwb
 * @date 2020/7/1 15:54
 */
@Getter
@AllArgsConstructor
public enum RechargeActivityEnum implements Serializable {
    NONE(0, "无"),
    @Deprecated
    GIFT_PACK(1000, "礼包"),
    CARD_PACK(2000, "卡包"),
    WAR_TOKEN(3000, "战令"),
    DIAMOND_PACK(5000, "钻石"),
    RECHARGE_SIGN_BAG(4000, "充值签到礼包"),
    TE_HUI_PACK(6000, "特惠礼包"),
    ;
    private int type;
    private String memo;

    public static RechargeActivityEnum fromVal(int val) {
        for (RechargeActivityEnum activityEnum : values()) {
            if (activityEnum.getType() == val) {
                return activityEnum;
            }
        }
        throw new ExceptionForClientTip("rechargeActivity.not.exist", val);
    }
}
