package com.bbw.god.rechargeactivities;

import com.bbw.exception.ExceptionForClientTip;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author lwb
 * @date 2020/7/1 16:06
 */
@Getter
@AllArgsConstructor
public enum RechargeActivityItemEnum implements Serializable {
    //礼包类
    DIAMOND_GIFT_PACK(1000, "钻石礼包"),
    DAILY_GIFT_PACK(1010, "每日礼包"),
    WEEKLY_GIFT_PACK(1020, "每周礼包"),
    MONTHLY_GIFT_PACK(1030, "每月礼包"),
    GOLD_GIFT_PACK(1040, "元宝礼包"),
    SHEN_LI_HENG_SAO_PACK(1050, "神力横扫"),
    TE_HUI_PACK(1060, "特惠礼包"),
    ROLE_TIME_LIMIT_BAG(1070, "限时礼包"),
    //卡包类
    MONTH_CARD(2010, "月/季卡"),
    SU_ZHAN_CARD(2020, "速战卡"),
    LING_YIN(2030, "天灵印"),
    DI_LING_YIN(2040, "地灵印"),
    //战令类
    WAR_TOKEN_TASK(3010, "任务"),
    WAR_TOKEN_EXCHANGE(3020, "兑换"),
    WAR_TOKEN_LEVEL_AWARD(3030, "奖励"),
    //充值签到
    RECHARGE_SIGN_BAG(4010, "充值签到礼包"),

    //每日摇一摇
    DAILY_SHAKE(5010, "每日摇一摇"),
    ;
    private final int type;
    private final String memo;

    public static RechargeActivityItemEnum fromVal(int val) {
        for (RechargeActivityItemEnum activityEnum : values()) {
            if (activityEnum.getType() == val) {
                return activityEnum;
            }
        }
        throw new ExceptionForClientTip("rechargeActivity.not.exist", val);
    }
}
