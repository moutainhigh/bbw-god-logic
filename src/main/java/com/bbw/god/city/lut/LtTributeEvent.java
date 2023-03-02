package com.bbw.god.city.lut;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 鹿台捐献事件
 * 
 * @author suhq
 * @date 2019-09-18 10:59:52
 */
public class LtTributeEvent extends ApplicationEvent {
	

	public LtTributeEvent(EventParam<Integer> source) {
		super(source);
	}

}
