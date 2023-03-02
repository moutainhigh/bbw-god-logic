package com.bbw.god.game.combat.runes.service.impl.entry.series;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import com.bbw.god.game.combat.runes.service.series.JuSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 毒箭词条	敌方造成的狙系技能增加[40]点伤害，毒系技能增加[10]点伤害。
 *
 * @author: suhq
 * @date: 2022/9/20 5:18 下午
 */
@Service
public class Runes331007 extends AbstractSeriesEntry {
    @Autowired
    private JuSeriesService juSeriesService;
    @Autowired
    private DuSeriesService duSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.DU_JIAN_ENTRY.getRunesId();
    }

    @Override
    public void handleInjure(CombatBuff combatBuff, Action action, CombatRunesParam param) {
        int addJuValue = 40 * combatBuff.getLevel();
        juSeriesService.addOpponentInjure(action, getRunesId(), param, addJuValue);
        int addDuValue = 10 * combatBuff.getLevel();
        duSeriesService.addOpponentInjure(action, getRunesId(), param, addDuValue);
    }
}
