package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 飞仙符31120  战斗开始初始法力值+4
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131120 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return 131120;
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        int mp=param.getPerformPlayer().getMp()+4;
        param.getPerformPlayer().setMp(mp);
        param.getPerformPlayer().setMaxMp(mp);
    }
}
