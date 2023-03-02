package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundEndStageRunes;
import org.springframework.stereotype.Service;

/**
 * 天劫符 131430  战斗开始后第六回合，击杀敌方召唤师
 *
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131430 implements IRoundEndStageRunes {

    @Override
    public int getRunesId() {
        return 131430;
    }

    @Override
    public Action doRoundEndRunes(CombatRunesParam param) {
        Action ar = new Action();
        // 战斗开始后第六回合，击杀敌方召唤师
        if (param.getRound() >= 6) {
            int oppoZhsPos = PositionService.getZhaoHuanShiPos(param.getOppoPlayer().getId());
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), oppoZhsPos);
            effect.setHp(-param.getOppoPlayer().getHp());
            effect.setSequence(param.getNextSeq());
            ar.addEffect(effect);
        }
        return ar;
    }

}
