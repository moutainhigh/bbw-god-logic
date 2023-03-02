package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.runes.service.series.HuoSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 火纹符131640绿：己方卡牌造成的火系技能伤害增加25%。
 * 火系技能：火球、炙焰、业火、圣火、烈焰。
 *
 * @author fzj
 * @date 2021/9/26 11:28
 */
@Service
public class Runes131640 implements IRoundStageRunes {
    @Autowired
    private HuoSeriesService huoSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.HUO_WEN.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        return huoSeriesService.addInjure(getRunesId(), param, 0.25);
    }
}
