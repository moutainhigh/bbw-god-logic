package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 招魂符 131040 己方阵亡的卡牌有50%概率回到牌堆（拥有复活技能优先触发）
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131040 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131040;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action=new Action();
        if (param.isEnemyTargetCard()){
            //非己方
            return action;
        }
        if (PowerRandom.hitProbability(50)) {
            return action;
        }
        CardPositionEffect positionEffect= CardPositionEffect.getSkillEffectToTargetPos(getRunesId(),param.getTargetCard().getPos());
        positionEffect.setSourceID(getRunesId());
        positionEffect.setToPositionType(PositionType.DRAWCARD);
        action.addEffect(positionEffect);
        return action;
    }


}
