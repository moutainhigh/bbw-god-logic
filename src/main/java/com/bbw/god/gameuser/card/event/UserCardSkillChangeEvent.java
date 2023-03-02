package com.bbw.god.gameuser.card.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;
/**
 * 卡牌练技
 * @author lwb
 *
 */
public class UserCardSkillChangeEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public UserCardSkillChangeEvent(EPCardSkillChange eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCardSkillChange getEP() {
		return (EPCardSkillChange) getSource();
	}

}
