package com.bbw.god.game.combat.runes.service.impl.player.blood;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 1阶固本符图	己方召唤师血量增加1000点。	额外+100点
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes234001 extends PlayerBloodRune {

    @Override
    public int getRunesId() {
        return RunesEnum.GU_BEN_PLAYER_1.getRunesId();
    }

    @Override
    public int getAddBlood(CombatBuff combatBuff) {
        return 1000 + 100 * combatBuff.getLevel();
    }
}
