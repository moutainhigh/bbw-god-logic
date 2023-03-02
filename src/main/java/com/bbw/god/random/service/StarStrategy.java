package com.bbw.god.random.service;

import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.config.SelectorCondition;

/**星级过滤
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-04 10:22
 */
public class StarStrategy extends AbstractStrategy {

	@Override
	public boolean valid(SelectorCondition select, CfgCardEntity card) {

		if (this.allowAll(select.getStar())) {
			return this.getNext().valid(select, card);
		}
		//灵石
		if (RandomKeys.isPowerStarCard(card.getId())) {
			return this.getNext().valid(select, card);
		}
		//星级匹配
		boolean pass = select.getStar() == card.getStar().intValue();
		if (!pass) {
			return false;
		}
		//下一个验证
		return this.getNext().valid(select, card);
	}
}
