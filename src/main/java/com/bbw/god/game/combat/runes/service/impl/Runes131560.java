package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 缓速符 131560 本场战斗双方法力值恒定为5
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131560 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return 131560;
    }
    @Override
    public void doInitRunes(CombatRunesParam param) {
        param.getPerformPlayer().setMp(5);
        param.getPerformPlayer().setMaxMp(5);
        param.getOppoPlayer().setMp(5);
        param.getOppoPlayer().setMaxMp(5);
    }
}
