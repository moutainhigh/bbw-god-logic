package com.bbw.god.gameuser.statistic.resource;


import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 资源统计类
 * @date 2020/3/28 15:00
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ResourceStatistic extends BaseStatistic {
	private AwardEnum awardEnum;
	/**0是获得，1是消耗*/
	private int type = 0;

	public ResourceStatistic(AwardEnum awardEnum, int type) {
		this.awardEnum = awardEnum;
		this.type = type;
	}

	public ResourceStatistic(Integer date, AwardEnum awardEnum, int type) {
		super(date);
		this.awardEnum = awardEnum;
		this.type = type;
	}

	public ResourceStatistic(Integer today, Integer total, Integer date, AwardEnum awardEnum, int type) {
		super(today, total, date);
		this.awardEnum = awardEnum;
		this.type = type;
	}
}
