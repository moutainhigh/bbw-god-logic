package com.bbw.god.activityrank.game.xianyuan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.event.EventParam;
import com.bbw.god.mall.cardshop.event.DrawEndEvent;
import com.bbw.god.mall.cardshop.event.EPDraw;

/**
 * 仙缘榜
 * 
 * @author suhq
 * @date 2019-05-30 14:08:38
 */
@Async
@Component
public class XianyuanRankListener {
	private ActivityRankEnum xyRank = ActivityRankEnum.XIAN_YUAN_RANK;
	private ActivityRankEnum xyDayRank = ActivityRankEnum.XIAN_YUAN_DAY_RANK;

	@Autowired
	private ActivityRankService activityRankService;

	/**
	 * 抽卡
	 * 
	 * @param event
	 */
	@EventListener
	public void drawEnd(DrawEndEvent event) {
		EventParam<EPDraw> ep = (EventParam<EPDraw>) event.getSource();
		long guId = ep.getGuId();
		EPDraw epDraw = ep.getValue();
		activityRankService.incrementRankValue(guId, epDraw.getDrawTimes(), xyRank);
		activityRankService.incrementRankValue(guId, epDraw.getDrawTimes(), xyDayRank);
	}
}
