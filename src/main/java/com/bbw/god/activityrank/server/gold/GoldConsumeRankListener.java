package com.bbw.god.activityrank.server.gold;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 元宝消耗榜
 *
 * @author suchaobin
 */
@Component
public class GoldConsumeRankListener {
	private final ActivityRankEnum rankType = ActivityRankEnum.GOLD_CONSUME_RANK;
	private final ActivityRankEnum combinedServiceRankType = ActivityRankEnum.COMBINED_SERVICE_GOLD_CONSUME_RANK;
	private final ActivityRankEnum dayRankType = ActivityRankEnum.GOLD_CONSUME_DAY_RANK;

	@Autowired
	private ActivityRankService activityRankService;

	@EventListener
	@Order(1000)
	public void deductGold(GoldDeductEvent event) {
		EPGoldDeduct ep = event.getEP();
		WayEnum way = ep.getWay();
		// 富临轩猜数字不计入
		if (WayEnum.FLX_SG == way) {
			return;
		}
		int deductGold = ep.getDeductGold();
		activityRankService.incrementRankValue(ep.getGuId(), deductGold, rankType);
		activityRankService.incrementRankValue(ep.getGuId(), deductGold, dayRankType);
		activityRankService.incrementRankValue(ep.getGuId(), deductGold, combinedServiceRankType);
	}
}
