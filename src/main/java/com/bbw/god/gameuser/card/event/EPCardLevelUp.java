package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡牌升级事件
 * 
 * @author suhq
 * @date 2019-10-21 10:18:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPCardLevelUp extends BaseEventParam {
	private Integer cardId;// 进阶的卡牌
	private Integer oldLevel;// 扣除的卡牌灵石数量
	private Integer newLevel;// 万能灵石ID

	public EPCardLevelUp(BaseEventParam baseEP, int cardId, int oldLevel, int newLevel) {
		setValues(baseEP);
		this.cardId = cardId;
		this.oldLevel = oldLevel;
		this.newLevel = newLevel;
	}
}
