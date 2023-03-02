package com.bbw.god.activityrank.server.fuhao.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 富豪榜排名上升事件
 * @date 2020/2/5 9:48
 */
public class FuHaoRankUpEvent extends ApplicationEvent implements IEventParam {
	public FuHaoRankUpEvent(EPFuHaoRankUp source) {
		super(source);
	}

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public EPFuHaoRankUp getEP() {
		return (EPFuHaoRankUp) getSource();
	}
}
