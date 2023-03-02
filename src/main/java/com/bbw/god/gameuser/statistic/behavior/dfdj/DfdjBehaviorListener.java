package com.bbw.god.gameuser.statistic.behavior.dfdj;

import com.bbw.common.DateUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFailEvent;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.sxdh.event.EPSxdhAwardSend;
import com.bbw.god.game.sxdh.event.SxdhAwardSendEvent;
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
 * @description 巅峰对决统计监听类
 * @date 2020/4/22 15:33
 */
@Component
@Slf4j
@Async
public class DfdjBehaviorListener {
	@Autowired
	private DfdjStatisticService dfdjStatisticService;
	@Autowired
	private DfdjZoneService dfdjZoneService;

	@Order(2)
	@EventListener
	public void dfdjWin(CombatFightWinEvent event) {
		try {
			EPFightEnd ep = (EPFightEnd) event.getSource();
			FightTypeEnum fightType = ep.getFightType();
			if (FightTypeEnum.DFDJ != fightType) {
				return;
			}
			DfdjZone zone = dfdjZoneService.getCurOrLastZone(ep.getGuId());
			Integer season = zone.getSeason();
			dfdjStatisticService.win(ep.getGuId(), DateUtil.getTodayInt(), ep.getFightSubmit().getCombatId(), season);
			DfdjStatistic statistic = dfdjStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), WayEnum.DFDJ_FIGHT, ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void dfdjFail(CombatFailEvent event) {
		try {
			EPFightEnd ep = (EPFightEnd) event.getSource();
			FightTypeEnum fightType = ep.getFightType();
			if (FightTypeEnum.DFDJ != fightType) {
				return;
			}
			DfdjZone zone = dfdjZoneService.getCurOrLastZone(ep.getGuId());
			Integer season = zone.getSeason();
			dfdjStatisticService.lose(ep.getGuId(), DateUtil.getTodayInt(), ep.getFightSubmit().getCombatId(), season);
			DfdjStatistic statistic = dfdjStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), WayEnum.DFDJ_FIGHT, ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void dfdjAwardSend(SxdhAwardSendEvent event) {
		try {
			EPSxdhAwardSend ep = event.getEP();
			SxdhRankType rankType = ep.getRankType();
			switch (rankType) {
				case MIDDLE_RANK:
				case RANK:
					dfdjStatisticService.updateRank(ep.getGuId(), rankType, ep.getSeason(), ep.getRank());
					break;
				default:
					break;
			}
			DfdjStatistic statistic = dfdjStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
