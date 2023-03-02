package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 四星召唤符处理器
 * @date 2020/8/7 15:40
 **/
@Service
public class FourStarSummonSymbolProcessor extends TreasureUseProcessor {
    @Autowired
    private UserCardService userCardService;

    public FourStarSummonSymbolProcessor() {
        this.treasureEnum = TreasureEnum.FOUR_STAR_SUMMON_SYMBOL;
        this.isAutoBuy = false;
    }

    /**
     * 是否宝箱类
     *
     * @return
     */
    @Override
    public boolean isChestType() {
        return true;
    }

    /**
     * 法宝生效
     *
     * @param gu
     * @param param
     * @param rd
     */
    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        long uid = gu.getId();
        CfgCardEntity openCard = getOpenCard(uid);
        int cardId = openCard.getId();
        CardEventPublisher.pubCardAddEvent(uid, cardId, WayEnum.USE_FOUR_STAR_SYMBOL, "开启四星召唤符获得", rd);
    }

    /**
     * 获取开到的卡
     *
     * @param uid
     * @return
     */
    private CfgCardEntity getOpenCard(long uid) {
        List<CfgCardEntity> notOwnCards = userCardService.getNotOwnCards(uid, 4);
        List<CfgCardEntity> notOwnCards2 =
                notOwnCards.stream().filter(s -> s.getWay() <= 2).collect(Collectors.toList());
        // 先判断第一档位
        if (ListUtil.isNotEmpty(notOwnCards2)) {
            return PowerRandom.getRandomFromList(notOwnCards2);
        }
        // 再判断第二档位
        List<CfgCardEntity> notOwnCards3 =
                notOwnCards.stream().filter(s -> s.getWay() == 3).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(notOwnCards3)) {
            return PowerRandom.getRandomFromList(notOwnCards3);
        }
        // 没有未拥有的卡牌后，从卡池内全随机
        List<CfgCardEntity> allCards = CardTool.getAllCards(4).stream()
                .filter(s -> s.getWay() <= 3).collect(Collectors.toList());
        return PowerRandom.getRandomFromList(allCards);
    }
}
