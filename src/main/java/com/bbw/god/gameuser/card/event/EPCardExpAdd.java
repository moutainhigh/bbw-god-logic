package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;

/**
 * 卡牌经验事件
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-02-27 15:11
 */

@Getter
@Setter
public class EPCardExpAdd extends BaseEventParam {
	private int addedExp;
	private int cardId;

	public EPCardExpAdd(BaseEventParam baseEP, Integer cardId, Integer addedExp) {
		setValues(baseEP);
		this.addedExp = addedExp;
		this.cardId = cardId;
	}
}
