package com.bbw.god.game.combat.runes.service.impl.player.blood;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 5阶固本符图
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes234005 extends PlayerBloodRune {

    @Override
    public int getRunesId() {
        return RunesEnum.GU_BEN_PLAYER_5.getRunesId();
    }

    @Override
    public int getAddBlood(CombatBuff combatBuff) {
        return 2000 + 800 * combatBuff.getLevel();
    }
}
