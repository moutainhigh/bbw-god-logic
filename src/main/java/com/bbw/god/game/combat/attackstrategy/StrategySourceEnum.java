package com.bbw.god.game.combat.attackstrategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 策略源
 *
 * @author: suhq
 * @date: 2021/9/23 1:52 上午
 */
@Getter
@AllArgsConstructor
public enum StrategySourceEnum {
    /** 封神大陆攻城 */
    FSDL_ATTACK_CITY("attackCityStrategy", StrategyVO.class),
    /** 梦魇世界攻城（第二幕） */
    NIGHTMARE_ATTACK_CITY("attackNightmareCityStrategy", StrategyNightmareVO.class),
    /** 妖族来犯（第三幕）*/
    YAOZU_ATTACK("attackYaoZuStrategy", StrategyYaoZuVO.class),
    ;
    /** 保存的key关键字 */
    private String key;
    private Class<? extends AbstractStrategyVO> voClazz;

}
