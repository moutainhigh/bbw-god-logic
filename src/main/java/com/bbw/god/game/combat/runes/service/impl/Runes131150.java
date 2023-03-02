package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 蛊惑符 131150 敌方全体卡牌法力值消耗+1
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131150 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return 131150;
    }
    @Override
    public void doInitRunes(CombatRunesParam param) {
        param.getOppoPlayer().getStatistics().setInitCardMp(param.getOppoPlayer().getStatistics().getInitCardMp()+1);
    }
}
