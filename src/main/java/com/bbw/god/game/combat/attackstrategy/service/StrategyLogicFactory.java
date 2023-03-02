package com.bbw.god.game.combat.attackstrategy.service;

import com.bbw.god.fight.FightTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 策略逻辑工厂类
 *
 * @author: suhq
 * @date: 2021/9/23 3:01 上午
 */
@Service
public class StrategyLogicFactory {
	@Autowired
	@Lazy
	private List<AbstractStrategyLogic> strategyLogics;

	/**
	 * 根据商品类型获取物品服务实现对象
	 *
	 * @param fightType
	 * @return
	 */
	public AbstractStrategyLogic getLogic(FightTypeEnum fightType) {
		return strategyLogics.stream().filter(mp -> mp.matchFight(fightType)).findFirst().orElse(null);
	}

}
