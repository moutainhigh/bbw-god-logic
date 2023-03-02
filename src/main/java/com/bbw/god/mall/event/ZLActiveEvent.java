package com.bbw.god.mall.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 助力礼包激活事件
 * 
 * @author suhq
 * @date 2019年3月1日 下午1:39:04
 */
public class ZLActiveEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public ZLActiveEvent(EventParam<Integer> source) {
		super(source);
	}

}
