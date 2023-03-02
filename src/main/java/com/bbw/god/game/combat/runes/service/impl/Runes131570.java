package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 迟缓符 131570 战斗开始时，双方初始法力值为3点
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131570 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return 131570;
    }


    @Override
    public void doInitRunes(CombatRunesParam param) {
       param.getPerformPlayer().setMp(3);
        param.getPerformPlayer().setMaxMp(3);
       param.getOppoPlayer().setMp(3);
        param.getOppoPlayer().setMaxMp(3);
    }
}
