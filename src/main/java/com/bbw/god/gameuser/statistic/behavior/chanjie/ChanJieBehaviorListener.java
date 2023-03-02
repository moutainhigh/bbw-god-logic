package com.bbw.god.gameuser.statistic.behavior.chanjie;

import com.bbw.common.DateUtil;
import com.bbw.god.game.chanjie.event.*;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suchaobin
 * @description 阐截斗法统计监听类
 * @date 2020/4/22 11:46
 */
@Component
@Slf4j
@Async
public class ChanJieBehaviorListener {
	@Autowired
	private ChanJieStatisticService chanJieStatisticService;

	@Order(1000)
	@EventListener
	public void fight(ChanjieFightEvent event) {
		try {
			EPChanjieFight fight = event.getEP();
			//失败方
			int headlv = fight.getHeadlv();
			int rid = fight.getRid();
			long uid = fight.getGuId();
			RDCommon rd = new RDCommon();
			chanJieStatisticService.defeatOpponent(uid, DateUtil.getTodayInt(), rid, headlv);
			chanJieStatisticService.loseOpponent(fight.getLoseUid(), DateUtil.getTodayInt());
			ChanJieStatistic statistic = chanJieStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, fight.getWay(), rd, statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(1000)
	@EventListener
	public void religionSelect(ChanjieReligionSelectEvent event) {
		try {
			EPChanjieReligionSelect ep = event.getEP();
			RDCommon rd = new RDCommon();
			int nowRid = ep.getNowRid();
			long uid = ep.getGuId();
			chanJieStatisticService.selectReligious(uid, DateUtil.getTodayInt(), nowRid);
			ChanJieStatistic statistic = chanJieStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
					DateUtil.getTodayInt());
			StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), rd, statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(1000)
	@EventListener
	public void gainDaliyHeadlv(ChanjieGainHeadEvent event) {
		try {
			EPChanjieGainHead ep = event.getEP();
			RDCommon rd = new RDCommon();
			int headlv = ep.getHeadlv();
			List<Long> uidsList = ep.getUids();
			//内门弟子2 真传弟子3 渡劫地仙4 大乘天仙5 大罗金仙6 护教法王7 掌教8
			// 本赛季连续6天获得护教法王头衔   获得则进度+1 否则清0
			if (7 == headlv) {
				uidsList.forEach(uid -> {
					chanJieStatisticService.becomeHjfw(uid);
					ChanJieStatistic statistic = chanJieStatisticService.fromRedis(uid, StatisticTypeEnum.NONE,
							DateUtil.getTodayInt());
					StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), rd, statistic);
				});
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
