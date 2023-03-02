package com.bbw.god.mall.snatchtreasure;

import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 夺宝界面数据
 * @date 2020/6/29 16:52
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class RDSnatchTreasureInfo extends RDCommon {
	private static final long serialVersionUID = 9132570285364452761L;
	private Integer wishValue;// 幸运值
	private Long remainTime;// 卡牌奖励的剩余时间，时间到了会换奖励
	private List<Award> awards;// 奖品
	private Integer weekDrawTimes;// 本周夺宝次数
	private Boolean isDiscount;// 当前是否有折扣
	private Integer costByTicket1;// 抽一次需要的夺宝抵用券
	private Integer costByTicket5;// 五连抽需要的夺宝抵用券
	private Integer costByScore1;// 抽一次需要的夺宝积分
	private Integer costByScore5;// 五连抽需要的夺宝积分
	private List<RDSnatchTreasureBox> weekBoxList;// 本周夺宝宝箱

	@Data
	@AllArgsConstructor
	public static class RDSnatchTreasureBox {
		private Integer boxId;
		private Integer type;
	}
}
