package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 陷仙符 131290  每击杀敌方一张卡牌，对敌方召唤师造成等同于阵亡卡牌防御力的伤害。（含云台）
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131290 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131290;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        if (!param.isEnemyTargetCard()) {
            return ar;
        }
        CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getRunesId(),param.getOppoPlayerPos());
        valueEffect.setHp(-param.getTargetCard().getInitHp());
        valueEffect.setSequence(param.getNextSeq());
        ar.addEffect(valueEffect);
        return ar;
    }

}
