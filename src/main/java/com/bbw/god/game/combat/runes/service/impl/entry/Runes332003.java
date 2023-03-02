package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
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
 * 防守词条 我方召唤师受到的伤害降低[4]%。
 *
 * @author: suhq
 * @date: 2022/9/22 11:41 上午
 */
@Service
public class Runes332003 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.FANG_SHOU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        if (ListUtil.isEmpty(param.getReceiveEffect())) {
            return action;
        }
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double deductInjureRate = 0.04 * combatBuff.getLevel();
        for (Effect effect : param.getReceiveEffect()) {
            if (!effect.isValueEffect()) {
                continue;
            }
            CardValueEffect valueEffect = (CardValueEffect) effect;
            int effectHp = valueEffect.getRoundHp() + valueEffect.getHp();
            //非伤害，不做处理
            if (effectHp >= 0) {
                continue;
            }
            boolean isToZhs = PositionService.isZhaoHuanShiPos(effect.getTargetPos());
            if (!isToZhs) {
                continue;
            }
            boolean isToSelfPlayer = isPerformSelf(effect.getTargetPos(), param.getPerformPlayer().getId());
            if (isToSelfPlayer) {
                valueEffect.setHp((int) (valueEffect.getHp() * (1 - deductInjureRate)));
                valueEffect.setRoundHp((int) (valueEffect.getRoundMp() * (1 - deductInjureRate)));
            }
        }
        action.setNeedAddAnimation(false);
        return action;
    }
}
