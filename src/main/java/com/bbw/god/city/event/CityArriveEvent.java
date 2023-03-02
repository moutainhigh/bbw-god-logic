package com.bbw.god.city.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 城池到达
 * 
 * @author suhq
 * @date 2018年10月10日 下午2:32:03
 */
public class CityArriveEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public CityArriveEvent(EventParam<Integer> eventParam) {
		super(eventParam);
	}

}
