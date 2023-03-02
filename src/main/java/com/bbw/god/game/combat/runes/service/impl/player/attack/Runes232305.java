package com.bbw.god.game.combat.runes.service.impl.player.attack;

import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 5阶炽炎符
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes232305 extends PlayerAttackRune {

    @Override
    public int getRunesId() {
        return RunesEnum.CHI_YAN_PLAYER_5.getRunesId();
    }

}
