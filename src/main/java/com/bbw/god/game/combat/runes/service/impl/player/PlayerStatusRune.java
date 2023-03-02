package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;

/**
 * 战斗开始时相关护符效果的基类
 *
 * @author: suhq
 * @date: 2021/11/16 10:58 上午
 */
public abstract class PlayerStatusRune implements IInitStageRunes, IRoundStageRunes {

    @Override
    public void doInitRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (null == combatBuff || !combatBuff.ifToPerform(30, 7)) {
            return;
        }
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        if (!param.getPerformPlayer().hasStatus(getRunesId())) {
            return action;
        }

        return handleStatus(param);
    }

    abstract Action handleStatus(CombatRunesParam param);
}
