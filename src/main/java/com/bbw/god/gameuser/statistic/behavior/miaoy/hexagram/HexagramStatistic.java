package com.bbw.god.gameuser.statistic.behavior.miaoy.hexagram;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/** 文王64卦统计
 * @author lzc
 * @description
 * @date 2021/4/15 11:28
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class HexagramStatistic extends BehaviorStatistic {
	/** 已点亮的卦象数 */
	private int hexagramNum = 0;
	/** 已点亮的上上卦数量 */
	private int hexagramUpUpNum = 0;
	/** 已点亮的下下卦数量 */
	private int hexagramDownDownNum = 0;
	/** 连续上上卦次数 */
	private int continuousUpUp = 0;
	/** 连续下下卦次数 */
	private int continuousDownDown = 0;

	public HexagramStatistic() {
		super(BehaviorType.WWM_HEXAGRAM);
	}

	public HexagramStatistic(Integer today, Integer total, Integer date, Integer hexagramNum,Integer hexagramUpUpNum,Integer hexagramDownDownNum,Integer continuousUpUp,Integer continuousDownDown) {
		super(today, total, date, BehaviorType.WWM_HEXAGRAM);
		this.hexagramNum = hexagramNum;
		this.hexagramUpUpNum = hexagramUpUpNum;
		this.hexagramDownDownNum = hexagramDownDownNum;
		this.continuousUpUp = continuousUpUp;
		this.continuousDownDown = continuousDownDown;
	}
}
