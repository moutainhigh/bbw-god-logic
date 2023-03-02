package com.bbw.god.random.service;

import com.bbw.exception.CoderException;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.SelectorCondition;

/**星级过滤
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-04 10:22
 */
public class TypeStrategy extends AbstractStrategy {

	@Override
	public boolean valid(SelectorCondition select, CfgCardEntity card) {

		if (this.allowAllstr(select.getType())) {
			return this.getNext().valid(select, card);
		}
		//属性参数
		if (select.needTypeParam()) {
			throw CoderException.high("需要角色属性参数！");
		}
		//属性匹配
		boolean pass = select.getType().equals(card.getType().toString());
		if (!pass) {
			return false;
		}
		//下一个验证
		return getNext().valid(select, card);
	}
}
