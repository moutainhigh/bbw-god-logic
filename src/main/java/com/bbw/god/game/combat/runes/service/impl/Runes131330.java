package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 永生符 131330  己方召唤师将不会受到任何伤害。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131330 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131330;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        if (param.isEffectToEnemy()){
            return ar;
        }
        CardValueEffect effect = (CardValueEffect) param.getReceiveEffect().get(0);
        if ((effect.getRoundHp()+effect.getHp())<0){
            effect.setHp(0);
            effect.setRoundHp(0);
            ar.addEffect(effect);
            param.getReceiveEffect().clear();
            AnimationSequence as = new AnimationSequence(effect);
            AnimationSequence.Animation animation=new AnimationSequence.Animation();
            animation.setHp(0);
            animation.setPos(effect.getTargetPos());
            as.setSeq(effect.getSequence());
            as.add(animation);
            ar.addClientAction(as);
        }
        return ar;
    }

}
