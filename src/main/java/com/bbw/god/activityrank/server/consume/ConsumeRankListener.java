package com.bbw.god.activityrank.server.consume;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.res.ele.EPEleDeduct;
import com.bbw.god.gameuser.res.ele.EVEle;
import com.bbw.god.gameuser.res.ele.EleDeductEvent;
import com.bbw.god.gameuser.treasure.event.EPTreasureDeduct;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureDeductEvent;

/**
 * 材料消耗榜
 * 
 * @author suhq
 * @date 2019-09-18 10:12:37
 */
@Async
@Component
public class ConsumeRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.CONSUME_RANK;

	@Autowired
	private ActivityRankService activityRankService;

	@EventListener
	@Order(1000)
	public void deductEle(EleDeductEvent event) {
		EPEleDeduct ep = event.getEP();
		int point = ep.getDeductEles().stream().mapToInt(EVEle::getNum).sum();
		activityRankService.incrementRankValue(ep.getGuId(), point, rankType);
	}

	@EventListener
	@Order(1000)
	public void deductTreasure(TreasureDeductEvent event) {
		EPTreasureDeduct ep = event.getEP();
		EVTreasure ev = ep.getDeductTreasure();
		int point = getPoint(ev.getId()) * ev.getNum();
		activityRankService.incrementRankValue(ep.getGuId(), point, rankType);
	}

	private int getPoint(int treasureId) {
		TreasureEnum treasure = TreasureEnum.fromValue(treasureId);
		if (treasure == null) {
			return 0;
		}
		switch (treasure) {
		case WNLS1:
			return 6;
		case WNLS2:
			return 18;
		case WNLS3:
			return 20;
		case WNLS4:
			return 40;
		case WNLS5:
			return 120;
		case HDXS:
			return 40;
		default:
			return 0;
		}
	}
}
