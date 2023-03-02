package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 巫术符 131400 己方召唤师受到治疗时，对敌方召唤师造成等量伤害。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131400 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131400;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar=new Action();
        int hp=0;
        for (Effect effect:param.getReceiveEffect()){
            if (effect.isValueEffect() && isPerformSelf(effect.getTargetPos(),param.getPerformPlayer().getId())&& PositionService.isZhaoHuanShiPos(effect.getTargetPos())){
                int lostHp = param.getPerformPlayer().getMaxHp() - param.getPerformPlayer().getHp();
                if (lostHp > 0){
                    if (lostHp > effect.toValueEffect().getHp()){
                        hp +=effect.toValueEffect().getHp() + effect.toValueEffect().getRoundHp();
                    }else {
                        hp +=lostHp + effect.toValueEffect().getRoundHp();
                    }
                }
            }
        }
        if (hp>0){
            CardValueEffect valueEffect=CardValueEffect.getSkillEffect(getRunesId(),param.getOppoPlayerPos());
            valueEffect.setHp(-hp);
            valueEffect.setSequence(param.getNextSeq());
            ar.addEffect(valueEffect);
        }
        return ar;
    }
}
