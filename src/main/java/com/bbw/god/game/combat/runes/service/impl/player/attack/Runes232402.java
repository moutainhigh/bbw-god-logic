package com.bbw.god.game.combat.runes.service.impl.player.attack;

import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 2阶烈石符图
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes232402 extends PlayerAttackRune {

    @Override
    public int getRunesId() {
        return RunesEnum.LIE_SHI_PLAYER_2.getRunesId();
    }

}
