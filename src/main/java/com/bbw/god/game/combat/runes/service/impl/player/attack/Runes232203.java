package com.bbw.god.game.combat.runes.service.impl.player.attack;

import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 3阶凝冰符
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes232203 extends PlayerAttackRune {

    @Override
    public int getRunesId() {
        return RunesEnum.NING_BING_PLAYER_3.getRunesId();
    }

}
