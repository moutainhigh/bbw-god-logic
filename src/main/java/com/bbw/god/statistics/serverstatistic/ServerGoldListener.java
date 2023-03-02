package com.bbw.god.statistics.serverstatistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldAddEvent;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;

/**
 * @author suchaobin
 * @title: ServerGoldListener
 * @projectName bbw-god-logic-server
 * @description: 区服元宝统计
 * @date 2019/6/1815:36
 */
@Component
@Async
public class ServerGoldListener {
	@Autowired
	private GameUserService gameUserService;

	@Autowired
	private GodServerStatisticService godServerStatisticService;

	@EventListener
	@Order(2)
	public void addGold(GoldAddEvent event) {
		EPGoldAdd ep = event.getEP();
		int sid = gameUserService.getActiveSid(ep.getGuId());
		int value = ep.gainAddGold();
		AwardEnum awardEnum = AwardEnum.YB;
		godServerStatisticService.addOutput(sid, ep.getWay(), value, awardEnum.getName());
	}

	@EventListener
	@Order(2)
	public void deductGold(GoldDeductEvent event) {
		EPGoldDeduct ep = event.getEP();
		int sid = gameUserService.getActiveSid(ep.getGuId());
		int value = ep.getDeductGold();
		AwardEnum awardEnum = AwardEnum.YB;
		godServerStatisticService.addConsume(sid, ep.getWay(), value, awardEnum.getName());
	}
}
