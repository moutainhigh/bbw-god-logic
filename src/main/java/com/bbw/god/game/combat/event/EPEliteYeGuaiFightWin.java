package com.bbw.god.game.combat.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

@Data
public class EPEliteYeGuaiFightWin extends BaseEventParam {
	private Integer type;
	private Integer cardLevel;
	private Integer cardHierarchy;

	public EPEliteYeGuaiFightWin(Integer type, Integer cardLevel, Integer cardHierarchy, BaseEventParam bep) {
		this.type = type;
		this.cardLevel = cardLevel;
		this.cardHierarchy = cardHierarchy;
		setValues(bep);
	}
}
