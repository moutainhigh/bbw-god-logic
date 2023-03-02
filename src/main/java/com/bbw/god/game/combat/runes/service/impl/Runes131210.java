package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 威慑符 131210  每回合向敌方全体卡牌施放集体震慑（含云台）
 * 无视金刚,含云台
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131210 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131210;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        List<BattleCard> playingCards = param.getOppoPlayer().getPlayingCards(true);
        if (playingCards.isEmpty()) {
            return ar;
        }
        int seq=param.getNextSeq();
        for (BattleCard card:playingCards) {
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
            effect.setAtkTimes(-0.5);
            effect.setSequence(seq);
            ar.addEffect(effect);
        }
        return ar;
    }
}
