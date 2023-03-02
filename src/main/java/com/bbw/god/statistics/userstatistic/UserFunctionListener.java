package com.bbw.god.statistics.userstatistic;

import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFailEvent;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.gameuser.chamberofcommerce.event.CocTaskFinishedEvent;
import com.bbw.god.server.guild.event.GuildTaskFinishedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @ClassName UserFunctionListener
 * @createTime 2019/9/20 9:14
 */
@Component
@Async
public class UserFunctionListener {
	@Autowired
	private UserStatisticService userStatisticService;

	@EventListener
	@Order(2)
	public void guildTask(GuildTaskFinishedEvent event) {
		userStatisticService.addFunction(event.getEP().getGuId(), "行会任务", 1);
	}

	@EventListener
	@Order(2)
	public void cocTask(CocTaskFinishedEvent event) {
		userStatisticService.addFunction(event.getEP().getGuId(), "商会任务", 1);
	}

	@EventListener
	@Order(2)
	public void fightWin(CombatFightWinEvent event) {
		EPFightEnd ep = (EPFightEnd) event.getSource();
		String fightTypeName = ep.getFightType().getName();
		if (fightTypeName.equals(FightTypeEnum.FST.getName()) || fightTypeName.equals(FightTypeEnum.SXDH.getName()) || fightTypeName.equals(FightTypeEnum.CJDF.getName())) {
			userStatisticService.addFunction(ep.getGuId(), fightTypeName, 1);
		}
	}

	@EventListener
	@Order(2)
	public void fightFail(CombatFailEvent event) {
		EPFightEnd ep = (EPFightEnd) event.getSource();
		String fightTypeName = ep.getFightType().getName();
		if (fightTypeName.equals(FightTypeEnum.FST.getName()) || fightTypeName.equals(FightTypeEnum.SXDH.getName()) || fightTypeName.equals(FightTypeEnum.CJDF.getName())) {
			userStatisticService.addFunction(ep.getGuId(), fightTypeName, 1);
		}
	}
}
