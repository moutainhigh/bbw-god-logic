package com.bbw.god.gameuser.task.grow.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

public class FinishNewbieTaskEvent extends ApplicationEvent implements IEventParam{
	private static final long serialVersionUID = 1L;

	public FinishNewbieTaskEvent(EPFinishNewbieTask dta) {
		super(dta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPFinishNewbieTask getEP() {
		return (EPFinishNewbieTask) getSource();
	}
	
}
