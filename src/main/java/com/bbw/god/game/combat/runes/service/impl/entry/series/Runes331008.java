package com.bbw.god.game.combat.runes.service.impl.entry.series;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.series.HuoSeriesService;
import com.bbw.god.game.combat.runes.service.series.JuSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 火箭词条	敌方造成的狙系技能增加[40]点伤害，火系技能增加[40]点伤害。
 *
 * @author: suhq
 * @date: 2022/9/20 5:18 下午
 */
@Service
public class Runes331008 extends AbstractSeriesEntry {
    @Autowired
    private JuSeriesService juSeriesService;
    @Autowired
    private HuoSeriesService huoSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.HUO_JIAN_ENTRY.getRunesId();
    }

    @Override
    public void handleInjure(CombatBuff combatBuff, Action action, CombatRunesParam param) {
        int addJuValue = 40 * combatBuff.getLevel();
        juSeriesService.addOpponentInjure(action, getRunesId(), param, addJuValue);
        int addHuoValue = 40 * combatBuff.getLevel();
        huoSeriesService.addOpponentInjure(action, getRunesId(), param, addHuoValue);
    }
}
