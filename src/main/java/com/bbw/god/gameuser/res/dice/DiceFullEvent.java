package com.bbw.god.gameuser.res.dice;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 *
 */
public class DiceFullEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public DiceFullEvent(EPDiceFull eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPDiceFull getEP() {
		return (EPDiceFull) getSource();
	}

}
