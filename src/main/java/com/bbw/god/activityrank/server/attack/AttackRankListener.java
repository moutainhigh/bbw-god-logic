package com.bbw.god.activityrank.server.attack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;

/**
 * 王者之路榜
 *
 * @author suhq
 * @date 2019-09-18 09:20:09
 */
@Async
@Component
public class AttackRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.ATTACK_RANK;

	@Autowired
	private ActivityRankService activityRankService;
	@Autowired
	private AttackRankService attackRankService;

	@EventListener
	@Order(1000)
	public void addUserCity(UserCityAddEvent event) {
		EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
		long guId = ep.getGuId();
		CfgCityEntity city = CityTool.getCityById(ep.getValue().getCityId());
		int point = attackRankService.getPoint(city);
		activityRankService.incrementRankValue(guId, point, rankType);
	}

	@EventListener
	@Order(1000)
	public void fightWin(CombatFightWinEvent event) {
		EPFightEnd ep = (EPFightEnd) event.getSource();
		long uid = ep.getGuId();
		if (ep.getFightType() == FightTypeEnum.PROMOTE) {
			CfgCityEntity city = CityTool.getCityByRoadId(ep.getPos());
			int point = attackRankService.getPoint(city);
			activityRankService.incrementRankValue(uid, point, rankType);
		}

	}
}