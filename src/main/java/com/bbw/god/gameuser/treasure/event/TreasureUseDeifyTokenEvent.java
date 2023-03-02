package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 封神令使用事件
 *
 */
public class TreasureUseDeifyTokenEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public TreasureUseDeifyTokenEvent(EPCardDeify source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCardDeify getEP() {
		return (EPCardDeify) getSource();
	}

}
