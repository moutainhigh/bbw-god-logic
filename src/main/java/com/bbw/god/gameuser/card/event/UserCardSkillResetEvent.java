package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 卡牌技能重置事件
 * @date 2020/2/24 16:19
 */
public class UserCardSkillResetEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = -5719438092042179468L;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public UserCardSkillResetEvent(EPCardSkillReset source) {
		super(source);
	}

	@Override
	public EPCardSkillReset getEP() {
		return (EPCardSkillReset) getSource();
	}
}
