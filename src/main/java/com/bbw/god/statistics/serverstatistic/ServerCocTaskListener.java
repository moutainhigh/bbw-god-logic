package com.bbw.god.statistics.serverstatistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.event.EventParam;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.chamberofcommerce.event.CocTaskFinishedEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;

/**
 * @author suchaobin
 * @title: ServerCocTaskListener
 * @projectName bbw-god-logic-server
 * @description:
 * @date 2019/7/59:46
 */

@Component
@Async
public class ServerCocTaskListener {
	@Autowired
	private GameUserService gameUserService;

	@Autowired
	private GodServerStatisticService godServerStatisticService;

	@EventListener
	@Order(2)
	public void sellSpecials(CocTaskFinishedEvent event) {
		EPTaskFinished evTaskFinished = event.getEP();
		Long uid = evTaskFinished.getGuId();
		int sid = gameUserService.getActiveSid(uid);
		godServerStatisticService.addCocTask(sid, evTaskFinished);
	}
}
