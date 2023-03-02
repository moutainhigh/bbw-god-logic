package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.impl.Runes131670;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 军师符图	3阶	战斗开始时，有30%概率（可升级）己方所有阵位获得军师位效果。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2021/11/16 2:33 下午
 */
@Service
public class Runes231203 extends PlayerStatusRune {
    @Autowired
    private Runes131670 runes131670;

    @Override
    public int getRunesId() {
        return RunesEnum.JUN_SHI_PLAYER.getRunesId();
    }

    @Override
    Action handleStatus(CombatRunesParam param) {
        return runes131670.doRoundRunes(param, getRunesId());
    }
}
