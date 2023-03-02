package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 健体 我方召唤师血量上限增加[10]%。
 *
 * @author: suhq
 * @date: 2022/9/23 5:40 下午
 */
@Service
public class Runes332002 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JIAN_TI_ENTRY.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        Player player = param.getPerformPlayer();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        double rate = 0.1 * combatBuff.getLevel();
        int hp = player.getHp();
        hp = (int) (hp * (1 + rate));
        player.setHp(hp);
        player.setBeginHp(hp);
        player.setMaxHp(hp);
    }
}
