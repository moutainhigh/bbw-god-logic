package com.bbw.god.game.chanjie.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
* @author lwb  
* @date 2019年7月8日  
* @version 1.0  
*/
public class ChanjieReligionSelectEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public ChanjieReligionSelectEvent(EPChanjieReligionSelect source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPChanjieReligionSelect getEP() {
		return (EPChanjieReligionSelect) getSource();
	}

}
