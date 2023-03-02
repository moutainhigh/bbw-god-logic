package com.bbw.god.gameuser.statistic.behavior.card;

import com.bbw.common.DateUtil;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.card.event.EPCardHierarchyUp;
import com.bbw.god.gameuser.card.event.UserCardHierarchyUpEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.mall.cardshop.event.DrawEndEvent;
import com.bbw.god.mall.cardshop.event.EPDraw;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 抽卡行为监听
 * @date 2020/3/30 15:58
 */
@Component
@Slf4j
@Async
public class CardBehaviorListener {
	@Autowired
	private DrawCardStatisticService drawCardStatisticService;
	@Autowired
	private HierarchyCardStatisticService hierarchyCardStatisticService;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void drawCard(DrawEndEvent endEvent) {
		try {
			EventParam<EPDraw> ep = (EventParam<EPDraw>) endEvent.getSource();
			EPDraw epDraw = ep.getValue();
			Integer drawTimes = epDraw.getDrawTimes();
			int todayInt = DateUtil.getTodayInt();
			drawCardStatisticService.drawCard(ep.getGuId(), epDraw.getCardPoolType(), drawTimes, DateUtil.getTodayInt());
			DrawCardStatistic statistic = drawCardStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.NONE,
					todayInt);
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Order(2)
	@EventListener
	public void hierarchyCard(UserCardHierarchyUpEvent event) {
		try {
			EPCardHierarchyUp ep = event.getEP();
			Integer cardId = ep.getCardId();
			CfgCardEntity card = CardTool.getCardById(cardId);
			int todayInt = DateUtil.getTodayInt();
			hierarchyCardStatisticService.hierarchyCard(ep.getGuId(), todayInt, card.getType(), card.getStar());
			HierarchyCardStatistic statistic = hierarchyCardStatisticService.fromRedis(ep.getGuId(),
					StatisticTypeEnum.NONE, todayInt);
			StatisticEventPublisher.pubBehaviorStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
