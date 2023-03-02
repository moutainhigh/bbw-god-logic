package com.bbw.god.game.combat.runes.service.impl.entry.series;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import com.bbw.god.game.combat.runes.service.series.LeiSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 雷毒词条	敌方造成的雷系技能增加[120]点伤害，毒系技能增加[5]点伤害。
 *
 * @author: suhq
 * @date: 2022/9/20 5:18 下午
 */
@Service
public class Runes331005 extends AbstractSeriesEntry {
    @Autowired
    private LeiSeriesService leiSeriesService;
    @Autowired
    private DuSeriesService duSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.LEI_DU_ENTRY.getRunesId();
    }

    @Override
    public void handleInjure(CombatBuff combatBuff, Action action, CombatRunesParam param) {
        int addLeiValue = 120 * combatBuff.getLevel();
        leiSeriesService.addOpponentInjure(action, getRunesId(), param, addLeiValue);
        int addDuValue = 5 * combatBuff.getLevel();
        duSeriesService.addOpponentInjure(action, getRunesId(), param, addDuValue);
    }
}
