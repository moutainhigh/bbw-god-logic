package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 宝鉴符 131540 回光召唤师所受的法术
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131540 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131540;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar=new Action();
        int seq=param.getNextSeq();
        for (Effect effect:param.getReceiveEffect()){
            if (effect.getPerformSkillID()<4000 && effect.isValueEffect() && isPerformSelf(effect.getTargetPos(),param.getPerformPlayer().getId()) && PositionService.isZhaoHuanShiPos(effect.getTargetPos())){
                if (!effect.toValueEffect().isHarmEffect()){
                    continue;
                }
                Effect copy= CloneUtil.clone(effect);
                copy.setSourcePos(param.getMyPlayerPos());
                copy.setTargetPos(param.getOppoPlayerPos());
                copy.setSourceID(getRunesId());
                copy.setSequence(seq);
                ar.addEffect(copy);
            }
        }
        return ar;
    }
}
