package com.bbw.god.statistics.serverstatistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.dice.DiceAddEvent;
import com.bbw.god.gameuser.res.dice.DiceDeductEvent;
import com.bbw.god.gameuser.res.dice.EPDiceAdd;
import com.bbw.god.gameuser.res.dice.EPDiceDeduct;

/**
 * @author suchaobin
 * @title: ServerDiceListener
 * @projectName bbw-god-logic-server
 * @description: 区服体力统计
 * @date 2019/6/199:24
 */
@Component
@Async
public class ServerDiceListener {
	@Autowired
	private GameUserService gameUserService;

	@Autowired

	private GodServerStatisticService godServerStatisticService;

	@EventListener
	@Order(2)
	public void addDice(DiceAddEvent event) {
		EPDiceAdd ep = event.getEP();
		int sid = gameUserService.getActiveSid(ep.getGuId());
		AwardEnum awardEnum = AwardEnum.TL;
		int value = ep.getAddDice();
		WayEnum way = ep.getWay();
		godServerStatisticService.addOutput(sid, way, value, awardEnum.getName());
	}

	@EventListener
	@Order(2)
	public void deductDice(DiceDeductEvent event) {
		EPDiceDeduct ep = event.getEP();
		int sid = gameUserService.getActiveSid(ep.getGuId());
		AwardEnum awardEnum = AwardEnum.TL;
		int value = ep.getDeductDice();
		WayEnum way = ep.getWay();
		godServerStatisticService.addConsume(sid, way, value, awardEnum.getName());
	}
}
