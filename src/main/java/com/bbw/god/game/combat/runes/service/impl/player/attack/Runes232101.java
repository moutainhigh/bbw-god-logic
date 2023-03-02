package com.bbw.god.game.combat.runes.service.impl.player.attack;

import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 1阶飞花符	己方木属性卡牌攻击值增加30点。	额外+5点	增加80点
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes232101 extends PlayerAttackRune {

    @Override
    public int getRunesId() {
        return RunesEnum.FEI_HUA_PLAYER_1.getRunesId();
    }

}
