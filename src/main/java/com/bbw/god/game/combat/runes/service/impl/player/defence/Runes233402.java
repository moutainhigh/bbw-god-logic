package com.bbw.god.game.combat.runes.service.impl.player.defence;

import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 2阶净土符图
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes233402 extends PlayerDefenceRune {

    @Override
    public int getRunesId() {
        return RunesEnum.JING_TU_PLAYER_2.getRunesId();
    }

}
