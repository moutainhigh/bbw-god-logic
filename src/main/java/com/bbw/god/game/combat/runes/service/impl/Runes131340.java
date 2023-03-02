package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 圣元符 131340  每回合开始时，对敌方施放一次混元技能。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131340 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131340;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        //敌方的法术值永久损失2。
        int oppoZhsPos = PositionService.getZhaoHuanShiPos(param.getOppoPlayer().getId());
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), oppoZhsPos);
        int roundMp = 2;
        effect.setRoundMp(-roundMp);
        effect.setAttackPower(Effect.AttackPower.getMaxPower());
        effect.setSequence(param.getNextSeq());
        ar.addEffect(effect);
        return ar;
    }

}
