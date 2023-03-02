package com.bbw.god.game.chanjie.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 教派奇人事件
* @author lwb  
* @date 2019年6月28日  
* @version 1.0  
*/
public class ChanjieSpecailHonorEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public ChanjieSpecailHonorEvent(EPChanjieSpecailHonor source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPChanjieSpecailHonor getEP() {
		return (EPChanjieSpecailHonor) getSource();
	}
}
