package com.bbw.god.activityrank.server.expedition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.event.EventParam;

/**
 * 远征榜听器
 * 
 * @author suhq
 * @date 2019年3月4日 下午4:15:07
 */
@Component
public class ExpeditionRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.EXPEDITION_RANK;

	@Autowired
	private ActivityRankService activityRankService;

	/**
	 * 远征榜增加步数
	 * 
	 * @param event
	 */
	@EventListener
	public void addSteps(ARStepEvent event) {
		EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
		long guId = ep.getGuId();
		activityRankService.incrementRankValue(guId, ep.getValue(), rankType);
	}
}
