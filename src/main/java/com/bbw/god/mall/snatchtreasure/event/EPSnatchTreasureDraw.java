package com.bbw.god.mall.snatchtreasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 夺宝抽奖事件参数
 * @date 2020/6/30 14:49
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class EPSnatchTreasureDraw extends BaseEventParam {
	private Integer DrawTimes;
	private Integer consumeTreasureId;

	public EPSnatchTreasureDraw(Integer drawTimes, Integer consumeTreasureId, BaseEventParam bep) {
		DrawTimes = drawTimes;
		this.consumeTreasureId = consumeTreasureId;
		setValues(bep);
	}
}
