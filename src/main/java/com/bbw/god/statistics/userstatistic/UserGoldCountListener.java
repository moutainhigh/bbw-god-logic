package com.bbw.god.statistics.userstatistic;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldAddEvent;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @title: UserGoldCountListener
 * @projectName bbw-god-logic-server
 * @description: 玩家个人元宝统计
 * @date 2019/6/17 9:30
 */

@Component
@Async
public class UserGoldCountListener {
	@Autowired
	private UserStatisticService userStatisticService;

	private static final Integer[] MAX_COUNT = {1500, 2000, 3000, 4000};

	@EventListener
	@Order(2)
	public void addGold(GoldAddEvent event) {
		EPGoldAdd ep = event.getEP();
		AwardEnum anEnum = AwardEnum.YB;
		int value = ep.gainAddGold();
		userStatisticService.addOutput(ep.getGuId(), ep.getWay(), value, MAX_COUNT, anEnum);
	}

	@EventListener
	@Order(2)
	public void deductGold(GoldDeductEvent event) {
		EPGoldDeduct ep = event.getEP();
		AwardEnum anEnum = AwardEnum.YB;
		int value = ep.getDeductGold();
		userStatisticService.addConsume(ep.getGuId(), ep.getWay(), value, anEnum);
	}
}
