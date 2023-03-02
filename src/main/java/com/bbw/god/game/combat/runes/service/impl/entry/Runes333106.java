package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 夜袭词条 我方手牌上限减少[2]张。
 *
 * @author longwh
 * @date 2022/12/30 11:41
 */
@Service
public class Runes333106 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.YE_XI_ENTRY.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        Player.Statistics statistics = param.getPerformPlayer().getStatistics();
        statistics.setHandCardUpLimit(statistics.getHandCardUpLimit() - 2);
    }
}