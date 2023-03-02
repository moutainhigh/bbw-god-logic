package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 灭魄符 131160 敌方阵亡的卡牌直接移出游戏。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131160 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131160;
    }


    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action=new Action();
        Effect effect=param.getReceiveEffect().get(0);
        if (effect.isPositionEffect() && !PositionService.getPlayerIdByPos(effect.getTargetPos()).equals(param.getPerformPlayer().getId())){
            CardPositionEffect positionEffect= CloneUtil.clone(effect.toPositionEffect());
            positionEffect.setSourceID(getRunesId());
            positionEffect.setToPositionType(PositionType.DEGENERATOR);
            positionEffect.setToPos(-1);
            action.addEffect(positionEffect);
            param.setReceiveEffect(null);
            return action;
        }
        return action;
    }
}
