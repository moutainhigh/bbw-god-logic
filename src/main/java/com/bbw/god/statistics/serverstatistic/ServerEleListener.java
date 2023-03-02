package com.bbw.god.statistics.serverstatistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ele.EPEleAdd;
import com.bbw.god.gameuser.res.ele.EPEleDeduct;
import com.bbw.god.gameuser.res.ele.EleAddEvent;
import com.bbw.god.gameuser.res.ele.EleDeductEvent;

/**
 * @author suchaobin
 * @title: ServerEleListener
 * @projectName bbw-god-logic-server
 * @description: 区服元素统计
 * @date 2019/6/1815:36
 */
@Component
@Async
public class ServerEleListener {
	@Autowired
	private GameUserService gameUserService;

	@Autowired
	private GodServerStatisticService godServerStatisticService;

	@EventListener
	@Order(2)
	public void addEle(EleAddEvent event) {
		EPEleAdd ep = event.getEP();
		int sid = gameUserService.getActiveSid(ep.getGuId());
		ep.getAddEles().stream().filter(s -> s == null).forEach(s -> godServerStatisticService.addOutput(sid, ep.getWay(), s.getNum(), "元素:" + String.valueOf(s.getType())));
		ep.getAddEles().stream().filter(s -> s != null).forEach(s -> godServerStatisticService.addOutput(sid, ep.getWay(), s.getNum(), TypeEnum.fromValue(s.getType()).getName() + "元素"));
	}

	@EventListener
	@Order(2)
	public void deductEle(EleDeductEvent event) {
		EPEleDeduct ep = event.getEP();
		int sid = gameUserService.getActiveSid(ep.getGuId());
		ep.getDeductEles().stream().filter(s -> s == null).forEach(s -> godServerStatisticService.addConsume(sid, ep.getWay(), s.getNum(), "元素:" + String.valueOf(s.getType())));
		ep.getDeductEles().stream().filter(s -> s != null).forEach(s -> godServerStatisticService.addConsume(sid, ep.getWay(), s.getNum(), TypeEnum.fromValue(s.getType()).getName() + "元素"));
	}
}
