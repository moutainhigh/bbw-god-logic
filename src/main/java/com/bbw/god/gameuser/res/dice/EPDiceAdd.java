package com.bbw.god.gameuser.res.dice;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获得的体力事件参数
 * 
 * @author suhq
 * @date 2019-10-16 17:28:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPDiceAdd extends BaseEventParam {
	private int addDice;

	public EPDiceAdd(BaseEventParam baseEP, int addDice) {
		setValues(baseEP);
		this.addDice = addDice;
	}

}
