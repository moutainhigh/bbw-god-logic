package com.bbw.god.game.combat.runes.service.impl.player.defence;

import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 1阶甘霖符图
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes233201 extends PlayerDefenceRune {

    @Override
    public int getRunesId() {
        return RunesEnum.GAN_LIN_PLAYER_1.getRunesId();
    }

}
