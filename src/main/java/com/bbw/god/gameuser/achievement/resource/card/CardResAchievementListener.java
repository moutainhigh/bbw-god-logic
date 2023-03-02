package com.bbw.god.gameuser.achievement.resource.card;

import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.EPCardLevelUp;
import com.bbw.god.gameuser.card.event.UserCardLevelUpEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 卡牌成就相关监听
 * @date 2020/5/14 14:00
 **/
@Async
@Component
@Slf4j
public class CardResAchievementListener {
	@Autowired
	private AchievementServiceFactory serviceFactory;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserCardService userCardService;

	@Order(1000)
	@EventListener
	public void cardLevelUp(UserCardLevelUpEvent event) {
		EPCardLevelUp ep = event.getEP();
		Integer cardId = ep.getCardId();
		List<Integer> cardIds = Arrays.asList(201, 456, 457);
		if (cardIds.contains(CardTool.getNormalCardId(cardId))) {
			long uid = ep.getGuId();
			BaseAchievementService service = serviceFactory.getById(14930);
			int count = (int) userCardService.getUserCards(uid).stream().filter(tmp ->
					cardIds.contains(CardTool.getNormalCardId(cardId)) && tmp.getLevel() >= 10).count();
			UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
			service.achieve(uid, count, info, ep.getRd());
		}
	}
}
