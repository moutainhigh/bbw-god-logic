package com.bbw.god.statistics.serverstatistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.EPCardAdd.CardAddInfo;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;

/**
 * @author suchaobin
 * @title: ServerCardListener
 * @projectName bbw-god-logic-server
 * @description:
 * @date 2019/7/1914:42
 */
@Component
@Async
public class ServerCardListener {
	@Autowired
	private GameUserService gameUserService;

	@Autowired
	private GodServerStatisticService godServerStatisticService;

	@EventListener
	@Order(2)
	public void addCard(UserCardAddEvent event) {
		EPCardAdd ep = event.getEP();
		ep.getAddCards().stream().forEach(epCard -> addCard(ep.getGuId(), epCard, ep.getWay()));
	}

	private void addCard(long guId, CardAddInfo epCard, WayEnum wayEnum) {
		int cardId = epCard.getCardId();
		CfgCardEntity cc = CardTool.getCardById(cardId);
		godServerStatisticService.addOutput(gameUserService.getActiveSid(guId), wayEnum, 1, cc.getName());
	}
}
