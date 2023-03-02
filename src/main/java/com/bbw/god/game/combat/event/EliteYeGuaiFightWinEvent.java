package com.bbw.god.game.combat.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

public class EliteYeGuaiFightWinEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 5146354899494569518L;

	public EliteYeGuaiFightWinEvent(EPEliteYeGuaiFightWin source) {
		super(source);
	}

	@Override
	public EPEliteYeGuaiFightWin getEP() {
		return (EPEliteYeGuaiFightWin) getSource();
	}
}
