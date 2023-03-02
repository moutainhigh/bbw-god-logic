package com.bbw.god.gameuser.statistic.behavior.chanjie;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 阐截斗法统计
 * @date 2020/4/22 10:58
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ChanJieStatistic extends BehaviorStatistic {
	private Integer chanWin = 0;
	private Integer jieWin = 0;
	/**
	 * 击败仙人
	 */
	private Integer defeatImmortal = 0;
	/**
	 * 击败掌教师尊
	 */
	private Integer defeatMaster = 0;
	/**
	 * 连续选择阐教
	 */
	private Integer continuousSelectChan = 0;
	/**
	 * 连续选择截教
	 */
	private Integer continuousSelectJie = 0;
	/**
	 * 连续成为护教法王天数
	 */
	private Integer continuousHJFW = 0;

	public ChanJieStatistic() {
		super(BehaviorType.CHAN_JIE_FIGHT);
	}

	public ChanJieStatistic(Integer today, Integer total, Integer date, Integer chanWin, Integer jieWin,
							Integer defeatImmortal, Integer defeatMaster, Integer continuousSelectChan,
							Integer continuousSelectJie, Integer continuousHJFW) {
		super(today, total, date, BehaviorType.CHAN_JIE_FIGHT);
		this.chanWin = chanWin;
		this.jieWin = jieWin;
		this.defeatImmortal = defeatImmortal;
		this.defeatMaster = defeatMaster;
		this.continuousSelectChan = continuousSelectChan;
		this.continuousSelectJie = continuousSelectJie;
		this.continuousHJFW = continuousHJFW;
	}
}
