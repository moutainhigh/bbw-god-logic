package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 法宝扣除参数
 * 
 * @author suhq
 * @date 2019-10-22 09:34:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPTreasureDeduct extends BaseEventParam {
	private EVTreasure deductTreasure;

	public EPTreasureDeduct(BaseEventParam baseEP, EVTreasure deductTreasures) {
		setValues(baseEP);
		this.deductTreasure = deductTreasures;
	}
}
