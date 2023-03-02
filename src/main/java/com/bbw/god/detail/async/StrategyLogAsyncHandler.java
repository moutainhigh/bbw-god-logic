package com.bbw.god.detail.async;

import com.bbw.god.db.entity.AbstractAttackStrategyEntity;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.attackstrategy.service.AbstractStrategyLogic;
import com.bbw.god.game.combat.attackstrategy.service.StrategyLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * 攻城策略保存
 *
 * @author: suhq
 * @date: 2021/12/16 1:52 下午
 */
@Slf4j
@Async
@Component
public class StrategyLogAsyncHandler {
	@Autowired
	private StrategyLogicFactory logicFactory;

	/**
	 * 记录明细
	 *
	 * @param fightType
	 * @param strategyEntity
	 */
	public void log(FightTypeEnum fightType, AbstractAttackStrategyEntity strategyEntity) {
		try {
			AbstractAttackStrategyEntity detailData = strategyEntity;
			AbstractStrategyLogic logic = logicFactory.getLogic(fightType);
			if (null != logic) {
				logic.saveAndUpload(detailData);
			}
		} catch (Exception e) {
			log.error("攻城策略数据保存失败！\n" + strategyEntity);
			log.error(e.getMessage(), e);
		}
	}

}
