package com.bbw.god.activityrank.server.chengc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.event.EventParam;

/**
 * 攻城榜
 * 
 * @author suhq
 * @date 2019年3月4日 下午4:15:07
 */
@Component
public class ChengChiRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.CHENGCHI_RANK;

	@Autowired
	private ActivityRankService activityRankService;

	@EventListener
	@Order(1000)
	public void addUserCity(UserCityAddEvent event) {
		EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
		long guId = ep.getGuId();
		// 攻城榜
		activityRankService.incrementRankValue(guId, 1, rankType);
	}
}
