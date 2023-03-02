package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 健体符 131520  己方召唤师血量+50%
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131520 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return 131520;
    }


    @Override
    public void doInitRunes(CombatRunesParam param) {
       int hp=getInt(param.getPerformPlayer().getHp()*1.5f);
       param.getPerformPlayer().setHp(hp);
        param.getPerformPlayer().setMaxHp(hp);
    }
}
