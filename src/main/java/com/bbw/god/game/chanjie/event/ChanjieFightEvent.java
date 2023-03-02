package com.bbw.god.game.chanjie.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 阐截斗法 战斗结果
* @author lwb  
* @date 2019年6月24日  
* @version 1.0
 */
public class ChanjieFightEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public ChanjieFightEvent(EPChanjieFight source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPChanjieFight getEP() {
		return (EPChanjieFight) getSource();
	}
}
