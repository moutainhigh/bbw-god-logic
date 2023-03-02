package com.bbw.god.gameuser.statistic.behavior.fight.fightdetail;

import com.bbw.common.DateUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.combat.event.CombatLeaderCardEvent;
import com.bbw.god.game.combat.event.CombatQiLinKillZhsEvent;
import com.bbw.god.game.combat.event.EPCombatLeaderCardParam;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author lzc
 * @description 战斗相关细节统计监听类
 * @date 2021/4/15 11:30
 */
@Component
@Slf4j
@Async
public class FightDetailBehaviorListener {
	@Autowired
	private FightDetailStatisticService statisticService;

	/**
	 * 主角卡战斗事件
	 * @param event
	 */
	@Order(2)
	@EventListener
	public void combatLeader(CombatLeaderCardEvent event) {
		try {
			EPCombatLeaderCardParam ep = event.getEP();
			Long uid = ep.getGuId();
			if(ep.isMainCity() && ep.getKillCards() > 0 && ep.getCityId() != null){
				//梦魇主城战斗,主角卡击杀卡牌
				statisticService.draw(uid,true,ep.getCityId(),ep.getKillCards(),false);
				FightDetailStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
				StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 麒麟相击败召唤师事件
	 * @param event
	 */
	@Order(2)
	@EventListener
	public void combatQiLin(CombatQiLinKillZhsEvent event) {
		try {
			BaseEventParam ep = event.getEP();
			Long uid = ep.getGuId();
			statisticService.draw(uid,false,0,0,true);
			FightDetailStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
