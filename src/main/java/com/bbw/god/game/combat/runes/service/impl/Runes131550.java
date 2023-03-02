package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 辟地符 131550 战斗开始时，双方战斗位置全部解锁且初始法力值翻倍。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131550 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return 131550;
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        param.getPerformPlayer().setUnlockAllPosBuff(true);
        param.getOppoPlayer().setUnlockAllPosBuff(true);
        param.getPerformPlayer().resetMp(param.getPerformPlayer().getMp()*2);
        param.getOppoPlayer().resetMp(param.getOppoPlayer().getMp()*2);
    }


}
