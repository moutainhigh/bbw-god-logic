package com.bbw.god.random.service;

import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.SelectorCondition;

/**组合过滤
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-04 10:22
 */
public class GroupStrategy extends AbstractStrategy {

	@Override
	public boolean valid(SelectorCondition select, CfgCardEntity card) {

		if (this.allowAll(select.getGroup())) {
			return this.getNext().valid(select, card);
		}

		if (null == card.getGroup()) {
			return false;
		}
		//组合匹配
		boolean pass = select.getGroup() == card.getGroup().intValue();
		if (!pass) {
			return false;
		}
		return this.getNext().valid(select, card);
	}
}
