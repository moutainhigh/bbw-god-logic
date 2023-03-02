package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 藤甲词条 敌方召唤师受到的伤害降低[6]%；我方召唤师受到的伤害增加[2]%。
 *
 * @author: suhq
 * @date: 2022/9/22 11:41 上午
 */
@Service
public class Runes331102 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.TENG_JIA_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        action.setNeedAddAnimation(false);
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        for (Effect effect : param.getReceiveEffect()) {
            if (!effect.isValueEffect()) {
                continue;
            }
            CardValueEffect valueEffect = (CardValueEffect) effect;
            int effectHp = valueEffect.getRoundHp() + valueEffect.getHp();
            //非伤害，不做处理
            if (effectHp >= 0) {
                return action;
            }
            boolean isToZhs = PositionService.isZhaoHuanShiPos(effect.getTargetPos());
            if (!isToZhs) {
                continue;
            }
            if (isPerformSelf(effect.getTargetPos(), param.getPerformPlayer().getId())) {
                double addInjureRate = 0.02 * combatBuff.getLevel();
                valueEffect.setHp((int) (valueEffect.getHp() * (1 + addInjureRate)));
//                valueEffect.setRoundHp((int) (valueEffect.getRoundMp() * (1 + addInjureRate)));
            } else {
                double deductInjureRate = 0.06 * combatBuff.getLevel();
                valueEffect.setHp((int) (valueEffect.getHp() * (1 - deductInjureRate)));
//                valueEffect.setRoundHp((int) (valueEffect.getRoundMp() * (1 - deductInjureRate)));
            }
        }
        return action;
    }
}
