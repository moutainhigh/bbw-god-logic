package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.AbstractSkillSeriesRunes;
import com.bbw.god.game.combat.runes.service.series.LeiSeriesService;
import com.bbw.god.game.combat.runes.service.series.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 导电词条 我方卡牌受到雷系技能伤害时，相邻卡牌将受到本次伤害的[20]%效果。
 *
 * @author longwh
 * @date 2023/1/5 9:48
 */
@Service
public class Runes333214 extends AbstractSkillSeriesRunes {
    @Autowired
    private LeiSeriesService leiSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.DAO_DIAN_ENTRY.getRunesId();
    }

    @Override
    public SeriesService getSeriesService() {
        return leiSeriesService;
    }

    @Override
    public List<Effect> getDealEffects(CombatRunesParam param, Effect effect, BattleCard playerCard) {
        List<Effect> effects = new ArrayList<>();
        // 获取相邻位置的卡牌
        List<BattleCard> adjacentCards = getAdjacentCards(playerCard, param.getPerformPlayer());
        for (BattleCard adjacentCard : adjacentCards) {
            // 受到本次伤害的[20]%效果
            CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
            int effectValue = Math.max(-effect.toValueEffect().getRoundHp(), -effect.toValueEffect().getHp());
            double hp = 0.2 * combatBuff.getLevel() * effectValue;
            CardValueEffect buffEffect = CardValueEffect.getSkillEffect(getRunesId(), adjacentCard.getPos());
            buffEffect.setHp((int) -hp);
            buffEffect.setSequence(param.getNextSeq());
            effects.add(buffEffect);
        }
        return effects;
    }

    /**
     * 获取卡牌的相邻卡牌
     *
     * @param targetCard
     * @param player
     * @return
     */
    private List<BattleCard> getAdjacentCards(BattleCard targetCard, Player player) {
        List<BattleCard> cardList = new ArrayList<>();
        if (targetCard == null) {
            return cardList;
        }
        // 获取效果目标卡牌的上阵位置
        int index = PositionService.getBattleCardIndex(targetCard.getPos());
        // 跳过云台位置
        if (index == 0) {
            return cardList;
        }
        // 获取左相邻位置 卡牌
        int adjacentIndex = index - 1;
        int maxIndex = player.getPlayingCards().length;
        if (adjacentIndex > 0 && adjacentIndex < maxIndex) {
            BattleCard card = player.getPlayingCards(adjacentIndex);
            if (card != null){
                cardList.add(card);
            }
        }
        // 获取右相邻位置 卡牌
        adjacentIndex = index + 1;
        if (adjacentIndex > 0 && adjacentIndex < maxIndex) {
            BattleCard card = player.getPlayingCards(adjacentIndex);
            if (card != null){
                cardList.add(card);
            }
        }
        return cardList;
    }
}