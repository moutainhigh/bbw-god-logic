package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.runes.service.series.JuSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 疾风词条 我方卡牌造成的狙系技能伤害降低[50]%。
 *
 * @author longwh
 * @date 2022/12/29 10:18
 */
@Service
public class Runes333101 implements IRoundStageRunes {
    @Autowired
    private JuSeriesService juSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.JI_FENG_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double rate = -0.5 * combatBuff.getLevel();
        // 增加负值倍率buff 相当于伤害降低
        return juSeriesService.addInjure(getRunesId(), param, rate);
    }
}