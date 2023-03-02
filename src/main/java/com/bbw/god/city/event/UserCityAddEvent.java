package com.bbw.god.city.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 获得封地的事件
 * 
 * @author suhq
 * @date 2018年11月14日 上午11:12:42
 */
public class UserCityAddEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public UserCityAddEvent(EventParam<EPCityAdd> eventParam) {
		super(eventParam);
	}

}
