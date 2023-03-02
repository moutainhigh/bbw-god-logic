package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 益毒 我方造成的毒系技能伤害增加[10]%。
 *
 * @author: suhq
 * @date: 2021/11/16 10:58 上午
 */
@Service
public class Runes332004 implements IRoundStageRunes {
    @Autowired
    private DuSeriesService duSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.YI_DU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double rate = 0.1 * combatBuff.getLevel();
        Action action = duSeriesService.addInjure(getRunesId(), param, rate);
        action.getClientActions().clear();
        action.setNeedAddAnimation(false);
        return action;
    }
}
