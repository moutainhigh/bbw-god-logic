package com.bbw.god.gameuser.statistic.resource.card;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 卡牌统计监听类
 * @date 2020/4/20 10:12
 */
@Component
@Slf4j
@Async
public class CardResListener {
	@Autowired
	private CardResStatisticService cardStatisticService;

	@Order(2)
	@EventListener
	public void addCard(UserCardAddEvent event) {
		try {
			EPCardAdd ep = event.getEP();
			Map<Integer, List<CfgCardEntity>> cardMap = ep.getAddCards().stream().filter(EPCardAdd.CardAddInfo::isNew)
					.map(c -> CardTool.getCardById(c.getCardId())).distinct()
					.collect(Collectors.groupingBy(CfgCardEntity::getType));
			int gold = cardMap.get(TypeEnum.Gold.getValue()) == null ? 0 :
					cardMap.get(TypeEnum.Gold.getValue()).size();
            int wood = cardMap.get(TypeEnum.Wood.getValue()) == null ? 0 :
                    cardMap.get(TypeEnum.Wood.getValue()).size();
            int water = cardMap.get(TypeEnum.Water.getValue()) == null ? 0 :
                    cardMap.get(TypeEnum.Water.getValue()).size();
            int fire = cardMap.get(TypeEnum.Fire.getValue()) == null ? 0 :
                    cardMap.get(TypeEnum.Fire.getValue()).size();
            int earth = cardMap.get(TypeEnum.Earth.getValue()) == null ? 0 :
                    cardMap.get(TypeEnum.Earth.getValue()).size();
            int fiveStar = (int) ep.getAddCards().stream().map(c ->
                    CardTool.getCardById(c.getCardId())).filter(c -> c.getStar() == 5).count();
            int gainNum = ep.getAddCards().size();
            cardStatisticService.increment(ep.getGuId(), DateUtil.getTodayInt(), gold, wood, water, fire, earth,
                    fiveStar, gainNum, ep.getWay());
            CardStatistic cardStatistic = cardStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.GAIN,
                    DateUtil.getTodayInt());
            StatisticEventPublisher.pubResourceStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), cardStatistic);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
