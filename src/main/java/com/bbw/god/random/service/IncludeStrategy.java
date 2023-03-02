package com.bbw.god.random.service;

import java.util.List;

import com.bbw.common.StrUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.SelectorCondition;

/**
 * 白名单
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-04 10:22
 */
public class IncludeStrategy extends AbstractStrategy {

	@Override
	public boolean valid(SelectorCondition select, CfgCardEntity card) {
		List<String> include = select.getInclude();
		// 没有配置白名单，就是允许所有则通过
		if (this.allowAllstr(include)) {
			return this.getNext().valid(select, card);
		}
		// 卡牌集合参数
		if (select.needIncludeParam()) {
			throw CoderException.high("需要设置卡牌白名单！");
		}
		String first = include.get(0);
		// 如果配置的是数字，认为是卡牌ID
		boolean pass = false;
		if (StrUtil.isDigit(first)) {
			pass = this.containsStr(include, card.getId().toString());
		} else {
			// System.out.println(include + "," + card.getName());
			pass = this.containsStr(include, card.getName());
		}
		// 不再白名单内
		if (!pass) {
			return false;
		}
//		System.out.println("通过include的卡牌：" + include + "," + card.getName());
		// 在白名单列表中，下一个验证
		return this.getNext().valid(select, card);

	}
}
