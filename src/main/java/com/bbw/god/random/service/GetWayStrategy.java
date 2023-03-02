package com.bbw.god.random.service;

import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.SelectorCondition;

/**
 * 来源过滤
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-04 10:22
 */
public class GetWayStrategy extends AbstractStrategy {

	@Override
	public boolean valid(SelectorCondition select, CfgCardEntity card) {

		if (this.allowAll(select.getGetWay())) {
			return this.getNext().valid(select, card);
		}
		// 来源匹配
		boolean pass = this.contains(select.getGetWay(), card.getWay().intValue());
		if (!pass) {
			return false;
		}
		return this.getNext().valid(select, card);

	}
}
