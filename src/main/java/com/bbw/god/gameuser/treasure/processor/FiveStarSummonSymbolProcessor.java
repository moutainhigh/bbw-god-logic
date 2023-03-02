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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 五星召唤符处理器
 * @date 2020/8/7 15:40
 **/
@Service
public class FiveStarSummonSymbolProcessor extends TreasureUseProcessor {
    @Autowired
    private UserCardService userCardService;

    private static final List<Integer> CARDS_1 = Arrays.asList(101, 236, 302, 401, 502);
    private static final List<Integer> CARDS_2 = Arrays.asList(102, 202, 301, 402);
    private static final List<Integer> CARDS_3 = Arrays.asList(501);

    public FiveStarSummonSymbolProcessor() {
        this.treasureEnum = TreasureEnum.FIVE_STAR_SUMMON_SYMBOL;
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
        CardEventPublisher.pubCardAddEvent(uid, cardId, WayEnum.USE_FIVE_STAR_SYMBOL, "开启五星召唤符获得", rd);
    }

    /**
     * 获取开到的卡
     *
     * @param uid
     * @return
     */
    private CfgCardEntity getOpenCard(long uid) {
        List<CfgCardEntity> notOwnCards = userCardService.getNotOwnCards(uid, 5);
        // 先判断第一档位
        List<CfgCardEntity> notOwnCards1 = notOwnCards.stream().filter(
                s -> CARDS_1.contains(s.getId())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(notOwnCards1)) {
            return PowerRandom.getRandomFromList(notOwnCards1);
        }
        // 再判断第二档位
        List<CfgCardEntity> notOwnCards2 = notOwnCards.stream().filter(
                s -> CARDS_2.contains(s.getId())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(notOwnCards2)) {
            return PowerRandom.getRandomFromList(notOwnCards2);
        }
        // 判断第三档位
        List<CfgCardEntity> notOwnCards3 = notOwnCards.stream().filter(
                s -> CARDS_3.contains(s.getId())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(notOwnCards3)) {
            return PowerRandom.getRandomFromList(notOwnCards3);
        }
        // 都获得了随机选一个
        List<Integer> allCards = new ArrayList<>();
        allCards.addAll(CARDS_1);
        allCards.addAll(CARDS_2);
        allCards.addAll(CARDS_3);
        return CardTool.getCardById(PowerRandom.getRandomFromList(allCards));
    }
}
