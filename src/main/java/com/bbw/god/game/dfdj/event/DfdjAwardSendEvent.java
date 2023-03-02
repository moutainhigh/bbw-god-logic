package com.bbw.god.game.dfdj.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

public class DfdjAwardSendEvent extends ApplicationEvent implements IEventParam {

	public DfdjAwardSendEvent(EPDfdjAwardSend ep) {
		super(ep);
	}

	@Override
	public EPDfdjAwardSend getEP() {
		return (EPDfdjAwardSend) getSource();
	}
}
