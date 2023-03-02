package com.bbw.god.game.combat.runes.service.impl.player.defence;

import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 3阶落木符图
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes233103 extends PlayerDefenceRune {

    @Override
    public int getRunesId() {
        return RunesEnum.LUO_MU_PLAYER_3.getRunesId();
    }

}
