package com.bbw.god.city.yed;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 野地触发事件
 * 
 * @author suhq
 * @date 2019年3月1日 下午3:00:01
 */
public class YeDTriggerEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public YeDTriggerEvent(EventParam<EPYeDTrigger> source) {
		super(source);
	}

}
