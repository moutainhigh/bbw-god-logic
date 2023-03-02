package com.bbw.god.game.award;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物品类别
 *
 * @author suhq
 * @version 创建时间：2018年9月21日 上午9:04:07
 */
@Getter
@AllArgsConstructor
public enum AwardEnum {

    ZS("钻石", 1),
    YB("元宝", 10),
    TQ("铜钱", 20),
    TL("体力", 30),
    KP("卡牌", 40),
    YS("元素", 50),
    FB("法宝", 60),
    TC("特产", 70),
    JY("经验", 80),
    JN("技能", 90),
    HY("每日任务活跃度", 100),
    //110会与客户端冲突
    WNLS("随机万能灵石", 120),
    DFZ("巅峰值", 130),
    SXDH_SCORE("神仙大会积分", 140),
    DFDJ_SCORE("巅峰对决积分", 150),
    SLZ("试炼值", 160),

    // 以下仅用于统计
    CITY("城池", 1010),
    FRIEND("好友", 1020),
    NIGHTMARE_CITY("梦魇城池", 1030),
    ;

    private final String name;
    private final int value;

    public static AwardEnum fromValue(int value) {
        for (AwardEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public static AwardEnum fromName(String name) {
        for (AwardEnum model : values()) {
            if (model.getName().equals(name)) {
                return model;
            }
        }
        return null;
    }
}
