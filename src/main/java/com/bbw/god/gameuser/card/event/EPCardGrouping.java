package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡牌编组事件
 * 
 * @author suhq
 * @date 2019-06-29 09:56:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPCardGrouping extends BaseEventParam {
	private String cardGroups;// 101,102,103!1 - 卡牌ID1,ID2,ID3!卡组

	public EPCardGrouping(BaseEventParam bep, String cardGroups) {
		setValues(bep);
		this.cardGroups = cardGroups;
	}
}
