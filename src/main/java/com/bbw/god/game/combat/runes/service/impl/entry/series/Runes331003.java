package com.bbw.god.game.combat.runes.service.impl.entry.series;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import com.bbw.god.game.combat.runes.service.series.HuoSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 火毒词条	敌方造成的火系技能增加[60]点伤害，毒系技能增加[5]点伤害。
 *
 * @author: suhq
 * @date: 2022/9/20 5:18 下午
 */
@Service
public class Runes331003 extends AbstractSeriesEntry {
    @Autowired
    private HuoSeriesService huoSeriesService;
    @Autowired
    private DuSeriesService duSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.HUO_DU_ENTRY.getRunesId();
    }

    @Override
    public void handleInjure(CombatBuff combatBuff, Action action, CombatRunesParam param) {
        int addHuoValue = 60 * combatBuff.getLevel();
        huoSeriesService.addOpponentInjure(action, getRunesId(), param, addHuoValue);
        int  addDuValue = 5 * combatBuff.getLevel();
        duSeriesService.addOpponentInjure(action, getRunesId(), param, addDuValue);
    }
}
