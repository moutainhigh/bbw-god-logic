package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大疫符 131320  每回合开始时，敌方全体卡牌流失50%的永久防御。(含云台）
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131320 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131320;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        List<BattleCard> cards= param.getOppoPlayer().getPlayingCards(true);
        if (cards.isEmpty()){
            return ar;
        }
        int seq=param.getNextSeq();
        for (BattleCard card:cards){
            CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getRunesId(),card.getPos());
            valueEffect.setSourcePos(param.getMyPlayerPos());
            valueEffect.setRoundHp(-getInt(card.getRoundHp()*0.5f));
            valueEffect.setSequence(seq);
            ar.addEffect(valueEffect);
        }
        return ar;
    }

}
