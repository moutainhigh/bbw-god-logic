package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.runes.service.series.LeiSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 引雷词条 我方卡牌造成的雷系技能伤害降低[50]%。
 *
 * @author longwh
 * @date 2022/12/29 14:43
 */
@Service
public class Runes333103 implements IRoundStageRunes {
    @Autowired
    private LeiSeriesService leiSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.YIN_LEI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double rate = -0.5 * combatBuff.getLevel();
        // 增加负值倍率buff 相当于伤害降低
        return leiSeriesService.addInjure(getRunesId(), param, rate);
    }
}