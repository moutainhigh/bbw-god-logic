package com.bbw.god.game.combat.attackstrategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * 策略类型
 * @author lwb
 */

@Getter
@AllArgsConstructor
public enum StrategyEnum {
    NEWEST(10,"newest",7),//最新
    CARDS_MIN(20,"cards",1),//携带卡牌最少
    ROUND_MIN(30,"round",1),//回合最少
    USE_WEAPON_ROUND_MIN(40,"weaponRound",1),//使用法宝后回合最少
    SPECIAL_CARD_ROUND_MIN(50,"spcCardRound",1),//修改过卡牌且回合数最少
    USER_LV_MIN(60,"userLv",1);//召唤师等级最低
    /**
     * 类型数值
     */
    private int val;
    /**
     * 保存的key关键字
     */
    private String key;
    /**
     * 最多显示给玩家的数量
     */
    private int showNum;

    /**
     * 根据值获取类型
     * 当不存在时  返回NEWEST 最新
     * @param val
     * @return
     */
    public static StrategyEnum fromVal(int val){
        for (StrategyEnum strategyEnum : values()) {
            if (strategyEnum.getVal()==val){
                return strategyEnum;
            }
        }
        return NEWEST;
    }

}
