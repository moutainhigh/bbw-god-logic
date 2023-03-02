package com.bbw.god.server.maou.alonemaou.maouskill;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suhq
 * @description: 魔王攻击类型
 * @date 2019-12-20 15:14
 **/
@Getter
@AllArgsConstructor
public enum MaouSkillEnum {

    NO("未设置", 99999),

    SAME_CARD_TYPE("只能上阵与魔王属性相同的卡牌", 100),

    XIANGKE_REVERSE("属性克制反转", 200),

    IGNORE_LIAN_GONG("联攻无效", 301),
    IGNORE_EXTRA_ATK_BUF("上阵卡牌所有加成无效", 302),

    REDUCE_BLOOD("减伤10%", 401),
    RECOVER_BLOOD("每2个回合，魔王恢复2%最大血量", 402),

    SHIELD("护盾", 501),
    WU_XIANG("附带技能-无相", 502),
    JIA_SUO("附带技能-枷锁", 503),
    LING_DONG("附带技能-灵动", 504),
    ;

    private String name;
    private int value;

    public static MaouSkillEnum fromValue(int value) {
        for (MaouSkillEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
