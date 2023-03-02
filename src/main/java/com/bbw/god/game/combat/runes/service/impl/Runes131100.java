package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 禁法符31100  禁用双方一切法宝
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131100 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return 131100;
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        param.getPerformPlayer().getWeapons().clear();
        param.getOppoPlayer().getWeapons().clear();
    }
}
