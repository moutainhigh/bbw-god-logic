package com.bbw.god.activity.processor.cardboost;

import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.EPCardLevelUp;
import com.bbw.god.gameuser.card.event.UserCardLevelUpEvent;
import com.bbw.god.gameuser.treasure.event.EPCardDeify;
import com.bbw.god.gameuser.treasure.event.TreasureUseDeifyTokenEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class BoostCardListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private CardBoostProcessor cardBoostProcessor;
    @Autowired
    private UserCardService userCardService;

    /**
     * 卡牌升级
     *
     * @param event
     */
    @Async
    @EventListener
    @Order(1000)
    public void levelUp(UserCardLevelUpEvent event) {
        EPCardLevelUp ep = event.getEP();
        int oldLevel = ep.getOldLevel();
        int newLevel = ep.getNewLevel();
        if (newLevel >= 10 && oldLevel < 10) {
            cardBoostProcessor.removeBoostCard(ep.getGuId(), ep.getCardId());
        }

    }

    @Async
    @EventListener
    public void deifyCardEvent(TreasureUseDeifyTokenEvent event) {
        EPCardDeify ep = event.getEP();
        UserCard deifyCard = userCardService.getUserCard(ep.getGuId(), CardTool.getDeifyCardId(ep.getCardId()));
        cardBoostProcessor.setBoostCardForDeifyCard(deifyCard);
    }
}
