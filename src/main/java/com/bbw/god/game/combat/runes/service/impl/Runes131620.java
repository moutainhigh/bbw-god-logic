package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.runes.service.series.LeiSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 雷纹符131620：己方卡牌造成的电系技能伤害增加25%。
 *
 * @author fzj
 * @date 2021/9/26 11:28
 */
@Service
public class Runes131620 implements IRoundStageRunes {
    @Autowired
    private LeiSeriesService leiSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.LEI_WEN.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        return leiSeriesService.addInjure(getRunesId(), param, 0.25);
    }
}
