package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 落石符 131480 每回合敌方召唤师血量上限降低20%
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131480 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131480;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar=new Action();
        CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getRunesId(),param.getOppoPlayerPos());
        valueEffect.setSourcePos(param.getMyPlayerPos());
        valueEffect.setRoundHp(-getInt(param.getOppoPlayer().getMaxHp()*0.2f));
        valueEffect.setSequence(param.getNextSeq());
        ar.addEffect(valueEffect);
        return ar;
    }
}
