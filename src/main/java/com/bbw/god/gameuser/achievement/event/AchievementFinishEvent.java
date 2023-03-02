package com.bbw.god.gameuser.achievement.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 成就已领取事件(主要用于广播)
 * @date 2020/2/28 10:57
 */
public class AchievementFinishEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 7055430311287608872L;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public AchievementFinishEvent(EPAchievementFinish source) {
		super(source);
	}

	@Override
	public EPAchievementFinish getEP() {
		return (EPAchievementFinish) getSource();
	}
}
