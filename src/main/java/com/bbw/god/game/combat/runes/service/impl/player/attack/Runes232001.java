package com.bbw.god.game.combat.runes.service.impl.player.attack;

import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

/**
 * 1阶神针符图	己方金属性卡牌攻击值增加30点。	每级额外+5点
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
@Service
public class Runes232001 extends PlayerAttackRune {

    @Override
    public int getRunesId() {
        return RunesEnum.SHEN_ZHEN_PLAYER_1.getRunesId();
    }
}
