package com.bbw.god.gameuser.res.exp;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 玩家获得经验事件
 * 
 * @author suhq
 * @date 2018年11月8日 下午3:45:49
 */
public class ExpAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public ExpAddEvent(EPExpAdd eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPExpAdd getEP() {
		return (EPExpAdd) getSource();
	}

}
