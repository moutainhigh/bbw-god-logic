package com.bbw.god.activityrank.server.expedition;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 冲榜远征步数事件
 * 
 * @author suhq
 * @date 2019年3月14日 下午3:54:57
 */
public class ARStepEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public ARStepEvent(EventParam<Integer> source) {
		super(source);
	}

}
