package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 亡命符 131470 己方召唤师受到伤害时，对敌方召唤师造成等量伤害。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131470 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131470;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar=new Action();
        int hp=0;
        int roundHp=0;
        for (Effect effect:param.getReceiveEffect()){
            if (effect.isValueEffect() && isPerformSelf(effect.getTargetPos(),param.getPerformPlayer().getId())&&
            PositionService.isZhaoHuanShiPos(effect.getTargetPos())){
                hp+=effect.toValueEffect().getHp();
                roundHp+=effect.toValueEffect().getRoundHp();
            }
        }
        if (hp<0 || roundHp<0){
            CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getRunesId(),param.getOppoPlayerPos());
            valueEffect.setHp(hp);
            valueEffect.setRoundHp(roundHp);
            valueEffect.setSequence(param.getNextSeq());
            ar.addEffect(valueEffect);
        }
        return ar;
    }
}
