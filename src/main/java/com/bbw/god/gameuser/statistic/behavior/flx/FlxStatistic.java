package com.bbw.god.gameuser.statistic.behavior.flx;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 福临轩统计
 * @date 2020/4/23 9:57
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FlxStatistic extends BehaviorStatistic {
	private Integer caishuzi;
	private Integer caishuziWin;
	private Integer yayale;
	/**
	 * 押押乐中奖次数，只计算头奖，安慰奖不计入
	 */
	private Integer yayaleWin;
	private Integer continuousLoseDays = 0;

	public FlxStatistic() {
		super(BehaviorType.FLX);
	}

	public FlxStatistic(Integer today, Integer total, Integer date, Integer caishuzi, Integer caishuziWin,
						Integer yayale, Integer yayaleWin, Integer continuousLoseDays) {
		super(today, total, date, BehaviorType.FLX);
		this.caishuzi = caishuzi;
		this.caishuziWin = caishuziWin;
		this.yayale = yayale;
		this.yayaleWin = yayaleWin;
		this.continuousLoseDays = continuousLoseDays;
	}
}
