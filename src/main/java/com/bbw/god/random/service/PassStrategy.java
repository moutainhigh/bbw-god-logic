package com.bbw.god.random.service;

import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.SelectorCondition;

/**
 * 返回通过
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-04 10:39
 */
public class PassStrategy extends AbstractStrategy {

	@Override
	public boolean valid(SelectorCondition select, CfgCardEntity card) {
		return true;
	}

}
