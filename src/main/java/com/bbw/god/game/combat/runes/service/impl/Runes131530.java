package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 升仙符 131530 初始法力值+2
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131530 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return 131530;
    }


    @Override
    public void doInitRunes(CombatRunesParam param) {
       int mp=param.getPerformPlayer().getMp()+2;
       param.getPerformPlayer().setMp(mp);
       param.getPerformPlayer().setMaxMp(mp);
    }
}
