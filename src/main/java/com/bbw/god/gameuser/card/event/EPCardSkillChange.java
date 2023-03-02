package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡牌练技事件
 * 
 * LWB
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPCardSkillChange extends BaseEventParam {
	private Integer cardId;//卡牌id
	private String cardName;
	private Integer oldSkill;
	private String skillScrollName;
	public EPCardSkillChange(BaseEventParam baseEP, int cardId, String cardName,Integer oldSkill ,String skillScrollName) {
		setValues(baseEP);
		this.cardId = cardId;
		this.cardName = cardName;
		this.oldSkill = oldSkill;
		this.skillScrollName = skillScrollName;
	}
}
