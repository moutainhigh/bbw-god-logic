package com.bbw.god.game.sxdh.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

public class SxdhAwardSendEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 3429968263238291469L;

	public SxdhAwardSendEvent(EPSxdhAwardSend ep) {
		super(ep);
	}

	@Override
	public EPSxdhAwardSend getEP() {
		return (EPSxdhAwardSend) getSource();
	}
}
