package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 毒沼符31170  每回合开始时，敌方全体卡牌流失12%永久防御
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131170 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131170;
    }


    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action=new Action();
        List<BattleCard> cards=param.getOppoPlayer().getPlayingCards(true);
        if (cards==null || cards.isEmpty()){
            return action;
        }
        List<Effect> effects=new ArrayList<>();
        int seq=param.getNextSeq();
        for (BattleCard card:cards){
            if (card==null){
                continue;
            }
            int hp=getInt(card.getRoundHp()*0.12f);
            CardValueEffect effect=CardValueEffect.getSkillEffect(getRunesId(),card.getPos());
            effect.setSequence(seq);
            effect.setRoundHp(-hp);
            effects.add(effect);
        }
        action.addEffects(effects);
        return action;
    }
}
