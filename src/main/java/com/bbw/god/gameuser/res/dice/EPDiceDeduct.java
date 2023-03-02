package com.bbw.god.gameuser.res.dice;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 扣除体力事件参数
 * 
 * @author suhq
 * @date 2019-10-16 17:29:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPDiceDeduct extends BaseEventParam {
	private int deductDice;

	public EPDiceDeduct(BaseEventParam baseEP, int deductDice) {
		setValues(baseEP);
		this.deductDice = deductDice;
	}
}
