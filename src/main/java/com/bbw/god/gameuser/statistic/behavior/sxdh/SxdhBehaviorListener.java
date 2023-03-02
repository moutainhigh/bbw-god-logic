package com.bbw.god.gameuser.statistic.behavior.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFailEvent;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.game.sxdh.SxdhZoneService;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.sxdh.event.EPSxdhAwardSend;
import com.bbw.god.game.sxdh.event.EPSxdhCardRefresh;
import com.bbw.god.game.sxdh.event.SxdhAwardSendEvent;
import com.bbw.god.game.sxdh.event.SxdhCardRefreshEvent;
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
 * @description 神仙大会统计监听类
 * @date 2020/4/22 15:33
 */
@Component
@Slf4j
@Async
public class SxdhBehaviorListener {
	@Autowired
	private SxdhStatisticService sxdhStatisticService;
	@Autowired
	private SxdhZoneService sxdhZoneService;

	@Order(2)
	@EventListener
	public void sxdhWin(CombatFightWinEvent event) {
		try {
			EPFightEnd ep = (EPFightEnd) event.getSource();
			FightTypeEnum fightType = ep.getFightType();
			if (FightTypeEnum.SXDH != fightType) {
				return;
			}
			SxdhZone zone = sxdhZoneService.getCurOrLastZone(ep.getGuId());
			Integer season = zone.getSeason();
			sxdhStatisticService.win(ep.getGuId(), DateUtil.getTodayInt(), ep.getFightSubmit().getCombatId(), season);
			SxdhStatistic statistic = sxdhStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), WayEnum.SXDH_FIGHT, ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void sxdhFail(CombatFailEvent event) {
		try {
			EPFightEnd ep = (EPFightEnd) event.getSource();
			FightTypeEnum fightType = ep.getFightType();
			if (FightTypeEnum.SXDH != fightType) {
				return;
			}
			SxdhZone zone = sxdhZoneService.getCurOrLastZone(ep.getGuId());
			Integer season = zone.getSeason();
			sxdhStatisticService.lose(ep.getGuId(), DateUtil.getTodayInt(), ep.getFightSubmit().getCombatId(), season);
			SxdhStatistic statistic = sxdhStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), WayEnum.SXDH_FIGHT, ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void sxdhChangeCards(SxdhCardRefreshEvent event) {
		try {
			EPSxdhCardRefresh ep = event.getEP();
			int refreshCardNum = ep.getRefreshCardNum();
			SxdhZone zone = sxdhZoneService.getCurOrLastZone(ep.getGuId());
			Integer season = zone.getSeason();
			sxdhStatisticService.changeCards(ep.getGuId(), refreshCardNum, season);
			SxdhStatistic statistic = sxdhStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void sxdhAwardSend(SxdhAwardSendEvent event) {
		try {
			EPSxdhAwardSend ep = event.getEP();
			SxdhRankType rankType = ep.getRankType();
			switch (rankType) {
				case MIDDLE_RANK:
				case RANK:
					sxdhStatisticService.updateRank(ep.getGuId(), rankType, ep.getSeason(), ep.getRank());
					break;
				default:
					break;
			}
			SxdhStatistic statistic = sxdhStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
