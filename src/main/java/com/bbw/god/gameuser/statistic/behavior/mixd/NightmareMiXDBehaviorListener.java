package com.bbw.god.gameuser.statistic.behavior.mixd;

import com.bbw.common.DateUtil;
import com.bbw.god.city.mixd.event.*;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
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
 * @description 梦魇迷仙洞统计监听类
 * @date 2020/4/23 11:46
 */
@Component
@Slf4j
@Async
public class NightmareMiXDBehaviorListener {
	@Autowired
	private NightmareMiXDStatisticService statisticService;

	/**
	 * 迷仙洞战斗胜利
	 * @param event
	 */
	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void fightWin(CombatFightWinEvent event) {
		try {
			EPFightEnd ep = (EPFightEnd) event.getSource();
			Long uid = ep.getGuId();
			if(ep.getFightType() == FightTypeEnum.MXD && ep.getFightSubmit().getOpponentId()< 0){
				if(ep.getFightSubmit().getOpponentName().equals(NightmareMiXianPosEnum.XUN_SHI_LEADER.getMemo())){
					//击败巡使头领
					statisticService.draw(uid, NightmareMiXDBehaviorType.BEAT_PATROL,1);
					NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
					StatisticEventPublisher.pubBehaviorStatisticEvent(uid, WayEnum.MXD_FIGHT, ep.getRd(), statistic);
				}else if(ep.getFightSubmit().getOpponentName().contains("巡使")){
					//击败巡使
					statisticService.draw(uid, NightmareMiXDBehaviorType.BEAT_PATROL,0);
					NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
					StatisticEventPublisher.pubBehaviorStatisticEvent(uid, WayEnum.MXD_FIGHT, ep.getRd(), statistic);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 迷仙洞饮用泉水
	 * @param event
	 */
	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void drinkWater(DrinkWaterEvent event) {
		try {
			EPDrinkWater ep = event.getEP();
			Long uid = ep.getGuId();
			if(ep.getBlood() == 11){
				statisticService.draw(uid, NightmareMiXDBehaviorType.DRINK_WATER,1);
			}else {
				statisticService.draw(uid, NightmareMiXDBehaviorType.DRINK_WATER,0);
			}
			NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 迷仙洞踩到陷阱
	 * @param event
	 */
	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void stepTrap(StepTrapEvent event) {
		try {
			BaseEventParam ep = event.getEP();
			Long uid = ep.getGuId();
			statisticService.draw(uid, NightmareMiXDBehaviorType.STEP_TRAP,1);
			NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 巡使驻地中打开特殊宝箱
	 * @param event
	 */
	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void openSpecialBox(OpenSpecialBoxEvent event) {
		try {
			BaseEventParam ep = event.getEP();
			Long uid = ep.getGuId();
			statisticService.draw(uid, NightmareMiXDBehaviorType.OPEN_SPECIAL_BOX,1);
			NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 通过一层梦魇迷仙洞
	 * @param event
	 */
	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void passTier(PassTierEvent event) {
		try {
			EPPassTier ep = event.getEP();
			Long uid = ep.getGuId();
			if(ep.getBlood() == 1){
				//1点生命值状态通过1层迷仙洞
				statisticService.draw(uid, NightmareMiXDBehaviorType.PASS_OF_ONE_HP,1);
				NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
				StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
			}else if(ep.isFullBlood()){
				//本层没有扣过血通过1层迷仙洞
				statisticService.draw(uid, NightmareMiXDBehaviorType.FULL_LIFE_PASS,1);
				NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
				StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 梦魇迷仙洞熔炼
	 * @param event
	 */
	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void smelt(SmeltEvent event) {
		try {
			EPSmelt ep = event.getEP();
			Long uid = ep.getGuId();
			if(ep.isResult()){
				//熔炼成功
				statisticService.draw(uid, NightmareMiXDBehaviorType.SMELT_SUCCEED,1);
			}else{
				//熔炼失败
				statisticService.draw(uid, NightmareMiXDBehaviorType.SMELT_FAIL,1);
				NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
				StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 层主击败挑战者事件
	 * @param event
	 */
	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void beatDefier(CZBeatDefierEvent event) {
		try {
			BaseEventParam ep = event.getEP();
			Long uid = ep.getGuId();
			statisticService.draw(uid, NightmareMiXDBehaviorType.BEAT_DEFIER,1);
			NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 层主被打败事件
	 * @param event
	 */
	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void biteTheDust(CZBiteTheDustEvent event) {
		try {
			BaseEventParam ep = event.getEP();
			Long uid = ep.getGuId();
			statisticService.draw(uid, NightmareMiXDBehaviorType.BITE_THE_DUST,1);
			NightmareMiXDStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
