package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 偏移词条 敌方卡牌受到的普通攻击，减少[6]%本次攻击受到的伤害。
 *
 * @author: suhq
 * @date: 2022/9/22 2:13 下午
 */
@Service
public class Runes331301 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.PIAN_YI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        action.setNeedAddAnimation(false);
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double deductRate = 0.06 * combatBuff.getLevel();
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            if (CombatSkillEnum.NORMAL_ATTACK.getValue() != skillId) {
                continue;
            }
            if (isPerformSelf(effect.getTargetPos(), param.getPerformPlayer().getId())) {
                continue;
            }
            CardValueEffect valueEffect = effect.toValueEffect();
            valueEffect.setHp((int) (valueEffect.getHp() * (1 - deductRate)));
//            action.setTakeEffect(true);
        }
        return action;
    }
}
