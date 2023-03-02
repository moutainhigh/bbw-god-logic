package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 健体符图	2阶	战斗开始时，有30%概率（可升级）发动，己方召唤师血量增加50%。	额外+7%概率
 *
 * @author: suhq
 * @date: 2021/11/16 2:28 下午
 */
@Service
public class Runes231101 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.JIAN_TI_PLAYER.getRunesId();
    }


    @Override
    public void doInitRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (null == combatBuff || !combatBuff.ifToPerform(30, 7)) {
            return;
        }
        int hp = getInt(param.getPerformPlayer().getHp() * 1.5f);
        param.getPerformPlayer().setHp(hp);
        param.getPerformPlayer().setMaxHp(hp);
    }
}
