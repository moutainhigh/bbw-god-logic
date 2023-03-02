package com.bbw.god.city.lut;

import com.bbw.god.event.EventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 鹿台退还事件
 * 
 * @author lzc
 * @date 2021-04-06 10:59:52
 */
public class LtBackEvent extends ApplicationEvent {


	public LtBackEvent(EventParam<Integer> source) {
		super(source);
	}

}
