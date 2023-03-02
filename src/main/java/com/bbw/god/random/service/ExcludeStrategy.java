package com.bbw.god.random.service;

import java.util.List;

import com.bbw.common.StrUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.SelectorCondition;

/**黑名单
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-04 10:22
 */
public class ExcludeStrategy extends AbstractStrategy {

	@Override
	public boolean valid(SelectorCondition select, CfgCardEntity card) {
		List<String> exclude = select.getExclude();
		if (this.allowAllstr(exclude)) {
			//没有配置，或者不在黑名单中，下一个校验
			return this.getNext().valid(select, card);
		}
		//卡牌集合参数
		if (select.needExcludeParam()) {
			throw CoderException.high("需要设置卡牌黑名单！");
		}

		String first = exclude.get(0);
		//如果配置的是数字，认为是卡牌ID
		boolean pass = false;
		if (StrUtil.isDigit(first)) {
			pass = !this.containsStr(exclude, card.getId().toString());
		} else {
			pass = !this.containsStr(exclude, card.getName());
		}
		if (!pass) {
			return false;
		}
		//有配置黑名单,并且在黑名单列表中，不通过
		return this.getNext().valid(select, card);

	}
}
