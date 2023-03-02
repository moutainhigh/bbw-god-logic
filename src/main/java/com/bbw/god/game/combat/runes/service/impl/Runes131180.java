package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 封天符 131180 禁用双方云台位。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131180 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return 131180;
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        param.getPerformPlayer().setBanYunTai(TimesLimit.noLimit());
        param.getOppoPlayer().setBanYunTai(TimesLimit.noLimit());
    }


}
