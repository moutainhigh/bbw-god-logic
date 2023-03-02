package com.bbw.god.server.fst.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 封神台胜利事件
 * 
 * @author suhq
 * @date 2019-08-28 14:25:01
 */
public class FstWinEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public FstWinEvent(EventParam<EVFstWin> source) {
		super(source);
	}

}
