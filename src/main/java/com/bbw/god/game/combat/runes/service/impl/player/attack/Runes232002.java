package com.bbw.god.game.combat.runes.service.impl.player.attack;

import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 2阶神针符图
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes232002 extends PlayerAttackRune {

    @Override
    public int getRunesId() {
        return RunesEnum.SHEN_ZHEN_PLAYER_2.getRunesId();
    }
}
