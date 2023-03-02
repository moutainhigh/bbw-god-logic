package com.bbw.god.gameuser.statistic.behavior.fight;

import com.bbw.common.DateUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFailEvent;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 战斗统计监听类
 * @date 2020/4/22 10:18
 */
@Component
@Slf4j
@Async
public class FightBehaviorListener {
	@Autowired
	private FightStatisticService fightStatisticService;

	@Order(2)
	@EventListener
	public void fightWin(CombatFightWinEvent event) {
		try {
			EPFightEnd ep = (EPFightEnd) event.getSource();
			Long uid = ep.getGuId();
			FightTypeEnum fightType = ep.getFightType();
			fightStatisticService.incFightStatistic(uid, DateUtil.getTodayInt(), fightType, true);
			FightStatistic statistic = fightStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, WayEnum.NONE, ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void fightFail(CombatFailEvent event) {
		try {
			EPFightEnd ep = (EPFightEnd) event.getSource();
			Long uid = ep.getGuId();
			FightTypeEnum fightType = ep.getFightType();
			fightStatisticService.incFightStatistic(uid, DateUtil.getTodayInt(), fightType, false);
			FightStatistic statistic = fightStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, WayEnum.NONE, ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
