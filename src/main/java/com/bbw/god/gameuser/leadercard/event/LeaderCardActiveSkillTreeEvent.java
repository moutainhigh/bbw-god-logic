package com.bbw.god.gameuser.leadercard.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author lzc
 * @description 激活完整技能树事件
 * @date 2021/4/14 10:57
 */
public class LeaderCardActiveSkillTreeEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1;

	public LeaderCardActiveSkillTreeEvent(BaseEventParam source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BaseEventParam getEP() {
		return (BaseEventParam) getSource();
	}
}
