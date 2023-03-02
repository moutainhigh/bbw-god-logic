package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import com.bbw.god.game.combat.runes.service.series.LeiSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 百草词条 我方卡牌造成的毒系技能伤害降低[50]%。
 *
 * @author longwh
 * @date 2022/12/29 16:51
 */
@Service
public class Runes333104 implements IRoundStageRunes {
    @Autowired
    private DuSeriesService duSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.BAI_CAO_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double rate = -0.5 * combatBuff.getLevel();
        // 增加负值倍率buff 相当于伤害降低
        return duSeriesService.addInjure(getRunesId(), param, rate);
    }
}