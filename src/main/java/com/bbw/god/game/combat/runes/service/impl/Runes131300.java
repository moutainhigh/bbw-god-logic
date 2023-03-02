package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 戮仙符 131300  每当敌方召唤师受到伤害前，将对敌方全体卡牌造成等量伤害，可以对云台有效。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131300 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131300;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        if (!param.isEffectToEnemy()){
            return ar;
        }
        List<BattleCard> playingCards = param.getOppoPlayer().getPlayingCards(true);
        if (playingCards.isEmpty()) {
            return ar;
        }
        int seq=param.getNextSeq();
        CardValueEffect effect= (CardValueEffect) param.getReceiveEffect().get(0);
        int hp=effect.getHp()+effect.getRoundHp();
        if (hp>0){
            return ar;
        }
        for (BattleCard card:playingCards){
            CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getRunesId(),card.getPos());
            valueEffect.setHp(hp);
            valueEffect.setSequence(seq);
            ar.addEffect(valueEffect);
        }
        return ar;
    }

}
