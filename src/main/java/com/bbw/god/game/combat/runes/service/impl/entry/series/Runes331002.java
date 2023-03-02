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
 * 毒雷词条	敌方造成的毒系技能增加[15]点伤害，雷系技能增加[40]点伤害。
 *
 * @author: suhq
 * @date: 2022/9/20 5:18 下午
 */
@Service
public class Runes331002 extends AbstractSeriesEntry {
    @Autowired
    private DuSeriesService duSeriesService;
    @Autowired
    private LeiSeriesService leiSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.DU_LEI_ENTRY.getRunesId();
    }

    @Override
    public void handleInjure(CombatBuff combatBuff, Action action, CombatRunesParam param) {
        int addDuValue = 15 * combatBuff.getLevel();
        duSeriesService.addOpponentInjure(action, getRunesId(), param, addDuValue);
        int addLeiValue = 40 * combatBuff.getLevel();
        leiSeriesService.addOpponentInjure(action, getRunesId(), param, addLeiValue);
    }
}
