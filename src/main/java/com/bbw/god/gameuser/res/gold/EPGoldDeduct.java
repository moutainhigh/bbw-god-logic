package com.bbw.god.gameuser.res.gold;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元宝扣除信息
 * 
 * @author suhq
 * @date 2019-10-16 14:45:13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPGoldDeduct extends BaseEventParam {
	private int deductGold;

	public EPGoldDeduct(BaseEventParam baseEP, int deductGold) {
		setValues(baseEP);
		this.deductGold = deductGold;
	}

}
