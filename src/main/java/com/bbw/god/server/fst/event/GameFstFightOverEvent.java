package com.bbw.god.server.fst.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 跨服封神台战斗结束事件
 *
 * @author: suhq
 * @date: 2021/8/10 11:29 上午
 */
public class GameFstFightOverEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public GameFstFightOverEvent(BaseEventParam source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BaseEventParam getEP() {
		return (BaseEventParam) getSource();
	}

}
