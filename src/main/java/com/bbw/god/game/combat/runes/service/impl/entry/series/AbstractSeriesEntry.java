package com.bbw.god.game.combat.runes.service.impl.entry.series;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;

/**
 * 敌方造成的**系技能伤害增加[8]%，**系技能伤害增加[2]%。
 *
 * @author: suhq
 * @date: 2022/9/22 11:10 上午
 */
public abstract class AbstractSeriesEntry implements IRoundStageRunes {
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        //不需要动画
        action.setNeedAddAnimation(false);
        param.getNextSeq();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (null == combatBuff) {
            return action;
        }
        if (isPerformSelf(param.getReceiveEffect().get(0).getSourcePos(), param.getPerformPlayer().getId())) {
            return action;
        }
        handleInjure(combatBuff, action, param);
        return action;
    }

    /**
     * 处理伤害
     *
     * @param combatBuff
     * @param action
     * @param param
     */
    public abstract void handleInjure(CombatBuff combatBuff, Action action, CombatRunesParam param);
}
