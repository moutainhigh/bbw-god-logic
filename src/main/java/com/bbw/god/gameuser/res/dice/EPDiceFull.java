package com.bbw.god.gameuser.res.dice;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获得的体力溢出事件参数
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPDiceFull extends BaseEventParam {
	private int addDice=0;

	public static EPDiceFull instance(long uid, int addDice) {
		EPDiceFull ep=new EPDiceFull();
		ep.setValues(new BaseEventParam(uid));
		ep.setAddDice(addDice);
		return ep;
	}

}
