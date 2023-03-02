package com.bbw.god.game.combat;

import com.bbw.cache.LocalCache;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 战斗卡牌临时本地缓存处理
 *
 * @author: suhq
 * @date: 2022/6/27 5:54 下午
 */
public class BattleCardLocalCacheService {

    /**
     * 将目标卡牌临时缓存起来，便于目标卡牌阵亡后也能处理回合结束的效果
     *
     * @param combat
     * @param card
     */
    public static void cacheCardToDoRoundEnd(Combat combat, Player player, BattleCard card) {
        CachedCardsToDoRoundEnd cachedCards = getCachedObjToDoRoundEnd(combat, player);
        if (null == cachedCards) {
            cachedCards = new CachedCardsToDoRoundEnd();
        }
        cachedCards.addCard(card);

        String type = "cardCachedToDoRoundEnd";
        //roundEnd执行在回合+1后，所以需要+1
        int round = combat.getRound()+1;
        String key = combat.getId() + ":" + round + ":" + player.getUid();
        LocalCache.getInstance().put(type, key, cachedCards);
    }

    /**
     * 从缓存中获取待处理回合结束效果的卡牌
     *
     * @param combat
     * @return
     */
    public static List<BattleCard> getCachedCardsToDoRoundEnd(Combat combat, Player player) {
        CachedCardsToDoRoundEnd cachedCards = getCachedObjToDoRoundEnd(combat, player);
        if (null == cachedCards) {
            return null;
        }
        return cachedCards.getCards();
    }

    /**
     * 从缓存中获取待处理回合结束效果的卡牌
     *
     * @param combat
     * @return
     */
    private static CachedCardsToDoRoundEnd getCachedObjToDoRoundEnd(Combat combat, Player player) {
        String type = "cardCachedToDoRoundEnd";
        String key = combat.getId() + ":" + combat.getRound() + ":" + player.getUid();
        CachedCardsToDoRoundEnd cachedBattleCards = LocalCache.getInstance().get(type, key);
        return cachedBattleCards;
    }

    /**
     * 本地卡牌临时缓存对象
     */
    @Data
    public static class CachedCardsToDoRoundEnd{
        private List<BattleCard> cards;

        public void addCard(BattleCard battleCard){
            if (null == cards){
                cards = new ArrayList<>();
            }
            cards.add(battleCard);
        }
    }
}
