package com.bbw.god.statistics.userstatistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.res.ele.EPEleAdd;
import com.bbw.god.gameuser.res.ele.EPEleDeduct;
import com.bbw.god.gameuser.res.ele.EVEle;
import com.bbw.god.gameuser.res.ele.EleAddEvent;
import com.bbw.god.gameuser.res.ele.EleDeductEvent;

/**
 * @author suchaobin
 * @title: UserEleCountListener
 * @projectName bbw-god-logic-server
 * @description: 玩家个人元素统计
 * @date 2019/6/17 9:21
 */

@Component
@Async
public class UserEleCountListener {
	@Autowired
	private UserStatisticService userStatisticService;

	private static final Integer[] MAX_COUNT = { 2000 };

	@EventListener
	@Order(2)
	public void addEle(EleAddEvent event) {
		EPEleAdd ep = event.getEP();
		AwardEnum anEnum = AwardEnum.YS;
		int sum = ep.getAddEles().stream().map(EVEle::getNum).reduce(0, Integer::sum);
		userStatisticService.addOutput(ep.getGuId(), ep.getWay(), sum, MAX_COUNT, anEnum);
	}

	@EventListener
	@Order(2)
	public void deductEle(EleDeductEvent event) {
		EPEleDeduct ep = event.getEP();
		AwardEnum anEnum = AwardEnum.YS;
		int sum = ep.getDeductEles().stream().map(EVEle::getNum).reduce(0, Integer::sum);
		userStatisticService.addConsume(ep.getGuId(), ep.getWay(), sum, anEnum);
	}
}
