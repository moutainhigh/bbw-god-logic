package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 泰体符31010 己方召唤师血量翻倍
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131010 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return 131010;
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        Player player=param.getPerformPlayer();
        int hp=player.getHp()*2;
        player.setHp(hp);
        player.setBeginHp(hp);
        player.setMaxHp(hp);
    }


}
