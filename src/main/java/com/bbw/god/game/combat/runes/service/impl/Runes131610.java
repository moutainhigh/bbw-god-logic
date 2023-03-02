package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 哼哈符131610：每回合开始时，破除敌方场上攻防之和最高的卡牌50%永久攻防。
 *
 * @author fzj
 * @date 2021/9/26 10:25
 */
@Service
public class Runes131610 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.HENG_HA.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        //每回合使敌方场上攻防之和最高的卡牌攻防值永久减半。
        Action action = new Action();
        //对手战场牌
        List<BattleCard> playingCards = param.getOppoPlayer().getPlayingCards(true);
        if (playingCards.isEmpty()) {
            return action;
        }
        BattleCard attackCard = getAttackCard(playingCards);
        CardValueEffect valueEffect = CardValueEffect.getSkillEffect(getRunesId(), attackCard.getPos());
        valueEffect.setRoundAtk(-attackCard.getRoundAtk() / 2);
        valueEffect.setRoundHp(-attackCard.getRoundHp() / 2);
        action.addEffect(valueEffect);
        return action;
    }

    /**
     * 获取攻防最高
     *
     * @param playingCards
     * @return
     */
    private BattleCard getAttackCard(List<BattleCard> playingCards) {
        int maxValue = 0;
        int index = 0;
        for (int i = 0; i < playingCards.size(); i++) {
            int value = playingCards.get(i).getRoundAtk() + playingCards.get(i).getRoundHp();
            if (value > maxValue) {
                maxValue = value;
                index = i;
            }
        }
        return playingCards.get(index);
    }
}
