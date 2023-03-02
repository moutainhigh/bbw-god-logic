package com.bbw.god.gameuser.leadercard.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author lzc
 * @description 法外分身升阶事件(主要用于广播)
 * @date 2021/4/14 10:57
 */
public class LeaderCardUpHvEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1;

	public LeaderCardUpHvEvent(EPLeaderCardUpHv source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPLeaderCardUpHv getEP() {
		return (EPLeaderCardUpHv) getSource();
	}
}
