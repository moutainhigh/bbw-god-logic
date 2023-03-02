package com.bbw.god.random.service;

import java.util.List;

import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.RandomKeys;
import com.bbw.god.random.config.SelectorCondition;

/**
 * 选卡策略
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-04 10:23
 */
public abstract class AbstractStrategy {

	//责任链中的下一个元素
	private AbstractStrategy nextStrategy = null;

	public AbstractStrategy getNext() {
		return nextStrategy;
	}

	/**
	 * 设置下一个责任链策略，返回下一个责任链策略
	 * @param next
	 * @return
	 */
	public AbstractStrategy setNext(AbstractStrategy next) {
		this.nextStrategy = next;
		return nextStrategy;
	}

	/**
	 * 校验
	 * @param select
	 * @param card
	 * @return
	 */
	public abstract boolean valid(SelectorCondition select, CfgCardEntity card);

	/**
	 * 是否允许所有
	 * @param regx
	 * @return
	 */
	public boolean allowAll(int regx) {
		return RandomKeys.NO_LIMIT == regx;
	}

	/**
	 * 是否允许所有
	 * @param list
	 * @return
	 */
	public boolean allowAll(List<Integer> list) {
		//未配置
		if (null == list || list.isEmpty()) {
			return true;
		}
		//配置了-1
		return list.contains(RandomKeys.NO_LIMIT);
	}

	/**
	 * 是否包含
	 * @param list
	 * @param match
	 * @return
	 */
	public boolean contains(List<Integer> list, int match) {
		if (null == list) {
			return false;
		}
		return list.contains(match);
	}

	/**
	 * 是否允许所有
	 * @param regx
	 * @return
	 */
	public boolean allowAllstr(String regx) {
		return RandomKeys.NO_LIMIT_STRING.equals(regx);
	}

	public boolean allowAllstr(List<String> list) {
		//未配置
		if (null == list || list.isEmpty()) {
			return true;
		}
		//配置了-1
		return list.contains(RandomKeys.NO_LIMIT_STRING);
	}

	/**
	 * 是否包含
	 * @param list
	 * @param match
	 * @return
	 */
	public boolean containsStr(List<String> list, String match) {
		if (null == list) {
			return false;
		}
		return list.contains(match);
	}
}
