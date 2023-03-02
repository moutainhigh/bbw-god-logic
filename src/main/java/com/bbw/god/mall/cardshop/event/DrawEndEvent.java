package com.bbw.god.mall.cardshop.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 抽卡事件，抽完卡触发
 * 
 * @author suhq
 * @date 2019-05-29 09:02:35
 */
public class DrawEndEvent extends ApplicationEvent {

	public DrawEndEvent(EventParam<EPDraw> source) {
		super(source);
	}

}
