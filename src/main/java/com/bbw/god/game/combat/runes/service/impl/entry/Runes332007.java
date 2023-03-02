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
 * 益狙 我方造成的狙系技能伤害增加[10]%。
 *
 * @author: suhq
 * @date: 2021/11/16 10:58 上午
 */
@Service
public class Runes332007 implements IRoundStageRunes {
    @Autowired
    private JuSeriesService juSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.YI_JU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double rate = 0.1 * combatBuff.getLevel();
        Action action = juSeriesService.addInjure(getRunesId(), param, rate);
        action.getClientActions().clear();
        action.setNeedAddAnimation(false);
        return action;
    }
}
