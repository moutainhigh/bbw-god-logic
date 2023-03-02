package com.bbw.god.city.taiyf;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 太一府捐献事件
 * 
 * @author suhq
 * @date 2019-09-18 10:59:52
 */
public class TyfFillEvent extends ApplicationEvent {

	public TyfFillEvent(EventParam<Integer> source) {
		super(source);
	}

}
