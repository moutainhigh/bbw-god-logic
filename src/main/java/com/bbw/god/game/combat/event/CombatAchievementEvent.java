package com.bbw.god.game.combat.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年5月11日 上午11:39:55 类说明
 */
public class CombatAchievementEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public CombatAchievementEvent(EPCombatAchievement source) {
		super(source);

	}

	@Override
	public EPCombatAchievement getEP() {
		return (EPCombatAchievement) getSource();
	}

}
