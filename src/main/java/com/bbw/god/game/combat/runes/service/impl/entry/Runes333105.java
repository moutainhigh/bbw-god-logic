package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 咒血词条 我方召唤师受到的血量恢复效果减少[50]%。
 *
 * @author longwh
 * @date 2022/12/30 10:44
 */
@Service
public class Runes333105 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.ZHOU_XUE_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        if (ListUtil.isEmpty(param.getReceiveEffect())) {
            return action;
        }
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double deductInjureRate = 0.5 * combatBuff.getLevel();
        for (Effect effect : param.getReceiveEffect()) {
            if (!effect.isValueEffect()) {
                continue;
            }
            // 不是我方召唤师 不处理
            if (effect.getTargetPos() != param.getMyPlayerPos()) {
                continue;
            }
            CardValueEffect valueEffect = effect.toValueEffect();
            int effectHp = valueEffect.getRoundHp() + valueEffect.getHp();
            // 不是恢复血量，不做处理
            if (effectHp <= 0) {
                continue;
            }
            valueEffect.setHp((int) (valueEffect.getHp() * (1 - deductInjureRate)));
            valueEffect.setRoundHp((int) (valueEffect.getRoundMp() * (1 - deductInjureRate)));
            // 触发 补充一个效果动画
            CardValueEffect animationEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getTargetPos());
            animationEffect.setSequence(param.getNextSeq());
            action.addEffect(animationEffect);
        }
        return action;
    }
}