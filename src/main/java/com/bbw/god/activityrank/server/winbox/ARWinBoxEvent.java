package com.bbw.god.activityrank.server.winbox;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 冲榜开宝箱事件
 * 
 * @author suhq
 * @date 2019年3月14日 下午3:53:34
 */
public class ARWinBoxEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public ARWinBoxEvent(EventParam<Integer> source) {
		super(source);
	}

}
