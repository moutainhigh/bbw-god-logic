package com.bbw.god.game.chanjie.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 阐截 头衔获取事件
* @author lwb  
* @date 2019年6月28日  
* @version 1.0  
*/
public class ChanjieGainHeadEvent extends ApplicationEvent implements IEventParam {

	private static final long serialVersionUID = 1L;

	public ChanjieGainHeadEvent(EPChanjieGainHead source) {
		super(source);
	}

	@Override
	@SuppressWarnings("unchecked")
	public EPChanjieGainHead getEP() {
		return (EPChanjieGainHead) getSource();
	}
}
