package com.bbw.god.gameuser.statistic.resource.dice;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 体力统计
 * @date 2020/11/05 11:13
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DiceStatistic extends ResourceStatistic {

	public DiceStatistic(Integer today, Integer total, Integer date, int type) {
		super(today, total, date, AwardEnum.TL, type);
	}

	public DiceStatistic(int type) {
		super(AwardEnum.TL, type);
	}
}
