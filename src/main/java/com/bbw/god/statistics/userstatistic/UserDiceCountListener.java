package com.bbw.god.statistics.userstatistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.res.dice.DiceAddEvent;
import com.bbw.god.gameuser.res.dice.DiceDeductEvent;
import com.bbw.god.gameuser.res.dice.EPDiceAdd;
import com.bbw.god.gameuser.res.dice.EPDiceDeduct;

/**
 * @author suchaobin
 * @title: UserDiceCountListener
 * @projectName bbw-god-logic-server
 * @description: 玩家个人体力统计
 * @date 2019/6/1911:50
 */
@Component
@Async
public class UserDiceCountListener {
	@Autowired
	private UserStatisticService userStatisticService;

	private static final Integer[] MAX_COUNT = { 2000, 3000, 4000, 5000 };

	@EventListener
	@Order(2)
	public void addDice(DiceAddEvent event) {
		EPDiceAdd ep = event.getEP();
		AwardEnum anEnum = AwardEnum.TL;
		int value = ep.getAddDice();
		userStatisticService.addOutput(ep.getGuId(), ep.getWay(), value, MAX_COUNT, anEnum);
	}

	@EventListener
	@Order(2)
	public void deductDice(DiceDeductEvent event) {
		EPDiceDeduct ep = event.getEP();
		AwardEnum anEnum = AwardEnum.TL;
		int value = ep.getDeductDice();
		userStatisticService.addConsume(ep.getGuId(), ep.getWay(), value, anEnum);
	}
}
