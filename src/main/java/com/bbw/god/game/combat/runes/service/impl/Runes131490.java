package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 得道符 131490 己方全体卡牌法力值消耗-1
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131490 implements IInitStageRunes {
    @Override
    public int getRunesId() {
        return 131490;
    }


    @Override
    public void doInitRunes(CombatRunesParam param) {
        param.getPerformPlayer().getStatistics().setInitCardMp(param.getPerformPlayer().getStatistics().getInitCardMp()-1);

    }
}
