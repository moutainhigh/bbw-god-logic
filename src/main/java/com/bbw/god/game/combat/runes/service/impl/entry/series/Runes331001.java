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
 * 毒火词条	敌方造成的毒系技能增加[15]点伤害，火系技能增加[20]点伤害。
 *
 * @author: suhq
 * @date: 2022/9/20 5:18 下午
 */
@Service
public class Runes331001 extends AbstractSeriesEntry {
    @Autowired
    private DuSeriesService duSeriesService;
    @Autowired
    private HuoSeriesService huoSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.DU_HUO_ENTRY.getRunesId();
    }

    @Override
    public void handleInjure(CombatBuff combatBuff, Action action, CombatRunesParam param) {
        int addDuValue = 15 * combatBuff.getLevel();
        duSeriesService.addOpponentInjure(action, getRunesId(), param, addDuValue);
        int addHuoValue = 20 * combatBuff.getLevel();
        huoSeriesService.addOpponentInjure(action, getRunesId(), param, addHuoValue);
    }
}
