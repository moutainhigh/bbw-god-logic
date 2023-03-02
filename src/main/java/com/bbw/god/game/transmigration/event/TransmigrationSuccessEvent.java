package com.bbw.god.game.transmigration.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

public class TransmigrationSuccessEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 3429968263238291469L;

	public TransmigrationSuccessEvent(EPTransmigrationSuccess ep) {
		super(ep);
	}

	@Override
	public EPTransmigrationSuccess getEP() {
		return (EPTransmigrationSuccess) getSource();
	}
}
