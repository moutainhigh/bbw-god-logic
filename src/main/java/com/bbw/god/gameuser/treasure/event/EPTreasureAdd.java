package com.bbw.god.gameuser.treasure.event;

import java.util.List;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 法宝添加参数
 * 
 * @author suhq
 * @date 2019-10-22 09:34:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPTreasureAdd extends BaseEventParam {
	private List<EVTreasure> addTreasures;

	public EPTreasureAdd(BaseEventParam baseEP, List<EVTreasure> addTreasures) {
		setValues(baseEP);
		this.addTreasures = addTreasures;
	}
}
