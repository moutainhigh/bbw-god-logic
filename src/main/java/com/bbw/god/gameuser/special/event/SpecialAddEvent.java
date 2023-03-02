package com.bbw.god.gameuser.special.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 特产添加事件
 * 
 * @author suhq
 * @date 2018年10月23日 上午8:59:57
 */
public class SpecialAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public SpecialAddEvent(EPSpecialAdd source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPSpecialAdd getEP() {
		return (EPSpecialAdd) getSource();
	}

}
