package com.bbw.god.game.combat.runes.service.impl.entry.series;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.series.HuoSeriesService;
import com.bbw.god.game.combat.runes.service.series.LeiSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 火雷词条	敌方造成的火系技能增加[60]点伤害，雷系技能增加[40]点伤害。
 *
 * @author: suhq
 * @date: 2022/9/20 5:18 下午
 */
@Service
public class Runes331004 extends AbstractSeriesEntry {
    @Autowired
    private HuoSeriesService huoSeriesService;
    @Autowired
    private LeiSeriesService leiSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.HUO_LEI_ENTRY.getRunesId();
    }

    @Override
    public void handleInjure(CombatBuff combatBuff, Action action, CombatRunesParam param) {
        int addHuoValue = 60 * combatBuff.getLevel();
        huoSeriesService.addOpponentInjure(action, getRunesId(), param, addHuoValue);
        int addLeiValue = 40 * combatBuff.getLevel();
        leiSeriesService.addOpponentInjure(action, getRunesId(), param, addLeiValue);
    }
}
