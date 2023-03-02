package com.bbw.god.statistics.userstatistic;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.EPCardLevelUp;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.card.event.UserCardLevelUpEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suchaobin
 * @title: UserCardListener
 * @projectName bbw-god-logic-server
 * @description:
 * @date 2019/7/1914:43
 */
@Component("UserCardListener")
@Async
public class UserCardListener {
  @Autowired
  private UserStatisticService userStatisticService;

  private static final Integer[] MAX_COUNT = {180};

  @EventListener
  @Order(2)
  public void addCard(UserCardAddEvent event) {
    AwardEnum awardEnum = AwardEnum.KP;
    EPCardAdd ep = event.getEP();
    ep.getAddCards().forEach(epCard -> userStatisticService.addOutput(ep.getGuId(), ep.getWay(), 1, MAX_COUNT,
            awardEnum));
  }

  @EventListener
  @Order(1000)
  public void addNewCard(UserCardAddEvent event) {
    EPCardAdd ep = event.getEP();
    List<EPCardAdd.CardAddInfo> addCards = ep.getAddCards();
    String wayName = ep.getWay().getName();
      addCards.stream().filter(EPCardAdd.CardAddInfo::isNew).forEach(epCard -> {
      String cardName = CardTool.getCardById(epCard.getCardId()).getName();
      userStatisticService.recordAddNewCardToRedis(ep.getGuId(), cardName, wayName);
    });
  }

  @EventListener
  @Order(1000)
  public void oldCardLevelUp(UserCardLevelUpEvent event) {
    EPCardLevelUp ep = event.getEP();
    String cardName = CardTool.getCardById(ep.getCardId()).getName();
    userStatisticService.recordOldCardLevelUpToRedis(ep.getGuId(), cardName, ep.getOldLevel(), ep.getNewLevel());
  }
}
