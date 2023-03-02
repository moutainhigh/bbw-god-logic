package com.bbw.god.gameuser.statistic.behavior.juling;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 聚灵统计
 * @date 2020/4/23 11:50
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JuLingStatistic extends BehaviorStatistic {
	private Integer lastCardId;
	/**
	 * 使用聚仙旗随机连续获取到同一张卡牌的次数
	 */
	private Integer continuousSomeCard = 0;

	public JuLingStatistic() {
		super(BehaviorType.JU_LING);
	}

	public JuLingStatistic(Integer today, Integer total, Integer date, Integer lastCardId,
						   Integer continuousSomeCard) {
		super(today, total, date, BehaviorType.JU_LING);
		this.lastCardId = lastCardId;
		this.continuousSomeCard = continuousSomeCard;
	}
}
