package com.bbw.god.random.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.random.config.RandomStrategy;
import com.bbw.god.random.config.ResultLimtRule;
import com.bbw.god.random.config.Selector;

import lombok.Data;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-07 11:03
 */
@Data
public class RandomResult {
	private RandomStrategy nextTimeStrategy;//下一次调用应该使用的策略
	private List<CfgCardEntity> cardList = new ArrayList<>();//结果卡牌集
	private List<Selector> matchSelectors = new ArrayList<>();

	List<Selector> getMatchSelectors() {
		return matchSelectors;
	}

	void addMatchSelectors(Selector selector) {
		matchSelectors.add(selector);
	}

	/**
	 * 抽取结果是否满足结果限制。
	 * @param strategy
	 * @return
	 */
	public boolean acceptAble(RandomStrategy strategy) {
		if (ListUtil.isEmpty(cardList)) {
			return true;
		}
		//返回总数限制
		if (cardList.size() > strategy.getMaxSize()) {
			return false;
		}
		if (ListUtil.isEmpty(strategy.getResultRules())) {
			return true;
		}
		//结果限制
		for (ResultLimtRule limitRule : strategy.getResultRules()) {
			long count = cardList.stream().filter(card -> card.getStar().intValue() == limitRule.getStar()).count();
			if (count > limitRule.getMaxSize()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否有下一次调用的策略
	 * @return
	 */
	public boolean hasNextTimeStrategy() {
		return null != nextTimeStrategy;
	}

	/**
	 * 返回卡牌数量
	 * @return
	 */
	public int cardSize() {
		if (ListUtil.isEmpty(cardList)) {
			return 0;
		}
		return cardList.size();
	}

	/**
	 * 获取第一张卡牌
	 * @return
	 */
	public Optional<CfgCardEntity> getFirstCard() {
		if (ListUtil.isNotEmpty(cardList)) {
			return Optional.of(cardList.get(0));
		}
		return Optional.empty();
	}
}
